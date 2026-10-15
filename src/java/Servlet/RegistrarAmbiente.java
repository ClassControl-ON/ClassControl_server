package Servlet;

import Controlador.AmbientesDAO;
import Modelo.Ambientes;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarAmbiente")
public class RegistrarAmbiente extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "ambiente"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Ambientes ambiente = new Ambientes();
        ambiente.setId_ambientes(0);
        ambiente.setDescripcion_Ambiente(texto(request, "descripcion_Ambiente"));
        ambiente.setCapacidad(entero(request, "capacidad"));
        ambiente.setSede_id_sede(entero(request, "Sede_id_sede"));
        ambiente.setEstado_Ambiente(texto(request, "estado_Ambiente"));
        return new AmbientesDAO().insertarAmbientes(ambiente);
    }
}
