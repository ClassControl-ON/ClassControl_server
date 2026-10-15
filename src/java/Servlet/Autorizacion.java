package Servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.Set;

/**
 * Punto único de autorización del servidor.
 *
 * Reglas por rol (ver /areas/classcontrol.md del proyecto para el detalle):
 *  - Administrador: acceso total, incluyendo eliminar.
 *  - Coordinador: puede crear y actualizar todo, pero NUNCA eliminar.
 *  - Instructor: solo puede crear/actualizar su propia programación
 *    (horario). El resto de catálogos son de solo lectura para él.
 *  - Aprendiz: solo lectura en todo, no gestiona nada.
 *
 * IMPORTANTE:
 * - La consulta de datos no debe bloquear la pantalla de Instructores.
 * - Los usuarios autenticados pueden consultar los catálogos/datos
 *   necesarios para visualizar la aplicación (el FILTRADO de qué fichas o
 *   programaciones ve cada quien se resuelve en el servlet/DAO, no aquí).
 * - Las operaciones de gestión (crear, actualizar) respetan la matriz de
 *   arriba. Eliminar queda reservado exclusivamente a Administrador.
 * - Toda variable que venga del cliente se debe normalizar con
 *   textoSeguro()/intParam() antes de usarla, y NUNCA se interpola en
 *   sentencias SQL (siempre PreparedStatement con «?»).
 */
public final class Autorizacion {

    private static final String ADMINISTRADOR = "administrador";
    private static final String COORDINADOR = "coordinador";
    private static final String INSTRUCTOR = "instructor";
    private static final String APRENDIZ = "aprendiz";

    /** Tipos que un Instructor puede crear/actualizar (su propio horario). */
    private static final Set<String> TIPOS_GESTIONABLES_POR_INSTRUCTOR =
            Set.of("programacion", "programaciones");

    /** Longitud máxima razonable para un texto que viene del cliente. */
    private static final int MAX_TEXTO = 200;

    private Autorizacion() {
    }

    /**
     * Autoriza operaciones de creación y actualización.
     * Administrador y Coordinador pueden gestionar cualquier tipo.
     * Instructor solo puede gestionar su propia programación/horario.
     * Aprendiz nunca gestiona nada.
     */
    public static boolean puedeGestionar(HttpServletRequest request,
            HttpServletResponse response, String tipo) throws IOException {

        String rol = rolDe(request);

        if (esAdministrador(rol) || esCoordinador(rol)) {
            return true;
        }

        if (esInstructor(rol) && tipo != null
                && TIPOS_GESTIONABLES_POR_INSTRUCTOR.contains(tipo.toLowerCase(java.util.Locale.ROOT))) {
            return true;
        }

        denegar(request, response);
        return false;
    }

    /**
     * Autoriza operaciones de eliminación. Reservado exclusivamente para
     * Administrador — ni Coordinador ni ningún otro rol puede eliminar.
     */
    public static boolean puedeEliminar(HttpServletRequest request,
            HttpServletResponse response, String tipo) throws IOException {

        String rol = rolDe(request);

        if (esAdministrador(rol)) {
            return true;
        }

        denegar(request, response);
        return false;
    }

    /**
     * Permite consultar información a cualquier usuario autenticado.
     *
     * Esto es necesario porque la interfaz de Instructores consulta
     * /ConsultarUsuarios y /ConsultarRoles para construir la lista y
     * los filtros. Antes estos endpoints devolvían 403 para un Instructor.
     *
     * IMPORTANTE: los Servlets de consulta deben seguir devolviendo
     * únicamente los campos que correspondan al cliente. Por ejemplo,
     * ConsultarUsuarios NO debe enviar la clave/hash de contraseña, y
     * el propio Servlet debe aplicar el filtrado por rol (p. ej. un
     * Instructor solo debe recibir las fichas de su propia programación).
     */
    public static boolean puedeConsultar(HttpServletRequest request,
            HttpServletResponse response, String tipo) throws IOException {

        String rol = rolDe(request);

        if (rol == null) {
            response.sendRedirect("Inicio_de_sesion.jsp");
            return false;
        }

        // Cualquier rol autenticado puede consultar.
        // Las restricciones de escritura siguen en puedeGestionar()/puedeEliminar().
        return true;
    }

    public static boolean puedeVerProgramacion(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        return puedeConsultar(request, response, "programacion");
    }

    /** Id del usuario autenticado en sesión, o null si no hay sesión. */
    public static Integer idUsuarioDe(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object id = session.getAttribute("id_usuario");
        return id instanceof Integer ? (Integer) id : null;
    }

    public static boolean esAdministrador(HttpServletRequest request) {
        return esAdministrador(rolDe(request));
    }

    public static boolean esCoordinador(HttpServletRequest request) {
        return esCoordinador(rolDe(request));
    }

    public static boolean esInstructor(HttpServletRequest request) {
        return esInstructor(rolDe(request));
    }

    public static boolean esAprendiz(HttpServletRequest request) {
        return esAprendiz(rolDe(request));
    }

    private static String rolDe(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("id_usuario") == null) {
            return null;
        }

        Object rol = session.getAttribute("rol_nombre");

        return rol instanceof String
                ? normalizar((String) rol)
                : null;
    }

    /**
     * El rol normalizado del usuario autenticado (o {@code null} si no hay
     * sesión). Visible para otros componentes del backend.
     */
    public static String rolDeSesion(HttpServletRequest request) {
        return rolDe(request);
    }

    /**
     * True si la petición parece venir de un cliente que espera JSON
     * (la app móvil, un fetch JS «API», o una URL que no termina en .jsp).
     * Se usa para devolver 403 como JSON en lugar de una página HTML.
     */
    private static boolean esPeticionJson(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri != null && uri.endsWith(".jsp")) {
            return false;
        }
        String acepta = request.getHeader("Accept");
        if (acepta != null && acepta.toLowerCase().contains("application/json")) {
            return true;
        }
        String ua = request.getHeader("User-Agent");
        return ua != null && ua.toLowerCase().contains("dart");
    }

    private static void denegar(HttpServletResponse response, boolean json) throws IOException {
        if (json) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"No tienes permiso para realizar esta accion.\"}");
            }
            return;
        }
        response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "No tienes permiso para realizar esta accion."
        );
    }

    private static void denegar(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        denegar(response, esPeticionJson(request));
    }

    /**
     * Lee un parámetro entero de forma segura: devuelve {@code null} si no
     * existe, no es numérico o se sale de rango. No lanza excepciones.
     */
    public static Integer intParam(HttpServletRequest request, String nombre) {
        String raw = request.getParameter(nombre);
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        if (raw.length() > 11 || !raw.trim().matches("-?\\d{1,10}")) {
            return null;
        }
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Normaliza un texto que viene del cliente: recorta, colapsa espacios,
     * limita longitud y elimina caracteres de control. Es la puerta de
     * entrada para impedir cadenas inyectables — combinada con el uso de
     * PreparedStatement en los DAO, bloquea la inyección SQL por las dos vías.
     */
    public static String textoSeguro(String raw, int maxLongitud) {
        if (raw == null) {
            return null;
        }
        String t = raw.replace('\u0000', ' ');
        t = t.trim().replaceAll("\\s{2,}", " ");
        if (t.length() > maxLongitud) {
            t = t.substring(0, maxLongitud);
        }
        return t;
    }

    public static String textoSeguro(String raw) {
        return textoSeguro(raw, MAX_TEXTO);
    }

    /** Compatibilidad con los nombres cortos que usa la app móvil. */
    private static boolean esAdministrador(String rol) {
        return ADMINISTRADOR.equals(rol) || "admin".equals(rol);
    }

    private static boolean esCoordinador(String rol) {
        return COORDINADOR.equals(rol) || "coord".equals(rol);
    }

    private static boolean esInstructor(String rol) {
        return INSTRUCTOR.equals(rol);
    }

    private static boolean esAprendiz(String rol) {
        return APRENDIZ.equals(rol) || "estudiante".equals(rol);
    }

    private static String normalizar(String valor) {
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase(java.util.Locale.ROOT);
    }
}
