package PruebaActualizar;

import Controlador.EstadoDAO;
import Modelo.Estado;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarEstado {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadoDAO dao = new EstadoDAO();

        System.out.println("=== MODIFICAR ESTADO EXISTENTE ===");

        System.out.print("Ingrese el ID del estado que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Estado rolActualizar = new Estado();
        rolActualizar.setId_estado(idMod);

        System.out.print("Ingrese la nueva descripcion del estado: ");
        rolActualizar.setDescripcion_Estado(sc.nextLine());

        
        boolean resultado = dao.actualizarEstado(rolActualizar);

        if (resultado) {
            System.out.println("✅ El estado ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ningun estado con el ID " + idMod);
        }

        sc.close();
    }
}