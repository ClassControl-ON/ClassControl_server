package Servlet;

import Controlador.Tipo_vinculacionDAO;
import Modelo.Tipo_vinculacion;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarTipoVinculacion")
public class ActualizarTipoVinculacion extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "tipoVinculacion"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Tipo_vinculacion tipoVinculacion = new Tipo_vinculacion();
        tipoVinculacion.setId_tipo_vinculacion(entero(request, "id"));
        tipoVinculacion.setDescripcion_vinculacion(texto(request, "descripcion_vinculacion"));
        return new Tipo_vinculacionDAO().actualizarTipo_vinculacion(tipoVinculacion);
    }
}
