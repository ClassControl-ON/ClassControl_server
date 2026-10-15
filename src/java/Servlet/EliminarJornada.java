package Servlet;

import Controlador.JornadaDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarJornada")
public class EliminarJornada extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "jornada"; }

    @Override
    protected boolean eliminar(int id) {
        return new JornadaDAO().eliminarJornada(id);
    }
}
