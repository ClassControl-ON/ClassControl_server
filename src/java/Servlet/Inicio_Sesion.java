package Servlet;

import Modelo.Usuarios;
import Modelo.Roles;
import Controlador.UsuariosDAO;
import Controlador.RolesDAO;
import java.io.IOException;
import java.io.PrintWriter;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/Iniciar")
public class Inicio_Sesion extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String clave = request.getParameter("clave");
        boolean api = aceptaJson(request);

        UsuariosDAO midao = new UsuariosDAO();
        Usuarios usuariosBD = midao.consultaUsuarios(username);

        if (usuariosBD == null) {
            responderError(request, response, api, "El usuario no existe.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!coincideClave(clave, usuariosBD.getClave())) {
            responderError(request, response, api, "Contraseña incorrecta.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!usuariosBD.isActivo()) {
            responderError(request, response, api,
                    "Usuario inactivo. Contacte al administrador.",
                    HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Roles rolBD = new RolesDAO().consultaRoles(usuariosBD.getRoles_id_roles());
        if (rolBD == null || rolBD.getDescripcion_Roles() == null) {
            responderError(request, response, api,
                    "No fue posible validar los permisos del usuario.",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        HttpSession session = request.getSession(true);
        request.changeSessionId();
        session.setAttribute("id_usuario", usuariosBD.getId_usuarios());
        session.setAttribute("nombres", usuariosBD.getNombres());
        session.setAttribute("apellidos", usuariosBD.getApellidos());
        session.setAttribute("username", usuariosBD.getUsername());
        session.setAttribute("rol", usuariosBD.getRoles_id_roles());
        session.setAttribute("rol_nombre", rolBD.getDescripcion_Roles());

        if (api) {
            // Flutter Web corre en localhost y Railway en otro dominio. Sin
            // SameSite=None el navegador no devuelve JSESSIONID en las
            // consultas posteriores (dashboard, fichas, etc.).
            response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId()
                    + "; Path=/; HttpOnly; Secure; SameSite=None");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            try (PrintWriter out = response.getWriter()) {
                out.print("{");
                out.print("\"idUsuario\":" + usuariosBD.getId_usuarios() + ",");
                out.print("\"nombres\":" + jsonStr(usuariosBD.getNombres()) + ",");
                out.print("\"apellidos\":" + jsonStr(usuariosBD.getApellidos()) + ",");
                out.print("\"username\":" + jsonStr(usuariosBD.getUsername()) + ",");
                out.print("\"rolId\":" + usuariosBD.getRoles_id_roles() + ",");
                out.print("\"rolNombre\":" + jsonStr(rolBD.getDescripcion_Roles()));
                out.print("}");
            }
        } else {
            response.sendRedirect("Pagina_Principal.jsp");
        }
    }

    private boolean aceptaJson(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.toLowerCase(java.util.Locale.ROOT).contains("application/json");
    }

    private void responderError(HttpServletRequest request, HttpServletResponse response,
            boolean api, String mensaje, int status) throws IOException, ServletException {
        if (!api) {
            request.setAttribute("mensaje", "❌ " + mensaje);
            request.getRequestDispatcher("Inicio_de_sesion.jsp").forward(request, response);
            return;
        }
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"error\":" + jsonStr(mensaje) + "}");
        }
    }

    private String jsonStr(String valor) {
        if (valor == null) return "null";
        return "\"" + valor.replace("\\\\", "\\\\\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\\\n") + "\"";
    }

    private boolean coincideClave(String claveIngresada, String hashAlmacenado) {
        if (claveIngresada == null || hashAlmacenado == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(claveIngresada, hashAlmacenado);
        } catch (IllegalArgumentException e) {
            System.out.println("Inicio_Sesion - hash BCrypt invalido.");
            return false;
        }
    }
}
