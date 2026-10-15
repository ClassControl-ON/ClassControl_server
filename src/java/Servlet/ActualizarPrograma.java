package Servlet;

import Controlador.ProgramasDAO;
import Modelo.Programas;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarPrograma")
public class ActualizarPrograma extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "programa"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Programas programa = new Programas();
        programa.setIdProgramas(entero(request, "id"));
        programa.setCodigo_programa(entero(request, "codigo_programa"));
        programa.setNombre_programa(texto(request, "nombre_programa"));
        return new ProgramasDAO().actualizarProgramas(programa);
    }
}
