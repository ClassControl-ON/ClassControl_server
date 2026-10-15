/* ============================================================
   ActividadesJS.js - ClassControl
   CRUD completo + filtros reactivos + paginación propia

   Datos reales desde el backend:
   - GET  ConsultarActividades -> lista de actividades (JSON)
   - GET  ConsultarResultados  -> catálogo de resultados de aprendizaje
   - POST RegistrarActividad   -> crear actividad
   - POST ActualizarActividad  -> editar actividad
   - POST EliminarActividad    -> eliminar actividad

   Nota: la tabla "actividades" en la BD solo guarda código, nombre,
   descripción y el resultado de aprendizaje asociado. Instructor,
   ambiente, días y horario se quitaron del formulario y la tabla
   porque esos datos viven en Programacion_Instructores, no aquí.
   ============================================================ */

'use strict';

/* ------------------------------------------
   1. ESTADO EN MEMORIA (poblado desde el backend)
------------------------------------------ */
let actividades  = [];
let resultados   = [];
let paginaActual = 1;
const POR_PAGINA = 5;
let idAEliminar  = null;

/* ------------------------------------------
   2. SELECTORES DOM
------------------------------------------ */
const tbodyActividades = document.getElementById('tbody-actividades');
const contadorEl       = document.getElementById('contador-actividades');
const paginacionEl     = document.getElementById('paginacion');

const formFiltros      = document.getElementById('form-filtros');
const filtroBusqueda   = document.getElementById('filtro-busqueda');
const filtroFicha      = document.getElementById('filtro-ficha');

const formActividad    = document.getElementById('form-actividad');
const modalTituloEl    = document.getElementById('modal-titulo');
const actividadId      = document.getElementById('actividad-id');
const actCodigo        = document.getElementById('act-codigo');
const actNombre        = document.getElementById('act-nombre');
const actDescripcion   = document.getElementById('act-descripcion');
const actFicha         = document.getElementById('act-ficha');

const btnNuevaActividad = document.getElementById('btn-nueva-actividad');
const btnConfirmarElim  = document.getElementById('btn-confirmar-eliminar');

const toastEl   = document.getElementById('toast');
const toastMsg  = document.getElementById('toast-msg');
const toastIcon = document.getElementById('toast-icon');

/* Bootstrap modals (instanciados una vez) */
const bsModalActividad = new bootstrap.Modal(document.getElementById('modal-actividad'));
const bsModalEliminar  = new bootstrap.Modal(document.getElementById('modal-eliminar'));
const bsModalDetalle   = new bootstrap.Modal(document.getElementById('modal-detalle'));

/* ------------------------------------------
   3. UTILIDADES
------------------------------------------ */
/** Escapa HTML para prevenir XSS */
function escHtml(str) {
  return String(str ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function resultadoPorId(id) {
  return resultados.find(r => r.id === id);
}

function resultadoLabel(id) {
  const r = resultadoPorId(id);
  return r ? `${r.codigo} - ${r.descripcion}` : '-';
}

function llenarSelectResultados(select, placeholder) {
  if (!select) return;
  const actual = select.value;
  const initial = `<option value="">${placeholder}</option>`;
  const opciones = resultados.map(r => {
    const label = [r.codigo, r.descripcion]
      .filter(v => v != null && String(v).trim() !== '')
      .join(' - ');
    return `<option value="${r.id}">${escHtml(label)}</option>`;
  }).join('');
  select.innerHTML = initial + opciones;
  if (actual && [...select.options].some(o => o.value === String(actual))) select.value = actual;
}

/* ------------------------------------------
   4. CARGA DE DATOS DESDE EL BACKEND
------------------------------------------ */
async function cargarResultados() {
  const resp = await fetch('ConsultarResultados');
  resultados = resp.ok ? await resp.json() : [];
  llenarSelectResultados(filtroFicha, 'Todos');
  llenarSelectResultados(actFicha, 'Seleccionar…');
}

async function cargarActividades() {
  const resp = await fetch('ConsultarActividades');
  if (!resp.ok) throw new Error('No se pudo cargar la lista de actividades');
  actividades = await resp.json();
  paginaActual = 1;
  renderTabla();
}

async function inicializarDatos() {
  try {
    await cargarResultados();
    await cargarActividades();
  } catch (err) {
    console.error(err);
    mostrarToast('No se pudieron cargar los datos desde el servidor.', 'error', ClassControl.colors.danger);
  }
}

/* ------------------------------------------
   5. FILTRADO
------------------------------------------ */
function filtrarActividades() {
  const busq  = filtroBusqueda.value.trim().toLowerCase();
  const ficha = filtroFicha.value ? parseInt(filtroFicha.value, 10) : null;

  return actividades.filter(a =>
    (!busq  || a.nombre.toLowerCase().includes(busq)
            || String(a.codigoActividad).toLowerCase().includes(busq)
            || (a.descripcion ?? '').toLowerCase().includes(busq)) &&
    (!ficha || a.resultadoId === ficha)
  );
}

/* ------------------------------------------
   6. RENDER TABLA
------------------------------------------ */
function renderTabla() {
  const filtradas = filtrarActividades();
  const total     = filtradas.length;
  const totalPags = Math.max(1, Math.ceil(total / POR_PAGINA));

  if (paginaActual > totalPags) paginaActual = totalPags;

  const inicio = (paginaActual - 1) * POR_PAGINA;
  const pagina = filtradas.slice(inicio, inicio + POR_PAGINA);

  if (pagina.length === 0) {
    tbodyActividades.innerHTML = `
      <tr class="cc-empty-row">
        <td colspan="5">
          <span class="material-symbols-outlined cc-empty-icon">search_off</span>
          No se encontraron actividades con los filtros aplicados.
        </td>
      </tr>`;
  } else {
    tbodyActividades.innerHTML = pagina.map(crearFila).join('');
  }

  const desde = total === 0 ? 0 : inicio + 1;
  const hasta  = Math.min(inicio + POR_PAGINA, total);
  contadorEl.textContent =
    `Mostrando ${desde} - ${hasta} de ${total} actividad${total !== 1 ? 'es' : ''}`;

  renderPaginacion(totalPags);
}

function crearFila(a) {
  // Botones de escritura solo para quien puede gestionar (Admin/Coordinador).
  const btnEditar = window.ccPuedeGestionarCatalogo ? `
        <button class="cc-btn-icon cc-btn-icon-edit btn-editar" data-id="${a.id}" title="Editar">
          <span class="material-symbols-outlined">edit</span>
        </button>` : '';
  const btnEliminar = window.ccPuedeGestionarCatalogo ? `
        <button class="cc-btn-icon cc-btn-icon-del btn-eliminar" data-id="${a.id}" title="Eliminar">
          <span class="material-symbols-outlined">delete</span>
        </button>` : '';
  return `
  <tr data-id="${a.id}">
    <td><span class="cc-code">${escHtml(a.codigoActividad)}</span></td>
    <td class="fw-semibold">${escHtml(a.nombre)}</td>
    <td class="small text-muted text-truncate" style="max-width:280px">${escHtml(a.descripcion || '-')}</td>
    <td>
      <span class="d-inline-flex align-items-center gap-1 text-muted small">
        <span class="material-symbols-outlined" style="font-size:.9rem">tag</span>${escHtml(resultadoLabel(a.resultadoId))}
      </span>
    </td>
    <td class="text-end">
      <div class="action-btns d-flex justify-content-end gap-1">
        <button class="cc-btn-icon cc-btn-icon-view btn-ver" data-id="${a.id}" title="Ver detalle">
          <span class="material-symbols-outlined">visibility</span>
        </button>${btnEditar}${btnEliminar}
      </div>
    </td>
  </tr>`;
}

/* ------------------------------------------
   7. PAGINACIÓN
------------------------------------------ */
function renderPaginacion(totalPags) {
  paginacionEl.innerHTML = '';

  paginacionEl.appendChild(
    crearBtnNav('chevron_left', paginaActual === 1, () => { paginaActual--; renderTabla(); })
  );

  calcularRango(paginaActual, totalPags).forEach(item => {
    if (item === '...') {
      const ell = document.createElement('span');
      ell.className   = 'cc-page-ellipsis';
      ell.textContent = '…';
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
  btn.className = 'cc-page-btn';
  btn.disabled  = disabled;
  btn.innerHTML = `<span class="material-symbols-outlined" style="font-size:1rem">${icon}</span>`;
  btn.addEventListener('click', handler);
  return btn;
}

function calcularRango(actual, total) {
  if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1);
  const rango  = new Set([1, total, actual]);
  if (actual > 1)     rango.add(actual - 1);
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
  modalTituloEl.textContent = 'Nueva Actividad';
  formActividad.reset();
  actividadId.value = '';
  limpiarValidacion();
  bsModalActividad.show();
}

function abrirModalEditar(id) {
  const a = actividades.find(x => x.id === id);
  if (!a) return;
  modalTituloEl.textContent = 'Editar Actividad';
  actividadId.value    = a.id;
  actCodigo.value      = a.codigoActividad;
  actNombre.value      = a.nombre;
  actDescripcion.value = a.descripcion || '';
  actFicha.value       = a.resultadoId;
  limpiarValidacion();
  bsModalActividad.show();
}

/* Limpiar validación al cerrar */
document.getElementById('modal-actividad').addEventListener('hidden.bs.modal', () => {
  formActividad.reset();
  limpiarValidacion();
});

/* ------------------------------------------
   9. MODAL DETALLE (Bootstrap 5)
------------------------------------------ */
function verDetalle(id) {
  const a = actividades.find(x => x.id === id);
  if (!a) return;

  const filas = [
    ['Código', a.codigoActividad],
    ['Resultado de aprendizaje', resultadoLabel(a.resultadoId)],
    ['Descripción', a.descripcion || 'Sin descripción.'],
  ];

  document.getElementById('detalle-titulo').textContent = a.nombre;
  document.getElementById('detalle-body').innerHTML = filas.map(([label, val]) => `
    <div class="cc-detail-row">
      <span class="cc-detail-label">${escHtml(label)}</span>
      <span class="cc-detail-value">${escHtml(val)}</span>
    </div>`).join('');

  bsModalDetalle.show();
}

/* ------------------------------------------
   10. VALIDACIÓN (Bootstrap 5 nativa)
------------------------------------------ */
function validarFormulario() {
  const requeridos = [actCodigo, actNombre, actFicha];
  let valido = true;
  requeridos.forEach(campo => {
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
  [actCodigo, actNombre, actFicha].forEach(c => {
    c.classList.remove('is-invalid', 'is-valid');
  });
}

/* ------------------------------------------
   11. GUARDAR ACTIVIDAD
------------------------------------------ */
formActividad.addEventListener('submit', async e => {
  e.preventDefault();
  if (!validarFormulario()) return;

  const isEdit = !!actividadId.value;
  const params = new URLSearchParams({
    codigo_Actividad: actCodigo.value.trim(),
    nombre_Act: actNombre.value.trim(),
    descripcion: actDescripcion.value.trim(),
    Resultado_aprendizaje_id_resultado_aprendizaje: actFicha.value,
  });
  if (isEdit) params.set('id', actividadId.value);

  const url = isEdit ? 'ActualizarActividad' : 'RegistrarActividad';

  try {
    const resp = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params
    });
    if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }

    await cargarActividades();
    bsModalActividad.hide();
    mostrarToast(isEdit ? 'Actividad actualizada correctamente.' : 'Actividad creada correctamente.', 'check_circle', 'var(--cc-primary)');
  } catch (err) {
    console.error(err);
    mostrarToast('No se pudo guardar la actividad. Verifica los datos e intenta de nuevo.', 'error', ClassControl.colors.danger);
  }
});

/* ------------------------------------------
   12. ELIMINAR ACTIVIDAD
------------------------------------------ */
function abrirModalEliminar(id) {
  idAEliminar = id;
  bsModalEliminar.show();
}

/* Solo se enlaza el confirmar si el rol puede gestionar (Admin/Coordinador);
   el backend además lo bloquea con puedeEliminar para no-Admin. */
if (window.ccPuedeGestionarCatalogo) {
  btnConfirmarElim.addEventListener('click', async () => {
    try {
      const resp = await fetch('EliminarActividad', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ id: String(idAEliminar) })
      });
      if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }

      idAEliminar = null;
      bsModalEliminar.hide();
      await cargarActividades();
      mostrarToast('Actividad eliminada.', 'delete', ClassControl.colors.danger);
    } catch (err) {
      console.error(err);
      bsModalEliminar.hide();
      mostrarToast(err.message || 'No se pudo eliminar la actividad.', 'error', ClassControl.colors.danger);
    }
  });
}

/* ------------------------------------------
   13. DESCARGA CSV
------------------------------------------ */
function descargarCSV() {
  const BOM      = '\uFEFF';
  const cabecera = ['Código', 'Nombre', 'Descripción', 'Resultado de aprendizaje'];
  const filas    = actividades.map(a =>
    [a.codigoActividad, `"${String(a.nombre).replace(/"/g, '""')}"`,
     `"${String(a.descripcion ?? '').replace(/"/g, '""')}"`,
     `"${resultadoLabel(a.resultadoId).replace(/"/g, '""')}"`].join(',')
  );
  const csv  = BOM + [cabecera.join(','), ...filas].join('\r\n');
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  const url  = URL.createObjectURL(blob);
  const a    = Object.assign(document.createElement('a'), {
    href: url, download: 'actividades_classcontrol.csv'
  });
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
  mostrarToast('Reporte descargado.', 'download', ClassControl.colors.info);
}

/* Botón de descarga (si existe en el HTML) */
document.getElementById('btn-descargar')?.addEventListener('click', descargarCSV);

/* ------------------------------------------
   14. TOAST
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
   15. DELEGACIÓN EN TABLA
------------------------------------------ */
tbodyActividades.addEventListener('click', e => {
  const btnVer  = e.target.closest('.btn-ver');
  const btnEdit = e.target.closest('.btn-editar');
  const btnDel  = e.target.closest('.btn-eliminar');
  if (btnVer)  verDetalle(parseInt(btnVer.dataset.id, 10));
  if (btnEdit) abrirModalEditar(parseInt(btnEdit.dataset.id, 10));
  if (btnDel)  abrirModalEliminar(parseInt(btnDel.dataset.id, 10));
});

/* ------------------------------------------
   16. FILTROS REACTIVOS
------------------------------------------ */
[filtroBusqueda, filtroFicha].forEach(el => {
  el.addEventListener('input', () => { paginaActual = 1; renderTabla(); });
});

formFiltros.addEventListener('reset', () => {
  setTimeout(() => { paginaActual = 1; renderTabla(); }, 0);
});

formFiltros.addEventListener('submit', e => {
  e.preventDefault();
  paginaActual = 1;
  renderTabla();
});

/* ------------------------------------------
   17. ATAJOS DE TECLADO
   El botón "Nueva" y el atajo Alt+N solo
   aplican a quien puede gestionar el catálogo.
   ------------------------------------------ */
if (window.ccPuedeGestionarCatalogo) {
  btnNuevaActividad.addEventListener('click', abrirModalNuevo);
} else {
  btnNuevaActividad?.classList.add('d-none');
}

document.addEventListener('keydown', e => {
  if (e.altKey && e.key === 'n' && window.ccPuedeGestionarCatalogo) { e.preventDefault(); abrirModalNuevo(); }
});

/* ------------------------------------------
   18. INICIALIZACIÓN
------------------------------------------ */
inicializarDatos();
