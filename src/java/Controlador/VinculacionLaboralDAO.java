package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.VinculacionLaboral;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class VinculacionLaboralDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todas las vinculaciones laborales
    // ══════════════════════════════════════════════════════════════
    public List<VinculacionLaboral> listarVinculacionesLaborales() {
        List<VinculacionLaboral> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar vinculaciones laborales.");
            return lista;
        }

        String sql = "SELECT id_vinculacion_Laboral, descripcion, numero_Contrato, fecha_Inicio, " +
                     "fecha_Fin, Usuarios_id_usuarios FROM vinculacionLaboral ORDER BY fecha_Inicio DESC";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                VinculacionLaboral v = new VinculacionLaboral();
                v.setId_vinculacion_Laboral(rs.getInt("id_vinculacion_Laboral"));
                v.setDescripcion(rs.getString("descripcion"));
                v.setNumero_Contrato(rs.getString("numero_Contrato"));
                v.setFecha_Inicio(rs.getObject("fecha_Inicio", LocalDate.class));
                v.setFecha_Fin(rs.getObject("fecha_Fin", LocalDate.class));
                v.setUsuarios_id_usuarios(rs.getInt("Usuarios_id_usuarios"));
                lista.add(v);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar vinculaciones laborales: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarVinculacionLaboral(VinculacionLaboral rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO vinculacionLaboral (id_vinculacion_Laboral, descripcion, numero_Contrato, fecha_Inicio, fecha_Fin, Usuarios_id_usuarios) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_vinculacion_Laboral());
            ps.setString(2, rol.getDescripcion());
            ps.setString(3, rol.getNumero_Contrato());
            ps.setObject(4, rol.getFecha_Inicio());
            ps.setObject(5, rol.getFecha_Fin());
            ps.setInt(6, rol.getUsuarios_id_usuarios());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Vinculacion laboral insertada correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar la vinculacion laboral: " + e.getMessage());
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
    
    public VinculacionLaboral consultaVinculacionLaboral(int IdvinculacionLaboral) {
        VinculacionLaboral rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_vinculacion_Laboral, descripcion, numero_Contrato, fecha_Inicio, fecha_Fin, Usuarios_id_usuarios FROM vinculacionLaboral WHERE id_vinculacion_Laboral = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, IdvinculacionLaboral);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new VinculacionLaboral();
                rolEncontrado.setId_vinculacion_Laboral(rs.getInt("Id_vinculacion_Laboral"));
                rolEncontrado.setDescripcion(rs.getString("Descripcion"));
                rolEncontrado.setNumero_Contrato(rs.getString("Numero_Contrato"));
                rolEncontrado.setFecha_Inicio(rs.getObject("Fecha_Inicio", LocalDate.class));
                rolEncontrado.setFecha_Fin(rs.getObject("Fecha_Fin", LocalDate.class));
                rolEncontrado.setUsuarios_id_usuarios(rs.getInt("Usuarios_id_usuarios"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar la vinculacion laboral: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarVinculacionLaboral(int IdvinculacionLaboral) {
        
        String sql = "DELETE FROM vinculacionLaboral WHERE id_vinculacion_Laboral = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, IdvinculacionLaboral);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar vinculacion laboral: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarVinculacionLaboral(VinculacionLaboral vinculacionLaboral) {
    
    String sql = "UPDATE vinculacion_laboral SET descripcion = ?, numero_Contrato = ?, fecha_Inicio = ?, fecha_Fin = ?, Usuarios_id_usuarios = ? WHERE id_vinculacion_Laboral = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, vinculacionLaboral.getDescripcion()); 
        ps.setString(2, vinculacionLaboral.getNumero_Contrato());
        ps.setObject(3, vinculacionLaboral.getFecha_Inicio());
        ps.setObject(4, vinculacionLaboral.getFecha_Fin());
        ps.setInt(5, vinculacionLaboral.getUsuarios_id_usuarios()); 
        ps.setInt(6, vinculacionLaboral.getId_vinculacion_Laboral());               
        
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
