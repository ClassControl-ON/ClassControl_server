<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Protección de sesión --%>
<%
  String sesNombres   = (String)  session.getAttribute("nombres");
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

  boolean esAdminWeb      = (sesRol == 3);
  boolean esInstructorWeb = (sesRol == 1);

  String tituloPagina;
  String subtituloPagina;
  if (esAdminWeb) {
    tituloPagina    = "Incidencias reportadas";
    subtituloPagina = "Gestiona, responde y da seguimiento a los reportes de los instructores.";
  } else if (esInstructorWeb) {
    tituloPagina    = "Mis incidencias";
    subtituloPagina = "Reporta problemas y consulta el estado de tus solicitudes.";
  } else {
    tituloPagina    = "Incidencias del centro";
    subtituloPagina = "Consulta los reportes de los instructores y descárgalos si lo necesitas.";
  }
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Incidencias - ClassControl</title>
  <link rel="icon" type="image/png" href="img/logo.png" />
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous" rel="stylesheet" />
  <link href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css" integrity="sha384-ok3J6xA9oQqai5C9ytYveFsBeKgoGk4T+NExsr6hoIKjZdv9SJcmx2mafwUWRNf9" crossorigin="anonymous" rel="stylesheet" />
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet" />
  <link rel="stylesheet" href="CSS/ClassControl_base.css" />
  <link rel="stylesheet" href="CSS/ClassControl_theme.css" />
  <link rel="stylesheet" href="CSS/ClassControl_sidebar.css" />
  <link rel="stylesheet" href="CSS/IncidenciasCSS.css" />
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
      <h1 class="cc-page-title"><%= tituloPagina %></h1>
      <p class="text-muted mb-0"><%= subtituloPagina %></p>
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

  <!-- Métricas -->
  <div class="row g-3 mb-4" id="inc-metricas"></div>

  <!-- Filtros -->
  <div class="cc-inc-widget-card mb-4">
    <form id="form-filtros" novalidate>
      <div class="row g-3 align-items-end">
        <div class="col-12 col-md-4">
          <div class="input-group cc-search-group">
            <span class="input-group-text bg-transparent border-end-0">
              <span class="material-symbols-outlined cc-icon-sm">search</span>
            </span>
            <label class="visually-hidden" for="filtro-busqueda">Buscar incidencia</label>
            <input id="filtro-busqueda" type="search" class="form-control border-start-0 ps-0"
                   placeholder="Buscar por título, descripción, ambiente o reportante…" autocomplete="off" />
          </div>
        </div>
        <div class="col-6 col-md-2">
          <label class="visually-hidden" for="filtro-estado">Filtrar por estado</label>
          <select id="filtro-estado" class="form-select">
            <option value="">Estado</option>
            <option value="pendiente">Pendiente</option>
            <option value="atendido">Atendido</option>
          </select>
        </div>
        <div class="col-6 col-md-2">
          <label class="visually-hidden" for="filtro-tipo">Filtrar por tipo</label>
          <select id="filtro-tipo" class="form-select"><option value="">Tipo</option></select>
        </div>
        <div class="col-6 col-md-2">
          <label class="visually-hidden" for="filtro-ambiente">Filtrar por ambiente</label>
          <select id="filtro-ambiente" class="form-select"><option value="">Ambiente</option></select>
        </div>
        <div class="col-6 col-md-2 d-flex gap-2 justify-content-md-end">
          <button type="submit" class="btn cc-btn-primary flex-grow-1 flex-md-grow-0">
            <span class="material-symbols-outlined" style="font-size:1rem">filter_list</span>
            Filtrar
          </button>
          <button type="reset" class="btn btn-outline-secondary" title="Limpiar filtros">
            <span class="material-symbols-outlined" style="font-size:1rem">filter_alt_off</span>
          </button>
        </div>
      </div>
    </form>
  </div>

  <!-- Tabla -->
  <div class="cc-inc-table-card">
    <div class="table-responsive">
      <table id="incidencias-table" class="table table-hover align-middle mb-0 w-100">
        <thead>
          <tr>
            <th>Incidencia</th>
            <th>Tipo</th>
            <th>Ambiente</th>
            <th>Reporta</th>
            <th>Fecha</th>
            <th>Estado</th>
            <th>Atendida por</th>
            <th class="text-end">Acciones</th>
          </tr>
        </thead>
        <tbody id="cuerpoTabla">
          <tr><td colspan="8" class="cc-inc-empty">Cargando…</td></tr>
        </tbody>
      </table>
    </div>
  </div>

</main>
</div><!-- /.cc-layout -->

<!-- Modal atender (solo admin) -->
<div class="modal fade" id="modalAtender" tabindex="-1" aria-labelledby="atenderTitulo" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="atenderTitulo">Atender incidencia</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <label class="form-label" for="respuestaAtender">Respuesta del administrador *</label>
        <textarea id="respuestaAtender" class="form-control" rows="4" maxlength="255"
                  placeholder="Ej.: El ambiente fue revisado y liberado."></textarea>
        <div class="cc-inc-contador"><span id="respuestaAtenderContador">0/255</span></div>
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
<div class="modal fade" id="modalNueva" tabindex="-1" aria-labelledby="modalNuevaTitulo" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="modalNuevaTitulo">Reportar incidencia</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label" for="nuevaTitulo">Título *</label>
          <input type="text" id="nuevaTitulo" class="form-control" maxlength="120"
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
          <select id="selAmbiente" class="form-select"><option value="">Ninguno</option></select>
        </div>
        <div class="mb-1">
          <label class="form-label" for="nuevaDescripcion">Descripción *</label>
          <textarea id="nuevaDescripcion" class="form-control" rows="4" maxlength="1000"
                    placeholder="Cuenta qué ocurrió, desde cuándo y qué impacto tiene…"></textarea>
          <div class="cc-inc-contador"><span id="nuevaDescripcionContador">0/1000</span></div>
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
<div class="modal fade" id="modalVerIncidencia" tabindex="-1" aria-labelledby="verIncidenciaTitulo" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="verIncidenciaTitulo">Detalle de la incidencia</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <div class="mb-1 cc-inc-detalle-titulo" id="verTitulo">-</div>
        <div class="mb-3 cc-inc-detalle-meta" id="verMeta">-</div>

        <div class="mb-3">
          <span class="form-label d-block">Descripción</span>
          <div class="cc-inc-descripcion" id="verDescripcion">-</div>
        </div>

        <div>
          <span class="form-label d-block">Seguimiento</span>
          <div class="cc-inc-timeline" id="verTimeline"></div>
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

<script>
  window.ccInc = {
    esAdmin: <%= esAdminWeb %>,
    esInstructor: <%= esInstructorWeb %>
  };
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha384-1H217gwSVyLSIfaLxHbE7dRb3v4mYCKbpQvzx0cegeju1MVsGrX5xXxAvs/HgeFs" crossorigin="anonymous"></script>
<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js" integrity="sha384-Udt767MMeKelGRBxaCfxX88YDLbViYdQ7T/gkRoB197Jf+OviZ+lsaRAOpS/MIjf" crossorigin="anonymous"></script>
<script src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js" integrity="sha384-PgPBH0hy6DTJwu7pTf6bkRqPlf/+pjUBExpr/eIfzszlGYFlF9Wi9VTAJODPhgCO" crossorigin="anonymous"></script>
<script src="JS/ClassControl_ui.js"></script>
<script src="JS/IncidenciasJS.js"></script>
<script src="JS/ClassControl_theme.js"></script>
</body>
</html>
