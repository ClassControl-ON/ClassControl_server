/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Tipo_Documento;
import Controlador.Tipo_DocumentoDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarTipo_Documento {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            Tipo_DocumentoDAO tipo_DocumentoDAO = new Tipo_DocumentoDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Tipo_Documento rol = tipo_DocumentoDAO.consultaTipo_Documento(idRol);
            

            if (rol != null) {
                System.out.println("=== Tipo de documento encontrado ===");
                System.out.println("ID: " + rol.getId_tipo_Documento());
                System.out.println("Descripcion del tipo de documento: " + rol.getDescripcion_Tipo_Doc());
            } else {
                System.out.println("No se encontró el tipo de documento con ID: " + idRol);
            }
        }
    
}