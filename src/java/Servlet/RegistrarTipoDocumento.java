package Servlet;

import Controlador.Tipo_DocumentoDAO;
import Modelo.Tipo_Documento;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarTipoDocumento")
public class RegistrarTipoDocumento extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "tipoDocumento"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Tipo_Documento tipoDocumento = new Tipo_Documento();
        tipoDocumento.setId_tipo_Documento(0);
        tipoDocumento.setDescripcion_Tipo_Doc(texto(request, "descripcion_Tipo_Doc"));
        return new Tipo_DocumentoDAO().insertarTipo_Documento(tipoDocumento);
    }
}
