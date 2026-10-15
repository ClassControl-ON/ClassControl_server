package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Jornada;


public class JornadaDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todas las jornadas
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Jornada> listarJornadas() {
        java.util.List<Jornada> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar jornadas.");
            return lista;
        }

        String sql = "SELECT id_jornada, descripcion_Jornada FROM jornada ORDER BY descripcion_Jornada";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Jornada j = new Jornada();
                j.setId_jornada(rs.getInt("id_jornada"));
                j.setDescripcion_Jornada(rs.getString("descripcion_Jornada"));
                lista.add(j);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar jornadas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarJornada(Jornada rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO jornada (id_jornada, descripcion_Jornada) VALUES (?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_jornada());
            ps.setString(2, rol.getDescripcion_Jornada());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Jornada insertada correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar la jornada: " + e.getMessage());
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
    
    public Jornada consultaJornada(int Id_jornada) {
        Jornada rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_jornada, descripcion_Jornada FROM jornada WHERE id_jornada = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_jornada);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Jornada();
                rolEncontrado.setId_jornada(rs.getInt("Id_jornada"));
                rolEncontrado.setDescripcion_Jornada(rs.getString("Descripcion_Jornada"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar la jornada: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarJornada(int Id_jornada) {
        
        String sql = "DELETE FROM jornada WHERE id_jornada = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_jornada);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar jornada: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarJornada(Jornada jornada) {
    
    String sql = "UPDATE jornada SET descripcion_Jornada = ? WHERE id_jornada = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, jornada.getDescripcion_Jornada());   
        ps.setInt(2, jornada.getId_jornada());               
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Jornada actualizada correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar jornada: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
