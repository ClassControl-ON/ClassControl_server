package Controlador;

import Conexion.Conexion;
import Modelo.Permisos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PermisosDAO {

    public boolean insertarPermiso(Permisos permiso) {
        String sql = "INSERT INTO permisos (id_permisos, descripcion_permisos) VALUES (?, ?)";
        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, permiso.getId_permisos());
            ps.setString(2, permiso.getDescripcion_permisos()); 
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar permiso: " + e.getMessage());
            return false;
        }
    }

    public Permisos consultarPermiso(int idPermiso) {
        String sql = "SELECT id_permisos, descripcion_permisos FROM permisos WHERE id_permisos = ?";
        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPermiso);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Permisos p = new Permisos();
                p.setId_permisos(rs.getInt("id_permisos"));
                p.setDescripcion_permisos(rs.getString("descripcion_permisos")); 
                return p;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar permiso: " + e.getMessage());
        }

        return null;
    }

    public List<Permisos> listarPermisos() {
        List<Permisos> lista = new ArrayList<>();
        String sql = "SELECT id_permisos, descripcion_permisos FROM permisos";
        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Permisos p = new Permisos();
                p.setId_permisos(rs.getInt("id_permisos"));
                p.setDescripcion_permisos(rs.getString("descripcion_permisos"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al listar permisos: " + e.getMessage());
        }

        return lista;
    }

    public boolean actualizarPermiso(Permisos permiso) {
        String sql = "UPDATE permisos SET descripcion_permisos = ? WHERE id_permisos = ?";
        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, permiso.getDescripcion_permisos());
            ps.setInt(2, permiso.getId_permisos());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar permiso: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPermiso(int idPermiso) {
        String sql = "DELETE FROM permisos WHERE id_permisos = ?";
        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPermiso);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar permiso: " + e.getMessage());
            return false;
        }
    }
}