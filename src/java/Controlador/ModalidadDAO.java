package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Modalidad;


public class ModalidadDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todas las modalidades
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Modalidad> listarModalidades() {
        java.util.List<Modalidad> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar modalidades.");
            return lista;
        }

        String sql = "SELECT id_modalidad, descripcion_Modalidad FROM modalidad ORDER BY descripcion_Modalidad";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Modalidad m = new Modalidad();
                m.setId_modalidad(rs.getInt("id_modalidad"));
                m.setDescripcion_Modalidad(rs.getString("descripcion_Modalidad"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar modalidades: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarModalidad(Modalidad rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO modalidad (id_modalidad, descripcion_Modalidad) VALUES (?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_modalidad());
            ps.setString(2, rol.getDescripcion_Modalidad());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Modalidad insertada correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar la modalidad: " + e.getMessage());
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
    
    public Modalidad consultaModalidad(int Id_modalidad) {
        Modalidad rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_modalidad, descripcion_Modalidad FROM modalidad WHERE id_modalidad = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_modalidad);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Modalidad();
                rolEncontrado.setId_modalidad(rs.getInt("Id_modalidad"));
                rolEncontrado.setDescripcion_Modalidad(rs.getString("Descripcion_Modalidad"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar la modalidad: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarModalidad(int Id_modalidad) {
        
        String sql = "DELETE FROM modalidad WHERE id_modalidad = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_modalidad);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar modalidad: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarModalidad(Modalidad modalidad) {
    
    String sql = "UPDATE modalidad SET descripcion_Modalidad = ? WHERE id_modalidad = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, modalidad.getDescripcion_Modalidad());   
        ps.setInt(2, modalidad.getId_modalidad());               
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Modalidad actualizada correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar modalidad: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
