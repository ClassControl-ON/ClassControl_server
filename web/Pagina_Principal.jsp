<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!
  /** Escapa texto para que los datos dinamicos no se interpreten como HTML. */
  public static String escaparHtml(Object valor) {
    if (valor == null) return "";
    String texto = String.valueOf(valor);
    return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
  }
%>
<!DOCTYPE html>
<html lang="es" data-bs-theme="light">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>ClassControl - Inicio</title>

  <link rel="icon" type="image/png" href="img/logo.png" />
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet" />
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet" />
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
  <link href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="CSS/ClassControl_base.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_theme.css" />
  <link rel="stylesheet" href="CSS/ClassControl_sidebar.css" />
  <link rel="stylesheet" href="CSS/Pagina_PrincipalCSS.css" />
</head>

<body>
<%-- ---------------------- PROTECCIÓN DE SESIÓN ---------------------- --%>
<%
  /* --- Leer sesión --- */
  String sesNombres   = (String)  session.getAttribute("nombres");
  String sesApellidos = (String)  session.getAttribute("apellidos");
  String sesUsername  = (String)  session.getAttribute("username");
  Integer sesRol      = (Integer) session.getAttribute("rol");
  Integer sesIdUser   = (Integer) session.getAttribute("id_usuario");

  /* --- Redirigir si no hay sesión --- */
  if (sesNombres == null) {
    response.sendRedirect("Inicio_de_sesion.jsp");
    return;
  }

  /* --- Etiqueta de rol --- */
  String rolLabel = "Usuario";
  if (sesRol != null) {
    if (sesRol == 1) rolLabel = "Instructor";
    else if (sesRol == 2) rolLabel = "Aprendiz";
    else if (sesRol == 3) rolLabel = "Administrador";
    else if (sesRol == 4) rolLabel = "Coordinador";
  }

  /* Los datos del dashboard (contadores, próximas actividades, estado de
     programas) y las opciones de los <select> ya NO se calculan aquí con
     SQL directo: se piden por fetch() desde Pagina_PrincipalJS.js a
     ConsultarDashboard y a los servlets ConsultarProgramas, ConsultarJornadas,
     ConsultarModalidades, ConsultarNiveles, ConsultarSedes, ConsultarEtapas,
     ConsultarResultados, ConsultarCompetencias, ConsultarProgramaciones,
     ConsultarUsuarios, ConsultarAmbientes, ConsultarActividades,
     ConsultarTrimestres, ConsultarEstados y ConsultarFichas - igual que
     hacen el resto de las vistas del proyecto. */

  /* Mensajes flash (tras redirect de modales) */
  String flashOk  = request.getParameter("ok");
  String flashErr = request.getParameter("error");
%>

<div class="cc-wrapper">

  <!-- -------------- SIDEBAR -------------- -->
  <%   String ccActivePage = "inicio"; %>
<%@ include file="_Sidebar.jspf" %>

  <!-- -------------- MAIN -------------- -->
  <main class="cc-main">

    <!-- HEADER -->
    <header class="cc-header">
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-sm cc-hamburger d-lg-none me-1" id="sidebar-toggle" aria-label="Menú">
          <span class="material-symbols-outlined">menu</span>
        </button>
        <h2 class="cc-page-title mb-0">Dashboard Principal</h2>
      </div>

      <div class="cc-header-right">
        <div class="input-group cc-search-group">
          <span class="input-group-text bg-transparent border-end-0">
            <span class="material-symbols-outlined cc-icon-sm">search</span>
          </span>
          <input id="search-input" type="search"
                 class="form-control border-start-0 ps-0"
                 placeholder="Buscar actividad, ficha…"
                 autocomplete="off" />
        </div>

        <button id="dark-toggle" class="btn cc-icon-btn" title="Cambiar tema">
          <span class="material-symbols-outlined">dark_mode</span>
        </button>

      </div>
    </header>

    <!-- DASHBOARD BODY -->
    <div class="cc-body">

      <%-- Mensajes flash tras guardar desde modal --%>
      <% if ("ficha".equals(flashOk)) { %>
        <div class="alert alert-success alert-dismissible fade show mb-3" role="alert">
          <span class="material-symbols-outlined me-1" style="font-size:1rem;vertical-align:middle">check_circle</span>
          Ficha registrada exitosamente.
          <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
      <% } else if ("actividad".equals(flashOk)) { %>
        <div class="alert alert-success alert-dismissible fade show mb-3" role="alert">
          <span class="material-symbols-outlined me-1" style="font-size:1rem;vertical-align:middle">check_circle</span>
          Actividad registrada exitosamente.
          <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
      <% } else if ("ambiente".equals(flashOk)) { %>
        <div class="alert alert-success alert-dismissible fade show mb-3" role="alert">
          <span class="material-symbols-outlined me-1" style="font-size:1rem;vertical-align:middle">check_circle</span>
          Ambiente registrado exitosamente.
          <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
      <% } else if (flashOk != null) { %>
        <div class="alert alert-success alert-dismissible fade show mb-3" role="alert">
          <span class="material-symbols-outlined me-1" style="font-size:1rem;vertical-align:middle">check_circle</span>
          Registro guardado exitosamente.
          <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
      <% } else if ("conflicto".equals(flashErr)) { %>
        <div class="alert alert-danger alert-dismissible fade show mb-3" role="alert">
          <span class="material-symbols-outlined me-1" style="font-size:1rem;vertical-align:middle">event_busy</span>
          Ya existe una programación para ese ambiente, día y hora. Elige otro horario o ambiente.
          <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
      <% } else if (flashErr != null) { %>
        <div class="alert alert-danger alert-dismissible fade show mb-3" role="alert">
          <span class="material-symbols-outlined me-1" style="font-size:1rem;vertical-align:middle">error</span>
          Ocurrió un error al guardar. Por favor intenta de nuevo.
          <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
      <% } %>

      <!-- Action button (solo Administrador) -->
      <% if (sesRol != null && sesRol == 3) { %>
      <div class="cc-create-wrap mb-4">
        <button class="btn cc-btn-primary" data-bs-toggle="modal" data-bs-target="#modal-crear-registro">
          <span class="material-symbols-outlined">add_circle</span> Crear registro
        </button>
      </div>
      <% } %>

      <!-- STATS GRID -->
      <div class="row g-3 mb-4">
        <div class="col-6 col-xl-3">
          <div class="cc-stat-card">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <div class="cc-stat-icon green"><span class="material-symbols-outlined">description</span></div>
              <span class="cc-badge green">Activas</span>
            </div>
            <p class="cc-stat-label">Fichas activas</p>
            <h3 class="cc-stat-value" id="stat-fichas-activas">00</h3>
          </div>
        </div>
        <div class="col-6 col-xl-3">
          <div class="cc-stat-card">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <div class="cc-stat-icon blue"><span class="material-symbols-outlined">meeting_room</span></div>
              <span class="cc-badge blue">Hoy</span>
            </div>
            <p class="cc-stat-label">Ambientes ocupados hoy</p>
            <h3 class="cc-stat-value" id="stat-ambientes-hoy">00</h3>
          </div>
        </div>
        <div class="col-6 col-xl-3">
          <div class="cc-stat-card">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <div class="cc-stat-icon orange"><span class="material-symbols-outlined">assignment_turned_in</span></div>
              <span class="cc-badge orange">En progreso</span>
            </div>
            <p class="cc-stat-label">Actividades en curso</p>
            <h3 class="cc-stat-value" id="stat-actividades-curso">00</h3>
          </div>
        </div>
        <div class="col-6 col-xl-3">
          <div class="cc-stat-card">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <div class="cc-stat-icon purple"><span class="material-symbols-outlined">person_check</span></div>
              <span class="cc-badge purple">Activos</span>
            </div>
            <p class="cc-stat-label">Instructores activos</p>
            <h3 class="cc-stat-value" id="stat-instructores">00</h3>
          </div>
        </div>
      </div>

      <!-- WIDGETS ROW -->
      <div class="row g-3 mb-4">

        <!-- Ocupación de Ambientes (datos reales desde ConsultarProgramaciones) -->
        <div class="col-lg-8">
          <div class="cc-widget-card h-100">
            <div class="cc-widget-header">
              <h4 class="cc-widget-title">Ocupación de Ambientes</h4>
              <select id="chart-select" class="form-select form-select-sm cc-widget-select">
                <option>Esta semana</option>
                <option>Hoy</option>
              </select>
            </div>
            <canvas id="ocupacionChart" height="110" style="height:280px;max-height:340px;position:relative;"></canvas>
            <p id="ocupacion-vacio" class="text-muted d-none" style="font-size:.8rem;text-align:center;padding:1.5rem 0;">
              No hay ambientes ocupados durante este período.
            </p>
          </div>
        </div>

        <!-- Estado de Programas (dinámico desde BD) -->
        <div class="col-lg-4">
          <div class="cc-widget-card h-100 d-flex flex-column">
            <div class="cc-widget-header">
              <h4 class="cc-widget-title">Estado de Programas</h4>
            </div>
            <div class="flex-grow-1" id="estado-programas-container">
              <p class="text-muted" style="font-size:.8rem">Cargando programas…</p>
              <%
                /* Plantilla que Pagina_PrincipalJS.js clona para cada programa */
              %>
              <template id="tpl-estado-programa">
              <div class="mb-3">
                <div class="d-flex justify-content-between mb-1">
                  <small class="fw-semibold" data-field="nombre"></small>
                  <small class="fw-semibold" data-field="pct-label"></small>
                </div>
                <div class="progress cc-progress">
                  <div class="progress-bar" data-field="barra" style="width:0%"></div>
                </div>
              </div>
              </template>
            </div>
            <div class="mt-3">
              <a href="Incidencias.jsp" class="btn btn-outline-secondary w-100 btn-sm">Ver incidencias</a>
            </div>
          </div>
        </div>
      </div>

      <!-- DATATABLE: Próximas Actividades -->
      <div class="cc-table-card">
        <div class="cc-table-header">
          <h4 class="cc-widget-title mb-0">Próximas Actividades</h4>
          <button class="btn btn-link cc-link-btn p-0">Ver todas</button>
        </div>
        <div class="table-responsive">
          <table id="actividades-table" class="table table-hover align-middle mb-0 w-100">
            <thead>
              <tr>
                <th>Ficha / Programa</th>
                <th>Actividad</th>
                <th>Ambiente</th>
                <th>Horario</th>
                <th>Instructor</th>
                <th class="text-end">Acción</th>
              </tr>
            </thead>
            <tbody id="tbody-actividades"></tbody>
          </table>
        </div>
      </div>

    </div><!-- /cc-body -->
  </main>
</div><!-- /cc-wrapper -->

<!-- Toast container -->
<div id="toast-container" aria-live="polite" aria-atomic="true"></div>

<div class="d-none" id="catalogo-option-sources" aria-hidden="true">
  <select data-options="programas"></select>
  <select data-options="competencias"></select>
  <select data-options="usuarios"></select>
  <select data-options="fichas"></select>
  <select data-options="ambientes"></select>
  <select data-options="actividades"></select>
  <select data-options="trimestres"></select>
  <select data-options="estados"></select>
  <select data-options="tipoEstado"></select>
</div>
<!-- MODAL: Elegir registro a crear -->
<div class="modal fade" id="modal-crear-registro" tabindex="-1"
     aria-labelledby="title-crear-registro" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <div>
          <h5 class="modal-title" id="title-crear-registro">Crear registro</h5>
          <p class="cc-modal-subtitle mb-0">Elige exactamente que tabla quieres insertar.</p>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <div class="input-group cc-create-search mb-3">
          <span class="input-group-text bg-transparent border-end-0">
            <span class="material-symbols-outlined cc-icon-sm">search</span>
          </span>
          <input id="create-search" type="search" class="form-control border-start-0 ps-0"
                 placeholder="Buscar tabla..." autocomplete="off" />
        </div>

        <div class="cc-create-list">
          <button type="button" class="cc-create-item" data-bs-target="#modal-nueva-ficha">
            <span class="cc-create-icon green material-symbols-outlined">description</span>
            <span><strong>Ficha</strong><small>Codigo, programa, jornada, modalidad, sede y fechas.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-bs-target="#modal-nueva-actividad">
            <span class="cc-create-icon orange material-symbols-outlined">assignment_turned_in</span>
            <span><strong>Actividad</strong><small>Codigo, nombre, descripcion y resultado.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-bs-target="#modal-nuevo-ambiente">
            <span class="cc-create-icon blue material-symbols-outlined">meeting_room</span>
            <span><strong>Ambiente</strong><small>Descripcion, capacidad y sede.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="programa">
            <span class="cc-create-icon green material-symbols-outlined">school</span>
            <span><strong>Programa</strong><small>Codigo y nombre del programa.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="sede">
            <span class="cc-create-icon blue material-symbols-outlined">location_city</span>
            <span><strong>Sede</strong><small>Nombre de la sede.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="jornada">
            <span class="cc-create-icon purple material-symbols-outlined">schedule</span>
            <span><strong>Jornada</strong><small>Descripcion de la jornada.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="modalidad">
            <span class="cc-create-icon orange material-symbols-outlined">cast_for_education</span>
            <span><strong>Modalidad</strong><small>Presencial, virtual u otra modalidad.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="nivel">
            <span class="cc-create-icon green material-symbols-outlined">workspace_premium</span>
            <span><strong>Nivel de formacion</strong><small>Tecnico, tecnologo, especializacion...</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="etapa">
            <span class="cc-create-icon blue material-symbols-outlined">timeline</span>
            <span><strong>Etapa</strong><small>Lectiva, productiva u otra etapa.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="estado">
            <span class="cc-create-icon purple material-symbols-outlined">toggle_on</span>
            <span><strong>Estado</strong><small>Activo, inactivo, en proceso...</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="trimestre">
            <span class="cc-create-icon orange material-symbols-outlined">calendar_month</span>
            <span><strong>Trimestre</strong><small>Numero, descripcion y rango de fechas.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="competencia">
            <span class="cc-create-icon green material-symbols-outlined">target</span>
            <span><strong>Competencia</strong><small>Codigo, descripcion y programacion relacionada.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="resultado">
            <span class="cc-create-icon blue material-symbols-outlined">fact_check</span>
            <span><strong>Resultado de aprendizaje</strong><small>Codigo, descripcion y competencia.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="tipoDocumento">
            <span class="cc-create-icon purple material-symbols-outlined">badge</span>
            <span><strong>Tipo de documento</strong><small>Descripcion del tipo de documento.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="tipoVinculacion">
            <span class="cc-create-icon orange material-symbols-outlined">link</span>
            <span><strong>Tipo de vinculacion</strong><small>Descripcion de vinculacion.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="rol">
            <span class="cc-create-icon green material-symbols-outlined">admin_panel_settings</span>
            <span><strong>Rol</strong><small>Descripcion del rol.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="vinculacionLaboral">
            <span class="cc-create-icon blue material-symbols-outlined">contract</span>
            <span><strong>Vinculacion laboral</strong><small>Contrato, fechas y usuario asociado.</small></span>
          </button>
          <button type="button" class="cc-create-item" data-catalogo="programacion">
            <span class="cc-create-icon purple material-symbols-outlined">event_available</span>
            <span><strong>Programacion de instructores</strong><small>Ficha, instructor, ambiente, actividad, trimestre y horario.</small></span>
          </button>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- MODAL: Catalogos y tablas complementarias -->
<div class="modal fade" id="modal-crear-catalogo" tabindex="-1"
     aria-labelledby="title-crear-catalogo" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="title-crear-catalogo">Nuevo registro</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <form id="form-crear-catalogo" action="" method="POST" novalidate>
        <div class="modal-body">
          <div id="catalogo-fields" class="row g-3"></div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
          <button type="submit" class="btn cc-btn-primary">
            <span class="material-symbols-outlined">save</span> Guardar
          </button>
        </div>
      </form>
    </div>
  </div>
</div>


<!-- ---------------------- MODAL: Nueva Ficha ---------------------- -->
<div class="modal fade" id="modal-nueva-ficha" tabindex="-1"
     aria-labelledby="title-nueva-ficha" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="title-nueva-ficha">Nueva Ficha</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <form id="form-nueva-ficha" action="RegistrarFicha" method="POST" novalidate>
        <div class="modal-body">
          <div class="row g-3">

            <div class="col-6">
              <label for="ficha-numero" class="form-label">Número de ficha *</label>
              <input id="ficha-numero" name="codigo_ficha" class="form-control" type="number"
                     min="1000000" max="9999999" placeholder="ej. 2670687" required />
              <div class="invalid-feedback">Ingresa un número de ficha válido</div>
            </div>

            <div class="col-6">
              <label for="ficha-programa" class="form-label">Programa *</label>
              <select id="ficha-programa" name="Programas_idProgramas" class="form-select" required>
                <option value="">Seleccionar...</option>
                              </select>
              <div class="invalid-feedback">Selecciona un programa</div>
            </div>

            <div class="col-6">
              <label for="ficha-jornada" class="form-label">Jornada *</label>
              <select id="ficha-jornada" name="Jornada_id_jornada" class="form-select" required>
                <option value="">Seleccionar...</option>
                              </select>
              <div class="invalid-feedback">Selecciona una jornada</div>
            </div>

            <div class="col-6">
              <label for="ficha-modalidad" class="form-label">Modalidad *</label>
              <select id="ficha-modalidad" name="Modalidad_id_modalidad" class="form-select" required>
                <option value="">Seleccionar...</option>
                              </select>
              <div class="invalid-feedback">Selecciona una modalidad</div>
            </div>

            <div class="col-6">
              <label for="ficha-nivel" class="form-label">Nivel de Formación *</label>
              <select id="ficha-nivel" name="Nivel_formacion_id_nivel_formacion" class="form-select" required>
                <option value="">Seleccionar...</option>
                              </select>
              <div class="invalid-feedback">Selecciona un nivel</div>
            </div>

            <div class="col-6">
              <label for="ficha-sede" class="form-label">Sede *</label>
              <select id="ficha-sede" name="Sede_id_sede" class="form-select" required>
                <option value="">Seleccionar...</option>
                              </select>
              <div class="invalid-feedback">Selecciona una sede</div>
            </div>

            <div class="col-6">
              <label for="ficha-etapa" class="form-label">Etapa *</label>
              <select id="ficha-etapa" name="Etapa_id_etapa" class="form-select" required>
                <option value="">Seleccionar...</option>
                              </select>
              <div class="invalid-feedback">Selecciona una etapa</div>
            </div>

            <div class="col-6">
              <label for="ficha-cantidad" class="form-label">Cantidad de Aprendices</label>
              <input id="ficha-cantidad" name="cantidad_aprendices" class="form-control"
                     type="number" min="1" placeholder="ej. 28" />
            </div>

            <div class="col-6">
              <label for="ficha-inicio" class="form-label">Fecha de inicio *</label>
              <input id="ficha-inicio" name="fecha_inicio" class="form-control" type="date" required />
              <div class="invalid-feedback">Ingresa la fecha de inicio</div>
            </div>

            <div class="col-6">
              <label for="ficha-fin" class="form-label">Fecha de fin *</label>
              <input id="ficha-fin" name="fecha_fin" class="form-control" type="date" required />
              <div class="invalid-feedback">Ingresa la fecha de fin</div>
            </div>

            <%-- Estado por defecto = 1 (Activo) --%>
            <input type="hidden" name="Estado_id_estado" value="1" />

          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
          <button type="submit" class="btn cc-btn-primary">
            <span class="material-symbols-outlined">save</span> Guardar ficha
          </button>
        </div>
      </form>
    </div>
  </div>
</div>


<!-- ---------------------- MODAL: Nueva Actividad ---------------------- -->
<div class="modal fade" id="modal-nueva-actividad" tabindex="-1"
     aria-labelledby="title-nueva-actividad" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="title-nueva-actividad">Nueva Actividad</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <form id="form-nueva-actividad" action="RegistrarActividad" method="POST" novalidate>
        <div class="modal-body">
          <div class="row g-3">

            <div class="col-12">
              <label for="act-nombre" class="form-label">Nombre de la actividad *</label>
              <input id="act-nombre" name="nombre_Act" class="form-control" type="text"
                     placeholder="ej. Codificación Frontend" required />
              <div class="invalid-feedback">Ingresa el nombre de la actividad</div>
            </div>

            <div class="col-6">
              <label for="act-codigo" class="form-label">Código *</label>
              <input id="act-codigo" name="codigo_Actividad" class="form-control" type="number"
                     placeholder="Código numérico" required />
              <div class="invalid-feedback">Ingresa el código</div>
            </div>

            <div class="col-6">
              <label for="act-resultado" class="form-label">Resultado de Aprendizaje *</label>
              <select id="act-resultado" name="Resultado_aprendizaje_id_resultado_aprendizaje" class="form-select" required>
                <option value="">Seleccionar...</option>
                              </select>
              <div class="invalid-feedback">Selecciona el resultado de aprendizaje</div>
            </div>

            <div class="col-12">
              <label for="act-descripcion" class="form-label">Descripción</label>
              <textarea id="act-descripcion" name="descripcion" class="form-control" rows="3"
                        placeholder="Describe brevemente la actividad…"></textarea>
            </div>

          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
          <button type="submit" class="btn cc-btn-primary">
            <span class="material-symbols-outlined">save</span> Registrar actividad
          </button>
        </div>
      </form>
    </div>
  </div>
</div>


<!-- ---------------------- MODAL: Nuevo Ambiente ---------------------- -->
<div class="modal fade" id="modal-nuevo-ambiente" tabindex="-1"
     aria-labelledby="title-nuevo-ambiente" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="title-nuevo-ambiente">Nuevo Ambiente</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <form id="form-nuevo-ambiente" action="RegistrarAmbiente" method="POST" novalidate>
        <div class="modal-body">
          <div class="row g-3">

            <div class="col-12">
              <label for="amb-descripcion" class="form-label">Nombre / Descripción *</label>
              <input id="amb-descripcion" name="descripcion_Ambiente" class="form-control" type="text"
                     placeholder="ej. Lab 204" required />
              <div class="invalid-feedback">Ingresa el nombre del ambiente</div>
            </div>

            <div class="col-6">
              <label for="amb-capacidad" class="form-label">Capacidad (personas) *</label>
              <input id="amb-capacidad" name="capacidad" class="form-control"
                     type="number" min="1" max="500" placeholder="ej. 30" required />
              <div class="invalid-feedback">Ingresa la capacidad</div>
            </div>

            <div class="col-6">
              <label for="amb-sede" class="form-label">Sede *</label>
              <select id="amb-sede" name="Sede_id_sede" class="form-select" required>
                <option value="">Seleccionar...</option>
                              </select>
              <div class="invalid-feedback">Selecciona la sede</div>
            </div>

          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
          <button type="submit" class="btn cc-btn-primary">
            <span class="material-symbols-outlined">save</span> Registrar ambiente
          </button>
        </div>
      </form>
    </div>
  </div>
</div>


<!-- -------------- SCRIPTS -------------- -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js"></script>
<script src="JS/ClassControl_ui.js"></script>
<script>
  // Solo Administrador (3) gestiona registros; el resto solo consulta.
  window.ccPuedeGestionarInicio = <%= (sesRol != null && sesRol == 3) %>;
</script>
<script src="JS/Pagina_PrincipalJS.js"></script>

<%-- Mapeo data-catalogo - Servlet independiente --%>
<script src="./JS/catalogo-modal.js"></script>
  <script src="JS/ClassControl_theme.js"></script>
</body>
</html>
