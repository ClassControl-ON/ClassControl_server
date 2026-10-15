package Servlet;

import Controlador.Programacion_InstructoresDAO;
import Modelo.ProgramacionInstructoresDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Conexion.Conexion;

/**
 * Servlet principal de la pantalla de Programación de Instructores.
 *
 * URL de acceso: /ProgramacionInstructoresServlet
 *
 * Carga desde la base de datos:
 *   - programaciones  → List<ProgramacionInstructoresDTO>  (vista principal)
 *   - instructores    → List<Object[]> {id, nombre}
 *   - fichas          → List<Object[]> {id, codigo_ficha, nombre_programa}
 *   - ambientes       → List<Object[]> {id, descripcion_Ambiente}
 *   - trimestres      → List<Object[]> {id, etiqueta}
 *   - competencias    → List<Object[]> {id, descripcion_Competencias}
 *   - estados         → List<Object[]> {id, descripcion_Estado}
 *
 * y hace forward a Programacion_instructores.jsp
 */
@WebServlet("/ProgramacionInstructoresServlet")
public class ProgramacionInstructoresServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!Autorizacion.puedeVerProgramacion(req, resp)) {
            return;
        }

        Programacion_InstructoresDAO dao = new Programacion_InstructoresDAO();

        // ── 1. Lista principal con JOINs ────────────────────────
        List<ProgramacionInstructoresDTO> programaciones = dao.listarProgramaciones();
        req.setAttribute("programaciones", programaciones);

        // ── 2. Datos para los selects del modal ─────────────────
        try (Connection con = new Conexion().getConexion()) {

            // Instructores: usuarios cuyo rol sea instructor
            // Ajusta la condición de rol según tu tabla Roles
            req.setAttribute("instructores",
                queryList(con,
                    "SELECT u.id_usuarios, CONCAT(u.nombres, ' ', u.apellidos) " +
                    "FROM Usuarios u " +
                    "INNER JOIN Roles r ON r.id_roles = u.Roles_id_roles " +
                    "WHERE LOWER(r.descripcion_Roles) LIKE '%instructor%' " +
                    "ORDER BY u.nombres"));

            // Fichas
            req.setAttribute("fichas",
                queryList(con,
                    "SELECT f.id_ficha, f.codigo_ficha, COALESCE(p.nombre_programa,'') " +
                    "FROM Ficha f " +
                    "LEFT JOIN Programas p ON p.idProgramas = f.Programas_idProgramas " +
                    "ORDER BY f.codigo_ficha"));

            // Ambientes
            req.setAttribute("ambientes",
                queryList(con,
                    "SELECT id_ambientes, descripcion_Ambiente " +
                    "FROM Ambientes ORDER BY descripcion_Ambiente"));

            // Trimestres — COALESCE protege contra campos NULL
            req.setAttribute("trimestres",
                queryList(con,
                    "SELECT id_trimestre, " +
                    "       CONCAT('Trimestre ', COALESCE(num_trimestre, id_trimestre), " +
                    "              ' — ', COALESCE(descripcion, 'Sin descripción')) " +
                    "FROM Trimestre ORDER BY COALESCE(num_trimestre, id_trimestre)"));

            // Actividades: son la FK directa de Programacion_Instructores.
            req.setAttribute("actividades",
                queryList(con,
                    "SELECT id_actividades, nombre_Act " +
                    "FROM Actividades ORDER BY nombre_Act"));

            // Estados (filtrado por tipo 'programacion' si tienes Tipo_Estado)
            req.setAttribute("estados",
                queryList(con,
                    "SELECT id_estado, descripcion_Estado " +
                    "FROM Estado ORDER BY descripcion_Estado"));

        } catch (SQLException e) {
            System.out.println("❌ Error cargando datos para el modal: " + e.getMessage());
            e.printStackTrace();
        }

        // ── 3. Forward al JSP ───────────────────────────────────
        req.getRequestDispatcher("/Programacion_instructores.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    // ── Helper: ejecuta un SELECT de 2 o 3 columnas y devuelve List<Object[]>
    private List<Object[]> queryList(Connection con, String sql) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int cols = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 0; i < cols; i++) row[i] = rs.getObject(i + 1);
                list.add(row);
            }
        }
        return list;
    }
}
