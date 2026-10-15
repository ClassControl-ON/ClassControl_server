package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Estado;


public class EstadoDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todos los estados
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Estado> listarEstados() {
        java.util.List<Estado> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar estados.");
            return lista;
        }

        String sql = "SELECT id_estado, descripcion_Estado, Tipo_Estado_id_tipo_estado FROM estado ORDER BY descripcion_Estado";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Estado e = new Estado();
                e.setId_estado(rs.getInt("id_estado"));
                e.setDescripcion_Estado(rs.getString("descripcion_Estado"));
                e.setTipo_Estado_id_tipo_estado(rs.getInt("Tipo_Estado_id_tipo_estado"));
                lista.add(e);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar estados: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarEstado(Estado rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO estado (id_estado, descripcion_Estado, Tipo_Estado_id_tipo_estado) VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_estado());
            ps.setString(2, rol.getDescripcion_Estado());
            ps.setInt(3, rol.getTipo_Estado_id_tipo_estado());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Estado insertado correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar el estado: " + e.getMessage());
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
    
    public Estado consultaEstado(int Id_estado) {
        Estado rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_estado, descripcion_Estado, Tipo_Estado_id_tipo_estado FROM estado WHERE id_estado = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_estado);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Estado();
                rolEncontrado.setId_estado(rs.getInt("Id_estado"));
                rolEncontrado.setDescripcion_Estado(rs.getString("Descripcion_Estado"));
                rolEncontrado.setTipo_Estado_id_tipo_estado(rs.getInt("Tipo_Estado_id_tipo_estado"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar el estado: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarEstado(int Id_estado) {
        
        String sql = "DELETE FROM estado WHERE id_estado = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_estado);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar estado: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarEstado(Estado estado) {
    
    String sql = "UPDATE estado SET descripcion_Estado = ?, Tipo_Estado_id_tipo_estado = ? WHERE id_estado = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, estado.getDescripcion_Estado());   
        ps.setInt(2, estado.getTipo_Estado_id_tipo_estado());
        ps.setInt(3, estado.getId_estado());   
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Estado actualizado correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar estado: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
