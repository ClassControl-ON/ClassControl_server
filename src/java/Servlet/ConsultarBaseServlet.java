package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Clase base para los servlets de consulta (listar) de cada entidad.
 * A diferencia de Registrar/Actualizar/Eliminar, esta NO redirige a una
 * página: responde directamente con un arreglo JSON, para que cualquier
 * JSP/JS pueda pedirlo por fetch() y pintar tablas, calendarios o selects
 * sin necesidad de recargar la página.
 *
 * No se usa ninguna librería externa (Gson/Jackson) porque el proyecto
 * no la trae — el JSON se arma a mano con los helpers de escape de abajo,
 * igual de estricto que si viniera de una librería.
 */
public abstract class ConsultarBaseServlet<T> extends HttpServlet {

    protected abstract String getTipo();
    protected abstract List<T> obtenerLista() throws Exception;

    /** Permite que una consulta concreta lea filtros seguros del request. */
    protected List<T> obtenerLista(HttpServletRequest request) throws Exception {
        return obtenerLista();
    }

    /** Debe devolver el contenido JSON de UN registro, SIN llaves { }. */
    protected abstract String camposJson(T item);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!Autorizacion.puedeConsultar(request, response, getTipo())) {
            return;
        }

        response.setContentType("application/json;charset=UTF-8");

        try {
            List<T> lista = obtenerLista(request);
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < lista.size(); i++) {
                if (i > 0) json.append(",");
                json.append("{").append(camposJson(lista.get(i))).append("}");
            }
            json.append("]");

            try (PrintWriter out = response.getWriter()) {
                out.print(json.toString());
            }
        } catch (Exception e) {
            System.out.println(getClass().getSimpleName() + " - error al listar " + getTipo() + ": " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"No se pudo obtener el listado de " + getTipo() + "\"}");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /* ── Helpers de formato/escape JSON reutilizables por las subclases ── */

    protected String campoStr(String nombre, String valor) {
        return "\"" + nombre + "\":" + jsonStr(valor);
    }

    protected String campoNum(String nombre, Number valor) {
        return "\"" + nombre + "\":" + (valor == null ? "null" : valor.toString());
    }

    protected String campoBool(String nombre, boolean valor) {
        return "\"" + nombre + "\":" + valor;
    }

    protected String campoFecha(String nombre, Object fecha) {
        return "\"" + nombre + "\":" + (fecha == null ? "null" : "\"" + fecha + "\"");
    }

    private String jsonStr(String valor) {
        if (valor == null) return "null";
        String escapado = valor
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
        return "\"" + escapado + "\"";
    }
}
