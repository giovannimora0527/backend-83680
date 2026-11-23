package com.uniminuto.clinica.util;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;

/**
 * Arma la respuesta HTTP para descargar un archivo (PDF, Excel o CSV).
 */
public final class Descarga {

    /** Tipo de contenido de Excel. */
    public static final String EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    /** Tipo de contenido de PDF. */
    public static final String PDF = "application/pdf";
    /** Tipo de contenido CSV. */
    public static final String CSV = "text/csv";

    private Descarga() {
    }

    /**
     * Respuesta con el archivo como adjunto.
     *
     * @param contenido   bytes del archivo.
     * @param nombre      nombre del archivo.
     * @param tipoMime    tipo de contenido.
     * @return respuesta HTTP.
     */
    public static ResponseEntity<byte[]> archivo(byte[] contenido, String nombre, String tipoMime) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(tipoMime));
        headers.setContentDisposition(ContentDisposition.attachment().filename(nombre, StandardCharsets.UTF_8).build());
        return ResponseEntity.ok().headers(headers).body(contenido);
    }
}
