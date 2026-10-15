package PruebaActualizar;

import Controlador.JornadaDAO;
import Modelo.Jornada;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarJornada {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        JornadaDAO dao = new JornadaDAO();

        System.out.println("=== MODIFICAR JORNADA EXISTENTE ===");

        System.out.print("Ingrese el ID de la jornada que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Jornada rolActualizar = new Jornada();
        rolActualizar.setId_jornada(idMod);

        System.out.print("Ingrese la nueva descripción de la jornada: ");
        rolActualizar.setDescripcion_Jornada(sc.nextLine());

        
        boolean resultado = dao.actualizarJornada(rolActualizar);

        if (resultado) {
            System.out.println("✅ La jornada ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ningúna jornada con el ID " + idMod);
        }

        sc.close();
    }
}