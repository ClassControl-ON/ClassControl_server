<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- ══════════════════════ PROTECCIÓN DE SESIÓN ══════════════════════ --%>
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
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="es_CO"/>
<fmt:setBundle basename="messages_es" var="i18n"/>
<!DOCTYPE html>
<html lang="es" data-bs-theme="light">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title><fmt:message bundle="${i18n}" key="programacion.title"/></title>

  <link rel="icon" type="image/png" href="img/logo.png" />
  <!-- Bootstrap 5 -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />

  <!-- DataTables + Bootstrap 5 theme -->
  <link href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css" rel="stylesheet" />

  <!-- Material Symbols -->
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet" />

  <!-- Google Fonts -->
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet" />

  <!-- Custom CSS -->
  <link rel="stylesheet" href="CSS/ClassControl_base.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_theme.css" />
  <link rel="stylesheet" href="CSS/ClassControl_sidebar.css" />
  <link rel="stylesheet" href="CSS/Programacion_instructoresCSS.css" />
</head>

<body>
<!-- ══════════════════════════════════════════════════════════
     LAYOUT WRAPPER
══════════════════════════════════════════════════════════ -->
<div class="cc-layout">

  <!-- ── SIDEBAR ── -->
  <%   String ccActivePage = "programacion"; %>
<%@ include file="_Sidebar.jspf" %>

  <!-- ── MAIN CONTENT ── -->
  <div class="cc-main">

    <!-- Header -->
    <header class="cc-header">
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-sm d-lg-none" id="sidebar-toggle">
          <span class="material-symbols-outlined">menu</span>
        </button>
        <h1 class="cc-page-title mb-0"><fmt:message bundle="${i18n}" key="programacion.header.title"/></h1>
      </div>

      <div class="cc-header__actions">
        <!-- Search -->
        <div class="input-group cc-search">
          <span class="input-group-text bg-transparent border-end-0">
            <span class="material-symbols-outlined cc-icon-sm">search</span>
          </span>
          <input id="search-input" type="text"
                 class="form-control border-start-0"
                 placeholder="<fmt:message bundle="${i18n}" key="programacion.placeholder.buscar"/>" />
        </div>

        <!-- <fmt:message bundle="${i18n}" key="programacion.btn.nueva"/> -->
        <button id="btn-new" class="btn btn-primary cc-btn-primary">
          <span class="material-symbols-outlined cc-icon-sm">add</span>
          <fmt:message bundle="${i18n}" key="programacion.btn.nueva"/>
        </button>

        <!-- <fmt:message bundle="${i18n}" key="common.btn.exportar"/> -->
        <button onclick="window.print()" class="btn btn-outline-secondary cc-btn-icon-text">
          <span class="material-symbols-outlined cc-icon-sm">picture_as_pdf</span>
          <fmt:message bundle="${i18n}" key="common.btn.exportar"/>
        </button>

        <!-- Dark mode -->
        <button id="dark-toggle" class="btn cc-icon-btn" title="Cambiar tema">
          <span class="material-symbols-outlined cc-icon-sm">dark_mode</span>
        </button>

      </div>
    </header>

    <!-- Toolbar: views + filter -->
    <div class="cc-toolbar">
      <div class="cc-view-toggle btn-group" role="group">
        <button id="btn-view-calendar" class="btn btn-sm btn-outline-primary active"
                title="Vista calendario">
          <span class="material-symbols-outlined cc-icon-sm">calendar_view_week</span>
          <fmt:message bundle="${i18n}" key="programacion.vista.calendario"/>
        </button>
        <button id="btn-view-list" class="btn btn-sm btn-outline-primary"
                title="Vista lista">
          <span class="material-symbols-outlined cc-icon-sm">table_rows</span>
          <fmt:message bundle="${i18n}" key="programacion.vista.lista"/>
        </button>
      </div>

      <div class="d-flex flex-wrap align-items-center gap-2" aria-label="Filtros de programación">
        <label class="cc-filter-label" for="filter-instructor">Instructor</label>
        <select id="filter-instructor" class="form-select form-select-sm cc-filter-select">
          <option value="">Todos los instructores</option>
        </select>
        <span id="filter-instructor-horas" class="cc-filter-horas d-none"></span>

        <label class="cc-filter-label" for="filter-ficha">Ficha</label>
        <select id="filter-ficha" class="form-select form-select-sm cc-filter-select">
          <option value="">Todas las fichas</option>
        </select>

        <label class="cc-filter-label" for="filter-trimestre">Trimestre</label>
        <select id="filter-trimestre" class="form-select form-select-sm cc-filter-select">
          <option value="">Todos los trimestres</option>
        </select>

        <button id="btn-clear-filters" type="button" class="btn btn-sm btn-outline-secondary">
          Limpiar filtros
        </button>
      </div>
    </div>

    <!-- ── Calendar view ── -->
    <div id="calendar-view" class="cc-content-scroll">
      <div class="cc-calendar-wrapper">

        <!-- Day headers -->
        <div class="cc-calendar-grid cc-calendar-header">
          <div class="cc-time-col"></div>
          <div class="cc-day-header">Lunes</div>
          <div class="cc-day-header">Martes</div>
          <div class="cc-day-header">Miércoles</div>
          <div class="cc-day-header">Jueves</div>
          <div class="cc-day-header">Viernes</div>
          <div class="cc-day-header">Sábado</div>
        </div>

        <!-- Time slots (rendered by JS) -->
        <div id="calendar-body"></div>
      </div>
    </div>

    <!-- ── List view (DataTable) ── -->
    <div id="list-view" class="cc-content-scroll d-none">
      <div class="card cc-table-card">
        <div class="card-body p-0">
          <table id="schedule-table" class="table table-hover mb-0 w-100">
            <thead class="table-light">
              <tr>
                <th>Actividad / competencia</th>
                <th><fmt:message bundle="${i18n}" key="programacion.form.instructor"/></th>
                <th>Ficha</th>
                <th>Trimestre</th>
                <th>Ambiente</th>
                <th>Día</th>
                <th>Horario</th>
                <th>Horas/sem</th>
                <th>Estado</th>
                <th>Vigencia</th>
                <th></th>
              </tr>
            </thead>
            <tbody id="list-tbody"></tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <footer class="cc-footer">
      <div class="d-flex align-items-center gap-4">
        <span class="cc-legend"><span class="cc-dot cc-dot--green"></span>Área Técnica</span>
        <span class="cc-legend"><span class="cc-dot cc-dot--blue"></span>Idiomas</span>
        <span class="cc-legend"><span class="cc-dot cc-dot--orange"></span>Matemáticas</span>
        <span class="cc-legend"><span class="cc-dot cc-dot--purple"></span>Transversales</span>
      </div>
      <span class="cc-footer__copy">ClassControl v2.5.0 © 2025</span>
    </footer>
  </div><!-- /.cc-main -->
</div><!-- /.cc-layout -->


<!-- ══════════════════════════════════════════════════════════
     MODAL — Nueva / Editar Programación
══════════════════════════════════════════════════════════ -->
<div class="modal fade" id="schedulingModal" tabindex="-1"
     aria-labelledby="modal-title" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">

      <div class="modal-header">
        <h5 class="modal-title" id="modal-title"><fmt:message bundle="${i18n}" key="programacion.btn.nueva"/></h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>

      <div class="modal-body">
        <form id="scheduling-form" action="RegistrarProgramacion" method="POST" novalidate>

          <input type="hidden" id="field-id" name="id" value="" />

          <!-- Materia -->
          <div class="mb-3">
            <label for="field-subject" class="form-label fw-semibold">
              <fmt:message bundle="${i18n}" key="programacion.form.materia"/> <span class="text-danger">*</span>
            </label>
            <select id="field-subject" name="Actividades_id_actividades" class="form-select" required>
              <option value="">Seleccione una actividad</option>
              <c:forEach items="${actividades}" var="actividad">
                <option value="${actividad[0]}"><c:out value="${actividad[1]}"/></option>
              </c:forEach>
            </select>
            <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="common.validation.requeridoCampo"/></div>
          </div>

          <!-- <fmt:message bundle="${i18n}" key="programacion.form.instructor"/> -->
          <div class="mb-3">
            <label for="field-instructor" class="form-label fw-semibold">
              <fmt:message bundle="${i18n}" key="programacion.form.instructor"/> <span class="text-danger">*</span>
            </label>
            <select id="field-instructor" name="Usuarios_id_usuarios" class="form-select" required>
              <option value="">Seleccione un instructor</option>
              <c:forEach items="${instructores}" var="instructor">
                <option value="${instructor[0]}"><c:out value="${instructor[1]}"/></option>
              </c:forEach>
            </select>
            <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="common.validation.requeridoCampo"/></div>
          </div>

          <!-- Ficha + Ambiente -->
          <div class="row g-3 mb-3">
            <div class="col-6">
              <label for="field-ficha" class="form-label fw-semibold">Ficha</label>
              <select id="field-ficha" name="Ficha_id_ficha" class="form-select" required>
                <option value="">Seleccione una ficha</option>
                <c:forEach items="${fichas}" var="ficha">
                  <option value="${ficha[0]}"><c:out value="${ficha[1]}"/> - <c:out value="${ficha[2]}"/></option>
                </c:forEach>
              </select>
            </div>
            <div class="col-6">
              <label for="field-ambiente" class="form-label fw-semibold">Ambiente</label>
              <select id="field-ambiente" name="Ambientes_id_ambientes" class="form-select" required>
                <option value="">Seleccione un ambiente</option>
                <c:forEach items="${ambientes}" var="ambiente">
                  <option value="${ambiente[0]}"><c:out value="${ambiente[1]}"/></option>
                </c:forEach>
              </select>
            </div>
          </div>

          <!-- Día + Franja -->
          <div class="row g-3 mb-3">
            <div class="col-6">
              <label for="field-day" class="form-label fw-semibold">Día</label>
              <select id="field-day" name="dias_Semana" class="form-select">
                <option value="LUN">Lunes</option>
                <option value="MAR">Martes</option>
                <option value="MIE">Miércoles</option>
                <option value="JUE">Jueves</option>
                <option value="VIE">Viernes</option>
                <option value="SAB">Sábado</option>
              </select>
            </div>
            <div class="col-6">
              <label for="field-slot" class="form-label fw-semibold">Franja horaria</label>
              <select id="field-slot" class="form-select">
                <option value="0">07:00 AM – 10:00 AM</option>
                <option value="1">10:00 AM – 01:00 PM</option>
                <option value="2">02:00 PM – 05:00 PM</option>
              </select>
            </div>
          </div>

          <div class="row g-3 mb-3">
            <div class="col-6">
              <label for="field-fecha-inicio" class="form-label fw-semibold">Fecha inicio</label>
              <input id="field-fecha-inicio" name="fecha_inicial_Prog" type="date" class="form-control" required />
            </div>
            <div class="col-6">
              <label for="field-fecha-fin" class="form-label fw-semibold">Fecha fin</label>
              <input id="field-fecha-fin" name="fecha_fin_Prog" type="date" class="form-control" required />
            </div>
            <div class="col-6">
              <label for="field-hora-inicio" class="form-label fw-semibold">Hora inicio</label>
              <input id="field-hora-inicio" name="hora_inicio" type="time" class="form-control" required />
            </div>
            <div class="col-6">
              <label for="field-hora-fin" class="form-label fw-semibold">Hora fin</label>
              <input id="field-hora-fin" name="hora_fin" type="time" class="form-control" required />
            </div>
          </div>
          <div class="row g-3 mb-3">
            <div class="col-6">
              <label for="field-trimestre" class="form-label fw-semibold">Trimestre <span class="text-danger">*</span></label>
              <select id="field-trimestre" name="Trimestre_id_trimestre" class="form-select" required>
                <option value="">Seleccione un trimestre</option>
                <c:forEach items="${trimestres}" var="trimestre">
                  <option value="${trimestre[0]}"><c:out value="${trimestre[1]}"/></option>
                </c:forEach>
              </select>
            </div>
            <div class="col-6">
              <label for="field-estado" class="form-label fw-semibold">Estado <span class="text-danger">*</span></label>
              <select id="field-estado" name="Estado_id_estado" class="form-select" required>
                <option value="">Seleccione un estado</option>
                <c:forEach items="${estados}" var="estado">
                  <option value="${estado[0]}"><c:out value="${estado[1]}"/></option>
                </c:forEach>
              </select>
            </div>
          </div>
          <div class="mb-3">
            <label for="field-obs" class="form-label fw-semibold">Observaciones</label>
            <textarea id="field-obs" name="Observaciones" class="form-control" rows="2" maxlength="45"></textarea>
          </div>
          <!-- Color -->
          <div class="mb-3">
            <label for="field-color" class="form-label fw-semibold">Área / Color de etiqueta</label>
            <select id="field-color" class="form-select">
              <option value="green">🟢 Verde — Área Técnica</option>
              <option value="blue">🔵 Azul — Idiomas</option>
              <option value="orange">🟠 Naranja — Matemáticas</option>
              <option value="purple">🟣 Morado — Transversales</option>
            </select>
          </div>

        </form>
      </div>

      <div class="modal-footer justify-content-between">
        <button type="button" id="btn-delete" class="btn btn-outline-danger d-none">
          <span class="material-symbols-outlined cc-icon-sm">delete</span> <fmt:message bundle="${i18n}" key="common.btn.eliminar"/>
        </button>
        <div class="d-flex gap-2">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cancelar"/></button>
          <button type="submit" form="scheduling-form" class="btn btn-primary">
            <span class="material-symbols-outlined cc-icon-sm">save</span> <fmt:message bundle="${i18n}" key="common.btn.guardar"/>
          </button>
        </div>
      </div>

    </div>
  </div>
</div>

<!-- Toast -->
<div class="toast-container position-fixed bottom-0 end-0 p-3">
  <div id="toast" class="toast align-items-center text-white border-0" role="status" aria-live="polite">
    <div class="d-flex">
      <div class="toast-body fw-semibold" id="toast-body"><fmt:message bundle="${i18n}" key="common.info.mensaje"/></div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto"
              data-bs-dismiss="toast" aria-label="Cerrar"></button>
    </div>
  </div>
</div>

<!-- ── Scripts ── -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js"></script>
<script src="JS/ClassControl_ui.js"></script>
<script>
  // Solo el Administrador (3) crea/edita/elimina programación.
  // Coordinador (4), Instructor (1) y Aprendiz (2) solo consultan.
  window.ccPuedeEscribirProgramacion = <%= (sesRol != null && sesRol == 3) %>;
</script>
<script src="JS/Programacion_instructoresJS.js"></script>
  <script src="JS/ClassControl_theme.js"></script>
</body>
</html>
