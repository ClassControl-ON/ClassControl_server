package PruebaInsertar;

import java.util.Scanner;
import Modelo.Competencias;
import Controlador.CompetenciasDAO;

public class PruebaInsertarCompetencias {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Competencias rol = new Competencias();
        CompetenciasDAO dao = new CompetenciasDAO();
        
        System.out.println("=== INSERTAR NUEVA COMPETENCIA ===");
        
       
        System.out.print("Ingrese el ID de la competencia: ");
        rol.setId_competencias(sc.nextInt());
        
       
        System.out.print("Ingrese el codigo de la competencia: ");
        rol.setCodigo_Competencias(sc.nextInt());
        sc.nextLine();
        
        
        System.out.print("Ingrese la descripcion de la competencia: ");
        rol.setDescripcion_Competencias (sc.nextLine());
        
        
        System.out.print("Ingrese el ID de la programacion de instructores: ");
        rol.setProgramas_idProgramas(sc.nextInt());
        
        
        boolean resultado = dao.insertarCompetencias(rol);
        
        if (resultado) {
            System.out.println("✅ La competencia se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar el rol.");
        }
        
        sc.close();
    }
}