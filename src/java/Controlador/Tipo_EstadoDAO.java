package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Modelo.Tipo_Estado;

public class Tipo_EstadoDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todos los tipos de estado
    // ══════════════════════════════════════════════════════════════
    public List<Tipo_Estado> listarTiposEstado() {
        List<Tipo_Estado> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar tipos de estado.");
            return lista;
        }

        String sql = "SELECT id_tipo_estado, descripcion FROM tipo_estado ORDER BY descripcion";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tipo_Estado t = new Tipo_Estado();
                t.setId_tipo_estado(rs.getInt("id_tipo_estado"));
                t.setDescripcion(rs.getString("descripcion"));
                lista.add(t);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar tipos de estado: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarTipo_Estado(Tipo_Estado tipoEstado) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();

        String sql = "INSERT INTO tipo_estado (id_tipo_estado, descripcion) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tipoEstado.getId_tipo_estado());
            ps.setString(2, tipoEstado.getDescripcion());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Tipo de estado insertado correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar el tipo de estado: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return insertado;
    }

    public Tipo_Estado consultaTipo_Estado(int Id_tipo_estado) {
        Tipo_Estado tipoEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_tipo_estado, descripcion FROM tipo_estado WHERE id_tipo_estado = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_tipo_estado);

            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tipoEncontrado = new Tipo_Estado();
                tipoEncontrado.setId_tipo_estado(rs.getInt("id_tipo_estado"));
                tipoEncontrado.setDescripcion(rs.getString("descripcion"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar el tipo de estado: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return tipoEncontrado;
    }

    public boolean eliminarTipo_Estado(int Id_tipo_estado) {

        String sql = "DELETE FROM tipo_estado WHERE id_tipo_estado = ?";
        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Id_tipo_estado);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar tipo de estado: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarTipo_Estado(Tipo_Estado tipoEstado) {

        String sql = "UPDATE tipo_estado SET descripcion = ? WHERE id_tipo_estado = ?";

        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipoEstado.getDescripcion());
            ps.setInt(2, tipoEstado.getId_tipo_estado());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                System.out.println("Tipo de estado actualizado correctamente.");
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar tipo de estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
