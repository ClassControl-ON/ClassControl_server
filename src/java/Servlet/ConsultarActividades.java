package Servlet;

import Controlador.ActividadesDAO;
import Modelo.Actividades;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarActividades")
public class ConsultarActividades extends ConsultarBaseServlet<Actividades> {

    @Override
    protected String getTipo() { return "actividades"; }

    @Override
    protected List<Actividades> obtenerLista() {
        return new ActividadesDAO().listarActividades();
    }

    @Override
    protected String camposJson(Actividades a) {
        return campoNum("id", a.getId_actividades()) + ","
             + campoNum("codigoActividad", a.getCodigo_Actividad()) + ","
             + campoStr("nombre", a.getNombre_Act()) + ","
             + campoStr("descripcion", a.getDescripcion()) + ","
             + campoNum("resultadoId", a.getResultado_aprendizaje_id_resultado_aprendizaje());
    }
}
