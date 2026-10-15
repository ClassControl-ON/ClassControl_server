package Servlet;

import Controlador.JornadaDAO;
import Modelo.Jornada;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarJornada")
public class ActualizarJornada extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "jornada"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Jornada jornada = new Jornada();
        jornada.setId_jornada(entero(request, "id"));
        jornada.setDescripcion_Jornada(texto(request, "descripcion_Jornada"));
        return new JornadaDAO().actualizarJornada(jornada);
    }
}
