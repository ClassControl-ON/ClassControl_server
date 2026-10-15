package Servlet;

import Controlador.ModalidadDAO;
import Modelo.Modalidad;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarModalidad")
public class RegistrarModalidad extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "modalidad"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Modalidad modalidad = new Modalidad();
        modalidad.setId_modalidad(0);
        modalidad.setDescripcion_Modalidad(texto(request, "descripcion_Modalidad"));
        return new ModalidadDAO().insertarModalidad(modalidad);
    }
}
