package PruebaInsertar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import Modelo.VinculacionLaboral;
import Controlador.VinculacionLaboralDAO;

public class PruebaInsertarVinculacionLaboral {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        VinculacionLaboral rol = new VinculacionLaboral(); 
        VinculacionLaboralDAO dao = new VinculacionLaboralDAO();
        
        System.out.println("=== INSERTAR NUEVA VINCULACION LABORAL ===");
        
        System.out.print("Ingrese el ID de la vinculacion laboral: ");
        rol.setId_vinculacion_Laboral(sc.nextInt());
        sc.nextLine();
        
        System.out.print("Ingrese la descripcion de la vinculacin laboral : ");
        rol.setDescripcion(sc.nextLine());
        
        System.out.print("Ingrese el numero de contrato: ");
        rol.setNumero_Contrato(sc.nextLine().trim());
        
        // Fecha de inicio - con manejo de errores
        LocalDate fechaInicio = null;
        while (fechaInicio == null) {
            System.out.print("Ingrese la fecha de inicio (formato: yyyy-MM-dd): ");
            String inputInicio = sc.nextLine().trim();
            try {
                fechaInicio = LocalDate.parse(inputInicio, DateTimeFormatter.ISO_LOCAL_DATE);
                rol.setFecha_Inicio(fechaInicio);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato inválido. Usa yyyy-MM-dd. Intenta de nuevo.");
            }
        }
        
        
        LocalDate fechaFin = null;
        while (fechaFin == null) {
            System.out.print("Ingrese la fecha de fin (formato: yyyy-MM-dd): ");
            String inputFin = sc.nextLine().trim();
            try {
                fechaFin = LocalDate.parse(inputFin, DateTimeFormatter.ISO_LOCAL_DATE);
                if (fechaFin.isBefore(fechaInicio)) {
                    System.out.println("❌ La fecha fin no puede ser anterior a la fecha inicio. Intenta de nuevo.");
                    fechaFin = null;
                    continue;
                }
                rol.setFecha_Fin(fechaFin);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato inválido. Usa yyyy-MM-dd. Intenta de nuevo.");
            }
        }
        
        System.out.print("Ingrese el ID del usuario: ");
        rol.setUsuarios_id_usuarios(sc.nextInt());
        
        
        boolean resultado = dao.insertarVinculacionLaboral(rol);
        
        if (resultado) {
            System.out.println("✅ La vinculacion laboral se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar el trimestre.");
        }
        
        sc.close();
    }
}