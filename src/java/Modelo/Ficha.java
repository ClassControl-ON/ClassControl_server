/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.time.LocalDate;

public class Ficha {
    
private int id_ficha;
private int codigo_ficha;
private LocalDate fecha_inicio;
private LocalDate fecha_fin;
private int cantidad_aprendices;
private int Programas_idProgramas;
private int Jornada_id_jornada;
private int Modalidad_id_modalidad;
private int Nivel_formacion_id_nivel_formacion;
private int Sede_id_sede;
private int Estado_id_estado;
private int Etapa_id_etapa;


    public int getId_ficha() {
        return id_ficha;
    }

    public void setId_ficha(int id_ficha) {
        this.id_ficha = id_ficha;
    }

    public int getCodigo_ficha() {
        return codigo_ficha;
    }

    public void setCodigo_ficha(int codigo_ficha) {
        this.codigo_ficha = codigo_ficha;
    }

    public LocalDate getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(LocalDate fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public LocalDate getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(LocalDate fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public int getCantidad_aprendices() {
        return cantidad_aprendices;
    }

    public void setCantidad_aprendices(int cantidad_aprendices) {
        this.cantidad_aprendices = cantidad_aprendices;
    }

    public int getProgramas_idProgramas() {
        return Programas_idProgramas;
    }

    public void setProgramas_idProgramas(int Programas_idProgramas) {
        this.Programas_idProgramas = Programas_idProgramas;
    }

    public int getJornada_id_jornada() {
        return Jornada_id_jornada;
    }

    public void setJornada_id_jornada(int Jornada_id_jornada) {
        this.Jornada_id_jornada = Jornada_id_jornada;
    }

    public int getModalidad_id_modalidad() {
        return Modalidad_id_modalidad;
    }

    public void setModalidad_id_modalidad(int Modalidad_id_modalidad) {
        this.Modalidad_id_modalidad = Modalidad_id_modalidad;
    }

    public int getNivel_formacion_id_nivel_formacion() {
        return Nivel_formacion_id_nivel_formacion;
    }

    public void setNivel_formacion_id_nivel_formacion(int Nivel_formacion_id_nivel_formacion) {
        this.Nivel_formacion_id_nivel_formacion = Nivel_formacion_id_nivel_formacion;
    }

    public int getSede_id_sede() {
        return Sede_id_sede;
    }

    public void setSede_id_sede(int Sede_id_sede) {
        this.Sede_id_sede = Sede_id_sede;
    }

    public int getEstado_id_estado() {
        return Estado_id_estado;
    }

    public void setEstado_id_estado(int Estado_id_estado) {
        this.Estado_id_estado = Estado_id_estado;
    }

    public int getEtapa_id_etapa() {
        return Etapa_id_etapa;
    }

    public void setEtapa_id_etapa(int Etapa_id_etapa) {
        this.Etapa_id_etapa = Etapa_id_etapa;
    }
 
}