package Servlet;

import Controlador.EtapaDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarEtapa")
public class EliminarEtapa extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "etapa"; }

    @Override
    protected boolean eliminar(int id) {
        return new EtapaDAO().eliminarEtapa(id);
    }
}
