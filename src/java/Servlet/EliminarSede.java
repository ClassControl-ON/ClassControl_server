package Servlet;

import Controlador.SedeDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarSede")
public class EliminarSede extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "sede"; }

    @Override
    protected boolean eliminar(int id) {
        return new SedeDAO().eliminarSede(id);
    }
}
