package Servlet;

import Controlador.TrimestreDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarTrimestre")
public class EliminarTrimestre extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "trimestre"; }

    @Override
    protected boolean eliminar(int id) {
        return new TrimestreDAO().eliminarTrimestre(id);
    }
}
