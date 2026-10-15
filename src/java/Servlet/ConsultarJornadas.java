package Servlet;

import Controlador.JornadaDAO;
import Modelo.Jornada;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarJornadas")
public class ConsultarJornadas extends ConsultarBaseServlet<Jornada> {

    @Override
    protected String getTipo() { return "jornadas"; }

    @Override
    protected List<Jornada> obtenerLista() {
        return new JornadaDAO().listarJornadas();
    }

    @Override
    protected String camposJson(Jornada j) {
        return campoNum("id", j.getId_jornada()) + ","
             + campoStr("descripcion", j.getDescripcion_Jornada());
    }
}
