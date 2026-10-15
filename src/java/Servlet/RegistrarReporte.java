package Servlet;

import Controlador.ReportesDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Crea un reporte de incidencia.
 * Permitido para Instructor, Coordinador y Administrador (no aprendices).
 * Parámetros: titulo (req), descripcion, tipo, ambienteId (opcionales).
 */
@WebServlet("/RegistrarReporte")
public class RegistrarReporte extends HttpServlet {

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
        if (!Autorizacion.esInstructor(request)) {
            responder(response, HttpServletResponse.SC_FORBIDDEN,
                    "Solo los instructores pueden enviar reportes de incidencias.");
            return;
        }

        String titulo = texto(request, "titulo");
        if (titulo == null) {
            responder(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Falta el título del reporte.");
            return;
        }

        Integer ambienteId = entero(request, "ambienteId");

        boolean ok = new ReportesDAO().insertar(
                titulo,
                texto(request, "descripcion"),
                texto(request, "tipo"),
                idSesion,
                ambienteId);

        if (ok) {
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"ok\":true}");
            }
        } else {
            responder(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo guardar el reporte. Intenta de nuevo.");
        }
    }

    private Integer entero(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String texto(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
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
