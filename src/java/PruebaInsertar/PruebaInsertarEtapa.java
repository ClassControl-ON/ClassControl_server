package PruebaInsertar;

import java.util.Scanner;
import Modelo.Etapa;
import Controlador.EtapaDAO;

public class PruebaInsertarEtapa {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Etapa rol = new Etapa();
        EtapaDAO dao = new EtapaDAO();
        
        System.out.println("=== INSERTAR NUEVO ETAPA ===");
        
       
        System.out.print("Ingrese el ID de la etapa: ");
        rol.setId_etapa(sc.nextInt());
        sc.nextLine();
        
       
        System.out.print("Ingrese la descripcion de la etapa: ");
        rol.setDescripcion_Etapa (sc.nextLine());
        
        
        boolean resultado = dao.insertarEtapa(rol);
        
        if (resultado) {
            System.out.println("✅ La etapa se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar la etapa.");
        }
        
        sc.close();
    }
}