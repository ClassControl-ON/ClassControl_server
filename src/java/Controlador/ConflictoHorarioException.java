package Controlador;

/**
 * Se lanza cuando un INSERT o UPDATE sobre Programacion_Instructores viola
 * el índice único `ambiente_dia_hora_UNIQUE` (mismo Ambiente + mismo
 * dias_Semana + misma hora_inicio ya reservados por otra fila).
 *
 * Permite que el servlet distinga este caso de un error genérico de base
 * de datos y le muestre al usuario un mensaje claro, en vez de repetir el
 * mismo síntoma de "no me deja crear" que tenía el bug original.
 */
public class ConflictoHorarioException extends RuntimeException {

    public ConflictoHorarioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
