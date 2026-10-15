package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Tipo_Documento;


public class Tipo_DocumentoDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todos los tipos de documento
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Tipo_Documento> listarTiposDocumento() {
        java.util.List<Tipo_Documento> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar tipos de documento.");
            return lista;
        }

        String sql = "SELECT id_tipo_Documento, descripcion_Tipo_Doc FROM tipo_documento ORDER BY descripcion_Tipo_Doc";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tipo_Documento t = new Tipo_Documento();
                t.setId_tipo_Documento(rs.getInt("id_tipo_Documento"));
                t.setDescripcion_Tipo_Doc(rs.getString("descripcion_Tipo_Doc"));
                lista.add(t);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar tipos de documento: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarTipo_Documento(Tipo_Documento rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO tipo_documento (id_tipo_Documento, descripcion_Tipo_Doc) VALUES (?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_tipo_Documento());
            ps.setString(2, rol.getDescripcion_Tipo_Doc());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Tipo de documento insertado correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar el tipo de documento: " + e.getMessage());
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
    
    public Tipo_Documento consultaTipo_Documento(int Id_tipo_Documento) {
        Tipo_Documento rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_tipo_Documento, descripcion_Tipo_Doc FROM tipo_documento WHERE id_tipo_Documento = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_tipo_Documento);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Tipo_Documento();
                rolEncontrado.setId_tipo_Documento(rs.getInt("Id_tipo_Documento"));
                rolEncontrado.setDescripcion_Tipo_Doc(rs.getString("Descripcion_Tipo_Doc"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar el tipo de documento: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarTipo_Documento(int Id_tipo_Documento) {
        
        String sql = "DELETE FROM tipo_documento WHERE id_tipo_Documento = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_tipo_Documento);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar tipo de documento: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarTipo_Documento(Tipo_Documento tipo_Documento) {
    
    String sql = "UPDATE tipo_documento SET descripcion_Tipo_Doc = ? WHERE id_tipo_Documento = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, tipo_Documento.getDescripcion_Tipo_Doc());   
        ps.setInt(2, tipo_Documento.getId_tipo_Documento());               
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Tipo de documento actualizado correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar tipo de documento: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
