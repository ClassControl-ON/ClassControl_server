package Servlet;

import Controlador.ActividadesDAO;
import Modelo.Actividades;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarActividad")
public class ActualizarActividad extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "actividad"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Actividades actividad = new Actividades();
        actividad.setId_actividades(entero(request, "id"));
        actividad.setCodigo_Actividad(entero(request, "codigo_Actividad"));
        actividad.setNombre_Act(texto(request, "nombre_Act"));
        actividad.setDescripcion(textoOpcional(request, "descripcion"));
        actividad.setResultado_aprendizaje_id_resultado_aprendizaje(
                entero(request, "Resultado_aprendizaje_id_resultado_aprendizaje"));
        return new ActividadesDAO().actualizarActividades(actividad);
    }
}
