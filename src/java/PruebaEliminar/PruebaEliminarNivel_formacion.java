package PruebaEliminar;

import java.util.Scanner;
import Controlador.Nivel_formacionDAO;

public class PruebaEliminarNivel_formacion {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Nivel_formacionDAO nivel_formacionDAO = new Nivel_formacionDAO();
        
        System.out.println("Ingrese el ID del nivel de formacion que desea eliminar: ");
        int Id_nivel_formacion = sc.nextInt();
        
        boolean eliminado = nivel_formacionDAO.eliminarNivel_formacion(Id_nivel_formacion);
        
        if(eliminado) {
            System.out.println("\nEl nivel de formacion con ID: " + Id_nivel_formacion + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar el nivel de formacion. Puede que no exista");
        }
        
        sc.close();
    }
}
