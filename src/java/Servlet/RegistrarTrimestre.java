package Servlet;

import Controlador.TrimestreDAO;
import Modelo.Trimestre;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarTrimestre")
public class RegistrarTrimestre extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "trimestre"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Trimestre trimestre = new Trimestre();
        trimestre.setId_trimestre(0);
        trimestre.setNum_trimestre(entero(request, "num_trimestre"));
        trimestre.setDescripcion(texto(request, "descripcion"));
        trimestre.setFecha_inicio(fecha(request, "fecha_inicio"));
        trimestre.setFecha_fin(fecha(request, "fecha_fin"));
        return new TrimestreDAO().insertarTrimestre(trimestre);
    }
}
