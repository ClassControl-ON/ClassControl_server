package Servlet;

import Controlador.ReportesMetricasDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Datos reales de la pantalla Reportes y Consultas.
 *
 * <p>Devuelve en JSON los indicadores del periodo, las series para los
 * graficos y el detalle de programaciones. El filtro por instructor se
 * fuerza en el servidor: un instructor nunca recibe datos de otro.</p>
 */
@WebServlet("/ConsultarReportesMetricas")
public class ConsultarReportesMetricas extends HttpServlet {

    private static final String PERIODO_MES = "mes";
    private static final String PERIODO_TRIMESTRE = "trimestre";
    private static final String PERIODO_ANIO = "anio";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!Autorizacion.puedeConsultar(request, response, "reportes")) {
            return;
        }

        response.setContentType("application/json;charset=UTF-8");

        if (Autorizacion.esAprendiz(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            escribir(response, "{\"error\":\"Los aprendices no tienen acceso a reportes.\"}");
            return;
        }

        try {
            Integer idInstructor = Autorizacion.esInstructor(request)
                    ? Autorizacion.idUsuarioDe(request)
                    : null;
            String periodo = periodoValido(request.getParameter("periodo"));
            LocalDate[] rango = rangoDe(periodo);

            ReportesMetricasDAO dao = new ReportesMetricasDAO();
            List<Map<String, Object>> detalle =
                    dao.listarDetalle(idInstructor, rango[0], rango[1], null, null, null);
            List<Map<String, Object>> tendencia =
                    dao.listarTendencia(idInstructor, LocalDate.now().minusMonths(5).withDayOfMonth(1));

            int totalAmbientes = dao.contarAmbientes();

            escribir(response, construirJson(periodo, rango, detalle, tendencia,
                    totalAmbientes, dao.contarInstructoresActivos(), dao.contarFichasActivas()));
        } catch (Exception e) {
            System.out.println("ConsultarReportesMetricas - error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            escribir(response, "{\"error\":\"No se pudieron calcular los reportes.\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        doGet(request, response);
    }

    private String construirJson(String periodo, LocalDate[] rango,
            List<Map<String, Object>> detalle, List<Map<String, Object>> tendencia,
            int totalAmbientes, int instructoresActivos, int fichasActivas) {

        Set<Integer> instructores = new HashSet<>();
        Set<Integer> ambientes = new HashSet<>();
        Set<String> fichas = new HashSet<>();
        Map<String, Integer> porPrograma = new LinkedHashMap<>();
        Map<String, Integer> porEstado = new LinkedHashMap<>();
        double horas = 0;
        int activas = 0;

        for (Map<String, Object> fila : detalle) {
            instructores.add((Integer) fila.get("instructorId"));
            ambientes.add((Integer) fila.get("ambienteId"));
            fichas.add(String.valueOf(fila.get("ficha")));
            horas += ((Number) fila.get("horasSemana")).doubleValue();
            if (Integer.valueOf(1).equals(fila.get("estadoId"))) {
                activas++;
            }
            porPrograma.merge(nombrePrograma(fila.get("programa")), 1, Integer::sum);
            porEstado.merge(texto(fila.get("estado")), 1, Integer::sum);
        }

        int total = detalle.size();
        double cumplimiento = total == 0 ? 0 : (activas * 100.0) / total;
        double ocupacion = totalAmbientes == 0 ? 0 : (ambientes.size() * 100.0) / totalAmbientes;

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"periodo\":").append(jsonStr(periodo)).append(",");
        json.append("\"desde\":").append(jsonStr(rango[0].toString())).append(",");
        json.append("\"hasta\":").append(jsonStr(rango[1].toString())).append(",");

        json.append("\"kpis\":{");
        json.append("\"programaciones\":").append(total).append(",");
        json.append("\"instructores\":").append(instructores.size()).append(",");
        json.append("\"horas\":").append(numero(horas)).append(",");
        json.append("\"ambientes\":").append(ambientes.size()).append(",");
        json.append("\"ambientesTotales\":").append(totalAmbientes).append(",");
        json.append("\"ocupacion\":").append(numero(ocupacion)).append(",");
        json.append("\"fichas\":").append(fichas.size()).append(",");
        json.append("\"cumplimiento\":").append(numero(cumplimiento)).append(",");
        json.append("\"instructoresActivos\":").append(instructoresActivos).append(",");
        json.append("\"fichasActivas\":").append(fichasActivas);
        json.append("},");

        json.append("\"porPrograma\":");
        agregarSerie(json, porPrograma);
        json.append(",");

        json.append("\"porEstado\":");
        agregarSerie(json, porEstado);
        json.append(",");

        json.append("\"tendencia\":[");
        for (int i = 0; i < tendencia.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            Map<String, Object> fila = tendencia.get(i);
            json.append("{")
                .append("\"mes\":").append(jsonStr(fila.get("mes"))).append(",")
                .append("\"total\":").append(fila.get("total"))
                .append("}");
        }
        json.append("],");

        json.append("\"detalle\":[");
        for (int i = 0; i < detalle.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append(detalleJson(detalle.get(i)));
        }
        json.append("]");
        json.append("}");
        return json.toString();
    }

    private void agregarSerie(StringBuilder json, Map<String, Integer> serie) {
        json.append("[");
        int i = 0;
        for (Map.Entry<String, Integer> entrada : serie.entrySet()) {
            if (i++ > 0) {
                json.append(",");
            }
            json.append("{")
                .append("\"nombre\":").append(jsonStr(entrada.getKey())).append(",")
                .append("\"total\":").append(entrada.getValue())
                .append("}");
        }
        json.append("]");
    }

    private String detalleJson(Map<String, Object> fila) {
        return "{"
                + "\"id\":" + fila.get("id") + ","
                + "\"programa\":" + jsonStr(nombrePrograma(fila.get("programa"))) + ","
                + "\"ficha\":" + jsonStr(fila.get("ficha")) + ","
                + "\"instructor\":" + jsonStr(fila.get("instructor")) + ","
                + "\"ambiente\":" + jsonStr(fila.get("ambiente")) + ","
                + "\"dia\":" + jsonStr(fila.get("dia")) + ","
                + "\"horaInicio\":" + jsonStr(fila.get("horaInicio")) + ","
                + "\"horaFin\":" + jsonStr(fila.get("horaFin")) + ","
                + "\"horasSemana\":" + fila.get("horasSemana") + ","
                + "\"estadoId\":" + fila.get("estadoId") + ","
                + "\"estado\":" + jsonStr(fila.get("estado")) + ","
                + "\"fechaInicio\":" + jsonStr(fila.get("fechaInicio")) + ","
                + "\"fechaFin\":" + jsonStr(fila.get("fechaFin"))
                + "}";
    }

    private String nombrePrograma(Object valor) {
        String nombre = texto(valor);
        return nombre.isBlank() ? "Sin programa" : nombre;
    }

    private String texto(Object valor) {
        return valor == null ? "" : String.valueOf(valor);
    }

    private String numero(double valor) {
        return String.format(Locale.ROOT, "%.1f", valor);
    }

    private String jsonStr(Object valor) {
        if (valor == null) {
            return "null";
        }
        String escapado = String.valueOf(valor)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");
        return "\"" + escapado + "\"";
    }

    private void escribir(HttpServletResponse response, String contenido) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(contenido);
        }
    }

    private String periodoValido(String valor) {
        if (PERIODO_TRIMESTRE.equalsIgnoreCase(valor)) {
            return PERIODO_TRIMESTRE;
        }
        if (PERIODO_ANIO.equalsIgnoreCase(valor)) {
            return PERIODO_ANIO;
        }
        return PERIODO_MES;
    }

    private LocalDate[] rangoDe(String periodo) {
        LocalDate hoy = LocalDate.now();
        LocalDate desde;
        LocalDate hasta;

        switch (periodo) {
            case PERIODO_ANIO -> {
                desde = LocalDate.of(hoy.getYear(), 1, 1);
                hasta = LocalDate.of(hoy.getYear(), 12, 31);
            }
            case PERIODO_TRIMESTRE -> {
                int mesInicio = ((hoy.getMonthValue() - 1) / 3) * 3 + 1;
                desde = LocalDate.of(hoy.getYear(), mesInicio, 1);
                hasta = desde.plusMonths(3).minusDays(1);
            }
            default -> {
                desde = hoy.withDayOfMonth(1);
                hasta = hoy.withDayOfMonth(hoy.lengthOfMonth());
            }
        }
        return new LocalDate[] {desde, hasta};
    }
}
