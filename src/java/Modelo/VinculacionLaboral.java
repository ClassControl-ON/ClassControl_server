/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.time.LocalDate;

public class VinculacionLaboral {
    
private int id_vinculacion_Laboral;
private String descripcion;
private String numero_Contrato;
private LocalDate fecha_Inicio;
private LocalDate fecha_Fin;
private int Usuarios_id_usuarios;

    public int getId_vinculacion_Laboral() {
        return id_vinculacion_Laboral;
    }

    public void setId_vinculacion_Laboral(int id_vinculacion_Laboral) {
        this.id_vinculacion_Laboral = id_vinculacion_Laboral;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNumero_Contrato() {
        return numero_Contrato;
    }

    public void setNumero_Contrato(String numero_Contrato) {
        this.numero_Contrato = numero_Contrato;
    }

    public LocalDate getFecha_Inicio() {
        return fecha_Inicio;
    }

    public void setFecha_Inicio(LocalDate fecha_Inicio) {
        this.fecha_Inicio = fecha_Inicio;
    }

    public LocalDate getFecha_Fin() {
        return fecha_Fin;
    }

    public void setFecha_Fin(LocalDate fecha_Fin) {
        this.fecha_Fin = fecha_Fin;
    }

    public int getUsuarios_id_usuarios() {
        return Usuarios_id_usuarios;
    }

    public void setUsuarios_id_usuarios(int Usuarios_id_usuarios) {
        this.Usuarios_id_usuarios = Usuarios_id_usuarios;
    }

}