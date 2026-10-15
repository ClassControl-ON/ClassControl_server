package PruebaEliminar;

import java.util.Scanner;
import Controlador.EtapaDAO;

public class PruebaEliminarEtapa {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EtapaDAO etapaDAO = new EtapaDAO();
        
        System.out.println("Ingrese el ID de la etapa que desea eliminar: ");
        int Id_etapa = sc.nextInt();
        
        boolean eliminado = etapaDAO.eliminarEtapa(Id_etapa);
        
        if(eliminado) {
            System.out.println("\nLa etapa con ID: " + Id_etapa + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar la etapa. Puede que no exista");
        }
        
        sc.close();
    }
}
