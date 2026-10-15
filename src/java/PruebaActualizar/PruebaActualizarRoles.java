package PruebaActualizar;

import Controlador.RolesDAO;
import Modelo.Roles;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarRoles {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RolesDAO dao = new RolesDAO();

        System.out.println("=== MODIFICAR ROL EXISTENTE ===");

        System.out.print("Ingrese el ID del rol que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Roles rolActualizar = new Roles();
        rolActualizar.setId_roles(idMod);

        System.out.print("Ingrese la nueva descripción del rol: ");
        rolActualizar.setDescripcion_Roles(sc.nextLine());

        
        boolean resultado = dao.actualizarRoles(rolActualizar);

        if (resultado) {
            System.out.println("✅ El rol ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ningún rol con el ID " + idMod);
        }

        sc.close();
    }
}