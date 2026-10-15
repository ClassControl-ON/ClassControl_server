/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.time.LocalDate;

public class Trimestre {
    
private int id_trimestre;
private int num_trimestre;
private String descripcion;
private LocalDate fecha_inicio; 
private LocalDate fecha_fin;

    public int getId_trimestre() {
        return id_trimestre;
    }

    public void setId_trimestre(int id_trimestre) {
        this.id_trimestre = id_trimestre;
    }

    public int getNum_trimestre() {
        return num_trimestre;
    }

    public void setNum_trimestre(int num_trimestre) {
        this.num_trimestre = num_trimestre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

}