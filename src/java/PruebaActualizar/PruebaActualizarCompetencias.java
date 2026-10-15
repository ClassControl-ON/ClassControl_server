package PruebaActualizar;

import Controlador.CompetenciasDAO;
import Modelo.Competencias;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarCompetencias {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CompetenciasDAO dao = new CompetenciasDAO();

        System.out.println("=== MODIFICAR COMPETENCIA EXISTENTE ===");

        System.out.print("Ingrese el ID de la competencia que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Competencias rolActualizar = new Competencias();
        rolActualizar.setId_competencias(idMod);

        System.out.print("Ingrese el nuevo codigo de la competencia: ");
        rolActualizar.setCodigo_Competencias(sc.nextInt());
        sc.nextLine(); 
        
        System.out.print("Ingrese la nueva descripcion de la competencia: ");
        rolActualizar.setDescripcion_Competencias(sc.nextLine());
        
        
        System.out.print("Ingrese el nuevo ID de la programacion de instructores: ");
        rolActualizar.setProgramas_idProgramas(sc.nextInt());
        sc.nextLine();

        
        boolean resultado = dao.actualizarCompetencias(rolActualizar);

        if (resultado) {
            System.out.println("✅ La competencia ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ninguna competencia con el ID " + idMod);
        }

        sc.close();
    }
}