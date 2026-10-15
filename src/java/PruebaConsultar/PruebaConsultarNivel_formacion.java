/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Nivel_formacion;
import Controlador.Nivel_formacionDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarNivel_formacion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            Nivel_formacionDAO nivel_formacionDAO = new Nivel_formacionDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Nivel_formacion rol = nivel_formacionDAO.consultaNivel_formacion(idRol);
            

            if (rol != null) {
                System.out.println("=== Nivel de formacion encontrado ===");
                System.out.println("ID: " + rol.getId_nivel_formacion());
                System.out.println("Descripcion del nivel de formacion: " + rol.getDescripcion_Nivel_Formacion());
            } else {
                System.out.println("No se encontró el nivel de formacion con ID: " + idRol);
            }
        }
    
}