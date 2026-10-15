package Servlet;

import Controlador.Nivel_formacionDAO;
import Modelo.Nivel_formacion;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarNivel")
public class RegistrarNivel extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "nivel"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Nivel_formacion nivel = new Nivel_formacion();
        nivel.setId_nivel_formacion(0);
        nivel.setDescripcion_Nivel_Formacion(texto(request, "descripcion_Nivel_Formacion"));
        return new Nivel_formacionDAO().insertarNivel_formacion(nivel);
    }
}
