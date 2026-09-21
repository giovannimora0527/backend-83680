'use strict';

/* =========================================================
 * Panel de la clínica veterinaria
 * Consume la API REST del backend (Spring Boot).
 * ========================================================= */

/** URL base por defecto del backend. */
const DEFAULT_API = 'http://localhost:8080/clinica/v1';

/** Datos de apoyo para los selectores de los formularios. */
const catalogo = { mascotas: [], medicos: [] };

/** Últimos registros cargados, para poder editarlos por su id. */
const cargados = { citas: [], anotaciones: [] };

const $ = (sel, root = document) => root.querySelector(sel);
const $$ = (sel, root = document) => [...root.querySelectorAll(sel)];

/* ---------------------------------------------------------
 * Utilidades
 * --------------------------------------------------------- */

/** Escapa texto para insertarlo de forma segura en HTML. */
function esc(valor) {
  return String(valor ?? '').replace(/[&<>"']/g, (c) => (
    { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]
  ));
}

const pad = (n) => String(n).padStart(2, '0');

/** Convierte un Date a yyyy-MM-dd (hora local). */
const aIsoFecha = (d) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;

/** Muestra una fecha ISO del backend de forma legible. */
function formatear(iso, conHora = true) {
  if (!iso) return '—';
  const opciones = conHora ? { dateStyle: 'medium', timeStyle: 'short' } : { dateStyle: 'medium' };
  return new Date(iso).toLocaleString('es-CO', opciones);
}

/** Suma (o resta) años a hoy. */
function hoyMasAnios(anios) {
  const d = new Date();
  d.setFullYear(d.getFullYear() + anios);
  return d;
}

/** Muestra un aviso temporal en la parte inferior. */
let toastTimer;
function aviso(mensaje, esError = false) {
  const t = $('#toast');
  t.textContent = mensaje;
  t.classList.toggle('error', esError);
  t.hidden = false;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => { t.hidden = true; }, 3500);
}

/* ---------------------------------------------------------
 * Acceso a la API
 * --------------------------------------------------------- */

function baseApi() {
  return ($('#apiBase').value.trim() || DEFAULT_API).replace(/\/+$/, '');
}

/**
 * Llama al backend y devuelve el JSON. Si el backend responde con error,
 * lanza un Error con el mensaje que envió ({status, error, message}).
 */
async function api(ruta, { metodo = 'GET', cuerpo } = {}) {
  let respuesta;
  try {
    respuesta = await fetch(baseApi() + ruta, {
      method: metodo,
      headers: cuerpo ? { 'Content-Type': 'application/json' } : undefined,
      body: cuerpo ? JSON.stringify(cuerpo) : undefined,
    });
  } catch {
    throw new Error('No se pudo conectar con el servidor. Verifica que el backend esté encendido y la URL de la API.');
  }
  const datos = await respuesta.json().catch(() => null);
  if (!respuesta.ok) {
    throw new Error(datos?.message || `Error ${respuesta.status}`);
  }
  return datos;
}

function estadoConexion(ok) {
  const s = $('#apiStatus');
  s.textContent = ok ? 'Conectado' : 'Sin conexión';
  s.classList.toggle('ok', ok);
}

/** Carga mascotas y médicos para los selectores. */
async function cargarCatalogo() {
  try {
    [catalogo.mascotas, catalogo.medicos] = await Promise.all([
      api('/mascota/listar'),
      api('/medico/listar'),
    ]);
    estadoConexion(true);
  } catch (e) {
    estadoConexion(false);
    catalogo.mascotas = [];
    catalogo.medicos = [];
  }
}

/* ---------------------------------------------------------
 * Tablas: helpers de estado
 * --------------------------------------------------------- */

function filasVacias(tbody, columnas, texto) {
  tbody.innerHTML = `<tr><td class="empty" colspan="${columnas}">${esc(texto)}</td></tr>`;
}

function filasError(tbody, columnas, e) {
  tbody.innerHTML = `<tr><td class="error" colspan="${columnas}">${esc(e.message)}</td></tr>`;
}

/* ---------------------------------------------------------
 * Fórmulas médicas
 * --------------------------------------------------------- */

async function cargarFormulas() {
  const tb = $('#tbFormulas');
  filasVacias(tb, 6, 'Cargando…');
  try {
    const formulas = await api('/formula-medica/listar');
    estadoConexion(true);
    if (!formulas.length) return filasVacias(tb, 6, 'No hay fórmulas médicas registradas.');
    tb.innerHTML = formulas.map((f) => `
      <tr>
        <td class="nowrap">${esc(formatear(f.fechaCreacionRegistro))}</td>
        <td>#${esc(f.citaId)}</td>
        <td>${esc(f.medicamentoNombre)}</td>
        <td>${esc(f.dosis)}</td>
        <td>${esc(f.indicaciones || '—')}</td>
        <td class="nowrap">${esc(formatear(f.fechaActualizacionRegistro))}</td>
      </tr>`).join('');
  } catch (e) {
    estadoConexion(false);
    filasError(tb, 6, e);
  }
}

/* ---------------------------------------------------------
 * Citas
 * --------------------------------------------------------- */

async function cargarCitas() {
  const tb = $('#tbCitas');
  filasVacias(tb, 7, 'Cargando…');
  const q = new URLSearchParams({ fechaInicial: $('#citaDesde').value, fechaFinal: $('#citaHasta').value });
  try {
    cargados.citas = await api(`/cita/listar?${q}`);
    estadoConexion(true);
    if (!cargados.citas.length) return filasVacias(tb, 7, 'No hay citas en ese rango de fechas.');
    tb.innerHTML = cargados.citas.map((c) => `
      <tr>
        <td class="nowrap">${esc(formatear(c.fechaHora))}</td>
        <td>${esc(c.mascotaNombre)}</td>
        <td>${esc(c.clienteNombre)}</td>
        <td>${esc(c.medicoNombre)}</td>
        <td>${esc(c.motivo || '—')}</td>
        <td><span class="badge ${esc(c.estado)}">${esc(c.estado)}</span></td>
        <td class="nowrap"><button class="btn btn-secondary btn-sm" data-editar-cita="${esc(c.id)}">Editar</button></td>
      </tr>`).join('');
  } catch (e) {
    estadoConexion(false);
    filasError(tb, 7, e);
  }
}

function opcionesMascotas(seleccionada) {
  if (!catalogo.mascotas.length) return '<option value="">(sin mascotas)</option>';
  return catalogo.mascotas.map((m) =>
    `<option value="${esc(m.mascotaId)}" ${m.mascotaId === seleccionada ? 'selected' : ''}>${esc(m.nombreMascota)} — ${esc(m.cliente.nombres)} ${esc(m.cliente.apellidos)}</option>`
  ).join('');
}

function opcionesMedicos(seleccionado) {
  if (!catalogo.medicos.length) return '<option value="">(sin médicos)</option>';
  return catalogo.medicos.map((m) =>
    `<option value="${esc(m.id)}" ${m.id === seleccionado ? 'selected' : ''}>${esc(m.nombres)} ${esc(m.apellidos)} — ${esc(m.especializacion.nombre)}</option>`
  ).join('');
}

/** Muestra a qué cliente pertenece la mascota elegida. */
function actualizarNotaCliente() {
  const m = catalogo.mascotas.find((x) => String(x.mascotaId) === $('#citaMascota').value);
  $('#citaClienteNote').textContent = m ? `Cliente: ${m.cliente.nombres} ${m.cliente.apellidos}` : '';
}

function abrirDialogoCita(cita) {
  const editando = Boolean(cita);
  $('#dlgCitaTitulo').textContent = editando ? `Editar cita #${cita.id}` : 'Nueva cita';
  $('#citaId').value = editando ? cita.id : '';
  $('#citaMascota').innerHTML = opcionesMascotas(editando ? cita.mascotaId : undefined);
  $('#citaMedico').innerHTML = opcionesMedicos(editando ? cita.medicoId : undefined);
  $('#citaFechaHora').value = editando ? cita.fechaHora.slice(0, 16) : '';
  $('#citaMotivo').value = editando ? (cita.motivo || '') : '';
  $('#citaEstado').value = editando ? cita.estado : 'programada';
  $('#citaEstadoWrap').hidden = !editando;
  $('#citaError').hidden = true;
  actualizarNotaCliente();
  $('#dlgCita').showModal();
}

async function guardarCita(evento) {
  evento.preventDefault();
  const error = $('#citaError');
  error.hidden = true;

  const mascota = catalogo.mascotas.find((m) => String(m.mascotaId) === $('#citaMascota').value);
  const fechaHora = $('#citaFechaHora').value;
  if (!mascota || !$('#citaMedico').value || !fechaHora) {
    error.textContent = 'Selecciona la mascota, el médico y la fecha y hora.';
    error.hidden = false;
    return;
  }

  const id = $('#citaId').value;
  const cuerpo = {
    clienteId: mascota.cliente.clienteId,
    mascotaId: mascota.mascotaId,
    medicoId: Number($('#citaMedico').value),
    fechaHora: fechaHora.length === 16 ? `${fechaHora}:00` : fechaHora,
    motivo: $('#citaMotivo').value.trim(),
  };
  if (id) {
    cuerpo.citaId = Number(id);
    cuerpo.estado = $('#citaEstado').value;
  }

  try {
    const rs = await api(id ? '/cita/actualizar' : '/cita/guardar', { metodo: 'POST', cuerpo });
    $('#dlgCita').close();
    aviso(rs.message);
    cargarCitas();
  } catch (e) {
    error.textContent = e.message;
    error.hidden = false;
  }
}

/* ---------------------------------------------------------
 * Anotaciones de historia médica
 * --------------------------------------------------------- */

async function cargarAnotaciones() {
  const tb = $('#tbAnotaciones');
  filasVacias(tb, 5, 'Cargando…');
  const q = new URLSearchParams({ fechaInicial: $('#anoDesde').value, fechaFinal: $('#anoHasta').value });
  try {
    cargados.anotaciones = await api(`/anotacion-historia/listar?${q}`);
    estadoConexion(true);
    if (!cargados.anotaciones.length) return filasVacias(tb, 5, 'No hay anotaciones en ese rango de fechas.');
    tb.innerHTML = cargados.anotaciones.map((a) => `
      <tr>
        <td class="nowrap">${esc(formatear(a.fecha))}</td>
        <td>#${esc(a.historiaId)}</td>
        <td>${esc(a.medicoNombre)}</td>
        <td>${esc(a.descripcion)}</td>
        <td class="nowrap"><button class="btn btn-secondary btn-sm" data-editar-anotacion="${esc(a.id)}">Editar</button></td>
      </tr>`).join('');
  } catch (e) {
    estadoConexion(false);
    filasError(tb, 5, e);
  }
}

function abrirDialogoAnotacion(anotacion) {
  const editando = Boolean(anotacion);
  $('#dlgAnotacionTitulo').textContent = editando ? `Editar anotación #${anotacion.id}` : 'Nueva anotación';
  $('#anoId').value = editando ? anotacion.id : '';
  $('#anoHistoriaForm').value = editando ? anotacion.historiaId : '';
  $('#anoHistoriaForm').readOnly = editando; // la historia no se puede cambiar al editar
  $('#anoMedico').innerHTML = opcionesMedicos(editando ? anotacion.medicoId : undefined);
  $('#anoDescripcion').value = editando ? anotacion.descripcion : '';
  $('#anoError').hidden = true;
  $('#dlgAnotacion').showModal();
}

async function guardarAnotacion(evento) {
  evento.preventDefault();
  const error = $('#anoError');
  error.hidden = true;

  const id = $('#anoId').value;
  const cuerpo = {
    medicoId: Number($('#anoMedico').value),
    descripcion: $('#anoDescripcion').value.trim(),
  };
  if (id) {
    cuerpo.anotacionId = Number(id);
  } else {
    cuerpo.historiaId = Number($('#anoHistoriaForm').value);
  }

  try {
    const rs = await api(id ? '/anotacion-historia/actualizar' : '/anotacion-historia/guardar', {
      metodo: 'POST',
      cuerpo,
    });
    $('#dlgAnotacion').close();
    aviso(rs.message);
    cargarAnotaciones();
  } catch (e) {
    error.textContent = e.message;
    error.hidden = false;
  }
}

/* ---------------------------------------------------------
 * Navegación por pestañas (hash: #formulas, #citas, #anotaciones)
 * --------------------------------------------------------- */

const vistas = {
  formulas: cargarFormulas,
  citas: cargarCitas,
  anotaciones: cargarAnotaciones,
};

function mostrarVista() {
  const nombre = vistas[location.hash.slice(1)] ? location.hash.slice(1) : 'formulas';
  $$('.view').forEach((v) => { v.hidden = v.id !== `view-${nombre}`; });
  $$('.tabs a').forEach((a) => a.classList.toggle('active', a.dataset.view === nombre));
  vistas[nombre]();
}

/* ---------------------------------------------------------
 * Inicio
 * --------------------------------------------------------- */

function iniciar() {
  // URL de la API recordada por el navegador
  let guardada = null;
  try { guardada = localStorage.getItem('apiBase'); } catch { /* sin almacenamiento */ }
  $('#apiBase').value = guardada || DEFAULT_API;

  // Rangos de fechas por defecto
  $('#citaDesde').value = aIsoFecha(hoyMasAnios(-3));
  $('#citaHasta').value = aIsoFecha(hoyMasAnios(1));
  $('#anoDesde').value = aIsoFecha(hoyMasAnios(-1));
  $('#anoHasta').value = aIsoFecha(new Date());

  // Cambio de URL de la API
  $('#apiBase').addEventListener('change', async () => {
    try { localStorage.setItem('apiBase', $('#apiBase').value.trim()); } catch { /* sin almacenamiento */ }
    await cargarCatalogo();
    mostrarVista();
  });

  // Botones y formularios
  $('#btnRefreshFormulas').addEventListener('click', cargarFormulas);
  $('#formFiltroCitas').addEventListener('submit', (e) => { e.preventDefault(); cargarCitas(); });
  $('#formFiltroAnotaciones').addEventListener('submit', (e) => { e.preventDefault(); cargarAnotaciones(); });
  $('#btnNewCita').addEventListener('click', () => abrirDialogoCita(null));
  $('#btnNewAnotacion').addEventListener('click', () => abrirDialogoAnotacion(null));
  $('#citaMascota').addEventListener('change', actualizarNotaCliente);
  $('#formCita').addEventListener('submit', guardarCita);
  $('#formAnotacion').addEventListener('submit', guardarAnotacion);

  // Botones "Editar" de las tablas (delegación de eventos)
  $('#tbCitas').addEventListener('click', (e) => {
    const b = e.target.closest('[data-editar-cita]');
    if (b) abrirDialogoCita(cargados.citas.find((c) => String(c.id) === b.dataset.editarCita));
  });
  $('#tbAnotaciones').addEventListener('click', (e) => {
    const b = e.target.closest('[data-editar-anotacion]');
    if (b) abrirDialogoAnotacion(cargados.anotaciones.find((a) => String(a.id) === b.dataset.editarAnotacion));
  });

  // Botones "Cancelar" de los diálogos
  $$('dialog [data-close]').forEach((b) => b.addEventListener('click', () => b.closest('dialog').close()));

  window.addEventListener('hashchange', mostrarVista);
  cargarCatalogo().then(mostrarVista);
}

iniciar();
