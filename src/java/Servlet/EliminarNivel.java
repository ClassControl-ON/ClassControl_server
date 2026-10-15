package Servlet;

import Controlador.Nivel_formacionDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarNivel")
public class EliminarNivel extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "nivel"; }

    @Override
    protected boolean eliminar(int id) {
        return new Nivel_formacionDAO().eliminarNivel_formacion(id);
    }
}
