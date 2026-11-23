package com.uniminuto.clinica.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.uniminuto.clinica.service.ReporteService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> resumen(LocalDate inicio, LocalDate fin) {
        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("inicio", inicio);
        resumen.put("fin", fin);
        resumen.put("facturas", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM factura WHERE DATE(fecha_generacion)>=? AND DATE(fecha_generacion)<=?",
                Long.class, inicio, fin));
        resumen.put("totalFacturado", jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(total),0) FROM factura WHERE estado<>\"ANULADA\" AND DATE(fecha_generacion)>=? AND DATE(fecha_generacion)<=?",
                java.math.BigDecimal.class, inicio, fin));
        resumen.put("pagosRegistrados", jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(monto),0) FROM pago WHERE DATE(fecha_pago)>=? AND DATE(fecha_pago)<=?",
                java.math.BigDecimal.class, inicio, fin));
        resumen.put("citas", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM cita WHERE DATE(fecha_hora)>=? AND DATE(fecha_hora)<=?",
                Long.class, inicio, fin));
        resumen.put("movimientosInventario", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM movimiento_inventario WHERE DATE(fecha_movimiento)>=? AND DATE(fecha_movimiento)<=?",
                Long.class, inicio, fin));
        return resumen;
    }

    @Override
    public byte[] excel(LocalDate inicio, LocalDate fin) throws Exception {
        Map<String, Object> resumen = resumen(inicio, fin);
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte financiero");
            int rowIndex = 0;
            for (Map.Entry<String, Object> entry : resumen.entrySet()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(String.valueOf(entry.getValue()));
            }
            workbook.write(output);
            return output.toByteArray();
        }
    }

    @Override
    public byte[] pdf(LocalDate inicio, LocalDate fin) throws Exception {
        Map<String, Object> resumen = resumen(inicio, fin);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();
            document.add(new Paragraph("Reporte administrativo y financiero"));
            document.add(new Paragraph("Periodo: " + inicio + " a " + fin));
            PdfPTable table = new PdfPTable(2);
            table.addCell("Concepto");
            table.addCell("Valor");
            for (Map.Entry<String, Object> entry : resumen.entrySet()) {
                table.addCell(entry.getKey());
                table.addCell(String.valueOf(entry.getValue()));
            }
            document.add(table);
            document.close();
            return output.toByteArray();
        }
    }
}
