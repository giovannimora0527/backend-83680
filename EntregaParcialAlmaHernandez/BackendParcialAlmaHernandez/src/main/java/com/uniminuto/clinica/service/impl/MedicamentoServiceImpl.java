package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.model.MedicamentoRq;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.service.MedicamentoService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import com.uniminuto.clinica.model.CargaInventarioConfirmarRq;

@Service
public class MedicamentoServiceImpl implements MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Override
    public List<Medicamento> listarMedicamentos() {
        return medicamentoRepository.findAll()
                .stream()
                .sorted((m1, m2) -> m2.getFechaCompra().compareTo(m1.getFechaCompra()))
                .toList();
    }

    @Override
    public RespuestaRs guardarMedicamento(MedicamentoRq medicamentoRq) throws BadRequestException {
        // Paso 1. Validar el formulario
        this.validarFormulario(medicamentoRq);
        // Paso 2. Consultar si el medicamento ya existe por nombre
        Optional<Medicamento> optMedicamento = medicamentoRepository
                .findByNombre(medicamentoRq.getNombre());

        // Paso 3. Si existe lanzo error
        if (optMedicamento.isPresent()) {
            throw new BadRequestException("El medicamento ya existe");
        }
        // Paso 4. Si no existe, creo el medicamento y lo guardo
        Medicamento nuevo = this.mapearAMedicamento(medicamentoRq);
        medicamentoRepository.save(nuevo);

        // Paso 5. Retorno la respuesta
        RespuestaRs rta = new RespuestaRs();
        rta.setMensaje("Medicamento creado exitosamente");
        rta.setStatus(200);

        return rta;
    }

    @Override
    public RespuestaRs actualizarMedicamento(MedicamentoRq medicamentoRq)
            throws BadRequestException {
        this.validarFormulario(medicamentoRq);
        // Paso 1. Consultar si el campo id existe y viene en el request
        if (medicamentoRq.getId() == null) {
            throw new BadRequestException("El id del medicamento es obligatorio");
        }
        // Paso 2. Consultar si el medicamento existe por id
        Optional<Medicamento> optMedicamento = medicamentoRepository
                .findById(medicamentoRq.getId());
        // Paso 3. Si no existe lanzo error
        if (!optMedicamento.isPresent()) {
            throw new BadRequestException("El medicamento no existe y no se puede actualizar");
        }
        // Paso 4. Si existe voy y valido que el atributo nombre cambie y si cambia lo consulto por nombre
        Medicamento medicamentoActual = optMedicamento.get();
        if (!medicamentoActual.getNombre()
                .toLowerCase().equals(medicamentoRq.getNombre().toLowerCase())) {
            Optional<Medicamento> optMedicamentoPorNombre = medicamentoRepository
                    .findByNombre(medicamentoRq.getNombre());
            // Paso 5. Si existe por nombre lanzo error
            if (optMedicamentoPorNombre.isPresent()) {
                throw new BadRequestException("El nombre del medicamento ya existe");
            }
        }

        // Paso 6. Si no existe por nombre, actualizo los datos del medicamento
        medicamentoActual.setNombre(medicamentoRq.getNombre() == null ? medicamentoActual.getNombre() : medicamentoRq.getNombre());
        medicamentoActual.setDescripcion(medicamentoRq.getDescripcion() == null ? medicamentoActual.getDescripcion() : medicamentoRq.getDescripcion());
        medicamentoActual.setPresentacion(medicamentoRq.getPresentacion() == null ? medicamentoActual.getPresentacion() : medicamentoRq.getPresentacion());
        medicamentoActual.setFechaCompra(medicamentoRq.getFechaCompra() == null ? medicamentoActual.getFechaCompra() : medicamentoRq.getFechaCompra());
        medicamentoActual.setFechaVence(medicamentoRq.getFechaVence() == null ? medicamentoActual.getFechaVence() : medicamentoRq.getFechaVence());
        medicamentoActual.setCodigo(medicamentoRq.getCodigo() == null ? medicamentoActual.getCodigo() : medicamentoRq.getCodigo());
        medicamentoActual.setTipoProducto(medicamentoRq.getTipoProducto() == null
                ? medicamentoActual.getTipoProducto() : normalizarTipo(medicamentoRq.getTipoProducto()));
        medicamentoActual.setPrecioCompra(medicamentoRq.getPrecioCompra() == null ? medicamentoActual.getPrecioCompra() : medicamentoRq.getPrecioCompra());
        medicamentoActual.setPrecioVenta(medicamentoRq.getPrecioVenta() == null ? medicamentoActual.getPrecioVenta() : medicamentoRq.getPrecioVenta());
        medicamentoActual.setCantidadActual(medicamentoRq.getCantidadActual() == null ? medicamentoActual.getCantidadActual() : medicamentoRq.getCantidadActual());
        medicamentoActual.setStockMinimo(medicamentoRq.getStockMinimo() == null ? medicamentoActual.getStockMinimo() : medicamentoRq.getStockMinimo());
        medicamentoActual.setFechaIngreso(medicamentoRq.getFechaIngreso() == null ? medicamentoActual.getFechaIngreso() : medicamentoRq.getFechaIngreso());
        medicamentoActual.setFechaProximoPedido(medicamentoRq.getFechaProximoPedido() == null ? medicamentoActual.getFechaProximoPedido() : medicamentoRq.getFechaProximoPedido());
        medicamentoActual.setRegistroInvima(medicamentoRq.getRegistroInvima() == null ? medicamentoActual.getRegistroInvima() : medicamentoRq.getRegistroInvima());
        medicamentoActual.setFechaModificacionRegistro(LocalDateTime.now());
        this.medicamentoRepository.save(medicamentoActual);
        // Paso 7. Retorno la respuesta
        RespuestaRs rta = new RespuestaRs();
        rta.setMensaje("Medicamento actualizado exitosamente");
        rta.setStatus(200);

        return rta;
    }


    /**
     * Mapea el request a la entidad Medicamento
     * @param medicamentoRq dato de entrada.
     * @return objeto mapeado medicamento.
     */
    private Medicamento mapearAMedicamento(MedicamentoRq medicamentoRq) {
        Medicamento nuevo = new Medicamento();
        nuevo.setNombre(medicamentoRq.getNombre());
        nuevo.setDescripcion(medicamentoRq.getDescripcion());
        nuevo.setPresentacion(medicamentoRq.getPresentacion());
        nuevo.setFechaCompra(medicamentoRq.getFechaCompra());
        nuevo.setFechaVence(medicamentoRq.getFechaVence());
        nuevo.setCodigo(medicamentoRq.getCodigo());
        nuevo.setTipoProducto(normalizarTipo(medicamentoRq.getTipoProducto()));
        nuevo.setPrecioCompra(medicamentoRq.getPrecioCompra());
        nuevo.setPrecioVenta(medicamentoRq.getPrecioVenta());
        nuevo.setCantidadActual(medicamentoRq.getCantidadActual() == null
                ? BigDecimal.ZERO : medicamentoRq.getCantidadActual());
        nuevo.setStockMinimo(medicamentoRq.getStockMinimo() == null
                ? BigDecimal.ZERO : medicamentoRq.getStockMinimo());
        nuevo.setFechaIngreso(medicamentoRq.getFechaIngreso());
        nuevo.setFechaProximoPedido(medicamentoRq.getFechaProximoPedido());
        nuevo.setRegistroInvima(medicamentoRq.getRegistroInvima());
        nuevo.setActivo(true);
        nuevo.setFechaCreacionRegistro(LocalDateTime.now());
        return nuevo;
    }

    private void validarFormulario(MedicamentoRq medicamentoRq) throws BadRequestException {
        if (medicamentoRq.getNombre() == null || medicamentoRq.getNombre().isBlank()) {
            throw new BadRequestException("El nombre es obligatorio");
        }
        if (medicamentoRq.getDescripcion() == null || medicamentoRq.getDescripcion().isBlank()) {
            throw new BadRequestException("El campo descripcion es obligatorio");
        }
        if (medicamentoRq.getPresentacion() == null || medicamentoRq.getPresentacion().isBlank()) {
            throw new BadRequestException("El campo presentacion es obligatorio");
        }
        if (medicamentoRq.getFechaCompra() == null) {
            throw new BadRequestException("El campo fecha de compra es obligatorio");
        }
        if (medicamentoRq.getFechaVence() == null) {
            throw new BadRequestException("El campo fecha de vencimiento es obligatorio");
        }
        if (medicamentoRq.getCodigo() == null
                || !medicamentoRq.getCodigo().matches("^[A-Za-z0-9]{8}$")) {
            throw new BadRequestException("El codigo debe tener exactamente 8 caracteres alfanumericos");
        }
        if (medicamentoRq.getTipoProducto() == null
                || (!"MEDICAMENTO".equalsIgnoreCase(medicamentoRq.getTipoProducto())
                && !"UTENSILIO".equalsIgnoreCase(medicamentoRq.getTipoProducto()))) {
            throw new BadRequestException("El tipo de producto debe ser MEDICAMENTO o UTENSILIO");
        }
        if ("MEDICAMENTO".equalsIgnoreCase(medicamentoRq.getTipoProducto())
                && (medicamentoRq.getRegistroInvima() == null
                || medicamentoRq.getRegistroInvima().isBlank())) {
            throw new BadRequestException("El registro Invima es obligatorio para medicamentos");
        }
        if (medicamentoRq.getPrecioCompra() != null && medicamentoRq.getPrecioCompra().signum() < 0) {
            throw new BadRequestException("El precio de compra no puede ser negativo");
        }
        if (medicamentoRq.getPrecioVenta() != null && medicamentoRq.getPrecioVenta().signum() < 0) {
            throw new BadRequestException("El precio de venta no puede ser negativo");
        }
        if (medicamentoRq.getCantidadActual() != null && medicamentoRq.getCantidadActual().signum() < 0) {
            throw new BadRequestException("La cantidad actual no puede ser negativa");
        }
        if (medicamentoRq.getStockMinimo() != null && medicamentoRq.getStockMinimo().signum() < 0) {
            throw new BadRequestException("El stock minimo no puede ser negativo");
        }
        if (medicamentoRq.getFechaVence().isBefore(medicamentoRq.getFechaCompra())) {
            throw new BadRequestException("La fecha de vencimiento no puede ser anterior a la fecha de compra");
        }
    }

    private String normalizarTipo(String tipoProducto) {
        return tipoProducto.trim().toUpperCase();
    }

    @Override
    public Map<String, Object> previsualizarCarga(MultipartFile archivo) throws Exception {
        if (archivo == null || archivo.isEmpty()) {
            throw new BadRequestException("El archivo es obligatorio.");
        }
        String nombre = archivo.getOriginalFilename() == null ? "" : archivo.getOriginalFilename().toLowerCase();
        if (!nombre.endsWith(".csv") && !nombre.endsWith(".xlsx")) {
            throw new BadRequestException("Solo se permiten archivos CSV o XLSX.");
        }

        List<MedicamentoRq> filas = nombre.endsWith(".csv")
                ? leerCsv(archivo)
                : leerXlsx(archivo);

        List<Map<String, Object>> preview = new ArrayList<>();
        int validos = 0;
        int filaNumero = 2;
        for (MedicamentoRq rq : filas) {
            List<String> errores = new ArrayList<>();
            try {
                validarFormulario(rq);
                if (medicamentoRepository.findByNombre(rq.getNombre()).isPresent()) {
                    errores.add("El nombre ya existe.");
                }
                if (medicamentoRepository.findAll().stream()
                        .anyMatch(m -> rq.getCodigo() != null
                                && rq.getCodigo().equalsIgnoreCase(m.getCodigo()))) {
                    errores.add("El código ya existe.");
                }
            } catch (BadRequestException ex) {
                errores.add(ex.getMessage());
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("fila", filaNumero++);
            item.put("valido", errores.isEmpty());
            item.put("errores", errores);
            item.put("registro", rq);
            if (errores.isEmpty()) validos++;
            preview.add(item);
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("archivo", archivo.getOriginalFilename());
        respuesta.put("total", preview.size());
        respuesta.put("validos", validos);
        respuesta.put("rechazados", preview.size() - validos);
        respuesta.put("registros", preview);
        return respuesta;
    }

    @Override
    public RespuestaRs confirmarCarga(CargaInventarioConfirmarRq carga) throws Exception {
        if (carga == null || carga.getRegistros() == null || carga.getRegistros().isEmpty()) {
            throw new BadRequestException("No hay registros válidos para importar.");
        }

        int importados = 0;
        int rechazados = 0;
        for (MedicamentoRq rq : carga.getRegistros()) {
            try {
                validarFormulario(rq);
                if (medicamentoRepository.findByNombre(rq.getNombre()).isPresent()) {
                    rechazados++;
                    continue;
                }
                medicamentoRepository.save(mapearAMedicamento(rq));
                importados++;
            } catch (BadRequestException ex) {
                rechazados++;
            }
        }

        RespuestaRs respuesta = new RespuestaRs();
        respuesta.setStatus(importados > 0 ? 200 : 400);
        respuesta.setMensaje("Importados: " + importados + ", rechazados: " + rechazados);
        return respuesta;
    }

    private List<MedicamentoRq> leerCsv(MultipartFile archivo) throws Exception {
        List<MedicamentoRq> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            String encabezado = reader.readLine();
            if (encabezado == null) return result;
            String[] headers = encabezado.split(",", -1);
            validarEncabezados(headers);
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) continue;
                result.add(mapearFilaConEncabezados(headers, linea.split(",", -1)));
            }
        }
        return result;
    }

    private List<MedicamentoRq> leerXlsx(MultipartFile archivo) throws Exception {
        List<MedicamentoRq> result = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(archivo.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() == 0) return result;
            Row header = sheet.getRow(0);
            String[] headers = new String[header.getLastCellNum()];
            for (int i = 0; i < headers.length; i++) {
                headers[i] = header.getCell(i) == null ? "" : header.getCell(i).getStringCellValue();
            }
            validarEncabezados(headers);
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                String[] values = new String[headers.length];
                for (int c = 0; c < headers.length; c++) {
                    values[c] = row.getCell(c) == null ? "" :
                            (row.getCell(c).getCellType().toString().equals("NUMERIC")
                                    ? String.valueOf(row.getCell(c).getNumericCellValue())
                                    : row.getCell(c).toString());
                }
                result.add(mapearFilaConEncabezados(headers, values));
            }
        }
        return result;
    }

    private void validarEncabezados(String[] headers) throws BadRequestException {
        String joined = String.join(",", headers).toLowerCase();
        String[] obligatorios = {"nombre","descripcion","presentacion","codigo","tipo_producto","fecha_compra","fecha_vence"};
        for (String header : obligatorios) {
            if (!joined.contains(header)) {
                throw new BadRequestException("Falta el encabezado obligatorio: " + header);
            }
        }
    }

    private MedicamentoRq mapearFila(String[] values) {
        String[] headers = {"nombre","descripcion","presentacion","tipo_producto","codigo",
                "precio_compra","precio_venta","cantidad_actual","stock_minimo",
                "fecha_compra","fecha_vence","fecha_ingreso","fecha_proximo_pedido","registro_invima"};
        return mapearFilaConEncabezados(headers, values);
    }

    private MedicamentoRq mapearFilaConEncabezados(String[] headers, String[] values) {
        Map<String, String> fila = new LinkedHashMap<>();
        for (int i = 0; i < headers.length && i < values.length; i++) {
            fila.put(headers[i].trim().toLowerCase(), values[i].trim());
        }
        MedicamentoRq rq = new MedicamentoRq();
        rq.setNombre(fila.get("nombre"));
        rq.setDescripcion(fila.get("descripcion"));
        rq.setPresentacion(fila.get("presentacion"));
        rq.setTipoProducto(fila.getOrDefault("tipo_producto","MEDICAMENTO"));
        rq.setCodigo(fila.get("codigo"));
        rq.setPrecioCompra(decimalOrNull(fila.get("precio_compra")));
        rq.setPrecioVenta(decimalOrNull(fila.get("precio_venta")));
        rq.setCantidadActual(decimalOrZero(fila.get("cantidad_actual")));
        rq.setStockMinimo(decimalOrZero(fila.get("stock_minimo")));
        rq.setFechaCompra(localDateOrNull(fila.get("fecha_compra")));
        rq.setFechaVence(localDateOrNull(fila.get("fecha_vence")));
        rq.setFechaIngreso(localDateOrNull(fila.get("fecha_ingreso")));
        rq.setFechaProximoPedido(localDateOrNull(fila.get("fecha_proximo_pedido")));
        rq.setRegistroInvima(fila.get("registro_invima"));
        return rq;
    }

    private java.math.BigDecimal decimalOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        try { return new java.math.BigDecimal(value); } catch (NumberFormatException ex) { return null; }
    }

    private java.math.BigDecimal decimalOrZero(String value) {
        java.math.BigDecimal result = decimalOrNull(value);
        return result == null ? java.math.BigDecimal.ZERO : result;
    }

    private java.time.LocalDate localDateOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        try { return java.time.LocalDate.parse(value); } catch (RuntimeException ex) { return null; }
    }


}
