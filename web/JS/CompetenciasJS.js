/* ============================================================
   CompetenciasJS.js - ClassControl
   CRUD + filtros reactivos + paginación + métricas + CSV
   Datos reales desde el backend:
   - GET  ConsultarCompetencias    -> lista de competencias (JSON)
   - GET  ConsultarProgramas       -> catálogo de programas de formación
   - POST RegistrarCompetencia     -> crear competencia
   - POST ActualizarCompetencia    -> editar competencia
   - POST EliminarCompetencia      -> eliminar competencia
   ============================================================ */

'use strict';

/* ------------------------------------------
   1. ESTADO EN MEMORIA (poblado desde el backend)
------------------------------------------ */
let competencias   = [];
let programas = [];

let paginaActual = 1;
const POR_PAGINA = 5;
let idAEliminar  = null;

/* ------------------------------------------
   2. SELECTORES
------------------------------------------ */
const tbodyComp      = document.getElementById('tbody-competencias');
const contadorEl     = document.getElementById('contador-competencias');
const paginacionEl   = document.getElementById('paginacion');
const metricasEl     = document.getElementById('metricas');
const resumenCargaEl = document.getElementById('resumen-carga');

const formFiltros       = document.getElementById('form-filtros');
const filtroBusqueda    = document.getElementById('filtro-busqueda');
const filtroPrograma    = document.getElementById('filtro-programacion');

const formComp         = document.getElementById('form-competencia');
const modalTituloEl    = document.getElementById('modal-titulo');
const compId           = document.getElementById('comp-id');
const compCodigo       = document.getElementById('comp-codigo');
const compDescripcion  = document.getElementById('comp-descripcion');
const compPrograma     = document.getElementById('comp-programacion');

const btnNueva          = document.getElementById('btn-nueva-competencia');
const btnDescargar      = document.getElementById('btn-descargar');
const btnGenerarReporte = document.getElementById('btn-generar-reporte');
const btnConfirmarElim  = document.getElementById('btn-confirmar-eliminar');

const toastEl   = document.getElementById('toast');
const toastMsg  = document.getElementById('toast-msg');
const toastIcon = document.getElementById('toast-icon');

/* Bootstrap modals */
const bsModalComp    = new bootstrap.Modal(document.getElementById('modal-competencia'));
const bsModalDetalle = new bootstrap.Modal(document.getElementById('modal-detalle'));
const bsModalElim    = new bootstrap.Modal(document.getElementById('modal-eliminar'));

/* ------------------------------------------
   3. UTILIDADES
------------------------------------------ */
function escHtml(str) {
  return ClassControl.escapeHtml(str);
}

function programaLabel(p) {
  if (!p) return null;
  return `${p.codigo ?? ''} - ${p.nombre ?? 'Sin nombre'}`;
}

function programaPorId(id) {
  return programas.find(p => p.id === id) ?? null;
}

function llenarSelectProgramas(select, placeholder) {
  if (!select) return;
  const actual = select.value;
  select.innerHTML = `<option value="">${placeholder}</option>` +
    programas.map(p => `<option value="${p.id}">${escHtml(programaLabel(p))}</option>`).join('');
  if (actual) select.value = actual;
}

/* ------------------------------------------
   4. BADGE PROGRAMACIÓN
------------------------------------------ */
function badgePrograma(id) {
  const p = programaPorId(id);
  if (!p) return `<span class="cc-badge badge-transversal">Sin asignar</span>`;
  return `<span class="cc-badge badge-gestion">${escHtml(programaLabel(p))}</span>`;
}

/* ------------------------------------------
   5. CARGA DE DATOS DESDE EL BACKEND
------------------------------------------ */
async function cargarProgramas() {
  const resp = await fetch('ConsultarProgramas');
  programas = resp.ok ? await resp.json() : [];
  llenarSelectProgramas(filtroPrograma, 'Todos');
  llenarSelectProgramas(compPrograma, 'Seleccionar…');
}

async function cargarCompetencias() {
  const resp = await fetch('ConsultarCompetencias');
  if (!resp.ok) throw new Error('No se pudo cargar la lista de competencias');
  competencias = await resp.json();
  paginaActual = 1;
  renderTabla();
}

async function inicializarDatos() {
  try {
    await cargarProgramas();
    await cargarCompetencias();
  } catch (err) {
    console.error(err);
    mostrarToast('No se pudieron cargar los datos desde el servidor.', 'error', ClassControl.colors.danger);
  }
}

/* ------------------------------------------
   6. MÉTRICAS
------------------------------------------ */
function renderMetricas() {
  const total       = competencias.length;
  const asignadas   = competencias.filter(c => programaPorId(c.programaId)).length;
  const sinAsignar  = total - asignadas;

  const datos = [
    { label: 'Total',           valor: total,      borde: 'var(--cc-primary)' },
    { label: 'Con programa',     valor: asignadas,  borde: 'var(--cc-primary)' },
    { label: 'Sin asignar',     valor: sinAsignar, borde: 'var(--cc-warning)' },
  ];

  metricasEl.innerHTML = datos.map(d => `
    <div class="col-6 col-md-4">
      <div class="cc-metric" style="border-left-color:${d.borde}">
        <div class="cc-metric-label">${d.label}</div>
        <div class="cc-metric-value">${d.valor}</div>
      </div>
    </div>`).join('');

  if (resumenCargaEl) {
    resumenCargaEl.textContent =
      `Hay ${sinAsignar} competencia${sinAsignar !== 1 ? 's' : ''} sin programa asignado.`;
  }
}

/* ------------------------------------------
   7. FILTRADO
------------------------------------------ */
function filtrar() {
  const busq = filtroBusqueda.value.trim().toLowerCase();
  const prog = filtroPrograma.value;

  return competencias.filter(c =>
    (!busq || String(c.codigo).toLowerCase().includes(busq) || (c.descripcion ?? '').toLowerCase().includes(busq)) &&
    (!prog || String(c.programaId) === prog)
  );
}

/* ------------------------------------------
   8. RENDER TABLA
------------------------------------------ */
function renderTabla() {
  const filtradas = filtrar();
  const total     = filtradas.length;
  const totalPags = Math.max(1, Math.ceil(total / POR_PAGINA));

  if (paginaActual > totalPags) paginaActual = totalPags;

  const inicio = (paginaActual - 1) * POR_PAGINA;
  const pagina = filtradas.slice(inicio, inicio + POR_PAGINA);

  if (pagina.length === 0) {
    tbodyComp.innerHTML = `
      <tr class="cc-empty-row">
        <td colspan="4">
          <span class="material-symbols-outlined cc-empty-icon">search_off</span>
          No se encontraron competencias con los filtros aplicados.
        </td>
      </tr>`;
  } else {
    tbodyComp.innerHTML = pagina.map(crearFila).join('');
  }

  const desde = total === 0 ? 0 : inicio + 1;
  const hasta  = Math.min(inicio + POR_PAGINA, total);
  contadorEl.textContent =
    `Mostrando ${desde} - ${hasta} de ${total} competencia${total !== 1 ? 's' : ''}`;

  renderPaginacion(totalPags);
  renderMetricas();
}

function crearFila(c) {
  // Botones de escritura solo para quien puede gestionar (Admin/Coordinador).
  const btnEditar = window.ccPuedeGestionarCatalogo ? `
        <button class="cc-btn-icon cc-btn-icon-edit btn-editar" data-id="${c.id}" title="Editar">
          <span class="material-symbols-outlined">edit</span>
        </button>` : '';
  const btnEliminar = window.ccPuedeGestionarCatalogo ? `
        <button class="cc-btn-icon cc-btn-icon-del btn-eliminar" data-id="${c.id}" title="Eliminar">
          <span class="material-symbols-outlined">delete</span>
        </button>` : '';
  return `
  <tr data-id="${c.id}">
    <td><span class="cc-code">${escHtml(c.codigo)}</span></td>
    <td><div class="cc-desc-truncada">${escHtml(c.descripcion)}</div></td>
    <td>${badgePrograma(c.programaId)}</td>
    <td class="text-end">
      <div class="action-btns d-flex justify-content-end gap-1">
        <button class="cc-btn-icon cc-btn-icon-view btn-ver" data-id="${c.id}" title="Ver detalle">
          <span class="material-symbols-outlined">visibility</span>
        </button>${btnEditar}${btnEliminar}
      </div>
    </td>
  </tr>`;
}

/* ------------------------------------------
   9. PAGINACIÓN
------------------------------------------ */
function renderPaginacion(totalPags) {
  paginacionEl.innerHTML = '';

  paginacionEl.appendChild(
    crearBtnNav('chevron_left', paginaActual === 1, () => { paginaActual--; renderTabla(); })
  );

  calcularRango(paginaActual, totalPags).forEach(item => {
    if (item === '...') {
      const ell = document.createElement('span');
      ell.className = 'cc-page-ellipsis'; ell.textContent = '…';
      paginacionEl.appendChild(ell);
    } else {
      const btn = document.createElement('button');
      btn.className   = `cc-page-btn${item === paginaActual ? ' active' : ''}`;
      btn.textContent = item;
      btn.addEventListener('click', () => { paginaActual = item; renderTabla(); });
      paginacionEl.appendChild(btn);
    }
  });

  paginacionEl.appendChild(
    crearBtnNav('chevron_right', paginaActual === totalPags, () => { paginaActual++; renderTabla(); })
  );
}

function crearBtnNav(icon, disabled, handler) {
  const btn = document.createElement('button');
  btn.className = 'cc-page-btn'; btn.disabled = disabled;
  btn.innerHTML = `<span class="material-symbols-outlined" style="font-size:1rem">${icon}</span>`;
  btn.addEventListener('click', handler);
  return btn;
}

function calcularRango(actual, total) {
  if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1);
  const s = new Set([1, total, actual]);
  if (actual > 1)     s.add(actual - 1);
  if (actual < total) s.add(actual + 1);
  const sorted = [...s].sort((a, b) => a - b);
  const result = [];
  sorted.forEach((n, i) => {
    if (i > 0 && n - sorted[i-1] > 1) result.push('...');
    result.push(n);
  });
  return result;
}

/* ------------------------------------------
   10. MODAL CREAR / EDITAR
------------------------------------------ */
function abrirModalNuevo() {
  modalTituloEl.textContent = 'Nueva Competencia';
  formComp.reset();
  formComp.action = 'RegistrarCompetencia';
  compId.value = '';
  limpiarValidacion();
  bsModalComp.show();
}

function abrirModalEditar(id) {
  const c = competencias.find(x => x.id === id);
  if (!c) return;
  modalTituloEl.textContent = 'Editar Competencia';
  formComp.action        = 'ActualizarCompetencia';
  compId.value            = c.id;
  compCodigo.value        = c.codigo;
  compDescripcion.value   = c.descripcion;
  compPrograma.value      = c.programaId;
  limpiarValidacion();
  bsModalComp.show();
}

document.getElementById('modal-competencia').addEventListener('hidden.bs.modal', () => {
  formComp.reset(); limpiarValidacion();
});

/* ------------------------------------------
   11. VALIDACIÓN
------------------------------------------ */
function validar() {
  let ok = true;
  [compCodigo, compDescripcion, compPrograma].forEach(campo => {
    if (!campo.value.trim()) {
      campo.classList.add('is-invalid'); campo.classList.remove('is-valid'); ok = false;
    } else {
      campo.classList.remove('is-invalid'); campo.classList.add('is-valid');
    }
  });
  return ok;
}

function limpiarValidacion() {
  [compCodigo, compDescripcion, compPrograma].forEach(c => {
    c.classList.remove('is-invalid', 'is-valid');
  });
}

/* ------------------------------------------
   12. GUARDAR (crear / editar) vía fetch
------------------------------------------ */
formComp.addEventListener('submit', async e => {
  e.preventDefault();
  if (!validar()) return;

  const id  = compId.value ? parseInt(compId.value, 10) : null;
  const params = new URLSearchParams({
    codigo_Competencias: compCodigo.value.trim(),
    descripcion_Competencias: compDescripcion.value.trim(),
    Programas_idProgramas: compPrograma.value,
  });
  if (id) params.set('id', String(id));

  const url = id ? 'ActualizarCompetencia' : 'RegistrarCompetencia';

  try {
    const resp = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params,
    });
    if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }

    await cargarCompetencias();
    bsModalComp.hide();
    mostrarToast(id ? 'Competencia actualizada correctamente.' : 'Competencia creada correctamente.', 'check_circle', 'var(--cc-primary)');
  } catch (err) {
    console.error(err);
    mostrarToast('No se pudo guardar la competencia. Verifica los datos e intenta de nuevo.', 'error', ClassControl.colors.danger);
  }
});

/* ------------------------------------------
   13. VER DETALLE (modal Bootstrap)
------------------------------------------ */
function verDetalle(id) {
  const c = competencias.find(x => x.id === id);
  if (!c) return;

  const filas = [
    ['Código',       `<span class="cc-code">${escHtml(c.codigo)}</span>`],
    ['Programa', badgePrograma(c.programaId)],
    ['Descripción',  escHtml(c.descripcion)],
  ];

  document.getElementById('detalle-titulo').textContent =
    `Competencia ${c.codigo}`;
  document.getElementById('detalle-contenido').innerHTML =
    filas.map(([label, val]) => `
      <div class="cc-detail-row">
        <span class="cc-detail-label">${label}</span>
        <span class="cc-detail-value">${val}</span>
      </div>`).join('');

  bsModalDetalle.show();
}

/* ------------------------------------------
   14. ELIMINAR vía fetch
------------------------------------------ */
function abrirModalEliminar(id) {
  idAEliminar = id;
  bsModalElim.show();
}

/* Solo se enlaza el confirmar si el rol puede gestionar (Admin/Coordinador);
   el backend además lo bloquea con puedeEliminar para no-Admin. */
if (window.ccPuedeGestionarCatalogo) {
  btnConfirmarElim.addEventListener('click', async () => {
    if (idAEliminar === null) return;
    try {
      const resp = await fetch('EliminarCompetencia', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ id: String(idAEliminar) }),
      });
      if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }

      idAEliminar = null;
      bsModalElim.hide();
      await cargarCompetencias();
      mostrarToast('Competencia eliminada.', 'delete', ClassControl.colors.danger);
    } catch (err) {
      console.error(err);
      mostrarToast(err.message || 'No se pudo eliminar la competencia.', 'error', ClassControl.colors.danger);
    }
  });
}

/* ------------------------------------------
   15. EXPORTAR CSV
------------------------------------------ */
function exportarCSV() {
  const BOM      = '\uFEFF';
  const cabecera = ['Código', 'Descripción', 'Programa'];
  const filas    = competencias.map(c =>
    [c.codigo, `"${(c.descripcion ?? '').replace(/"/g,'""')}"`, `"${(programaLabel(programaPorId(c.programaId)) ?? 'Sin asignar').replace(/"/g,'""')}"`].join(',')
  );
  const csv  = BOM + [cabecera.join(','), ...filas].join('\r\n');
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  const url  = URL.createObjectURL(blob);
  const a    = Object.assign(document.createElement('a'), {
    href: url, download: 'competencias_classcontrol.csv'
  });
  document.body.appendChild(a); a.click();
  document.body.removeChild(a); URL.revokeObjectURL(url);
  mostrarToast('Reporte exportado correctamente.', 'download', ClassControl.colors.info);
}

/* ------------------------------------------
   16. TOAST (CSS puro)
------------------------------------------ */
let toastTimer = null;

function mostrarToast(msg, icon = 'check_circle', color = 'var(--cc-primary)') {
  toastMsg.textContent  = msg;
  toastIcon.textContent = icon;
  toastIcon.style.color = color;
  toastEl.classList.add('show');
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toastEl.classList.remove('show'), 3200);
}

/* ------------------------------------------
   17. DELEGACIÓN EN TABLA
------------------------------------------ */
tbodyComp.addEventListener('click', e => {
  const btnVer  = e.target.closest('.btn-ver');
  const btnEdit = e.target.closest('.btn-editar');
  const btnDel  = e.target.closest('.btn-eliminar');
  if (btnVer)  verDetalle(parseInt(btnVer.dataset.id, 10));
  if (btnEdit) abrirModalEditar(parseInt(btnEdit.dataset.id, 10));
  if (btnDel)  abrirModalEliminar(parseInt(btnDel.dataset.id, 10));
});

/* ------------------------------------------
   18. FILTROS REACTIVOS
------------------------------------------ */
[filtroBusqueda, filtroPrograma].forEach(el => {
  el.addEventListener('input', () => { paginaActual = 1; renderTabla(); });
});

formFiltros.addEventListener('reset', () => {
  setTimeout(() => { paginaActual = 1; renderTabla(); }, 0);
});

formFiltros.addEventListener('submit', e => {
  e.preventDefault(); paginaActual = 1; renderTabla();
});

/* ------------------------------------------
   19. EVENTOS BOTONES + ATAJOS
   El botón "Nueva" y el atajo Alt+N solo
   aplican a quien puede gestionar el catálogo.
   ------------------------------------------ */
if (window.ccPuedeGestionarCatalogo) {
  btnNueva.addEventListener('click', abrirModalNuevo);
} else {
  btnNueva?.classList.add('d-none');
}
btnDescargar.addEventListener('click', exportarCSV);
btnGenerarReporte.addEventListener('click', exportarCSV);

document.addEventListener('keydown', e => {
  if (e.altKey && e.key === 'n' && window.ccPuedeGestionarCatalogo) { e.preventDefault(); abrirModalNuevo(); }
});

/* ------------------------------------------
   20. INICIALIZACIÓN
------------------------------------------ */
inicializarDatos();
