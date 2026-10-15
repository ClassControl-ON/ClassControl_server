package Servlet;

import Controlador.VinculacionLaboralDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarVinculacionLaboral")
public class EliminarVinculacionLaboral extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "vinculacionLaboral"; }

    @Override
    protected boolean eliminar(int id) {
        return new VinculacionLaboralDAO().eliminarVinculacionLaboral(id);
    }
}
