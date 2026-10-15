package PruebaActualizar;

import Controlador.SedeDAO;
import Modelo.Sede;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarSede {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SedeDAO dao = new SedeDAO();

        System.out.println("=== MODIFICAR SEDE EXISTENTE ===");

        System.out.print("Ingrese el ID de la sede que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Sede rolActualizar = new Sede();
        rolActualizar.setId_sede(idMod);

        System.out.print("Ingrese el nuevo nombre de la sede: ");
        rolActualizar.setNombre_sede(sc.nextLine());

        
        boolean resultado = dao.actualizarSede(rolActualizar);

        if (resultado) {
            System.out.println("✅ La sede ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ninguna sede con el ID " + idMod);
        }

        sc.close();
    }
}