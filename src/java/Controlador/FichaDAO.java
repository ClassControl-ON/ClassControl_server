package Controlador;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Modelo.Ficha;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class FichaDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — todas las fichas
    // ══════════════════════════════════════════════════════════════
    public List<Ficha> listarFichas() {
        List<Ficha> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar fichas.");
            return lista;
        }

        String sql = "SELECT id_ficha, codigo_ficha, fecha_inicio, fecha_fin, cantidad_aprendices, " +
                     "Programas_idProgramas, Jornada_id_jornada, Modalidad_id_modalidad, " +
                     "Nivel_formacion_id_nivel_formacion, Sede_id_sede, Estado_id_estado, Etapa_id_etapa " +
                     "FROM ficha ORDER BY codigo_ficha";

        try (PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ficha f = new Ficha();
                f.setId_ficha(rs.getInt("id_ficha"));
                f.setCodigo_ficha(rs.getInt("codigo_ficha"));
                f.setFecha_inicio(rs.getObject("fecha_inicio", LocalDate.class));
                f.setFecha_fin(rs.getObject("fecha_fin", LocalDate.class));
                f.setCantidad_aprendices(rs.getInt("cantidad_aprendices"));
                f.setProgramas_idProgramas(rs.getInt("Programas_idProgramas"));
                f.setJornada_id_jornada(rs.getInt("Jornada_id_jornada"));
                f.setModalidad_id_modalidad(rs.getInt("Modalidad_id_modalidad"));
                f.setNivel_formacion_id_nivel_formacion(rs.getInt("Nivel_formacion_id_nivel_formacion"));
                f.setSede_id_sede(rs.getInt("Sede_id_sede"));
                f.setEstado_id_estado(rs.getInt("Estado_id_estado"));
                f.setEtapa_id_etapa(rs.getInt("Etapa_id_etapa"));
                lista.add(f);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar fichas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — solo las fichas asignadas a la programación de un
    //  instructor concreto. Se usa para que la vista de un Instructor
    //  no muestre fichas con las que no tiene relación.
    // ══════════════════════════════════════════════════════════════
    public List<Ficha> listarFichasPorInstructor(int idInstructor) {
        List<Ficha> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar fichas por instructor.");
            return lista;
        }

        String sql = "SELECT DISTINCT f.id_ficha, f.codigo_ficha, f.fecha_inicio, f.fecha_fin, " +
                     "f.cantidad_aprendices, f.Programas_idProgramas, f.Jornada_id_jornada, " +
                     "f.Modalidad_id_modalidad, f.Nivel_formacion_id_nivel_formacion, " +
                     "f.Sede_id_sede, f.Estado_id_estado, f.Etapa_id_etapa " +
                     "FROM ficha f " +
                     "INNER JOIN programacion_instructores pi ON pi.Ficha_id_ficha = f.id_ficha " +
                     "WHERE pi.Usuarios_id_usuarios = ? " +
                     "ORDER BY f.codigo_ficha";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInstructor);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ficha f = new Ficha();
                    f.setId_ficha(rs.getInt("id_ficha"));
                    f.setCodigo_ficha(rs.getInt("codigo_ficha"));
                    f.setFecha_inicio(rs.getObject("fecha_inicio", LocalDate.class));
                    f.setFecha_fin(rs.getObject("fecha_fin", LocalDate.class));
                    f.setCantidad_aprendices(rs.getInt("cantidad_aprendices"));
                    f.setProgramas_idProgramas(rs.getInt("Programas_idProgramas"));
                    f.setJornada_id_jornada(rs.getInt("Jornada_id_jornada"));
                    f.setModalidad_id_modalidad(rs.getInt("Modalidad_id_modalidad"));
                    f.setNivel_formacion_id_nivel_formacion(rs.getInt("Nivel_formacion_id_nivel_formacion"));
                    f.setSede_id_sede(rs.getInt("Sede_id_sede"));
                    f.setEstado_id_estado(rs.getInt("Estado_id_estado"));
                    f.setEtapa_id_etapa(rs.getInt("Etapa_id_etapa"));
                    lista.add(f);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar fichas por instructor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    // ══════════════════════════════════════════════════════════════
    //  CONSULTAR — la ficha asignada a un aprendiz concreto.
    //  Fuente de verdad: la columna usuarios.Ficha_id_ficha
    //  (misma que usa la web). Devuelve null si no tiene ficha.
    // ══════════════════════════════════════════════════════════════
    public Ficha consultarFichaDeAprendiz(int idUsuario) {
        Ficha ficha = null;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para consultar la ficha del aprendiz.");
            return null;
        }

        String sql = "SELECT f.id_ficha, f.codigo_ficha, f.fecha_inicio, f.fecha_fin, " +
                     "f.cantidad_aprendices, f.Programas_idProgramas, f.Jornada_id_jornada, " +
                     "f.Modalidad_id_modalidad, f.Nivel_formacion_id_nivel_formacion, " +
                     "f.Sede_id_sede, f.Estado_id_estado, f.Etapa_id_etapa " +
                     "FROM ficha f " +
                     "INNER JOIN usuarios u ON u.Ficha_id_ficha = f.id_ficha " +
                     "WHERE u.id_usuarios = ? LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ficha = new Ficha();
                    ficha.setId_ficha(rs.getInt("id_ficha"));
                    ficha.setCodigo_ficha(rs.getInt("codigo_ficha"));
                    ficha.setFecha_inicio(rs.getObject("fecha_inicio", LocalDate.class));
                    ficha.setFecha_fin(rs.getObject("fecha_fin", LocalDate.class));
                    ficha.setCantidad_aprendices(rs.getInt("cantidad_aprendices"));
                    ficha.setProgramas_idProgramas(rs.getInt("Programas_idProgramas"));
                    ficha.setJornada_id_jornada(rs.getInt("Jornada_id_jornada"));
                    ficha.setModalidad_id_modalidad(rs.getInt("Modalidad_id_modalidad"));
                    ficha.setNivel_formacion_id_nivel_formacion(rs.getInt("Nivel_formacion_id_nivel_formacion"));
                    ficha.setSede_id_sede(rs.getInt("Sede_id_sede"));
                    ficha.setEstado_id_estado(rs.getInt("Estado_id_estado"));
                    ficha.setEtapa_id_etapa(rs.getInt("Etapa_id_etapa"));
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al consultar la ficha del aprendiz: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return ficha;
    }

    // ══════════════════════════════════════════════════════════════
    //  ASIGNAR — guarda (o reemplaza) la ficha de un aprendiz en
    //  usuarios.Ficha_id_ficha, la misma columna que usa la web.
    // ══════════════════════════════════════════════════════════════
    public boolean asignarFichaAAprendiz(int idUsuario, int idFicha) {
        boolean ok = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para asignar la ficha.");
            return false;
        }

        String sql = "UPDATE usuarios SET Ficha_id_ficha = ? WHERE id_usuarios = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFicha);
            ps.setInt(2, idUsuario);
            ok = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al asignar la ficha al aprendiz: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return ok;
    }

    public boolean insertarFicha(Ficha rol) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
       
        String sql = "INSERT INTO ficha (id_ficha, codigo_ficha, fecha_inicio, fecha_fin, cantidad_aprendices, Programas_idProgramas, Jornada_id_jornada, Modalidad_id_modalidad, Nivel_formacion_id_nivel_formacion, Sede_id_sede, Estado_id_estado, Etapa_id_etapa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rol.getId_ficha());
            ps.setInt(2, rol.getCodigo_ficha());
            ps.setObject(3, rol.getFecha_inicio());
            ps.setObject(4, rol.getFecha_fin());
            ps.setInt(5, rol.getCantidad_aprendices());
            ps.setInt(6, rol.getProgramas_idProgramas());
            ps.setInt(7, rol.getJornada_id_jornada());
            ps.setInt(8, rol.getModalidad_id_modalidad());
            ps.setInt(9, rol.getNivel_formacion_id_nivel_formacion());
            ps.setInt(10, rol.getSede_id_sede());
            ps.setInt(11, rol.getEstado_id_estado());
            ps.setInt(12, rol.getEtapa_id_etapa());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Ficha insertada correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar la ficha: " + e.getMessage());
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
    
    public Ficha consultaFicha(int Id_ficha) {
        Ficha rolEncontrado = null;
        Conexion conexion = new Conexion();

        Connection con = conexion.getConexion();
        if (con == null) {
            System.out.println("No se pudo obtener conexión. Abortando consulta.");
            return null;
        }

        String sql = "SELECT id_ficha, codigo_ficha, fecha_inicio, fecha_fin, cantidad_aprendices, Programas_idProgramas, Jornada_id_jornada, Modalidad_id_modalidad, Nivel_formacion_id_nivel_formacion, Sede_id_sede, Estado_id_estado, Etapa_id_etapa FROM ficha WHERE id_ficha = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Id_ficha);

            java.sql.ResultSet rs = ps.executeQuery(); 

            if (rs.next()) {
                rolEncontrado = new Ficha();
                rolEncontrado.setId_ficha(rs.getInt("Id_ficha"));
                rolEncontrado.setCodigo_ficha(rs.getInt("Codigo_ficha"));
                rolEncontrado.setFecha_inicio(rs.getObject("Fecha_inicio", LocalDate.class));
                rolEncontrado.setFecha_fin(rs.getObject("Fecha_fin", LocalDate.class));
                rolEncontrado.setCantidad_aprendices(rs.getInt("Cantidad_aprendices"));
                rolEncontrado.setProgramas_idProgramas(rs.getInt("Programas_idProgramas"));
                rolEncontrado.setJornada_id_jornada(rs.getInt("Jornada_id_jornada"));
                rolEncontrado.setModalidad_id_modalidad(rs.getInt("Modalidad_id_modalidad"));
                rolEncontrado.setNivel_formacion_id_nivel_formacion(rs.getInt("Nivel_formacion_id_nivel_formacion"));
                rolEncontrado.setSede_id_sede(rs.getInt("Sede_id_sede"));
                rolEncontrado.setEstado_id_estado(rs.getInt("Estado_id_estado"));
                rolEncontrado.setEtapa_id_etapa(rs.getInt("Etapa_id_etapa"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar la ficha: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return rolEncontrado;
    }
    
    public boolean eliminarFicha(int Id_ficha) {
        
        String sql = "DELETE FROM ficha WHERE id_ficha = ?";
        Conexion conexion = new Conexion();
    
        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setInt(1, Id_ficha);
            return ps.executeUpdate() > 0;
        
         } catch (SQLException e) {
             System.out.println("Error al eliminar ficha: " + e.getMessage());
             return false;
         }
     }
     
    public boolean actualizarFicha(Ficha ficha) {
    
    String sql = "UPDATE ficha SET codigo_ficha = ?, fecha_inicio = ?, fecha_fin = ?, cantidad_aprendices = ?, Programas_idProgramas = ?, Jornada_id_jornada = ?, Modalidad_id_modalidad = ?, Nivel_formacion_id_nivel_formacion = ?, Sede_id_sede = ?, Estado_id_estado = ?, Etapa_id_etapa = ? WHERE id_ficha = ?";
    
    Conexion conexion = new Conexion();
    
    try (Connection con = conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, ficha.getCodigo_ficha()); 
        ps.setObject(2, ficha.getFecha_inicio());
        ps.setObject(3, ficha.getFecha_fin());
        ps.setInt(4, ficha.getCantidad_aprendices());
        ps.setInt(5, ficha.getProgramas_idProgramas());
        ps.setInt(6, ficha.getJornada_id_jornada());
        ps.setInt(7, ficha.getModalidad_id_modalidad());
        ps.setInt(8, ficha.getNivel_formacion_id_nivel_formacion());
        ps.setInt(9, ficha.getSede_id_sede());
        ps.setInt(10, ficha.getEstado_id_estado());
        ps.setInt(11, ficha.getEtapa_id_etapa());
        ps.setInt(12, ficha.getId_ficha());               
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            System.out.println("Ficha actualizada correctamente.");
            return true;
        } else {
            return false;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar ficha: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
   }
    
}
