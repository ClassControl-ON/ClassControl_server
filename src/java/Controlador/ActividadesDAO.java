package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Actividades;


public class ActividadesDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todas las actividades
    // ══════════════════════════════════════════════════════════════
    public java.util.List<Actividades> listarActividades() {
        java.util.List<Actividades> lista = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar actividades.");
            return lista;
        }

        String sql = "SELECT id_actividades, codigo_Actividad, nombre_Act, descripcion, " +
                     "Resultado_aprendizaje_id_resultado_aprendizaje FROM actividades ORDER BY nombre_Act";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Actividades a = new Actividades();
                a.setId_actividades(rs.getInt("id_actividades"));
                a.setCodigo_Actividad(rs.getInt("codigo_Actividad"));
                a.setNombre_Act(rs.getString("nombre_Act"));
                a.setDescripcion(rs.getString("descripcion"));
                a.setResultado_aprendizaje_id_resultado_aprendizaje(
                        rs.getInt("Resultado_aprendizaje_id_resultado_aprendizaje"));
                lista.add(a);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar actividades: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarActividades(Actividades rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO actividades (id_actividades, codigo_Actividad, nombre_Act, descripcion, Resultado_aprendizaje_id_resultado_aprendizaje) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_actividades());
            ps.setInt(2, rol.getCodigo_Actividad());
            ps.setString(3, rol.getNombre_Act());
            ps.setString(4, rol.getDescripcion());
            ps.setInt(5, rol.getResultado_aprendizaje_id_resultado_aprendizaje());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Actividad insertada correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar la actividad: " + e.getMessage());
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
    
    public Actividades consultaActividades(int Id_actividades) {
        Actividades rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_actividades, codigo_Actividad, nombre_Act, descripcion, Resultado_aprendizaje_id_resultado_aprendizaje FROM actividades WHERE id_actividades = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_actividades);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Actividades();
                rolEncontrado.setId_actividades(rs.getInt("Id_actividades"));
                rolEncontrado.setCodigo_Actividad(rs.getInt("Codigo_Actividad"));
                rolEncontrado.setNombre_Act(rs.getString("Nombre_Act"));
                rolEncontrado.setDescripcion(rs.getString("Descripcion"));
                rolEncontrado.setResultado_aprendizaje_id_resultado_aprendizaje(rs.getInt("Resultado_aprendizaje_id_resultado_aprendizaje"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar la actividad: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarActividades(int Id_actividades) {

        String sqlProgramaciones =
                "DELETE FROM programacion_instructores WHERE Actividades_id_actividades = ?";
        String sql = "DELETE FROM actividades WHERE Id_actividades = ?";
        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement psProg = con.prepareStatement(sqlProgramaciones)) {
                psProg.setInt(1, Id_actividades);
                psProg.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, Id_actividades);
                int filas = ps.executeUpdate();
                con.commit();
                return filas > 0;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
         } catch (SQLException e) {
             System.out.println("Error al eliminar actividad: " + e.getMessage());
             return false;
         }
     }
    
    public boolean actualizarActividades(Actividades actividades) {
    
    String sql = "UPDATE actividades SET codigo_Actividad = ?, nombre_Act = ?, descripcion = ?, Resultado_aprendizaje_id_resultado_aprendizaje = ? WHERE id_actividades = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, actividades.getCodigo_Actividad());   
        ps.setString(2, actividades.getNombre_Act()); 
        ps.setString(3, actividades.getDescripcion()); 
        ps.setInt(4, actividades.getResultado_aprendizaje_id_resultado_aprendizaje()); 
        ps.setInt(5, actividades.getId_actividades());               
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Actividad actualizada correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar actividad: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
