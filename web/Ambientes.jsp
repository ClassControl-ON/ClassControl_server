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

  // Solo Administrador (3) y Coordinador (4) ven Ambientes.
  if (sesRol == null || (sesRol != 1 && sesRol != 3 && sesRol != 4)) { // 1=Instructor tambien gestiona ambientes (igual que la app)
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
  <title><fmt:message bundle="${i18n}" key="ambientes.title"/></title>

  <link rel="icon" type="image/png" href="img/logo.png" />
  <!-- Fuentes: DM Sans + DM Mono (iguales en todas las pantallas) -->
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet"/>
  <!-- Material Symbols (igual que <fmt:message bundle="${i18n}" key="nav.programas"/> y <fmt:message bundle="${i18n}" key="nav.actividades"/>) -->
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet"/>

  <!-- Bootstrap 5.3 -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <!-- DataTables Bootstrap 5 -->
  <link href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css" rel="stylesheet"/>

  <!-- ClassControl CSS -->
  <link rel="stylesheet" href="CSS/ClassControl_base.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_theme.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_sidebar.css" />
  <link rel="stylesheet" href="CSS/AmbientesCSS.css"/>
</head>

<body>
<div class="cc-layout">

  <!-- ---------------------------------------
       SIDEBAR (idéntico en todas las pantallas)
  ------------------------------------------ -->
  <%   String ccActivePage = "ambientes"; %>
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
        <h1 class="cc-page-title">Gestión de <fmt:message bundle="${i18n}" key="nav.ambientes"/></h1>
        <p class="cc-page-sub"><fmt:message bundle="${i18n}" key="ambientes.header.subtitle"/></p>
      </div>
      <div class="d-flex align-items-center gap-2">
        <button id="dark-toggle" class="btn cc-icon-btn" title="Cambiar tema">
          <span class="material-symbols-outlined">dark_mode</span>
        </button>
        <% if (sesRol != null && sesRol == 3) { %>
        <button id="btn-nuevo" class="btn cc-btn-primary" type="button"
                data-bs-toggle="modal" data-bs-target="#ambienteModal">
          <span class="material-symbols-outlined">add_circle</span>
          <fmt:message bundle="${i18n}" key="ambientes.btn.nuevo"/>
        </button>
        <% } %>
      </div>
    </div>

    <!-- Métricas -->
    <div class="row g-3 mb-4">
      <div class="col-6 col-xl-3">
        <div class="cc-metric" style="border-left-color: var(--cc-primary)">
          <div class="cc-metric-label">Total <fmt:message bundle="${i18n}" key="nav.ambientes"/></div>
          <div class="cc-metric-value" id="stat-total">0</div>
        </div>
      </div>
      <div class="col-6 col-xl-3">
        <div class="cc-metric" style="border-left-color: #3b82f6">
          <div class="cc-metric-label"><fmt:message bundle="${i18n}" key="ambientes.metric.capacidad"/></div>
          <div class="cc-metric-value" id="stat-capacidad">0</div>
        </div>
      </div>
      <div class="col-6 col-xl-3">
        <div class="cc-metric" style="border-left-color: #10b981">
          <div class="cc-metric-label"><fmt:message bundle="${i18n}" key="ambientes.metric.sedes"/></div>
          <div class="cc-metric-value" id="stat-sedes">0</div>
        </div>
      </div>
      <div class="col-6 col-xl-3">
        <div class="cc-metric" style="border-left-color: #ef4444">
          <div class="cc-metric-label"><fmt:message bundle="${i18n}" key="ambientes.metric.promedio"/></div>
          <div class="cc-metric-value" id="stat-promedio">0</div>
        </div>
      </div>
    </div>

    <!-- Tabla + gráfica -->
    <div class="row g-4">

      <!-- Tabla principal -->
      <div class="col-12 col-xxl-8">
        <div class="card cc-card">
          <div class="card-header cc-card-header">
            <div>
              <h2 class="cc-card-title">Listado de <fmt:message bundle="${i18n}" key="nav.ambientes"/></h2>
              <p class="cc-card-sub"><fmt:message bundle="${i18n}" key="ambientes.listado.subtitle"/></p>
            </div>
            <!-- Filtros externos -->
            <form id="form-filtros" class="d-flex flex-wrap gap-2 mt-2 mt-lg-0">
              <div>
                <label class="cc-label" for="filter-sede">Sede</label>
                <select id="filter-sede" class="form-select cc-input cc-input-sm">
                  <option value="">Todas</option>
                </select>
              </div>
              <div>
                <label class="cc-label" for="filter-estado">Estado</label>
                <select id="filter-estado" class="form-select cc-input cc-input-sm">
                  <option value="">Todos</option>
                  <option value="Disponible">Disponibles</option>
                  <option value="Ocupado">Ocupados</option>
                  <option value="Mantenimiento">En mantenimiento</option>
                </select>
              </div>
            </form>
          </div>
          <div class="card-body p-0">
            <div class="table-responsive">
              <table id="tabla-ambientes" class="table cc-table align-middle mb-0">
                <thead>
                  <tr>
                    <th>Ambiente</th>
                    <th>Sede</th>
                    <th class="text-center">Capacidad</th>
                    <th>Estado</th>
                    <th class="text-end">Acciones</th>
                  </tr>
                </thead>
                <tbody></tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <!-- Gráfica donut -->
      <div class="col-12 col-xxl-4">
        <div class="card cc-card h-100">
          <div class="card-header cc-card-header">
            <div>
              <h2 class="cc-card-title"><fmt:message bundle="${i18n}" key="nav.ambientes"/> por Sede</h2>
              <p class="cc-card-sub">Distribución de ambientes registrados por sede.</p>
            </div>
          </div>
          <div class="card-body d-flex align-items-center justify-content-center">
            <canvas id="ambientesChart" style="max-height:300px"
              aria-label="Gráfica de ocupación de ambientes" role="img"></canvas>
          </div>
        </div>
      </div>

    </div>
  </main>
</div><!-- /.cc-layout -->


<!-- ---------------------------------------
     MODAL - Nuevo / Editar Ambiente
------------------------------------------ -->
<div class="modal fade" id="ambienteModal" tabindex="-1"
     aria-labelledby="ambienteModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
    <div class="modal-content cc-modal">
      <form id="form-ambiente" action="RegistrarAmbiente" method="POST" novalidate>
        <div class="modal-header">
          <h5 class="modal-title" id="ambienteModalLabel"><fmt:message bundle="${i18n}" key="ambientes.btn.nuevo"/></h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
        </div>
        <div class="modal-body">
          <input id="amb-id" type="hidden"/>
          <div class="row g-3">
            <div class="col-12 col-md-6">
              <label class="cc-label" for="amb-nombre">Nombre / Código <span class="text-danger">*</span></label>
              <input id="amb-nombre" name="descripcion_Ambiente" class="form-control cc-input" type="text"
                     placeholder="Ej. Lab 204" required/>
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="ambientes.validation.nombre"/></div>
            </div>
            <div class="col-12 col-md-6">
              <label class="cc-label" for="amb-sede">Sede <span class="text-danger">*</span></label>
              <select id="amb-sede" name="Sede_id_sede" class="form-select cc-input" required>
                <option value="">Seleccionar…</option>
              </select>
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="ambientes.validation.sede"/></div>
            </div>
            <div class="col-12 col-md-6">
              <label class="cc-label" for="amb-capacidad">Capacidad <span class="text-danger">*</span></label>
              <input id="amb-capacidad" name="capacidad" class="form-control cc-input" type="number"
                     min="1" max="500" placeholder="Ej. 30" required/>
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="ambientes.validation.capacidad"/></div>
            </div>
            <div class="col-12 col-md-6">
              <label class="cc-label" for="amb-estado">Estado administrativo</label>
              <select id="amb-estado" name="estado_Ambiente" class="form-select cc-input">
                <option value="Disponible">Disponible</option>
                <option value="Mantenimiento">Mantenimiento</option>
                <option value="Inhabilitado">Inhabilitado</option>
              </select>
              <div class="form-text">&ldquo;Ocupado&rdquo; se calcula solo seg&uacute;n las clases programadas.</div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn cc-btn-outline" data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cancelar"/></button>
          <button type="submit" class="btn cc-btn-primary">
            <span class="material-symbols-outlined">save</span><fmt:message bundle="${i18n}" key="ambientes.btn.guardar"/>
          </button>
        </div>
      </form>
    </div>
  </div>
</div>


<!-- ---------------------------------------
     MODAL - Confirmar <fmt:message bundle="${i18n}" key="common.btn.eliminar"/>
------------------------------------------ -->
<div class="modal fade" id="deleteModal" tabindex="-1"
     aria-labelledby="deleteModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-sm">
    <div class="modal-content cc-modal text-center">
      <div class="modal-body p-4">
        <div class="cc-delete-icon mb-3">
          <span class="material-symbols-outlined">delete_forever</span>
        </div>
        <h5 class="fw-bold mb-1"><fmt:message bundle="${i18n}" key="ambientes.confirm.eliminarTitulo"/></h5>
        <p id="delete-msg" class="text-muted small mb-4"><fmt:message bundle="${i18n}" key="common.confirm.irreversible"/></p>
        <div class="d-flex justify-content-center gap-2">
          <button type="button" class="btn cc-btn-outline" data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cancelar"/></button>
          <button id="btn-confirmar-delete" type="button" class="btn btn-danger d-flex align-items-center gap-1">
            <span class="material-symbols-outlined" style="font-size:1rem">delete</span><fmt:message bundle="${i18n}" key="common.btn.eliminar"/>
          </button>
        </div>
      </div>
    </div>
  </div>
</div>


<!-- ---------------------------------------
     MODAL - Ver Ambiente (solo lectura)
----------------------------------------- -->
<div class="modal fade" id="ambienteViewModal" tabindex="-1"
     aria-labelledby="ambienteViewModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
    <div class="modal-content cc-modal">
      <div class="modal-header">
        <h5 class="modal-title" id="ambienteViewModalLabel">Ver Ambiente</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <div class="row g-3">
          <div class="col-12 col-md-6">
            <label class="cc-label">Nombre / Código</label>
            <p id="view-nombre" class="form-control-static fw-semibold mb-0">-</p>
          </div>
          <div class="col-12 col-md-6">
            <label class="cc-label">Sede</label>
            <p id="view-sede" class="form-control-static fw-semibold mb-0">-</p>
          </div>
          <div class="col-12 col-md-6">
            <label class="cc-label">Capacidad</label>
            <p id="view-capacidad" class="form-control-static fw-semibold mb-0">-</p>
          </div>
          <div class="col-12 col-md-6">
            <label class="cc-label">Estado</label>
            <p id="view-estado" class="form-control-static fw-semibold mb-0">-</p>
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
     TOAST
------------------------------------------ -->
<div id="toast" class="cc-toast" role="alert" aria-live="polite">
  <span id="toast-icon" class="material-symbols-outlined">check_circle</span>
  <span id="toast-msg"><fmt:message bundle="${i18n}" key="common.info.operacionExitosa"/></span>
</div>


<!-- Scripts -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js"></script>
<script src="JS/ClassControl_ui.js"></script>
<script>
  // Solo el Administrador (3) gestiona ambientes; el resto solo visualiza.
  window.ccPuedeGestionarAmbientes = <%= (sesRol != null && sesRol == 3) %>;
</script>
<script src="JS/AmbientesJS.js"></script>
  <script src="JS/ClassControl_theme.js"></script>
</body>
</html>
