/* ClassControl - Gestión de Usuarios (Bootstrap + DataTables + Chart.js)
   Datos reales desde el backend:
   - GET  ConsultarUsuarios         -> lista de usuarios (JSON)
   - GET  ConsultarRoles            -> lista de roles (JSON)
   - GET  ConsultarTiposDocumento   -> lista de tipos de documento (JSON)
   - POST RegistrarUsuarioAdmin     -> crear usuario (cualquier rol)
   - POST ActualizarUsuario         -> editar usuario
   - POST EliminarUsuario           -> eliminar usuario
*/

let usuarios = [];
let roles = [];
let tiposDocumento = [];
let fichas = [];
let editingId = null;
let dataTable = null;
let roleChart = null;
let statusChart = null;

const userModalEl = document.getElementById('userModal');
const userModal = new bootstrap.Modal(userModalEl);
const toastEl = document.getElementById('appToast');
const appToast = new bootstrap.Toast(toastEl, { delay: 2500 });

function getInitials(name) {
  return name.split(' ').slice(0, 2).map(p => p[0]).join('').toUpperCase();
}

function nombreCompleto(u) {
  return [u.nombres, u.apellidos].filter(Boolean).join(' ');
}

function rolNombre(rolId) {
  const r = roles.find(r => r.id === Number(rolId));
  return r ? r.descripcion : `Rol ${rolId}`;
}

/** true si el rol seleccionado corresponde a Aprendiz. */
function esRolAprendiz(rolId) {
  const r = roles.find(r => r.id === Number(rolId));
  return !!r && (r.descripcion || '').toLowerCase().includes('aprendiz');
}

/** Muestra/oculta el selector de Ficha según el rol elegido. */
function actualizarVisibilidadFicha() {
  const wrap = document.getElementById('fieldFichaWrap');
  if (!wrap) return;
  const rolId = document.getElementById('fieldRole').value;
  wrap.classList.toggle('d-none', !esRolAprendiz(rolId));
}

function avatarHTML(u) {
  return `<div class="rounded-circle d-inline-flex align-items-center justify-content-center" style="width:36px;height:36px;background:#e8f5e0;color:#38a800;font-weight:700;">${getInitials(nombreCompleto(u))}</div>`;
}

/** Número de ficha del usuario (id → codigo_ficha) o null si no tiene. */
function fichaCodigo(u) {
  if (!u || u.fichaId == null) return null;
  const f = fichas.find(f => f.id === Number(u.fichaId));
  return f ? f.codigo : u.fichaId;
}

/** Etiqueta corta " • Ficha 2393245" para la fila del usuario. */
function fichaEtiqueta(u) {
  const c = fichaCodigo(u);
  return c != null ? ` • <span class="badge text-bg-light border">Ficha ${ClassControl.escapeHtml(c)}</span>` : '';
}

function roleBadge(rolId) {
  return `<span class="badge rounded-pill bg-light text-dark border badge-role">${ClassControl.escapeHtml(rolNombre(rolId))}</span>`;
}

function statusBadge(activo) {
  if (activo) return '<span class="badge text-bg-success">Activo</span>';
  return '<span class="badge text-bg-secondary">Inactivo</span>';
}

function rowTemplate(u) {
  const puedeGestionar = !!window.ccPuedeGestionarUsuarios;
  let acciones = `<button class="btn btn-sm btn-outline-secondary me-1" onclick="openViewModal(${u.id})">Ver</button>`;
  if (puedeGestionar) {
    acciones += `<button class="btn btn-sm btn-outline-primary me-1" onclick="openEditModal(${u.id})">Editar</button>
      <button class="btn btn-sm btn-outline-danger" onclick="deleteUser(${u.id})">Eliminar</button>`;
  }
  return [
    `<div class="d-flex align-items-center gap-2">${avatarHTML(u)}<div><div class="fw-semibold">${ClassControl.escapeHtml(nombreCompleto(u))}</div><small class="text-muted">${ClassControl.escapeHtml(u.identificacion)}${fichaEtiqueta(u)}</small></div></div>`,
    ClassControl.escapeHtml(u.correo),
    roleBadge(u.rolId),
    statusBadge(u.activo),
    `<div class="text-end">${acciones}</div>`
  ];
}

function refreshTable() {
  if (!dataTable) return;
  dataTable.clear();
  usuarios.forEach(u => dataTable.row.add(rowTemplate(u)));
  dataTable.draw();
  updateStats();
  updateCharts();
}

function updateStats() {
  const total = usuarios.length;
  const active = usuarios.filter(u => u.activo).length;
  const inactive = total - active;
  const pct = total ? ((active / total) * 100).toFixed(1) : '0.0';

  document.getElementById('statTotal').textContent = total.toLocaleString('es-CO');
  document.getElementById('statActive').textContent = active.toLocaleString('es-CO');
  document.getElementById('statInactive').textContent = inactive.toLocaleString('es-CO');
  document.getElementById('statPct').textContent = `${pct}% de la nómina total`;
}

function countBy(list, fn) {
  return list.reduce((acc, item) => {
    const key = fn(item);
    acc[key] = (acc[key] || 0) + 1;
    return acc;
  }, {});
}

function updateCharts() {
  const roleCounts = countBy(usuarios, u => rolNombre(u.rolId));
  const statusCounts = countBy(usuarios, u => (u.activo ? 'Activo' : 'Inactivo'));

  if (roleChart) {
    roleChart.data.labels = Object.keys(roleCounts);
    roleChart.data.datasets[0].data = Object.values(roleCounts);
    roleChart.update();
  }

  if (statusChart) {
    statusChart.data.labels = Object.keys(statusCounts);
    statusChart.data.datasets[0].data = Object.values(statusCounts);
    statusChart.update();
  }
}

function initCharts() {
  const roleCtx = document.getElementById('roleChart');
  const statusCtx = document.getElementById('statusChart');

  roleChart = new Chart(roleCtx, {
    type: 'doughnut',
    data: {
      labels: [],
      datasets: [{
        data: [],
        backgroundColor: ['#38a800', '#00304d', '#6ea8fe', '#ffc107']
      }]
    },
    options: { responsive: true, plugins: { legend: { position: 'bottom' } } }
  });

  statusChart = new Chart(statusCtx, {
    type: 'bar',
    data: {
      labels: [],
      datasets: [{ label: 'Usuarios', data: [], backgroundColor: ['#38a800', '#6c757d'] }]
    },
    options: {
      responsive: true,
      scales: { y: { beginAtZero: true, ticks: { precision: 0 } } },
      plugins: { legend: { display: false } }
    }
  });

  updateCharts();
}

function showToast(message, type = 'success') {
  const body = document.getElementById('appToastBody');
  toastEl.classList.remove('toast-success', 'toast-info', 'toast-error');
  body.textContent = message;
  if (type === 'error') toastEl.classList.add('toast-error');
  else if (type === 'info') toastEl.classList.add('toast-info');
  else toastEl.classList.add('toast-success');
  appToast.show();
}

function clearValidation() {
  const form = document.getElementById('userForm');
  form.classList.remove('was-validated');
  [...form.elements].forEach(el => {
    if (el.classList) el.classList.remove('is-invalid');
  });
}

function setInvalid(fieldId, message) {
  const field = document.getElementById(fieldId);
  field.classList.add('is-invalid');
  if (message) {
    const feedback = document.getElementById(`${fieldId}Feedback`);
    if (feedback) feedback.textContent = message;
  }
}

function validateForm() {
  clearValidation();
  const form = document.getElementById('userForm');

  const nombres = document.getElementById('fieldNombres').value.trim();
  const apellidos = document.getElementById('fieldApellidos').value.trim();
  const identificacion = document.getElementById('fieldIdentificacion').value.trim();
  const tipoDoc = document.getElementById('fieldTipoDoc').value;
  const email = document.getElementById('fieldEmail').value.trim();
  const telefono = document.getElementById('fieldTelefono').value.trim();
  const username = document.getElementById('fieldUsername').value.trim();
  const role = document.getElementById('fieldRole').value;
  const status = document.getElementById('fieldStatus').value;
  const password = document.getElementById('fieldPassword').value;

  let valid = true;

  if (nombres.length < 2) { setInvalid('fieldNombres'); valid = false; }
  if (apellidos.length < 2) { setInvalid('fieldApellidos'); valid = false; }
  if (!identificacion) { setInvalid('fieldIdentificacion'); valid = false; }
  if (!tipoDoc) { setInvalid('fieldTipoDoc'); valid = false; }

  const emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRe.test(email)) {
    setInvalid('fieldEmail', 'Correo electrónico no válido.');
    valid = false;
  } else {
    const dupEmail = usuarios.find(u => u.correo.toLowerCase() === email.toLowerCase() && u.id !== editingId);
    if (dupEmail) {
      setInvalid('fieldEmail', 'Este correo ya está registrado.');
      valid = false;
    }
  }

  if (!telefono) { setInvalid('fieldTelefono'); valid = false; }

  if (username.length < 3) {
    setInvalid('fieldUsername', 'Ingresa un nombre de usuario válido.');
    valid = false;
  } else {
    const dupUser = usuarios.find(u => u.username.toLowerCase() === username.toLowerCase() && u.id !== editingId);
    if (dupUser) {
      setInvalid('fieldUsername', 'Este usuario ya existe.');
      valid = false;
    }
  }

  if (!role) { setInvalid('fieldRole'); valid = false; }
  if (!status) { setInvalid('fieldStatus'); valid = false; }

  if (!editingId && password.length < 6) {
    setInvalid('fieldPassword', 'La contraseña debe tener al menos 6 caracteres.');
    valid = false;
  } else if (editingId && password.length > 0 && password.length < 6) {
    setInvalid('fieldPassword', 'La contraseña debe tener al menos 6 caracteres.');
    valid = false;
  }

  if (!valid) form.classList.add('was-validated');
  return valid;
}

function llenarSelect(selectEl, items, placeholder) {
  selectEl.innerHTML = `<option value="">${placeholder}</option>` +
    items.map(i => `<option value="${i.id}">${ClassControl.escapeHtml(i.descripcion)}</option>`).join('');
}

function llenarFiltroRoles() {
  const filterRole = document.getElementById('filterRole');
  filterRole.innerHTML = '<option value="">Todos los roles</option>' +
    roles.map(r => `<option value="${r.id}">${ClassControl.escapeHtml(r.descripcion)}</option>`).join('');
}

/** El filtro de fichas usa "Ficha {codigo}" como texto. */
function llenarFiltroFichas() {
  const sel = document.getElementById('filterFicha');
  if (!sel) return;
  const actual = sel.value;
  sel.innerHTML = '<option value="">Todas las fichas</option>' +
    fichas.map(f => `<option value="${f.id}">Ficha ${ClassControl.escapeHtml(f.codigo)}</option>`).join('');
  sel.value = actual;
}

function openNewModal() {
  editingId = null;
  document.getElementById('fieldId').value = '';
  document.getElementById('userModalLabel').textContent = 'Nuevo Usuario';
  document.getElementById('submitUserBtn').textContent = 'Crear Usuario';
  document.getElementById('fieldPasswordReq').textContent = '*';
  document.getElementById('fieldPassword').setAttribute('required', 'required');
  document.getElementById('userForm').reset();
  document.getElementById('fieldFicha').value = '';
  actualizarVisibilidadFicha();
  clearValidation();
  userModal.show();
}

window.openViewModal = function (id) {
  const u = usuarios.find(u => u.id === id);
  if (!u) return;
  document.getElementById('viewNombre').textContent = nombreCompleto(u);
  document.getElementById('viewIdentificacion').textContent = u.identificacion || '-';
  document.getElementById('viewCorreo').textContent = u.correo || '-';
  document.getElementById('viewTelefono').textContent = u.telefono || '-';
  document.getElementById('viewUsername').textContent = u.username || '-';
  document.getElementById('viewRol').textContent = rolNombre(u.rolId) || '-';
  const f = fichaCodigo(u);
  document.getElementById('viewFicha').textContent = f != null ? `Ficha ${f}` : 'Sin asignar';
  document.getElementById('viewEstado').textContent = u.activo ? 'Activo' : 'Inactivo';
  new bootstrap.Modal(document.getElementById('userViewModal')).show();
};

window.openEditModal = function (id) {
  if (!window.ccPuedeGestionarUsuarios) { showToast('No tienes permisos para esta acción.', 'error'); return; }
  const u = usuarios.find(u => u.id === id);
  if (!u) return;

  editingId = id;
  document.getElementById('fieldId').value = u.id;
  document.getElementById('userModalLabel').textContent = 'Editar Usuario';
  document.getElementById('submitUserBtn').textContent = 'Guardar Cambios';
  document.getElementById('fieldPasswordReq').textContent = '';
  document.getElementById('fieldPassword').removeAttribute('required');

  document.getElementById('fieldNombres').value = u.nombres;
  document.getElementById('fieldApellidos').value = u.apellidos;
  document.getElementById('fieldIdentificacion').value = u.identificacion;
  document.getElementById('fieldTipoDoc').value = u.tipoDocumentoId;
  document.getElementById('fieldEmail').value = u.correo;
  document.getElementById('fieldTelefono').value = u.telefono;
  document.getElementById('fieldUsername').value = u.username;
  document.getElementById('fieldDireccion').value = u.direccion || '';
  document.getElementById('fieldRole').value = u.rolId;
  document.getElementById('fieldStatus').value = String(u.activo);
  document.getElementById('fieldPassword').value = '';
  document.getElementById('fieldFicha').value = u.fichaId ?? '';
  actualizarVisibilidadFicha();
  clearValidation();
  userModal.show();
};

window.deleteUser = async function (id) {
  if (!window.ccPuedeGestionarUsuarios) { showToast('No tienes permisos para esta acción.', 'error'); return; }
  const u = usuarios.find(u => u.id === id);
  if (!u) return;
  if (!window.confirm(`¿Eliminar a "${nombreCompleto(u)}"?`)) return;

  try {
    const resp = await fetch('EliminarUsuario', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ id: String(id) })
    });
    if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }
    await cargarUsuarios();
    showToast(`Usuario "${nombreCompleto(u)}" eliminado.`, 'error');
  } catch (err) {
    console.error(err);
    showToast(err.message || 'No se pudo eliminar el usuario.', 'error');
  }
};

function exportCSV() {
  const roleFilter = document.getElementById('filterRole').value;
  const statusFilter = document.getElementById('filterStatus').value;
  const search = document.getElementById('searchInput').value.trim().toLowerCase();

  const filtered = usuarios.filter(u => {
    const estado = u.activo ? 'Activo' : 'Inactivo';
    const rol = rolNombre(u.rolId);
    const mRole = !roleFilter || rol === roleFilter;
    const mStatus = !statusFilter || estado === statusFilter;
    const mSearch = !search || [u.identificacion, nombreCompleto(u), u.correo, rol, estado].join(' ').toLowerCase().includes(search);
    return mRole && mStatus && mSearch;
  });

  const rows = [['ID', 'Nombre', 'Correo', 'Rol', 'Ficha', 'Estado'],
    ...filtered.map(u => [u.id, nombreCompleto(u), u.correo, rolNombre(u.rolId), fichaCodigo(u) ?? '', u.activo ? 'Activo' : 'Inactivo'])];
  const csv = rows.map(r => r.map(c => `"${String(c).replace(/"/g, '""')}"`).join(',')).join('\n');
  const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `usuarios_${new Date().toISOString().slice(0, 10)}.csv`;
  a.click();
  URL.revokeObjectURL(url);
  showToast('Exportación CSV lista.', 'info');
}

function initDataTable() {
  dataTable = $('#usersTable').DataTable({
    data: [],
    columns: [
      { title: 'Usuario' },
      { title: 'Correo' },
      { title: 'Rol' },
      { title: 'Estado' },
      { title: 'Acciones', orderable: false, searchable: false }
    ],
    pageLength: 5,
    lengthMenu: [5, 10, 25, 50],
    order: [[0, 'asc']],
    language: {
      search: 'Buscar:',
      lengthMenu: 'Mostrar _MENU_ registros',
      info: 'Mostrando _START_ a _END_ de _TOTAL_ usuarios',
      infoEmpty: 'Mostrando 0 a 0 de 0 usuarios',
      zeroRecords: 'No se encontraron usuarios',
      paginate: { first: 'Primero', last: 'Último', next: 'Siguiente', previous: 'Anterior' }
    }
  });

  $.fn.dataTable.ext.search.push(function (_, data) {
    const roleFilter = document.getElementById('filterRole').value;
    const statusFilter = document.getElementById('filterStatus').value;
    const roleText = $('<div>').html(data[2]).text().trim();
    const statusText = $('<div>').html(data[3]).text().trim();

    const roleOk = !roleFilter || rolNombre(Number(roleFilter)) === roleText;
    const statusOk = !statusFilter || statusText === statusFilter;
    return roleOk && statusOk;
  });
}

async function cargarUsuarios() {
  const ficha = document.getElementById('filterFicha')?.value || '';
  const qs = ficha ? `?ficha=${encodeURIComponent(ficha)}` : '';
  const resp = await fetch('ConsultarUsuarios' + qs);
  if (!resp.ok) throw new Error('No se pudo cargar la lista de usuarios');
  usuarios = await resp.json();
  refreshTable();
}

async function cargarCatalogos() {
  const [rolesResp, tiposDocResp, fichasResp] = await Promise.all([
    fetch('ConsultarRoles'),
    fetch('ConsultarTiposDocumento'),
    fetch('ConsultarFichas')
  ]);
  roles = rolesResp.ok ? await rolesResp.json() : [];
  tiposDocumento = tiposDocResp.ok ? await tiposDocResp.json() : [];
  fichas = fichasResp.ok ? await fichasResp.json() : [];

  llenarSelect(document.getElementById('fieldTipoDoc'), tiposDocumento, 'Seleccione...');
  llenarSelect(document.getElementById('fieldRole'), roles, 'Seleccione...');
  llenarFiltroRoles();
  llenarSelectFichas();
  llenarFiltroFichas();
}

/** El select de fichas usa "Ficha {codigo}" como texto (no hay descripcion). */
function llenarSelectFichas() {
  const sel = document.getElementById('fieldFicha');
  if (!sel) return;
  sel.innerHTML = '<option value="">Sin asignar</option>' +
    fichas.map(f => `<option value="${f.id}">Ficha ${ClassControl.escapeHtml(f.codigo)}</option>`).join('');
}

async function inicializarDatos() {
  try {
    await cargarCatalogos();
    await cargarUsuarios();
  } catch (err) {
    console.error(err);
    showToast('No se pudieron cargar los datos desde el servidor.', 'error');
  }
}

function bindEvents() {
  const btnNewUser = document.getElementById('btnNewUser');
  if (btnNewUser) btnNewUser.addEventListener('click', openNewModal);

  document.getElementById('searchInput').addEventListener('input', function (e) {
    dataTable.search(e.target.value).draw();
  });

  document.getElementById('filterRole').addEventListener('change', function () {
    dataTable.draw();
  });

  document.getElementById('filterStatus').addEventListener('change', function () {
    dataTable.draw();
  });

  // Filtro por ficha → recarga desde el servidor (los aprendices de esa ficha).
  document.getElementById('filterFicha').addEventListener('change', async function () {
    try {
      await cargarUsuarios();
      const f = fichas.find(x => String(x.id) === String(this.value));
      showToast(f ? `Mostrando usuarios de la ficha ${f.codigo}.` : 'Mostrando todos los usuarios.', 'info');
    } catch (err) {
      console.error(err);
      showToast('No se pudo filtrar por ficha.', 'error');
    }
  });

  // El campo "Ficha del aprendiz" solo se muestra cuando el rol es Aprendiz.
  document.getElementById('fieldRole').addEventListener('change', actualizarVisibilidadFicha);

  document.getElementById('btnExport').addEventListener('click', exportCSV);

  document.getElementById('userForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    if (!window.ccPuedeGestionarUsuarios) { showToast('No tienes permisos para esta acción.', 'error'); return; }
    if (!validateForm()) return;

    const params = new URLSearchParams({
      nombres: document.getElementById('fieldNombres').value.trim(),
      apellidos: document.getElementById('fieldApellidos').value.trim(),
      identificacion: document.getElementById('fieldIdentificacion').value.trim(),
      tipoDoc: document.getElementById('fieldTipoDoc').value,
      correo: document.getElementById('fieldEmail').value.trim(),
      telefono: document.getElementById('fieldTelefono').value.trim(),
      username: document.getElementById('fieldUsername').value.trim(),
      direccion: document.getElementById('fieldDireccion').value.trim(),
      rol: document.getElementById('fieldRole').value,
      activo: document.getElementById('fieldStatus').value,
      ficha: document.getElementById('fieldFicha').value
    });

    const password = document.getElementById('fieldPassword').value;
    if (password) params.set('clave', password);

    const isEdit = !!editingId;
    if (isEdit) params.set('id', String(editingId));

    const url = isEdit ? 'ActualizarUsuario' : 'RegistrarUsuarioAdmin';

    try {
      const resp = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
      });
      if (!resp.ok) { let m = 'Respuesta no exitosa del servidor'; try { const d = await resp.json(); if (d && d.error) m = d.error; } catch (_) {} throw new Error(m); }

      await cargarUsuarios();
      userModal.hide();
      showToast(isEdit ? 'Usuario actualizado correctamente.' : 'Usuario creado correctamente.', 'success');
    } catch (err) {
      console.error(err);
      showToast('No se pudo guardar el usuario. Verifica los datos e intenta de nuevo.', 'error');
    }
  });

  userModalEl.addEventListener('hidden.bs.modal', () => {
    editingId = null;
    document.getElementById('userForm').reset();
    clearValidation();
  });
}

document.addEventListener('DOMContentLoaded', () => {
  initDataTable();
  bindEvents();
  initCharts();
  inicializarDatos();
});
