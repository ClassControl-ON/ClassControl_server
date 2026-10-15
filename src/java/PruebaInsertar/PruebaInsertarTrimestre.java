package PruebaInsertar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import Modelo.Trimestre;
import Controlador.TrimestreDAO;

public class PruebaInsertarTrimestre {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Trimestre rol = new Trimestre(); 
        TrimestreDAO dao = new TrimestreDAO();
        
        System.out.println("=== INSERTAR NUEVO TRIMESTRE ===");
        
        System.out.print("Ingrese el ID del trimestre: ");
        rol.setId_trimestre(sc.nextInt());
        
        
        System.out.print("Ingrese el número de trimestre : ");
        rol.setNum_trimestre(sc.nextInt());
        sc.nextLine();
        
        System.out.print("Ingrese la descripción del trimestre: ");
        rol.setDescripcion(sc.nextLine().trim());
        
        // Fecha de inicio - con manejo de errores
        LocalDate fechaInicio = null;
        while (fechaInicio == null) {
            System.out.print("Ingrese la fecha de inicio (formato: yyyy-MM-dd): ");
            String inputInicio = sc.nextLine().trim();
            try {
                fechaInicio = LocalDate.parse(inputInicio, DateTimeFormatter.ISO_LOCAL_DATE);
                rol.setFecha_inicio(fechaInicio);
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
                rol.setFecha_fin(fechaFin);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato inválido. Usa yyyy-MM-dd. Intenta de nuevo.");
            }
        }
        
        boolean resultado = dao.insertarTrimestre(rol);
        
        if (resultado) {
            System.out.println("✅ El trimestre se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar el trimestre.");
        }
        
        sc.close();
    }
}