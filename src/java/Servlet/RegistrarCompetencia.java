package Servlet;

import Controlador.CompetenciasDAO;
import Modelo.Competencias;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarCompetencia")
public class RegistrarCompetencia extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "competencia"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Competencias competencia = new Competencias();
        competencia.setId_competencias(0);
        competencia.setCodigo_Competencias(entero(request, "codigo_Competencias"));
        competencia.setDescripcion_Competencias(texto(request, "descripcion_Competencias"));
        competencia.setProgramas_idProgramas(
                entero(request, "Programas_idProgramas"));
        return new CompetenciasDAO().insertarCompetencias(competencia);
    }
}
