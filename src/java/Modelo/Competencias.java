/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

public class Competencias {

private int id_competencias;
private int codigo_Competencias;
private String descripcion_Competencias;
private int Programas_idProgramas;

    public int getId_competencias() {
        return id_competencias;
    }

    public void setId_competencias(int id_competencias) {
        this.id_competencias = id_competencias;
    }

    public int getCodigo_Competencias() {
        return codigo_Competencias;
    }

    public void setCodigo_Competencias(int codigo_Competencias) {
        this.codigo_Competencias = codigo_Competencias;
    }

    public String getDescripcion_Competencias() {
        return descripcion_Competencias;
    }

    public void setDescripcion_Competencias(String descripcion_Competencias) {
        this.descripcion_Competencias = descripcion_Competencias;
    }

    public int getProgramas_idProgramas() {
        return Programas_idProgramas;
    }

    public void setProgramas_idProgramas(int Programas_idProgramas) {
        this.Programas_idProgramas = Programas_idProgramas;
    }

}
