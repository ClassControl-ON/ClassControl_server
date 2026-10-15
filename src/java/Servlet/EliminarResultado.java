package Servlet;

import Controlador.Resultado_aprendizajeDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarResultado")
public class EliminarResultado extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "resultado"; }

    @Override
    protected boolean eliminar(int id) {
        return new Resultado_aprendizajeDAO().eliminarResultado_aprendizaje(id);
    }
}
