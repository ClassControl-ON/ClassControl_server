/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.VinculacionLaboral;
import Controlador.VinculacionLaboralDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarVinculacionLaboral {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            VinculacionLaboralDAO vinculacionLaboralDAO = new VinculacionLaboralDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            VinculacionLaboral rol = vinculacionLaboralDAO.consultaVinculacionLaboral(idRol);
            

            if (rol != null) {
                System.out.println("=== Trimestre encontrado ===");
                System.out.println("ID: " + rol.getId_vinculacion_Laboral());
                System.out.println("Descripcion de la vinculacion laboral: " + rol.getDescripcion());
                System.out.println("Numero de la vinculacion laboral: " + rol.getNumero_Contrato());
                System.out.println("Fecha de inicio de la vinculacion laboral: " + rol.getFecha_Inicio());
                System.out.println("Fecha de finalizacion de la vinculacion laboral: " + rol.getFecha_Fin());
                System.out.println("ID del usuario: " + rol.getUsuarios_id_usuarios());
            } else {
                System.out.println("No se encontró el tipo de vinculacion con ID: " + idRol);
            }
        }
    
}