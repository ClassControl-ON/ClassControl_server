package Servlet;

import Controlador.Tipo_EstadoDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarTipoEstado")
public class EliminarTipoEstado extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "tipoEstado"; }

    @Override
    protected boolean eliminar(int id) {
        return new Tipo_EstadoDAO().eliminarTipo_Estado(id);
    }
}
