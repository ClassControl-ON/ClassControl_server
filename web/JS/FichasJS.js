/**
 * FichasJS.js - ClassControl
 *
 * Datos reales desde el backend:
 * - GET  ConsultarFichas      -> lista de fichas (JSON)
 * - GET  ConsultarProgramas   -> catálogo de programas
 * - GET  ConsultarSedes       -> catálogo de sedes
 * - GET  ConsultarModalidades -> catálogo de modalidades
 * - GET  ConsultarNiveles     -> catálogo de niveles de formación
 * - GET  ConsultarEstados     -> catálogo de estados
 * - GET  ConsultarJornadas    -> catálogo de jornadas
 * - GET  ConsultarEtapas      -> catálogo de etapas
 * - POST RegistrarFicha       -> crear ficha
 * - POST ActualizarFicha      -> editar ficha
 * - POST EliminarFicha        -> eliminar ficha
 */

'use strict';

/* ------------------------------------------
   1. ESTADO EN MEMORIA (poblado desde el backend)
------------------------------------------ */
let fichas = [];
let programas = [];
let sedes = [];
let modalidades = [];
let niveles = [];
let estados = [];
let jornadas = [];
let etapas = [];


/* ------------------------------------------
   2. UTILIDADES
------------------------------------------ */
function formatDate(iso) {
  if (!iso) return '-';
  const [y, m, d] = iso.split('-');
  return `${d}/${m}/${y}`;
}

function nombrePor(lista, id, campo) {
  const item = lista.find(x => x.id === id);
  return item ? item[campo] : '-';
}

function programaNombre(id) { return nombrePor(programas, id, 'nombre'); }
function sedeNombre(id) { return nombrePor(sedes, id, 'nombre'); }
function modalidadNombre(id) { return nombrePor(modalidades, id, 'descripcion'); }
function nivelNombre(id) { return nombrePor(niveles, id, 'descripcion'); }
function estadoNombre(id) { return nombrePor(estados, id, 'descripcion'); }

function modalidadChip(id) {
  const nombre = modalidadNombre(id);
  const iconMap = { Presencial: 'home', Virtual: 'laptop_mac', Distancia: 'directions_walk' };
  const clsMap = { Presencial: 'cc-chip-presencial', Virtual: 'cc-chip-virtual', Distancia: 'cc-chip-distancia' };
  const icon = iconMap[nombre] ?? 'help';
  const cls = clsMap[nombre] ?? 'cc-chip-presencial';
  return `<span class="cc-chip ${cls}"><span class="material-symbols-outlined">${icon}</span>${ClassControl.escapeHtml(nombre)}</span>`;
}

function estadoBadge(id) {
  const nombre = estadoNombre(id);
  const map = {
    'Activa': 'cc-badge-activa',
    'En proceso': 'cc-badge-proceso',
    'Finalizada': 'cc-badge-finalizada',
  };
  return `<span class="cc-badge ${map[nombre] ?? 'cc-badge-proceso'}">${ClassControl.escapeHtml(nombre)}</span>`;
}

function llenarSelect(select, lista, placeholder, campoTexto) {
  if (!select) return;
  const actual = select.value;
  select.innerHTML = `<option value="">${placeholder}</option>` +
    lista.map(item => `<option value="${item.id}">${ClassControl.escapeHtml(item[campoTexto])}</option>`).join('');
  if (actual) select.value = actual;
}

function llenarFiltro(select, lista, placeholder, campoTexto) {
  if (!select) return;
  select.innerHTML = `<option value="">${placeholder}</option>` +
    lista.map(item => `<option value="${ClassControl.escapeHtml(item[campoTexto])}">${ClassControl.escapeHtml(item[campoTexto])}</option>`).join('');
}


/* ------------------------------------------
   3. CARGA DE DATOS DESDE EL BACKEND
------------------------------------------ */
async function cargarCatalogos() {
  const [progR, sedeR, modR, nivR, estR, jorR, etaR] = await Promise.all([
    fetch('ConsultarProgramas'),
    fetch('ConsultarSedes'),
    fetch('ConsultarModalidades'),
    fetch('ConsultarNiveles'),
    fetch('ConsultarEstados'),
    fetch('ConsultarJornadas'),
    fetch('ConsultarEtapas'),
  ]);

  programas = progR.ok ? await progR.json() : [];
  sedes = sedeR.ok ? await sedeR.json() : [];
  modalidades = modR.ok ? await modR.json() : [];
  niveles = nivR.ok ? await nivR.json() : [];
  estados = estR.ok ? await estR.json() : [];
  jornadas = jorR.ok ? await jorR.json() : [];
  etapas = etaR.ok ? await etaR.json() : [];

  llenarSelect(document.getElementById('f-programa'), programas, 'Seleccione...', 'nombre');
  llenarSelect(document.getElementById('f-sede'), sedes, 'Seleccione...', 'nombre');
  llenarSelect(document.getElementById('f-modalidad'), modalidades, 'Seleccione...', 'descripcion');
  llenarSelect(document.getElementById('f-nivel'), niveles, 'Seleccione...', 'descripcion');
  llenarSelect(document.getElementById('f-estado'), estados, 'Seleccione...', 'descripcion');
  llenarSelect(document.getElementById('f-jornada'), jornadas, 'Seleccione...', 'descripcion');
  llenarSelect(document.getElementById('f-etapa'), etapas, 'Seleccione...', 'descripcion');

  llenarFiltro(document.getElementById('filter-modalidad'), modalidades, 'Modalidad: Todas', 'descripcion');
  llenarFiltro(document.getElementById('filter-sede'), sedes, 'Sede: Todas', 'nombre');
  llenarFiltro(document.getElementById('filter-estado'), estados, 'Estado: Todos', 'descripcion');
}

async function cargarFichas() {
  const resp = await fetch('ConsultarFichas');
  if (!resp.ok) throw new Error('No se pudo cargar la lista de fichas');
  fichas = await resp.json();
  refreshTable();
}

async function inicializarDatos() {
  try {
    await cargarCatalogos();
    await cargarFichas();
  } catch (err) {
    console.error(err);
    showToast('No se pudieron cargar los datos desde el servidor.', 'error');
  }
}


/* ------------------------------------------
   4. DATATABLES - INIT
------------------------------------------ */
let dataTable;

function initDataTable() {
  dataTable = $('#tabla-fichas').DataTable({
    language: {
      url: 'https://cdn.datatables.net/plug-ins/1.13.8/i18n/es-ES.json',
    },
    pageLength: 8,
    order: [],
    columnDefs: [{ orderable: false, targets: -1 }],
    dom: '<"d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3"lf>t<"d-flex justify-content-between align-items-center flex-wrap gap-2 mt-3"ip>',
  });
}

$(document).ready(function () {

  initDataTable();

  document.getElementById('search-input')?.addEventListener('input', function () {
    dataTable.search(this.value).draw();
  });

  $.fn.dataTable.ext.search.push(function (settings, data) {
    if (settings.nTable.id !== 'tabla-fichas') return true;

    const estado    = document.getElementById('filter-estado')?.value    || '';
    const modalidad = document.getElementById('filter-modalidad')?.value || '';
    const sede      = document.getElementById('filter-sede')?.value      || '';

    const okEstado    = !estado    || data[5].includes(estado);
    const okModalidad = !modalidad || data[3].includes(modalidad);
    const okSede      = !sede      || data[1].includes(sede);

    return okEstado && okModalidad && okSede;
  });

  ['filter-estado', 'filter-modalidad', 'filter-sede'].forEach(id => {
    document.getElementById(id)?.addEventListener('change', () => dataTable.draw());
  });

  bindFormEvents();
  inicializarDatos();
});


/* ------------------------------------------
   5. RENDERIZADO DE FILAS
------------------------------------------ */
function rowHTML(f) {
  // Botones según permisos expuestos por Fichas.jsp:
  // ccPuedeEscribirFichas = Admin/Coordinador, ccPuedeEliminarFichas = solo Admin.
  const btnEditar = window.ccPuedeEscribirFichas ? `
          <button class="cc-row-btn cc-edit" data-edit="${f.id}" title="Editar">
            <span class="material-symbols-outlined">edit</span>
          </button>` : '';
  const btnEliminar = window.ccPuedeEliminarFichas ? `
          <button class="cc-row-btn cc-delete" data-del="${f.id}" title="Eliminar">
            <span class="material-symbols-outlined">delete</span>
          </button>` : '';
  return `
    <tr data-id="${f.id}">
      <td class="cc-ficha-codigo">${f.codigo}</td>
      <td>
        <div class="cc-prog-name">${ClassControl.escapeHtml(programaNombre(f.programaId))}</div>
        <div class="cc-prog-meta">${ClassControl.escapeHtml(nivelNombre(f.nivelFormacionId))} - ${ClassControl.escapeHtml(sedeNombre(f.sedeId))}</div>
      </td>
      <td class="text-center">
        <div class="cc-fecha-main">${formatDate(f.fechaInicio)}</div>
        <div class="cc-fecha-sub">a ${formatDate(f.fechaFin)}</div>
      </td>
      <td>${modalidadChip(f.modalidadId)}</td>
      <td class="text-center fw-bold">${f.cantidadAprendices ?? 0}</td>
      <td>${estadoBadge(f.estadoId)}</td>
      <td class="text-end">
        <div class="d-flex justify-content-end gap-1">
          <button class="cc-row-btn cc-view"   data-view="${f.id}" title="Ver detalle">
            <span class="material-symbols-outlined">visibility</span>
          </button>${btnEditar}${btnEliminar}
        </div>
      </td>
    </tr>`;
}

function renderAllRows() {
  const tbody = document.getElementById('fichas-tbody');
  if (!tbody) return;
  tbody.innerHTML = fichas.map(rowHTML).join('');
  attachRowEvents();
}

function refreshTable() {
  if (dataTable) {
    dataTable.clear().destroy();
  }
  renderAllRows();
  initDataTable();
}

function attachRowEvents() {
  document.querySelectorAll('[data-view]').forEach(btn =>
    btn.addEventListener('click', () => openDetail(+btn.dataset.view)));
  document.querySelectorAll('[data-edit]').forEach(btn =>
    btn.addEventListener('click', () => openEdit(+btn.dataset.edit)));
  document.querySelectorAll('[data-del]').forEach(btn =>
    btn.addEventListener('click', () => openConfirm(+btn.dataset.del)));
}


/* ------------------------------------------
   6. MODAL - NUEVA / EDITAR FICHA
------------------------------------------ */
const formModalEl = document.getElementById('modal-form');
const formModal   = formModalEl ? new bootstrap.Modal(formModalEl) : null;
const fichaForm   = document.getElementById('ficha-form');
const formTitle   = document.getElementById('form-modal-title');
const btnFormDel  = document.getElementById('btn-form-delete');

let editingId = null;

if (window.ccPuedeEscribirFichas) {
  document.getElementById('btn-new-ficha')?.addEventListener('click', openNew);
} else {
  document.getElementById('btn-new-ficha')?.classList.add('d-none');
}

function openNew() {
  editingId = null;
  if (formTitle) formTitle.innerHTML = '<span class="material-symbols-outlined me-2">note_add</span>Nueva Ficha';
  fichaForm?.reset();
  fichaForm?.classList.remove('was-validated');
  btnFormDel?.classList.add('d-none');
  formModal?.show();
}

function openEdit(id) {
  const f = fichas.find(x => x.id === id);
  if (!f) return;

  detailModal?.hide();
  editingId = id;

  if (formTitle) formTitle.innerHTML = '<span class="material-symbols-outlined me-2">edit</span>Editar Ficha';
  fichaForm?.classList.remove('was-validated');
  // El botón de eliminar del formulario solo se muestra para quien puede eliminar (Admin).
  if (window.ccPuedeEliminarFichas) {
    btnFormDel?.classList.remove('d-none');
  } else {
    btnFormDel?.classList.add('d-none');
  }

  document.getElementById('f-codigo').value     = f.codigo;
  document.getElementById('f-programa').value   = f.programaId;
  document.getElementById('f-nivel').value      = f.nivelFormacionId;
  document.getElementById('f-sede').value       = f.sedeId;
  document.getElementById('f-modalidad').value  = f.modalidadId;
  document.getElementById('f-jornada').value    = f.jornadaId;
  document.getElementById('f-etapa').value      = f.etapaId;
  document.getElementById('f-inicio').value     = f.fechaInicio ?? '';
  document.getElementById('f-fin').value        = f.fechaFin ?? '';
  document.getElementById('f-aprendices').value = f.cantidadAprendices ?? 0;
  document.getElementById('f-estado').value     = f.estadoId;

  formModal?.show();
}

function bindFormEvents() {
  fichaForm?.addEventListener('submit', async e => {
    e.preventDefault();
    fichaForm.classList.add('was-validated');

    if (!fichaForm.checkValidity()) {
      showToast('Completa los campos obligatorios.', 'error');
      return;
    }

    const params = new URLSearchParams({
      codigo_ficha: document.getElementById('f-codigo').value.trim(),
      Programas_idProgramas: document.getElementById('f-programa').value,
      Nivel_formacion_id_nivel_formacion: document.getElementById('f-nivel').value,
      Sede_id_sede: document.getElementById('f-sede').value,
      Modalidad_id_modalidad: document.getElementById('f-modalidad').value,
      Jornada_id_jornada: document.getElementById('f-jornada').value,
      Etapa_id_etapa: document.getElementById('f-etapa').value,
      fecha_inicio: document.getElementById('f-inicio').value,
      fecha_fin: document.getElementById('f-fin').value,
      cantidad_aprendices: document.getElementById('f-aprendices').value || '0',
      Estado_id_estado: document.getElementById('f-estado').value,
    });

    const isEdit = editingId !== null;
    if (isEdit) params.set('id', String(editingId));
    const url = isEdit ? 'ActualizarFicha' : 'RegistrarFicha';

    try {
      const resp = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
      });
      if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }

      await cargarFichas();
      formModal?.hide();
      fichaForm.classList.remove('was-validated');
      showToast(isEdit ? 'Ficha actualizada correctamente ?' : 'Ficha creada correctamente ?');
    } catch (err) {
      console.error(err);
      showToast('No se pudo guardar la ficha. Verifica los datos e intenta de nuevo.', 'error');
    }
  });

  btnFormDel?.addEventListener('click', () => {
    formModal?.hide();
    openConfirm(editingId);
  });

  formModalEl?.addEventListener('hidden.bs.modal', () => {
    fichaForm?.reset();
    fichaForm?.classList.remove('was-validated');
  });
}


/* ------------------------------------------
   7. MODAL - DETALLE (solo lectura)
------------------------------------------ */
const detailModalEl = document.getElementById('modal-detail');
const detailModal   = detailModalEl ? new bootstrap.Modal(detailModalEl) : null;
const detailContent = document.getElementById('detail-content');
const btnDetailEdit = document.getElementById('btn-detail-edit');

let viewingId = null;

function openDetail(id) {
  const f = fichas.find(x => x.id === id);
  if (!f) return;
  viewingId = id;

  const modalidad = modalidadNombre(f.modalidadId);
  const iconMap = { Presencial: 'home', Virtual: 'laptop_mac', Distancia: 'directions_walk' };
  const icon = iconMap[modalidad] ?? 'help';

  detailContent.innerHTML = `
    <div class="cc-detail-row">
      <span class="cc-detail-label">Código</span>
      <span class="cc-detail-value cc-mono">${f.codigo}</span>
    </div>
    <div class="cc-detail-row">
      <span class="cc-detail-label">Programa de Formación</span>
      <span class="cc-detail-value">${ClassControl.escapeHtml(programaNombre(f.programaId))}</span>
    </div>
    <div class="cc-detail-row">
      <span class="cc-detail-label">Nivel</span>
      <span class="cc-detail-value">${ClassControl.escapeHtml(nivelNombre(f.nivelFormacionId))}</span>
    </div>
    <div class="cc-detail-row">
      <span class="cc-detail-label">Sede</span>
      <span class="cc-detail-value">${ClassControl.escapeHtml(sedeNombre(f.sedeId))}</span>
    </div>
    <div class="row g-0">
      <div class="col-6 cc-detail-row pe-3">
        <span class="cc-detail-label">Fecha Inicio</span>
        <span class="cc-detail-value">${formatDate(f.fechaInicio)}</span>
      </div>
      <div class="col-6 cc-detail-row">
        <span class="cc-detail-label">Fecha Fin</span>
        <span class="cc-detail-value">${formatDate(f.fechaFin)}</span>
      </div>
    </div>
    <div class="cc-detail-row">
      <span class="cc-detail-label">Modalidad</span>
      <span class="cc-detail-value">
        <span class="material-symbols-outlined me-1">${icon}</span>${ClassControl.escapeHtml(modalidad)}
      </span>
    </div>
    <div class="cc-detail-row">
      <span class="cc-detail-label">N.º Aprendices</span>
      <span class="cc-detail-value">${f.cantidadAprendices ?? 0}</span>
    </div>
    <div class="cc-detail-row">
      <span class="cc-detail-label">Estado</span>
      <span class="cc-detail-value">${estadoBadge(f.estadoId)}</span>
    </div>
  `;

  detailModal?.show();
}

if (window.ccPuedeEscribirFichas) {
  btnDetailEdit?.addEventListener('click', () => {
    if (viewingId !== null) openEdit(viewingId);
  });
} else {
  btnDetailEdit?.classList.add('d-none');
}


/* ------------------------------------------
   8. MODAL - CONFIRMAR ELIMINACIÓN
------------------------------------------ */
const confirmModalEl = document.getElementById('modal-confirm');
const confirmModal   = confirmModalEl ? new bootstrap.Modal(confirmModalEl) : null;
const confirmName    = document.getElementById('confirm-ficha-name');
const btnConfirmDel  = document.getElementById('btn-confirm-delete');

let deletingId = null;

function openConfirm(id) {
  const f = fichas.find(x => x.id === id);
  if (!f) return;
  deletingId = id;
  if (confirmName) confirmName.textContent = `${f.codigo} - ${programaNombre(f.programaId)}`;
  confirmModal?.show();
}

if (window.ccPuedeEliminarFichas) {
  btnConfirmDel?.addEventListener('click', async () => {
    if (deletingId === null) return;
    try {
      const resp = await fetch('EliminarFicha', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ id: String(deletingId) })
      });
      if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }
      await cargarFichas();
      confirmModal?.hide();
      showToast('Ficha eliminada.', 'error');
    } catch (err) {
      console.error(err);
      showToast(err.message || 'No se pudo eliminar la ficha.', 'error');
    }
  });
}


/* ------------------------------------------
   9. TOASTS BOOTSTRAP 5
------------------------------------------ */
function showToast(message, type = 'success') {
  const container = document.getElementById('toast-container');
  if (!container) return;

  const id   = 'toast-' + Date.now();
  const icon = type === 'success' ? 'check_circle' : 'error';
  const iconColorClass = type === 'success' ? 'text-success' : 'text-danger';

  container.insertAdjacentHTML('beforeend', `
    <div id="${id}" class="toast align-items-center border-0 shadow-sm" role="alert">
      <div class="d-flex">
        <div class="toast-body d-flex align-items-center gap-2"
             style="font-family:var(--cc-font);font-size:.875rem">
          <span class="material-symbols-outlined ${iconColorClass}">${icon}</span>
          <span>${message}</span>
        </div>
        <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>
      </div>
    </div>`);

  const el    = document.getElementById(id);
  const toast = new bootstrap.Toast(el, { delay: 3500 });
  toast.show();
  el.addEventListener('hidden.bs.toast', () => el.remove());
}


/* ------------------------------------------
   10. DARK MODE
------------------------------------------ */
(function initDarkMode() {
  const btn  = document.getElementById('dark-toggle');
  const icon = document.getElementById('dark-icon');
  const html = document.documentElement;

  function applyTheme(dark) {
    html.classList.toggle('dark', dark);
    if (icon) { icon.className = 'material-symbols-outlined'; icon.textContent = dark ? 'light_mode' : 'dark_mode'; }
  }

  const saved      = localStorage.getItem('cc-theme');
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
  applyTheme(saved ? saved === 'dark' : prefersDark);

  btn?.addEventListener('click', () => {
    const isDark = html.classList.toggle('dark');
    localStorage.setItem('cc-theme', isDark ? 'dark' : 'light');
    applyTheme(isDark);
  });
})();


/* ------------------------------------------
   11. (Sidebar móvil gestionado de forma central
       en JS/ClassControl_theme.js)
------------------------------------------ */
