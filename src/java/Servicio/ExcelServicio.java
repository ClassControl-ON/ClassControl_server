package Servicio;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelServicio {
    public void escribirReporte(OutputStream out) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            wb.createSheet();
            wb.write(out);
        }
    }
}
