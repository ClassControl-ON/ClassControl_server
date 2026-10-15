package PruebaInsertar;

import java.util.Scanner;
import Modelo.Jornada;
import Controlador.JornadaDAO;

public class PruebaInsertarJornada {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Jornada rol = new Jornada();
        JornadaDAO dao = new JornadaDAO();
        
        System.out.println("=== INSERTAR NUEVA JORNADA ===");
        
       
        System.out.print("Ingrese el ID de la jornada: ");
        rol.setId_jornada(sc.nextInt());
        sc.nextLine();
        
       
        System.out.print("Ingrese la descripcion de la jornada: ");
        rol.setDescripcion_Jornada (sc.nextLine());
        
        
        boolean resultado = dao.insertarJornada(rol);
        
        if (resultado) {
            System.out.println("✅ La jornada se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar el rol.");
        }
        
        sc.close();
    }
}