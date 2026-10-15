package Servlet;

import Controlador.RolesDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarRol")
public class EliminarRol extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "rol"; }

    @Override
    protected boolean eliminar(int id) {
        return new RolesDAO().eliminarRoles(id);
    }
}
