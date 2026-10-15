package Servlet;

import Controlador.RestablecerTokenDAO;
import Servicio.CorreoServicio;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Recuperación de contraseña por CORREO.
 *
 * El usuario ingresa su correo (o username) y su número de documento. Si
 * ambos coinciden con un usuario activo, se genera un token de un solo uso
 * con expiración, se guarda (hash) en la BD y se envía por correo un enlace
 * de restablecimiento.
 *
 * Para no revelar qué correos están registrados, la respuesta es idéntica
 * (éxito aparente) tanto si el usuario existe como si no.
 */
@WebServlet("/RecuperarPassword")
public class RecuperarPassword extends HttpServlet {

    private static final int MINUTOS_VALIDEZ = 30;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String correo = texto(request, "correo");
        String identificacion = texto(request, "identificacion");

        if (correo == null || identificacion == null) {
            responderJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    "{\"ok\":false,\"error\":\"Ingresa tu correo electrónico y tu número de documento.\"}");
            return;
        }

        if (!esCorreoValido(correo)) {
            responderJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    "{\"ok\":false,\"error\":\"Ingresa un correo electrónico válido (ej. usuario@sena.edu.co).\"}");
            return;
        }

        String correoUsuario = null;
        Integer idUsuario = null;

        try (Connection con = new Conexion.Conexion().getConexion()) {
            if (con == null) {
                responderJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "{\"ok\":false,\"error\":\"El servicio no está disponible en este momento.\"}");
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                    "SELECT id_usuarios, correo, activo FROM usuarios "
                    + "WHERE correo = ? "
                    + "AND identificacion = ? LIMIT 1");
            ps.setString(1, correo);
            ps.setString(2, identificacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idUsuario = rs.getInt("id_usuarios");
                correoUsuario = rs.getString("correo");
                boolean activo = rs.getBoolean("activo");
                if (!activo) {
                    idUsuario = null;
                }
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("RecuperarPassword - error de consulta: " + e.getMessage());
            e.printStackTrace();
            responderJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "{\"ok\":false,\"error\":\"No se pudo procesar la recuperación. Intenta de nuevo.\"}");
            return;
        }

        // Solo se envía el correo si la cuenta existe y está activa. A diferencia
        // del comportamiento anterior (éxito aparente), aquí se informa al usuario
        // cuando el correo no está registrado.
        if (idUsuario == null) {
            responderJson(response, HttpServletResponse.SC_NOT_FOUND,
                    "{\"ok\":false,\"error\":\"El correo no está registrado o los datos no coinciden con una cuenta activa.\"}");
            return;
        }

        if (!CorreoServicio.estaConfigurado()) {
            responderJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "{\"ok\":false,\"error\":\"El servicio de correo no está disponible. Intenta más tarde.\"}");
            return;
        }

        RestablecerTokenDAO tokenDAO = new RestablecerTokenDAO();
        tokenDAO.limpiarExpirados();
        String token = tokenDAO.generarToken();
        Timestamp expira = tokenDAO.guardar(idUsuario, token, MINUTOS_VALIDEZ);

        if (expira == null) {
            responderJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "{\"ok\":false,\"error\":\"No se pudo generar el enlace de recuperación. Intenta de nuevo.\"}");
            return;
        }

        String enlace = construirEnlace(request, token);
        String cuerpo = cuerpoCorreo(enlace, MINUTOS_VALIDEZ);
        String errorEnvio = new CorreoServicio()
                .enviar(correoUsuario, "ClassControl - Restablecer contraseña", cuerpo);
        if (errorEnvio != null) {
            System.out.println("RecuperarPassword - envío fallido: " + errorEnvio);
            responderJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "{\"ok\":false,\"error\":\"Ocurrió un problema al enviar el correo. Intenta de nuevo.\"}");
            return;
        }

        responderJson(response, HttpServletResponse.SC_OK,
                "{\"ok\":true,\"mensaje\":\"Hemos enviado un enlace de recuperación a tu correo. "
                + "Revisa tu bandeja de entrada y también la de correo no deseado.\"}");
    }

    private boolean esCorreoValido(String correo) {
        return correo != null
                && correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");
    }

    /**
     * Construye la URL de restablecimiento. Usa la variable de entorno APP_URL
     * si está configurada (recomendado en Railway); si no, deriva la base de la
     * petición entrante.
     */
    private String construirEnlace(HttpServletRequest request, String token) {
        String appUrl = System.getenv("APP_URL");
        String base;
        if (appUrl != null && !appUrl.trim().isEmpty()) {
            base = appUrl.trim().replaceAll("/+$", "");
        } else {
            String scheme = request.getScheme();
            String server = request.getServerName();
            int port = request.getServerPort();
            base = scheme + "://" + server + (port == 80 || port == 443 ? "" : ":" + port);
        }
        return base + "/RestablecerPassword?token=" + token;
    }

    private String cuerpoCorreo(String enlace, int minutos) {
        return "<div style=\"font-family:Segoe UI,Arial,sans-serif;max-width:560px;margin:auto;"
                + "border:1px solid #e3e3e3;border-radius:12px;overflow:hidden\">"
                + "<div style=\"background:#0B6E2E;color:#fff;padding:18px 24px;font-size:20px;font-weight:700\">"
                + "ClassControl - SENA</div>"
                + "<div style=\"padding:24px\">"
                + "<h2 style=\"color:#1B5E20;font-size:20px;margin-top:0\">Restablecer tu contraseña</h2>"
                + "<p>Recibimos una solicitud para restablecer la contraseña de tu cuenta.</p>"
                + "<p>Haz clic en el siguiente botón para elegir una nueva contraseña. "
                + "El enlace es válido por <b>" + minutos + " minutos</b> y solo puede usarse una vez.</p>"
                + "<p style=\"text-align:center;margin:28px 0\">"
                + "<a href=\"" + enlace + "\" style=\"background:#1B5E20;color:#fff;text-decoration:none;"
                + "padding:12px 28px;border-radius:8px;font-weight:600\">Restablecer contraseña</a></p>"
                + "<p>Si no solicitaste este cambio, ignora este correo y tu contraseña seguirá igual.</p>"
                + "<p style=\"color:#777;font-size:12px;margin-top:24px\">"
                + "Si el botón no funciona, copia y pega este enlace en tu navegador:<br>"
                + "<span style=\"color:#0B6E2E;word-break:break-all\">" + enlace + "</span></p>"
                + "</div></div>";
    }

    private void responderJson(HttpServletResponse response, int status, String json)
            throws IOException {
        response.setStatus(status);
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
        }
    }

    private String texto(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
