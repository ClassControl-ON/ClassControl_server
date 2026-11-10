package Servlet;

import Controlador.ReportesMetricasDAO;
import Servicio.ExcelServicio;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Descarga el detalle de programaciones en Excel (.xlsx) o CSV.
 *
 * <p>Reutiliza los mismos filtros de la pantalla (periodo, programa, estado
 * y busqueda) y las mismas reglas de permisos: un instructor solo puede
 * exportar su propia programacion.</p>
 */
@WebServlet("/ExportarReporte")
public class ExportarReporte extends HttpServlet {

    private static final String PERIODO_TRIMESTRE = "trimestre";
    private static final String PERIODO_ANIO = "anio";
    private static final String FORMATO_CSV = "csv";

    private static final List<String> CABECERAS = List.of(
            "Programa", "Ficha", "Instructor", "Ambiente", "Dia",
            "Hora inicio", "Hora fin", "Horas/semana", "Estado",
            "Vigencia desde", "Vigencia hasta");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!Autorizacion.puedeConsultar(request, response, "reportes")) {
            return;
        }
        if (Autorizacion.esAprendiz(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Los aprendices no tienen acceso a reportes.");
            return;
        }

        try {
            Integer idInstructor = Autorizacion.esInstructor(request)
                    ? Autorizacion.idUsuarioDe(request)
                    : null;

            String periodo = periodoValido(request.getParameter("periodo"));
            LocalDate[] rango = rangoDe(periodo);

            String programa = Autorizacion.textoSeguro(request.getParameter("programa"), 150);
            String estado = Autorizacion.textoSeguro(request.getParameter("estado"), 100);
            String texto = Autorizacion.textoSeguro(request.getParameter("q"), 100);

            List<Map<String, Object>> detalle = new ReportesMetricasDAO()
                    .listarDetalle(idInstructor, rango[0], rango[1], programa, estado, texto);

            String nombreArchivo = "reporte_classcontrol_" + LocalDate.now();
            String titulo = "Reporte de programaciones - periodo " + periodo;

            if (FORMATO_CSV.equalsIgnoreCase(request.getParameter("formato"))) {
                exportarCsv(response, nombreArchivo, detalle);
            } else {
                exportarExcel(response, nombreArchivo, titulo, detalle);
            }
        } catch (Exception e) {
            System.out.println("ExportarReporte - error: " + e.getMessage());
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo generar el reporte.");
            }
        }
    }

    private void exportarExcel(HttpServletResponse response, String nombreArchivo,
            String titulo, List<Map<String, Object>> detalle) throws IOException {

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + nombreArchivo + ".xlsx\"");

        List<List<Object>> filas = new ArrayList<>();
        for (Map<String, Object> fila : detalle) {
            filas.add(filaExportacion(fila));
        }

        try (OutputStream out = response.getOutputStream()) {
            new ExcelServicio().escribirReporte(out, titulo, CABECERAS, filas);
        }
    }

    private void exportarCsv(HttpServletResponse response, String nombreArchivo,
            List<Map<String, Object>> detalle) throws IOException {

        response.setContentType("text/csv;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + nombreArchivo + ".csv\"");

        try (PrintWriter out = response.getWriter()) {
            out.print('\uFEFF');
            out.print(filaCsv(CABECERAS));
            for (Map<String, Object> fila : detalle) {
                out.print(filaCsv(filaExportacion(fila)));
            }
        }
    }

    private List<Object> filaExportacion(Map<String, Object> fila) {
        List<Object> valores = new ArrayList<>();
        valores.add(fila.get("programa"));
        valores.add(fila.get("ficha"));
        valores.add(fila.get("instructor"));
        valores.add(fila.get("ambiente"));
        valores.add(fila.get("dia"));
        valores.add(fila.get("horaInicio"));
        valores.add(fila.get("horaFin"));
        valores.add(fila.get("horasSemana"));
        valores.add(fila.get("estado"));
        valores.add(fila.get("fechaInicio"));
        valores.add(fila.get("fechaFin"));
        return valores;
    }

    private String filaCsv(List<?> valores) {
        StringBuilder linea = new StringBuilder();
        for (int i = 0; i < valores.size(); i++) {
            if (i > 0) {
                linea.append(';');
            }
            Object valor = valores.get(i);
            linea.append('"')
                 .append(valor == null ? "" : String.valueOf(valor).replace("\"", "\"\""))
                 .append('"');
        }
        linea.append("\r\n");
        return linea.toString();
    }

    private String periodoValido(String valor) {
        if (PERIODO_TRIMESTRE.equalsIgnoreCase(valor)) {
            return PERIODO_TRIMESTRE;
        }
        if (PERIODO_ANIO.equalsIgnoreCase(valor)) {
            return PERIODO_ANIO;
        }
        return "mes";
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
