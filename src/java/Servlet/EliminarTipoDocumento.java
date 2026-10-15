package Servlet;

import Controlador.Tipo_DocumentoDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarTipoDocumento")
public class EliminarTipoDocumento extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "tipoDocumento"; }

    @Override
    protected boolean eliminar(int id) {
        return new Tipo_DocumentoDAO().eliminarTipo_Documento(id);
    }
}
