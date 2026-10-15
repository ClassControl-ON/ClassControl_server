package Servlet;

import Controlador.AmbientesDAO;
import Modelo.Ambientes;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarAmbiente")
public class ActualizarAmbiente extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "ambiente"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Ambientes ambiente = new Ambientes();
        ambiente.setId_ambientes(entero(request, "id"));
        ambiente.setDescripcion_Ambiente(texto(request, "descripcion_Ambiente"));
        ambiente.setCapacidad(entero(request, "capacidad"));
        ambiente.setSede_id_sede(entero(request, "Sede_id_sede"));
        ambiente.setEstado_Ambiente(texto(request, "estado_Ambiente"));
        return new AmbientesDAO().actualizarAmbientes(ambiente);
    }
}
