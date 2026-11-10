package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Consultas agregadas para la pantalla Reportes y Consultas.
 *
 * <p>Todas las cifras salen de la base de datos real (no hay datos de
 * ejemplo). El filtro por instructor se aplica siempre en el servlet: un
 * instructor solo puede ver su propia programacion.</p>
 */
public class ReportesMetricasDAO {

    private static final String FILTRO_INSTRUCTOR =
            " AND pi.Usuarios_id_usuarios = ?";

    private static final String FILTRO_PROGRAMA =
            " AND p.nombre_programa = ?";

    private static final String FILTRO_ESTADO =
            " AND e.descripcion_Estado = ?";

    private static final String FILTRO_TEXTO =
            " AND (LOWER(CONCAT(u.nombres, ' ', u.apellidos)) LIKE ?"
            + " OR LOWER(f.codigo_ficha) LIKE ?"
            + " OR LOWER(COALESCE(p.nombre_programa, '')) LIKE ?"
            + " OR LOWER(a.descripcion_Ambiente) LIKE ?)";

    private static final String SQL_DETALLE =
            "SELECT pi.id_programacion_Instructores AS id, "
            + "       COALESCE(p.nombre_programa, '') AS programa, "
            + "       f.codigo_ficha AS ficha, "
            + "       pi.Usuarios_id_usuarios AS instructorId, "
            + "       CONCAT(u.nombres, ' ', u.apellidos) AS instructor, "
            + "       pi.Ambientes_id_ambientes AS ambienteId, "
            + "       a.descripcion_Ambiente AS ambiente, "
            + "       pi.dias_Semana AS dia, "
            + "       pi.hora_inicio AS horaInicio, "
            + "       pi.hora_fin AS horaFin, "
            + "       pi.Estado_id_estado AS estadoId, "
            + "       e.descripcion_Estado AS estado, "
            + "       pi.fecha_inicial_Prog AS fechaInicio, "
            + "       pi.fecha_fin_Prog AS fechaFin "
            + "FROM programacion_instructores pi "
            + "INNER JOIN ficha f ON f.id_ficha = pi.Ficha_id_ficha "
            + "LEFT JOIN programas p ON p.idProgramas = f.Programas_idProgramas "
            + "INNER JOIN usuarios u ON u.id_usuarios = pi.Usuarios_id_usuarios "
            + "INNER JOIN ambientes a ON a.id_ambientes = pi.Ambientes_id_ambientes "
            + "INNER JOIN estado e ON e.id_estado = pi.Estado_id_estado "
            + "WHERE pi.fecha_inicial_Prog <= ? "
            + "  AND pi.fecha_fin_Prog >= ?";

    private static final String ORDEN_DETALLE =
            " ORDER BY p.nombre_programa, f.codigo_ficha, pi.hora_inicio";

    /**
     * Programaciones que se cruzan con el rango indicado.
     *
     * @param idInstructor si no es null, limita el resultado a ese instructor
     * @param programa nombre exacto del programa (opcional)
     * @param estado nombre exacto del estado (opcional)
     * @param texto busqueda libre en instructor, ficha, programa o ambiente
     */
    public List<Map<String, Object>> listarDetalle(Integer idInstructor,
            LocalDate desde, LocalDate hasta, String programa, String estado,
            String texto) {

        List<Map<String, Object>> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SQL_DETALLE);
        List<Object> valores = new ArrayList<>();
        // Solapamiento del periodo: la programacion empieza antes de que
        // termine el rango y termina despues de que empiece.
        valores.add(hasta);
        valores.add(desde);

        if (idInstructor != null) {
            sql.append(FILTRO_INSTRUCTOR);
            valores.add(idInstructor);
        }
        if (programa != null && !programa.isBlank()) {
            sql.append(FILTRO_PROGRAMA);
            valores.add(programa.trim());
        }
        if (estado != null && !estado.isBlank()) {
            sql.append(FILTRO_ESTADO);
            valores.add(estado.trim());
        }
        if (texto != null && !texto.isBlank()) {
            sql.append(FILTRO_TEXTO);
            String patron = "%" + texto.trim().toLowerCase(Locale.ROOT) + "%";
            valores.add(patron);
            valores.add(patron);
            valores.add(patron);
            valores.add(patron);
        }
        sql.append(ORDEN_DETALLE);

        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < valores.size(); i++) {
                Object valor = valores.get(i);
                if (valor instanceof LocalDate fecha) {
                    // setObject(LocalDate) aplica la zona horaria de la JVM y
                    // puede desplazar la fecha un dia; setDate usa el valor
                    // calendario tal cual.
                    ps.setDate(i + 1, java.sql.Date.valueOf(fecha));
                } else {
                    ps.setObject(i + 1, valor);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDetalle(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("ReportesMetricasDAO - error al listar detalle: " + e.getMessage());
        }
        return lista;
    }

    /** Cantidad de programaciones por mes (ultimos meses). */
    public List<Map<String, Object>> listarTendencia(Integer idInstructor, LocalDate desde) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(pi.fecha_inicial_Prog, '%Y-%m') AS mes, "
                + "       COUNT(*) AS total "
                + "FROM programacion_instructores pi "
                + "WHERE pi.fecha_inicial_Prog >= ? "
                + (idInstructor == null ? "" : " AND pi.Usuarios_id_usuarios = ? ")
                + "GROUP BY mes "
                + "ORDER BY mes";

        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(desde));
            if (idInstructor != null) {
                ps.setInt(2, idInstructor);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("mes", rs.getString("mes"));
                    fila.put("total", rs.getInt("total"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.out.println("ReportesMetricasDAO - error en tendencia: " + e.getMessage());
        }
        return lista;
    }

    public int contarAmbientes() {
        return contarSimple("SELECT COUNT(*) FROM ambientes");
    }

    public int contarInstructoresActivos() {
        return contarSimple("SELECT COUNT(*) FROM usuarios WHERE Roles_id_roles = 1 AND activo = 1");
    }

    public int contarFichasActivas() {
        return contarSimple("SELECT COUNT(*) FROM ficha WHERE Estado_id_estado = 1");
    }

    private int contarSimple(String sql) {
        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.out.println("ReportesMetricasDAO - error en conteo: " + e.getMessage());
            return 0;
        }
    }

    private Map<String, Object> mapearDetalle(ResultSet rs) throws SQLException {
        Map<String, Object> fila = new LinkedHashMap<>();
        fila.put("id", rs.getInt("id"));
        fila.put("programa", rs.getString("programa"));
        fila.put("ficha", rs.getString("ficha"));
        fila.put("instructorId", rs.getInt("instructorId"));
        fila.put("instructor", rs.getString("instructor"));
        fila.put("ambienteId", rs.getInt("ambienteId"));
        fila.put("ambiente", rs.getString("ambiente"));
        fila.put("dia", rs.getString("dia"));
        fila.put("horaInicio", textoHora(rs.getObject("horaInicio", LocalTime.class)));
        fila.put("horaFin", textoHora(rs.getObject("horaFin", LocalTime.class)));
        fila.put("horasSemana", calcularHoras(rs.getObject("horaInicio", LocalTime.class),
                rs.getObject("horaFin", LocalTime.class)));
        fila.put("estadoId", rs.getInt("estadoId"));
        fila.put("estado", rs.getString("estado"));

        LocalDate inicio = rs.getObject("fechaInicio", LocalDate.class);
        LocalDate fin = rs.getObject("fechaFin", LocalDate.class);
        fila.put("fechaInicio", inicio == null ? null : inicio.toString());
        fila.put("fechaFin", fin == null ? null : fin.toString());
        return fila;
    }

    private String textoHora(LocalTime hora) {
        return hora == null ? "" : hora.toString();
    }

    private double calcularHoras(LocalTime inicio, LocalTime fin) {
        if (inicio == null || fin == null) {
            return 0;
        }
        long minutos = ChronoUnit.MINUTES.between(inicio, fin);
        if (minutos <= 0) {
            return 0;
        }
        return Math.round((minutos / 60.0) * 10) / 10.0;
    }
}
