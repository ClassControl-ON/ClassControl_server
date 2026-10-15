package Servlet;

import Controlador.RolesDAO;
import Controlador.UsuariosDAO;
import Modelo.Roles;
import Modelo.Usuarios;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/api/MiPerfil")
public class MiPerfil extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        Integer idUsuario = idDeSesion(request);
        if (idUsuario == null) {
            responderNoAuth(response);
            return;
        }

        Usuarios u = new UsuariosDAO().consultaUsuarios(idUsuario);
        if (u == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"No se encontró el usuario de la sesión.\"}");
            }
            return;
        }

        Roles rol = new RolesDAO().consultaRoles(u.getRoles_id_roles());
        String rolNombre = (rol != null && rol.getDescripcion_Roles() != null)
                ? rol.getDescripcion_Roles() : "";

        try (PrintWriter out = response.getWriter()) {
            out.print("{");
            out.print("\"idUsuario\":" + u.getId_usuarios() + ",");
            out.print("\"nombres\":" + jsonStr(u.getNombres()) + ",");
            out.print("\"apellidos\":" + jsonStr(u.getApellidos()) + ",");
            out.print("\"username\":" + jsonStr(u.getUsername()) + ",");
            out.print("\"correo\":" + jsonStr(u.getCorreo()) + ",");
            out.print("\"identificacion\":" + jsonStr(u.getIdentificacion()) + ",");
            out.print("\"telefono\":" + jsonStr(u.getTelefono()) + ",");
            out.print("\"profesion\":" + jsonStr(u.getProfesion()) + ",");
            out.print("\"rolId\":" + u.getRoles_id_roles() + ",");
            out.print("\"rolNombre\":" + jsonStr(rolNombre));
            out.print("}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        Integer idUsuario = idDeSesion(request);
        if (idUsuario == null) {
            responderNoAuth(response);
            return;
        }

        UsuariosDAO dao = new UsuariosDAO();
        Usuarios u = dao.consultaUsuarios(idUsuario);
        if (u == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"No se encontró el usuario de la sesión.\"}");
            }
            return;
        }

        // ── Cambio de contraseña (Sección "Seguridad y Acceso") ──────────
        String claveActual = request.getParameter("claveActual");
        String claveNueva = request.getParameter("claveNueva");
        if (claveNueva != null && !claveNueva.trim().isEmpty()) {
            if (claveActual == null
                    || u.getClave() == null
                    || !BCrypt.checkpw(claveActual, u.getClave())) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "La contraseña actual no es correcta.");
                return;
            }
            if (!claveNueva.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "La contraseña debe incluir mínimo 8 caracteres, una mayúscula, un número y un símbolo.");
                return;
            }
            if (claveNueva.equals(claveActual)) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "La nueva contraseña debe ser diferente a la actual.");
                return;
            }
            // setClave también renueva fecha_ExpiracionContraseña (ver Modelo.Usuarios).
            u.setClave(BCrypt.hashpw(claveNueva.trim(), BCrypt.gensalt(12)));
            boolean actualizado = dao.actualizarUsuario(u);
            if (!actualizado) {
                responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No fue posible actualizar la contraseña.");
                return;
            }
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"ok\":true,\"mensaje\":\"Contraseña actualizada correctamente.\"}");
            }
            return;
        }

        // ── Actualización de datos personales ───────────────────────────
        // El usuario puede editar sus datos de contacto y de identidad de la
        // cuenta (nombres, apellidos, correo, teléfono, profesión). El rol,
        // la identificación, el username y el estado los gestiona
        // "Gestión de Usuarios" (ActualizarUsuario).
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String profesion = request.getParameter("profesion");

        if (nombres != null) {
            nombres = nombres.trim();
            if (nombres.length() < 2) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "El nombre debe tener al menos 2 caracteres.");
                return;
            }
            u.setNombres(nombres);
        }
        if (apellidos != null) {
            apellidos = apellidos.trim();
            if (apellidos.length() < 2) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Los apellidos deben tener al menos 2 caracteres.");
                return;
            }
            u.setApellidos(apellidos);
        }
        if (correo != null) {
            correo = correo.trim();
            if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "El correo no es válido.");
                return;
            }
            u.setCorreo(correo);
        }
        if (telefono != null) {
            u.setTelefono(telefono.trim());
        }
        if (profesion != null) {
            if (profesion.length() > 100) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "La profesión no puede superar los 100 caracteres.");
                return;
            }
            u.setProfesion(profesion.trim());
        }

        boolean actualizado = dao.actualizarUsuario(u);
        if (!actualizado) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible actualizar el perfil.");
            return;
        }

        // Refresca la sesión para que el nombre del sidebar/header sea el
        // mismo nombre que el usuario acaba de guardar.
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("nombres", u.getNombres());
            session.setAttribute("apellidos", u.getApellidos());
        }

        try (PrintWriter out = response.getWriter()) {
            out.print("{");
            out.print("\"ok\":true,");
            out.print("\"mensaje\":\"Perfil actualizado correctamente.\",");
            out.print("\"nombres\":" + jsonStr(u.getNombres()) + ",");
            out.print("\"apellidos\":" + jsonStr(u.getApellidos()) + ",");
            out.print("\"correo\":" + jsonStr(u.getCorreo()) + ",");
            out.print("\"telefono\":" + jsonStr(u.getTelefono()) + ",");
            out.print("\"profesion\":" + jsonStr(u.getProfesion()));
            out.print("}");
        }
    }

    private Integer idDeSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id_usuario") == null) return null;
        return (Integer) session.getAttribute("id_usuario");
    }

    private void responderNoAuth(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"error\":\"No hay una sesión activa.\"}");
        }
    }

    private void responderError(HttpServletResponse response, int status, String mensaje)
            throws IOException {
        response.setStatus(status);
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"error\":" + jsonStr(mensaje) + "}");
        }
    }

    private String jsonStr(String valor) {
        if (valor == null) return "null";
        return "\"" + valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n") + "\"";
    }
}
