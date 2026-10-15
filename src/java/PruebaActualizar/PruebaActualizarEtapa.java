package PruebaActualizar;

import Controlador.EtapaDAO;
import Modelo.Etapa;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarEtapa {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EtapaDAO dao = new EtapaDAO();

        System.out.println("=== MODIFICAR ETAPA EXISTENTE ===");

        System.out.print("Ingrese el ID de la etapa que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Etapa rolActualizar = new Etapa();
        rolActualizar.setId_etapa(idMod);

        System.out.print("Ingrese la nueva descripcion de la etapa: ");
        rolActualizar.setDescripcion_Etapa(sc.nextLine());

        
        boolean resultado = dao.actualizarEtapa(rolActualizar);

        if (resultado) {
            System.out.println("✅ La etapa ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ninguna etapa con el ID " + idMod);
        }

        sc.close();
    }
}