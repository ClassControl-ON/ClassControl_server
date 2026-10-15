package PruebaActualizar;

import Controlador.Resultado_aprendizajeDAO;
import Modelo.Resultado_aprendizaje;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarResultado_aprendizaje {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Resultado_aprendizajeDAO dao = new Resultado_aprendizajeDAO();

        System.out.println("=== MODIFICAR RESULTADO DE APRENDIZAJE EXISTENTE ===");

        System.out.print("Ingrese el ID del programa que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Resultado_aprendizaje rolActualizar = new Resultado_aprendizaje();
        rolActualizar.setId_resultado_aprendizaje(idMod);

        System.out.print("Ingrese el nuevo codigo del resultado: ");
        rolActualizar.setCodigo_ResultadoAp(sc.nextInt());
        sc.nextLine();
        
        System.out.print("Ingrese la nueva descripcion del resultado: ");
        rolActualizar.setDescripcion_Resul(sc.nextLine());
        
        
        System.out.print("Ingrese el nuevo ID de la competencia: ");
        rolActualizar.setCompetencias_id_competencias(sc.nextInt());

        
        boolean resultado = dao.actualizarResultado_aprendizaje(rolActualizar);

        if (resultado) {
            System.out.println("✅ El resultado de aprendizaje ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ningun resultado de aprendizaje con el ID " + idMod);
        }

        sc.close();
    }
}