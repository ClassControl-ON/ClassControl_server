package Servlet;

import Controlador.CompetenciasDAO;
import Modelo.Competencias;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarCompetencia")
public class ActualizarCompetencia extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "competencia"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Competencias competencia = new Competencias();
        competencia.setId_competencias(entero(request, "id"));
        competencia.setCodigo_Competencias(entero(request, "codigo_Competencias"));
        competencia.setDescripcion_Competencias(texto(request, "descripcion_Competencias"));
        competencia.setProgramas_idProgramas(
                entero(request, "Programas_idProgramas"));
        return new CompetenciasDAO().actualizarCompetencias(competencia);
    }
}
