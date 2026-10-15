/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

public class Actividades {
    
private int id_actividades;
private int codigo_Actividad;
private String nombre_Act;
private String descripcion;
private int Resultado_aprendizaje_id_resultado_aprendizaje;

    public int getId_actividades() {
        return id_actividades;
    }

    public void setId_actividades(int id_actividades) {
        this.id_actividades = id_actividades;
    }

    public int getCodigo_Actividad() {
        return codigo_Actividad;
    }

    public void setCodigo_Actividad(int codigo_Actividad) {
        this.codigo_Actividad = codigo_Actividad;
    }

    public String getNombre_Act() {
        return nombre_Act;
    }

    public void setNombre_Act(String nombre_Act) {
        this.nombre_Act = nombre_Act;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getResultado_aprendizaje_id_resultado_aprendizaje() {
        return Resultado_aprendizaje_id_resultado_aprendizaje;
    }

    public void setResultado_aprendizaje_id_resultado_aprendizaje(int Resultado_aprendizaje_id_resultado_aprendizaje) {
        this.Resultado_aprendizaje_id_resultado_aprendizaje = Resultado_aprendizaje_id_resultado_aprendizaje;
    }
 
}