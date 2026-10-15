package Servlet;

import Controlador.Tipo_EstadoDAO;
import Modelo.Tipo_Estado;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarTipoEstado")
public class ActualizarTipoEstado extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "tipoEstado"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Tipo_Estado tipoEstado = new Tipo_Estado();
        tipoEstado.setId_tipo_estado(entero(request, "id"));
        tipoEstado.setDescripcion(texto(request, "descripcion"));
        return new Tipo_EstadoDAO().actualizarTipo_Estado(tipoEstado);
    }
}
