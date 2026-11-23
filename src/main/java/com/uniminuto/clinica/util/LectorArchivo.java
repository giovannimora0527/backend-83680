package com.uniminuto.clinica.util;

import com.uniminuto.clinica.exception.BadRequestException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lee archivos .csv o .xlsx y los convierte en una lista de filas (encabezado -> valor).
 * Valida que vengan todos los encabezados esperados.
 */
public final class LectorArchivo {

    private LectorArchivo() {
    }

    /**
     * Lee el archivo.
     *
     * @param archivo     archivo subido.
     * @param encabezados encabezados obligatorios.
     * @return filas con sus valores (sin la fila de encabezados).
     */
    public static List<Map<String, String>> leer(MultipartFile archivo, List<String> encabezados) {
        String nombre = archivo.getOriginalFilename() == null ? "" : archivo.getOriginalFilename().toLowerCase();
        try (InputStream entrada = archivo.getInputStream()) {
            List<List<String>> filas;
            if (nombre.endsWith(".csv")) {
                filas = leerCsv(entrada);
            } else if (nombre.endsWith(".xlsx")) {
                filas = leerExcel(entrada);
            } else {
                throw new BadRequestException("Solo se permiten archivos .csv o .xlsx");
            }
            return convertir(filas, encabezados);
        } catch (IOException e) {
            throw new BadRequestException("No se pudo leer el archivo");
        }
    }

    /**
     * Valida los encabezados y arma los mapas por fila.
     */
    private static List<Map<String, String>> convertir(List<List<String>> filas, List<String> encabezados) {
        if (filas.isEmpty()) {
            throw new BadRequestException("El archivo está vacío");
        }
        List<String> cabecera = filas.get(0).stream().map(c -> c.trim().toLowerCase()).toList();
        List<String> faltantes = encabezados.stream().filter(e -> !cabecera.contains(e)).toList();
        if (!faltantes.isEmpty()) {
            throw new BadRequestException("Estructura inválida. Faltan los encabezados: " + String.join(", ", faltantes)
                    + ". Descargue la plantilla para ver el formato.");
        }
        List<Map<String, String>> resultado = new ArrayList<>();
        for (int i = 1; i < filas.size(); i++) {
            List<String> valores = filas.get(i);
            if (valores.stream().allMatch(String::isBlank)) {
                continue;
            }
            Map<String, String> registro = new LinkedHashMap<>();
            for (String encabezado : encabezados) {
                int pos = cabecera.indexOf(encabezado);
                registro.put(encabezado, pos < valores.size() ? valores.get(pos).trim() : "");
            }
            resultado.add(registro);
        }
        return resultado;
    }

    /**
     * Lee un CSV separado por coma o punto y coma (soporta valores entre comillas).
     */
    private static List<List<String>> leerCsv(InputStream entrada) throws IOException {
        List<List<String>> filas = new ArrayList<>();
        BufferedReader lector = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8));
        String linea;
        Character separador = null;
        while ((linea = lector.readLine()) != null) {
            if (filas.isEmpty() && linea.startsWith("﻿")) {
                linea = linea.substring(1);
            }
            if (separador == null) {
                separador = linea.contains(";") && !linea.contains(",") ? ';' : ',';
            }
            filas.add(partirLinea(linea, separador));
        }
        return filas;
    }

    /**
     * Parte una linea CSV respetando las comillas.
     */
    private static List<String> partirLinea(String linea, char separador) {
        List<String> valores = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean entreComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '"') {
                if (entreComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    actual.append('"');
                    i++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if (c == separador && !entreComillas) {
                valores.add(actual.toString());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        valores.add(actual.toString());
        return valores;
    }

    /**
     * Lee la primera hoja de un Excel. Las fechas se convierten a AAAA-MM-DD.
     */
    private static List<List<String>> leerExcel(InputStream entrada) throws IOException {
        List<List<String>> filas = new ArrayList<>();
        DataFormatter formato = new DataFormatter();
        try (Workbook libro = WorkbookFactory.create(entrada)) {
            Sheet hoja = libro.getSheetAt(0);
            int columnas = hoja.getRow(hoja.getFirstRowNum()) == null ? 0 : hoja.getRow(hoja.getFirstRowNum()).getLastCellNum();
            for (Row fila : hoja) {
                List<String> valores = new ArrayList<>();
                for (int c = 0; c < columnas; c++) {
                    Cell celda = fila.getCell(c);
                    if (celda == null) {
                        valores.add("");
                    } else if (celda.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(celda)) {
                        valores.add(celda.getLocalDateTimeCellValue().toLocalDate().toString());
                    } else {
                        valores.add(formato.formatCellValue(celda));
                    }
                }
                filas.add(valores);
            }
        }
        return filas;
    }
}
