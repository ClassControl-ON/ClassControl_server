package PruebaActualizar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import Controlador.Programacion_InstructoresDAO;
import Modelo.Programacion_Instructores;
import java.util.Scanner;

/**
 * Prueba para actualizar una Ficha
 * @author isabe (mejorado por Grok)
 */
public class PruebaActualizarProgramacion_Instructores{

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {  
            Programacion_InstructoresDAO dao = new Programacion_InstructoresDAO();

            System.out.println("=== MODIFICAR PROGRAMACION INSTRUCTORES ===");
            System.out.print("Ingrese el ID de la programacion de instructores que desea modificar: ");
            int idMod = sc.nextInt();
            sc.nextLine(); 

            Programacion_Instructores fichaActualizar = new Programacion_Instructores();
            fichaActualizar.setId_programacion_Instructores(idMod);

            System.out.print("Ingrese observacion de la programacion: ");
            fichaActualizar.setObservaciones(sc.nextLine());
            sc.nextLine();
            
         
            LocalDate fechaInicio = leerFecha(sc, "Ingrese la nueva fecha de inicio (yyyy-MM-dd): ");

            LocalDate fechaFin = null;
            while (fechaFin == null) {
                fechaFin = leerFecha(sc, "Ingrese la nueva fecha de fin (yyyy-MM-dd): ");
                if (fechaFin.isBefore(fechaInicio)) {
                    System.out.println("❌ La fecha de fin no puede ser anterior a la fecha de inicio.");
                    fechaFin = null;
                }
            }

            fichaActualizar.setFecha_inicial_Prog(fechaInicio);
            fichaActualizar.setFecha_fin_Prog(fechaFin);
            
            
            System.out.print("Ingrese la nueva dia de la programacion: ");
            fichaActualizar.setDias_Semana(sc.nextLine());
            
            
            LocalTime horaInicio = leerHora(sc, "Ingrese la nueva hora de inicio (HH:mm): ");
            fichaActualizar.setHora_inicio(horaInicio);


            LocalTime horaFin = null;
            while (horaFin == null) {
                horaFin = leerHora(sc, "Ingrese la nueva hora de fin (HH:mm): ");
    
                if (horaFin.isBefore(horaInicio)) {
                    System.out.println("❌ La hora de fin no puede ser anterior a la hora de inicio.");
                    horaFin = null;
                } else if (horaFin.equals(horaInicio)) {
                    System.out.println("❌ La hora de fin no puede ser igual a la hora de inicio.");
                    horaFin = null;
                }
            }
            fichaActualizar.setHora_fin(horaFin);
            
            
            System.out.print("Ingrese el nuevo ID de la ficha: ");
            fichaActualizar.setFicha_id_ficha(sc.nextInt());
            
        
            System.out.print("Ingrese el nuevo ID del usuario: ");
            fichaActualizar.setUsuarios_id_usuarios(sc.nextInt());
            
            
            System.out.print("Ingrese el nuevo ID del ambiente: ");
            fichaActualizar.setAmbientes_id_ambientes(sc.nextInt());
            
            
            
            
            System.out.print("Ingrese el nuevo ID del trimestre: ");
            fichaActualizar.setTrimestre_id_trimestre(sc.nextInt());
            
            
            System.out.print("Ingrese el nuevo ID del estado: ");
            fichaActualizar.setEstado_id_estado(sc.nextInt());
            

            boolean resultado = dao.actualizarProgramacion_Instructores(fichaActualizar);

            if (resultado) {
                System.out.println("✅ El trimestre ha sido actualizado correctamente.");
            } else {
                System.out.println("❌ Error: No se encontró ningun trimestre con el ID " + idMod + 
                                 " o ocurrió un error en la actualización.");
            }

        } catch (Exception e) {
            System.out.println("❌ Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    private static LocalDate leerFecha(Scanner sc, String mensaje) {
        LocalDate fecha = null;
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        while (fecha == null) {
            System.out.print(mensaje);
            String input = sc.nextLine().trim();

            try {
                fecha = LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato inválido. Usa el formato yyyy-MM-dd (ejemplo: 2026-03-28). Intenta de nuevo.");
            }
        }
        return fecha;
    }
    
    private static LocalTime leerHora(Scanner sc, String mensaje) {
    LocalTime hora = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    
    while (hora == null) {
        System.out.print(mensaje);
        String input = sc.nextLine().trim();
        
        try {
            hora = LocalTime.parse(input, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("❌ Formato inválido. Usa HH:mm (ejemplo: 08:30, 14:45, 23:00).");
        }
    }
    return hora;
}
    
}