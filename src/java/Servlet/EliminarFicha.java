package Servlet;

import Controlador.FichaDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarFicha")
public class EliminarFicha extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "ficha"; }

    @Override
    protected boolean eliminar(int id) {
        return new FichaDAO().eliminarFicha(id);
    }
}
