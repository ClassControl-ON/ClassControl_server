package Servlet;

import Controlador.CompetenciasDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarCompetencia")
public class EliminarCompetencia extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "competencia"; }

    @Override
    protected boolean eliminar(int id) {
        return new CompetenciasDAO().eliminarCompetencias(id);
    }
}
