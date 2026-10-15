package Servlet;

import Controlador.TrimestreDAO;
import Modelo.Trimestre;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarTrimestre")
public class ActualizarTrimestre extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "trimestre"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Trimestre trimestre = new Trimestre();
        trimestre.setId_trimestre(entero(request, "id"));
        trimestre.setNum_trimestre(entero(request, "num_trimestre"));
        trimestre.setDescripcion(texto(request, "descripcion"));
        trimestre.setFecha_inicio(fecha(request, "fecha_inicio"));
        trimestre.setFecha_fin(fecha(request, "fecha_fin"));
        return new TrimestreDAO().actualizarTrimestre(trimestre);
    }
}
