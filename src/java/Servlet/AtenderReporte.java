package Servlet;

import Controlador.ReportesDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Marca un reporte como ATENDIDO. Exclusivo del Administrador.
 * Parámetros: id (req), respuesta (opcional).
 */
@WebServlet("/AtenderReporte")
public class AtenderReporte extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        Integer idSesion = Autorizacion.idUsuarioDe(request);
        if (idSesion == null) {
            responder(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "No hay una sesión activa.");
            return;
        }
        if (!Autorizacion.esAdministrador(request)) {
            responder(response, HttpServletResponse.SC_FORBIDDEN,
                    "Solo el administrador puede atender reportes.");
            return;
        }

        String idParam = request.getParameter("id");
        int idReporte;
        try {
            idReporte = Integer.parseInt(idParam.trim());
        } catch (Exception e) {
            responder(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Formato de datos invalido.");
            return;
        }

        boolean ok = new ReportesDAO().atender(
                idReporte, texto(request, "respuesta"), idSesion);

        if (ok) {
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"ok\":true}");
            }
        } else {
            responder(response, HttpServletResponse.SC_BAD_REQUEST,
                    "No se pudo atender el reporte.");
        }
    }

    private String texto(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim();
    }

    private String escapar(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void responder(HttpServletResponse response, int status, String mensaje)
            throws IOException {
        response.setStatus(status);
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"error\":\"" + escapar(mensaje) + "\"}");
        }
    }
}
