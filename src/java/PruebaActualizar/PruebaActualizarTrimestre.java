package PruebaActualizar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import Controlador.TrimestreDAO;
import Modelo.Trimestre;
import java.util.Scanner;

/**
 * Prueba para actualizar una Ficha
 * @author isabe (mejorado por Grok)
 */
public class PruebaActualizarTrimestre {

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {  
            TrimestreDAO dao = new TrimestreDAO();

            System.out.println("=== MODIFICAR TRIMESTRE EXISTENTE ===");
            System.out.print("Ingrese el ID del trimestre que desea modificar: ");
            int idMod = sc.nextInt();
            sc.nextLine(); 

            Trimestre fichaActualizar = new Trimestre();
            fichaActualizar.setId_trimestre(idMod);

            System.out.print("Ingrese el nuevo numero de trimestre: ");
            fichaActualizar.setNum_trimestre(sc.nextInt());
            sc.nextLine();
            
         
            System.out.print("Ingrese la nueva descripcion de trimestre: ");
            fichaActualizar.setDescripcion(sc.nextLine());
            

            LocalDate fechaInicio = leerFecha(sc, "Ingrese la nueva fecha de inicio (yyyy-MM-dd): ");

            LocalDate fechaFin = null;
            while (fechaFin == null) {
                fechaFin = leerFecha(sc, "Ingrese la nueva fecha de fin (yyyy-MM-dd): ");
                if (fechaFin.isBefore(fechaInicio)) {
                    System.out.println("❌ La fecha de fin no puede ser anterior a la fecha de inicio.");
                    fechaFin = null;
                }
            }

            fichaActualizar.setFecha_inicio(fechaInicio);
            fichaActualizar.setFecha_fin(fechaFin);
       

            boolean resultado = dao.actualizarTrimestre(fichaActualizar);

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
}