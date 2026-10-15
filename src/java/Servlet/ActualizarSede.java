package Servlet;

import Controlador.SedeDAO;
import Modelo.Sede;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarSede")
public class ActualizarSede extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "sede"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Sede sede = new Sede();
        sede.setId_sede(entero(request, "id"));
        sede.setNombre_sede(texto(request, "nombre_sede"));
        return new SedeDAO().actualizarSede(sede);
    }
}
