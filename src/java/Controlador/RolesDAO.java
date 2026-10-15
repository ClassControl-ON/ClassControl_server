package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Roles;


public class RolesDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todos los roles
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Roles> listarRoles() {
        java.util.List<Roles> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar roles.");
            return lista;
        }

        String sql = "SELECT id_roles, descripcion_Roles FROM roles ORDER BY descripcion_Roles";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Roles r = new Roles();
                r.setId_roles(rs.getInt("id_roles"));
                r.setDescripcion_Roles(rs.getString("descripcion_Roles"));
                lista.add(r);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar roles: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarRoles(Roles rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO roles (id_roles, descripcion_Roles) VALUES (?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_roles());
            ps.setString(2, rol.getDescripcion_Roles());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Rol insertado correctamente en la base de datos.");
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
    
    public Roles consultaRoles(int Id_roles) {
        Roles rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_roles, descripcion_Roles FROM roles WHERE id_roles = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_roles);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Roles();
                rolEncontrado.setId_roles(rs.getInt("Id_roles"));
                rolEncontrado.setDescripcion_Roles(rs.getString("Descripcion_Roles"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar el rol: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarRoles(int Id_roles) {
        
        String sql = "DELETE FROM roles WHERE id_roles = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_roles);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar rol: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarRoles(Roles roles) {
    
    String sql = "UPDATE roles SET descripcion_Roles = ? WHERE id_roles = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, roles.getDescripcion_Roles());   
        ps.setInt(2, roles.getId_roles());               
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Rol actualizado correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar rol: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
}
