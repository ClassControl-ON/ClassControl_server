package Servlet;

import Controlador.Tipo_DocumentoDAO;
import Modelo.Tipo_Documento;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarTipoDocumento")
public class ActualizarTipoDocumento extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "tipoDocumento"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Tipo_Documento tipoDocumento = new Tipo_Documento();
        tipoDocumento.setId_tipo_Documento(entero(request, "id"));
        tipoDocumento.setDescripcion_Tipo_Doc(texto(request, "descripcion_Tipo_Doc"));
        return new Tipo_DocumentoDAO().actualizarTipo_Documento(tipoDocumento);
    }
}
