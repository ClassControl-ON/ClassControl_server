package Servlet;

import Controlador.Nivel_formacionDAO;
import Modelo.Nivel_formacion;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarNiveles")
public class ConsultarNiveles extends ConsultarBaseServlet<Nivel_formacion> {

    @Override
    protected String getTipo() { return "niveles"; }

    @Override
    protected List<Nivel_formacion> obtenerLista() {
        return new Nivel_formacionDAO().listarNiveles();
    }

    @Override
    protected String camposJson(Nivel_formacion n) {
        return campoNum("id", n.getId_nivel_formacion()) + ","
             + campoStr("descripcion", n.getDescripcion_Nivel_Formacion());
    }
}
