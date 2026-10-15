package Servlet;

import Controlador.Programacion_InstructoresDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarProgramacion")
public class EliminarProgramacion extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "programacion"; }

    @Override
    protected boolean eliminar(int id) {
        return new Programacion_InstructoresDAO().eliminarProgramacion_Instructores(id);
    }
}
