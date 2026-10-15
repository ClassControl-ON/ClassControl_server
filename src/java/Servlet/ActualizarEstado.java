package Servlet;

import Controlador.EstadoDAO;
import Modelo.Estado;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarEstado")
public class ActualizarEstado extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "estado"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Estado estado = new Estado();
        estado.setId_estado(entero(request, "id"));
        estado.setDescripcion_Estado(texto(request, "descripcion_Estado"));
        estado.setTipo_Estado_id_tipo_estado(entero(request, "Tipo_Estado_id_tipo_estado"));
        return new EstadoDAO().actualizarEstado(estado);
    }
}
