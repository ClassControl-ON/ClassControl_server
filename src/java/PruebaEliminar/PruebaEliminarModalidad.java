package PruebaEliminar;

import java.util.Scanner;
import Controlador.ModalidadDAO;

public class PruebaEliminarModalidad {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ModalidadDAO modalidadDAO = new ModalidadDAO();
        
        System.out.println("Ingrese el ID de la modalidad que desea eliminar: ");
        int Id_modalidad = sc.nextInt();
        
        boolean eliminado = modalidadDAO.eliminarModalidad(Id_modalidad);
        
        if(eliminado) {
            System.out.println("\nLa modalidad con ID: " + Id_modalidad + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar la modalidad. Puede que no exista");
        }
        
        sc.close();
    }
}
