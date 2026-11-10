package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.MovimientoMedicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CargaMasivaMedicamentoRS;
import com.uniminuto.clinica.models.ErrorCargaMasivaMedicamento;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MovimientoMedicamentoRepository;
import com.uniminuto.clinica.service.MedicamentoService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
/** Implementa las operaciones del inventario de medicamentos. */
public class MedicamentoServiceImpl implements MedicamentoService {

    private static final Pattern CODIGO_PATTERN = Pattern.compile("^[A-Za-z0-9]{8}$");

    @Autowired
    /** Repositorio de medicamentos. */
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    /** Repositorio que registra los movimientos de inventario. */
    private MovimientoMedicamentoRepository movimientoMedicamentoRepository;

    @Override
    public List<Medicamento> obtenerMedicamentos() throws BadRequestException {
        return medicamentoRepository.findAllByOrderByFechaVenceAsc();
    }

    @Override
    public Medicamento consultarMedicamento(Long medicamentoId) throws BadRequestException {
        if (medicamentoId == null || medicamentoId <= 0) {
            throw new BadRequestException("El identificador del medicamento es obligatorio");
        }
        return medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new BadRequestException("Medicamento no encontrado"));
    }

    @Override
    public MiRespuestaRS guardarMedicamento(Medicamento medicamento) throws BadRequestException {
        validarMedicamento(medicamento);

        String nombre = medicamento.getNombre().trim();
        String codigo = medicamento.getCodigo().trim().toUpperCase();

        if (medicamentoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new BadRequestException("Ya existe un medicamento registrado con ese nombre");
        }

        if (medicamentoRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new BadRequestException("Ya existe un medicamento registrado con ese código");
        }

        medicamento.setNombre(nombre);
        medicamento.setCodigo(codigo);
        medicamento.setDescripcion(medicamento.getDescripcion() == null ? null : medicamento.getDescripcion().trim());
        medicamento.setPresentacion(medicamento.getPresentacion() == null ? null : medicamento.getPresentacion().trim());
        medicamento.setLote(medicamento.getLote() == null ? null : medicamento.getLote().trim());
        medicamento.setRegistroInvima(medicamento.getRegistroInvima() == null ? null : medicamento.getRegistroInvima().trim());
        medicamento.setActivo(medicamento.getActivo() == null || medicamento.getActivo());
        medicamento.setFechaCreacionRegistro(LocalDateTime.now());
        medicamento.setFechaModificacionRegistro(LocalDateTime.now());

        medicamentoRepository.save(medicamento);
        return respuesta("Medicamento registrado correctamente");
    }

    @Override
    public CargaMasivaMedicamentoRS importarMasivo(MultipartFile archivo) throws BadRequestException {
        if (archivo == null || archivo.isEmpty()) {
            throw new BadRequestException("Debe seleccionar un archivo para realizar la carga masiva");
        }

        String nombreArchivo = archivo.getOriginalFilename() == null ? "" : archivo.getOriginalFilename().toLowerCase(Locale.ROOT);
        List<String[]> filas;

        try {
            if (nombreArchivo.endsWith(".csv")) {
                filas = leerCsv(archivo);
            } else if (nombreArchivo.endsWith(".xlsx")) {
                filas = leerXlsx(archivo);
            } else {
                throw new BadRequestException("El archivo debe estar en formato CSV o XLSX");
            }
        } catch (IOException e) {
            throw new BadRequestException("No se pudo leer el archivo cargado");
        }

        if (filas.isEmpty()) {
            throw new BadRequestException("El archivo no tiene registros para importar");
        }

        List<String> encabezadosEsperados = Arrays.asList(
                "nombre", "descripcion", "presentacion", "codigo", "lote",
                "registroInvima", "requiereRegistroInvima", "precioCompra","precioVenta",
                "cantidadDisponible", "stockMinimo", "fechaCompra", "fechaIngresoInventario",
                "fechaVence", "fechaProximoPedido", "activo"
        );

        String[] encabezados = filas.getFirst();
        for (int i = 0; i < encabezadosEsperados.size(); i++) {
            String esperado = encabezadosEsperados.get(i);
            if (i >= encabezados.length || encabezados[i] == null || !encabezados[i].trim().equalsIgnoreCase(esperado)) {
                throw new BadRequestException("La estructura del archivo es inválida. Encabezados esperados: " + encabezadosEsperados);
            }
        }

        CargaMasivaMedicamentoRS resultado = new CargaMasivaMedicamentoRS();
        List<Medicamento> validos = new ArrayList<>();

        for (int i = 1; i < filas.size(); i++) {
            String[] fila = filas.get(i);
            resultado.setTotalRegistros(resultado.getTotalRegistros() + 1);

            if (fila == null || fila.length == 0 || Arrays.stream(fila).allMatch(value -> value == null || value.trim().isEmpty())) {
                continue;
            }

            try {
                Medicamento medicamento = construirMedicamentoDesdeFila(fila, i + 1);
                validarMedicamento(medicamento);
                if (medicamentoRepository.existsByCodigoIgnoreCase(medicamento.getCodigo().trim().toUpperCase())) {
                    throw new BadRequestException("Código duplicado en la base de datos");
                }
                if (medicamentoRepository.existsByNombreIgnoreCase(medicamento.getNombre().trim())) {
                    throw new BadRequestException("Nombre duplicado en la base de datos");
                }
                medicamento.setFechaCreacionRegistro(LocalDateTime.now());
                medicamento.setFechaModificacionRegistro(LocalDateTime.now());
                validos.add(medicamento);
            } catch (BadRequestException ex) {
                ErrorCargaMasivaMedicamento error = new ErrorCargaMasivaMedicamento();
                error.setFila(i + 1);
                error.setMensaje(ex.getMessage());
                resultado.getErrores().add(error);
            }
        }

        resultado.setRegistrosValidos(validos.size());
        resultado.setRegistrosInvalidos(resultado.getErrores().size());

        if (!resultado.getErrores().isEmpty()) {
            return resultado;
        }

        List<Medicamento> importados = medicamentoRepository.saveAll(validos);
        resultado.setImportados(importados);
        return resultado;
    }

    @Override
    public MiRespuestaRS actualizarMedicamento(Medicamento medicamento) throws BadRequestException {
        if (medicamento == null || medicamento.getId() == null) {
            throw new BadRequestException("El identificador del medicamento es obligatorio para actualizar");
        }

        Medicamento medicamentoActual = medicamentoRepository.findById(medicamento.getId())
                .orElseThrow(() -> new BadRequestException("Medicamento no encontrado"));

        validarMedicamento(medicamento);

        String nombre = medicamento.getNombre().trim();
        String codigo = medicamento.getCodigo().trim().toUpperCase();

        if (!nombre.equalsIgnoreCase(medicamentoActual.getNombre())
                && medicamentoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new BadRequestException("Ya existe un medicamento registrado con ese nombre");
        }

        if (!codigo.equalsIgnoreCase(medicamentoActual.getCodigo())
                && medicamentoRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new BadRequestException("Ya existe un medicamento registrado con ese código");
        }

        medicamentoActual.setNombre(nombre);
        medicamentoActual.setDescripcion(medicamento.getDescripcion() == null ? null : medicamento.getDescripcion().trim());
        medicamentoActual.setPresentacion(medicamento.getPresentacion() == null ? null : medicamento.getPresentacion().trim());
        medicamentoActual.setCodigo(codigo);
        medicamentoActual.setLote(medicamento.getLote() == null ? null : medicamento.getLote().trim());
        medicamentoActual.setRegistroInvima(medicamento.getRegistroInvima() == null ? null : medicamento.getRegistroInvima().trim());
        medicamentoActual.setRequiereRegistroInvima(Boolean.TRUE.equals(medicamento.getRequiereRegistroInvima()));
        medicamentoActual.setPrecioCompra(medicamento.getPrecioCompra());
        medicamentoActual.setPrecioVenta(medicamento.getPrecioVenta());
        medicamentoActual.setCantidadDisponible(medicamento.getCantidadDisponible());
        medicamentoActual.setStockMinimo(medicamento.getStockMinimo());
        medicamentoActual.setFechaCompra(medicamento.getFechaCompra());
        medicamentoActual.setFechaIngresoInventario(medicamento.getFechaIngresoInventario());
        medicamentoActual.setFechaVence(medicamento.getFechaVence());
        medicamentoActual.setFechaProximoPedido(medicamento.getFechaProximoPedido());
        medicamentoActual.setActivo(medicamento.getActivo() == null || medicamento.getActivo());
        medicamentoActual.setFechaModificacionRegistro(LocalDateTime.now());

        medicamentoRepository.save(medicamentoActual);
        return respuesta("Medicamento actualizado correctamente");
    }

    @Override
    @Transactional
    public MiRespuestaRS registrarSalidaInventario(Long medicamentoId, Integer cantidad, String motivo,
                                                Long referenciaId, String referenciaTipo) throws BadRequestException {
        if (medicamentoId == null || medicamentoId <= 0) {
            throw new BadRequestException("El identificador del medicamento es obligatorio");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new BadRequestException("La cantidad a descontar debe ser mayor que cero");
        }

        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new BadRequestException("Medicamento no encontrado"));

        if (medicamento.getCantidadDisponible() < cantidad) {
            throw new BadRequestException("No hay suficiente inventario para despachar " + cantidad + " unidades");
        }

        medicamento.setCantidadDisponible(medicamento.getCantidadDisponible() - cantidad);
        medicamento.setFechaModificacionRegistro(LocalDateTime.now());
        medicamentoRepository.save(medicamento);

        MovimientoMedicamento movimiento = new MovimientoMedicamento();
        movimiento.setMedicamentoId(medicamentoId);
        movimiento.setTipo("SALIDA");
        movimiento.setCantidad(cantidad);
        movimiento.setMotivo(motivo == null || motivo.isBlank() ? "DESPACHO" : motivo.trim());
        movimiento.setReferenciaId(referenciaId);
        movimiento.setReferenciaTipo(referenciaTipo == null || referenciaTipo.isBlank() ? "AJUSTE" : referenciaTipo.trim());
        movimiento.setFechaRegistro(LocalDateTime.now());
        movimientoMedicamentoRepository.save(movimiento);

        return respuesta("Salida de inventario registrada correctamente");
    }

    @Override
    public List<MovimientoMedicamento> consultarHistorialInventario(Long medicamentoId) throws BadRequestException {
        if (medicamentoId == null || medicamentoId <= 0) {
            throw new BadRequestException("El identificador del medicamento es obligatorio");
        }
        consultarMedicamento(medicamentoId);
        return movimientoMedicamentoRepository.findByMedicamentoIdOrderByFechaRegistroDesc(medicamentoId);
    }

    private List<String[]> leerCsv(MultipartFile archivo) throws IOException {
        List<String[]> filas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(archivo.getInputStream()))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                filas.add(linea.split(",", -1));
            }
        }
        return filas;
    }

    private List<String[]> leerXlsx(MultipartFile archivo) throws IOException {
        List<String[]> filas = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(archivo.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row == null) {
                    continue;
                }
                List<String> valores = new ArrayList<>();
                int ultimaColumna = Math.max(16, row.getLastCellNum());
                for (int i = 0; i < ultimaColumna; i++) {
                    if (row.getCell(i) == null) {
                        valores.add("");
                    } else {
                        valores.add(row.getCell(i).toString().trim());
                    }
                }
                filas.add(valores.toArray(String[]::new));
            }
        }
        return filas;
    }

    private Medicamento construirMedicamentoDesdeFila(String[] fila, int numeroFila) throws BadRequestException {
        if (fila.length < 16) {
            throw new BadRequestException("La fila " + numeroFila + " no tiene la cantidad mínima de columnas");
        }

        Medicamento medicamento = new Medicamento();
        medicamento.setNombre(normalizarCampo(fila[0], "nombre"));
        medicamento.setDescripcion(normalizarCampoOpcional(fila[1]));
        medicamento.setPresentacion(normalizarCampoOpcional(fila[2]));
        medicamento.setCodigo(normalizarCampo(fila[3], "codigo"));
        medicamento.setLote(normalizarCampoOpcional(fila[4]));
        medicamento.setRegistroInvima(normalizarCampoOpcional(fila[5]));
        medicamento.setRequiereRegistroInvima(parseBoolean(fila[6], "requiereRegistroInvima"));
        medicamento.setPrecioCompra(parseBigDecimal(fila[7], "precioCompra"));
        medicamento.setPrecioVenta(parseBigDecimal(fila[8], "precioVenta"));
        medicamento.setCantidadDisponible(parseInteger(fila[9], "cantidadDisponible"));
        medicamento.setStockMinimo(parseInteger(fila[10], "stockMinimo"));
        medicamento.setFechaCompra(parseLocalDate(fila[11], "fechaCompra"));
        medicamento.setFechaIngresoInventario(parseLocalDate(fila[12], "fechaIngresoInventario"));
        medicamento.setFechaVence(parseLocalDate(fila[13], "fechaVence"));
        medicamento.setFechaProximoPedido(parseLocalDateOpcional(fila[14]));
        medicamento.setActivo(parseBoolean(fila[15], "activo"));
        return medicamento;
    }

    private String normalizarCampo(String valor, String nombreCampo) throws BadRequestException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new BadRequestException("El campo '" + nombreCampo + "' es obligatorio");
        }
        return valor.trim();
    }

    private String normalizarCampoOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String resultado = valor.trim();
        return resultado.isEmpty() ? null : resultado;
    }

    private BigDecimal parseBigDecimal(String valor, String nombreCampo) throws BadRequestException {
        String texto = normalizarCampo(valor, nombreCampo);
        try {
            return new BigDecimal(texto.replace(".", "").replace(",", "."));
        } catch (NumberFormatException ex) {
            throw new BadRequestException("El campo '" + nombreCampo + "' debe ser numérico");
        }
    }

    private Integer parseInteger(String valor, String nombreCampo) throws BadRequestException {
        String texto = normalizarCampo(valor, nombreCampo);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("El campo '" + nombreCampo + "' debe ser un número entero");
        }
    }

    private LocalDate parseLocalDate(String valor, String nombreCampo) throws BadRequestException {
        String texto = normalizarCampo(valor, nombreCampo);
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("El campo '" + nombreCampo + "' debe tener formato yyyy-MM-dd");
        }
    }

    private LocalDate parseLocalDateOpcional(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(valor.trim());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private Boolean parseBoolean(String valor, String nombreCampo) throws BadRequestException {
        String texto = normalizarCampoOpcional(valor);
        if (texto == null) {
            return false;
        }
        String normalizado = texto.trim().toLowerCase(Locale.ROOT);
        if ("true".equals(normalizado) || "1".equals(normalizado) || "si".equals(normalizado) || "yes".equals(normalizado)) {
            return true;
        }
        if ("false".equals(normalizado) || "0".equals(normalizado) || "no".equals(normalizado)) {
            return false;
        }
        throw new BadRequestException("El campo '" + nombreCampo + "' debe ser verdadero o falso");
    }

    private void validarMedicamento(Medicamento medicamento) throws BadRequestException {
        if (medicamento == null) {
            throw new BadRequestException("La información del medicamento es obligatoria");
        }

        if (medicamento.getNombre() == null || medicamento.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del medicamento es obligatorio");
        }

        if (medicamento.getCodigo() == null || medicamento.getCodigo().isBlank()) {
            throw new BadRequestException("El código del medicamento es obligatorio");
        }

        if (!CODIGO_PATTERN.matcher(medicamento.getCodigo().trim()).matches()) {
            throw new BadRequestException("El código debe tener exactamente 8 caracteres alfanuméricos");
        }

        if (medicamento.getPrecioCompra() == null || medicamento.getPrecioCompra().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El precio de compra debe ser mayor que cero");
        }

        if (medicamento.getPrecioVenta() == null || medicamento.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El precio de venta debe ser mayor que cero");
        }

        if (medicamento.getCantidadDisponible() == null || medicamento.getCantidadDisponible() < 0) {
            throw new BadRequestException("La cantidad disponible no es válida");
        }

        if (medicamento.getStockMinimo() == null || medicamento.getStockMinimo() < 0) {
            throw new BadRequestException("El stock mínimo no es válido");
        }

        if (medicamento.getFechaCompra() == null) {
            throw new BadRequestException("La fecha de compra es obligatoria");
        }

        if (medicamento.getFechaIngresoInventario() == null) {
            throw new BadRequestException("La fecha de ingreso al inventario es obligatoria");
        }

        if (medicamento.getFechaVence() == null) {
            throw new BadRequestException("La fecha de vencimiento es obligatoria");
        }

        if (medicamento.getFechaVence().isBefore(LocalDate.now()) && medicamento.getCantidadDisponible() > 0) {
            throw new BadRequestException("El producto no puede tener fecha de vencimiento vencida");
        }

        if (Boolean.TRUE.equals(medicamento.getRequiereRegistroInvima())
                && (medicamento.getRegistroInvima() == null || medicamento.getRegistroInvima().isBlank())) {
            throw new BadRequestException("El registro Invima es obligatorio para medicamentos con registro sanitario");
        }
    }

    private MiRespuestaRS respuesta(String mensaje) {
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
