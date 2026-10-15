package Servlet;

import Controlador.AmbientesDAO;
import Modelo.Ambientes;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Cambia el estado administrativo de un ambiente (Disponible,
 * Mantenimiento, Inhabilitado). Solo el Administrador.
 * "Ocupado" no se fija aqui: se calcula segun las clases programadas.
 */
@WebServlet("/ActualizarEstadoAmbiente")
public class ActualizarEstadoAmbiente extends HttpServlet {

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("Disponible", "Mantenimiento", "Inhabilitado");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        if (Autorizacion.idUsuarioDe(request) == null) {
            responder(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "No hay una sesión activa.");
            return;
        }
        if (!Autorizacion.esAdministrador(request)) {
            responder(response, HttpServletResponse.SC_FORBIDDEN,
                    "Solo el administrador puede cambiar el estado de un ambiente.");
            return;
        }

        Integer id = entero(request.getParameter("id"));
        String estado = texto(request.getParameter("estado"));

        if (id == null || estado == null || !ESTADOS_VALIDOS.contains(estado)) {
            responder(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Estado inválido. Use Disponible, Mantenimiento o Inhabilitado.");
            return;
        }

        boolean ok = new AmbientesDAO().actualizarEstadoAmbiente(id, estado);

        if (ok) {
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"ok\":true,\"estado\":\"" + estado + "\"}");
            }
        } else {
            responder(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo actualizar el estado del ambiente.");
        }
    }

    private Integer entero(String valor) {
        if (valor == null || valor.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String texto(String valor) {
        if (valor == null || valor.trim().isEmpty()) return null;
        return valor.trim();
    }

    private void responder(HttpServletResponse response, int status, String mensaje)
            throws IOException {
        response.setStatus(status);
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"error\":\"" + escapar(mensaje) + "\"}");
        }
    }

    private String escapar(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
