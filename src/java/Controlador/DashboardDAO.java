package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Consultas agregadas para el dashboard de Pagina_Principal.jsp
 * (conteos, próximas actividades y % de fichas activas por programa).
 *
 * Antes esta lógica vivía directamente en un scriptlet dentro del JSP;
 * se movió aquí para que la vista deje de acceder a la base de datos
 * directamente y el servlet ConsultarDashboard pueda exponerla como
 * cualquier otro Consultar* (JSON vía fetch).
 */
public class DashboardDAO {

    public int contarFichasActivas() {
        String sql = "SELECT COUNT(*) FROM ficha WHERE Estado_id_estado = 1";
        return contarSimple(sql);
    }

    public int contarAmbientesOcupadosHoy() {
        String sql = "SELECT COUNT(DISTINCT Ambientes_id_ambientes) " +
                     "FROM programacion_instructores " +
                     "WHERE fecha_inicial_Prog <= CURDATE() " +
                     "  AND fecha_fin_Prog     >= CURDATE() " +
                     "  AND Estado_id_estado = 1";
        return contarSimple(sql);
    }

    public int contarActividadesEnCurso() {
        String sql = "SELECT COUNT(*) " +
                     "FROM programacion_instructores " +
                     "WHERE fecha_inicial_Prog <= CURDATE() " +
                     "  AND fecha_fin_Prog     >= CURDATE() " +
                     "  AND Estado_id_estado = 1";
        return contarSimple(sql);
    }

    public int contarInstructoresActivos() {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE Roles_id_roles = 1 AND activo = 1";
        return contarSimple(sql);
    }

    private int contarSimple(String sql) {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) return 0;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.out.println("❌ Error en conteo del dashboard: " + e.getMessage());
            return 0;
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }
    }

    /** Próximas actividades (JOIN completo), máximo 10, ordenadas por hora de inicio. */
    public List<Map<String, String>> listarProximasActividades() {
        List<Map<String, String>> actividades = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) return actividades;

        /*
         * Programacion_Instructores no tiene relación directa con
         * Actividades. La cadena real, según el modelo, es:
         *   Programacion_Instructores → Ficha → Programas
         *     → Competencias → Resultado_aprendizaje → Actividades
         * Es decir, se muestran las actividades del currículo del
         * programa al que pertenece la ficha de cada programación
         * activa (no una actividad asignada a la programación en sí).
         */
        String sql =
            "SELECT DISTINCT " +
            "  f.codigo_ficha, " +
            "  p.nombre_programa, " +
            "  j.descripcion_Jornada, " +
            "  a.nombre_Act, " +
            "  amb.descripcion_Ambiente, " +
            "  pi.hora_inicio, " +
            "  pi.hora_fin, " +
            "  CONCAT(u.nombres, ' ', u.apellidos) AS instructor " +
            "FROM programacion_instructores pi " +
            "JOIN ficha      f   ON pi.Ficha_id_ficha            = f.id_ficha " +
            "JOIN programas  p   ON f.Programas_idProgramas      = p.idProgramas " +
            "JOIN jornada    j   ON f.Jornada_id_jornada         = j.id_jornada " +
            "JOIN competencias          comp ON comp.Programas_idProgramas             = p.idProgramas " +
            "JOIN resultado_aprendizaje ra   ON ra.Competencias_id_competencias         = comp.id_competencias " +
            "JOIN actividades a  ON a.Resultado_aprendizaje_id_resultado_aprendizaje = ra.id_resultado_aprendizaje " +
            "JOIN ambientes  amb ON pi.Ambientes_id_ambientes    = amb.id_ambientes " +
            "JOIN usuarios   u   ON pi.Usuarios_id_usuarios      = u.id_usuarios " +
            "WHERE pi.fecha_fin_Prog >= CURDATE() " +
            "  AND pi.Estado_id_estado = 1 " +
            "ORDER BY pi.hora_inicio ASC " +
            "LIMIT 10";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            while (rs.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                row.put("codigoFicha", String.valueOf(rs.getInt("codigo_ficha")));
                row.put("programa",    rs.getString("nombre_programa"));
                row.put("jornada",     rs.getString("descripcion_Jornada"));
                row.put("actividad",   rs.getString("nombre_Act"));
                row.put("ambiente",    rs.getString("descripcion_Ambiente"));
                LocalTime hi = rs.getObject("hora_inicio", LocalTime.class);
                LocalTime hf = rs.getObject("hora_fin",    LocalTime.class);
                row.put("horario", (hi != null ? hi.format(fmt) : "--") +
                                    " – " +
                                    (hf != null ? hf.format(fmt) : "--"));
                row.put("instructor", rs.getString("instructor"));
                actividades.add(row);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar próximas actividades: " + e.getMessage());
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return actividades;
    }

    /** % de fichas activas por programa (top 5). */
    public List<Map<String, Object>> listarEstadoProgramas() {
        List<Map<String, Object>> estadoProgramas = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) return estadoProgramas;

        String sql =
            "SELECT p.nombre_programa, " +
            "       COUNT(f.id_ficha)                                        AS total, " +
            "       SUM(CASE WHEN f.Estado_id_estado = 1 THEN 1 ELSE 0 END) AS activas " +
            "FROM programas p " +
            "LEFT JOIN ficha f ON f.Programas_idProgramas = p.idProgramas " +
            "GROUP BY p.idProgramas, p.nombre_programa " +
            "ORDER BY activas DESC " +
            "LIMIT 5";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int total   = rs.getInt("total");
                int activas = rs.getInt("activas");
                int pct     = (total > 0) ? (int) Math.round((activas * 100.0) / total) : 0;
                Map<String, Object> prog = new LinkedHashMap<>();
                prog.put("nombre", rs.getString("nombre_programa"));
                prog.put("pct",    pct);
                estadoProgramas.add(prog);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar estado de programas: " + e.getMessage());
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return estadoProgramas;
    }
}
