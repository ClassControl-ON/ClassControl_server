package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO de reportes de incidencias (instructor -> administrador).
 * Devuelve mapas listos para serializar a JSON en los servlets.
 */
public class ReportesDAO {

    private static final String SELECT_BASE =
            "SELECT r.id_reportes, r.titulo, r.descripcion, r.tipo, r.estado, "
            + "r.respuesta_admin, r.fecha_creacion, r.fecha_atencion, "
            + "r.Usuarios_id_usuarios AS reportaId, "
            + "CONCAT(u.nombres, ' ', u.apellidos) AS reportaNombre, "
            + "r.Ambientes_id_ambientes AS ambienteId, "
            + "a.descripcion_Ambiente AS ambienteNombre, "
            + "r.atendido_por AS atendidoPorId, "
            + "CONCAT(ua.nombres, ' ', ua.apellidos) AS atendidoPorNombre "
            + "FROM reportes r "
            + "INNER JOIN usuarios u ON u.id_usuarios = r.Usuarios_id_usuarios "
            + "LEFT JOIN ambientes a ON a.id_ambientes = r.Ambientes_id_ambientes "
            + "LEFT JOIN usuarios ua ON ua.id_usuarios = r.atendido_por ";

    /** Lista todos los reportes o solo los de un usuario (si idUsuario != null). */
    public List<Map<String, Object>> listar(Integer idUsuario) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = SELECT_BASE
                + (idUsuario == null ? "" : "WHERE r.Usuarios_id_usuarios = ? ")
                + "ORDER BY r.fecha_creacion DESC, r.id_reportes DESC";

        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (idUsuario != null) {
                ps.setInt(1, idUsuario);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("ReportesDAO - error al listar: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(String titulo, String descripcion, String tipo,
                            int idUsuarioReporta, Integer ambienteId) {
        String sql = "INSERT INTO reportes (titulo, descripcion, tipo, "
                + "Usuarios_id_usuarios, Ambientes_id_ambientes) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, (descripcion == null || descripcion.isBlank()) ? null : descripcion);
            ps.setString(3, tipo == null || tipo.isBlank() ? "ambiente" : tipo);
            ps.setInt(4, idUsuarioReporta);
            if (ambienteId == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, ambienteId);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("ReportesDAO - error al insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean atender(int idReporte, String respuesta, int idAdmin) {
        String sql = "UPDATE reportes SET estado = 'atendido', respuesta_admin = ?, "
                + "atendido_por = ?, fecha_atencion = CURRENT_TIMESTAMP "
                + "WHERE id_reportes = ?";
        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, (respuesta == null || respuesta.isBlank()) ? null : respuesta);
            ps.setInt(2, idAdmin);
            ps.setInt(3, idReporte);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("ReportesDAO - error al atender: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idReporte) {
        String sql = "DELETE FROM reportes WHERE id_reportes = ?";
        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReporte);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("ReportesDAO - error al eliminar: " + e.getMessage());
            return false;
        }
    }

    private Map<String, Object> mapear(ResultSet rs) throws SQLException {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", rs.getInt("id_reportes"));
        m.put("titulo", rs.getString("titulo"));
        m.put("descripcion", rs.getString("descripcion"));
        m.put("tipo", rs.getString("tipo"));
        m.put("estado", rs.getString("estado"));
        Timestamp creacion = rs.getTimestamp("fecha_creacion");
        m.put("fechaCreacion", creacion == null ? null : creacion.toString());
        Timestamp atencion = rs.getTimestamp("fecha_atencion");
        m.put("fechaAtencion", atencion == null ? null : atencion.toString());
        m.put("respuestaAdmin", rs.getString("respuesta_admin"));
        int reportaId = rs.getInt("reportaId");
        m.put("reportaId", rs.wasNull() ? null : reportaId);
        m.put("reporta", rs.getString("reportaNombre"));
        int ambienteId = rs.getInt("ambienteId");
        m.put("ambienteId", rs.wasNull() ? null : ambienteId);
        m.put("ambiente", rs.getString("ambienteNombre"));
        int atendidoPorId = rs.getInt("atendidoPorId");
        m.put("atendidoPorId", rs.wasNull() ? null : atendidoPorId);
        m.put("atendidoPor", rs.getString("atendidoPorNombre"));
        return m;
    }
}
