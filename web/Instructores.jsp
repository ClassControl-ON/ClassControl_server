<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- ---------------------- PROTECCIÓN DE SESIÓN ---------------------- --%>
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

  // Aprendiz (2) no ve Instructores. El Instructor (1) sí, en modo de
  // solo lectura (para consultar datos de contacto de sus colegas).
  if (sesRol == null || sesRol == 2) {
    response.sendRedirect("Pagina_Principal.jsp?error=permiso");
    return;
  }
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="es_CO"/>
<fmt:setBundle basename="messages_es" var="i18n"/>
<!DOCTYPE html>
<html lang="es" data-bs-theme="light">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title><fmt:message bundle="${i18n}" key="instructores.title"/></title>

  <link rel="icon" type="image/png" href="img/logo.png" />
  <!-- Google Fonts: DM Sans + DM Mono -->
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet" />
  <!-- Material Symbols -->
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet" />

  <!-- Bootstrap 5.3 -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
  <!-- DataTables Bootstrap 5 -->
  <link href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css" rel="stylesheet" />

  <!-- Shared base styles -->
  <link rel="stylesheet" href="CSS/ClassControl_base.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_theme.css" />
  <link rel="stylesheet" href="CSS/ClassControl_sidebar.css" />
  <link rel="stylesheet" href="CSS/Pagina_PrincipalCSS.css" />
  <link rel="stylesheet" href="CSS/InstructoresCSS.css" />
</head>

<body>
<div class="cc-wrapper">

  <!-- -------------- SIDEBAR -------------- -->
  <%   String ccActivePage = "instructores"; %>
<%@ include file="_Sidebar.jspf" %>

  <!-- -------------- MAIN -------------- -->
  <main class="cc-main">

    <!-- HEADER -->
    <header class="cc-header">
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-sm cc-hamburger d-lg-none me-1" id="sidebar-toggle" aria-label="Menú">
          <span class="material-symbols-outlined">menu</span>
        </button>
        <div>
          <h2 class="cc-page-title mb-0"><fmt:message bundle="${i18n}" key="instructores.header.title"/></h2>
          <p class="cc-page-sub mb-0 d-none d-md-block"><fmt:message bundle="${i18n}" key="instructores.header.subtitle"/></p>
        </div>
      </div>

      <div class="cc-header-right">
        <!-- <fmt:message bundle="${i18n}" key="common.btn.exportar"/> -->
        <button id="btn-descargar" class="btn btn-outline-secondary btn-sm d-flex align-items-center gap-1">
          <span class="material-symbols-outlined" style="font-size:1rem">file_download</span>
          <span class="d-none d-md-inline"><fmt:message bundle="${i18n}" key="common.btn.exportar"/></span>
        </button>
        <!-- <fmt:message bundle="${i18n}" key="instructores.btn.nuevo"/> -->
        <% if (sesRol != null && sesRol == 3) { %>
        <button id="btn-nuevo-instructor" class="btn cc-btn-primary"
                data-bs-toggle="modal" data-bs-target="#modal-instructor">
          <span class="material-symbols-outlined">add_circle</span>
          <span class="d-none d-sm-inline"><fmt:message bundle="${i18n}" key="instructores.btn.nuevo"/></span>
        </button>
        <% } %>
        <!-- Dark toggle -->
        <button id="dark-toggle" class="btn cc-icon-btn" title="Cambiar tema">
          <span class="material-symbols-outlined">dark_mode</span>
        </button>
      </div>
    </header>

    <!-- BODY -->
    <div class="cc-body">

      <!-- -- Métricas -- -->
      <div class="row g-3 mb-4" id="metricas"></div>

      <!-- -- Filtros -- -->
      <div class="cc-widget-card mb-4">
        <form id="form-filtros" novalidate>
          <div class="row g-3 align-items-end">
            <!-- Búsqueda -->
            <div class="col-12 col-md-4">
              <div class="input-group cc-search-group">
                <span class="input-group-text bg-transparent border-end-0">
                  <span class="material-symbols-outlined cc-icon-sm">search</span>
                </span>
                <input id="filtro-busqueda" type="search" class="form-control border-start-0 ps-0"
                       placeholder="Buscar por nombre, apellido o cédula…" autocomplete="off" />
              </div>
            </div>
            <!-- Rol -->
            <div class="col-6 col-md-2">
              <select id="filtro-rol" class="form-select">
                <option value="">Todos los Roles</option>
              </select>
            </div>
            <!-- Estado -->
            <div class="col-6 col-md-2">
              <select id="filtro-estado" class="form-select">
                <option value="">Estado</option>
                <option value="Activo">Activo</option>
                <option value="Inactivo">Inactivo</option>
              </select>
            </div>
            <!-- Botones de filtro -->
            <div class="col-12 col-md-4 d-flex gap-2 justify-content-md-end">
              <button type="submit" class="btn cc-btn-primary flex-grow-1 flex-md-grow-0">
                <span class="material-symbols-outlined" style="font-size:1rem">filter_list</span> <fmt:message bundle="${i18n}" key="common.btn.filtrar"/>
              </button>
              <button type="reset" id="btn-limpiar" class="btn btn-outline-secondary" title="<fmt:message bundle="${i18n}" key="common.tooltip.limpiarFiltros"/>">
                <span class="material-symbols-outlined" style="font-size:1rem">filter_alt_off</span>
              </button>
            </div>
          </div>
        </form>
      </div>

      <!-- -- DataTable -- -->
      <div class="cc-table-card">
        <div class="table-responsive">
          <table id="instructores-table" class="table table-hover align-middle mb-0 w-100">
            <thead>
              <tr>
                <th>Instructor</th>
                <th>Identificación</th>
                <th>Correo</th>
                <th>Área</th>
                <th>Rol</th>
                <th>Estado</th>
                <th class="text-end">Acciones</th>
              </tr>
            </thead>
            <tbody id="tbody-instructores"></tbody>
          </table>
        </div>
      </div>

    </div><!-- /cc-body -->
  </main>
</div><!-- /cc-wrapper -->

<!-- Toast container -->
<div id="toast-container" aria-live="polite" aria-atomic="true"></div>

<!-- -------------- MODAL: Nuevo / Editar Instructor -------------- -->
<div class="modal fade" id="modal-instructor" tabindex="-1"
     aria-labelledby="modal-titulo" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="modal-titulo"><fmt:message bundle="${i18n}" key="instructores.btn.nuevo"/></h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="<fmt:message bundle="${i18n}" key="common.btn.cerrar"/>"></button>
      </div>
      <form id="form-instructor" action="RegistrarUsuarioAdmin" method="POST" novalidate>
        <input type="hidden" id="inst-id" />
        <div class="modal-body">
          <div class="row g-3">
            <!-- Nombres + Apellidos -->
            <div class="col-6">
              <label for="inst-nombres" class="form-label">Nombres <span class="text-danger">*</span></label>
              <input id="inst-nombres" name="nombres" type="text" class="form-control"
                     placeholder="Ej. Carlos Alberto" required />
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="common.validation.requerido"/></div>
            </div>
            <div class="col-6">
              <label for="inst-apellidos" class="form-label">Apellidos <span class="text-danger">*</span></label>
              <input id="inst-apellidos" name="apellidos" type="text" class="form-control"
                     placeholder="Ej. Gómez Ruiz" required />
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="common.validation.requerido"/></div>
            </div>
            <!-- Cédula -->
            <div class="col-12">
              <label for="inst-cedula" class="form-label">Cédula / Identificación <span class="text-danger">*</span></label>
              <input id="inst-cedula" name="identificacion" type="text" class="form-control"
                     placeholder="Ej. 1.020.304.050" required />
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="common.validation.requerido"/></div>
            </div>
            <!-- Correo -->
            <div class="col-12">
              <label for="inst-correo" class="form-label">Correo Electrónico <span class="text-danger">*</span></label>
              <input id="inst-correo" name="correo" type="email" class="form-control"
                     placeholder="Ej. nombre@sena.edu.co" required />
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="instructores.validation.correo"/></div>
            </div>
            <!-- Teléfono -->
            <div class="col-12">
              <label for="inst-telefono" class="form-label">Teléfono</label>
              <input id="inst-telefono" name="telefono" type="tel" class="form-control"
                     placeholder="Ej. 3001234567" />
            </div>
            <!-- Área + Rol -->
            <div class="col-6">
              <label for="inst-area" class="form-label">Área / Especialidad</label>
              <input id="inst-area" name="profesion" type="text" class="form-control"
                     placeholder="Ej. Tecnología" />
            </div>
            <div class="col-6">
              <label for="inst-rol" class="form-label">Rol <span class="text-danger">*</span></label>
              <select id="inst-rol" name="rol" class="form-select" required>
                <option value="">Seleccionar…</option>
              </select>
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="instructores.validation.rol"/></div>
            </div>
            <!-- Tipo de documento -->
            <div class="col-6">
              <label for="inst-tipoDoc" class="form-label">Tipo de Documento <span class="text-danger">*</span></label>
              <select id="inst-tipoDoc" name="tipoDoc" class="form-select" required>
                <option value="">Seleccionar…</option>
              </select>
            </div>
            <!-- Estado -->
            <div class="col-6">
              <label for="inst-estado" class="form-label">Estado <span class="text-danger">*</span></label>
              <select id="inst-estado" class="form-select" required>
                <option value="">Seleccionar…</option>
                <option value="Activo">Activo</option>
                <option value="Inactivo">Inactivo</option>
              </select>
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="instructores.validation.estado"/></div>
            </div>
            <!-- Contraseña -->
            <div class="col-12">
              <label for="inst-clave" class="form-label">Contraseña <span id="inst-clave-req" class="text-danger">*</span></label>
              <input id="inst-clave" name="clave" type="password" class="form-control"
                     placeholder="Mínimo 6 caracteres" minlength="6" />
              <div class="form-text">Déjala en blanco al editar para conservar la contraseña actual.</div>
            </div>
          <input type="hidden" id="inst-username" name="username" value=""/>
          <input type="hidden" id="inst-tipoVinculacion" name="tipoVinculacion" value=""/>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cancelar"/></button>
          <button type="submit" class="btn cc-btn-primary">
            <span class="material-symbols-outlined">save</span> <fmt:message bundle="${i18n}" key="common.btn.guardar"/>
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- -------------- MODAL: Ver Perfil (read-only) -------------- -->
<div class="modal fade" id="modal-detalle" tabindex="-1"
     aria-labelledby="detalle-modal-title" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="detalle-modal-title"><fmt:message bundle="${i18n}" key="instructores.detalle.titulo"/></h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="<fmt:message bundle="${i18n}" key="common.btn.cerrar"/>"></button>
      </div>
      <div class="modal-body">
        <!-- Avatar + nombre -->
        <div id="detalle-avatar" class="d-flex align-items-center gap-3 mb-4 pb-4 border-bottom"></div>
        <!-- Detalles -->
        <div id="detalle-contenido" class="cc-detail-grid"></div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cerrar"/></button>
      </div>
    </div>
  </div>
</div>

<!-- -------------- MODAL: Confirmar Eliminación -------------- -->
<div class="modal fade" id="modal-eliminar" tabindex="-1"
     aria-labelledby="eliminar-modal-title" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-sm">
    <div class="modal-content text-center">
      <div class="modal-body pt-4">
        <div class="cc-confirm-icon mx-auto mb-3">
          <span class="material-symbols-outlined" style="color:#ef4444;font-size:1.8rem">person_remove</span>
        </div>
        <h5 class="mb-1" id="eliminar-modal-title"><fmt:message bundle="${i18n}" key="instructores.confirm.eliminarTitulo"/></h5>
        <p class="mb-0" style="font-size:.82rem;color:var(--cc-muted);"><fmt:message bundle="${i18n}" key="common.confirm.irreversible"/></p>
      </div>
      <div class="modal-footer justify-content-center border-0 pt-0">
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cancelar"/></button>
        <button type="button" id="btn-confirmar-eliminar" class="btn btn-danger">
          <span class="material-symbols-outlined" style="font-size:.9rem">delete</span> <fmt:message bundle="${i18n}" key="common.btn.eliminar"/>
        </button>
      </div>
    </div>
  </div>
</div>

<!-- Scripts -->
<script>
  // Solo lectura para todos excepto Administrador (3): coordinador (4) y
  // instructor (1) solo visualizan datos de contacto.
  window.ccSoloLectura = <%= (sesRol == null || sesRol != 3) %>;
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js"></script>
<script src="JS/ClassControl_ui.js"></script>
<script src="JS/InstructoresJS.js"></script>
  <script src="JS/ClassControl_theme.js"></script>
</body>
</html>
