package Servlet;

import Controlador.FichaDAO;
import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Guarda la ficha del aprendiz autenticado (una sola por aprendiz).
 *
 * La app movil lo llama desde Inicio cuando el aprendiz elige su ficha.
 * Solo el propio aprendiz puede asignarse su ficha (o un administrador
 * en nombre de otro, pasando idUsuario).
 */
@WebServlet("/AsignarFichaAprendiz")
public class AsignarFichaAprendiz extends HttpServlet {

    private static final int ROL_APRENDIZ = 2;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        Integer idSesion = Autorizacion.idUsuarioDe(request);
        if (idSesion == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"No hay una sesión activa.\"}");
            }
            return;
        }

        // Solo Administrador/Coordinador pueden indicar otro idUsuario.
        // Aprendiz e Instructor únicamente pueden modificar su propia ficha.
        int idUsuario = idSesion;
        String pedido = request.getParameter("idUsuario");
        boolean privilegiado = Autorizacion.esAdministrador(request)
                || Autorizacion.esCoordinador(request);

        if (pedido != null && !pedido.isBlank()) {
            if (!privilegiado) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                try (PrintWriter out = response.getWriter()) {
                    out.print("{\"error\":\"No puedes modificar la ficha de otro usuario.\"}");
                }
                return;
            }
            try {
                idUsuario = Integer.parseInt(pedido.trim());
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                try (PrintWriter out = response.getWriter()) {
                    out.print("{\"error\":\"idUsuario inválido.\"}");
                }
                return;
            }
        }

        Usuarios destino = new UsuariosDAO().consultaUsuarios(idUsuario);
        if (destino == null || destino.getRoles_id_roles() != ROL_APRENDIZ) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"El usuario destino debe ser un aprendiz.\"}");
            }
            return;
        }

        String fichaParam = request.getParameter("idFicha");
        if (fichaParam == null || fichaParam.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"Falta el parámetro idFicha.\"}");
            }
            return;
        }

        int idFicha;
        try {
            idFicha = Integer.parseInt(fichaParam.trim());
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"idFicha debe ser un número.\"}");
            }
            return;
        }

        boolean ok = new FichaDAO().asignarFichaAAprendiz(idUsuario, idFicha);
        if (ok) {
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"ok\":true}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"No se pudo guardar la ficha. Verifica que la ficha exista.\"}");
            }
        }
    }
}
