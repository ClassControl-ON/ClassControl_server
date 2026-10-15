package Servlet;

import Controlador.Nivel_formacionDAO;
import Modelo.Nivel_formacion;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarNivel")
public class ActualizarNivel extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "nivel"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Nivel_formacion nivel = new Nivel_formacion();
        nivel.setId_nivel_formacion(entero(request, "id"));
        nivel.setDescripcion_Nivel_Formacion(texto(request, "descripcion_Nivel_Formacion"));
        return new Nivel_formacionDAO().actualizarNivel_formacion(nivel);
    }
}
