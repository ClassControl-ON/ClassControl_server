package Servlet;

import Controlador.ModalidadDAO;
import Modelo.Modalidad;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarModalidad")
public class ActualizarModalidad extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "modalidad"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Modalidad modalidad = new Modalidad();
        modalidad.setId_modalidad(entero(request, "id"));
        modalidad.setDescripcion_Modalidad(texto(request, "descripcion_Modalidad"));
        return new ModalidadDAO().actualizarModalidad(modalidad);
    }
}
