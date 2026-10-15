/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Programacion_Instructores {
    
private int id_programacion_Instructores;
private String Observaciones;
private LocalDate fecha_inicial_Prog;
private LocalDate fecha_fin_Prog;
private String dias_Semana;
private LocalTime hora_inicio;
private LocalTime hora_fin;
private int Ficha_id_ficha;
private int Usuarios_id_usuarios;
private int Ambientes_id_ambientes;
private int Trimestre_id_trimestre;
private int Estado_id_estado;
private int Actividades_id_actividades;

    public int getId_programacion_Instructores() {
        return id_programacion_Instructores;
    }

    public void setId_programacion_Instructores(int id_programacion_Instructores) {
        this.id_programacion_Instructores = id_programacion_Instructores;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(String Observaciones) {
        this.Observaciones = Observaciones;
    }

    public LocalDate getFecha_inicial_Prog() {
        return fecha_inicial_Prog;
    }

    public void setFecha_inicial_Prog(LocalDate fecha_inicial_Prog) {
        this.fecha_inicial_Prog = fecha_inicial_Prog;
    }

    public LocalDate getFecha_fin_Prog() {
        return fecha_fin_Prog;
    }

    public void setFecha_fin_Prog(LocalDate fecha_fin_Prog) {
        this.fecha_fin_Prog = fecha_fin_Prog;
    }

    public String getDias_Semana() {
        return dias_Semana;
    }

    public void setDias_Semana(String dias_Semana) {
        this.dias_Semana = dias_Semana;
    }

    public LocalTime getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(LocalTime hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public LocalTime getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(LocalTime hora_fin) {
        this.hora_fin = hora_fin;
    }

    public int getFicha_id_ficha() {
        return Ficha_id_ficha;
    }

    public void setFicha_id_ficha(int Ficha_id_ficha) {
        this.Ficha_id_ficha = Ficha_id_ficha;
    }

    public int getUsuarios_id_usuarios() {
        return Usuarios_id_usuarios;
    }

    public void setUsuarios_id_usuarios(int Usuarios_id_usuarios) {
        this.Usuarios_id_usuarios = Usuarios_id_usuarios;
    }

    public int getAmbientes_id_ambientes() {
        return Ambientes_id_ambientes;
    }

    public void setAmbientes_id_ambientes(int Ambientes_id_ambientes) {
        this.Ambientes_id_ambientes = Ambientes_id_ambientes;
    }

    public int getTrimestre_id_trimestre() {
        return Trimestre_id_trimestre;
    }

    public void setTrimestre_id_trimestre(int Trimestre_id_trimestre) {
        this.Trimestre_id_trimestre = Trimestre_id_trimestre;
    }

    public int getEstado_id_estado() {
        return Estado_id_estado;
    }

    public void setEstado_id_estado(int Estado_id_estado) {
        this.Estado_id_estado = Estado_id_estado;
    }

    public int getActividades_id_actividades() {
        return Actividades_id_actividades;
    }

    public void setActividades_id_actividades(int Actividades_id_actividades) {
        this.Actividades_id_actividades = Actividades_id_actividades;
    }

}