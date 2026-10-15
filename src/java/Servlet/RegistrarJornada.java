package Servlet;

import Controlador.JornadaDAO;
import Modelo.Jornada;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarJornada")
public class RegistrarJornada extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "jornada"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Jornada jornada = new Jornada();
        jornada.setId_jornada(0);
        jornada.setDescripcion_Jornada(texto(request, "descripcion_Jornada"));
        return new JornadaDAO().insertarJornada(jornada);
    }
}
