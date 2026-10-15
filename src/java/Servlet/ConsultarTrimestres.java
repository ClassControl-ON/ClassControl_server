package Servlet;

import Controlador.TrimestreDAO;
import Modelo.Trimestre;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarTrimestres")
public class ConsultarTrimestres extends ConsultarBaseServlet<Trimestre> {

    @Override
    protected String getTipo() { return "trimestres"; }

    @Override
    protected List<Trimestre> obtenerLista() {
        return new TrimestreDAO().listarTrimestres();
    }

    @Override
    protected String camposJson(Trimestre t) {
        return campoNum("id", t.getId_trimestre()) + ","
             + campoNum("numTrimestre", t.getNum_trimestre()) + ","
             + campoStr("descripcion", t.getDescripcion()) + ","
             + campoFecha("fechaInicio", t.getFecha_inicio()) + ","
             + campoFecha("fechaFin", t.getFecha_fin());
    }
}
