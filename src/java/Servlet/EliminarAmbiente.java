package Servlet;

import Controlador.AmbientesDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarAmbiente")
public class EliminarAmbiente extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "ambiente"; }

    @Override
    protected boolean eliminar(int id) {
        return new AmbientesDAO().eliminarAmbientes(id);
    }
}
