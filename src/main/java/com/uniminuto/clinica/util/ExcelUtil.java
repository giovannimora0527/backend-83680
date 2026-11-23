package com.uniminuto.clinica.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Genera archivos Excel (.xlsx) con encabezado, fecha de generacion, tabla y fila de totales.
 */
public final class ExcelUtil {

    private ExcelUtil() {
    }

    /**
     * Crea un Excel con una tabla.
     *
     * @param titulo    titulo del reporte.
     * @param subtitulo filtros aplicados (rango de fechas, etc.).
     * @param columnas  nombres de las columnas.
     * @param filas     datos (cada fila es una lista de valores).
     * @param totales   fila de totales (puede ser null).
     * @return bytes del archivo.
     */
    public static byte[] tabla(String titulo, String subtitulo, List<String> columnas,
                               List<List<Object>> filas, List<Object> totales) {
        try (XSSFWorkbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Sheet hoja = libro.createSheet("Reporte");

            Font negrita = libro.createFont();
            negrita.setBold(true);
            CellStyle estiloTitulo = libro.createCellStyle();
            Font fuenteTitulo = libro.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 14);
            estiloTitulo.setFont(fuenteTitulo);

            CellStyle estiloEncabezado = libro.createCellStyle();
            estiloEncabezado.setFont(negrita);
            estiloEncabezado.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle estiloTotal = libro.createCellStyle();
            estiloTotal.setFont(negrita);

            CellStyle estiloNumero = libro.createCellStyle();
            estiloNumero.setDataFormat(libro.createDataFormat().getFormat("#,##0.00"));

            int r = 0;
            hoja.createRow(r++).createCell(0).setCellValue("Clínica Veterinaria");
            Cell celdaTitulo = hoja.createRow(r++).createCell(0);
            celdaTitulo.setCellValue(titulo);
            celdaTitulo.setCellStyle(estiloTitulo);
            if (subtitulo != null && !subtitulo.isBlank()) {
                hoja.createRow(r++).createCell(0).setCellValue(subtitulo);
            }
            hoja.createRow(r++).createCell(0).setCellValue("Generado: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            // Las filas del titulo se combinan a lo ancho de la tabla para que el
            // texto largo no ensanche la primera columna al ajustar los anchos
            if (columnas.size() > 1) {
                for (int i = 0; i < r; i++) {
                    hoja.addMergedRegion(new CellRangeAddress(i, i, 0, columnas.size() - 1));
                }
            }
            r++;

            Row encabezado = hoja.createRow(r++);
            for (int c = 0; c < columnas.size(); c++) {
                Cell celda = encabezado.createCell(c);
                celda.setCellValue(columnas.get(c));
                celda.setCellStyle(estiloEncabezado);
            }
            for (List<Object> fila : filas) {
                escribirFila(hoja.createRow(r++), fila, estiloNumero, null);
            }
            if (totales != null) {
                escribirFila(hoja.createRow(r), totales, estiloNumero, estiloTotal);
            }
            for (int c = 0; c < columnas.size(); c++) {
                hoja.autoSizeColumn(c);
            }
            libro.write(salida);
            return salida.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo generar el Excel", e);
        }
    }

    /**
     * Escribe una fila respetando el tipo de dato (numeros como numeros).
     */
    private static void escribirFila(Row fila, List<Object> valores, CellStyle estiloNumero, CellStyle estiloFila) {
        for (int c = 0; c < valores.size(); c++) {
            Object valor = valores.get(c);
            Cell celda = fila.createCell(c);
            if (valor instanceof BigDecimal numero) {
                celda.setCellValue(numero.doubleValue());
                celda.setCellStyle(estiloNumero);
            } else if (valor instanceof Number numero) {
                celda.setCellValue(numero.doubleValue());
            } else {
                celda.setCellValue(valor == null ? "" : valor.toString());
            }
            if (estiloFila != null && !(valor instanceof BigDecimal)) {
                celda.setCellStyle(estiloFila);
            }
        }
    }
}
