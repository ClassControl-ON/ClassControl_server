package Servlet;

import Controlador.ReportesDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Consulta de reportes de incidencias.
 *  - Instructor: solo ve los suyos.
 *  - Coordinador y Administrador: ven todos.
 *  - Aprendiz: no participa en reportes (403).
 */
@WebServlet("/ConsultarReportes")
public class ConsultarReportes extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        Integer idSesion = Autorizacion.idUsuarioDe(request);
        if (idSesion == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"No hay una sesión activa.\"}");
            }
            return;
        }

        if (Autorizacion.esAprendiz(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"Los aprendices no usan reportes de incidencias.\"}");
            }
            return;
        }

        // Instructor ve solo los suyos; coordinador/admin, todos.
        Integer filtro = Autorizacion.esInstructor(request) ? idSesion : null;
        List<Map<String, Object>> reportes = new ReportesDAO().listar(filtro);

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < reportes.size(); i++) {
            Map<String, Object> r = reportes.get(i);
            if (i > 0) json.append(",");
            json.append("{")
                .append("\"id\":").append(r.get("id")).append(",")
                .append("\"titulo\":").append(jsonStr(r.get("titulo"))).append(",")
                .append("\"descripcion\":").append(jsonStr(r.get("descripcion"))).append(",")
                .append("\"tipo\":").append(jsonStr(r.get("tipo"))).append(",")
                .append("\"estado\":").append(jsonStr(r.get("estado"))).append(",")
                .append("\"fechaCreacion\":").append(jsonStr(r.get("fechaCreacion"))).append(",")
                .append("\"fechaAtencion\":").append(jsonStr(r.get("fechaAtencion"))).append(",")
                .append("\"respuestaAdmin\":").append(jsonStr(r.get("respuestaAdmin"))).append(",")
                .append("\"reportaId\":").append(r.get("reportaId") == null ? "null" : r.get("reportaId")).append(",")
                .append("\"reporta\":").append(jsonStr(r.get("reporta"))).append(",")
                .append("\"ambienteId\":").append(r.get("ambienteId") == null ? "null" : r.get("ambienteId")).append(",")
                .append("\"ambiente\":").append(jsonStr(r.get("ambiente"))).append(",")
                .append("\"atendidoPorId\":").append(r.get("atendidoPorId") == null ? "null" : r.get("atendidoPorId")).append(",")
                .append("\"atendidoPor\":").append(jsonStr(r.get("atendidoPor")))
                .append("}");
        }
        json.append("]");

        try (PrintWriter out = response.getWriter()) {
            out.print(json.toString());
        }
    }

    private String jsonStr(Object valor) {
        if (valor == null) return "null";
        String s = String.valueOf(valor)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");
        return "\"" + s + "\"";
    }
}
