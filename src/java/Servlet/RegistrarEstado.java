package Servlet;

import Controlador.EstadoDAO;
import Modelo.Estado;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarEstado")
public class RegistrarEstado extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "estado"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Estado estado = new Estado();
        estado.setId_estado(0);
        estado.setDescripcion_Estado(texto(request, "descripcion_Estado"));
        estado.setTipo_Estado_id_tipo_estado(entero(request, "Tipo_Estado_id_tipo_estado"));
        return new EstadoDAO().insertarEstado(estado);
    }
}
