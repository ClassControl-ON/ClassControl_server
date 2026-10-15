/* ============================================================
   AmbientesJS.js - ClassControl
   CRUD + DataTables + Chart.js + Filtro por sede
   Datos reales desde el backend:
   - GET  ConsultarAmbientes -> lista de ambientes (JSON)
   - GET  ConsultarSedes     -> catálogo de sedes
   - POST RegistrarAmbiente  -> crear ambiente
   - POST ActualizarAmbiente -> editar ambiente
   - POST EliminarAmbiente   -> eliminar ambiente
   ============================================================ */

'use strict';

/* ------------------------------------------
   1. ESTADO EN MEMORIA (poblado desde el backend)
------------------------------------------ */
let ambientes = [];
let sedes = [];
let ocupacionIds = new Set();

function estadoEfectivo(ambiente) {
  const guardado = String(ambiente.estado || 'Disponible');
  if (guardado === 'Mantenimiento' || guardado === 'Inhabilitado') return guardado;
  return ocupacionIds.has(Number(ambiente.id)) ? 'Ocupado' : 'Disponible';
}

function renderEstado(data, type, row) {
  const estado = estadoEfectivo(row);
  if (type === 'filter' || type === 'sort') return estado;
  return `<span class="status-pill status-${estado.toLowerCase()}">${estado}</span>`;
}

async function cargarProgramaciones() {
  try {
    const resp = await fetch('ConsultarProgramaciones');
    if (!resp.ok) return;
    const progs = await resp.json();
    ocupacionIds = new Set((Array.isArray(progs) ? progs : [])
      .map(p => Number(p.ambienteId ?? p.ambiente_id ?? p.Ambientes_id_ambientes))
      .filter(x => x > 0));
  } catch (_) {
    /* sin datos de programación, todos se muestran según su estado guardado */
  }
}

let tablaAmbientes;
let chartAmbientes;
let deleteTargetId = null;

/* ------------------------------------------
   2. UTILIDADES
------------------------------------------ */
const $id = id => document.getElementById(id);

function escapeHtml(value) {
  return ClassControl.escapeHtml(value);
}

function escapeRegex(value) {
  return String(value).replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

function sedeNombre(id) {
  const sede = sedes.find(s => s.id === id);
  return sede ? sede.nombre : '-';
}

function llenarSelectSedes(select, placeholder) {
  if (!select) return;
  const actual = select.value;
  select.innerHTML = `<option value="">${placeholder}</option>` +
    sedes.map(s => `<option value="${s.id}">${escapeHtml(s.nombre)}</option>`).join('');
  if (actual) select.value = actual;
}

/* ------------------------------------------
   3. CARGA DE DATOS DESDE EL BACKEND
------------------------------------------ */
async function cargarSedes() {
  const resp = await fetch('ConsultarSedes');
  sedes = resp.ok ? await resp.json() : [];
  llenarSelectSedes($id('filter-sede'), 'Todas');
  llenarSelectSedes($id('amb-sede'), 'Seleccionar…');
}

async function cargarAmbientes() {
  const resp = await fetch('ConsultarAmbientes');
  if (!resp.ok) throw new Error('No se pudo cargar la lista de ambientes');
  ambientes = await resp.json();
  reloadTable();
  applyExternalFilters();
  updateStats();
  updateChart();
}

async function inicializarDatos() {
  try {
    await cargarSedes();
    await cargarProgramaciones();
    await cargarAmbientes();
  } catch (err) {
    console.error(err);
    showToast('No se pudieron cargar los datos desde el servidor.', 'danger');
  }
}

/* ------------------------------------------
   4. RENDERERS DATATABLES
------------------------------------------ */
function renderAmbiente(data, type, row) {
  if (type === 'filter' || type === 'sort') return row.descripcion;
  return `
    <div class="ambiente-cell">
      <span class="ambiente-icon">
        <span class="material-symbols-outlined">meeting_room</span>
      </span>
      <span>
        <span class="ambiente-name">${escapeHtml(row.descripcion)}</span>
      </span>
    </div>`;
}

function renderSede(data, type, row) {
  const nombre = sedeNombre(row.sedeId);
  if (type === 'filter' || type === 'sort') return nombre;
  return escapeHtml(nombre);
}

function renderAcciones(id) {
  const puedeGestionar = !!window.ccPuedeGestionarAmbientes;
  let html = `
    <span class="row-actions">
      <button class="cc-btn-icon cc-btn-icon-view btn-ver btn-view-row" type="button" data-id="${id}"
              title="Ver" aria-label="Ver ambiente">
        <span class="material-symbols-outlined">visibility</span>
      </button>`;
  if (puedeGestionar) {
    html += `
      <button class="btn btn-edit btn-edit-row" type="button" data-id="${id}"
              title="Editar" aria-label="Editar ambiente">
        <span class="material-symbols-outlined">edit</span>
      </button>
      <button class="btn btn-delete btn-delete-row" type="button" data-id="${id}"
              title="Eliminar" aria-label="Eliminar ambiente">
        <span class="material-symbols-outlined">delete</span>
      </button>`;
  }
  html += `</span>`;
  return html;
}

/* ------------------------------------------
   5. DATATABLES
------------------------------------------ */
function initDataTable() {
  tablaAmbientes = $('#tabla-ambientes').DataTable({
    data: ambientes,
    responsive: true,
    pageLength: 5,
    lengthMenu: [5, 10, 25],
    language: {
      emptyTable:    'No hay ambientes registrados',
      info:          'Mostrando _START_ a _END_ de _TOTAL_ ambientes',
      infoEmpty:     'Mostrando 0 ambientes',
      infoFiltered:  '(filtrado de _MAX_ ambientes)',
      lengthMenu:    'Ver _MENU_',
      loadingRecords:'Cargando...',
      processing:    'Procesando...',
      search:        'Buscar:',
      zeroRecords:   'No se encontraron ambientes',
      paginate: {
        first: 'Primero', last: 'último',
        next: 'Siguiente', previous: 'Anterior',
      },
    },
    columns: [
      { data: null,       render: renderAmbiente },
      { data: null,       render: renderSede },
      { data: 'capacidad', className: 'text-center',
        render: data => `${data} <span class="text-muted" style="font-size:.75rem">personas</span>` },
      { data: null, render: renderEstado },
      { data: 'id', orderable: false, searchable: false,
        className: 'text-end', render: renderAcciones },
    ],
  });
}

function reloadTable() {
  if (!tablaAmbientes) return;
  tablaAmbientes.clear();
  tablaAmbientes.rows.add(ambientes);
  tablaAmbientes.draw();
}

function applyExternalFilters() {
  if (!tablaAmbientes) return;
  const sede = $id('filter-sede').value;
  const estado = $id('filter-estado') ? $id('filter-estado').value : '';
  tablaAmbientes.column(1).search(sede ? `^${escapeRegex(sedeNombre(Number(sede)))}$` : '', true, false);
  if (estado) {
    tablaAmbientes.column(3).search(`^${escapeRegex(estado)}$`, true, false);
  } else {
    tablaAmbientes.column(3).search('');
  }
  tablaAmbientes.draw();
}

/* ------------------------------------------
   6. ESTADÍSTICAS
------------------------------------------ */
function updateStats() {
  const totalCapacidad = ambientes.reduce((s, a) => s + Number(a.capacidad), 0);
  const sedesConAmbientes = new Set(ambientes.map(a => a.sedeId)).size;
  const promedio = ambientes.length ? Math.round(totalCapacidad / ambientes.length) : 0;

  $id('stat-total').textContent    = ambientes.length;
  $id('stat-capacidad').textContent = totalCapacidad;
  $id('stat-sedes').textContent    = sedesConAmbientes;
  $id('stat-promedio').textContent = promedio;
}

/* ------------------------------------------
   7. GRÁFICA CHART.JS (donut por sede)
------------------------------------------ */
function getChartData() {
  return sedes.map(s => ambientes.filter(a => a.sedeId === s.id).length);
}

function initChart() {
  const ctx = $id('ambientesChart');
  if (!ctx) return;

  chartAmbientes = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: sedes.map(s => s.nombre),
      datasets: [{
        data: getChartData(),
        backgroundColor: [ClassControl.colors.primary, ClassControl.colors.warning, ClassControl.colors.muted, ClassControl.colors.danger, ClassControl.colors.info],
        borderWidth: 0,
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      cutout: '64%',
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            boxWidth: 12,
            font: { family: 'DM Sans', size: 12, weight: '600' },
          },
        },
      },
    },
  });
}

function updateChart() {
  if (!chartAmbientes) return;
  chartAmbientes.data.labels = sedes.map(s => s.nombre);
  chartAmbientes.data.datasets[0].data = getChartData();
  chartAmbientes.update();
}

/* ------------------------------------------
   8. TOAST (CSS puro)
------------------------------------------ */
let toastTimer = null;

const TOAST_VARIANTS = {
  success: { icon: 'check_circle',  color: 'var(--cc-primary)' },
  danger:  { icon: 'cancel',        color: ClassControl.colors.danger  },
  info:    { icon: 'info',          color: ClassControl.colors.info    },
  warning: { icon: 'warning',       color: ClassControl.colors.warning },
};

function showToast(message, variant = 'success') {
  const toastEl  = $id('toast');
  const msgEl    = $id('toast-msg');
  const iconEl   = $id('toast-icon');
  if (!toastEl || !msgEl || !iconEl) return;

  const v = TOAST_VARIANTS[variant] ?? TOAST_VARIANTS.success;
  msgEl.textContent  = message;
  iconEl.textContent = v.icon;
  iconEl.style.color = v.color;
  toastEl.classList.add('show');
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toastEl.classList.remove('show'), 3200);
}

/* ------------------------------------------
   9. FORMULARIO HELPERS
------------------------------------------ */
function resetForm() {
  const form = $id('form-ambiente');
  form.reset();
  form.classList.remove('was-validated');
  form.action = 'RegistrarAmbiente';
  $id('amb-id').value = '';
  $id('ambienteModalLabel').textContent = 'Nuevo Ambiente';
}

function setFormData(ambiente) {
  $id('amb-id').value        = ambiente.id;
  $id('amb-nombre').value    = ambiente.descripcion;
  $id('amb-sede').value      = ambiente.sedeId;
  $id('amb-capacidad').value = ambiente.capacidad;
  if ($id('amb-estado')) $id('amb-estado').value = String(ambiente.estado || 'Disponible');
}

/* ------------------------------------------
   10. GUARDAR (crear / editar) vía fetch
------------------------------------------ */
async function saveAmbiente(event) {
  event.preventDefault();
  const form = event.currentTarget;
  form.classList.add('was-validated');

  if (!form.checkValidity()) {
    showToast('Revise los campos requeridos.', 'danger');
    return;
  }

  const id = Number($id('amb-id').value) || null;
  const params = new URLSearchParams({
    descripcion_Ambiente: $id('amb-nombre').value.trim(),
    Sede_id_sede: $id('amb-sede').value,
    capacidad: $id('amb-capacidad').value,
    estado_Ambiente: $id('amb-estado') ? ($id('amb-estado').value || 'Disponible') : 'Disponible',
  });
  if (id) params.set('id', String(id));

  const url = id ? 'ActualizarAmbiente' : 'RegistrarAmbiente';

  try {
    const resp = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params,
    });
    if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }

    await cargarAmbientes();
    showToast(id ? 'Ambiente actualizado correctamente.' : 'Ambiente registrado correctamente.', 'success');
    bootstrap.Modal.getInstance($id('ambienteModal')).hide();
  } catch (err) {
    console.error(err);
    showToast('No se pudo guardar el ambiente. Verifica los datos e intenta de nuevo.', 'danger');
  }
}

/* ------------------------------------------
   11. MODALES EDITAR / ELIMINAR
------------------------------------------ */
function openEditModal(id) {
  const ambiente = ambientes.find(a => a.id === id);
  if (!ambiente) return;
  resetForm();
  setFormData(ambiente);
  $id('ambienteModalLabel').textContent = 'Editar Ambiente';
  bootstrap.Modal.getOrCreateInstance($id('ambienteModal')).show();
}

function openDeleteModal(id) {
  const ambiente = ambientes.find(a => a.id === id);
  if (!ambiente) return;
  deleteTargetId = id;
  $id('delete-msg').textContent =
    `¿Seguro que desea eliminar "${ambiente.descripcion}"? Esta acción no se puede deshacer.`;
  bootstrap.Modal.getOrCreateInstance($id('deleteModal')).show();
}

function openViewModal(id) {
  const ambiente = ambientes.find(a => a.id === id);
  if (!ambiente) return;
  const estado = estadoEfectivo(ambiente);
  $id('view-nombre').textContent = ambiente.descripcion || '-';
  $id('view-sede').textContent   = sedeNombre(ambiente.sedeId) || '-';
  $id('view-capacidad').textContent = ambiente.capacidad != null ? `${ambiente.capacidad} personas` : '-';
  $id('view-estado').textContent = estado || '-';
  bootstrap.Modal.getOrCreateInstance($id('ambienteViewModal')).show();
}

async function confirmDelete() {
  if (!deleteTargetId) return;
  const ambiente = ambientes.find(a => a.id === deleteTargetId);
  const nombre = ambiente?.descripcion ?? 'Ambiente';

  try {
    const resp = await fetch('EliminarAmbiente', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ id: String(deleteTargetId) }),
    });
    if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }

    deleteTargetId = null;
    await cargarAmbientes();
    bootstrap.Modal.getInstance($id('deleteModal')).hide();
    showToast(`"${nombre}" eliminado.`, 'info');
  } catch (err) {
    console.error(err);
    showToast(err.message || 'No se pudo eliminar el ambiente.', 'danger');
  }
}

/* ------------------------------------------
   12. EVENTOS
------------------------------------------ */
function wireEvents() {
  /* Filtro externo */
  $id('filter-sede').addEventListener('change', applyExternalFilters);
  if ($id('filter-estado')) $id('filter-estado').addEventListener('change', applyExternalFilters);

  /* Nuevo ambiente (solo admin) */
  const puedeGestionar = !!window.ccPuedeGestionarAmbientes;
  if (puedeGestionar && $id('btn-nuevo')) {
    $id('btn-nuevo').addEventListener('click', resetForm);

    /* Formulario submit */
    $id('form-ambiente').addEventListener('submit', saveAmbiente);

    /* Confirmar eliminar */
    $id('btn-confirmar-delete').addEventListener('click', confirmDelete);

    /* Atajo Alt+N - nuevo ambiente */
    document.addEventListener('keydown', e => {
      if (e.altKey && e.key === 'n') {
        e.preventDefault();
        resetForm();
        bootstrap.Modal.getOrCreateInstance($id('ambienteModal')).show();
      }
    });
  }

  /* Delegación en tabla */
  $id('tabla-ambientes').addEventListener('click', event => {
    const viewBtn    = event.target.closest('.btn-view-row');
    const editBtn    = event.target.closest('.btn-edit-row');
    const deleteBtn  = event.target.closest('.btn-delete-row');
    if (viewBtn)   openViewModal(Number(viewBtn.dataset.id));
    if (editBtn && puedeGestionar)   openEditModal(Number(editBtn.dataset.id));
    if (deleteBtn && puedeGestionar) openDeleteModal(Number(deleteBtn.dataset.id));
  });

}

/* ------------------------------------------
   13. INICIALIZACIÓN
------------------------------------------ */
document.addEventListener('DOMContentLoaded', () => {
  initDataTable();
  initChart();
  wireEvents();
  inicializarDatos();
});
