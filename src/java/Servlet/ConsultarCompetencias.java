package Servlet;

import Controlador.CompetenciasDAO;
import Controlador.FichaDAO;
import Modelo.Competencias;
import Modelo.Ficha;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarCompetencias")
public class ConsultarCompetencias extends ConsultarBaseServlet<Competencias> {

    @Override
    protected String getTipo() { return "competencias"; }

    @Override
    protected List<Competencias> obtenerLista() {
        return new CompetenciasDAO().listarCompetencias();
    }

    /**
     * Un aprendiz solo ve las competencias del programa de su ficha.
     * Los demas roles ven el catalogo completo.
     */
    @Override
    protected List<Competencias> obtenerLista(jakarta.servlet.http.HttpServletRequest request)
            throws Exception {
        List<Competencias> todas = new CompetenciasDAO().listarCompetencias();

        Integer idSesion = Autorizacion.idUsuarioDe(request);
        if (idSesion == null || !Autorizacion.esAprendiz(request)) {
            return todas;
        }

        Ficha ficha = new FichaDAO().consultarFichaDeAprendiz(idSesion);
        if (ficha == null) {
            return new java.util.ArrayList<>();
        }
        int programaDeLaFicha = ficha.getProgramas_idProgramas();
        if (programaDeLaFicha <= 0) {
            return new java.util.ArrayList<>();
        }

        List<Competencias> filtradas = new java.util.ArrayList<>();
        for (Competencias c : todas) {
            if (c.getProgramas_idProgramas() == programaDeLaFicha) {
                filtradas.add(c);
            }
        }
        return filtradas;
    }

    @Override
    protected String camposJson(Competencias c) {
        int pid = c.getProgramas_idProgramas();
        return campoNum("id", c.getId_competencias()) + ","
             + campoNum("codigo", c.getCodigo_Competencias()) + ","
             + campoStr("descripcion", c.getDescripcion_Competencias()) + ","
             + campoNum("programaId", pid == 0 ? null : Integer.valueOf(pid));
    }
}
