package PruebaEliminar;

import java.util.Scanner;
import Controlador.RolesDAO;

public class PruebaEliminarRoles {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RolesDAO rolesDAO = new RolesDAO();
        
        System.out.println("Ingrese el ID del rol que desea eliminar: ");
        int Id_roles = sc.nextInt();
        
        boolean eliminado = rolesDAO.eliminarRoles(Id_roles);
        
        if(eliminado) {
            System.out.println("\nEl rol con ID " + Id_roles + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar el rol. Puede que no exista");
        }
        
        sc.close();
    }
}
