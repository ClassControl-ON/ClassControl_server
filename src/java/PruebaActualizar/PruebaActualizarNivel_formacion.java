package PruebaActualizar;

import Controlador.Nivel_formacionDAO;
import Modelo.Nivel_formacion;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarNivel_formacion {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Nivel_formacionDAO dao = new Nivel_formacionDAO();

        System.out.println("=== MODIFICAR NIVEL DE FORMACION EXISTENTE ===");

        System.out.print("Ingrese el ID del nivel de formacion que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Nivel_formacion rolActualizar = new Nivel_formacion();
        rolActualizar.setId_nivel_formacion(idMod);

        System.out.print("Ingrese la nueva descripcion del nivel de formacion: ");
        rolActualizar.setDescripcion_Nivel_Formacion(sc.nextLine());

        
        boolean resultado = dao.actualizarNivel_formacion(rolActualizar);

        if (resultado) {
            System.out.println("✅ El nivel de formacion ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ningun nivel de formacion con el ID " + idMod);
        }

        sc.close();
    }
}