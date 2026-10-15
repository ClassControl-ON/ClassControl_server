package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Etapa;


public class EtapaDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todas las etapas
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Etapa> listarEtapas() {
        java.util.List<Etapa> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar etapas.");
            return lista;
        }

        String sql = "SELECT id_etapa, descripcion_Etapa FROM etapa ORDER BY descripcion_Etapa";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Etapa e = new Etapa();
                e.setId_etapa(rs.getInt("id_etapa"));
                e.setDescripcion_Etapa(rs.getString("descripcion_Etapa"));
                lista.add(e);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar etapas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarEtapa(Etapa rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO etapa (id_etapa, descripcion_Etapa) VALUES (?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_etapa());
            ps.setString(2, rol.getDescripcion_Etapa());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Etapa insertada correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar la etapa: " + e.getMessage());
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
    
    public Etapa consultaEtapa(int Id_etapa) {
        Etapa rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_etapa, descripcion_Etapa FROM etapa WHERE id_etapa = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_etapa);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Etapa();
                rolEncontrado.setId_etapa(rs.getInt("Id_etapa"));
                rolEncontrado.setDescripcion_Etapa(rs.getString("Descripcion_Etapa"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar la etapa: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarEtapa(int Id_etapa) {
        
        String sql = "DELETE FROM etapa WHERE id_etapa = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_etapa);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar etapa: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarEtapa(Etapa etapa) {
    
    String sql = "UPDATE etapa SET descripcion_Etapa = ? WHERE id_etapa = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, etapa.getDescripcion_Etapa());   
        ps.setInt(2, etapa.getId_etapa());   
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Etapa actualizada correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar etapa: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
