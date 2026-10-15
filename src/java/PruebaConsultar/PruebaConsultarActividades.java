/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Actividades;
import Controlador.ActividadesDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarActividades {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            ActividadesDAO actividadesDAO = new ActividadesDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Actividades rol = actividadesDAO.consultaActividades(idRol);
            

            if (rol != null) {
                System.out.println("=== Actividad encontrada ===");
                System.out.println("ID: " + rol.getId_actividades());
                System.out.println("Codigo de la actividad: " + rol.getCodigo_Actividad());
                System.out.println("Nombre de la actividad: " + rol.getNombre_Act());
                System.out.println("Descripcion de la actividad: " + rol.getDescripcion());
                System.out.println("ID del resultado de aprendizaje: " + rol.getResultado_aprendizaje_id_resultado_aprendizaje());
            } else {
                System.out.println("No se encontró la actividad con ID: " + idRol);
            }
        }
    
}