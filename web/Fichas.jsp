<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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

  // Aprendiz (2) no ve Fichas; el Instructor (1) sí, pero filtrado a las
  // suyas (ver ConsultarFichas / FichaDAO.listarFichasPorInstructor).
  if (sesRol == null || sesRol == 2) {
    response.sendRedirect("Pagina_Principal.jsp?error=permiso");
    return;
  }

  String rolLabel = "Usuario";
  if (sesRol != null) {
    if (sesRol == 1) rolLabel = "Instructor";
    else if (sesRol == 2) rolLabel = "Aprendiz";
    else if (sesRol == 3) rolLabel = "Administrador";
    else if (sesRol == 4) rolLabel = "Coordinador";
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
  <title><fmt:message bundle="${i18n}" key="fichas.title"/></title>

  <link rel="icon" type="image/png" href="img/logo.png" />
  <!-- -- Google Fonts (sistema de diseño ClassControl) -- -->
  <link rel="preconnect" href="https://fonts.googleapis.com"/>
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet"/>
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet"/>

  <!-- -- Bootstrap 5.3 -- -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>

  <!-- -- DataTables + Bootstrap 5 skin -- -->
  <link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css"/>

  <link rel="stylesheet" href="CSS/ClassControl_base.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_theme.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_sidebar.css" />
  <link rel="stylesheet" href="CSS/Pagina_PrincipalCSS.css"/>
  <link rel="stylesheet" href="CSS/FichasCSS.css"/>
</head>

<body class="cc-dash-body">
<div class="cc-wrapper">

  <!-- ---------- SIDEBAR ---------- -->
  <%   String ccActivePage = "fichas"; %>
<%@ include file="_Sidebar.jspf" %>

  <!-- ---------- MAIN CONTENT ---------- -->
  <main class="cc-main">

    <!-- -- HEADER -- -->
    <header class="cc-top-header">
      <div class="d-flex align-items-center gap-3">
        <!-- Hamburger (solo mobile) -->
        <button class="btn cc-icon-btn d-lg-none" id="sidebarToggle" aria-label="Menú">
          <span class="material-symbols-outlined fs-5">menu</span>
        </button>
        <h2 class="cc-page-title mb-0"><fmt:message bundle="${i18n}" key="fichas.header.title"/></h2>
      </div>

      <div class="d-flex align-items-center gap-3">
        <!-- Dark mode -->
        <button id="dark-toggle" class="btn cc-icon-btn" title="Cambiar tema">
          <span id="dark-icon" class="material-symbols-outlined">dark_mode</span>
        </button>

      </div>
    </header>

    <!-- -- CONTENIDO -- -->
    <div class="cc-dashboard-body">

      <!-- Barra de filtros -->
      <div class="card cc-filter-card border-0 shadow-sm mb-4">
        <div class="card-body py-3">
          <div class="row g-2 align-items-center">

            <!-- Búsqueda -->
            <div class="col-12 col-lg-4">
              <div class="input-group cc-search-group">
                <span class="input-group-text bg-transparent border-end-0 cc-search-addon">
                  <span class="material-symbols-outlined">search</span>
                </span>
                <input id="search-input" type="search"
                       class="form-control border-start-0 ps-0 cc-search-input"
                       placeholder="<fmt:message bundle="${i18n}" key="fichas.placeholder.buscar"/>"
                       autocomplete="off"/>
              </div>
            </div>

            <!-- Estado -->
            <div class="col-6 col-sm-4 col-lg-2">
              <select id="filter-estado" class="form-select form-select-sm cc-filter-select">
                <option value="">Estado: Todos</option>
              </select>
            </div>

            <!-- Modalidad -->
            <div class="col-6 col-sm-4 col-lg-2">
              <select id="filter-modalidad" class="form-select form-select-sm cc-filter-select">
                <option value="">Modalidad: Todas</option>
              </select>
            </div>

            <!-- Sede -->
            <div class="col-6 col-sm-4 col-lg-2">
              <select id="filter-sede" class="form-select form-select-sm cc-filter-select">
                <option value="">Sede: Todas</option>
              </select>
            </div>

            <!-- Nueva ficha -->
            <div class="col-6 col-lg-2 text-lg-end">
              <button id="btn-new-ficha" class="btn cc-btn-action w-100">
                <span class="material-symbols-outlined me-1">add_circle</span><fmt:message bundle="${i18n}" key="fichas.btn.nueva"/>
              </button>
            </div>

          </div>
        </div>
      </div>

      <!-- Tabla DataTables -->
      <div class="card cc-widget-card border-0 shadow-sm">
        <div class="card-body p-3 p-md-4">
          <div class="table-responsive">
            <table id="tabla-fichas" class="table table-hover align-middle cc-table w-100">
              <thead>
                <tr>
                  <th>Código</th>
                  <th>Programa de Formación</th>
                  <th class="text-center">Fechas</th>
                  <th>Modalidad</th>
                  <th class="text-center">Aprendices</th>
                  <th>Estado</th>
                  <th class="text-end">Acciones</th>
                </tr>
              </thead>
              <tbody id="fichas-tbody">
                <!-- Filas generadas por FichasJS.js -->
              </tbody>
            </table>
          </div>
        </div>
      </div>

    </div><!-- /cc-dashboard-body -->
  </main>
</div><!-- /cc-wrapper -->

<!-- Toast container -->
<div class="toast-container position-fixed bottom-0 end-0 p-3" id="toast-container"
     style="z-index:1100"></div>


<!-- ------------------------------------
     MODAL - NUEVA / EDITAR FICHA
------------------------------------ -->
<div class="modal fade" id="modal-form" tabindex="-1"
     aria-labelledby="form-modal-title" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-lg modal-dialog-scrollable">
    <div class="modal-content cc-modal">

      <div class="modal-header cc-modal-header">
        <h5 class="modal-title" id="form-modal-title">
          <span class="material-symbols-outlined me-2">note_add</span><fmt:message bundle="${i18n}" key="fichas.btn.nueva"/>
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>

      <form id="ficha-form" action="RegistrarFicha" method="POST" novalidate>
        <div class="modal-body">
          <div class="row g-3">

            <div class="col-md-6">
              <label class="form-label" for="f-codigo">
                Código <span class="text-danger">*</span>
              </label>
              <input id="f-codigo" name="codigo_ficha" type="number" class="form-control"
                     placeholder="Ej. 2560342" required/>
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="fichas.validation.codigo"/></div>
            </div>

            <div class="col-md-6">
              <label class="form-label" for="f-nivel">Nivel</label>
              <select id="f-nivel" name="Nivel_formacion_id_nivel_formacion" class="form-select" required>
                <option value="">Seleccione...</option>
              </select>
            </div>

            <div class="col-12">
              <label class="form-label" for="f-programa">
                Programa de Formación <span class="text-danger">*</span>
              </label>
              <select id="f-programa" name="Programas_idProgramas" class="form-select" required>
                <option value="">Seleccione...</option>
              </select>
              <div class="invalid-feedback"><fmt:message bundle="${i18n}" key="fichas.validation.programa"/></div>
            </div>

            <div class="col-md-6">
              <label class="form-label" for="f-sede">Sede</label>
              <select id="f-sede" name="Sede_id_sede" class="form-select" required>
                <option value="">Seleccione...</option>
              </select>
            </div>

            <div class="col-md-6">
              <label class="form-label" for="f-modalidad">Modalidad</label>
              <select id="f-modalidad" name="Modalidad_id_modalidad" class="form-select" required>
                <option value="">Seleccione...</option>
              </select>
            </div>

            <div class="col-md-6">
              <label class="form-label" for="f-inicio">Fecha de Inicio</label>
              <input id="f-inicio" name="fecha_inicio" type="date" class="form-control"/>
            </div>

            <div class="col-md-6">
              <label class="form-label" for="f-fin">Fecha de Fin</label>
              <input id="f-fin" name="fecha_fin" type="date" class="form-control"/>
            </div>

            <div class="col-md-6">
              <label class="form-label" for="f-aprendices">N.º Aprendices</label>
              <input id="f-aprendices" name="cantidad_aprendices" type="number" min="0"
                     class="form-control" placeholder="0"/>
            </div>

            <div class="col-md-6">
              <label class="form-label" for="f-estado">Estado</label>
              <select id="f-estado" name="Estado_id_estado" class="form-select" required>
                <option value="">Seleccione...</option>
              </select>
            </div>

            <div class="col-md-6">
              <label class="form-label" for="f-jornada">Jornada</label>
              <select id="f-jornada" name="Jornada_id_jornada" class="form-select" required>
                <option value="">Seleccione...</option>
              </select>
            </div>

            <div class="col-md-6">
              <label class="form-label" for="f-etapa">Etapa</label>
              <select id="f-etapa" name="Etapa_id_etapa" class="form-select" required>
                <option value="">Seleccione...</option>
              </select>
            </div>

          </div>
        </div>

        <div class="modal-footer cc-modal-footer">
          <button type="button" id="btn-form-delete"
                  class="btn btn-outline-danger me-auto d-none">
            <span class="material-symbols-outlined me-1">delete</span>Eliminar
          </button>
          <button type="button" class="btn cc-btn-secondary"
                  data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cancelar"/></button>
          <button type="submit" class="btn cc-btn-action">
            <span class="material-symbols-outlined me-1">save</span><fmt:message bundle="${i18n}" key="fichas.btn.guardar"/>
          </button>
        </div>
      </form>

    </div>
  </div>
</div>


<!-- ------------------------------------
     MODAL - DETALLE (solo lectura)
------------------------------------ -->
<div class="modal fade" id="modal-detail" tabindex="-1"
     aria-labelledby="detail-modal-title" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
    <div class="modal-content cc-modal">

      <div class="modal-header cc-modal-header">
        <h5 class="modal-title" id="detail-modal-title">
          <span style="color:var(--cc-primary)" class="material-symbols-outlined me-2">description</span><fmt:message bundle="${i18n}" key="fichas.detalle.titulo"/>
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>

      <div class="modal-body" id="detail-content">
        <!-- Inyectado por JS -->
      </div>

      <div class="modal-footer cc-modal-footer">
        <button type="button" class="btn cc-btn-secondary"
                data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cerrar"/></button>
        <button type="button" id="btn-detail-edit" class="btn cc-btn-action">
          <span class="material-symbols-outlined me-1">edit</span><fmt:message bundle="${i18n}" key="common.btn.editar"/>
        </button>
      </div>

    </div>
  </div>
</div>


<!-- ------------------------------------
     MODAL - CONFIRMAR ELIMINACIÓN
------------------------------------ -->
<div class="modal fade" id="modal-confirm" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-sm">
    <div class="modal-content cc-modal">

      <div class="modal-body p-4">
        <div class="d-flex align-items-center gap-3 mb-3">
          <div class="cc-warn-icon">
            <span class="material-symbols-outlined text-danger fs-4">warning</span>
          </div>
          <div>
            <h6 class="fw-bold mb-0"><fmt:message bundle="${i18n}" key="fichas.confirm.eliminarTitulo"/></h6>
            <small class="text-muted"><fmt:message bundle="${i18n}" key="common.confirm.irreversible"/></small>
          </div>
        </div>
        <p class="text-muted mb-0" style="font-size:.875rem">
          ¿Confirmas eliminar la ficha
          <strong id="confirm-ficha-name" class="text-dark"></strong>?
        </p>
      </div>

      <div class="modal-footer cc-modal-footer border-0 pt-0">
        <button type="button" class="btn cc-btn-secondary"
                data-bs-dismiss="modal"><fmt:message bundle="${i18n}" key="common.btn.cancelar"/></button>
        <button type="button" id="btn-confirm-delete" class="btn btn-danger">
          <span class="material-symbols-outlined me-1">delete</span><fmt:message bundle="${i18n}" key="fichas.btn.confirmarEliminar"/>
        </button>
      </div>

    </div>
  </div>
</div>


<!-- -- Librerías JS -- -->
<script>
  // Solo Administrador (3) crea/edita/elimina.
  // Coordinador (4), Instructor (1) y Aprendiz (2): solo lectura.
  window.ccPuedeEscribirFichas  = <%= (sesRol != null && sesRol == 3) %>;
  window.ccPuedeEliminarFichas  = <%= (sesRol != null && sesRol == 3) %>;
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js"></script>
<!-- JS en la MISMA carpeta que el HTML -->
<script src="JS/ClassControl_ui.js"></script>
<script src="JS/FichasJS.js"></script>
  <script src="JS/ClassControl_theme.js"></script>
</body>
</html>
