package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Trimestre;
import java.time.LocalDate;


public class TrimestreDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todos los trimestres
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Trimestre> listarTrimestres() {
        java.util.List<Trimestre> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar trimestres.");
            return lista;
        }

        String sql = "SELECT id_trimestre, num_trimestre, descripcion, fecha_inicio, fecha_fin "
                + "FROM trimestre WHERE num_trimestre <= 7 ORDER BY num_trimestre ASC";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Trimestre t = new Trimestre();
                t.setId_trimestre(rs.getInt("id_trimestre"));
                t.setNum_trimestre(rs.getInt("num_trimestre"));
                t.setDescripcion(rs.getString("descripcion"));
                t.setFecha_inicio(rs.getObject("fecha_inicio", java.time.LocalDate.class));
                t.setFecha_fin(rs.getObject("fecha_fin", java.time.LocalDate.class));
                lista.add(t);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar trimestres: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarTrimestre(Trimestre rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO trimestre (id_trimestre, num_trimestre, descripcion, fecha_inicio, fecha_fin) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_trimestre());
            ps.setInt(2, rol.getNum_trimestre());
            ps.setString(3, rol.getDescripcion());
            ps.setObject(4, rol.getFecha_inicio());
            ps.setObject(5, rol.getFecha_fin());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Trimestre insertado correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar el trimestre: " + e.getMessage());
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
    
    public Trimestre consultaTrimestre(int Id_trimestre) {
        Trimestre rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_trimestre, num_trimestre, descripcion, fecha_inicio, fecha_fin FROM trimestre WHERE id_trimestre = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_trimestre);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Trimestre();
                rolEncontrado.setId_trimestre(rs.getInt("Id_trimestre"));
                rolEncontrado.setNum_trimestre(rs.getInt("Num_trimestre"));
                rolEncontrado.setDescripcion(rs.getString("Descripcion"));
                rolEncontrado.setFecha_inicio(rs.getObject("fecha_inicio", LocalDate.class));
                rolEncontrado.setFecha_fin(rs.getObject("fecha_fin", LocalDate.class));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar el trimestre: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }

    public boolean eliminarTrimestre(int Id_trimestre) {
        
        String sql = "DELETE FROM trimestre WHERE id_trimestre = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_trimestre);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar trimestre: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarTrimestre(Trimestre trimestre) {
    
    String sql = "UPDATE trimestre SET num_trimestre = ?, descripcion = ?, fecha_inicio = ?, fecha_fin = ? WHERE id_trimestre = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, trimestre.getNum_trimestre()); 
        ps.setString(2, trimestre.getDescripcion());
        ps.setObject(3, trimestre.getFecha_inicio());
        ps.setObject(4, trimestre.getFecha_fin());
        ps.setInt(5, trimestre.getId_trimestre());               
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Trimestre actualizado correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar trimestre: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
