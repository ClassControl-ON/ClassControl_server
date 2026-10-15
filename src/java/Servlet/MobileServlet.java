package Servlet;

import Conexion.Conexion;
import Controlador.ActividadesDAO;
import Controlador.AmbientesDAO;
import Controlador.CompetenciasDAO;
import Controlador.EstadoDAO;
import Controlador.EtapaDAO;
import Controlador.FichaDAO;
import Controlador.JornadaDAO;
import Controlador.ModalidadDAO;
import Controlador.Nivel_formacionDAO;
import Controlador.PermisosDAO;
import Controlador.Programacion_InstructoresDAO;
import Controlador.ProgramasDAO;
import Controlador.ReportesDAO;
import Controlador.Resultado_aprendizajeDAO;
import Controlador.RolesDAO;
import Controlador.SedeDAO;
import Controlador.Tipo_DocumentoDAO;
import Controlador.Tipo_EstadoDAO;
import Controlador.Tipo_vinculacionDAO;
import Controlador.TrimestreDAO;
import Controlador.UsuariosDAO;
import Controlador.VinculacionLaboralDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MobileServlet — fachada pública de lectura (GET) del sistema.
 *
 * Punto único de consumo para la APK (Flutter) y para quien quiera explorar
 * la base de datos desde el navegador. En vez de golpear decenas de
 * servlets independientes, todos los catálogos se consultan aquí con un
 * mismo formato JSON.
 *
 * Uso (solo GET):
 *   /api/mobile                  → índice auto-documentado de acciones
 *   /api/mobile?action=usuarios  → listado de usuarios (sin clave)
 *   /api/mobile?action=usuarios&ficha=12&rol=2&q=luna   → con filtros
 *   /api/mobile?action=estado    → salud del servicio y conteos por tabla
 *
 * Medidas listas para sobrecarga:
 *   - Los catálogos estáticos se cachean en memoria 20 s (menos golpes a BD).
 *   - Contadores en memoria por acción para monitorear la carga real.
 *   - Límite de registros por respuesta (limit, máx. 2000).
 *   - Toda salida es PreparedStatement con «?»; los campos sensibles de
 *     usuario (clave/hash) jamás se serializan.
 */
@WebServlet(urlPatterns = {"/api/mobile", "/Mobile", "/MobileServlet"})
public class MobileServlet extends HttpServlet {

    /** Campos que NUNCA se exponen en el JSON público (seguridad). */
    private static final Set<String> CAMPOS_PROHIBIDOS = Set.of(
            "clave", "password", "contrasena", "token", "secret", "hash",
            "salt", "pin");

    private static final int LIMITE_DEFECTO = 500;
    private static final int LIMITE_MAXIMO = 2000;
    private static final long TTL_CACHE_MS = 20_000L;

    private final java.util.concurrent.ConcurrentMap<String, AtomicLong> golpes =
            new ConcurrentHashMap<>();
    private final Map<String, Cache> cache =
            Collections.synchronizedMap(new java.util.LinkedHashMap<>());
    private final long inicio = System.currentTimeMillis();

    private static final class Cache {
        final long expira;
        final String json;
        Cache(long expira, String json) { this.expira = expira; this.json = json; }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");

        String accion = request.getParameter("action");
        if (accion == null || accion.isBlank()) {
            accion = "index";
        }
        accion = accion.trim().toLowerCase();
        registrar(accion);

        String json;
        try {
            json = resolver(accion, request);
        } catch (Exception e) {
            System.out.println("MobileServlet - error en action=" + accion + ": " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json = "{\"error\":\"No se pudo procesar la accion " + json(accion) + "\"}";
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(json);
        }
    }

    private void registrar(String accion) {
        golpes.computeIfAbsent(accion, k -> new AtomicLong()).incrementAndGet();
    }

    private long golpes(String accion) {
        AtomicLong c = golpes.get(accion);
        return c == null ? 0 : c.get();
    }

    // ════════════════════════ DISPATCHER ════════════════════════

    private String resolver(String accion, HttpServletRequest request) throws Exception {
        switch (accion) {
            case "index":        return indexJson();
            case "estado":       return estadoJson();
            case "usuarios":     return conFiltros(request, () -> new UsuariosDAO()
                    .listarUsuarios(Autorizacion.intParam(request, "ficha"),
                            Autorizacion.intParam(request, "rol"),
                            Autorizacion.textoSeguro(request.getParameter("q"), 80)), accion);
            case "ambientes":    return catalogo(request, () -> new AmbientesDAO().listarAmbientes(), accion);
            case "sedes":        return catalogo(request, () -> new SedeDAO().listarSedes(), accion);
            case "fichas":       return catalogo(request, () -> new FichaDAO().listarFichas(), accion);
            case "programas":    return catalogo(request, () -> new ProgramasDAO().listarProgramas(), accion);
            case "competencias": return catalogo(request, () -> new CompetenciasDAO().listarCompetencias(), accion);
            case "actividades":  return catalogo(request, () -> new ActividadesDAO().listarActividades(), accion);
            case "jornadas":     return catalogo(request, () -> new JornadaDAO().listarJornadas(), accion);
            case "modalidades":  return catalogo(request, () -> new ModalidadDAO().listarModalidades(), accion);
            case "estados":      return catalogo(request, () -> new EstadoDAO().listarEstados(), accion);
            case "tiposestado":  return catalogo(request, () -> new Tipo_EstadoDAO().listarTiposEstado(), accion);
            case "tiposdocumento": return catalogo(request, () -> new Tipo_DocumentoDAO().listarTiposDocumento(), accion);
            case "tiposvinculacion": return catalogo(request, () -> new Tipo_vinculacionDAO().listarTiposVinculacion(), accion);
            case "niveles":      return catalogo(request, () -> new Nivel_formacionDAO().listarNiveles(), accion);
            case "resultados":   return catalogo(request, () -> new Resultado_aprendizajeDAO().listarResultados(), accion);
            case "trimestres":   return catalogo(request, () -> new TrimestreDAO().listarTrimestres(), accion);
            case "roles":        return catalogo(request, () -> new RolesDAO().listarRoles(), accion);
            case "etapas":       return catalogo(request, () -> new EtapaDAO().listarEtapas(), accion);
            case "vinculacioneslaborales": return catalogo(request, () -> new VinculacionLaboralDAO().listarVinculacionesLaborales(), accion);
            case "permisos":     return catalogo(request, () -> new PermisosDAO().listarPermisos(), accion);
            case "programaciones": return catalogo(request, () -> new Programacion_InstructoresDAO().listarProgramaciones(), accion);
            case "reportes":     return conFiltros(request, () -> new ReportesDAO()
                    .listar(Autorizacion.intParam(request, "usuario")), accion);
            default:
                return errorDesconocida(accion);
        }
    }

    // ════════════════════════ SALIDAS ════════════════════════

    private String indexJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"servicio\":\"ClassControl Mobile API\",")
          .append("\"version\":\"1.0\",")
          .append("\"metodo\":\"GET\",")
          .append("\"formato\":\"application/json\",")
          .append("\"generado\":\"").append(Instant.now()).append("\",")
          .append("\"acciones\":{");
        String[] acciones = {
            "index", "estado", "usuarios", "ambientes", "sedes", "fichas",
            "programas", "competencias", "actividades", "jornadas",
            "modalidades", "estados", "tiposestado", "tiposdocumento",
            "tiposvinculacion", "niveles", "resultados", "trimestres",
            "roles", "etapas", "vinculacioneslaborales", "permisos",
            "programaciones", "reportes"
        };
        for (int i = 0; i < acciones.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(nombreJson(acciones[i])).append(":").append(json(acciones[i]));
        }
        sb.append("}}");
        return sb.toString();
    }

    private String errorDesconocida(String accion) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"error\":\"Accion desconocida: ").append(json(accion)).append("\",")
          .append("\"sugerencia\":\"/api/mobile (sin action) muestra el indice de acciones\"}");
        return sb.toString();
    }

    /** Para catálogos estáticos: consulta con caché en memoria de 20 s. */
    private String catalogo(HttpServletRequest request, ListaProveedor supplier, String accion)
            throws Exception {
        if (request.getParameter("limit") == null) {
            Cache c = cache.get(accion);
            if (c != null && c.expira > System.currentTimeMillis()) {
                return c.json;
            }
        }
        String json = conFiltros(request, supplier, accion);
        if (request.getParameter("limit") == null) {
            cache.put(accion, new Cache(System.currentTimeMillis() + TTL_CACHE_MS, json));
        }
        return json;
    }

    private String conFiltros(HttpServletRequest request, ListaProveedor supplier, String accion)
            throws Exception {
        List<?> lista = supplier.obtener();
        int limite = limitDe(request);
        if (lista.size() > limite) {
            lista = new ArrayList<>(lista.subList(0, limite));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\"action\":").append(json(accion)).append(",")
          .append("\"total\":").append(lista.size()).append(",")
          .append("\"registros\":[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(aJson(lista.get(i)));
        }
        sb.append("]}");
        return sb.toString();
    }

    private int limitDe(HttpServletRequest request) {
        Integer l = Autorizacion.intParam(request, "limit");
        if (l == null || l < 1) return LIMITE_DEFECTO;
        return Math.min(l, LIMITE_MAXIMO);
    }

    /** Estado del servicio: conexión, conteos por tabla y carga real. */
    private String estadoJson() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"servicio\":\"ClassControl Mobile API\",")
          .append("\"estado\":\"ok\",")
          .append("\"conexion\":").append(probandoConexion() ? "\"ok\"" : "\"error\"").append(",")
          .append("\"uptimeSegundos\":").append((System.currentTimeMillis() - inicio) / 1000).append(",")
          .append("\"generado\":\"").append(Instant.now()).append("\",")
          .append("\"golpesTotales\":").append(golpesTotal()).append(",")
          .append("\"golpesPorAccion\":{");

        List<String> acciones = new ArrayList<>(golpes.keySet());
        Collections.sort(acciones);
        for (int i = 0; i < acciones.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(nombreJson(acciones.get(i))).append(":").append(golpes(acciones.get(i)));
        }
        sb.append("},");

        sb.append("\"conteos\":{").append("usuarios:")
          .append(new UsuariosDAO().listarUsuarios().size()).append(",")
          .append("ambientes:").append(new AmbientesDAO().listarAmbientes().size()).append(",")
          .append("sedes:").append(new SedeDAO().listarSedes().size()).append(",")
          .append("fichas:").append(new FichaDAO().listarFichas().size()).append(",")
          .append("programas:").append(new ProgramasDAO().listarProgramas().size()).append(",")
          .append("programaciones:").append(new Programacion_InstructoresDAO().listarProgramaciones().size())
          .append("}}");

        return sb.toString();
    }

    private boolean probandoConexion() {
        try (Connection con = new Conexion().getConexion()) {
            return con != null && con.isValid(3);
        } catch (Exception e) {
            return false;
        }
    }

    private long golpesTotal() {
        long t = 0;
        for (AtomicLong c : golpes.values()) t += c.get();
        return t;
    }

    // ════════════════════════ SERIALIZADOR GENÉRICO ════════════════════════

    /** Serializa beans (vía getters), Map, listas, números, fechas y textos. */
    private String aJson(Object o) {
        if (o == null) return "null";
        if (o instanceof Number || o instanceof Boolean) return o.toString();
        if (o instanceof String || o instanceof Enum) return json(o.toString());
        if (o instanceof java.time.temporal.TemporalAccessor
                || o instanceof java.util.Date) {
            return json(o.toString());
        }
        if (o instanceof Map) {
            Map<?, ?> m = (Map<?, ?>) o;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> e : m.entrySet()) {
                Object k = e.getKey();
                if (k == null) continue;
                if (first) first = false; else sb.append(",");
                sb.append(json(String.valueOf(k))).append(":").append(aJson(e.getValue()));
            }
            return sb.append("}").toString();
        }
        if (o instanceof Iterable) {
            return listaJson((Iterable<?>) o);
        }
        if (o.getClass().isArray()) {
            return listaJson(Arrays.asList((Object[]) o));
        }
        return beanJson(o);
    }

    private String listaJson(Iterable<?> it) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : it) {
            if (first) first = false; else sb.append(",");
            sb.append(aJson(item));
        }
        return sb.append("]").toString();
    }

    private String beanJson(Object o) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Method m : o.getClass().getMethods()) {
            if (m.getParameterCount() != 0
                    || m.getReturnType() == Void.TYPE
                    || m.isBridge() || m.isSynthetic()) {
                continue;
            }
            String nombre = m.getName();
            String campo;
            if (nombre.startsWith("get") && nombre.length() > 3) {
                campo = nombre.substring(3);
            } else if (nombre.startsWith("is") && nombre.length() > 2
                    && m.getReturnType() == boolean.class) {
                campo = nombre.substring(2);
            } else {
                continue;
            }
            if ("Class".equals(campo)) continue;
            if (campoProhibido(campo)) continue;
            try {
                Object valor = m.invoke(o);
                if (first) first = false; else sb.append(",");
                sb.append(json(campoJson(campo))).append(":").append(aJson(valor));
            } catch (Exception ignorado) {
                // un getter que falle no debe tumbar la lista completa
            }
        }
        return sb.append("}").toString();
    }

    private boolean campoProhibido(String campo) {
        String c = campo.toLowerCase();
        for (String p : CAMPOS_PROHIBIDOS) {
            if (c.contains(p)) return true;
        }
        return false;
    }

    /** Convierte "Id_usuarios" → "id_usuarios"; "Activo" → "activo". */
    private String campoJson(String campo) {
        if (campo.isEmpty()) return campo;
        return Character.toLowerCase(campo.charAt(0)) + campo.substring(1);
    }

    private String nombreJson(String s) {
        return json(s);
    }

    private String json(String valor) {
        if (valor == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : valor.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.append("\"").toString();
    }

    @FunctionalInterface
    private interface ListaProveedor {
        List<?> obtener() throws Exception;
    }
}