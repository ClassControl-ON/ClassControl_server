package Servlet;

import Conexion.Conexion;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase base para los servlets de eliminación (DELETE) de cada entidad.
 * Toma el parámetro "id" del request, valida sesión y delega la
 * eliminación en la subclase (que a su vez llama al DAO correspondiente).
 *
 * Antes de intentar borrar, consulta information_schema para detectar
 * registros hijos que bloqueen la eliminación (llaves foráneas con
 * regla RESTRICT/NO ACTION) y responde con un mensaje claro indicando
 * qué tablas están asociadas y en cuántas cantidades. Así el admin ve
 * POR QUÉ no se puede borrar en lugar del genérico "No se pudo".
 */
public abstract class EliminarBaseServlet extends HttpServlet {

    /** Tabla física que corresponde a cada getTipo() de las subclases. */
    private static final Map<String, String> TABLA_POR_TIPO = new HashMap<>();

    /** Etiquetas legibles para los nombres de tabla hijas. */
    private static final Map<String, String> ETIQUETA_TABLA = new HashMap<>();

    static {
        TABLA_POR_TIPO.put("actividad", "actividades");
        TABLA_POR_TIPO.put("ambiente", "ambientes");
        TABLA_POR_TIPO.put("competencia", "competencias");
        TABLA_POR_TIPO.put("estado", "estado");
        TABLA_POR_TIPO.put("etapa", "etapa");
        TABLA_POR_TIPO.put("ficha", "ficha");
        TABLA_POR_TIPO.put("jornada", "jornada");
        TABLA_POR_TIPO.put("modalidad", "modalidad");
        TABLA_POR_TIPO.put("nivel", "nivel_formacion");
        TABLA_POR_TIPO.put("programa", "programas");
        TABLA_POR_TIPO.put("programacion", "programacion_instructores");
        TABLA_POR_TIPO.put("resultado", "resultado_aprendizaje");
        TABLA_POR_TIPO.put("rol", "roles");
        TABLA_POR_TIPO.put("sede", "sede");
        TABLA_POR_TIPO.put("tipoDocumento", "tipo_documento");
        TABLA_POR_TIPO.put("tipoEstado", "tipo_estado");
        TABLA_POR_TIPO.put("tipoVinculacion", "tipo_vinculacion");
        TABLA_POR_TIPO.put("trimestre", "trimestre");
        TABLA_POR_TIPO.put("usuario", "usuarios");
        TABLA_POR_TIPO.put("vinculacionLaboral", "vinculacion_laboral");
        TABLA_POR_TIPO.put("reporte", "reportes");

        ETIQUETA_TABLA.put("usuarios", "usuarios");
        ETIQUETA_TABLA.put("ficha", "fichas");
        ETIQUETA_TABLA.put("programacion_instructores", "programaciones");
        ETIQUETA_TABLA.put("ambientes", "ambientes");
        ETIQUETA_TABLA.put("actividades", "actividades");
        ETIQUETA_TABLA.put("competencias", "competencias");
        ETIQUETA_TABLA.put("programas", "programas");
        ETIQUETA_TABLA.put("resultado_aprendizaje", "resultados de aprendizaje");
        ETIQUETA_TABLA.put("roles_has_permisos", "permisos del rol");
        ETIQUETA_TABLA.put("jornada", "jornadas");
        ETIQUETA_TABLA.put("modalidad", "modalidades");
        ETIQUETA_TABLA.put("nivel_formacion", "niveles de formación");
        ETIQUETA_TABLA.put("sede", "sedes");
        ETIQUETA_TABLA.put("estado", "estados");
        ETIQUETA_TABLA.put("etapa", "etapas");
        ETIQUETA_TABLA.put("trimestre", "trimestres");
        ETIQUETA_TABLA.put("tipo_documento", "tipos de documento");
        ETIQUETA_TABLA.put("tipo_estado", "tipos de estado");
        ETIQUETA_TABLA.put("tipo_vinculacion", "tipos de vinculación");
        ETIQUETA_TABLA.put("vinculacion_laboral", "vinculaciones laborales");
        ETIQUETA_TABLA.put("roles", "roles");
        ETIQUETA_TABLA.put("permisos", "permisos");
    }

    protected abstract String getTipo();

    /** Llama al método eliminarX(id) del DAO correspondiente. */
    protected abstract boolean eliminar(int id) throws Exception;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!Autorizacion.puedeEliminar(request, response, getTipo())) {
            return;
        }

        boolean api = aceptaJson(request);
        try {
            int id = Integer.parseInt(texto(request, "id"));

            // Chequeo previo de dependencias para dar un mensaje útil.
            String bloqueo = dependenciasBloqueantes(id);
            if (bloqueo != null) {
                responder(response, api, false, bloqueo,
                        HttpServletResponse.SC_CONFLICT);
                return;
            }

            boolean ok = eliminar(id);
            responder(response, api, ok, ok ? "" : "No se pudo eliminar el registro.",
                    ok ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
        } catch (NumberFormatException e) {
            System.out.println(getClass().getSimpleName() + " - error en parametros numericos: " + e.getMessage());
            responder(response, api, false, "Formato de datos invalido.", HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(getClass().getSimpleName() + " - error general: " + e.getMessage());
            e.printStackTrace();
            responder(response, api, false, "No se pudo eliminar el registro.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Devuelve null si el registro puede eliminarse sin romper llaves
     * foráneas; si tiene registros hijos asociados (con regla RESTRICT
     * o NO ACTION) devuelve un mensaje describiéndolos.
     */
    private String dependenciasBloqueantes(int id) {
        String tabla = TABLA_POR_TIPO.get(getTipo());
        if (tabla == null) {
            return null;
        }

        List<String> partes = new ArrayList<>();
        try (Connection con = new Conexion().getConexion()) {
            if (con == null) {
                return null;
            }

            PreparedStatement ps = con.prepareStatement(
                    "SELECT kcu.TABLE_NAME AS tabla_hija, kcu.COLUMN_NAME AS col_fk, rc.DELETE_RULE AS regla "
                    + "FROM information_schema.KEY_COLUMN_USAGE kcu "
                    + "LEFT JOIN information_schema.REFERENTIAL_CONSTRAINTS rc "
                    + "  ON rc.CONSTRAINT_SCHEMA = kcu.CONSTRAINT_SCHEMA "
                    + " AND rc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME "
                    + "WHERE kcu.TABLE_SCHEMA = DATABASE() "
                    + "AND kcu.REFERENCED_TABLE_NAME = ?");
            ps.setString(1, tabla);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String regla = rs.getString("regla");
                // CASCADE y SET NULL no impiden el borrado; solo las
                // reglas restrictivas bloquean.
                if (regla != null && !"RESTRICT".equals(regla)
                        && !"NO ACTION".equals(regla)) {
                    continue;
                }
                String hija = rs.getString("tabla_hija");
                String colFk = rs.getString("col_fk");
                if (hija == null || colFk == null
                        || hija.equalsIgnoreCase(tabla)) {
                    continue;
                }

                long n = -1;
                try (PreparedStatement contar = con.prepareStatement(
                        "SELECT COUNT(*) FROM `" + hija.replace("`", "")
                        + "` WHERE `" + colFk.replace("`", "") + "` = ?")) {
                    contar.setInt(1, id);
                    ResultSet crs = contar.executeQuery();
                    crs.next();
                    n = crs.getLong(1);
                } catch (SQLException ignorada) {
                    // Tabla ilegible por permisos/estructura: no bloqueamos por ella.
                }
                if (n > 0) {
                    String etiqueta = ETIQUETA_TABLA.getOrDefault(hija.toLowerCase(), hija);
                    partes.add(etiqueta + " (" + n + ")");
                }
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            // Si el chequeo falla dejamos seguir el flujo clásico.
            System.out.println(getClass().getSimpleName()
                    + " - no se pudo verificar dependencias: " + e.getMessage());
            return null;
        }

        if (partes.isEmpty()) {
            return null;
        }
        return "No se puede eliminar este registro porque está asociado a: "
                + String.join(", ", partes)
                + ". Elimina o reasigna primero esos registros.";
    }

    private boolean aceptaJson(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.toLowerCase(java.util.Locale.ROOT).contains("application/json");
    }

    private void responder(HttpServletResponse response, boolean api, boolean ok,
            String mensaje, int status) throws IOException {
        if (!api) {
            response.sendRedirect("Pagina_Principal.jsp?" + (ok ? "eliminado=" + getTipo() : "error=" + getTipo()));
            return;
        }
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(ok ? "{\"ok\":true}" : "{\"error\":\"" + escapar(mensaje) + "\"}");
        }
    }

    private String escapar(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    private String texto(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            throw new NumberFormatException("Campo requerido vacio: " + nombre);
        }
        return valor.trim();
    }
}
