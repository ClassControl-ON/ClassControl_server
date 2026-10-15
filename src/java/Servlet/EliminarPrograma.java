package Servlet;

import Controlador.ProgramasDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarPrograma")
public class EliminarPrograma extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "programa"; }

    @Override
    protected boolean eliminar(int id) {
        return new ProgramasDAO().eliminarProgramas(id);
    }
}
