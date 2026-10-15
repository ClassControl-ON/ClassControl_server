package Servlet;

import Controlador.Tipo_EstadoDAO;
import Modelo.Tipo_Estado;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarTipoEstado")
public class RegistrarTipoEstado extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "tipoEstado"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Tipo_Estado tipoEstado = new Tipo_Estado();
        tipoEstado.setId_tipo_estado(0);
        tipoEstado.setDescripcion(texto(request, "descripcion"));
        return new Tipo_EstadoDAO().insertarTipo_Estado(tipoEstado);
    }
}
