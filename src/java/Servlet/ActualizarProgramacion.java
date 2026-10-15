package Servlet;

import Controlador.Programacion_InstructoresDAO;
import Modelo.Programacion_Instructores;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarProgramacion")
public class ActualizarProgramacion extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "programacion"; }

    @Override
    protected String getUrlRedireccion() { return "ProgramacionInstructoresServlet"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Programacion_Instructores programacion = new Programacion_Instructores();
        programacion.setId_programacion_Instructores(entero(request, "id"));
        programacion.setObservaciones(textoOpcional(request, "Observaciones"));
        programacion.setFecha_inicial_Prog(fecha(request, "fecha_inicial_Prog"));
        programacion.setFecha_fin_Prog(fecha(request, "fecha_fin_Prog"));
        programacion.setDias_Semana(texto(request, "dias_Semana"));
        programacion.setHora_inicio(hora(request, "hora_inicio"));
        programacion.setHora_fin(hora(request, "hora_fin"));
        programacion.setFicha_id_ficha(entero(request, "Ficha_id_ficha"));
        // Un Instructor siempre edita su propia programación: se ignora el
        // valor que venga del cliente (igual que hace ConsultarProgramaciones).
        if (Autorizacion.esInstructor(request)) {
            Integer idPropio = Autorizacion.idUsuarioDe(request);
            if (idPropio != null) {
                programacion.setUsuarios_id_usuarios(idPropio);
            } else {
                programacion.setUsuarios_id_usuarios(entero(request, "Usuarios_id_usuarios"));
            }
        } else {
            programacion.setUsuarios_id_usuarios(entero(request, "Usuarios_id_usuarios"));
        }
        programacion.setAmbientes_id_ambientes(entero(request, "Ambientes_id_ambientes"));
        programacion.setTrimestre_id_trimestre(entero(request, "Trimestre_id_trimestre"));
        programacion.setEstado_id_estado(entero(request, "Estado_id_estado"));
        programacion.setActividades_id_actividades(entero(request, "Actividades_id_actividades"));
        return new Programacion_InstructoresDAO().actualizarProgramacion_Instructores(programacion);
    }
}
