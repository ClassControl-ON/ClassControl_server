package PruebaInsertar;

import java.util.Scanner;
import Modelo.Nivel_formacion;
import Controlador.Nivel_formacionDAO;

public class PruebaInsertarNivel_formacion {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Nivel_formacion rol = new Nivel_formacion();
        Nivel_formacionDAO dao = new Nivel_formacionDAO();
        
        System.out.println("=== INSERTAR NUEVO NIVEL DE FORMACION ===");
        
       
        System.out.print("Ingrese el ID del nivel de formacion: ");
        rol.setId_nivel_formacion(sc.nextInt());
        sc.nextLine();
        
       
        System.out.print("Ingrese la descripcion del nivel de formacion: ");
        rol.setDescripcion_Nivel_Formacion (sc.nextLine());
        
        
        boolean resultado = dao.insertarNivel_formacion(rol);
        
        if (resultado) {
            System.out.println("✅ El nivel de formacion se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar el nivel de formacion.");
        }
        
        sc.close();
    }
}