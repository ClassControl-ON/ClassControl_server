/* ============================================================
   ClassControl - Gestión de Instructores
   InstructoresJS.js  (Bootstrap 5 + DataTables)

   Datos reales desde el backend:
   - GET  ConsultarUsuarios         -> lista de personal (JSON)
   - GET  ConsultarRoles            -> catálogo de roles
   - GET  ConsultarTiposDocumento   -> catálogo de tipos de documento
   - GET  ConsultarTiposVinculacion -> catálogo de tipos de vinculación
   - POST RegistrarUsuarioAdmin     -> crear instructor/personal
   - POST ActualizarUsuario         -> editar
   - POST EliminarUsuario           -> eliminar

   Esta pantalla gestiona el mismo recurso "Usuarios" que
   Gestión de Usuarios, pero enfocado en personal académico
   (se excluye el rol "Aprendiz" del listado y de los selectores).
   ============================================================ */

'use strict';

/* --------------------------------------------------
   1. ESTADO EN MEMORIA (poblado desde el backend)
-------------------------------------------------- */
let usuarios = [];
let roles = [];
let rolesPersonal = [];   // roles sin "Aprendiz", usados en filtros/formulario
let tiposDocumento = [];
let tiposVinculacion = [];
let idAEliminar = null;

function esRolAprendiz(descripcion) {
  return (descripcion || '').trim().toLowerCase() === 'aprendiz';
}

function instructores() {
  // "Personal" visible en esta pantalla: todo usuario cuyo rol no sea Aprendiz.
  const idsExcluidos = new Set(roles.filter(r => esRolAprendiz(r.descripcion)).map(r => r.id));
  return usuarios.filter(u => !idsExcluidos.has(u.rolId));
}

/* --------------------------------------------------
   2. COLORES DE AVATAR (por Área/profesión)
-------------------------------------------------- */
const PALETA_AVATAR = [
  { bg: '#dbeafe', text: '#1e40af' },
  { bg: '#dcfce7', text: '#166534' },
  { bg: '#ffedd5', text: '#9a3412' },
  { bg: '#f0fdf4', text: '#15803d' },
  { bg: '#fef9c3', text: '#854d0e' },
  { bg: '#fce7f3', text: '#9d174d' },
];

function getColorAvatar(area) {
  if (!area) return { bg: '#e2e8f0', text: '#475569' };
  let hash = 0;
  for (let i = 0; i < area.length; i++) hash = (hash * 31 + area.charCodeAt(i)) >>> 0;
  return PALETA_AVATAR[hash % PALETA_AVATAR.length];
}

function getIniciales(nombres, apellidos) {
  const n = nombres?.trim().split(' ')[0]?.[0]?.toUpperCase() || '';
  const a = apellidos?.trim().split(' ')[0]?.[0]?.toUpperCase() || '';
  return n + a;
}

function rolNombre(rolId) {
  const r = roles.find(r => r.id === rolId);
  return r ? r.descripcion : `Rol ${rolId}`;
}

/* --------------------------------------------------
   3. BOOTSTRAP MODAL INSTANCES
-------------------------------------------------- */
let bsModalInst, bsModalDetalle, bsModalEliminar;
let dtInstance = null;

$(document).ready(function () {
  bsModalInst     = new bootstrap.Modal(document.getElementById('modal-instructor'));
  bsModalDetalle  = new bootstrap.Modal(document.getElementById('modal-detalle'));
  bsModalEliminar = new bootstrap.Modal(document.getElementById('modal-eliminar'));

  initDataTable();
  initFiltros();
  initFormEvents();
  initDeleteEvents();
  initDarkMode();

  document.getElementById('btn-descargar')?.addEventListener('click', exportarCSV);
  if (window.ccSoloLectura) {
    document.getElementById('btn-nuevo-instructor')?.classList.add('d-none');
  } else {
    document.getElementById('btn-nuevo-instructor')?.addEventListener('click', () => abrirModalNuevo());
  }

  document.getElementById('modal-instructor')?.addEventListener('hidden.bs.modal', () => {
    document.getElementById('inst-id').value = '';
    document.getElementById('form-instructor')?.reset();
    document.querySelectorAll('#form-instructor .is-invalid').forEach(el => el.classList.remove('is-invalid'));
    document.getElementById('modal-titulo').textContent = 'Nuevo Instructor';
  });

  inicializarDatos();
});

async function inicializarDatos() {
  try {
    await cargarCatalogos();
    await cargarUsuarios();
  } catch (err) {
    console.error(err);
    showToast('No se pudieron cargar los datos desde el servidor.', 'error');
  }
}

async function cargarCatalogos() {
  const [rolesR, tipoDocR, tipoVincR] = await Promise.all([
    fetch('ConsultarRoles'),
    fetch('ConsultarTiposDocumento'),
    fetch('ConsultarTiposVinculacion'),
  ]);

  roles = rolesR.ok ? await rolesR.json() : [];
  tiposDocumento = tipoDocR.ok ? await tipoDocR.json() : [];
  tiposVinculacion = tipoVincR.ok ? await tipoVincR.json() : [];

  rolesPersonal = roles.filter(r => !esRolAprendiz(r.descripcion));

  const selRolFiltro = document.getElementById('filtro-rol');
  if (selRolFiltro) {
    selRolFiltro.innerHTML = '<option value="">Todos los Roles</option>' +
      rolesPersonal.map(r => `<option value="${r.id}">${ClassControl.escapeHtml(r.descripcion)}</option>`).join('');
  }

  const selRolForm = document.getElementById('inst-rol');
  if (selRolForm) {
    selRolForm.innerHTML = '<option value="">Seleccionar…</option>' +
      rolesPersonal.map(r => `<option value="${r.id}">${ClassControl.escapeHtml(r.descripcion)}</option>`).join('');
  }

  const selTipoDoc = document.getElementById('inst-tipoDoc');
  if (selTipoDoc) {
    selTipoDoc.innerHTML = '<option value="">Seleccionar…</option>' +
      tiposDocumento.map(t => `<option value="${t.id}">${ClassControl.escapeHtml(t.descripcion)}</option>`).join('');
  }

  const hiddenVinculacion = document.getElementById('inst-tipoVinculacion');
  if (hiddenVinculacion && tiposVinculacion.length) {
    hiddenVinculacion.value = tiposVinculacion[0].id;
  }
}

async function cargarUsuarios() {
  const resp = await fetch('ConsultarUsuarios');
  if (!resp.ok) throw new Error('No se pudo cargar la lista de personal');
  usuarios = await resp.json();
  renderTableData();
  renderMetricas();
}

/* --------------------------------------------------
   4. DATATABLES INIT
-------------------------------------------------- */
function initDataTable() {
  dtInstance = $('#instructores-table').DataTable({
    dom: '<"d-none"f>rt<"d-flex align-items-center justify-content-between px-3 py-2 border-top"ip>',
    language: {
      info:         'Mostrando _START_ - _END_ de _TOTAL_ instructor(es)',
      infoEmpty:    'Sin instructores disponibles',
      infoFiltered: '(filtrados de _MAX_ totales)',
      emptyTable:   'No se encontraron instructores con los filtros aplicados.',
      paginate:     { previous: '«', next: '»' },
    },
    pageLength: 10,
    ordering:   true,
    responsive: true,
    autoWidth:  false,
    columnDefs: [
      { orderable: false, targets: 6 }, // Acciones
    ],
  });

  $('#instructores-table tbody').on('click', '.btn-ver',      function() { verDetalle(parseInt(this.dataset.id, 10)); });
  $('#instructores-table tbody').on('click', '.btn-editar',   function() { abrirModalEditar(parseInt(this.dataset.id, 10)); });
  $('#instructores-table tbody').on('click', '.btn-eliminar', function() { abrirModalEliminar(parseInt(this.dataset.id, 10)); });
}

/* --------------------------------------------------
   5. RENDER
-------------------------------------------------- */
function renderTableData(lista = instructores()) {
  if (!dtInstance) return;

  dtInstance.clear();

  lista.forEach(u => {
    const area = u.profesion || '-';
    const col  = getColorAvatar(u.profesion);
    const inic = getIniciales(u.nombres, u.apellidos);
    const inac = !u.activo;

    dtInstance.row.add([
      `<div class="d-flex align-items-center gap-2${inac ? ' opacity-50' : ''}">
        <div class="cc-avatar-ini" style="background:${col.bg};color:${col.text}">${inic}</div>
        <div>
          <p class="cc-inst-name mb-0">${ClassControl.escapeHtml(u.nombres)}</p>
          <p class="cc-inst-apellido mb-0">${ClassControl.escapeHtml(u.apellidos)}</p>
        </div>
       </div>`,
      `<span class="cc-inst-cedula">${ClassControl.escapeHtml(u.identificacion)}</span>`,
      `<span class="cc-inst-correo">${ClassControl.escapeHtml(u.correo)}</span>`,
      `<span class="cc-area-badge">${ClassControl.escapeHtml(area)}</span>`,
      badgeRol(rolNombre(u.rolId)),
      badgeEstado(u.activo),
      `<div class="cc-action-group d-flex justify-content-end gap-1">
        <button class="cc-inst-btn cc-inst-btn--ver btn-ver"      data-id="${u.id}" title="Ver perfil">
          <span class="material-symbols-outlined" style="font-size:1.2rem">visibility</span>
        </button>${window.ccSoloLectura ? '' : `
        <button class="cc-inst-btn cc-inst-btn--editar btn-editar"  data-id="${u.id}" title="Editar">
          <span class="material-symbols-outlined" style="font-size:1.2rem">edit_note</span>
        </button>
        <button class="cc-inst-btn cc-inst-btn--borrar btn-eliminar" data-id="${u.id}" title="Eliminar">
          <span class="material-symbols-outlined" style="font-size:1.2rem">person_remove</span>
        </button>`}
       </div>`,
    ]);
  });

  dtInstance.draw();
}

/* --------------------------------------------------
   6. MÉTRICAS
-------------------------------------------------- */
function renderMetricas() {
  const lista   = instructores();
  const total   = lista.length;
  const activos = lista.filter(u => u.activo).length;
  const insts   = lista.filter(u => rolNombre(u.rolId).toLowerCase() === 'instructor').length;
  const admins  = total - insts;

  const datos = [
    { label: 'Total Personal', valor: total,   borde: '#38a800', col: 'col-6 col-xl-3' },
    { label: 'Activos',        valor: activos, borde: '#38a800', col: 'col-6 col-xl-3' },
    { label: 'Instructores',   valor: insts,   borde: '#1e40af', col: 'col-6 col-xl-3' },
    { label: 'Admin / Coord.', valor: admins,  borde: '#7c3aed', col: 'col-6 col-xl-3' },
  ];

  const metricas = document.getElementById('metricas');
  if (!metricas) return;

  metricas.innerHTML = datos.map(d => `
    <div class="${d.col}">
      <div class="cc-metrica-card" style="border-left-color:${d.borde}">
        <p class="cc-metrica-label">${d.label}</p>
        <p class="cc-metrica-valor">${d.valor}</p>
      </div>
    </div>`).join('');
}

/* --------------------------------------------------
   7. HTML HELPERS
-------------------------------------------------- */
function badgeRol(rol) {
  const cls = {
    'Instructor':    'cc-rol-badge--instructor',
    'Administrador': 'cc-rol-badge--administrador',
    'Coordinador':   'cc-rol-badge--coordinador',
  }[rol] ?? 'cc-rol-badge--instructor';
  return `<span class="cc-rol-badge ${cls}">${ClassControl.escapeHtml(rol)}</span>`;
}

function badgeEstado(activo) {
  return `<span class="cc-inst-status ${activo ? 'activo' : 'inactivo'}">
    <span class="dot"></span>${activo ? 'Activo' : 'Inactivo'}
  </span>`;
}

/* --------------------------------------------------
   8. FILTROS REACTIVOS
-------------------------------------------------- */
function initFiltros() {
  const filtroBusqueda = document.getElementById('filtro-busqueda');
  const filtroRol      = document.getElementById('filtro-rol');
  const filtroEstado   = document.getElementById('filtro-estado');
  const formFiltros    = document.getElementById('form-filtros');

  const aplicar = () => {
    const busq   = (filtroBusqueda?.value ?? '').trim().toLowerCase();
    const rolId  = filtroRol?.value ?? '';
    const estado = filtroEstado?.value ?? '';

    const lista = instructores().filter(u => {
      const nombre = `${u.nombres} ${u.apellidos}`.toLowerCase();
      return (!busq   || nombre.includes(busq) || u.identificacion.includes(busq) || u.correo.toLowerCase().includes(busq))
          && (!rolId  || u.rolId === parseInt(rolId, 10))
          && (!estado || (estado === 'Activo') === u.activo);
    });

    renderTableData(lista);
  };

  [filtroBusqueda, filtroRol, filtroEstado].forEach(el => el?.addEventListener('input', aplicar));

  formFiltros?.addEventListener('submit', e => { e.preventDefault(); aplicar(); });
  formFiltros?.addEventListener('reset',  () => setTimeout(() => renderTableData(), 0));
}

/* --------------------------------------------------
   9. MODAL CREAR / EDITAR
-------------------------------------------------- */
function abrirModalNuevo() {
  document.getElementById('modal-titulo').textContent = 'Nuevo Instructor';
  document.getElementById('inst-id').value = '';
  document.getElementById('form-instructor')?.reset();
  document.getElementById('inst-clave')?.setAttribute('required', 'required');
  document.getElementById('inst-clave-req').textContent = '*';
  limpiarErrores();
  bsModalInst.show();
}

function abrirModalEditar(id) {
  if (window.ccSoloLectura) return;
  const u = usuarios.find(x => x.id === id);
  if (!u) return;
  document.getElementById('modal-titulo').textContent   = 'Editar Instructor';
  document.getElementById('inst-id').value              = u.id;
  document.getElementById('inst-nombres').value         = u.nombres;
  document.getElementById('inst-apellidos').value       = u.apellidos;
  document.getElementById('inst-cedula').value          = u.identificacion;
  document.getElementById('inst-correo').value          = u.correo;
  document.getElementById('inst-telefono').value        = u.telefono || '';
  document.getElementById('inst-area').value            = u.profesion || '';
  document.getElementById('inst-rol').value              = u.rolId;
  document.getElementById('inst-tipoDoc').value          = u.tipoDocumentoId;
  document.getElementById('inst-estado').value           = u.activo ? 'Activo' : 'Inactivo';
  document.getElementById('inst-clave').value            = '';
  document.getElementById('inst-clave')?.removeAttribute('required');
  document.getElementById('inst-clave-req').textContent  = '';
  limpiarErrores();
  bsModalInst.show();
}

function initFormEvents() {
  document.getElementById('form-instructor')?.addEventListener('submit', async e => {
    e.preventDefault();
    if (window.ccSoloLectura) { showToast('No tienes permisos para esta acción.', 'error'); return; }

    const correo = document.getElementById('inst-correo')?.value.trim() || '';
    const username = document.getElementById('inst-username');
    if (username) username.value = correo;

    if (!validarForm()) { showToast('Completa los campos obligatorios.', 'error'); return; }

    const idVal = document.getElementById('inst-id').value;
    const isEdit = !!idVal;

    const params = new URLSearchParams({
      nombres: document.getElementById('inst-nombres').value.trim(),
      apellidos: document.getElementById('inst-apellidos').value.trim(),
      identificacion: document.getElementById('inst-cedula').value.trim(),
      correo: document.getElementById('inst-correo').value.trim(),
      telefono: document.getElementById('inst-telefono').value.trim(),
      profesion: document.getElementById('inst-area').value.trim(),
      rol: document.getElementById('inst-rol').value,
      tipoDoc: document.getElementById('inst-tipoDoc').value,
      tipoVinculacion: document.getElementById('inst-tipoVinculacion').value,
      activo: String(document.getElementById('inst-estado').value === 'Activo'),
      username: correo,
    });

    const clave = document.getElementById('inst-clave').value;
    if (clave) params.set('clave', clave);

    if (isEdit) params.set('id', idVal);
    const url = isEdit ? 'ActualizarUsuario' : 'RegistrarUsuarioAdmin';

    try {
      const resp = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
      });
      if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }

      await cargarUsuarios();
      bsModalInst.hide();
      showToast(isEdit ? 'Instructor actualizado correctamente.' : 'Instructor registrado correctamente.');
    } catch (err) {
      console.error(err);
      showToast('No se pudo guardar el instructor. Verifica los datos e intenta de nuevo.', 'error');
    }
  });
}

/* --------------------------------------------------
   10. VALIDACIÓN
-------------------------------------------------- */
function validarForm() {
  let ok = true;
  const requeridos = ['inst-nombres', 'inst-apellidos', 'inst-cedula', 'inst-correo', 'inst-rol', 'inst-tipoDoc', 'inst-estado'];

  requeridos.forEach(id => {
    const el = document.getElementById(id);
    if (!el) return;
    const vacio = !el.value.trim();
    const correoInvalido = id === 'inst-correo' && !vacio && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(el.value);
    const invalido = vacio || correoInvalido;
    el.classList.toggle('is-invalid', invalido);
    if (invalido) ok = false;
  });

  const idVal = document.getElementById('inst-id').value;
  const claveEl = document.getElementById('inst-clave');
  if (!idVal && claveEl && claveEl.value.trim().length < 6) {
    claveEl.classList.add('is-invalid');
    ok = false;
  } else if (claveEl && claveEl.value && claveEl.value.trim().length < 6) {
    claveEl.classList.add('is-invalid');
    ok = false;
  } else {
    claveEl?.classList.remove('is-invalid');
  }

  return ok;
}

function limpiarErrores() {
  document.querySelectorAll('#form-instructor .is-invalid').forEach(el => el.classList.remove('is-invalid'));
}

/* --------------------------------------------------
   11. VER DETALLE (Perfil)
-------------------------------------------------- */
function verDetalle(id) {
  const u = usuarios.find(x => x.id === id);
  if (!u) return;

  const col  = getColorAvatar(u.profesion);
  const inic = getIniciales(u.nombres, u.apellidos);

  document.getElementById('detalle-avatar').innerHTML = `
    <div class="cc-avatar-ini lg" style="background:${col.bg};color:${col.text}">${inic}</div>
    <div>
      <p class="fw-bold mb-0" style="font-size:1rem;">${ClassControl.escapeHtml(u.nombres)} ${ClassControl.escapeHtml(u.apellidos)}</p>
      <p class="mb-1" style="font-size:.82rem;color:var(--cc-muted);">${ClassControl.escapeHtml(u.correo)}</p>
      ${badgeRol(rolNombre(u.rolId))}
    </div>`;

  document.getElementById('detalle-contenido').innerHTML = `
    <div class="cc-detail-row">
      <span class="cc-detail-label">Identificación</span>
      <span class="cc-detail-value mono">${ClassControl.escapeHtml(u.identificacion)}</span>
    </div>
    <div class="cc-detail-row">
      <span class="cc-detail-label">Teléfono</span>
      <span class="cc-detail-value">${ClassControl.escapeHtml(u.telefono || '-')}</span>
    </div>
    <div class="cc-detail-row">
      <span class="cc-detail-label">Área</span>
      <span class="cc-detail-value">${ClassControl.escapeHtml(u.profesion || '-')}</span>
    </div>
    <div class="cc-detail-row">
      <span class="cc-detail-label">Estado</span>
      <span class="cc-detail-value">${badgeEstado(u.activo)}</span>
    </div>`;

  bsModalDetalle.show();
}

/* --------------------------------------------------
   12. ELIMINAR
-------------------------------------------------- */
function abrirModalEliminar(id) {
  if (window.ccSoloLectura) return;
  idAEliminar = id;
  bsModalEliminar.show();
}

function initDeleteEvents() {
  document.getElementById('btn-confirmar-eliminar')?.addEventListener('click', async () => {
    if (idAEliminar === null) return;
    try {
      const resp = await fetch('EliminarUsuario', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ id: String(idAEliminar) })
      });
      if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }
      idAEliminar = null;
      bsModalEliminar.hide();
      await cargarUsuarios();
      showToast('Instructor eliminado.', 'error');
    } catch (err) {
      console.error(err);
      showToast(err.message || 'No se pudo eliminar el instructor.', 'error');
    }
  });
}

/* --------------------------------------------------
   13. EXPORTAR CSV
-------------------------------------------------- */
function exportarCSV() {
  const lista = instructores();
  const cabecera = ['Nombres', 'Apellidos', 'Identificación', 'Correo', 'Teléfono', 'Área', 'Rol', 'Estado'];
  const filas = lista.map(u =>
    [u.nombres, u.apellidos, u.identificacion, u.correo, u.telefono || '', u.profesion || '', rolNombre(u.rolId), u.activo ? 'Activo' : 'Inactivo']
      .map(v => `"${String(v).replace(/"/g, '""')}"`)
      .join(',')
  );
  const csv  = [cabecera.join(','), ...filas].join('\n');
  const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' });
  const url  = URL.createObjectURL(blob);
  const a    = Object.assign(document.createElement('a'), { href: url, download: 'instructores_classcontrol.csv' });
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
  showToast('Reporte exportado correctamente.');
}

/* --------------------------------------------------
   14. DARK MODE
-------------------------------------------------- */
// El tema se administra de forma única en ClassControl_theme.js.
// Mantener esta página sin un segundo listener evita que un clic cambie dos veces.
function initDarkMode() {}

/* --------------------------------------------------
   15. (Sidebar móvil gestionado de forma central
       en JS/ClassControl_theme.js)
-------------------------------------------------- */

/* --------------------------------------------------
   16. TOAST
-------------------------------------------------- */
function showToast(message, type = 'success') {
  const container = document.getElementById('toast-container');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = `cc-toast ${type}`;
  toast.innerHTML = `
    <span class="material-symbols-outlined" style="font-size:1.1rem">
      ${type === 'success' ? 'check_circle' : 'error'}
    </span>
    <span>${message}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.classList.add('hide');
    toast.addEventListener('animationend', () => toast.remove(), { once: true });
  }, 3500);
}
