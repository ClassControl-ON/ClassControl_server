<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Reportes y Consultas - ClassControl</title>

  <link rel="icon" type="image/png" href="img/logo.png" />
  <!-- Bootstrap 5 -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous" rel="stylesheet" />
  <!-- DM Sans -->
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet" />
  <!-- Material Symbols -->
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet" />

  <link rel="stylesheet" href="CSS/ClassControl_base.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_theme.css" />
  <link rel="stylesheet" href="CSS/ClassControl_sidebar.css" />
  <link rel="stylesheet" href="CSS/Reportes_Y_ConsultasCSS.css" />
</head>
<body>


<!-- ==== SIDEBAR ==== -->
<%   String ccActivePage = "reportes"; %>
<%@ include file="_Sidebar.jspf" %>

<!-- ==== MAIN ==== -->
<main class="cc-main">

  <!-- TOP BAR -->
  <header class="cc-topbar">
    <div>
      <h2 class="cc-page-title">Reportes y Consultas</h2>
      <nav class="cc-breadcrumb">
        <span>ClassControl</span>
        <span class="material-symbols-outlined">chevron_right</span>
        <span class="cc-breadcrumb-active">Análisis de Gestión</span>
      </nav>
    </div>
    <div class="cc-topbar-actions">
      <!-- Selector de periodo como form -->
      <form id="formPeriodo" class="cc-period-form">
        <label for="selectPeriodo" class="visually-hidden">Periodo</label>
        <span class="material-symbols-outlined">calendar_today</span>
        <select id="selectPeriodo" name="periodo" class="cc-period-select">
          <option value="mes">Este Mes</option>
          <option value="trimestre">Trimestre Actual</option>
          <option value="anio">Año 2024</option>
        </select>
      </form>
      <div class="d-flex gap-2">
        <button id="dark-toggle" class="cc-icon-btn" title="Cambiar tema">
          <span class="material-symbols-outlined">dark_mode</span>
        </button>
        <button class="cc-icon-btn position-relative" title="Notificaciones">
          <span class="material-symbols-outlined">notifications</span>
          <span class="cc-notification-dot"></span>
        </button>
        <button class="cc-icon-btn" title="Perfil">
          <span class="material-symbols-outlined">account_circle</span>
        </button>
      </div>
    </div>
  </header>

  <div class="cc-content">

    <!-- ==== CARDS DE MÉTRICAS ==== -->
    <section class="row g-4 mb-4">
      <div class="col-12 col-md-6 col-lg-3">
        <div class="cc-metric-card cc-metric-green">
          <div class="cc-metric-top">
            <div class="cc-metric-icon bg-green-soft">
              <span class="material-symbols-outlined text-green">groups</span>
            </div>
            <span class="cc-badge-green">+4% vs mes ant.</span>
          </div>
          <p class="cc-metric-label">Instructores Programados</p>
          <h3 class="cc-metric-value" id="metricInstructores">${metricaInstructores}</h3>
        </div>
      </div>
      <div class="col-12 col-md-6 col-lg-3">
        <div class="cc-metric-card cc-metric-blue">
          <div class="cc-metric-top">
            <div class="cc-metric-icon bg-blue-soft">
              <span class="material-symbols-outlined text-blue">schedule</span>
            </div>
            <span class="cc-badge-blue">82% de la meta</span>
          </div>
          <p class="cc-metric-label">Horas de Formación</p>
          <h3 class="cc-metric-value" id="metricHoras">${metricaHoras}</h3>
        </div>
      </div>
      <div class="col-12 col-md-6 col-lg-3">
        <div class="cc-metric-card cc-metric-orange">
          <div class="cc-metric-top">
            <div class="cc-metric-icon bg-orange-soft">
              <span class="material-symbols-outlined text-orange">room_preferences</span>
            </div>
            <span class="cc-badge-orange">Pico: 10am - 2pm</span>
          </div>
          <p class="cc-metric-label">Ocupación de Ambientes</p>
          <h3 class="cc-metric-value" id="metricOcupacion">${metricaOcupacion}</h3>
        </div>
      </div>
      <div class="col-12 col-md-6 col-lg-3">
        <div class="cc-metric-card cc-metric-red">
          <div class="cc-metric-top">
            <div class="cc-metric-icon bg-red-soft">
              <span class="material-symbols-outlined text-red">verified</span>
            </div>
            <span class="cc-badge-red">Meta: 90%</span>
          </div>
          <p class="cc-metric-label">Cumplimiento Competencias</p>
          <h3 class="cc-metric-value" id="metricCumplimiento">${metricaCumplimiento}</h3>
        </div>
      </div>
    </section>

    <!-- ==== GRÁFICOS ==== -->
    <section class="row g-4 mb-4">

      <!-- Barras por Programa -->
      <div class="col-12 col-lg-8">
        <div class="cc-card h-100">
          <div class="cc-card-header">
            <div>
              <h4 class="cc-card-title">Ocupación por Programa</h4>
              <p class="cc-card-subtitle">Distribución de horas por área de formación</p>
            </div>
            <!-- Form de filtro avanzado -->
            <form id="formFiltroPrograma" class="d-flex gap-2 align-items-center">
              <label for="selectPrograma" class="visually-hidden">Filtrar programa</label>
              <select id="selectPrograma" name="programa" class="form-select form-select-sm cc-filter-select">
                <option value="todos">Todos los programas</option>
                <option value="software">Desarrollo de Software</option>
                <option value="multimedia">Multimedia y Diseño</option>
                <option value="admin">Gestión Administrativa</option>
                <option value="mante">Mantenimiento Electrónico</option>
              </select>
              <button type="submit" class="btn btn-sm cc-btn-outline">
                <span class="material-symbols-outlined" style="font-size:16px">filter_list</span>
                Filtrar
              </button>
            </form>
          </div>
          <div class="cc-card-body" id="programaBars">
            <!-- Renderizado por JS -->
          </div>
        </div>
      </div>

      <!-- Gráfico de Tendencia Semanal -->
      <div class="col-12 col-lg-4">
        <div class="cc-card h-100">
          <div class="cc-card-header">
            <div>
              <h4 class="cc-card-title">Tendencia Semanal</h4>
              <p class="cc-card-subtitle">Variación de horas impartidas</p>
            </div>
          </div>
          <div class="cc-card-body">
            <div class="cc-bar-chart" id="tendenciaChart">
              <!-- Renderizado por JS -->
            </div>
            <div class="cc-bar-labels" id="tendenciaLabels">
              <!-- Renderizado por JS -->
            </div>
          </div>
        </div>
      </div>

    </section>

    <!-- ==== REPORTES DISPONIBLES ==== -->
    <section class="cc-card">
      <div class="cc-card-header">
        <div>
          <h4 class="cc-card-title">Reportes Disponibles</h4>
          <p class="cc-card-subtitle">Documentación oficial lista para descarga</p>
        </div>
        <!-- Form de búsqueda -->
        <form id="formBuscarReporte" class="cc-search-form">
          <span class="material-symbols-outlined cc-search-icon">search</span>
          <label class="visually-hidden" for="inputBuscarReporte">Buscar reporte</label>
          <input
            type="search"
            id="inputBuscarReporte"
            name="busqueda"
            class="cc-search-input"
            placeholder="Buscar reporte..."
            autocomplete="off"
          />
        </form>
      </div>

      <div class="table-responsive">
        <table class="cc-table" id="tablaReportes">
          <thead>
            <tr>
              <th>Nombre del Reporte</th>
              <th>Última Generación</th>
              <th>Categoría</th>
              <th class="text-end">Acciones</th>
            </tr>
          </thead>
          <tbody id="tablaReportesBody">
            <%-- Ejemplo de iteración con JSTL --%>
            <%--
            <c:forEach var="reporte" items="${listaReportes}">
              <tr>
                <td>
                  <div class="d-flex align-items-center gap-2">
                    <span class="material-symbols-outlined text-muted">description</span>
                    <span class="fw-medium">${reporte.nombre}</span>
                  </div>
                </td>
                <td>${reporte.fechaGeneracion}</td>
                <td><span class="badge bg-light text-dark border">${reporte.categoria}</span></td>
                <td class="text-end">
                  <a href="${pageContext.request.contextPath}/descargarReporte?id=${reporte.id}" class="btn btn-sm btn-outline-primary">Descargar</a>
                </td>
              </tr>
            </c:forEach>
            --%>
          </tbody>
        </table>
      </div>

      <div class="cc-card-footer text-center">
        <button class="cc-link-btn" id="btnVerTodos">Ver todos los reportes disponibles (${totalReportes})</button>
      </div>
    </section>

  </div><!-- /cc-content -->
</main>

<!-- ==== MODAL: NUEVO REPORTE ==== -->
<div class="modal fade" id="modalNuevoReporte" tabindex="-1" aria-labelledby="modalNuevoReporteLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content cc-modal">
      <div class="modal-header cc-modal-header">
        <div class="d-flex align-items-center gap-2">
          <span class="material-symbols-outlined text-green">add_chart</span>
          <h5 class="modal-title cc-modal-title" id="modalNuevoReporteLabel">Generar Nuevo Reporte</h5>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body p-4">
        <form id="formNuevoReporte" novalidate>
          <div class="mb-3">
            <label for="inputNombreReporte" class="cc-label">Nombre del Reporte <span class="text-danger">*</span></label>
            <input type="text" class="form-control cc-input" id="inputNombreReporte" name="nombre" placeholder="Ej: Listado Fichas Activas Junio" required />
            <div class="invalid-feedback">El nombre del reporte es obligatorio.</div>
          </div>

          <div class="mb-3">
            <label for="selectCategoria" class="cc-label">Categoría <span class="text-danger">*</span></label>
            <select class="form-select cc-input" id="selectCategoria" name="categoria" required>
              <option value="">Seleccionar categoría...</option>
              <option value="academico">Académico</option>
              <option value="talento">Talento Humano</option>
              <option value="logistica">Logística</option>
              <option value="financiero">Financiero</option>
            </select>
            <div class="invalid-feedback">Selecciona una categoría.</div>
          </div>

          <div class="row g-3 mb-3">
            <div class="col-6">
              <label for="inputFechaInicio" class="cc-label">Fecha Inicio <span class="text-danger">*</span></label>
              <input type="date" class="form-control cc-input" id="inputFechaInicio" name="fechaInicio" required />
              <div class="invalid-feedback">Selecciona la fecha de inicio.</div>
            </div>
            <div class="col-6">
              <label for="inputFechaFin" class="cc-label">Fecha Fin <span class="text-danger">*</span></label>
              <input type="date" class="form-control cc-input" id="inputFechaFin" name="fechaFin" required />
              <div class="invalid-feedback">Selecciona la fecha de fin.</div>
            </div>
          </div>

          <div class="mb-3">
            <span class="cc-label d-block">Formato de Exportación</span>
            <div class="d-flex gap-3 mt-1">
              <div class="form-check">
                <input class="form-check-input" type="radio" name="formato" id="formatoPdf" value="pdf" checked />
                <label class="form-check-label cc-label" for="formatoPdf">PDF</label>
              </div>
              <div class="form-check">
                <input class="form-check-input" type="radio" name="formato" id="formatoExcel" value="excel" />
                <label class="form-check-label cc-label" for="formatoExcel">Excel</label>
              </div>
              <div class="form-check">
                <input class="form-check-input" type="radio" name="formato" id="formatoCsv" value="csv" />
                <label class="form-check-label cc-label" for="formatoCsv">CSV</label>
              </div>
            </div>
          </div>

          <div class="mb-3">
            <label for="textareaDescripcion" class="cc-label">Descripción (opcional)</label>
            <textarea class="form-control cc-input" id="textareaDescripcion" name="descripcion" rows="3" placeholder="Observaciones o detalles adicionales..."></textarea>
          </div>
        </form>
      </div>
      <div class="modal-footer cc-modal-footer">
        <button type="button" class="btn cc-btn-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="button" class="btn cc-btn-primary" id="btnGuardarReporte">
          <span class="material-symbols-outlined">save</span>
          Generar Reporte
        </button>
      </div>
    </div>
  </div>
</div>

<!-- FAB -->
<button class="cc-fab" id="btnFAB" data-bs-toggle="modal" data-bs-target="#modalNuevoReporte" title="Nuevo Reporte">
  <span class="material-symbols-outlined">add_chart</span>
  <span class="cc-fab-tooltip">Nuevo Reporte</span>
</button>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
<script src="JS/ClassControl_ui.js"></script>
<script src="JS/Reportes_Y_ConsultasJS.js"></script>
  <script src="JS/ClassControl_theme.js"></script>
</body>
</html>
