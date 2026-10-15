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

  // Administrador (3), Coordinador (4) y Aprendiz (2) ven Competencias.
  // El Instructor (1) no la necesita.
  if (sesRol == null || sesRol == 1) {
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
  <title><fmt:message bundle="${i18n}" key="competencias.title"/></title>

  <link rel="icon" type="image/png" href="img/logo.png" />
  <!-- Fuentes: DM Sans + DM Mono (idénticas en todas las pantallas) -->
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet"/>
  <!-- Material Symbols -->
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet"/>

  <!-- Bootstrap 5.3 -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <!-- DataTables Bootstrap 5 -->
  <link href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css" rel="stylesheet"/>

  <!-- ClassControl CSS compartido -->
  <link rel="stylesheet" href="CSS/ClassControl_base.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_theme.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_sidebar.css" />
  <link rel="stylesheet" href="CSS/CompetenciasCSS.css"/>
</head>

<body>
<div class="cc-layout">

  <!-- SIDEBAR -->
  <%   String ccActivePage = "competencias"; %>
<%@ include file="_Sidebar.jspf" %>

  <!-- MAIN -->
  <main class="cc-main cc-catalog-main">

    <button class="cc-sidebar-toggle d-lg-none" id="btnSidebarToggle" aria-label="Abrir menú">
      <span class="material-symbols-outlined">menu</span>
    </button>

    <!-- Page header -->
    <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
      <div>
        <h1 class="cc-page-title"><fmt:message bundle="${i18n}" key="competencias.header.title"/></h1>
        <p class="cc-page-sub"><fmt:message bundle="${i18n}" key="competencias.header.subtitle"/></p>
      </div>
      <div class="d-flex gap-2">
        <button id="dark-toggle" class="btn cc-icon-btn" title="Cambiar tema">
          <span class="material-symbols-outlined">dark_mode</span>
        </button>
        <button id="btn-descargar" class="btn cc-btn-outline">
          <span class="material-symbols-outlined">file_download</span><fmt:message bundle="${i18n}" key="competencias.btn.exportarCsv"/>
        </button>
        <button id="btn-nueva-competencia" class="btn cc-btn-primary">
          <span class="material-symbols-outlined">add_circle</span><fmt:message bundle="${i18n}" key="competencias.btn.nueva"/>
        </button>
      </div>
    </div>

    <!-- Métricas -->
    <div class="row g-3 mb-4" id="metricas"></div>

    <!-- Filtros -->
    <div class="card cc-card mb-4">
      <div class="card-body">
        <form id="form-filtros" novalidate>
          <div class="row g-2 align-items-end">
            <div class="col-12 col-md-4">
              <label class="cc-label" for="filtro-busqueda">Buscar</label>
              <div class="cc-input-icon-wrap">
                <span class="material-symbols-outlined cc-input-icon">search</span>
                <input id="filtro-busqueda" name="busqueda" type="text"
                  placeholder="Código o descripción..."
                  class="form-control cc-input ps-5"/>
              </div>
            </div>
            <div class="col-6 col-md-3">
              <label class="cc-label" for="filtro-programacion">Programa</label>
              <select id="filtro-programacion" name="programacion" class="form-select cc-input">
                <option value="">Todas</option>
              </select>
            </div>
            <div class="col-12 col-md-4 d-flex gap-2">
              <button type="submit" class="btn cc-btn-dark flex-fill">
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
    <div class="card cc-card mb-4">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table cc-table align-middle mb-0" id="tabla-competencias">
            <thead>
              <tr>
                <th>Código</th>
                <th>Descripción</th>
                <th>Programa</th>
                <th class="text-end">Acciones</th>
              </tr>
            </thead>
            <tbody id="tbody-competencias"></tbody>
          </table>
        </div>
        <div class="d-flex flex-wrap align-items-center justify-content-between gap-2 px-4 py-3 cc-table-footer">
          <span id="contador-competencias" class="text-muted small"></span>
          <div id="paginacion" class="d-flex gap-1"></div>
        </div>
      </div>
    </div>

    <!-- Panel inferior -->
    <div class="row g-3">
      <div class="col-12 col-md-6">
        <div class="card cc-card h-100">
          <div class="card-body d-flex align-items-center gap-3">
            <span class="material-symbols-outlined cc-info-icon">info</span>
            <p id="resumen-carga" class="mb-0 small text-muted lh-sm"></p>
          </div>
        </div>
      </div>
      <div class="col-12 col-md-6">
        <div class="cc-export-banner">
          <div>
            <div class="fw-bold mb-1">Exportar Listado Académico</div>
            <div class="small" style="color:rgba(255,255,255,.7)">Descarga la sábana de competencias en formato CSV.</div>
          </div>
          <button id="btn-generar-reporte" class="btn cc-btn-export">
            <span class="material-symbols-outlined">download</span>Generar Reporte
          </button>
        </div>
      </div>
    </div>

  </main>
</div>


<!-- MODAL - Nueva / Editar -->
<div class="modal fade" id="modal-competencia" tabindex="-1"
     aria-labelledby="modal-titulo" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content cc-modal">
      <div class="modal-header">
        <h5 class="modal-title" id="modal-titulo"><fmt:message bundle="${i18n}" key="competencias.btn.nueva"/></h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"
                id="btn-cerrar-modal" aria-label="<fmt:message bundle="${i18n}" key="common.btn.cerrar"/>"></button>
      </div>
      <div class="modal-body">
        <form id="form-competencia" action="RegistrarCompetencia" method="POST" novalidate>
          <input type="hidden" id="comp-id" name="id"/>
          <div class="row g-3">
            <div class="col-12">
              <label class="cc-label" for="comp-codigo">Código <span class="text-danger">*</span></label>
              <input id="comp-codigo" name="codigo_Competencias" type="number" required
                class="form-control cc-input" placeholder="Ej. 220501096"/>
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="common.validation.requerido"/></div>
            </div>
            <div class="col-12">
              <label class="cc-label" for="comp-descripcion">Descripción <span class="text-danger">*</span></label>
              <textarea id="comp-descripcion" name="descripcion_Competencias" rows="3" required
                class="form-control cc-input"
                placeholder="Describe la competencia de forma clara y precisa..."></textarea>
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="common.validation.requerido"/></div>
            </div>
            <div class="col-12">
              <label class="cc-label" for="comp-programacion">Programa <span class="text-danger">*</span></label>
              <select id="comp-programacion" name="Programas_idProgramas" required class="form-select cc-input">
                <option value="">Seleccionar…</option>
              </select>
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="competencias.validation.programacion"/></div>
            </div>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" id="btn-cancelar" class="btn cc-btn-outline"
                data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cancelar"/></button>
        <button type="submit" form="form-competencia" class="btn cc-btn-primary">
          <span class="material-symbols-outlined">save</span><fmt:message bundle="${i18n}" key="common.btn.guardar"/>
        </button>
      </div>
    </div>
  </div>
</div>


<!-- MODAL - Ver Detalle -->
<div class="modal fade" id="modal-detalle" tabindex="-1"
     aria-labelledby="detalle-titulo" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content cc-modal">
      <div class="modal-header">
        <h5 class="modal-title" id="detalle-titulo"><fmt:message bundle="${i18n}" key="competencias.detalle.title"/></h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"
                id="btn-cerrar-detalle" aria-label="<fmt:message bundle="${i18n}" key="common.btn.cerrar"/>"></button>
      </div>
      <div class="modal-body" id="detalle-contenido"></div>
      <div class="modal-footer">
        <button type="button" class="btn cc-btn-outline" id="btn-cerrar-detalle2"
                data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cerrar"/></button>
      </div>
    </div>
  </div>
</div>


<!-- MODAL - Confirmar <fmt:message bundle="${i18n}" key="common.btn.eliminar"/> -->
<div class="modal fade" id="modal-eliminar" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-sm">
    <div class="modal-content cc-modal text-center">
      <div class="modal-body p-4">
        <div class="cc-delete-icon mb-3">
          <span class="material-symbols-outlined">delete_forever</span>
        </div>
        <h5 class="fw-bold mb-1"><fmt:message bundle="${i18n}" key="competencias.confirm.eliminarTitulo"/></h5>
        <p class="text-muted small mb-4"><fmt:message bundle="${i18n}" key="common.confirm.irreversible"/></p>
        <div class="d-flex justify-content-center gap-2">
          <button id="btn-cancelar-eliminar" class="btn cc-btn-outline"
                  data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cancelar"/></button>
          <button id="btn-confirmar-eliminar"
                  class="btn btn-danger d-flex align-items-center gap-1">
            <span class="material-symbols-outlined" style="font-size:1rem">delete</span><fmt:message bundle="${i18n}" key="common.btn.eliminar"/>
          </button>
        </div>
      </div>
    </div>
  </div>
</div>


<!-- Toast CSS puro -->
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
  // Solo Administrador (3) gestiona competencias.
  // Coordinador (4), Instructor (1) y Aprendiz (2) entran en modo solo lectura.
  window.ccPuedeGestionarCatalogo = <%= (sesRol != null && sesRol == 3) %>;
</script>
<script src="JS/CompetenciasJS.js"></script>
  <script src="JS/ClassControl_theme.js"></script>
</body>
</html>
