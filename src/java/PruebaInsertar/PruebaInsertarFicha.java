package PruebaInsertar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import Modelo.Ficha;
import Controlador.FichaDAO;

public class PruebaInsertarFicha {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Ficha rol = new Ficha(); 
        FichaDAO dao = new FichaDAO();
        
        System.out.println("=== INSERTAR NUEVO FICHA ===");
        
        System.out.print("Ingrese el ID de la ficha: ");
        rol.setId_ficha(sc.nextInt());
        sc.nextLine();
        
        System.out.print("Ingrese el codigo de la ficha: ");
        rol.setCodigo_ficha(sc.nextInt());

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
        
        
        System.out.print("Ingrese la cantidad de aprendices: ");
        rol.setCantidad_aprendices(sc.nextInt());
        
        
        System.out.print("Ingrese el ID del programa: ");
        rol.setProgramas_idProgramas(sc.nextInt());
        
        
        System.out.print("Ingrese el ID de la jornada: ");
        rol.setJornada_id_jornada(sc.nextInt());
        
        
        System.out.print("Ingrese el ID de la modalidad: ");
        rol.setModalidad_id_modalidad(sc.nextInt());
        
        
        System.out.print("Ingrese el ID del nivel de formacion: ");
        rol.setNivel_formacion_id_nivel_formacion(sc.nextInt());
        
        
        System.out.print("Ingrese el ID de la sede: ");
        rol.setSede_id_sede(sc.nextInt());
        
        
        System.out.print("Ingrese el ID del estado: ");
        rol.setEstado_id_estado(sc.nextInt());
        
        
        System.out.print("Ingrese el ID de la etapa: ");
        rol.setEtapa_id_etapa(sc.nextInt());
        
        
        boolean resultado = dao.insertarFicha(rol);
        
        if (resultado) {
            System.out.println("✅ La ficha se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar la ficha.");
        }
        
        sc.close();
    }
}