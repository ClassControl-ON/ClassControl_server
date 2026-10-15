package PruebaInsertar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import Modelo.Programacion_Instructores;
import Controlador.Programacion_InstructoresDAO;

public class PruebaInsertarProgramacion_Instructores {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Programacion_Instructores rol = new Programacion_Instructores();  // Mejor nombre que "rol"
        Programacion_InstructoresDAO dao = new Programacion_InstructoresDAO();
        
        System.out.println("=== INSERTAR NUEVA PROGRAMACIÓN DE INSTRUCTORES ===");
        
        
        System.out.print("Ingrese el ID de la programación de instructores: ");
        rol.setId_programacion_Instructores(sc.nextInt());
        sc.nextLine();  
        
        
        System.out.print("Ingrese las observaciones: ");
        rol.setObservaciones(sc.nextLine().trim());
        
        
        LocalDate fechaInicio = null;
        while (fechaInicio == null) {
            System.out.print("Ingrese la fecha de inicio (yyyy-MM-dd): ");
            String input = sc.nextLine().trim();
            try {
                fechaInicio = LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
                rol.setFecha_inicial_Prog(fechaInicio);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato inválido. Usa yyyy-MM-dd (ej: 2026-03-01). Intenta de nuevo.");
            }
        }
        
        
        LocalDate fechaFin = null;
        while (fechaFin == null) {
            System.out.print("Ingrese la fecha de fin (yyyy-MM-dd): ");
            String input = sc.nextLine().trim();
            try {
                fechaFin = LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
                if (fechaFin.isBefore(fechaInicio)) {
                    System.out.println("❌ La fecha fin no puede ser anterior a la fecha inicio.");
                    fechaFin = null;
                    continue;
                }
                rol.setFecha_fin_Prog(fechaFin);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato inválido. Usa yyyy-MM-dd. Intenta de nuevo.");
            }
        }
        
        
        LocalTime horaInicio = null;
        while (horaInicio == null) {
            System.out.print("Ingrese la hora de inicio (formato HH:mm, ej: 08:00): ");
            String input = sc.nextLine().trim();
            try {
                horaInicio = LocalTime.parse(input, DateTimeFormatter.ofPattern("HH:mm"));
                rol.setHora_inicio(horaInicio);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato inválido. Usa HH:mm (ej: 14:30, 07:15). Intenta de nuevo.");
            }
        }
        
       
        LocalTime horaFin = null;
        while (horaFin == null) {
            System.out.print("Ingrese la hora de fin (formato HH:mm, ej: 12:00): ");
            String input = sc.nextLine().trim();
            try {
                horaFin = LocalTime.parse(input, DateTimeFormatter.ofPattern("HH:mm"));
                if (horaFin.isBefore(horaInicio)) {
                    System.out.println("❌ La hora de fin no puede ser anterior a la hora de inicio.");
                    horaFin = null;
                    continue;
                }
                rol.setHora_fin(horaFin);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato inválido. Usa HH:mm. Intenta de nuevo.");
            }
        }
        
   
        System.out.print("Ingrese los días de la semana (ej: Lunes,Martes,Miercoles): ");
        rol.setDias_Semana(sc.nextLine().trim());
        
        
        System.out.print("Ingrese el ID de la ficha: ");
        rol.setFicha_id_ficha(sc.nextInt());
        sc.nextLine();
        
        
        System.out.print("Ingrese el ID del usuario: ");
        rol.setUsuarios_id_usuarios(sc.nextInt());
        sc.nextLine();
        
        
        System.out.print("Ingrese el ID del ambiente: ");
        rol.setAmbientes_id_ambientes(sc.nextInt());
        sc.nextLine();
        
        
        System.out.print("Ingrese el ID del trimestre: ");
        rol.setTrimestre_id_trimestre(sc.nextInt());
        sc.nextLine();
        
        
        System.out.print("Ingrese el ID del estado: ");
        rol.setEstado_id_estado(sc.nextInt());
        sc.nextLine();
        
        
        boolean resultado = dao.InsertarProgramacion_Instructores(rol);
        
        if (resultado) {
            System.out.println("✅ La programación se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar la programación.");
        }
        
        sc.close();
    }
}