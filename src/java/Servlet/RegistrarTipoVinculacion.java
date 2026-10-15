package Servlet;

import Controlador.Tipo_vinculacionDAO;
import Modelo.Tipo_vinculacion;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarTipoVinculacion")
public class RegistrarTipoVinculacion extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "tipoVinculacion"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Tipo_vinculacion tipoVinculacion = new Tipo_vinculacion();
        tipoVinculacion.setId_tipo_vinculacion(0);
        tipoVinculacion.setDescripcion_vinculacion(texto(request, "descripcion_vinculacion"));
        return new Tipo_vinculacionDAO().insertarTipo_Vinculacion(tipoVinculacion);
    }
}
