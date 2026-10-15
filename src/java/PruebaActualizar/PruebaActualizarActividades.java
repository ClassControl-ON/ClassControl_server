package PruebaActualizar;

import Controlador.ActividadesDAO;
import Modelo.Actividades;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarActividades {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ActividadesDAO dao = new ActividadesDAO();

        System.out.println("=== MODIFICAR ACTIVIDAD EXISTENTE ===");

        System.out.print("Ingrese el ID de la actvidad que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Actividades rolActualizar = new Actividades();
        rolActualizar.setId_actividades(idMod);

        System.out.print("Ingrese el nuevo codigo de la actividad: ");
        rolActualizar.setCodigo_Actividad(sc.nextInt());
        
        System.out.print("Ingrese el nuevo nombre de la actividad: ");
        rolActualizar.setNombre_Act(sc.nextLine());
        
        System.out.print("Ingrese la nueva descripción de la actividad: ");
        rolActualizar.setDescripcion(sc.nextLine());
        
        System.out.print("Ingrese el nuevo ID del resultado de aprendizaje: ");
        rolActualizar.setResultado_aprendizaje_id_resultado_aprendizaje(sc.nextInt());

        
        boolean resultado = dao.actualizarActividades(rolActualizar);

        if (resultado) {
            System.out.println("✅ La actividad ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ningúna actividad con el ID " + idMod);
        }

        sc.close();
    }
}