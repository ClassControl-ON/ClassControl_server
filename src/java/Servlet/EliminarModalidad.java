package Servlet;

import Controlador.ModalidadDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarModalidad")
public class EliminarModalidad extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "modalidad"; }

    @Override
    protected boolean eliminar(int id) {
        return new ModalidadDAO().eliminarModalidad(id);
    }
}
