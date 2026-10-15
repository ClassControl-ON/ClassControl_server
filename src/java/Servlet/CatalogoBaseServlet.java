package Servlet;

import Conexion.Conexion;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Clase base para todos los Servlets de catálogo.
 * Contiene la protección de sesión, la ejecución SQL
 * y los helpers de parseo de parámetros.
 */
public abstract class CatalogoBaseServlet extends HttpServlet {

    /* ── Nombre del tipo (ej: "sede", "programa") usado en el redirect ── */
    protected abstract String getTipo();

    /* ── Lógica específica de cada Servlet hijo ── */
    protected abstract boolean guardar(Connection con, HttpServletRequest request)
            throws SQLException;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!Autorizacion.puedeGestionar(request, response, getTipo())) {
            return;
        }

        Conexion conexion = new Conexion();
        try (Connection con = conexion.getConexion()) {
            boolean ok = guardar(con, request);
            String tipo = getTipo();
            response.sendRedirect("Pagina_Principal.jsp?" + (ok ? "ok=" + tipo : "error=" + tipo));
        } catch (NumberFormatException e) {
            System.out.println("❌ " + getClass().getSimpleName() + " — error de formato: " + e.getMessage());
            response.sendRedirect("Pagina_Principal.jsp?error=formato");
        } catch (Exception e) {
            System.out.println("❌ " + getClass().getSimpleName() + " — error general: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("Pagina_Principal.jsp?error=1");
        }
    }

    /* ────────────────────────────────────────────
       Helpers compartidos por todos los hijos
    ──────────────────────────────────────────── */

    protected boolean ejecutar(Connection con, String sql, Object... valores) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) {
                ps.setObject(i + 1, valores[i]);
            }
            return ps.executeUpdate() > 0;
        }
    }

    protected String texto(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            throw new NumberFormatException("Campo requerido vacío: " + nombre);
        }
        return valor.trim();
    }

    protected String textoOpcional(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }

    protected int entero(HttpServletRequest request, String nombre) {
        return Integer.parseInt(texto(request, nombre));
    }

    protected LocalDate fecha(HttpServletRequest request, String nombre) {
        return LocalDate.parse(texto(request, nombre));
    }

    protected LocalTime hora(HttpServletRequest request, String nombre) {
        return LocalTime.parse(texto(request, nombre));
    }
}
