package Servicio;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Genera los archivos .xlsx que descarga la pantalla de Reportes y Consultas.
 *
 * <p>La clase no conoce la base de datos: recibe el titulo, las cabeceras y
 * las filas ya calculadas por el servlet, de modo que la misma logica sirve
 * para cualquier reporte futuro (programaciones, fichas, incidencias...).</p>
 */
public class ExcelServicio {

    /**
     * Escribe un libro de una sola hoja con titulo, cabecera y filas.
     *
     * @param out flujo de salida del archivo (el servlet cierra la respuesta)
     * @param titulo texto que aparece en la primera fila
     * @param cabeceras nombres de las columnas
     * @param filas valores de cada fila, en el mismo orden de las cabeceras
     * @throws IOException si falla la escritura del libro
     */
    public void escribirReporte(OutputStream out, String titulo,
            List<String> cabeceras, List<List<Object>> filas) throws IOException {

        try (Workbook libro = new XSSFWorkbook()) {
            Sheet hoja = libro.createSheet("Reporte");

            CellStyle estiloTitulo = crearEstiloTitulo(libro);
            CellStyle estiloCabecera = crearEstiloCabecera(libro);
            CellStyle estiloTexto = crearEstiloTexto(libro);

            int indice = 0;

            Row filaTitulo = hoja.createRow(indice++);
            Cell celdaTitulo = filaTitulo.createCell(0);
            celdaTitulo.setCellValue(titulo);
            celdaTitulo.setCellStyle(estiloTitulo);

            Row filaCabecera = hoja.createRow(indice++);
            for (int columna = 0; columna < cabeceras.size(); columna++) {
                Cell celda = filaCabecera.createCell(columna);
                celda.setCellValue(cabeceras.get(columna));
                celda.setCellStyle(estiloCabecera);
            }

            for (List<Object> datos : filas) {
                Row fila = hoja.createRow(indice++);
                for (int columna = 0; columna < datos.size(); columna++) {
                    Cell celda = fila.createCell(columna);
                    Object valor = datos.get(columna);
                    if (valor instanceof Number numero) {
                        celda.setCellValue(numero.doubleValue());
                    } else {
                        celda.setCellValue(valor == null ? "" : String.valueOf(valor));
                    }
                    celda.setCellStyle(estiloTexto);
                }
            }

            for (int columna = 0; columna < cabeceras.size(); columna++) {
                hoja.autoSizeColumn(columna);
            }
            hoja.createFreezePane(0, 2);

            libro.write(out);
        }
    }

    private CellStyle crearEstiloTitulo(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        Font fuente = libro.createFont();
        fuente.setBold(true);
        fuente.setFontHeightInPoints((short) 14);
        estilo.setFont(fuente);
        return estilo;
    }

    private CellStyle crearEstiloCabecera(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        Font fuente = libro.createFont();
        fuente.setBold(true);
        fuente.setColor(IndexedColors.WHITE.getIndex());
        estilo.setFont(fuente);
        estilo.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return estilo;
    }

    private CellStyle crearEstiloTexto(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        Font fuente = libro.createFont();
        fuente.setFontHeightInPoints((short) 10);
        estilo.setFont(fuente);
        return estilo;
    }
}
