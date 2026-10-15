package PruebaEliminar;

import java.util.Scanner;
import Controlador.ActividadesDAO;

public class PruebaEliminarActividades {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ActividadesDAO actividadesDAO = new ActividadesDAO();
        
        System.out.println("Ingrese el ID de la actvidad que desea eliminar: ");
        int Id_actividades = sc.nextInt();
        
        boolean eliminado = actividadesDAO.eliminarActividades(Id_actividades);
        
        if(eliminado) {
            System.out.println("\nLa actividad con ID: " + Id_actividades + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar la actividad. Puede que no exista");
        }
        
        sc.close();
    }
}
