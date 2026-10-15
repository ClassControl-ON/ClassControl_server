package Servlet;

import Controlador.ProgramasDAO;
import Modelo.Programas;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarPrograma")
public class RegistrarPrograma extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "programa"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Programas programa = new Programas();
        programa.setIdProgramas(0);
        programa.setCodigo_programa(entero(request, "codigo_programa"));
        programa.setNombre_programa(texto(request, "nombre_programa"));
        return new ProgramasDAO().insertarProgramas(programa);
    }
}
