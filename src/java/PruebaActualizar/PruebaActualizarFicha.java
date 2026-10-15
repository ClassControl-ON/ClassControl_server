package PruebaActualizar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import Controlador.FichaDAO;
import Modelo.Ficha;
import java.util.Scanner;

/**
 * Prueba para actualizar una Ficha
 * @author isabe (mejorado por Grok)
 */
public class PruebaActualizarFicha {

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {  
            FichaDAO dao = new FichaDAO();

            System.out.println("=== MODIFICAR FICHA EXISTENTE ===");
            System.out.print("Ingrese el ID de la ficha que desea modificar: ");
            int idMod = sc.nextInt();
            sc.nextLine(); 

            Ficha fichaActualizar = new Ficha();
            fichaActualizar.setId_ficha(idMod);

            System.out.print("Ingrese el nuevo código de la ficha: ");
            fichaActualizar.setCodigo_ficha(sc.nextInt());
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

            fichaActualizar.setFecha_inicio(fechaInicio);
            fichaActualizar.setFecha_fin(fechaFin);

            
            System.out.print("Ingrese la nueva cantidad de aprendices: ");
            fichaActualizar.setCantidad_aprendices(sc.nextInt());
            
            System.out.print("Ingrese el nuevo ID del programa: ");
            fichaActualizar.setProgramas_idProgramas(sc.nextInt());
            sc.nextLine();

            System.out.print("Ingrese el nuevo ID de la jornada: ");
            fichaActualizar.setJornada_id_jornada(sc.nextInt());
            sc.nextLine();

            System.out.print("Ingrese el nuevo ID de la modalidad: ");
            fichaActualizar.setModalidad_id_modalidad(sc.nextInt());
            sc.nextLine();

            System.out.print("Ingrese el nuevo ID del nivel de formación: ");
            fichaActualizar.setNivel_formacion_id_nivel_formacion(sc.nextInt());
            sc.nextLine();

            System.out.print("Ingrese el nuevo ID de la sede: ");
            fichaActualizar.setSede_id_sede(sc.nextInt());
            sc.nextLine();

            System.out.print("Ingrese el nuevo ID del estado: ");
            fichaActualizar.setEstado_id_estado(sc.nextInt());
            sc.nextLine();

            System.out.print("Ingrese el nuevo ID de la etapa: ");
            fichaActualizar.setEtapa_id_etapa(sc.nextInt());
            sc.nextLine();

            boolean resultado = dao.actualizarFicha(fichaActualizar);

            if (resultado) {
                System.out.println("✅ La ficha ha sido actualizada correctamente.");
            } else {
                System.out.println("❌ Error: No se encontró ninguna ficha con el ID " + idMod + 
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