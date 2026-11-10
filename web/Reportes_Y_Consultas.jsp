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
  // Reportes: Instructor (1), Administrador (3) y Coordinador (4).
  if (sesRol == null || !(sesRol == 1 || sesRol == 3 || sesRol == 4)) {
    response.sendRedirect("Pagina_Principal.jsp?error=permiso");
    return;
  }
  boolean esInstructorWeb = (sesRol == 1);
%>
<!DOCTYPE html>
<html lang="es" data-bs-theme="light">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Reportes y Consultas - ClassControl</title>
  <link rel="icon" type="image/png" href="img/logo.png" />

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous" rel="stylesheet" />
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet" />

  <link rel="stylesheet" href="CSS/ClassControl_base.css" />
  <link rel="stylesheet" href="CSS/ClassControl_theme.css" />
  <link rel="stylesheet" href="CSS/ClassControl_sidebar.css" />
  <link rel="stylesheet" href="CSS/Reportes_Y_ConsultasCSS.css" />
</head>
<body>
<div class="cc-layout">

  <% String ccActivePage = "reportes"; %>
  <%@ include file="_Sidebar.jspf" %>

  <main class="cc-main cc-catalog-main">

    <button class="cc-sidebar-toggle d-lg-none" id="sidebar-toggle" aria-label="Abrir menú">
      <span class="material-symbols-outlined">menu</span>
    </button>

    <!-- ── Encabezado ── -->
    <div class="d-flex flex-wrap align-items-start justify-content-between gap-3 mb-4">
      <div>
        <h1 class="cc-page-title">Reportes y Consultas</h1>
        <p class="text-muted mb-0">
          <%= esInstructorWeb
                ? "Resumen de tu programación y descarga de reportes."
                : "Indicadores de programación, ocupación de ambientes y descarga de reportes." %>
        </p>
      </div>

      <div class="d-flex flex-wrap align-items-center gap-2">
        <div>
          <label class="visually-hidden" for="selectPeriodo">Periodo</label>
          <select id="selectPeriodo" class="form-select cc-input">
            <option value="mes">Este mes</option>
            <option value="trimestre">Trimestre actual</option>
            <option value="anio">Año actual</option>
          </select>
        </div>
        <button type="button" class="btn cc-btn-outline" id="btnActualizar">
          <span class="material-symbols-outlined">refresh</span>
          Actualizar
        </button>
        <button id="dark-toggle" class="btn cc-icon-btn" title="Cambiar tema">
          <span class="material-symbols-outlined">dark_mode</span>
        </button>
      </div>
    </div>

    <!-- ── Mensajes ── -->
    <div id="estadoCarga" class="alert alert-info d-flex align-items-center gap-2" role="status">
      <span class="material-symbols-outlined">hourglass_top</span>
      <span>Cargando reportes...</span>
    </div>
    <div id="alertaError" class="alert alert-danger d-none" role="alert"></div>

    <!-- ── Indicadores ── -->
    <section class="row g-3 mb-4" aria-label="Indicadores del periodo">
      <div class="col-12 col-sm-6 col-xl-3">
        <div class="cc-kpi">
          <div class="cc-kpi-icon cc-kpi-icon--green">
            <span class="material-symbols-outlined">event_available</span>
          </div>
          <div class="cc-kpi-body">
            <p class="cc-kpi-label">Programaciones</p>
            <p class="cc-kpi-value" id="kpiProgramaciones">0</p>
            <p class="cc-kpi-hint" id="kpiProgramacionesHint">En el periodo</p>
          </div>
        </div>
      </div>
      <div class="col-12 col-sm-6 col-xl-3">
        <div class="cc-kpi">
          <div class="cc-kpi-icon cc-kpi-icon--blue">
            <span class="material-symbols-outlined">groups</span>
          </div>
          <div class="cc-kpi-body">
            <p class="cc-kpi-label">Instructores</p>
            <p class="cc-kpi-value" id="kpiInstructores">0</p>
            <p class="cc-kpi-hint" id="kpiInstructoresHint">Con programación</p>
          </div>
        </div>
      </div>
      <div class="col-12 col-sm-6 col-xl-3">
        <div class="cc-kpi">
          <div class="cc-kpi-icon cc-kpi-icon--orange">
            <span class="material-symbols-outlined">schedule</span>
          </div>
          <div class="cc-kpi-body">
            <p class="cc-kpi-label">Horas semanales</p>
            <p class="cc-kpi-value" id="kpiHoras">0</p>
            <p class="cc-kpi-hint" id="kpiHorasHint">Horas programadas</p>
          </div>
        </div>
      </div>
      <div class="col-12 col-sm-6 col-xl-3">
        <div class="cc-kpi">
          <div class="cc-kpi-icon cc-kpi-icon--red">
            <span class="material-symbols-outlined">verified</span>
          </div>
          <div class="cc-kpi-body">
            <p class="cc-kpi-label">Cumplimiento</p>
            <p class="cc-kpi-value" id="kpiCumplimiento">0%</p>
            <p class="cc-kpi-hint" id="kpiCumplimientoHint">Programaciones activas</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ── Gráficos ── -->
    <section class="row g-3 mb-4">
      <div class="col-12 col-xl-7">
        <div class="card cc-card h-100">
          <div class="card-body">
            <div class="cc-card-heading">
              <div>
                <h2 class="cc-card-title">Programaciones por programa</h2>
                <p class="cc-card-subtitle">Cantidad de bloques de formación en el periodo</p>
              </div>
            </div>
            <div class="cc-chart-wrap">
              <canvas id="graficoProgramas"
                      aria-label="Gráfico de barras con las programaciones por programa"></canvas>
            </div>
          </div>
        </div>
      </div>

      <div class="col-12 col-xl-5">
        <div class="card cc-card h-100">
          <div class="card-body">
            <div class="cc-card-heading">
              <div>
                <h2 class="cc-card-title">Distribución por estado</h2>
                <p class="cc-card-subtitle">Programaciones activas e inactivas</p>
              </div>
            </div>
            <div class="cc-chart-wrap cc-chart-wrap--doughnut">
              <canvas id="graficoEstados"
                      aria-label="Gráfico circular con la distribución de programaciones por estado"></canvas>
            </div>
          </div>
        </div>
      </div>

      <div class="col-12">
        <div class="card cc-card">
          <div class="card-body">
            <div class="cc-card-heading">
              <div>
                <h2 class="cc-card-title">Tendencia de los últimos 6 meses</h2>
                <p class="cc-card-subtitle">Programaciones que inician cada mes</p>
              </div>
            </div>
            <div class="cc-chart-wrap cc-chart-wrap--line">
              <canvas id="graficoTendencia"
                      aria-label="Gráfico de línea con la tendencia de programaciones de los últimos seis meses"></canvas>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ── Detalle ── -->
    <section class="card cc-card">
      <div class="card-body">
        <div class="d-flex flex-wrap align-items-start justify-content-between gap-3 mb-3">
          <div>
            <h2 class="cc-card-title">Detalle de programaciones</h2>
            <p class="cc-card-subtitle" id="resumenTabla">Sin datos para mostrar.</p>
          </div>

          <div class="d-flex flex-wrap gap-2">
            <div class="cc-search-field">
              <span class="material-symbols-outlined">search</span>
              <label class="visually-hidden" for="inputBuscar">Buscar en el detalle</label>
              <input type="search" id="inputBuscar" class="form-control cc-input"
                     placeholder="Buscar instructor, ficha, ambiente..." autocomplete="off" />
            </div>
            <div>
              <label class="visually-hidden" for="selectPrograma">Filtrar por programa</label>
              <select id="selectPrograma" class="form-select cc-input">
                <option value="todos">Todos los programas</option>
              </select>
            </div>
            <div>
              <label class="visually-hidden" for="selectEstado">Filtrar por estado</label>
              <select id="selectEstado" class="form-select cc-input">
                <option value="todos">Todos los estados</option>
              </select>
            </div>
            <button type="button" class="btn cc-btn-outline" id="btnLimpiar">
              <span class="material-symbols-outlined">filter_alt_off</span>
              Limpiar
            </button>
          </div>
        </div>

        <div class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-3">
          <p class="text-muted mb-0" id="textoOcupacion"></p>
          <div class="d-flex flex-wrap gap-2" role="group" aria-label="Exportar reporte">
            <button type="button" class="btn cc-btn-outline" id="btnExcel">
              <span class="material-symbols-outlined">table_view</span>
              Excel
            </button>
            <button type="button" class="btn cc-btn-outline" id="btnCsv">
              <span class="material-symbols-outlined">description</span>
              CSV
            </button>
            <button type="button" class="btn cc-btn-primary" id="btnPdf">
              <span class="material-symbols-outlined">picture_as_pdf</span>
              PDF
            </button>
          </div>
        </div>

        <div class="table-responsive">
          <table class="table cc-table align-middle mb-0" id="tablaDetalle">
            <thead>
              <tr>
                <th>Programa</th>
                <th>Ficha</th>
                <th>Instructor</th>
                <th>Ambiente</th>
                <th>Día</th>
                <th>Horario</th>
                <th class="text-end">Horas/sem</th>
                <th>Estado</th>
                <th>Vigencia</th>
              </tr>
            </thead>
            <tbody id="cuerpoDetalle">
              <tr>
                <td colspan="9" class="text-center text-muted py-4">Cargando...</td>
              </tr>
            </tbody>
          </table>
        </div>

        <noscript>
          <div class="alert alert-warning mt-3 mb-0">
            Activa JavaScript para ver los indicadores y descargar los reportes.
          </div>
        </noscript>
      </div>
    </section>

  </main>
</div><!-- /.cc-layout -->

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js" integrity="sha384-JUh163oCRItcbPme8pYnROHQMC6fNKTBWtRG3I3I0erJkzNgL7uxKlNwcrcFKeqF" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/jspdf@2.5.1/dist/jspdf.umd.min.js" integrity="sha384-JcnsjUPPylna1s1fvi1u12X5qjY5OL56iySh75FdtrwhO/SWXgMjoVqcKyIIWOLk" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/jspdf-autotable@3.8.2/dist/jspdf.plugin.autotable.min.js" integrity="sha384-fCAW/rDWORTbQXSiB7mOg0QtQ5c+r0f544y6XoKjuVva0nMBlCpNUjiFeG5iMdS3" crossorigin="anonymous"></script>
<script src="JS/ClassControl_ui.js"></script>
<script src="JS/Reportes_Y_ConsultasJS.js"></script>
<script src="JS/ClassControl_theme.js"></script>
</body>
</html>
