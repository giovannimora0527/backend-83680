package com.uniminuto.clinica.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Generador sencillo de PDF: encabezado de la clinica, secciones de datos,
 * tablas, totales y pie. Se usa para formulas, prefacturas, facturas,
 * comprobantes de despacho y reportes.
 */
public final class PdfUtil {

    /** Verde de la clinica. */
    private static final Color VERDE = new Color(6, 95, 70);
    private static final Font TITULO = new Font(Font.HELVETICA, 16, Font.BOLD, VERDE);
    private static final Font SUBTITULO = new Font(Font.HELVETICA, 11, Font.BOLD, VERDE);
    private static final Font NORMAL = new Font(Font.HELVETICA, 9, Font.NORMAL);
    private static final Font NEGRITA = new Font(Font.HELVETICA, 9, Font.BOLD);
    private static final Font BLANCA = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
    private static final Font PEQUENA = new Font(Font.HELVETICA, 7, Font.ITALIC, Color.GRAY);

    /** Datos fijos de la clinica que salen en todos los documentos. */
    public static final String CLINICA_NOMBRE = "Clínica Veterinaria";
    public static final String CLINICA_DATOS = "NIT 900.123.456-7  ·  Calle 80 # 10-20, Bogotá  ·  Tel. (601) 555 0101";

    private PdfUtil() {
    }

    /**
     * Formatea un valor como pesos colombianos.
     *
     * @param valor valor.
     * @return texto tipo $ 12.500,00
     */
    public static String pesos(BigDecimal valor) {
        DecimalFormat formato = new DecimalFormat("$ #,##0.00", DecimalFormatSymbols.getInstance(Locale.of("es", "CO")));
        return formato.format(valor == null ? BigDecimal.ZERO : valor.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Constructor de documentos PDF.
     */
    public static class Documento {
        private final String titulo;
        private final List<Object[]> bloques = new ArrayList<>();
        private boolean horizontal;

        /**
         * @param titulo titulo del documento.
         */
        public Documento(String titulo) {
            this.titulo = titulo;
        }

        /** Usa hoja horizontal (para reportes con muchas columnas). */
        public Documento horizontal() {
            this.horizontal = true;
            return this;
        }

        /**
         * Seccion de datos en dos columnas (etiqueta: valor).
         *
         * @param subtitulo titulo de la seccion.
         * @param pares     pares {etiqueta, valor}.
         */
        public Documento seccion(String subtitulo, List<String[]> pares) {
            bloques.add(new Object[]{"seccion", subtitulo, pares});
            return this;
        }

        /**
         * Tabla con encabezado.
         *
         * @param subtitulo titulo de la tabla (puede ser null).
         * @param columnas  encabezados.
         * @param filas     filas de texto.
         */
        public Documento tabla(String subtitulo, List<String> columnas, List<List<String>> filas) {
            bloques.add(new Object[]{"tabla", subtitulo, columnas, filas});
            return this;
        }

        /**
         * Totales alineados a la derecha.
         *
         * @param totales pares {etiqueta, valor}.
         */
        public Documento totales(List<String[]> totales) {
            bloques.add(new Object[]{"totales", totales});
            return this;
        }

        /**
         * Parrafo de texto.
         *
         * @param subtitulo titulo (puede ser null).
         * @param texto     contenido.
         */
        public Documento texto(String subtitulo, String texto) {
            bloques.add(new Object[]{"texto", subtitulo, texto});
            return this;
        }

        /**
         * Genera el PDF.
         *
         * @return bytes del archivo.
         */
        @SuppressWarnings("unchecked")
        public byte[] generar() {
            Rectangle hoja = horizontal ? PageSize.LETTER.rotate() : PageSize.LETTER;
            Document documento = new Document(hoja, 36, 36, 36, 36);
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            try {
                PdfWriter.getInstance(documento, salida);
                documento.open();
                Paragraph clinica = new Paragraph(CLINICA_NOMBRE, TITULO);
                documento.add(clinica);
                documento.add(new Paragraph(CLINICA_DATOS, PEQUENA));
                Paragraph tituloDoc = new Paragraph(titulo, SUBTITULO);
                tituloDoc.setSpacingBefore(8);
                documento.add(tituloDoc);
                documento.add(new Paragraph("Generado: "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), PEQUENA));

                for (Object[] b : bloques) {
                    switch ((String) b[0]) {
                        case "seccion" -> agregarSeccion(documento, (String) b[1], (List<String[]>) b[2]);
                        case "tabla" -> agregarTabla(documento, (String) b[1], (List<String>) b[2], (List<List<String>>) b[3]);
                        case "totales" -> agregarTotales(documento, (List<String[]>) b[1]);
                        default -> agregarTexto(documento, (String) b[1], (String) b[2]);
                    }
                }
                documento.close();
            } catch (DocumentException e) {
                throw new IllegalStateException("No se pudo generar el PDF", e);
            }
            return salida.toByteArray();
        }

        private void subtitulo(Document documento, String texto) throws DocumentException {
            if (texto != null) {
                Paragraph p = new Paragraph(texto, SUBTITULO);
                p.setSpacingBefore(10);
                p.setSpacingAfter(4);
                documento.add(p);
            }
        }

        private void agregarSeccion(Document documento, String subtitulo, List<String[]> pares) throws DocumentException {
            subtitulo(documento, subtitulo);
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{1.2f, 2.3f, 1.2f, 2.3f});
            for (String[] par : pares) {
                tabla.addCell(celda(par[0], NEGRITA, null));
                tabla.addCell(celda(par[1], NORMAL, null));
            }
            if (pares.size() % 2 != 0) {
                tabla.addCell(celda("", NORMAL, null));
                tabla.addCell(celda("", NORMAL, null));
            }
            documento.add(tabla);
        }

        private void agregarTabla(Document documento, String subtitulo, List<String> columnas, List<List<String>> filas)
                throws DocumentException {
            subtitulo(documento, subtitulo);
            PdfPTable tabla = new PdfPTable(columnas.size());
            tabla.setWidthPercentage(100);
            tabla.setHeaderRows(1);
            for (String columna : columnas) {
                PdfPCell c = celda(columna, BLANCA, VERDE);
                c.setPadding(4);
                tabla.addCell(c);
            }
            if (filas.isEmpty()) {
                PdfPCell vacia = celda("Sin registros", NORMAL, null);
                vacia.setColspan(columnas.size());
                tabla.addCell(vacia);
            }
            boolean par = false;
            for (List<String> fila : filas) {
                for (String valor : fila) {
                    PdfPCell c = celda(valor, NORMAL, par ? new Color(240, 248, 245) : null);
                    c.setBorderColor(new Color(220, 220, 220));
                    c.setBorder(Rectangle.BOTTOM);
                    tabla.addCell(c);
                }
                par = !par;
            }
            documento.add(tabla);
        }

        private void agregarTotales(Document documento, List<String[]> totales) throws DocumentException {
            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(45);
            tabla.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tabla.setSpacingBefore(6);
            for (String[] total : totales) {
                PdfPCell etiqueta = celda(total[0], NEGRITA, null);
                PdfPCell valor = celda(total[1], NEGRITA, null);
                valor.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tabla.addCell(etiqueta);
                tabla.addCell(valor);
            }
            documento.add(tabla);
        }

        private void agregarTexto(Document documento, String subtitulo, String texto) throws DocumentException {
            subtitulo(documento, subtitulo);
            Paragraph p = new Paragraph(texto == null || texto.isBlank() ? "-" : texto, NORMAL);
            // Espacio para que el texto no quede pegado a la tabla que sigue
            p.setSpacingAfter(8);
            documento.add(p);
        }

        private PdfPCell celda(String texto, Font fuente, Color fondo) {
            PdfPCell c = new PdfPCell(new Phrase(texto == null ? "" : texto, fuente));
            c.setBorder(Rectangle.NO_BORDER);
            c.setPadding(3);
            if (fondo != null) {
                c.setBackgroundColor(fondo);
            }
            return c;
        }
    }
}
