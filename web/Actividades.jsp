<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Gestión de Actividades - ClassControl</title>

  <link rel="icon" type="image/png" href="img/logo.png" />
  <!-- Google Fonts: DM Sans (body) + DM Mono (código) -->
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet"/>
  <!-- Material Symbols -->
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet"/>

  <!-- Bootstrap 5.3 CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <!-- DataTables Bootstrap 5 CSS -->
  <link href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css" rel="stylesheet"/>

  <!-- ClassControl CSS (sistema compartido) -->
  <link rel="stylesheet" href="CSS/ClassControl_base.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_theme.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_sidebar.css" />
  <link rel="stylesheet" href="CSS/ActividadesCSS.css"/>
</head>

<body>
<div class="cc-layout">

  <!-- ----
       SIDEBAR
  ---- -->
  <%   String ccActivePage = "actividades"; %>
<%@ include file="_Sidebar.jspf" %>

  <!-- ----
       MAIN
  ---- -->
  <main class="cc-main cc-catalog-main">

    <!-- Toggle sidebar (móvil) -->
    <button class="cc-sidebar-toggle d-lg-none" id="btnSidebarToggle" aria-label="Abrir menú">
      <span class="material-symbols-outlined">menu</span>
    </button>

    <!-- Page header -->
    <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
      <div>
        <h1 class="cc-page-title">Gestión de Actividades</h1>
        <p class="cc-page-sub">Panel de control y seguimiento de la formación profesional integral.</p>
      </div>
      <div class="d-flex align-items-center gap-2">
        <button id="dark-toggle" class="btn cc-icon-btn" title="Cambiar tema">
          <span class="material-symbols-outlined">dark_mode</span>
        </button>
        <button id="btn-nueva-actividad" class="btn cc-btn-primary">
          <span class="material-symbols-outlined">add_circle</span>
          Nueva Actividad
        </button>
      </div>
    </div>

    <!-- Filtros -->
    <div class="card cc-card mb-4">
      <div class="card-body">
        <form id="form-filtros" novalidate>
          <div class="row g-2 align-items-end">

            <!-- Búsqueda -->
            <div class="col-12 col-md-4">
              <label class="cc-label" for="filtro-busqueda">Buscar</label>
              <div class="cc-input-icon-wrap">
                <span class="material-symbols-outlined cc-input-icon">search</span>
                <input id="filtro-busqueda" name="busqueda" type="text"
                  placeholder="Actividad, código o descripción..."
                  class="form-control cc-input ps-5"/>
              </div>
            </div>

            <!-- Resultado de aprendizaje -->
            <div class="col-6 col-md-3">
              <label class="cc-label" for="filtro-ficha">Resultado de aprendizaje</label>
              <select id="filtro-ficha" name="ficha" class="form-select cc-input">
                <option value="">Todos</option>
              </select>
            </div>

            <!-- Acciones filtro -->
            <div class="col-6 col-md-3 d-flex gap-2">
              <button type="submit" id="btn-filtrar" class="btn cc-btn-dark flex-fill">
                <span class="material-symbols-outlined">filter_list</span>Filtrar
              </button>
              <button type="reset" id="btn-limpiar" class="btn cc-btn-outline px-3" title="Limpiar filtros">
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
          <table class="table cc-table align-middle mb-0" id="tabla-actividades">
            <thead>
              <tr>
                <th>Código</th>
                <th>Actividad</th>
                <th>Descripción</th>
                <th>Resultado de aprendizaje</th>
                <th class="text-end">Acciones</th>
              </tr>
            </thead>
            <tbody id="tbody-actividades"></tbody>
          </table>
        </div>

        <!-- Paginación -->
        <div class="d-flex flex-wrap align-items-center justify-content-between gap-2 px-4 py-3 cc-table-footer">
          <span id="contador-actividades" class="text-muted small"></span>
          <div id="paginacion" class="d-flex gap-1"></div>
        </div>
      </div>
    </div>

  </main>
</div><!-- /.cc-layout -->


<!-- ----
     MODAL - Nueva / Editar Actividad
---- -->
<div class="modal fade" id="modal-actividad" tabindex="-1" aria-labelledby="modal-titulo" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content cc-modal">
      <div class="modal-header">
        <h5 class="modal-title" id="modal-titulo">Nueva Actividad</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" id="btn-cerrar-modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <form id="form-actividad" action="RegistrarActividad" method="POST" novalidate>
          <input type="hidden" id="actividad-id" name="id"/>

          <div class="row g-3">

            <!-- Código -->
            <div class="col-4">
              <label class="cc-label" for="act-codigo">Código <span class="text-danger">*</span></label>
              <input id="act-codigo" name="codigo_Actividad" type="number" required
                class="form-control cc-input" placeholder="Ej. 1"/>
              <div class="invalid-feedback">Este campo es obligatorio.</div>
            </div>

            <!-- Nombre -->
            <div class="col-8">
              <label class="cc-label" for="act-nombre">Nombre <span class="text-danger">*</span></label>
              <input id="act-nombre" name="nombre_Act" type="text" required
                class="form-control cc-input"
                placeholder="Ej. Taller de Lógica de Programación"/>
              <div class="invalid-feedback">Este campo es obligatorio.</div>
            </div>

            <!-- Descripción -->
            <div class="col-12">
              <label class="cc-label" for="act-descripcion">Descripción</label>
              <textarea id="act-descripcion" name="descripcion" rows="2"
                class="form-control cc-input"
                placeholder="Descripción breve de la actividad..."></textarea>
            </div>

            <!-- Resultado de aprendizaje -->
            <div class="col-12">
              <label class="cc-label" for="act-ficha">Resultado de aprendizaje <span class="text-danger">*</span></label>
              <select id="act-ficha" name="Resultado_aprendizaje_id_resultado_aprendizaje" required class="form-select cc-input">
                <option value="">Seleccionar…</option>
              </select>
              <div class="invalid-feedback">Selecciona un resultado de aprendizaje.</div>
            </div>

          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" id="btn-cancelar" class="btn cc-btn-outline" data-bs-dismiss="modal">Cancelar</button>
        <button type="submit" form="form-actividad" class="btn cc-btn-primary">
          <span class="material-symbols-outlined">save</span>Guardar
        </button>
      </div>
    </div>
  </div>
</div>


<!-- ----
     MODAL - Ver Detalle Actividad
---- -->
<div class="modal fade" id="modal-detalle" tabindex="-1" aria-labelledby="detalle-titulo" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content cc-modal">
      <div class="modal-header">
        <h5 class="modal-title" id="detalle-titulo">Detalle de Actividad</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body" id="detalle-body">
        <!-- Contenido dinámico -->
      </div>
      <div class="modal-footer">
        <button type="button" class="btn cc-btn-outline" data-bs-dismiss="modal">Cerrar</button>
      </div>
    </div>
  </div>
</div>


<!-- ----
     MODAL - Confirmar Eliminar
---- -->
<div class="modal fade" id="modal-eliminar" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-sm">
    <div class="modal-content cc-modal text-center">
      <div class="modal-body p-4">
        <div class="cc-delete-icon mb-3">
          <span class="material-symbols-outlined">delete_forever</span>
        </div>
        <h5 class="fw-bold mb-1">¿Eliminar actividad?</h5>
        <p class="text-muted small mb-4">Esta acción no se puede deshacer.</p>
        <div class="d-flex justify-content-center gap-2">
          <button id="btn-cancelar-eliminar" class="btn cc-btn-outline" data-bs-dismiss="modal">Cancelar</button>
          <button id="btn-confirmar-eliminar" class="btn btn-danger d-flex align-items-center gap-1">
            <span class="material-symbols-outlined" style="font-size:1rem">delete</span>Eliminar
          </button>
        </div>
      </div>
    </div>
  </div>
</div>


<!-- ----
     TOAST
---- -->
<div id="toast" class="cc-toast" role="alert" aria-live="polite">
  <span id="toast-icon" class="material-symbols-outlined">check_circle</span>
  <span id="toast-msg">Operación exitosa</span>
</div>


<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js" defer></script>
<script src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js" defer></script>
<script src="JS/ClassControl_ui.js"></script>
<script>
  // Solo Administrador (3) gestiona actividades.
  // Coordinador (4), Instructor (1) y Aprendiz (2) entran en modo solo lectura.
  window.ccPuedeGestionarCatalogo = <%= (sesRol != null && sesRol == 3) %>;
</script>
<script src="JS/ActividadesJS.js"></script>
  <script src="JS/ClassControl_theme.js"></script>
</body>
</html>
