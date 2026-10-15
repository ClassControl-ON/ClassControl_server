package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Competencias;
import java.util.ArrayList;
import java.util.List;


public class CompetenciasDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todas las competencias
    // ══════════════════════════════════════════════════════════════
    public List<Competencias> listarCompetencias() {
        List<Competencias> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar competencias.");
            return lista;
        }

        String sql = "SELECT id_competencias, codigo_Competencias, descripcion_Competencias, " +
                     "Programas_idProgramas " +
                     "FROM competencias ORDER BY descripcion_Competencias";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Competencias c = new Competencias();
                c.setId_competencias(rs.getInt("id_competencias"));
                c.setCodigo_Competencias(rs.getInt("codigo_Competencias"));
                c.setDescripcion_Competencias(rs.getString("descripcion_Competencias"));
                c.setProgramas_idProgramas(rs.getInt("Programas_idProgramas"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar competencias: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarCompetencias(Competencias rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO competencias (id_competencias, codigo_Competencias, descripcion_Competencias, Programas_idProgramas) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_competencias());
            ps.setInt(2, rol.getCodigo_Competencias());
            ps.setString(3, rol.getDescripcion_Competencias());
            ps.setInt(4, rol.getProgramas_idProgramas());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅Competencia insertada correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar la competencia: " + e.getMessage());
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
    
    public Competencias consultaCompetencias(int Id_competencias) {
        Competencias rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_competencias, codigo_Competencias, descripcion_Competencias, Programas_idProgramas FROM competencias WHERE id_competencias = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_competencias);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Competencias();
                rolEncontrado.setId_competencias(rs.getInt("Id_competencias"));
                rolEncontrado.setCodigo_Competencias(rs.getInt("Codigo_Competencias"));
                rolEncontrado.setDescripcion_Competencias(rs.getString("Descripcion_Competencias"));
                rolEncontrado.setProgramas_idProgramas(rs.getInt("Programas_idProgramas"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar la competencia: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarCompetencias(int Id_competencias) {
        
        String sql = "DELETE FROM competencias WHERE id_competencias = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_competencias);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar competencia: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarCompetencias(Competencias competencias) {
    
    String sql = "UPDATE competencias SET codigo_Competencias = ?, descripcion_Competencias = ?, Programas_idProgramas = ? WHERE id_competencias = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, competencias.getCodigo_Competencias());   
        ps.setString(2, competencias.getDescripcion_Competencias()); 
        ps.setInt(3, competencias.getProgramas_idProgramas());
        ps.setInt(4, competencias.getId_competencias());    
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Competencia actualizada correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar competencia: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
   
}
