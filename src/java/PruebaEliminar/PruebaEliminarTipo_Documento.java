package PruebaEliminar;

import java.util.Scanner;
import Controlador.Tipo_DocumentoDAO;

public class PruebaEliminarTipo_Documento {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Tipo_DocumentoDAO tipo_DocumentoDAO = new Tipo_DocumentoDAO();
        
        System.out.println("Ingrese el ID del tipo de documento que desea eliminar: ");
        int Id_tipo_Documento = sc.nextInt();
        
        boolean eliminado = tipo_DocumentoDAO.eliminarTipo_Documento(Id_tipo_Documento);
        
        if(eliminado) {
            System.out.println("\nEl tipo de documento con ID: " + Id_tipo_Documento + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar el tipo de documento. Puede que no exista");
        }
        
        sc.close();
    }
}
