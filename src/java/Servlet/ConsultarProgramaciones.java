package Servlet;

import Controlador.Programacion_InstructoresDAO;
import Controlador.UsuariosDAO;
import Modelo.ProgramacionInstructoresDTO;
import Modelo.Usuarios;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Este es el servlet que le falta a Programacion_instructoresJS.js:
 * expone en JSON exactamente lo que el JS espera encontrar en
 * "data-programaciones" (instructor, ficha, ambiente,
 * trimestre, horaInicio, horaFin, diasSemana, estado...).
 *
 * Filtrado por rol:
 *  - Administrador / Coordinador: ven todo, pueden usar los filtros de
 *    la pantalla libremente (instructorId/fichaId/trimestreId).
 *  - Instructor: SIEMPRE se fuerza instructorId = su propio id de
 *    sesión, sin importar lo que envíe el cliente, para que nunca vea
 *    el horario de otro instructor.
 *  - Aprendiz: se fuerza fichaId = la ficha asignada en su perfil
 *    (usuarios.Ficha_id_ficha), para que vea únicamente el horario de
 *    su propia ficha. Si aún no tiene ficha asignada ve el listado
 *    general filtrable a mano.
 */
@WebServlet("/ConsultarProgramaciones")
public class ConsultarProgramaciones extends ConsultarBaseServlet<ProgramacionInstructoresDTO> {

    @Override
    protected String getTipo() { return "programaciones"; }

    @Override
    protected List<ProgramacionInstructoresDTO> obtenerLista() {
        return new Programacion_InstructoresDAO().listarProgramaciones();
    }

    @Override
    protected List<ProgramacionInstructoresDTO> obtenerLista(HttpServletRequest request) {
        Integer instructorId = enteroOpcional(request, "instructorId");
        Integer fichaId = enteroOpcional(request, "fichaId");
        Integer trimestreId = enteroOpcional(request, "trimestreId");

        if (Autorizacion.esInstructor(request)) {
            Integer idPropio = Autorizacion.idUsuarioDe(request);
            instructorId = idPropio; // se ignora cualquier valor que venga del cliente
        }

        if (Autorizacion.esAprendiz(request)) {
            Integer idPropio = Autorizacion.idUsuarioDe(request);
            if (idPropio != null) {
                Usuarios aprendiz = new UsuariosDAO().consultaUsuarios(idPropio);
                if (aprendiz != null && aprendiz.getFicha_id_ficha() != null) {
                    // Se ignora el filtro que venga del cliente: su ficha manda.
                    fichaId = aprendiz.getFicha_id_ficha();
                }
            }
        }

        return new Programacion_InstructoresDAO().listarProgramaciones(
                instructorId, fichaId, trimestreId);
    }

    private Integer enteroOpcional(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.isBlank()) {
            return null;
        }
        int numero = Integer.parseInt(valor);
        if (numero <= 0) {
            throw new NumberFormatException("El filtro " + nombre + " debe ser positivo");
        }
        return numero;
    }

    @Override
    protected String camposJson(ProgramacionInstructoresDTO p) {
        return campoNum("id", p.getId()) + ","
             + campoStr("observaciones", p.getObservaciones()) + ","
             + campoFecha("fechaInicio", p.getFechaInicialProg()) + ","
             + campoFecha("fechaFin", p.getFechaFinProg()) + ","
             + campoStr("diasSemana", p.getDiasSemana()) + ","
             + campoStr("horaInicio", p.getHoraInicio() == null ? null : p.getHoraInicio().toString()) + ","
             + campoStr("horaFin", p.getHoraFin() == null ? null : p.getHoraFin().toString()) + ","
             + campoNum("fichaId", p.getFichaId()) + ","
             + campoStr("ficha", p.getFichaNumero()) + ","
             + campoStr("fichaPrograma", p.getFichaPrograma()) + ","
             + campoNum("instructorId", p.getInstructorId()) + ","
             + campoStr("instructor", p.getInstructorNombre()) + ","
             + campoNum("ambienteId", p.getAmbienteId()) + ","
             + campoStr("ambiente", p.getAmbienteNombre()) + ","
             + campoNum("trimestreId", p.getTrimestreId()) + ","
             + campoStr("trimestre", p.getTrimestreNombre()) + ","
             + campoNum("estadoId", p.getEstadoId()) + ","
             + campoStr("estado", p.getEstadoNombre()) + ","
             + campoNum("actividadId", p.getActividadId()) + ","
             + campoStr("actividad", p.getActividadNombre()) + ","
             + campoStr("competencia", p.getCompetenciaNombre());
    }
}
