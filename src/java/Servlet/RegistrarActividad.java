package Servlet;

import Controlador.ActividadesDAO;
import Modelo.Actividades;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarActividad")
public class RegistrarActividad extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "actividad"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Actividades actividad = new Actividades();
        actividad.setId_actividades(0);
        actividad.setCodigo_Actividad(entero(request, "codigo_Actividad"));
        actividad.setNombre_Act(texto(request, "nombre_Act"));
        actividad.setDescripcion(textoOpcional(request, "descripcion"));
        actividad.setResultado_aprendizaje_id_resultado_aprendizaje(
                entero(request, "Resultado_aprendizaje_id_resultado_aprendizaje"));
        return new ActividadesDAO().insertarActividades(actividad);
    }
}
