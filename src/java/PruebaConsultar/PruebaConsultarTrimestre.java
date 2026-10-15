/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Trimestre;
import Controlador.TrimestreDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarTrimestre {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            TrimestreDAO trimestreDAO = new TrimestreDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Trimestre rol = trimestreDAO.consultaTrimestre(idRol);
            

            if (rol != null) {
                System.out.println("=== Trimestre encontrado ===");
                System.out.println("ID: " + rol.getId_trimestre());
                System.out.println("Numero de trimestre: " + rol.getNum_trimestre());
                System.out.println("Descripcion del trimestre: " + rol.getDescripcion());
                System.out.println("Fecha de inicio del trimestre: " + rol.getFecha_inicio());
                System.out.println("Fecha de finalizacion del trimestre: " + rol.getFecha_fin());
            } else {
                System.out.println("No se encontró el tipo de vinculacion con ID: " + idRol);
            }
        }
    
}