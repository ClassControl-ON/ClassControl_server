package Servlet;

import Controlador.Resultado_aprendizajeDAO;
import Modelo.Resultado_aprendizaje;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarResultado")
public class ActualizarResultado extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "resultado"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Resultado_aprendizaje resultado = new Resultado_aprendizaje();
        resultado.setId_resultado_aprendizaje(entero(request, "id"));
        resultado.setCodigo_ResultadoAp(entero(request, "codigo_ResultadoAp"));
        resultado.setDescripcion_Resul(texto(request, "descripcion_Resul"));
        resultado.setCompetencias_id_competencias(entero(request, "Competencias_id_competencias"));
        return new Resultado_aprendizajeDAO().actualizarResultado_aprendizaje(resultado);
    }
}
