<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Protección de sesión --%>
<%
  String sesNombres   = (String)  session.getAttribute("nombres");
  String sesApellidos = (String)  session.getAttribute("apellidos");
  String sesUsername  = (String)  session.getAttribute("username");
  Integer sesRol      = (Integer) session.getAttribute("rol");
  Integer sesIdUser   = (Integer) session.getAttribute("id_usuario");

  if (sesNombres == null) {
    response.sendRedirect("Inicio_de_sesion.jsp");
    return;
  }
  // Instructor (1), Administrador (3) y Coordinador (4).
  if (sesRol == null || !(sesRol == 1 || sesRol == 3 || sesRol == 4)) {
    response.sendRedirect("Pagina_Principal.jsp?error=permiso");
    return;
  }
  boolean esAdminWeb       = (sesRol == 3);
  boolean esInstructorWeb  = (sesRol == 1);
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Incidencias - ClassControl</title>
  <link rel="icon" type="image/png" href="img/logo.png" />
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet" />
  <link rel="stylesheet" href="CSS/ClassControl_base.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_theme.css" />
  <link rel="stylesheet" href="CSS/ClassControl_sidebar.css" />
</head>
<body>
<div class="cc-layout">

<% String ccActivePage = "incidencias"; %>
<%@ include file="_Sidebar.jspf" %>

<main class="cc-main cc-catalog-main">

  <!-- Toggle sidebar (móvil) -->
  <button class="cc-sidebar-toggle d-lg-none" id="sidebar-toggle" aria-label="Abrir menú">
    <span class="material-symbols-outlined">menu</span>
  </button>

  <!-- Page header -->
  <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
    <div>
      <h1 class="cc-page-title"><%= esAdminWeb ? "Incidencias reportadas" : "Mis incidencias reportadas" %></h1>
      <p class="text-muted mb-0">
        <%= esAdminWeb
              ? "Los instructores envían incidencias desde la aplicación móvil."
              : "Aquí ves el estado de las incidencias que has enviado." %>
      </p>
    </div>
    <div class="d-flex flex-wrap gap-2">
      <button id="dark-toggle" class="btn cc-icon-btn" title="Cambiar tema">
        <span class="material-symbols-outlined">dark_mode</span>
      </button>
      <button type="button" class="btn cc-btn-outline" id="btnActualizar">
        <span class="material-symbols-outlined">refresh</span>
        Actualizar
      </button>
      <% if (esInstructorWeb) { %>
      <button type="button" class="btn cc-btn-primary" id="btnNueva">
        <span class="material-symbols-outlined">add_circle</span>
        Reportar incidencia
      </button>
      <% } else { %>
      <button type="button" class="btn cc-btn-outline" id="btnCSV">
        <span class="material-symbols-outlined">file_download</span>
        Descargar CSV
      </button>
      <% } %>
    </div>
  </div>

  <div class="card cc-card mb-4">
    <div class="card-body">
      <div class="table-responsive">
        <table class="table cc-table align-middle mb-0">
          <thead>
            <tr>
              <th>Título</th>
              <th>Tipo</th>
              <th>Ambiente</th>
              <th>Reporta</th>
              <th>Fecha</th>
              <th>Estado</th>
              <th>Respuesta</th>
              <th class="text-end">Acciones</th>
            </tr>
          </thead>
          <tbody id="cuerpoTabla">
            <tr><td colspan="8" class="text-center text-muted py-4">Cargando...</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</main>
</div><!-- /.cc-layout -->

<!-- Modal atender (solo admin) -->
<div class="modal fade" id="modalAtender" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="atenderTitulo">Atender incidencia</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <label class="form-label" for="respuestaAtender">Respuesta del administrador *</label>
        <textarea id="respuestaAtender" class="form-control" rows="3"
                  placeholder="Ej.: El ambiente fue revisado y liberado."></textarea>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn cc-btn-outline" data-bs-dismiss="modal">Cancelar</button>
        <button type="button" class="btn cc-btn-primary" id="btnGuardarAtencion">Guardar atención</button>
      </div>
    </div>
  </div>
</div>

<!-- Modal reportar incidencia (solo instructor) -->
<% if (esInstructorWeb) { %>
<div class="modal fade" id="modalNueva" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Reportar incidencia</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label" for="nuevaTitulo">T&iacute;tulo *</label>
          <input type="text" id="nuevaTitulo" class="form-control"
                 placeholder="Ej.: Ambiente ocupado sin reserva" />
        </div>
        <div class="mb-3">
          <label class="form-label" for="selTipoNuevo">Tipo</label>
          <select id="selTipoNuevo" class="form-select">
            <option value="ambiente">Ambiente</option>
            <option value="equipo">Equipo</option>
            <option value="otro">Otro</option>
          </select>
        </div>
        <div class="mb-3">
          <label class="form-label" for="selAmbiente">Ambiente (opcional)</label>
          <select id="selAmbiente" class="form-select">
            <option value="">Ninguno</option>
          </select>
        </div>
        <div class="mb-2">
          <label class="form-label" for="nuevaDescripcion">Descripci&oacute;n</label>
          <textarea id="nuevaDescripcion" class="form-control" rows="3"
                    placeholder="Cuenta qu&eacute; pas&oacute;..."></textarea>
        </div>
        <p class="text-muted mb-0" style="font-size:.8rem;">
          El administrador recibir&aacute; tu reporte y podr&aacute; responderlo.
        </p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn cc-btn-outline" data-bs-dismiss="modal">Cancelar</button>
        <button type="button" class="btn cc-btn-primary" id="btnEnviarReporte">Enviar reporte</button>
      </div>
    </div>
  </div>
</div>
<% } %>

<!-- Modal ver incidencia (solo lectura) -->
<div class="modal fade" id="modalVerIncidencia" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Detalle de la incidencia</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label">Título</label>
          <p id="verTitulo" class="fw-semibold mb-0">-</p>
        </div>
        <div class="mb-3">
          <label class="form-label">Tipo</label>
          <p id="verTipo" class="fw-semibold mb-0">-</p>
        </div>
        <div class="mb-3">
          <label class="form-label">Ambiente</label>
          <p id="verAmbiente" class="fw-semibold mb-0">-</p>
        </div>
        <div class="mb-3">
          <label class="form-label">Reporta</label>
          <p id="verReporta" class="fw-semibold mb-0">-</p>
        </div>
        <div class="mb-3">
          <label class="form-label">Fecha</label>
          <p id="verFecha" class="fw-semibold mb-0">-</p>
        </div>
        <div class="mb-3">
          <label class="form-label">Estado</label>
          <p id="verEstado" class="fw-semibold mb-0">-</p>
        </div>
        <div class="mb-0">
          <label class="form-label">Respuesta del administrador</label>
          <p id="verRespuesta" class="fw-semibold mb-0">-</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn cc-btn-outline" data-bs-dismiss="modal">Cerrar</button>
      </div>
    </div>
  </div>
</div>

<!-- Modal confirmar eliminación (solo admin) -->
<div class="modal fade" id="modalEliminarIncidencia" tabindex="-1"
     aria-labelledby="eliminarIncidenciaTitulo" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="eliminarIncidenciaTitulo">¿Eliminar incidencia?</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <p class="fw-semibold mb-1" id="eliminarIncidenciaNombre">-</p>
        <p class="text-muted mb-0">Esta acción no se puede deshacer.</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn cc-btn-outline" data-bs-dismiss="modal">Cancelar</button>
        <button type="button" class="btn cc-btn-primary" id="btnConfirmarEliminar">Eliminar</button>
      </div>
    </div>
  </div>
</div>

<div class="toast-container position-fixed bottom-0 end-0 p-3" id="toast-container"></div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JS/ClassControl_ui.js"></script>
<script src="JS/ClassControl_theme.js"></script>
<script>
(() => {
  'use strict';
  const ES_ADMIN = <%= esAdminWeb %>;
  const ES_INSTRUCTOR = <%= esInstructorWeb %>;
  let incidencias = [];
  let idAtender = null;
  let idEliminar = null;
  let ambientesSel = [];

  const esc = v => String(v ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');

  function toast(msg, tipo = 'ok') {
    const container = document.getElementById('toast-container');
    if (!container) { if (window.ClassControl?.ui?.toast) { window.ClassControl.ui.toast(msg, tipo); } return; }
    const id   = 'toast-inc-' + Date.now();
    const icon = tipo === 'ok' ? 'check_circle' : 'error';
    const iconColor = tipo === 'ok' ? 'text-success' : 'text-danger';
    container.insertAdjacentHTML('beforeend',
      '<div id="' + id + '" class="toast align-items-center border-0 shadow-sm" role="alert">' +
        '<div class="d-flex">' +
          '<div class="toast-body d-flex align-items-center gap-2" ' +
               'style="font-family:var(--cc-font);font-size:.875rem">' +
            '<span class="material-symbols-outlined ' + iconColor + '">' + icon + '</span>' +
            '<span class="cc-inc-toast-msg"></span>' +
          '</div>' +
          '<button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Cerrar"></button>' +
        '</div>' +
      '</div>');
    const el = document.getElementById(id);
    el.querySelector('.cc-inc-toast-msg').textContent = msg;
    const t = new bootstrap.Toast(el, { delay: 3500 });
    el.addEventListener('hidden.bs.toast', () => el.remove());
    t.show();
  }

  async function cargar() {
    const cuerpo = document.getElementById('cuerpoTabla');
    cuerpo.innerHTML = '<tr><td colspan="8" class="text-center text-muted py-4">Cargando...</td></tr>';
    try {
      const resp = await fetch('ConsultarReportes', { headers: { 'Accept': 'application/json' } });
      if (!resp.ok) throw new Error('El servidor no respondió correctamente');
      incidencias = await resp.json();
      if (!Array.isArray(incidencias)) incidencias = [];

      if (incidencias.length === 0) {
        cuerpo.innerHTML = '<tr><td colspan="8" class="text-center text-muted py-4">No hay incidencias registradas.</td></tr>';
        return;
      }
      cuerpo.innerHTML = incidencias.map(r => {
        const atendido = String(r.estado || '').toLowerCase() === 'atendido';
        const badge = atendido
          ? '<span class="badge text-bg-success">Atendido</span>'
          : '<span class="badge text-bg-warning">Pendiente</span>';
        let acciones = '';
        if (ES_ADMIN) {
          acciones = '<td class="text-end">' +
            (atendido ? '' :
              '<button class="cc-btn-icon cc-btn-icon--edit" data-atender="' + r.id + '" title="Atender">' +
                '<span class="material-symbols-outlined">task_alt</span></button> ') +
            '<button class="cc-btn-icon cc-btn-icon--delete" data-eliminar="' + r.id + '" title="Eliminar">' +
              '<span class="material-symbols-outlined">delete</span></button>' +
          '</td>';
        } else {
          acciones = '<td class="text-end">' +
            '<button class="cc-btn-icon cc-btn-icon--view" data-ver="' + r.id + '" title="Ver">' +
              '<span class="material-symbols-outlined">visibility</span></button>' +
          '</td>';
        }
        return '<tr>' +
          '<td>' + esc(r.titulo) + '</td>' +
          '<td>' + esc(r.tipo || '-') + '</td>' +
          '<td>' + esc(r.ambiente || '-') + '</td>' +
          '<td>' + esc(r.reporta || '-') + '</td>' +
          '<td>' + esc(String(r.fechaCreacion || '').substring(0, 16)) + '</td>' +
          '<td>' + badge + '</td>' +
          '<td>' + esc(r.respuestaAdmin || '-') + '</td>' +
          acciones +
        '</tr>';
      }).join('');
    } catch (err) {
      console.error(err);
      cuerpo.innerHTML = '<tr><td colspan="8" class="text-center text-danger py-4">' +
        esc(err.message || 'No se pudieron cargar las incidencias.') + '</td></tr>';
    }
  }

  function abrirAtender(id, titulo) {
    idAtender = id;
    document.getElementById('atenderTitulo').textContent = 'Atender: ' + titulo;
    document.getElementById('respuestaAtender').value = '';
    bootstrap.Modal.getOrCreateInstance(document.getElementById('modalAtender')).show();
  }

  function verIncidencia(id) {
    const r = incidencias.find(x => String(x.id) === String(id));
    if (!r) return;
    const atendido = String(r.estado || '').toLowerCase() === 'atendido';
    document.getElementById('verTitulo').textContent = r.titulo || '-';
    document.getElementById('verTipo').textContent = r.tipo || '-';
    document.getElementById('verAmbiente').textContent = r.ambiente || '-';
    document.getElementById('verReporta').textContent = r.reporta || '-';
    document.getElementById('verFecha').textContent = String(r.fechaCreacion || '').substring(0, 16) || '-';
    document.getElementById('verEstado').textContent = atendido ? 'Atendido' : 'Pendiente';
    document.getElementById('verRespuesta').textContent = r.respuestaAdmin || 'Sin respuesta aún.';
    bootstrap.Modal.getOrCreateInstance(document.getElementById('modalVerIncidencia')).show();
  }

  async function guardarAtencion() {
    const respuesta = document.getElementById('respuestaAtender').value.trim();
    if (!respuesta) { toast('Escribe la respuesta antes de guardar.', 'error'); return; }
    try {
      const resp = await fetch('AtenderReporte', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ id: String(idAtender), respuesta })
      });
      const data = await resp.json().catch(() => ({}));
      if (!resp.ok) throw new Error(data.error || 'No se pudo registrar la atención');
      bootstrap.Modal.getInstance(document.getElementById('modalAtender'))?.hide();
      toast('Incidencia atendida.', 'ok');
      cargar();
    } catch (err) {
      toast(err.message || 'Error al atender.', 'error');
    }
  }

  async function cargarAmbientes() {
    try {
      const resp = await fetch('ConsultarAmbientes', { headers: { 'Accept': 'application/json' } });
      if (!resp.ok) return;
      ambientesSel = await resp.json();
      const sel = document.getElementById('selAmbiente');
      if (sel) sel.innerHTML = '<option value="">Ninguno</option>' +
        ambientesSel.map(a => '<option value="' + a.id + '">' + esc(a.descripcion) + '</option>').join('');
    } catch (_) { /* el ambiente es opcional */ }
  }

  function abrirNuevo() {
    document.getElementById('nuevaTitulo').value = '';
    document.getElementById('selTipoNuevo').value = 'ambiente';
    document.getElementById('selAmbiente').value = '';
    document.getElementById('nuevaDescripcion').value = '';
    bootstrap.Modal.getOrCreateInstance(document.getElementById('modalNueva')).show();
  }

  async function enviarReporte() {
    const titulo = document.getElementById('nuevaTitulo').value.trim();
    if (!titulo) { toast('Escribe un título para el reporte.', 'error'); return; }
    try {
      const params = { titulo: titulo,
                       tipo: document.getElementById('selTipoNuevo').value,
                       descripcion: document.getElementById('nuevaDescripcion').value.trim() };
      const amb = document.getElementById('selAmbiente').value;
      if (amb) params.ambienteId = amb;
      const resp = await fetch('RegistrarReporte', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams(params)
      });
      const data = await resp.json().catch(() => ({}));
      if (!resp.ok) throw new Error(data.error || 'No se pudo enviar el reporte');
      bootstrap.Modal.getInstance(document.getElementById('modalNueva'))?.hide();
      toast('Reporte enviado al administrador.', 'ok');
      cargar();
    } catch (err) {
      toast(err.message || 'Error al enviar el reporte.', 'error');
    }
  }

  function abrirConfirmarEliminar(id, titulo) {
    idEliminar = id;
    document.getElementById('eliminarIncidenciaNombre').textContent = titulo || 'Incidencia';
    bootstrap.Modal.getOrCreateInstance(document.getElementById('modalEliminarIncidencia')).show();
  }

  async function eliminar() {
    if (idEliminar == null) return;
    const modalEl = document.getElementById('modalEliminarIncidencia');
    try {
      const resp = await fetch('EliminarReporte', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ id: String(idEliminar) })
      });
      const data = await resp.json().catch(() => ({}));
      if (!resp.ok) throw new Error(data.error || 'No se pudo eliminar');
      bootstrap.Modal.getInstance(modalEl)?.hide();
      toast('Incidencia eliminada.', 'ok');
      cargar();
    } catch (err) {
      bootstrap.Modal.getInstance(modalEl)?.hide();
      toast(err.message || 'Error al eliminar.', 'error');
    }
  }

  function descargarCSV() {
    if (!incidencias.length) { toast('No hay incidencias para descargar.', 'error'); return; }
    const cabeceras = ['ID','Título','Tipo','Ambiente','Reporta','Fecha','Estado','Respuesta'];
    const filas = incidencias.map(r => [
      r.id, r.titulo, r.tipo || '', r.ambiente || '', r.reporta || '',
      r.fechaCreacion || '', r.estado || '', r.respuestaAdmin || ''
    ]);
    const csv = [cabeceras].concat(filas)
      .map(f => f.map(c => '"' + String(c ?? '').replace(/"/g,'""') + '"').join(';'))
      .join('\r\n');
    const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = 'incidencias_classcontrol.csv';
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(a.href);
  }

  document.addEventListener('DOMContentLoaded', () => {
    cargar();
    if (ES_INSTRUCTOR) {
      cargarAmbientes();
      document.getElementById('btnNueva')?.addEventListener('click', abrirNuevo);
      document.getElementById('btnEnviarReporte')?.addEventListener('click', enviarReporte);
    } else {
      document.getElementById('btnCSV')?.addEventListener('click', descargarCSV);
    }
    document.getElementById('btnActualizar')?.addEventListener('click', cargar);
    document.getElementById('btnGuardarAtencion')?.addEventListener('click', guardarAtencion);
    document.getElementById('btnConfirmarEliminar')?.addEventListener('click', eliminar);
    const modalEliminarEl = document.getElementById('modalEliminarIncidencia');
    modalEliminarEl?.addEventListener('hidden.bs.modal', () => { idEliminar = null; });
    document.getElementById('cuerpoTabla')?.addEventListener('click', e => {
      const bAt = e.target.closest('[data-atender]');
      const bEl = e.target.closest('[data-eliminar]');
      const bVr = e.target.closest('[data-ver]');
      if (bAt) {
        const inc = incidencias.find(i => String(i.id) === bAt.dataset.atender);
        abrirAtender(inc.id, inc.titulo);
      }
      if (bEl) {
        const inc = incidencias.find(i => String(i.id) === bEl.dataset.eliminar);
        abrirConfirmarEliminar(inc.id, inc.titulo);
      }
      if (bVr) verIncidencia(bVr.dataset.ver);
    });
  });
})();
</script>
</body>
</html>
