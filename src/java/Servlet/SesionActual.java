package Servlet;

import Controlador.FichaDAO;
import Modelo.Ficha;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/SesionActual")
public class SesionActual extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);

        response.setContentType("application/json;charset=UTF-8");

        if (session == null || session.getAttribute("id_usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"No hay una sesión activa.\"}");
            }
            return;
        }

        Integer idUsuario = (Integer) session.getAttribute("id_usuario");

        // Si el usuario tiene ficha asignada (aprendiz), se la informamos
        // a la app para que muestre automaticamente su propio horario.
        Integer fichaId = null;
        Integer fichaCodigo = null;
        Ficha ficha = new FichaDAO().consultarFichaDeAprendiz(idUsuario);
        if (ficha != null) {
            fichaId = ficha.getId_ficha();
            fichaCodigo = ficha.getCodigo_ficha();
        }

        try (PrintWriter out = response.getWriter()) {
            out.print("{");
            out.print("\"idUsuario\":" + session.getAttribute("id_usuario") + ",");
            out.print("\"nombres\":" + jsonStr(session.getAttribute("nombres")) + ",");
            out.print("\"apellidos\":" + jsonStr(session.getAttribute("apellidos")) + ",");
            out.print("\"username\":" + jsonStr(session.getAttribute("username")) + ",");
            out.print("\"rolId\":" + session.getAttribute("rol") + ",");
            out.print("\"rolNombre\":" + jsonStr(session.getAttribute("rol_nombre")) + ",");
            out.print("\"fichaId\":" + (fichaId == null ? "null" : fichaId) + ",");
            out.print("\"fichaCodigo\":" + (fichaCodigo == null ? "null" : fichaCodigo));
            out.print("}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        doGet(request, response);
    }

    private String jsonStr(Object valor) {
        if (valor == null) return "null";
        String s = String.valueOf(valor)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");
        return "\"" + s + "\"";
    }
}
