package Servlet;

import Controlador.ProgramasDAO;
import Modelo.Programas;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarProgramas")
public class ConsultarProgramas extends ConsultarBaseServlet<Programas> {

    @Override
    protected String getTipo() { return "programas"; }

    @Override
    protected List<Programas> obtenerLista() {
        return new ProgramasDAO().listarProgramas();
    }

    @Override
    protected String camposJson(Programas p) {
        return campoNum("id", p.getIdProgramas()) + ","
             + campoNum("codigo", p.getCodigo_programa()) + ","
             + campoStr("nombre", p.getNombre_programa());
    }
}
