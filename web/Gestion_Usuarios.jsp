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

  // Solo Administrador (3) y Coordinador (4) ven Gestión de Usuarios.
  if (sesRol == null || (sesRol != 3 && sesRol != 4)) {
    response.sendRedirect("Pagina_Principal.jsp?error=permiso");
    return;
  }
%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>ClassControl – Gestión de Usuarios (Bootstrap 5)</title>

  <link rel="icon" type="image/png" href="img/logo.png" />
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet">

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">

  <link rel="stylesheet" href="CSS/ClassControl_base.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_theme.css" />
  <link rel="stylesheet" href="CSS/ClassControl_sidebar.css" />
  <link rel="stylesheet" href="CSS/Gestion_UsuariosCSS.css" />
</head>
<body>

  <div class="cc-wrapper">
    <!-- Sidebar desktop -->
    <%   String ccActivePage = "usuarios"; %>
<%@ include file="_Sidebar.jspf" %>

    <!-- Main -->
    <div class="cc-main">
      <header class="border-bottom bg-white sticky-top z-2">
        <div class="container-fluid py-2 d-flex flex-wrap gap-2 align-items-center justify-content-between">
          <div class="d-flex align-items-center gap-2 flex-grow-1">
            <button class="cc-sidebar-toggle d-lg-none" id="btnSidebarToggle" aria-label="Abrir menú">
              <span class="material-symbols-outlined align-middle">menu</span>
            </button>
            <div class="input-group" style="max-width: 420px;">
              <span class="input-group-text bg-white"><span class="material-symbols-outlined">search</span></span>
              <input id="searchInput" type="search" class="form-control" placeholder="Buscar por nombre, correo, rol o ID...">
            </div>
          </div>
          <div class="d-flex align-items-center gap-2">
            <button id="dark-toggle" class="btn cc-icon-btn" title="Cambiar tema">
              <span class="material-symbols-outlined">dark_mode</span>
            </button>
            <button class="btn btn-sm btn-outline-secondary" id="btnExport" type="button">Exportar CSV</button>
            <% if (sesRol != null && sesRol == 3) { %>
            <button class="btn btn-sm btn-brand" id="btnNewUser" type="button" data-bs-toggle="modal" data-bs-target="#userModal">Nuevo Usuario</button>
            <% } %>
          </div>
        </div>
      </header>

      <main class="container-fluid py-4">
        <div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
          <div>
            <h2 class="cc-page-title mb-1">Gestión de Usuarios</h2>
            <p class="text-muted mb-0">Administra los accesos y roles de la institución académica.</p>
          </div>

          <div class="d-flex gap-2 flex-wrap">
            <select id="filterFicha" class="form-select form-select-sm w-auto">
              <option value="">Todas las fichas</option>
              <%-- Las opciones se llenan dinámicamente en JS desde ConsultarFichas --%>
            </select>
            <select id="filterRole" class="form-select form-select-sm w-auto">
              <option value="">Todos los roles</option>
              <%-- Las opciones de rol se llenan dinámicamente en JS desde ConsultarRoles --%>
            </select>
            <select id="filterStatus" class="form-select form-select-sm w-auto">
              <option value="">Cualquier estado</option>
              <option>Activo</option>
              <option>Inactivo</option>
            </select>
          </div>
        </div>

        <section class="row g-3 mb-4">
          <div class="col-12 col-md-4">
            <div class="card border-0 shadow-sm stat-card h-100 border-start border-4 border-brand-green">
              <div class="card-body">
                <p class="text-uppercase fw-semibold small text-muted mb-2">Total Usuarios</p>
                <h3 class="fw-bold text-brand-blue mb-0" id="statTotal">0</h3>
              </div>
            </div>
          </div>
          <div class="col-12 col-md-4">
            <div class="card border-0 shadow-sm stat-card h-100 border-start border-4 border-brand-blue">
              <div class="card-body">
                <p class="text-uppercase fw-semibold small text-muted mb-2">Usuarios Activos</p>
                <h3 class="fw-bold text-brand-blue mb-0" id="statActive">0</h3>
                <small class="text-muted" id="statPct">0%</small>
              </div>
            </div>
          </div>
          <div class="col-12 col-md-4">
            <div class="card border-0 shadow-sm stat-card h-100 border-start border-4 border-warning">
              <div class="card-body">
                <p class="text-uppercase fw-semibold small text-muted mb-2">Inactivos / Pendientes</p>
                <h3 class="fw-bold text-brand-blue mb-0" id="statInactive">0</h3>
              </div>
            </div>
          </div>
        </section>

        <section class="card border-0 shadow-sm mb-4">
          <div class="card-body table-responsive">
            <table id="usersTable" class="table table-hover align-middle w-100">
              <thead>
                <tr>
                  <th>Usuario</th>
                  <th>Correo</th>
                  <th>Rol</th>
                  <th>Estado</th>
                  <th class="text-end">Acciones</th>
                </tr>
              </thead>
              <tbody>
                <%-- Se llena dinámicamente en JS con fetch('ConsultarUsuarios') --%>
              </tbody>
            </table>
          </div>
        </section>

        <section class="row g-3 mb-4">
          <div class="col-12 col-lg-6">
            <div class="card border-0 shadow-sm h-100">
              <div class="card-header bg-white border-0 pb-0">
                <h4 class="h6 fw-bold text-brand-blue mb-0">Distribución por Rol</h4>
              </div>
              <div class="card-body">
                <canvas id="roleChart" height="220"></canvas>
              </div>
            </div>
          </div>
          <div class="col-12 col-lg-6">
            <div class="card border-0 shadow-sm h-100">
              <div class="card-header bg-white border-0 pb-0">
                <h4 class="h6 fw-bold text-brand-blue mb-0">Distribución por Estado</h4>
              </div>
              <div class="card-body">
                <canvas id="statusChart" height="220"></canvas>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  </div>

  <!-- Sidebar móvil: lo gestiona el drawer compartido de ClassControl_theme.js -->

  <!-- Modal Usuario -->
  <div class="modal fade" id="userModal" tabindex="-1" aria-labelledby="userModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
      <div class="modal-content border-0 shadow">
        <div class="modal-header">
          <h5 class="modal-title" id="userModalLabel">Nuevo Usuario</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <form id="userForm" class="needs-validation" novalidate>
          <input type="hidden" id="fieldId" value="" />
          <div class="modal-body">
            <div class="row g-3">
              <div class="col-md-6">
                <label for="fieldNombres" class="form-label">Nombres *</label>
                <input type="text" class="form-control" id="fieldNombres" required minlength="2" />
                <div class="invalid-feedback">Ingresa los nombres.</div>
              </div>
              <div class="col-md-6">
                <label for="fieldApellidos" class="form-label">Apellidos *</label>
                <input type="text" class="form-control" id="fieldApellidos" required minlength="2" />
                <div class="invalid-feedback">Ingresa los apellidos.</div>
              </div>
              <div class="col-md-6">
                <label for="fieldIdentificacion" class="form-label">Identificación *</label>
                <input type="text" class="form-control" id="fieldIdentificacion" required />
                <div class="invalid-feedback">Ingresa el número de identificación.</div>
              </div>
              <div class="col-md-6">
                <label for="fieldTipoDoc" class="form-label">Tipo de documento *</label>
                <select class="form-select" id="fieldTipoDoc" required>
                  <option value="">Seleccione...</option>
                </select>
                <div class="invalid-feedback">Selecciona el tipo de documento.</div>
              </div>
              <div class="col-md-6">
                <label for="fieldEmail" class="form-label">Correo electrónico *</label>
                <input type="email" class="form-control" id="fieldEmail" required />
                <div class="invalid-feedback" id="fieldEmailFeedback">Correo electrónico no válido.</div>
              </div>
              <div class="col-md-6">
                <label for="fieldTelefono" class="form-label">Teléfono *</label>
                <input type="text" class="form-control" id="fieldTelefono" required />
                <div class="invalid-feedback">Ingresa el teléfono.</div>
              </div>
              <div class="col-md-6">
                <label for="fieldUsername" class="form-label">Usuario (login) *</label>
                <input type="text" class="form-control" id="fieldUsername" required minlength="3" />
                <div class="invalid-feedback" id="fieldUsernameFeedback">Ingresa un nombre de usuario válido.</div>
              </div>
              <div class="col-12">
                <label for="fieldDireccion" class="form-label">Dirección</label>
                <input type="text" class="form-control" id="fieldDireccion" />
              </div>
              <div class="col-md-4">
                <label for="fieldRole" class="form-label">Rol *</label>
                <select class="form-select" id="fieldRole" required>
                  <option value="">Seleccione...</option>
                </select>
                <div class="invalid-feedback">Selecciona un rol.</div>
              </div>
              <div class="col-md-4">
                <label for="fieldStatus" class="form-label">Estado *</label>
                <select class="form-select" id="fieldStatus" required>
                  <option value="">Seleccione...</option>
                  <option value="true">Activo</option>
                  <option value="false">Inactivo</option>
                </select>
                <div class="invalid-feedback">Selecciona un estado.</div>
              </div>
              <div class="col-md-4">
                <label for="fieldPassword" class="form-label">Contraseña <span id="fieldPasswordReq">*</span></label>
                <input type="password" class="form-control" id="fieldPassword" placeholder="Mínimo 6 caracteres" />
                <div class="form-text">En edición, dejar vacío para no cambiar.</div>
                <div class="invalid-feedback" id="fieldPasswordFeedback">La contraseña debe tener al menos 6 caracteres.</div>
              </div>
              <div class="col-md-6 d-none" id="fieldFichaWrap">
                <label for="fieldFicha" class="form-label">Ficha del aprendiz</label>
                <select class="form-select" id="fieldFicha">
                  <option value="">Sin asignar</option>
                </select>
                <div class="form-text">Visible solo para el rol Aprendiz. Determina el horario que verá en Programación.</div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
            <button type="submit" class="btn btn-brand" id="submitUserBtn">Crear Usuario</button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <!-- Modal Ver Usuario (solo lectura) -->
  <div class="modal fade" id="userViewModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
      <div class="modal-content border-0 shadow">
        <div class="modal-header">
          <h5 class="modal-title">Ver Usuario</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label fw-semibold text-muted">Nombre completo</label>
              <p id="viewNombre" class="fw-semibold mb-0">-</p>
            </div>
            <div class="col-md-6">
              <label class="form-label fw-semibold text-muted">Identificación</label>
              <p id="viewIdentificacion" class="fw-semibold mb-0">-</p>
            </div>
            <div class="col-md-6">
              <label class="form-label fw-semibold text-muted">Correo</label>
              <p id="viewCorreo" class="fw-semibold mb-0">-</p>
            </div>
            <div class="col-md-6">
              <label class="form-label fw-semibold text-muted">Teléfono</label>
              <p id="viewTelefono" class="fw-semibold mb-0">-</p>
            </div>
            <div class="col-md-6">
              <label class="form-label fw-semibold text-muted">Usuario (login)</label>
              <p id="viewUsername" class="fw-semibold mb-0">-</p>
            </div>
            <div class="col-md-6">
              <label class="form-label fw-semibold text-muted">Rol</label>
              <p id="viewRol" class="fw-semibold mb-0">-</p>
            </div>
            <div class="col-md-6">
              <label class="form-label fw-semibold text-muted">Ficha</label>
              <p id="viewFicha" class="fw-semibold mb-0">-</p>
            </div>
            <div class="col-md-6">
              <label class="form-label fw-semibold text-muted">Estado</label>
              <p id="viewEstado" class="fw-semibold mb-0">-</p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cerrar</button>
        </div>
      </div>
    </div>
  </div>

  <!-- Toast -->
  <div class="toast-container position-fixed bottom-0 end-0 p-3">
    <div id="appToast" class="toast align-items-center border-0" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="d-flex">
        <div class="toast-body" id="appToastBody"></div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>
  </div>

  <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
  <script src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <script src="JS/ClassControl_ui.js"></script>
  <script>
    // Solo el Administrador (3) gestiona usuarios; Coordinador solo visualiza.
    window.ccPuedeGestionarUsuarios = <%= (sesRol != null && sesRol == 3) %>;
  </script>
  <script src="JS/Gestion_UsuariosJS.js"></script>
  <script src="JS/ClassControl_theme.js"></script>
</body>
</html>
