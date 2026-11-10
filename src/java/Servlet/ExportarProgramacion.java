package Servlet;

import Controlador.Programacion_InstructoresDAO;
import Controlador.UsuariosDAO;
import Modelo.ProgramacionInstructoresDTO;
import Modelo.Usuarios;
import Servicio.ExcelServicio;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Descarga la programación de instructores en Excel (.xlsx).
 *
 * <p>Reutiliza los filtros de la pantalla (instructor, ficha, trimestre y
 * búsqueda de texto) y las mismas reglas de permisos que
 * {@link ConsultarProgramaciones}: un instructor solo exporta su propia
 * programación y un aprendiz solo la de su ficha.</p>
 */
@WebServlet("/ExportarProgramacion")
public class ExportarProgramacion extends HttpServlet {

    private static final List<String> CABECERAS = List.of(
            "Actividad", "Competencia", "Instructor", "Ficha", "Programa",
            "Trimestre", "Ambiente", "Día", "Hora inicio", "Hora fin",
            "Horas/semana", "Estado", "Vigencia desde", "Vigencia hasta",
            "Observaciones");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!Autorizacion.puedeVerProgramacion(request, response)) {
            return;
        }

        try {
            Integer instructorId = Autorizacion.intParam(request, "instructorId");
            Integer fichaId = Autorizacion.intParam(request, "fichaId");
            Integer trimestreId = Autorizacion.intParam(request, "trimestreId");

            if (Autorizacion.esInstructor(request)) {
                instructorId = Autorizacion.idUsuarioDe(request);
            }

            if (Autorizacion.esAprendiz(request)) {
                Integer idPropio = Autorizacion.idUsuarioDe(request);
                if (idPropio != null) {
                    Usuarios aprendiz = new UsuariosDAO().consultaUsuarios(idPropio);
                    if (aprendiz != null && aprendiz.getFicha_id_ficha() != null) {
                        fichaId = aprendiz.getFicha_id_ficha();
                    }
                }
            }

            List<ProgramacionInstructoresDTO> programaciones =
                    new Programacion_InstructoresDAO()
                            .listarProgramaciones(instructorId, fichaId, trimestreId);

            String busqueda = normalizar(
                    Autorizacion.textoSeguro(request.getParameter("q"), 100));
            List<ProgramacionInstructoresDTO> filtradas = filtrar(programaciones, busqueda);

            exportarExcel(response, filtradas);
        } catch (Exception e) {
            System.out.println("ExportarProgramacion - error: " + e.getMessage());
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo generar la programación.");
            }
        }
    }

    /**
     * Aplica la misma búsqueda de texto libre que la tabla del cliente:
     * instructor, ficha, programa de la ficha, trimestre y ambiente.
     */
    private List<ProgramacionInstructoresDTO> filtrar(
            List<ProgramacionInstructoresDTO> datos, String busqueda) {

        if (busqueda == null || busqueda.isBlank()) {
            return datos;
        }

        List<ProgramacionInstructoresDTO> resultado = new ArrayList<>();
        for (ProgramacionInstructoresDTO p : datos) {
            if (coincide(p, busqueda)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    private boolean coincide(ProgramacionInstructoresDTO p, String busqueda) {
        return contiene(p.getInstructorNombre(), busqueda)
                || contiene(p.getFichaNumero(), busqueda)
                || contiene(p.getFichaPrograma(), busqueda)
                || contiene(p.getTrimestreNombre(), busqueda)
                || contiene(p.getAmbienteNombre(), busqueda);
    }

    private boolean contiene(String valor, String busqueda) {
        return valor != null && normalizar(valor).contains(busqueda);
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase(Locale.ROOT);
    }

    private void exportarExcel(HttpServletResponse response,
            List<ProgramacionInstructoresDTO> programaciones) throws IOException {

        String nombreArchivo = "programacion_classcontrol_" + LocalDate.now();

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + nombreArchivo + ".xlsx\"");

        Map<Integer, Integer> minutosPorInstructor = minutosPorInstructor(programaciones);
        List<List<Object>> filas = new ArrayList<>();

        for (ProgramacionInstructoresDTO p : programaciones) {
            filas.add(filaExportacion(p, minutosPorInstructor));
        }

        try (OutputStream out = response.getOutputStream()) {
            new ExcelServicio().escribirReporte(out,
                    "Programación de instructores - " + LocalDate.now(),
                    CABECERAS, filas);
        }
    }

    /** Suma la duración semanal (hora_fin - hora_inicio) por instructor. */
    private Map<Integer, Integer> minutosPorInstructor(
            List<ProgramacionInstructoresDTO> programaciones) {

        Map<Integer, Integer> totales = new HashMap<>();
        for (ProgramacionInstructoresDTO p : programaciones) {
            if (p.getHoraInicio() == null || p.getHoraFin() == null) {
                continue;
            }
            int minutos = p.getHoraFin().toSecondOfDay() / 60
                    - p.getHoraInicio().toSecondOfDay() / 60;
            if (minutos > 0) {
                totales.merge(p.getInstructorId(), minutos, Integer::sum);
            }
        }
        return totales;
    }

    private List<Object> filaExportacion(ProgramacionInstructoresDTO p,
            Map<Integer, Integer> minutosPorInstructor) {

        int minutos = minutosPorInstructor.getOrDefault(p.getInstructorId(), 0);

        List<Object> valores = new ArrayList<>();
        valores.add(p.getActividadNombre());
        valores.add(p.getCompetenciaNombre());
        valores.add(p.getInstructorNombre());
        valores.add(p.getFichaNumero());
        valores.add(p.getFichaPrograma());
        valores.add(p.getTrimestreNombre());
        valores.add(p.getAmbienteNombre());
        valores.add(diasLegibles(p.getDiasSemana()));
        valores.add(p.getHoraInicio() == null ? "" : p.getHoraInicio().toString());
        valores.add(p.getHoraFin() == null ? "" : p.getHoraFin().toString());
        valores.add(minutos / 60.0);
        valores.add(p.getEstadoNombre());
        valores.add(p.getFechaInicialProg() == null ? "" : p.getFechaInicialProg().toString());
        valores.add(p.getFechaFinProg() == null ? "" : p.getFechaFinProg().toString());
        valores.add(p.getObservaciones());
        return valores;
    }

    private String diasLegibles(String codigos) {
        if (codigos == null) {
            return "";
        }
        Map<String, String> etiquetas = Map.of(
                "LUN", "Lunes",
                "MAR", "Martes",
                "MIE", "Miércoles",
                "JUE", "Jueves",
                "VIE", "Viernes",
                "SAB", "Sábado");

        List<String> dias = new ArrayList<>();
        for (String codigo : codigos.split(",")) {
            String limpio = codigo.trim();
            if (!limpio.isEmpty()) {
                dias.add(etiquetas.getOrDefault(limpio, limpio));
            }
        }
        return String.join(", ", dias);
    }
}
