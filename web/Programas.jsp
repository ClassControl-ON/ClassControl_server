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

  // Solo Administrador (3) y Coordinador (4) ven Programas.
  if (sesRol == null || (sesRol != 3 && sesRol != 4)) {
    response.sendRedirect("Pagina_Principal.jsp?error=permiso");
    return;
  }
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="es_CO"/>
<fmt:setBundle basename="messages_es" var="i18n"/>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title><fmt:message bundle="${i18n}" key="programas.title"/></title>

  <link rel="icon" type="image/png" href="img/logo.png" />
  <!-- Google Fonts: DM Sans (body) + DM Mono (código) -->
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet"/>
  <!-- Material Symbols -->
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet"/>

  <!-- Bootstrap 5.3 CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <!-- DataTables Bootstrap 5 CSS -->
  <link href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css" rel="stylesheet"/>

  <!-- ClassControl CSS -->
  <link rel="stylesheet" href="CSS/ClassControl_base.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_theme.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_sidebar.css" />
  <link rel="stylesheet" href="CSS/ProgramasCSS.css"/>
</head>

<body>
<div class="cc-layout">

  <!-- ---------------------------------------
       SIDEBAR
  ------------------------------------------ -->
  <%   String ccActivePage = "programas"; %>
<%@ include file="_Sidebar.jspf" %>

  <!-- ---------------------------------------
       MAIN
  ------------------------------------------ -->
  <main class="cc-main cc-catalog-main">

    <!-- Toggle sidebar (móvil) -->
    <button class="cc-sidebar-toggle d-lg-none" id="btnSidebarToggle" aria-label="Abrir menú">
      <span class="material-symbols-outlined">menu</span>
    </button>

    <!-- Page header -->
    <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
      <div>
        <h1 class="cc-page-title"><fmt:message bundle="${i18n}" key="programas.header.title"/></h1>
        <p class="cc-page-sub"><fmt:message bundle="${i18n}" key="programas.header.subtitle"/></p>
      </div>
      <div class="d-flex gap-2">
        <button id="dark-toggle" class="btn cc-icon-btn" title="Cambiar tema">
          <span class="material-symbols-outlined">dark_mode</span>
        </button>
        <button id="btn-descargar" class="btn cc-btn-outline">
          <span class="material-symbols-outlined">file_download</span>
          <fmt:message bundle="${i18n}" key="programas.btn.descargarCsv"/>
        </button>
        <% if (sesRol != null && sesRol == 3) { %>
        <button id="btn-nuevo-programa" class="btn cc-btn-primary">
          <span class="material-symbols-outlined">add_circle</span>
          <fmt:message bundle="${i18n}" key="programas.btn.nuevo"/>
        </button>
        <% } %>
      </div>
    </div>

    <!-- Métricas -->
    <div class="row g-3 mb-4" id="metricas"></div>

    <!-- Filtros -->
    <div class="card cc-card mb-4">
      <div class="card-body">
        <form id="form-filtros" novalidate>
          <div class="row g-2 align-items-end">

            <div class="col-12 col-md-8">
              <label class="cc-label" for="filtro-busqueda">Buscar</label>
              <div class="cc-input-icon-wrap">
                <span class="material-symbols-outlined cc-input-icon">search</span>
                <input id="filtro-busqueda" name="busqueda" type="text"
                  placeholder="Nombre, código..."
                  class="form-control cc-input ps-5"/>
              </div>
            </div>

            <div class="col-12 col-md-4 d-flex gap-2">
              <button type="submit" id="btn-filtrar" class="btn cc-btn-dark flex-fill">
                <span class="material-symbols-outlined">filter_list</span><fmt:message bundle="${i18n}" key="common.btn.filtrar"/>
              </button>
              <button type="reset" id="btn-limpiar" class="btn cc-btn-outline px-3" title="<fmt:message bundle="${i18n}" key="common.tooltip.limpiarFiltros"/>">
                <span class="material-symbols-outlined">filter_alt_off</span>
              </button>
            </div>

          </div>
        </form>
      </div>
    </div>

    <!-- Tabla -->
    <div class="card cc-card">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table cc-table align-middle mb-0" id="tabla-programas">
            <thead>
              <tr>
                <th>Código</th>
                <th>Nombre del Programa</th>
                <th class="text-end">Acciones</th>
              </tr>
            </thead>
            <tbody id="tbody-programas"></tbody>
          </table>
        </div>

        <!-- Paginación -->
        <div class="d-flex flex-wrap align-items-center justify-content-between gap-2 px-4 py-3 cc-table-footer">
          <span id="contador-programas" class="text-muted small"></span>
          <div id="paginacion" class="d-flex gap-1"></div>
        </div>
      </div>
    </div>

  </main>
</div><!-- /.cc-layout -->


<!-- ---------------------------------------
     MODAL - Nuevo / Editar Programa
------------------------------------------ -->
<div class="modal fade" id="modal-programa" tabindex="-1" aria-labelledby="modal-titulo" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content cc-modal">
      <div class="modal-header">
        <h5 class="modal-title" id="modal-titulo"><fmt:message bundle="${i18n}" key="programas.btn.nuevo"/></h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" id="btn-cerrar-modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <form id="form-programa" action="RegistrarPrograma" method="POST" novalidate>
          <input type="hidden" id="prog-id" name="id"/>

          <div class="row g-3">
            <div class="col-6">
              <label class="cc-label" for="prog-codigo">Código <span class="text-danger">*</span></label>
              <input id="prog-codigo" name="codigo_programa" type="number" required
                class="form-control cc-input" placeholder="Ej. 228106"/>
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="common.validation.requerido"/></div>
            </div>
            <div class="col-12">
              <label class="cc-label" for="prog-nombre">Nombre del Programa <span class="text-danger">*</span></label>
              <input id="prog-nombre" name="nombre_programa" type="text" required
                class="form-control cc-input" placeholder="Ej. Análisis y Desarrollo de Software"/>
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="common.validation.requerido"/></div>
            </div>
          </div>

        </form>
      </div>
      <div class="modal-footer">
        <button type="button" id="btn-cancelar" class="btn cc-btn-outline" data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cancelar"/></button>
        <button type="submit" form="form-programa" class="btn cc-btn-primary">
          <span class="material-symbols-outlined">save</span><fmt:message bundle="${i18n}" key="common.btn.guardar"/>
        </button>
      </div>
    </div>
  </div>
</div>


<!-- ---------------------------------------
     MODAL - Ver Programa (solo lectura)
------------------------------------------ -->
<div class="modal fade" id="modal-ver-programa" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content cc-modal">
      <div class="modal-header">
        <h5 class="modal-title">Ver Programa</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <div class="row g-3">
          <div class="col-6">
            <label class="cc-label">Código</label>
            <p id="pv-codigo" class="form-control-static fw-semibold mb-0">-</p>
          </div>
          <div class="col-12">
            <label class="cc-label">Nombre del Programa</label>
            <p id="pv-nombre" class="form-control-static fw-semibold mb-0">-</p>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn cc-btn-outline" data-bs-dismiss="modal">Cerrar</button>
      </div>
    </div>
  </div>
</div>


<!-- ---------------------------------------
     MODAL - Confirmar <fmt:message bundle="${i18n}" key="common.btn.eliminar"/>
------------------------------------------ -->
<div class="modal fade" id="modal-eliminar" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-sm">
    <div class="modal-content cc-modal text-center">
      <div class="modal-body p-4">
        <div class="cc-delete-icon mb-3">
          <span class="material-symbols-outlined">delete_forever</span>
        </div>
        <h5 class="fw-bold mb-1"><fmt:message bundle="${i18n}" key="programas.confirm.eliminarTitulo"/></h5>
        <p class="text-muted small mb-4"><fmt:message bundle="${i18n}" key="common.confirm.irreversible"/></p>
        <div class="d-flex justify-content-center gap-2">
          <button id="btn-cancelar-eliminar" class="btn cc-btn-outline" data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cancelar"/></button>
          <button id="btn-confirmar-eliminar" class="btn btn-danger d-flex align-items-center gap-1">
            <span class="material-symbols-outlined" style="font-size:1rem">delete</span><fmt:message bundle="${i18n}" key="common.btn.eliminar"/>
          </button>
        </div>
      </div>
    </div>
  </div>
</div>


<!-- ---------------------------------------
     TOAST
------------------------------------------ -->
<div id="toast" class="cc-toast" role="alert" aria-live="polite">
  <span id="toast-icon" class="material-symbols-outlined">check_circle</span>
  <span id="toast-msg"><fmt:message bundle="${i18n}" key="common.info.operacionExitosa"/></span>
</div>


<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js" defer></script>
<script src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js" defer></script>
<script src="JS/ClassControl_ui.js"></script>
<script>
  // Solo el Administrador (3) gestiona programas; Coordinador solo visualiza.
  window.ccPuedeGestionarProgramas = <%= (sesRol != null && sesRol == 3) %>;
</script>
<script src="JS/ProgramasJS.js"></script>
  <script src="JS/ClassControl_theme.js"></script>
</body>
</html>
