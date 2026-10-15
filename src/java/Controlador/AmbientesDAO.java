package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Ambientes;


public class AmbientesDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todos los ambientes
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Ambientes> listarAmbientes() {
        java.util.List<Ambientes> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar ambientes.");
            return lista;
        }

        String sql = "SELECT id_ambientes, descripcion_Ambiente, capacidad, Sede_id_sede, Estado_Ambiente FROM ambientes ORDER BY descripcion_Ambiente";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ambientes a = new Ambientes();
                a.setId_ambientes(rs.getInt("id_ambientes"));
                a.setDescripcion_Ambiente(rs.getString("descripcion_Ambiente"));
                a.setCapacidad(rs.getInt("capacidad"));
                a.setSede_id_sede(rs.getInt("Sede_id_sede"));
                a.setEstado_Ambiente(rs.getString("Estado_Ambiente"));
                lista.add(a);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar ambientes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarAmbientes(Ambientes rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO ambientes (id_ambientes, descripcion_Ambiente, capacidad, Sede_id_sede, Estado_Ambiente) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_ambientes());
            ps.setString(2, rol.getDescripcion_Ambiente());
            ps.setInt(3, rol.getCapacidad());
            ps.setInt(4, rol.getSede_id_sede());
            ps.setString(5, (rol.getEstado_Ambiente() == null || rol.getEstado_Ambiente().isBlank())
                    ? "Disponible" : rol.getEstado_Ambiente());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Ambiente insertado correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar el ambiente: " + e.getMessage());
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
    
    public Ambientes consultaAmbientes(int Id_ambientes) {
        Ambientes rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_ambientes, descripcion_Ambiente, capacidad, Sede_id_sede, Estado_Ambiente FROM ambientes WHERE id_ambientes = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_ambientes);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Ambientes();
                rolEncontrado.setId_ambientes(rs.getInt("Id_ambientes"));
                rolEncontrado.setDescripcion_Ambiente(rs.getString("Descripcion_Ambiente"));
                rolEncontrado.setCapacidad(rs.getInt("Capacidad"));
                rolEncontrado.setSede_id_sede(rs.getInt("Sede_id_sede"));
                rolEncontrado.setEstado_Ambiente(rs.getString("Estado_Ambiente"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar el ambiente: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarAmbientes(int Id_ambientes) {
        
        String sql = "DELETE FROM ambientes WHERE id_ambientes = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_ambientes);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar ambiente: " + e.getMessage());
             return false;
         }
     }
    
    /** Cambia solo el estado administrativo del ambiente. */
    public boolean actualizarEstadoAmbiente(int idAmbiente, String estado) {
        String sql = "UPDATE ambientes SET Estado_Ambiente = ? WHERE id_ambientes = ?";
        Conexion conexion = new Conexion();
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idAmbiente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del ambiente: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarAmbientes(Ambientes ambientes) {
    
    String sql = "UPDATE ambientes SET descripcion_Ambiente = ?, capacidad = ?, Sede_id_sede = ?, Estado_Ambiente = ? WHERE id_ambientes = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, ambientes.getDescripcion_Ambiente());   
        ps.setInt(2, ambientes.getCapacidad()); 
        ps.setInt(3, ambientes.getSede_id_sede()); 
        ps.setString(4, (ambientes.getEstado_Ambiente() == null || ambientes.getEstado_Ambiente().isBlank())
                ? "Disponible" : ambientes.getEstado_Ambiente());
        ps.setInt(5, ambientes.getId_ambientes());    
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Ambiente actualizada correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar ambiente: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
