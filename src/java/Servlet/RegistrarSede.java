package Servlet;

import Controlador.SedeDAO;
import Modelo.Sede;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarSede")
public class RegistrarSede extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "sede"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Sede sede = new Sede();
        sede.setId_sede(0);
        sede.setNombre_sede(texto(request, "nombre_sede"));
        return new SedeDAO().insertarSede(sede);
    }
}
