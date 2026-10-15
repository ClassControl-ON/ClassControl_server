package Servlet;

import Controlador.Resultado_aprendizajeDAO;
import Modelo.Resultado_aprendizaje;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarResultado")
public class RegistrarResultado extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "resultado"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Resultado_aprendizaje resultado = new Resultado_aprendizaje();
        resultado.setId_resultado_aprendizaje(0);
        resultado.setCodigo_ResultadoAp(entero(request, "codigo_ResultadoAp"));
        resultado.setDescripcion_Resul(texto(request, "descripcion_Resul"));
        resultado.setCompetencias_id_competencias(entero(request, "Competencias_id_competencias"));
        return new Resultado_aprendizajeDAO().insertarResultado_aprendizaje(resultado);
    }
}
