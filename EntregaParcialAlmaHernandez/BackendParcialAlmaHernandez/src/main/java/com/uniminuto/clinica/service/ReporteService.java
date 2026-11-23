package com.uniminuto.clinica.service;

import java.time.LocalDate;
import java.util.Map;

public interface ReporteService {
    Map<String, Object> resumen(LocalDate inicio, LocalDate fin);
    byte[] excel(LocalDate inicio, LocalDate fin) throws Exception;
    byte[] pdf(LocalDate inicio, LocalDate fin) throws Exception;
}
