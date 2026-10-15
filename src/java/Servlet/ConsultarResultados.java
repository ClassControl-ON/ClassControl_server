package Servlet;

import Controlador.Resultado_aprendizajeDAO;
import Modelo.Resultado_aprendizaje;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarResultados")
public class ConsultarResultados extends ConsultarBaseServlet<Resultado_aprendizaje> {

    @Override
    protected String getTipo() { return "resultados"; }

    @Override
    protected List<Resultado_aprendizaje> obtenerLista() {
        return new Resultado_aprendizajeDAO().listarResultados();
    }

    @Override
    protected String camposJson(Resultado_aprendizaje r) {
        return campoNum("id", r.getId_resultado_aprendizaje()) + ","
             + campoNum("codigo", r.getCodigo_ResultadoAp()) + ","
             + campoStr("descripcion", r.getDescripcion_Resul()) + ","
             + campoNum("competenciaId", r.getCompetencias_id_competencias());
    }
}
