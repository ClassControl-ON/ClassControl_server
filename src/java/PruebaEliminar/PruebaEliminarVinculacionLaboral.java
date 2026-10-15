package PruebaEliminar;

import java.util.Scanner;
import Controlador.VinculacionLaboralDAO;

public class PruebaEliminarVinculacionLaboral {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        VinculacionLaboralDAO vinculacionLaboralDAO = new VinculacionLaboralDAO();
        
        System.out.println("Ingrese el ID de la vinculacion laboral que desea eliminar: ");
        int IdvinculacionLaboral = sc.nextInt();
        
        boolean eliminado = vinculacionLaboralDAO.eliminarVinculacionLaboral(IdvinculacionLaboral);
        
        if(eliminado) {
            System.out.println("\nLa vinculacion laboral con ID: " + IdvinculacionLaboral + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar la vinculacion laboral. Puede que no exista");
        }
        
        sc.close();
    }
}
