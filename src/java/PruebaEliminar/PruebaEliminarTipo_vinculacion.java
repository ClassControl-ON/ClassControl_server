package PruebaEliminar;

import java.util.Scanner;
import Controlador.Tipo_vinculacionDAO;

public class PruebaEliminarTipo_vinculacion {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Tipo_vinculacionDAO tipo_vinculacionDAO = new Tipo_vinculacionDAO();
        
        System.out.println("Ingrese el ID del tipo de vinculacion que desea eliminar: ");
        int Id_tipo_vinculacion = sc.nextInt();
        
        boolean eliminado = tipo_vinculacionDAO.eliminarTipo_vinculacion(Id_tipo_vinculacion);
        
        if(eliminado) {
            System.out.println("\nEl tipo de vinculacion con ID: " + Id_tipo_vinculacion + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar el tipo de vinculacion. Puede que no exista");
        }
        
        sc.close();
    }
}
