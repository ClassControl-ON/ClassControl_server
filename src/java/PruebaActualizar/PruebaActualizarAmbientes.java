package PruebaActualizar;

import Controlador.AmbientesDAO;
import Modelo.Ambientes;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarAmbientes {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AmbientesDAO dao = new AmbientesDAO();

        System.out.println("=== MODIFICAR AMBIENTE EXISTENTE ===");

        System.out.print("Ingrese el ID del ambiente que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Ambientes rolActualizar = new Ambientes();
        rolActualizar.setId_ambientes(idMod);

        System.out.print("Ingrese la nueva descripcion del amnbiente: ");
        rolActualizar.setDescripcion_Ambiente(sc.nextLine());
        sc.nextLine();  
        
        System.out.print("Ingrese la nueva capacidad del ambiente: ");
        rolActualizar.setCapacidad(sc.nextInt());
        
        System.out.print("Ingrese la sede del ambiente: ");
        rolActualizar.setSede_id_sede(sc.nextInt());

        
        boolean resultado = dao.actualizarAmbientes(rolActualizar);

        if (resultado) {
            System.out.println("✅ El ambiente ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ningun ambiente con el ID " + idMod);
        }

        sc.close();
    }
}