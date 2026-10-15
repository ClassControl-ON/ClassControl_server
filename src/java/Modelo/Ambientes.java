/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

public class Ambientes{
    
private int id_ambientes;
private String descripcion_Ambiente;
private int capacidad;
private int Sede_id_sede;
private String estado_Ambiente = "Disponible";

    public int getId_ambientes() {
        return id_ambientes;
    }

    public void setId_ambientes(int id_ambientes) {
        this.id_ambientes = id_ambientes;
    }

    public String getDescripcion_Ambiente() {
        return descripcion_Ambiente;
    }

    public void setDescripcion_Ambiente(String descripcion_Ambiente) {
        this.descripcion_Ambiente = descripcion_Ambiente;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public int getSede_id_sede() {
        return Sede_id_sede;
    }

    public void setSede_id_sede(int Sede_id_sede) {
        this.Sede_id_sede = Sede_id_sede;
    }

    public String getEstado_Ambiente() {
        return estado_Ambiente;
    }

    public void setEstado_Ambiente(String estado_Ambiente) {
        this.estado_Ambiente = estado_Ambiente;
    }

}