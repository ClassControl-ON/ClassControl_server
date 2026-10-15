/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Etapa;
import Controlador.EtapaDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarEtapa {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            EtapaDAO etapaDAO = new EtapaDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Etapa rol = etapaDAO.consultaEtapa(idRol);
            

            if (rol != null) {
                System.out.println("=== Etapa encontrada ===");
                System.out.println("ID: " + rol.getId_etapa());
                System.out.println("Descripcion de la etapa: " + rol.getDescripcion_Etapa());
            } else {
                System.out.println("No se encontró el rol con ID: " + idRol);
            }
        }
    
}