package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Sede;


public class SedeDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todas las sedes
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Sede> listarSedes() {
        java.util.List<Sede> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar sedes.");
            return lista;
        }

        String sql = "SELECT id_sede, nombre_sede FROM sede ORDER BY nombre_sede";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sede s = new Sede();
                s.setId_sede(rs.getInt("id_sede"));
                s.setNombre_sede(rs.getString("nombre_sede"));
                lista.add(s);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar sedes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarSede(Sede rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO sede (id_sede, nombre_sede) VALUES (?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_sede());
            ps.setString(2, rol.getNombre_sede());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅Sede insertada correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar el rol: " + e.getMessage());
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
    
    public Sede consultaSede(int Id_sede) {
        Sede rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_sede, nombre_sede FROM sede WHERE id_sede = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_sede);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Sede();
                rolEncontrado.setId_sede(rs.getInt("Id_sede"));
                rolEncontrado.setNombre_sede(rs.getString("Nombre_sede"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar la sede: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarSede(int Id_sede) {
        
        String sql = "DELETE FROM sede WHERE id_sede = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_sede);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar sede: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarSede(Sede sede) {
    
    String sql = "UPDATE sede SET nombre_sede = ? WHERE id_sede = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, sede.getNombre_sede());   
        ps.setInt(2, sede.getId_sede());               
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Sede actualizada correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar sede: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
