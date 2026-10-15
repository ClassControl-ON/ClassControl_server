package Controlador;

import Conexion.Conexion;
import Modelo.Roles_has_Permisos;
import Modelo.Permisos; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Roles_has_PermisosDAO {

    // Asignar un permiso a un rol
    public boolean asignarPermiso(Roles_has_Permisos rhp) {
        String sql = "INSERT INTO Roles_has_Permisos (Roles_id_roles, Permisos_id_permisos) VALUES (?, ?)";
        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, rhp.getRoles_id_roles());
            ps.setInt(2, rhp.getPermisos_id_permisos());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al asignar permiso: " + e.getMessage());
            return false;
        }
    }

    public boolean quitarPermiso(Roles_has_Permisos rhp) {
        String sql = "DELETE FROM Roles_has_Permisos WHERE Roles_id_roles = ? AND Permisos_id_permisos = ?";
        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, rhp.getRoles_id_roles());
            ps.setInt(2, rhp.getPermisos_id_permisos());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al quitar permiso: " + e.getMessage());
            return false;
        }
    }

    public List<Permisos> obtenerPermisosPorRol(int idRol) {
        List<Permisos> lista = new ArrayList<>();
        String sql = """
            SELECT p.id_permisos, p.descripcion_permisos
            FROM permisos p
            INNER JOIN Roles_has_Permisos rp ON p.id_permisos = rp.Permisos_id_permisos
            WHERE rp.Roles_id_roles = ?
        """;
        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRol);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Permisos p = new Permisos();
                p.setId_permisos(rs.getInt("id_permisos"));
                p.setDescripcion_permisos(rs.getString("descripcion_permisos"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener permisos: " + e.getMessage());
        }

        return lista;
    }

    public boolean existeRelacion(int idRol, int idPermiso) {
        String sql = "SELECT COUNT(*) FROM Roles_has_Permisos WHERE Roles_id_roles = ? AND Permisos_id_permisos = ?";
        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRol);
            ps.setInt(2, idPermiso);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al verificar relación: " + e.getMessage());
        }

        return false;
    }
}