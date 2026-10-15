package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Tipo_vinculacion;


public class Tipo_vinculacionDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todos los tipos de vinculación
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Tipo_vinculacion> listarTiposVinculacion() {
        java.util.List<Tipo_vinculacion> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar tipos de vinculación.");
            return lista;
        }

        String sql = "SELECT id_tipo_vinculacion, descripcion_vinculacion FROM tipo_vinculacion ORDER BY descripcion_vinculacion";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tipo_vinculacion t = new Tipo_vinculacion();
                t.setId_tipo_vinculacion(rs.getInt("id_tipo_vinculacion"));
                t.setDescripcion_vinculacion(rs.getString("descripcion_vinculacion"));
                lista.add(t);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar tipos de vinculación: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarTipo_Vinculacion(Tipo_vinculacion rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO tipo_vinculacion (id_tipo_vinculacion, descripcion_vinculacion) VALUES (?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_tipo_vinculacion());
            ps.setString(2, rol.getDescripcion_vinculacion());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Tipo de vinculacion insertado correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar el tipo de vinculacion: " + e.getMessage());
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
    
    public Tipo_vinculacion consultaTipo_vinculacion(int Id_tipo_vinculacion) {
        Tipo_vinculacion rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_tipo_vinculacion, descripcion_vinculacion FROM tipo_vinculacion WHERE id_tipo_vinculacion = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_tipo_vinculacion);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Tipo_vinculacion();
                rolEncontrado.setId_tipo_vinculacion(rs.getInt("Id_tipo_vinculacion"));
                rolEncontrado.setDescripcion_vinculacion(rs.getString("Descripcion_vinculacion"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar el tipo de vinculacion: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarTipo_vinculacion(int Id_tipo_vinculacion) {
        
        String sql = "DELETE FROM tipo_vinculacion WHERE id_tipo_vinculacion = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_tipo_vinculacion);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar tipo de vinculacion: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarTipo_vinculacion(Tipo_vinculacion tipo_vinculacion) {
    
    String sql = "UPDATE tipo_vinculacion SET descripcion_vinculacion = ? WHERE id_tipo_vinculacion = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, tipo_vinculacion.getDescripcion_vinculacion());   
        ps.setInt(2, tipo_vinculacion.getId_tipo_vinculacion());               
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Tipo de vinculacion actualizado correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar tipo de vinculacion: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
