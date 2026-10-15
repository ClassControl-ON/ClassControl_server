package Servlet;

import Controlador.EstadoDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarEstado")
public class EliminarEstado extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "estado"; }

    @Override
    protected boolean eliminar(int id) {
        return new EstadoDAO().eliminarEstado(id);
    }
}
