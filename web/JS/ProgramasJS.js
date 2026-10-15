/* ============================================================
   ProgramasJS.js - ClassControl
   CRUD completo + filtros reactivos + paginación propia
   + métricas dinámicas + descarga CSV

   Datos reales desde el backend:
   - GET  ConsultarProgramas -> lista de programas (JSON)
   - POST RegistrarPrograma  -> crear programa
   - POST ActualizarPrograma -> editar programa
   - POST EliminarPrograma   -> eliminar programa

   Nota: el modelo de "programas" en la BD solo tiene código y
   nombre (idProgramas, codigo_programa, nombre_programa). Los
   campos nivel/versión/estado se quitaron del formulario y la
   tabla porque todavía no existen en el backend.
   ============================================================ */

'use strict';

/* ------------------------------------------
   1. ESTADO EN MEMORIA (poblado desde el backend)
------------------------------------------ */
let programas    = [];
let paginaActual = 1;
const POR_PAGINA = 5;
let idAEliminar  = null;

/* ------------------------------------------
   2. SELECTORES
------------------------------------------ */
const tbodyProgramas   = document.getElementById('tbody-programas');
const contadorEl       = document.getElementById('contador-programas');
const paginacionEl     = document.getElementById('paginacion');
const metricasEl       = document.getElementById('metricas');

const formFiltros      = document.getElementById('form-filtros');
const filtroBusqueda   = document.getElementById('filtro-busqueda');

const formPrograma     = document.getElementById('form-programa');
const modalTituloEl    = document.getElementById('modal-titulo');
const progId           = document.getElementById('prog-id');
const progCodigo       = document.getElementById('prog-codigo');
const progNombre       = document.getElementById('prog-nombre');

const btnNuevo         = document.getElementById('btn-nuevo-programa');
const btnDescargar     = document.getElementById('btn-descargar');
const btnConfirmarElim = document.getElementById('btn-confirmar-eliminar');

const toastEl          = document.getElementById('toast');
const toastMsg         = document.getElementById('toast-msg');
const toastIcon        = document.getElementById('toast-icon');

/* Bootstrap modals (instanciados una sola vez) */
const bsModalPrograma = new bootstrap.Modal(document.getElementById('modal-programa'));
const bsModalEliminar = new bootstrap.Modal(document.getElementById('modal-eliminar'));

/* Sidebar toggle (móvil) */
document.getElementById('btnSidebarToggle')?.addEventListener('click', () => {
  document.getElementById('sidebar').classList.toggle('open');
});

/* ------------------------------------------
   3. CARGA DE DATOS DESDE EL BACKEND
------------------------------------------ */
async function cargarProgramas() {
  const resp = await fetch('ConsultarProgramas');
  if (!resp.ok) throw new Error('No se pudo cargar la lista de programas');
  programas = await resp.json();
  paginaActual = 1;
  renderTabla();
}

async function inicializarDatos() {
  try {
    await cargarProgramas();
  } catch (err) {
    console.error(err);
    mostrarToast('No se pudieron cargar los datos desde el servidor.', 'error', ClassControl.colors.danger);
  }
}

/* ------------------------------------------
   4. MÉTRICAS
------------------------------------------ */
function renderMetricas() {
  const datos = [
    { label: 'Total Programas', valor: programas.length, borde: 'var(--cc-primary)' },
  ];

  metricasEl.innerHTML = datos.map(d => `
    <div class="col-12 col-md-4">
      <div class="cc-metric" style="border-left-color: ${d.borde}">
        <div class="cc-metric-label">${d.label}</div>
        <div class="cc-metric-value">${d.valor}</div>
      </div>
    </div>
  `).join('');
}

/* ------------------------------------------
   5. FILTRADO
------------------------------------------ */
function filtrarProgramas() {
  const busq = filtroBusqueda.value.trim().toLowerCase();

  return programas.filter(p =>
    !busq ||
    p.nombre.toLowerCase().includes(busq) ||
    String(p.codigo).toLowerCase().includes(busq)
  );
}

/* ------------------------------------------
   6. RENDER TABLA
------------------------------------------ */
function renderTabla() {
  const filtrados  = filtrarProgramas();
  const total      = filtrados.length;
  const totalPags  = Math.max(1, Math.ceil(total / POR_PAGINA));

  if (paginaActual > totalPags) paginaActual = totalPags;

  const inicio = (paginaActual - 1) * POR_PAGINA;
  const pagina = filtrados.slice(inicio, inicio + POR_PAGINA);

  if (pagina.length === 0) {
    tbodyProgramas.innerHTML = `
      <tr class="cc-empty-row">
        <td colspan="3">
          <span class="material-symbols-outlined cc-empty-icon">search_off</span>
          No se encontraron programas con los filtros aplicados.
        </td>
      </tr>`;
  } else {
    tbodyProgramas.innerHTML = pagina.map(crearFila).join('');
  }

  const desde = total === 0 ? 0 : inicio + 1;
  const hasta = Math.min(inicio + POR_PAGINA, total);
  contadorEl.textContent =
    `Mostrando ${desde} - ${hasta} de ${total} programa${total !== 1 ? 's' : ''}`;

  renderPaginacion(totalPags);
  renderMetricas();
}

function crearFila(p) {
  const puedeGestionar = !!window.ccPuedeGestionarProgramas;
  let acciones = `
        <button class="cc-btn-icon cc-btn-icon-view btn-ver" data-id="${p.id}" title="Ver">
          <span class="material-symbols-outlined">visibility</span>
        </button>`;
  if (puedeGestionar) {
    acciones += `
        <button class="cc-btn-icon cc-btn-icon-edit btn-editar" data-id="${p.id}" title="Editar">
          <span class="material-symbols-outlined">edit</span>
        </button>
        <button class="cc-btn-icon cc-btn-icon-del btn-eliminar" data-id="${p.id}" title="Eliminar">
          <span class="material-symbols-outlined">delete</span>
        </button>`;
  }
  return `
  <tr data-id="${p.id}">
    <td><span class="cc-code">${escHtml(p.codigo)}</span></td>
    <td class="fw-semibold">${escHtml(p.nombre)}</td>
    <td class="text-end">
      <div class="action-btns d-flex justify-content-end gap-1">
        ${acciones}
      </div>
    </td>
  </tr>`;
}

/* Escapa HTML para evitar XSS */
function escHtml(str) {
  return String(str ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

/* ------------------------------------------
   7. PAGINACIÓN
------------------------------------------ */
function renderPaginacion(totalPags) {
  paginacionEl.innerHTML = '';

  const btnPrev = crearBtnNav('chevron_left', paginaActual === 1, () => { paginaActual--; renderTabla(); });
  paginacionEl.appendChild(btnPrev);

  calcularRango(paginaActual, totalPags).forEach(item => {
    if (item === '...') {
      const ell = document.createElement('span');
      ell.className = 'cc-page-ellipsis';
      ell.textContent = '…';
      paginacionEl.appendChild(ell);
    } else {
      const btn = document.createElement('button');
      btn.className = `cc-page-btn${item === paginaActual ? ' active' : ''}`;
      btn.textContent = item;
      btn.addEventListener('click', () => { paginaActual = item; renderTabla(); });
      paginacionEl.appendChild(btn);
    }
  });

  const btnNext = crearBtnNav('chevron_right', paginaActual === totalPags, () => { paginaActual++; renderTabla(); });
  paginacionEl.appendChild(btnNext);
}

function crearBtnNav(icon, disabled, handler) {
  const btn = document.createElement('button');
  btn.className = 'cc-page-btn';
  btn.disabled  = disabled;
  btn.innerHTML = `<span class="material-symbols-outlined" style="font-size:1rem">${icon}</span>`;
  btn.addEventListener('click', handler);
  return btn;
}

function calcularRango(actual, total) {
  if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1);
  const rango = new Set([1, total, actual]);
  if (actual > 1) rango.add(actual - 1);
  if (actual < total) rango.add(actual + 1);
  const sorted = [...rango].sort((a, b) => a - b);
  const result = [];
  sorted.forEach((n, i) => {
    if (i > 0 && n - sorted[i - 1] > 1) result.push('...');
    result.push(n);
  });
  return result;
}

/* ------------------------------------------
   8. MODAL CREAR / EDITAR (Bootstrap 5)
------------------------------------------ */
function abrirModalNuevo() {
  modalTituloEl.textContent = 'Nuevo Programa';
  formPrograma.reset();
  progId.value = '';
  limpiarValidacion();
  bsModalPrograma.show();
}

function abrirModalEditar(id) {
  const p = programas.find(x => x.id === id);
  if (!p) return;
  modalTituloEl.textContent = 'Editar Programa';
  progId.value     = p.id;
  progCodigo.value = p.codigo;
  progNombre.value = p.nombre;
  limpiarValidacion();
  bsModalPrograma.show();
}

function abrirModalVer(id) {
  const p = programas.find(x => x.id === id);
  if (!p) return;
  document.getElementById('pv-codigo').textContent = p.codigo ?? '-';
  document.getElementById('pv-nombre').textContent = p.nombre ?? '-';
  new bootstrap.Modal(document.getElementById('modal-ver-programa')).show();
}

/* ------------------------------------------
   9. VALIDACIÓN (Bootstrap 5 nativa)
------------------------------------------ */
function validarFormulario() {
  const campos = [progCodigo, progNombre];
  let valido = true;

  campos.forEach(campo => {
    if (!campo.value.toString().trim()) {
      campo.classList.add('is-invalid');
      campo.classList.remove('is-valid');
      valido = false;
    } else {
      campo.classList.remove('is-invalid');
      campo.classList.add('is-valid');
    }
  });

  return valido;
}

function limpiarValidacion() {
  [progCodigo, progNombre].forEach(c => {
    c.classList.remove('is-invalid', 'is-valid');
  });
}

/* ------------------------------------------
   10. GUARDAR (crear / editar)
------------------------------------------ */
const puedeGestionarProgramas = !!window.ccPuedeGestionarProgramas;

formPrograma.addEventListener('submit', async e => {
  e.preventDefault();
  if (!puedeGestionarProgramas) { mostrarToast('No tienes permisos para esta acción.', 'error', ClassControl.colors.danger); return; }
  if (!validarFormulario()) return;

  const isEdit = !!progId.value;
  const params = new URLSearchParams({
    codigo_programa: progCodigo.value.trim(),
    nombre_programa: progNombre.value.trim(),
  });
  if (isEdit) params.set('id', progId.value);

  const url = isEdit ? 'ActualizarPrograma' : 'RegistrarPrograma';

  try {
    const resp = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params
    });
    if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }

    await cargarProgramas();
    bsModalPrograma.hide();
    mostrarToast(isEdit ? 'Programa actualizado correctamente.' : 'Programa creado correctamente.', 'check_circle', 'var(--cc-primary)');
  } catch (err) {
    console.error(err);
    mostrarToast('No se pudo guardar el programa. Verifica los datos e intenta de nuevo.', 'error', ClassControl.colors.danger);
  }
});

/* Limpiar validación al cerrar el modal */
document.getElementById('modal-programa').addEventListener('hidden.bs.modal', () => {
  formPrograma.reset();
  limpiarValidacion();
});

/* ------------------------------------------
   11. ELIMINAR
------------------------------------------ */
function abrirModalEliminar(id) {
  idAEliminar = id;
  bsModalEliminar.show();
}

btnConfirmarElim.addEventListener('click', async () => {
  if (!puedeGestionarProgramas) { mostrarToast('No tienes permisos para esta acción.', 'error', ClassControl.colors.danger); return; }
  try {
    const resp = await fetch('EliminarPrograma', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ id: String(idAEliminar) })
    });
    if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }

    idAEliminar = null;
    bsModalEliminar.hide();
    await cargarProgramas();
    mostrarToast('Programa eliminado.', 'delete', ClassControl.colors.danger);
  } catch (err) {
    console.error(err);
    bsModalEliminar.hide();
    mostrarToast(err.message || 'No se pudo eliminar el programa.', 'error', ClassControl.colors.danger);
  }
});

/* ------------------------------------------
   12. DESCARGA CSV
------------------------------------------ */
btnDescargar.addEventListener('click', () => {
  const BOM      = '\uFEFF'; // UTF-8 BOM para compatibilidad Excel
  const cabecera = ['Código', 'Nombre'];
  const filas    = programas.map(p =>
    [p.codigo, `"${String(p.nombre).replace(/"/g, '""')}"`].join(',')
  );
  const csv  = BOM + [cabecera.join(','), ...filas].join('\r\n');
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  const url  = URL.createObjectURL(blob);
  const a    = Object.assign(document.createElement('a'), { href: url, download: 'programas_classcontrol.csv' });
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
  mostrarToast('Reporte descargado.', 'download', ClassControl.colors.info);
});

/* ------------------------------------------
   13. TOAST
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
   14. DELEGACIÓN EN TABLA
------------------------------------------ */
tbodyProgramas.addEventListener('click', e => {
  const puedeGestionar = !!window.ccPuedeGestionarProgramas;
  const btnVer  = e.target.closest('.btn-ver');
  const btnEdit = e.target.closest('.btn-editar');
  const btnDel  = e.target.closest('.btn-eliminar');
  if (btnVer)  abrirModalVer(parseInt(btnVer.dataset.id, 10));
  if (btnEdit && puedeGestionar) abrirModalEditar(parseInt(btnEdit.dataset.id, 10));
  if (btnDel  && puedeGestionar) abrirModalEliminar(parseInt(btnDel.dataset.id, 10));
});

/* ------------------------------------------
   15. FILTROS REACTIVOS
------------------------------------------ */
filtroBusqueda.addEventListener('input', () => { paginaActual = 1; renderTabla(); });

formFiltros.addEventListener('reset', () => {
  setTimeout(() => { paginaActual = 1; renderTabla(); }, 0);
});

formFiltros.addEventListener('submit', e => {
  e.preventDefault();
  paginaActual = 1;
  renderTabla();
});

/* ------------------------------------------
   16. ATAJO TECLADO (Nuevo con Alt+N)
------------------------------------------ */
if (puedeGestionarProgramas && btnNuevo) {
  btnNuevo.addEventListener('click', abrirModalNuevo);
}

document.addEventListener('keydown', e => {
  if (!puedeGestionarProgramas) return;
  if (e.altKey && e.key === 'n') { e.preventDefault(); abrirModalNuevo(); }
});

/* ------------------------------------------
   17. INICIALIZACIÓN
------------------------------------------ */
inicializarDatos();
