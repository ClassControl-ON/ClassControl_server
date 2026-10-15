package Servlet;

import Controlador.Tipo_vinculacionDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarTipoVinculacion")
public class EliminarTipoVinculacion extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "tipoVinculacion"; }

    @Override
    protected boolean eliminar(int id) {
        return new Tipo_vinculacionDAO().eliminarTipo_vinculacion(id);
    }
}
