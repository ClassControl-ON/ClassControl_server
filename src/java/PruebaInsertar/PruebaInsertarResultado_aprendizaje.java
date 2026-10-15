package PruebaInsertar;

import java.util.Scanner;
import Modelo.Resultado_aprendizaje;
import Controlador.Resultado_aprendizajeDAO;

public class PruebaInsertarResultado_aprendizaje {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Resultado_aprendizaje rol = new Resultado_aprendizaje();
        Resultado_aprendizajeDAO dao = new Resultado_aprendizajeDAO();
        
        System.out.println("=== INSERTAR NUEVO RESULTADO DE APRENDIZAJE ===");
        
       
        System.out.print("Ingrese el ID del resultado de aprendizaje: ");
        rol.setId_resultado_aprendizaje(sc.nextInt());
        sc.nextLine();
        
        
        System.out.print("Ingrese el codigo del resultado de aprendizaje: ");
        rol.setCodigo_ResultadoAp(sc.nextInt());
        sc.nextLine();
        
       
        System.out.print("Ingrese la descripcion del resultado de aprendizaje: ");
        rol.setDescripcion_Resul (sc.nextLine());
        
        
        System.out.print("Ingrese el ID de la competencia: ");
        rol.setCompetencias_id_competencias(sc.nextInt());
        
        
        boolean resultado = dao.insertarResultado_aprendizaje(rol);
        
        if (resultado) {
            System.out.println("✅ El resultado de aprendizaje se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar el resultado de aprendizaje.");
        }
        
        sc.close();
    }
}