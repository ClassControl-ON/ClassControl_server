package Servlet;

import Controlador.DashboardDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Expone en JSON los datos que antes se calculaban con scriptlets Java
 * directamente dentro de Pagina_Principal.jsp: contadores del dashboard,
 * próximas actividades y % de fichas activas por programa.
 *
 * Sigue el mismo contrato que ConsultarBaseServlet (requiere sesión,
 * responde JSON armado a mano) pero no extiende de esa clase porque
 * el resultado no es una lista de un solo tipo, sino un objeto con
 * varias secciones.
 */
@WebServlet("/ConsultarDashboard")
public class ConsultarDashboard extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!Autorizacion.puedeConsultar(request, response, "dashboard")) {
            return;
        }

        response.setContentType("application/json;charset=UTF-8");

        try {
            DashboardDAO dao = new DashboardDAO();

            int totalFichasActivas      = dao.contarFichasActivas();
            int totalAmbientesHoy       = dao.contarAmbientesOcupadosHoy();
            int totalActividadesEnCurso = dao.contarActividadesEnCurso();
            int totalInstructores       = dao.contarInstructoresActivos();

            List<Map<String, String>> actividades      = dao.listarProximasActividades();
            List<Map<String, Object>> estadoProgramas  = dao.listarEstadoProgramas();

            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"totalFichasActivas\":").append(totalFichasActivas).append(",");
            json.append("\"totalAmbientesHoy\":").append(totalAmbientesHoy).append(",");
            json.append("\"totalActividadesEnCurso\":").append(totalActividadesEnCurso).append(",");
            json.append("\"totalInstructores\":").append(totalInstructores).append(",");

            json.append("\"proximasActividades\":[");
            for (int i = 0; i < actividades.size(); i++) {
                if (i > 0) json.append(",");
                Map<String, String> act = actividades.get(i);
                json.append("{");
                json.append("\"codigoFicha\":").append(jsonStr(act.get("codigoFicha"))).append(",");
                json.append("\"programa\":").append(jsonStr(act.get("programa"))).append(",");
                json.append("\"jornada\":").append(jsonStr(act.get("jornada"))).append(",");
                json.append("\"actividad\":").append(jsonStr(act.get("actividad"))).append(",");
                json.append("\"ambiente\":").append(jsonStr(act.get("ambiente"))).append(",");
                json.append("\"horario\":").append(jsonStr(act.get("horario"))).append(",");
                json.append("\"instructor\":").append(jsonStr(act.get("instructor")));
                json.append("}");
            }
            json.append("],");

            json.append("\"estadoProgramas\":[");
            for (int i = 0; i < estadoProgramas.size(); i++) {
                if (i > 0) json.append(",");
                Map<String, Object> prog = estadoProgramas.get(i);
                json.append("{");
                json.append("\"nombre\":").append(jsonStr(String.valueOf(prog.get("nombre")))).append(",");
                json.append("\"pct\":").append(prog.get("pct"));
                json.append("}");
            }
            json.append("]");

            json.append("}");

            try (PrintWriter out = response.getWriter()) {
                out.print(json.toString());
            }
        } catch (Exception e) {
            System.out.println("ConsultarDashboard - error al calcular el dashboard: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"No se pudo obtener el dashboard\"}");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String jsonStr(String valor) {
        if (valor == null) return "null";
        String escapado = valor
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
        return "\"" + escapado + "\"";
    }
}
