/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

public class Estado {
    
private int id_estado;
private String descripcion_Estado;
private int Tipo_Estado_id_tipo_estado;

    public int getId_estado() {
        return id_estado;
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }

    public String getDescripcion_Estado() {
        return descripcion_Estado;
    }

    public void setDescripcion_Estado(String descripcion_Estado) {
        this.descripcion_Estado = descripcion_Estado;
    }

    public int getTipo_Estado_id_tipo_estado() {
        return Tipo_Estado_id_tipo_estado;
    }

    public void setTipo_Estado_id_tipo_estado(int Tipo_Estado_id_tipo_estado) {
        this.Tipo_Estado_id_tipo_estado = Tipo_Estado_id_tipo_estado;
    }

}