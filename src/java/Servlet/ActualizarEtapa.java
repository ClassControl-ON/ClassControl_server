package Servlet;

import Controlador.EtapaDAO;
import Modelo.Etapa;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarEtapa")
public class ActualizarEtapa extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "etapa"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Etapa etapa = new Etapa();
        etapa.setId_etapa(entero(request, "id"));
        etapa.setDescripcion_Etapa(texto(request, "descripcion_Etapa"));
        return new EtapaDAO().actualizarEtapa(etapa);
    }
}
