package Servlet;

import Controlador.EtapaDAO;
import Modelo.Etapa;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarEtapa")
public class RegistrarEtapa extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "etapa"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Etapa etapa = new Etapa();
        etapa.setId_etapa(0);
        etapa.setDescripcion_Etapa(texto(request, "descripcion_Etapa"));
        return new EtapaDAO().insertarEtapa(etapa);
    }
}
