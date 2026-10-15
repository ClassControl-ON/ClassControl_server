package PruebaActualizar;

import Controlador.ProgramasDAO;
import Modelo.Programas;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarProgramas {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ProgramasDAO dao = new ProgramasDAO();

        System.out.println("=== MODIFICAR PROGRAMA EXISTENTE ===");

        System.out.print("Ingrese el ID del programa que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Programas rolActualizar = new Programas();
        rolActualizar.setIdProgramas(idMod);

        System.out.print("Ingrese el nuevo codigo del programa: ");
        rolActualizar.setCodigo_programa(sc.nextInt());
        sc.nextLine();
        
        System.out.print("Ingrese el nuevo nombre del programa: ");
        rolActualizar.setNombre_programa(sc.nextLine());

        
        boolean resultado = dao.actualizarProgramas(rolActualizar);

        if (resultado) {
            System.out.println("✅ El programa ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ningun programa con el ID " + idMod);
        }

        sc.close();
    }
}