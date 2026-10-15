package Servlet;

import Controlador.RestablecerTokenDAO;
import Controlador.UsuariosDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Restablecimiento de contraseña a partir de un token recibido por correo.
 *
 * GET  /RestablecerPassword?token=... → valida el token y muestra el form.
 * POST /RestablecerPassword             → guarda la nueva contraseña.
 */
@WebServlet("/RestablecerPassword")
public class RestablecerPassword extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String token = request.getParameter("token");
        if (token == null || token.trim().isEmpty()) {
            enviarInvalido(request, response, "El enlace de restablecimiento es inválido o incompleto.");
            return;
        }
        Integer idUsuario = new RestablecerTokenDAO().validar(token.trim());
        if (idUsuario == null) {
            enviarInvalido(request, response,
                    "El enlace es inválido o ya expiró. Solicita uno nuevo.");
            return;
        }
        request.setAttribute("token", token.trim());
        request.getRequestDispatcher("Restablecer_Contraseña.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String token = request.getParameter("token");
        String claveNueva = request.getParameter("clave");
        String confirmar = request.getParameter("confirmar");

        if (token == null || token.trim().isEmpty()) {
            enviarInvalido(request, response, "El enlace de restablecimiento es inválido o incompleto.");
            return;
        }
        if (claveNueva == null || confirmar == null
                || claveNueva.trim().length() < 8) {
            request.setAttribute("mensaje", "La contraseña debe tener al menos 8 caracteres.");
            request.setAttribute("token", token.trim());
            request.getRequestDispatcher("Restablecer_Contraseña.jsp").forward(request, response);
            return;
        }
        if (!claveNueva.trim().equals(confirmar.trim())) {
            request.setAttribute("mensaje", "Las contraseñas no coinciden.");
            request.setAttribute("token", token.trim());
            request.getRequestDispatcher("Restablecer_Contraseña.jsp").forward(request, response);
            return;
        }

        Integer idUsuario = new RestablecerTokenDAO().validarYUso(token.trim());
        if (idUsuario == null) {
            enviarInvalido(request, response,
                    "El enlace es inválido o ya expiró. Solicita uno nuevo.");
            return;
        }

        boolean ok = new UsuariosDAO().actualizarClave(
                idUsuario, BCrypt.hashpw(claveNueva.trim(), BCrypt.gensalt(10)));

        if (ok) {
            request.setAttribute("exito", true);
            request.getRequestDispatcher("Restablecer_Contraseña.jsp").forward(request, response);
        } else {
            request.setAttribute("mensaje", "No se pudo actualizar la contraseña. Intenta de nuevo.");
            request.setAttribute("token", token.trim());
            request.getRequestDispatcher("Restablecer_Contraseña.jsp").forward(request, response);
        }
    }

    private void enviarInvalido(HttpServletRequest request, HttpServletResponse response,
            String mensaje) throws ServletException, IOException {
        request.setAttribute("invalido", mensaje);
        request.getRequestDispatcher("Restablecer_Contraseña.jsp").forward(request, response);
    }
}
