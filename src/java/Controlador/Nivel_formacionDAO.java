package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Nivel_formacion;


public class Nivel_formacionDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todos los niveles de formación
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Nivel_formacion> listarNiveles() {
        java.util.List<Nivel_formacion> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar niveles de formación.");
            return lista;
        }

        String sql = "SELECT id_nivel_formacion, descripcion_Nivel_Formacion FROM nivel_formacion ORDER BY descripcion_Nivel_Formacion";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Nivel_formacion n = new Nivel_formacion();
                n.setId_nivel_formacion(rs.getInt("id_nivel_formacion"));
                n.setDescripcion_Nivel_Formacion(rs.getString("descripcion_Nivel_Formacion"));
                lista.add(n);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar niveles de formación: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarNivel_formacion(Nivel_formacion rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO nivel_formacion (id_nivel_formacion, descripcion_Nivel_Formacion) VALUES (?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_nivel_formacion());
            ps.setString(2, rol.getDescripcion_Nivel_Formacion());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Nivel de formacion insertada correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar el nivel de formacion: " + e.getMessage());
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
    
    public Nivel_formacion consultaNivel_formacion(int Id_nivel_formacion) {
        Nivel_formacion rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_nivel_formacion, descripcion_Nivel_Formacion FROM nivel_formacion WHERE id_nivel_formacion = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_nivel_formacion);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Nivel_formacion();
                rolEncontrado.setId_nivel_formacion(rs.getInt("Id_nivel_formacion"));
                rolEncontrado.setDescripcion_Nivel_Formacion(rs.getString("Descripcion_Nivel_Formacion"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar el nivel de formacion: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarNivel_formacion(int Id_nivel_formacion) {
        
        String sql = "DELETE FROM nivel_formacion WHERE id_nivel_formacion = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_nivel_formacion);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar nivel de formacion: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarNivel_formacion(Nivel_formacion nivel_formacion) {
    
    String sql = "UPDATE nivel_formacion SET descripcion_Nivel_Formacion = ? WHERE id_nivel_formacion = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, nivel_formacion.getDescripcion_Nivel_Formacion());   
        ps.setInt(2, nivel_formacion.getId_nivel_formacion());   
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Nivel de formacion actualizada correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar el nivel de formacion: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
