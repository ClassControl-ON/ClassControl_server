package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Resultado_aprendizaje;


public class Resultado_aprendizajeDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todos los resultados de aprendizaje
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Resultado_aprendizaje> listarResultados() {
        java.util.List<Resultado_aprendizaje> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar resultados de aprendizaje.");
            return lista;
        }

        String sql = "SELECT id_resultado_aprendizaje, codigo_ResultadoAp, descripcion_Resul, " +
                     "Competencias_id_competencias FROM resultado_aprendizaje ORDER BY descripcion_Resul";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Resultado_aprendizaje r = new Resultado_aprendizaje();
                r.setId_resultado_aprendizaje(rs.getInt("id_resultado_aprendizaje"));
                r.setCodigo_ResultadoAp(rs.getInt("codigo_ResultadoAp"));
                r.setDescripcion_Resul(rs.getString("descripcion_Resul"));
                r.setCompetencias_id_competencias(rs.getInt("Competencias_id_competencias"));
                lista.add(r);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar resultados de aprendizaje: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarResultado_aprendizaje(Resultado_aprendizaje rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO resultado_aprendizaje (id_resultado_aprendizaje, codigo_ResultadoAp, descripcion_Resul, Competencias_id_competencias) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_resultado_aprendizaje());
            ps.setInt(2, rol.getCodigo_ResultadoAp());
            ps.setString(3, rol.getDescripcion_Resul());
            ps.setInt(4, rol.getCompetencias_id_competencias());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Resultado de aprendizaje insertado correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar el resultado de aprendizaje: " + e.getMessage());
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
    
    public Resultado_aprendizaje consultaResultado_aprendizaje(int Id_resultado_aprendizaje) {
        Resultado_aprendizaje rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_resultado_aprendizaje, codigo_ResultadoAp, descripcion_Resul, Competencias_id_competencias FROM resultado_aprendizaje WHERE id_resultado_aprendizaje = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_resultado_aprendizaje);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Resultado_aprendizaje();
                rolEncontrado.setId_resultado_aprendizaje(rs.getInt("Id_resultado_aprendizaje"));
                rolEncontrado.setCodigo_ResultadoAp(rs.getInt("Codigo_ResultadoAp"));
                rolEncontrado.setDescripcion_Resul(rs.getString("Descripcion_Resul"));
                rolEncontrado.setCompetencias_id_competencias(rs.getInt("Competencias_id_competencias"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar el resultado de aprendizaje: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarResultado_aprendizaje(int Id_resultado_aprendizaje) {
        
        String sql = "DELETE FROM resultado_aprendizaje WHERE id_resultado_aprendizaje = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_resultado_aprendizaje);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar resultado de aprendizaje: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarResultado_aprendizaje(Resultado_aprendizaje resultado_aprendizaje) {
    
    String sql = "UPDATE resultado_aprendizaje SET codigo_ResultadoAp = ?, descripcion_Resul = ?, Competencias_id_competencias = ? WHERE id_resultado_aprendizaje = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, resultado_aprendizaje.getCodigo_ResultadoAp()); 
        ps.setObject(2, resultado_aprendizaje.getDescripcion_Resul());
        ps.setInt(3, resultado_aprendizaje.getCompetencias_id_competencias());
        ps.setInt(4, resultado_aprendizaje.getId_resultado_aprendizaje());               
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Resultado de aprendizaje actualizado correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar resultado de aprendizaje: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
