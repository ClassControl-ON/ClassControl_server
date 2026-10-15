package PruebaActualizar;

import Controlador.ModalidadDAO;
import Modelo.Modalidad;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarModalidad {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ModalidadDAO dao = new ModalidadDAO();

        System.out.println("=== MODIFICAR MODALIDAD EXISTENTE ===");

        System.out.print("Ingrese el ID de la modalidad que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Modalidad rolActualizar = new Modalidad();
        rolActualizar.setId_modalidad(idMod);

        System.out.print("Ingrese la nueva descripción de la modalidad: ");
        rolActualizar.setDescripcion_Modalidad(sc.nextLine());

        
        boolean resultado = dao.actualizarModalidad(rolActualizar);

        if (resultado) {
            System.out.println("✅ La modalidad ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ningúna modalidad con el ID " + idMod);
        }

        sc.close();
    }
}