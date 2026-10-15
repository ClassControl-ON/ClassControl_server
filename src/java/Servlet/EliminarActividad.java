package Servlet;

import Controlador.ActividadesDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarActividad")
public class EliminarActividad extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "actividad"; }

    @Override
    protected boolean eliminar(int id) {
        return new ActividadesDAO().eliminarActividades(id);
    }
}
