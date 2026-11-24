/* ============================================================
   ClassControl - Gestión de Incidencias
   IncidenciasJS.js  (Bootstrap 5 + DataTables)

   Datos reales desde el backend:
   - GET  ConsultarReportes   -> listado (filtrado por rol en servidor)
   - GET  ConsultarAmbientes  -> catálogo para el formulario
   - POST RegistrarReporte    -> crear incidencia (solo instructor)
   - POST AtenderReporte      -> atender (solo administrador)
   - POST EliminarReporte     -> eliminar (solo administrador)

   El filtrado, el CSV y las métricas trabajan sobre el mismo
   conjunto de registros que se pinta en la tabla.
   ============================================================ */

'use strict';

const INC = window.ccInc || { esAdmin: false, esInstructor: false };

let incidencias = [];
let listaVisible = [];
let dtInstance = null;
let idAtender = null;
let idEliminar = null;
let ambientesSel = [];

/* --------------------------------------------------
   Utilidades
-------------------------------------------------- */

function esc(valor) {
  return String(valor ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function parseFecha(valor) {
  if (!valor) return null;
  const texto = String(valor);
  const m = texto.match(/^(\d{4})-(\d{2})-(\d{2})[ T](\d{2}):(\d{2})/);
  if (m) {
    return new Date(+m[1], +m[2] - 1, +m[3], +m[4], +m[5]);
  }
  const d = new Date(texto);
  return Number.isNaN(d.getTime()) ? null : d;
}

function pad(n) { return String(n).padStart(2, '0'); }

function fmtFecha(valor) {
  const d = parseFecha(valor);
  if (!d) return '—';
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

function diasDesde(valor) {
  const d = parseFecha(valor);
  if (!d) return null;
  return Math.max(0, Math.floor((Date.now() - d.getTime()) / 86400000));
}

function fmtEdad(valor) {
  const d = diasDesde(valor);
  if (d === null) return '';
  if (d === 0) return 'Hoy';
  if (d === 1) return '1 día';
  return `${d} días`;
}

function estadoDe(r) {
  return String(r.estado || '').toLowerCase() === 'atendido' ? 'atendido' : 'pendiente';
}

function estadoLabel(r) {
  return estadoDe(r) === 'atendido' ? 'Atendido' : 'Pendiente';
}

function badgeEstado(r) {
  const cls = estadoDe(r) === 'atendido' ? 'atendido' : 'pendiente';
  return `<span class="cc-inc-badge cc-inc-badge--${cls}"><span class="dot"></span>${estadoLabel(r)}</span>`;
}

function resumen(texto, max = 95) {
  const limpio = String(texto ?? '').replace(/\s+/g, ' ').trim();
  if (!limpio) return '';
  return limpio.length > max ? `${limpio.substring(0, max)}…` : limpio;
}

function toast(mensaje, tipo = 'ok') {
  const container = document.getElementById('toast-container');
  if (!container) return;
  const id = `toast-inc-${Date.now()}`;
  const icono = tipo === 'ok' ? 'check_circle' : 'error';
  const color = tipo === 'ok' ? 'text-success' : 'text-danger';
  container.insertAdjacentHTML('beforeend',
    `<div id="${id}" class="toast align-items-center border-0 shadow-sm" role="alert">
       <div class="d-flex">
         <div class="toast-body d-flex align-items-center gap-2 cc-inc-toast-body">
           <span class="material-symbols-outlined ${color}">${icono}</span>
           <span class="cc-inc-toast-msg"></span>
         </div>
         <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Cerrar"></button>
       </div>
     </div>`);
  const el = document.getElementById(id);
  el.querySelector('.cc-inc-toast-msg').textContent = mensaje;
  const t = new bootstrap.Toast(el, { delay: 3500 });
  el.addEventListener('hidden.bs.toast', () => el.remove(), { once: true });
  t.show();
}

/* --------------------------------------------------
   Métricas
-------------------------------------------------- */

function renderMetricas(lista = incidencias) {
  const total = lista.length;
  const pendientes = lista.filter(r => estadoDe(r) === 'pendiente');
  const atendidas = total - pendientes.length;
  const edades = pendientes.map(r => diasDesde(r.fechaCreacion)).filter(v => v !== null);
  const promedio = edades.length
    ? Math.round(edades.reduce((a, b) => a + b, 0) / edades.length)
    : 0;

  const datos = [
    { label: 'Total', valor: total, borde: '#64748b', nota: 'incidencias registradas' },
    { label: 'Pendientes', valor: pendientes.length, borde: '#f59e0b', nota: 'sin atender' },
    { label: 'Atendidas', valor: atendidas, borde: '#22c55e', nota: 'con respuesta' },
    {
      label: 'Días pendiente',
      valor: promedio,
      borde: '#3b82f6',
      nota: pendientes.length ? 'antigüedad media' : 'sin incidencias abiertas'
    },
  ];

  const contenedor = document.getElementById('inc-metricas');
  if (!contenedor) return;

  const tarjetas = datos.map(d => {
    const col = document.createElement('div');
    col.className = 'col-6 col-xl-3';

    const card = document.createElement('div');
    card.className = 'cc-inc-metrica-card';
    card.style.borderLeftColor = d.borde;

    const label = document.createElement('p');
    label.className = 'cc-inc-metrica-label';
    label.textContent = d.label;

    const valor = document.createElement('p');
    valor.className = 'cc-inc-metrica-valor';
    valor.textContent = d.valor;

    const nota = document.createElement('p');
    nota.className = 'cc-inc-metrica-nota';
    nota.textContent = d.nota;

    card.append(label, valor, nota);
    col.appendChild(card);
    return col;
  });

  contenedor.replaceChildren(...tarjetas);
}

/* --------------------------------------------------
   Tabla
-------------------------------------------------- */

function accionesDe(r) {
  const id = esc(r.id);
  let html = `<button class="cc-inc-btn cc-inc-btn--ver" data-ver="${id}" title="Ver detalle">
      <span class="material-symbols-outlined">visibility</span>
    </button>`;

  if (INC.esAdmin) {
    if (estadoDe(r) !== 'atendido') {
      html += `<button class="cc-inc-btn cc-inc-btn--atender" data-atender="${id}" title="Atender">
        <span class="material-symbols-outlined">task_alt</span>
      </button>`;
    }
    html += `<button class="cc-inc-btn cc-inc-btn--borrar" data-eliminar="${id}" title="Eliminar">
      <span class="material-symbols-outlined">delete</span>
    </button>`;
  }

  return `<td class="text-end">
    <div class="d-flex justify-content-end gap-1">${html}</div>
  </td>`;
}

function renderTabla(lista = listaVisible) {
  if (!dtInstance) return;

  listaVisible = lista;
  dtInstance.clear();

  lista.forEach(r => {
    const atendido = estadoDe(r) === 'atendido';
    const por = atendido ? (r.atendidoPor || 'Administrador') : '—';

    dtInstance.row.add([
      `<div>
         <div class="cc-inc-titulo">${esc(r.titulo)}</div>
         <div class="cc-inc-desc" title="${esc(resumen(r.descripcion, 180))}">${esc(resumen(r.descripcion))}</div>
       </div>`,
      `<span class="cc-inc-tipo">${esc(r.tipo || 'otro')}</span>`,
      esc(r.ambiente || '—'),
      esc(r.reporta || '—'),
      `<span class="cc-inc-fecha">${esc(fmtFecha(r.fechaCreacion))}</span>
       <span class="cc-inc-edad">${esc(fmtEdad(r.fechaCreacion))}</span>`,
      badgeEstado(r),
      esc(por),
      accionesDe(r),
    ]);
  });

  dtInstance.draw();
}

function initDataTable() {
  dtInstance = $('#incidencias-table').DataTable({
    dom: '<"d-none"f>rt<"d-flex align-items-center justify-content-between px-3 py-2 border-top"ip>',
    language: {
      info: 'Mostrando _START_ - _END_ de _TOTAL_ incidencia(s)',
      infoEmpty: 'Sin incidencias disponibles',
      infoFiltered: '(filtradas de _MAX_ totales)',
      emptyTable: 'No hay incidencias con los filtros aplicados.',
      paginate: { previous: '«', next: '»' },
    },
    pageLength: 10,
    ordering: true,
    order: [[4, 'desc']],
    responsive: true,
    autoWidth: false,
    columnDefs: [{ orderable: false, targets: 7 }],
  });
}

/* --------------------------------------------------
   Filtros
-------------------------------------------------- */

function llenarFiltro(select, valores, placeholder) {
  if (!select) return;
  const anterior = select.value;
  select.replaceChildren(new Option(placeholder, ''));
  valores.forEach(v => select.add(new Option(v, v)));
  if (valores.includes(anterior)) select.value = anterior;
}

function aplicarFiltros() {
  const busq = (document.getElementById('filtro-busqueda')?.value ?? '').trim().toLowerCase();
  const estado = document.getElementById('filtro-estado')?.value ?? '';
  const tipo = document.getElementById('filtro-tipo')?.value ?? '';
  const ambiente = document.getElementById('filtro-ambiente')?.value ?? '';

  const lista = incidencias.filter(r => {
    const texto = [r.titulo, r.descripcion, r.reporta, r.ambiente]
      .join(' ')
      .toLowerCase();
    return (!busq || texto.includes(busq))
      && (!estado || estadoDe(r) === estado)
      && (!tipo || String(r.tipo || '') === tipo)
      && (!ambiente || String(r.ambiente || '') === ambiente);
  });

  renderTabla(lista);
  renderMetricas(lista);
}

function initFiltros() {
  const form = document.getElementById('form-filtros');
  const ids = ['filtro-busqueda', 'filtro-estado', 'filtro-tipo', 'filtro-ambiente'];

  ids.forEach(id => {
    const el = document.getElementById(id);
    el?.addEventListener('input', aplicarFiltros);
    el?.addEventListener('change', aplicarFiltros);
  });

  form?.addEventListener('submit', e => { e.preventDefault(); aplicarFiltros(); });
  form?.addEventListener('reset', () => setTimeout(aplicarFiltros, 0));
}

/* --------------------------------------------------
   Carga de datos
-------------------------------------------------- */

async function cargar() {
  const cuerpo = document.getElementById('cuerpoTabla');
  cuerpo.innerHTML = '<tr><td colspan="8" class="cc-inc-empty">Cargando incidencias…</td></tr>';

  try {
    const resp = await fetch('ConsultarReportes', { headers: { 'Accept': 'application/json' } });
    if (!resp.ok) throw new Error('El servidor no respondió correctamente');
    const datos = await resp.json();
    incidencias = Array.isArray(datos) ? datos : [];

    const tipos = [...new Set(incidencias.map(r => String(r.tipo || '').trim()).filter(Boolean))].sort();
    const ambientes = [...new Set(incidencias.map(r => String(r.ambiente || '').trim()).filter(Boolean))].sort();
    llenarFiltro(document.getElementById('filtro-tipo'), tipos, 'Tipo');
    llenarFiltro(document.getElementById('filtro-ambiente'), ambientes, 'Ambiente');

    aplicarFiltros();
  } catch (err) {
    incidencias = [];
    listaVisible = [];
    dtInstance?.clear().draw();
    renderMetricas([]);
    cuerpo.innerHTML = `<tr><td colspan="8" class="cc-inc-empty text-danger">
      ${esc(err.message || 'No se pudieron cargar las incidencias.')}
    </td></tr>`;
  }
}

async function cargarAmbientes() {
  try {
    const resp = await fetch('ConsultarAmbientes', { headers: { 'Accept': 'application/json' } });
    if (!resp.ok) return;
    const datos = await resp.json();
    ambientesSel = Array.isArray(datos) ? datos : [];
    const sel = document.getElementById('selAmbiente');
    if (!sel) return;
    sel.replaceChildren(new Option('Ninguno', ''));
    ambientesSel.forEach(a => sel.add(new Option(String(a.descripcion ?? ''), String(a.id))));
  } catch (_) {
    /* El ambiente es opcional en el reporte. */
  }
}

/* --------------------------------------------------
   Detalle
-------------------------------------------------- */

function verIncidencia(id) {
  const r = incidencias.find(x => String(x.id) === String(id));
  if (!r) return;

  document.getElementById('verTitulo').textContent = r.titulo || '—';
  document.getElementById('verMeta').textContent =
    `${estadoLabel(r)} · ${r.tipo || 'otro'} · ${r.ambiente || 'Sin ambiente'} · ${fmtFecha(r.fechaCreacion)}`;
  document.getElementById('verDescripcion').textContent =
    r.descripcion || 'El reporte no incluye descripción.';

  const creada = `<div class="cc-inc-timeline-item">
      <span class="cc-inc-timeline-dot"></span>
      <div class="cc-inc-timeline-titulo">Reportada por ${esc(r.reporta || 'Instructor')}</div>
      <div class="cc-inc-timeline-meta">${esc(fmtFecha(r.fechaCreacion))}</div>
    </div>`;

  const seguimiento = estadoDe(r) === 'atendido'
    ? `<div class="cc-inc-timeline-item cc-inc-timeline-item--atendido">
         <span class="cc-inc-timeline-dot"></span>
         <div class="cc-inc-timeline-titulo">Atendida por ${esc(r.atendidoPor || 'Administrador')}</div>
         <div class="cc-inc-timeline-meta">${esc(fmtFecha(r.fechaAtencion))}</div>
         <div class="cc-inc-timeline-respuesta">${esc(r.respuestaAdmin || 'Sin respuesta registrada.')}</div>
       </div>`
    : `<div class="cc-inc-timeline-item">
         <span class="cc-inc-timeline-dot" style="background:var(--cc-muted)"></span>
         <div class="cc-inc-timeline-titulo">Pendiente de atención</div>
         <div class="cc-inc-timeline-meta">Sin respuesta aún</div>
       </div>`;

  document.getElementById('verTimeline').innerHTML = creada + seguimiento;
  bootstrap.Modal.getOrCreateInstance(document.getElementById('modalVerIncidencia')).show();
}

/* --------------------------------------------------
   Atender / eliminar
-------------------------------------------------- */

function actualizarContador(textareaId, contadorId, max) {
  const ta = document.getElementById(textareaId);
  const c = document.getElementById(contadorId);
  if (!ta || !c) return;
  const actualizar = () => { c.textContent = `${ta.value.length}/${max}`; };
  ta.addEventListener('input', actualizar);
  actualizar();
}

function abrirAtender(id, titulo) {
  idAtender = id;
  document.getElementById('atenderTitulo').textContent = `Atender: ${titulo}`;
  const ta = document.getElementById('respuestaAtender');
  ta.value = '';
  ta.dispatchEvent(new Event('input'));
  bootstrap.Modal.getOrCreateInstance(document.getElementById('modalAtender')).show();
}

async function guardarAtencion() {
  const respuesta = document.getElementById('respuestaAtender').value.trim();
  if (!respuesta) { toast('Escribe la respuesta antes de guardar.', 'error'); return; }
  try {
    const resp = await fetch('AtenderReporte', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ id: String(idAtender), respuesta }),
    });
    const data = await resp.json().catch(() => ({}));
    if (!resp.ok) throw new Error(data.error || 'No se pudo registrar la atención');
    bootstrap.Modal.getInstance(document.getElementById('modalAtender'))?.hide();
    toast('Incidencia atendida.', 'ok');
    void cargar();
  } catch (err) {
    toast(err.message || 'Error al atender.', 'error');
  }
}

function abrirConfirmarEliminar(id, titulo) {
  idEliminar = id;
  document.getElementById('eliminarIncidenciaNombre').textContent = titulo || 'Incidencia';
  bootstrap.Modal.getOrCreateInstance(document.getElementById('modalEliminarIncidencia')).show();
}

async function eliminar() {
  if (idEliminar === null) return;
  const modal = document.getElementById('modalEliminarIncidencia');
  try {
    const resp = await fetch('EliminarReporte', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ id: String(idEliminar) }),
    });
    const data = await resp.json().catch(() => ({}));
    if (!resp.ok) throw new Error(data.error || 'No se pudo eliminar');
    bootstrap.Modal.getInstance(modal)?.hide();
    toast('Incidencia eliminada.', 'ok');
    void cargar();
  } catch (err) {
    bootstrap.Modal.getInstance(modal)?.hide();
    toast(err.message || 'Error al eliminar.', 'error');
  }
}

/* --------------------------------------------------
   Reportar (instructor)
-------------------------------------------------- */

function abrirNuevo() {
  document.getElementById('nuevaTitulo').value = '';
  document.getElementById('selTipoNuevo').value = 'ambiente';
  document.getElementById('selAmbiente').value = '';
  const ta = document.getElementById('nuevaDescripcion');
  ta.value = '';
  ta.dispatchEvent(new Event('input'));
  bootstrap.Modal.getOrCreateInstance(document.getElementById('modalNueva')).show();
}

async function enviarReporte() {
  const titulo = document.getElementById('nuevaTitulo').value.trim();
  const descripcion = document.getElementById('nuevaDescripcion').value.trim();
  if (!titulo) { toast('Escribe un título para el reporte.', 'error'); return; }
  if (!descripcion) { toast('Describe qué ocurrió para poder atenderlo.', 'error'); return; }

  const params = {
    titulo,
    tipo: document.getElementById('selTipoNuevo').value,
    descripcion,
  };
  const amb = document.getElementById('selAmbiente').value;
  if (amb) params.ambienteId = amb;

  try {
    const resp = await fetch('RegistrarReporte', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams(params),
    });
    const data = await resp.json().catch(() => ({}));
    if (!resp.ok) throw new Error(data.error || 'No se pudo enviar el reporte');
    bootstrap.Modal.getInstance(document.getElementById('modalNueva'))?.hide();
    toast('Reporte enviado al administrador.', 'ok');
    void cargar();
  } catch (err) {
    toast(err.message || 'Error al enviar el reporte.', 'error');
  }
}

/* --------------------------------------------------
   Exportación
-------------------------------------------------- */

function descargarCSV() {
  if (!listaVisible.length) { toast('No hay incidencias para descargar.', 'error'); return; }

  const cabeceras = [
    'ID', 'Título', 'Tipo', 'Ambiente', 'Reporta', 'Fecha creación',
    'Estado', 'Atendido por', 'Fecha atención', 'Descripción', 'Respuesta',
  ];
  const filas = listaVisible.map(r => [
    r.id,
    r.titulo || '',
    r.tipo || '',
    r.ambiente || '',
    r.reporta || '',
    fmtFecha(r.fechaCreacion),
    estadoLabel(r),
    estadoDe(r) === 'atendido' ? (r.atendidoPor || '') : '',
    estadoDe(r) === 'atendido' ? fmtFecha(r.fechaAtencion) : '',
    r.descripcion || '',
    r.respuestaAdmin || '',
  ]);

  const csv = [cabeceras, ...filas]
    .map(fila => fila.map(c => `"${String(c ?? '').replace(/"/g, '""')}"`).join(';'))
    .join('\r\n');

  const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const a = Object.assign(document.createElement('a'), {
    href: url,
    download: 'incidencias_classcontrol.csv',
  });
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
  toast(`Se exportaron ${listaVisible.length} incidencia(s).`, 'ok');
}

/* --------------------------------------------------
   Bootstrap
-------------------------------------------------- */

document.addEventListener('DOMContentLoaded', () => {
  initDataTable();
  initFiltros();
  actualizarContador('nuevaDescripcion', 'nuevaDescripcionContador', 1000);
  actualizarContador('respuestaAtender', 'respuestaAtenderContador', 255);

  document.getElementById('btnActualizar')?.addEventListener('click', () => { void cargar(); });
  document.getElementById('btnCSV')?.addEventListener('click', descargarCSV);
  document.getElementById('btnGuardarAtencion')?.addEventListener('click', () => { void guardarAtencion(); });
  document.getElementById('btnConfirmarEliminar')?.addEventListener('click', () => { void eliminar(); });

  if (INC.esInstructor) {
    document.getElementById('btnNueva')?.addEventListener('click', abrirNuevo);
    document.getElementById('btnEnviarReporte')?.addEventListener('click', () => { void enviarReporte(); });
    void cargarAmbientes();
  }

  document.getElementById('modalEliminarIncidencia')?.addEventListener('hidden.bs.modal', () => {
    idEliminar = null;
  });

  document.getElementById('cuerpoTabla')?.addEventListener('click', e => {
    const bAt = e.target.closest('[data-atender]');
    const bEl = e.target.closest('[data-eliminar]');
    const bVr = e.target.closest('[data-ver]');
    if (bAt) {
      const r = incidencias.find(i => String(i.id) === bAt.dataset.atender);
      if (r) abrirAtender(r.id, r.titulo);
    }
    if (bEl) {
      const r = incidencias.find(i => String(i.id) === bEl.dataset.eliminar);
      if (r) abrirConfirmarEliminar(r.id, r.titulo);
    }
    if (bVr) verIncidencia(bVr.dataset.ver);
  });

  void cargar();
});
