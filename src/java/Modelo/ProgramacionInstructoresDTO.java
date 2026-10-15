package Modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO de solo lectura para la vista principal de Programación de
 * Instructores. A diferencia de {@link Programacion_Instructores}
 * (que solo trae los IDs de las FK, tal cual está en la tabla),
 * este objeto ya trae los nombres/descripciones resueltos mediante
 * los JOIN hechos en Programacion_InstructoresDAO.listarProgramaciones(),
 * para que el JSP/JS los pinte directamente sin consultas adicionales.
 */
public class ProgramacionInstructoresDTO {

    private int id;
    private String observaciones;
    private LocalDate fechaInicialProg;
    private LocalDate fechaFinProg;
    private String diasSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    private int fichaId;
    private String fichaNumero;
    private String fichaPrograma;

    private int instructorId;
    private String instructorNombre;

    private int ambienteId;
    private String ambienteNombre;

    private int trimestreId;
    private String trimestreNombre;

    private int estadoId;
    private String estadoNombre;

    private int actividadId;
    private String actividadNombre;
    private String competenciaNombre;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDate getFechaInicialProg() {
        return fechaInicialProg;
    }

    public void setFechaInicialProg(LocalDate fechaInicialProg) {
        this.fechaInicialProg = fechaInicialProg;
    }

    public LocalDate getFechaFinProg() {
        return fechaFinProg;
    }

    public void setFechaFinProg(LocalDate fechaFinProg) {
        this.fechaFinProg = fechaFinProg;
    }

    public String getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(String diasSemana) {
        this.diasSemana = diasSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public int getFichaId() {
        return fichaId;
    }

    public void setFichaId(int fichaId) {
        this.fichaId = fichaId;
    }

    public String getFichaNumero() {
        return fichaNumero;
    }

    public void setFichaNumero(String fichaNumero) {
        this.fichaNumero = fichaNumero;
    }

    public String getFichaPrograma() {
        return fichaPrograma;
    }

    public void setFichaPrograma(String fichaPrograma) {
        this.fichaPrograma = fichaPrograma;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public String getInstructorNombre() {
        return instructorNombre;
    }

    public void setInstructorNombre(String instructorNombre) {
        this.instructorNombre = instructorNombre;
    }

    public int getAmbienteId() {
        return ambienteId;
    }

    public void setAmbienteId(int ambienteId) {
        this.ambienteId = ambienteId;
    }

    public String getAmbienteNombre() {
        return ambienteNombre;
    }

    public void setAmbienteNombre(String ambienteNombre) {
        this.ambienteNombre = ambienteNombre;
    }

    public int getTrimestreId() {
        return trimestreId;
    }

    public void setTrimestreId(int trimestreId) {
        this.trimestreId = trimestreId;
    }

    public String getTrimestreNombre() {
        return trimestreNombre;
    }

    public void setTrimestreNombre(String trimestreNombre) {
        this.trimestreNombre = trimestreNombre;
    }

    public int getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(int estadoId) {
        this.estadoId = estadoId;
    }

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    public int getActividadId() {
        return actividadId;
    }

    public void setActividadId(int actividadId) {
        this.actividadId = actividadId;
    }

    public String getActividadNombre() {
        return actividadNombre;
    }

    public void setActividadNombre(String actividadNombre) {
        this.actividadNombre = actividadNombre;
    }

    public String getCompetenciaNombre() {
        return competenciaNombre;
    }

    public void setCompetenciaNombre(String competenciaNombre) {
        this.competenciaNombre = competenciaNombre;
    }
}
