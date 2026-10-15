package PruebaInsertar;

import java.util.Scanner;
import Modelo.Roles;
import Controlador.RolesDAO;

public class PruebaInsertarRoles {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Roles rol = new Roles();
        RolesDAO dao = new RolesDAO();
        
        System.out.println("=== INSERTAR NUEVO ROL ===");
        
       
        System.out.print("Ingrese el ID del rol: ");
        rol.setId_roles(sc.nextInt());
        sc.nextLine();
        
       
        System.out.print("Ingrese la descripcion del rol: ");
        rol.setDescripcion_Roles (sc.nextLine());
        
        
        boolean resultado = dao.insertarRoles(rol);
        
        if (resultado) {
            System.out.println("✅ El rol se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar el rol.");
        }
        
        sc.close();
    }
}