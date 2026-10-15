package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Programas;


public class ProgramasDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todos los programas
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Programas> listarProgramas() {
        java.util.List<Programas> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar programas.");
            return lista;
        }

        String sql = "SELECT idProgramas, codigo_programa, nombre_programa FROM programas ORDER BY nombre_programa";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Programas p = new Programas();
                p.setIdProgramas(rs.getInt("idProgramas"));
                p.setCodigo_programa(rs.getInt("codigo_programa"));
                p.setNombre_programa(rs.getString("nombre_programa"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar programas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarProgramas(Programas rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO programas (idProgramas, codigo_programa, nombre_programa) VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getIdProgramas());
            ps.setInt(2, rol.getCodigo_programa());
            ps.setString(3, rol.getNombre_programa());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Programa insertado correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar el programa: " + e.getMessage());
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
    
    public Programas consultaProgramas(int Id_programas) {
        Programas rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT idProgramas, codigo_programa, nombre_programa FROM programas WHERE idProgramas = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_programas);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Programas();
                rolEncontrado.setIdProgramas(rs.getInt("IdProgramas"));
                rolEncontrado.setCodigo_programa(rs.getInt("Codigo_programa"));
                rolEncontrado.setNombre_programa(rs.getString("Nombre_programa"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar el programa: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarProgramas(int Id_programas) {
        
        String sql = "DELETE FROM programas WHERE idProgramas = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_programas);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar programa: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarProgramas(Programas programas) {
    
    String sql = "UPDATE programas SET codigo_programa = ?, nombre_programa = ? WHERE idProgramas = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, programas.getCodigo_programa());
        ps.setString(2, programas.getNombre_programa());
        ps.setInt(3, programas.getIdProgramas());   
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Programa actualizado correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar el programa: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
