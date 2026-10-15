<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Registro de Usuario — ClassControl (SENA)</title>

  <link rel="icon" type="image/png" href="img/logo.png" />
  <!-- ── Google Fonts (sistema de diseño ClassControl) ── -->
  <link rel="preconnect" href="https://fonts.googleapis.com"/>
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet"/>
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet"/>

  <!-- ── Bootstrap 5.3 ── -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>

  <!-- ── CSS global compartido ── -->
  <link rel="stylesheet" href="CSS/ClassControl_base.css"/>
  <link rel="stylesheet" href="CSS/ClassControl_theme.css"/>
  <link rel="stylesheet" href="CSS/Inicio_de_sesionCSS.css"/>

  <!-- ── CSS específico de esta pantalla ── -->
  <link rel="stylesheet" href="CSS/Registrar_usuarioCSS.css"/>
</head>

<body class="cc-reg-body">

  <%-- ════
       Leer parámetros de redirección del Servlet
       ?error=1        → fallo en BD
       ?error=formato  → fallo al parsear int
  ════ --%>
  <%
    String error = request.getParameter("error");
  %>

  <!-- ════ NAVBAR ════ -->
  <nav class="navbar cc-navbar sticky-top">
    <div class="container-fluid px-4">

      <!-- Brand -->
      <a class="navbar-brand cc-brand d-flex align-items-center gap-2" href="#">
        <div class="cc-brand-icon">
          <span class="material-symbols-outlined">school</span>
        </div>
        <div>
          <div class="cc-brand-name">ClassControl</div>
          <div class="cc-brand-sub">Gestión Académica SENA</div>
        </div>
      </a>

      <!-- Acciones -->
      <div class="d-flex align-items-center gap-3">
        <a href="Inicio_de_sesion.jsp" class="btn cc-btn-outline-light btn-sm">
          <span class="material-symbols-outlined fs-6 align-middle">login</span>
          ¿Ya tienes cuenta? Inicia sesión
        </a>
      </div>
    </div>
  </nav>


  <!-- ════ MAIN ════ -->
  <main class="cc-reg-main">
    <div class="cc-reg-card shadow">

      <!-- ── Sidebar / Steps ── -->
      <aside class="cc-reg-sidebar">
        <div class="cc-sidebar-intro">
          <span class="material-symbols-outlined cc-sidebar-big-icon">person_add</span>
          <h2>Registro de Usuarios</h2>
          <p>Ingrese los datos para dar de alta un nuevo integrante en la plataforma institucional.</p>
        </div>

        <div class="cc-steps">
          <div class="cc-step-item" id="step1">
            <div class="cc-step-icon cc-step-active">
              <span class="material-symbols-outlined">info</span>
            </div>
            <div>
              <div class="cc-step-title">Datos Personales</div>
              <div class="cc-step-sub">Información básica y contacto</div>
            </div>
          </div>

          <div class="cc-step-item" id="step2">
            <div class="cc-step-icon cc-step-inactive">
              <span class="material-symbols-outlined">encrypted</span>
            </div>
            <div>
              <div class="cc-step-title">Seguridad</div>
              <div class="cc-step-sub">Credenciales de acceso</div>
            </div>
          </div>

          <div class="cc-step-item" id="step3">
            <div class="cc-step-icon cc-step-inactive">
              <span class="material-symbols-outlined">badge</span>
            </div>
            <div>
              <div class="cc-step-title">Roles</div>
              <div class="cc-step-sub">Permisos en el sistema</div>
            </div>
          </div>
        </div>

        <div class="cc-sidebar-quote mt-auto">
          <blockquote class="cc-quote-box">
            "La formación profesional integral es el proceso educativo teórico-práctico de carácter integral."
          </blockquote>
        </div>
      </aside>


      <!-- ── Formulario ── -->
      <div class="cc-reg-form-section">

        <%-- Alertas del servidor --%>
        <% if ("1".equals(error)) { %>
          <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
            <span class="material-symbols-outlined me-2">warning</span>
            <strong>Error al registrar el usuario.</strong> Verifique los datos e intente de nuevo.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
          </div>
        <% } else if ("formato".equals(error)) { %>
          <div class="alert alert-warning alert-dismissible fade show mb-4" role="alert">
            <span class="material-symbols-outlined me-2">error</span>
            <strong>Error de formato.</strong> Uno o más campos numéricos tienen un valor incorrecto.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
          </div>
        <% } %>

        <%--
          action="RegistrarUsuario" → URL del @WebServlet("/RegistrarUsuario")
          method="post"             → invoca doPost()
          novalidate                → Bootstrap + JS manejan los estilos de validación
        --%>
        <form id="registerForm" action="${pageContext.request.contextPath}/RegistrarUsuario" method="post" novalidate>

          <!-- ─ Datos Personales ─ -->
          <div class="cc-form-section-title">
            <span class="material-symbols-outlined">person</span> Datos Personales
          </div>

          <div class="row g-3 mb-4">

            <%-- name="nombres" → request.getParameter("nombres") --%>
            <div class="col-12 col-sm-6">
              <label class="form-label" for="fNombres">Nombres <span class="text-danger">*</span></label>
              <input id="fNombres" name="nombres" type="text" class="form-control"
                    placeholder="Ej: Juan Alberto" autocomplete="given-name" required/>
              <div class="invalid-feedback" id="fNombresError">Mínimo 2 caracteres.</div>
            </div>

            <%-- name="apellidos" → request.getParameter("apellidos") --%>
            <div class="col-12 col-sm-6">
              <label class="form-label" for="fApellidos">Apellidos <span class="text-danger">*</span></label>
              <input id="fApellidos" name="apellidos" type="text" class="form-control"
                    placeholder="Ej: Pérez Castro" autocomplete="family-name" required/>
              <div class="invalid-feedback" id="fApellidosError">Mínimo 2 caracteres.</div>
            </div>

            <%--
              name="tipoDoc" → request.getParameter("tipoDoc") → Integer.parseInt()
              Los value son los IDs numéricos de la tabla Tipo_Documento.
              ⚠ Ajusta los números si tu BD usa IDs distintos.
            --%>
            <div class="col-12 col-sm-6">
              <label class="form-label" for="fTipoDoc">Tipo de Documento <span class="text-danger">*</span></label>
              <select id="fTipoDoc" name="tipoDoc" class="form-select" required>
                <option value="">— Seleccione tipo —</option>
                <option value="1">Cédula de Ciudadanía</option>
                <option value="2">Tarjeta de Identidad</option>
                <option value="3">Cédula de Extranjería</option>
                <option value="4">Pasaporte</option>
              </select>
              <div class="invalid-feedback" id="fTipoDocError">Seleccione un tipo.</div>
            </div>

            <%-- name="identificacion" → request.getParameter("identificacion") --%>
            <div class="col-12 col-sm-6">
              <label class="form-label" for="fDocumento">Número de Identificación <span class="text-danger">*</span></label>
              <input id="fDocumento" name="identificacion" type="text" class="form-control"
                    placeholder="Número de documento" inputmode="numeric" maxlength="12"
                    autocomplete="off" required/>
              <div class="invalid-feedback" id="fDocumentoError">Entre 6 y 12 dígitos numéricos.</div>
            </div>

            <%-- name="fecha_Nacimiento" → request.getParameter("fecha_Nacimiento") → LocalDate.parse() --%>
            <div class="col-12 col-sm-6">
              <label class="form-label" for="fFechaNacimiento">Fecha de Nacimiento <span class="text-danger">*</span></label>
              <input id="fFechaNacimiento" name="fecha_Nacimiento" type="date" class="form-control"
                    autocomplete="bday" required/>
              <div class="invalid-feedback" id="fFechaNacimientoError">Ingrese una fecha válida (debe ser mayor de 14 años).</div>
            </div>

            <%-- name="nivel_Educativo" → request.getParameter("nivel_Educativo") --%>
            <div class="col-12 col-sm-6">
              <label class="form-label" for="fNivelEducativo">Nivel Educativo <span class="text-danger">*</span></label>
              <select id="fNivelEducativo" name="nivel_Educativo" class="form-select" required>
                <option value="">— Seleccione nivel —</option>
                <option value="primaria">Primaria</option>
                <option value="bachillerato">Bachillerato</option>
                <option value="tecnico">Técnico</option>
                <option value="tecnologo">Tecnólogo</option>
                <option value="universitario">Universitario</option>
                <option value="especializacion">Especialización</option>
                <option value="maestria">Maestría</option>
                <option value="doctorado">Doctorado</option>
              </select>
              <div class="invalid-feedback" id="fNivelEducativoError">Seleccione un nivel educativo.</div>
            </div>

            <%-- name="profesion" → request.getParameter("profesion") --%>
            <div class="col-12 col-sm-6">
              <label class="form-label" for="fProfesion">Profesión</label>
              <input id="fProfesion" name="profesion" type="text" class="form-control"
                    placeholder="Ej: Ingeniero de Sistemas" autocomplete="organization-title" maxlength="100"/>
              <div class="invalid-feedback" id="fProfesionError">Máximo 100 caracteres.</div>
            </div>

          </div>


          <!-- ─ Contacto ─ -->
          <div class="cc-form-section-title">
            <span class="material-symbols-outlined">mail</span> Contacto
          </div>

          <div class="row g-3 mb-4">

            <%-- name="correo" → request.getParameter("correo") --%>
            <div class="col-12">
              <label class="form-label" for="fEmail">Correo Electrónico <span class="text-danger">*</span></label>
              <div class="input-group">
                <span class="input-group-text"><span class="material-symbols-outlined">mail</span></span>
                <input id="fEmail" name="correo" type="email" class="form-control"
                    placeholder="ejemplo@sena.edu.co" autocomplete="email" required/>
                <div class="invalid-feedback" id="fEmailError">Correo inválido.</div>
              </div>
            </div>

            <%-- name="telefono" → request.getParameter("telefono") --%>
            <div class="col-12 col-sm-6">
              <label class="form-label" for="fTelefono">Teléfono <span class="text-danger">*</span></label>
              <div class="input-group">
                <span class="input-group-text"><span class="material-symbols-outlined">call</span></span>
                <input id="fTelefono" name="telefono" type="tel" class="form-control"
                    placeholder="300 123 4567" autocomplete="tel" required/>
                <div class="invalid-feedback" id="fTelefonoError">Teléfono inválido.</div>
              </div>
            </div>

            <%-- name="direccion" → request.getParameter("direccion") --%>
            <div class="col-12">
              <label class="form-label" for="fDireccion">Dirección</label>
              <div class="input-group">
                <span class="input-group-text"><span class="material-symbols-outlined">location_on</span></span>
                <input id="fDireccion" name="direccion" type="text" class="form-control"
                    placeholder="Ej: Cra 10 # 20-30, Bogotá" autocomplete="street-address" maxlength="150"/>
                <div class="invalid-feedback" id="fDireccionError">Máximo 150 caracteres.</div>
              </div>
            </div>

            <%--
              El registro público solo permite los roles Instructor y Aprendiz.
              El rol Administrador se asigna desde la gestión interna de usuarios.
            --%>
            <div class="col-12 col-sm-6">
              <label class="form-label" for="fRol">Rol del Usuario <span class="text-danger">*</span></label>
              <select id="fRol" name="rol" class="form-select" required>
                <option value="">— Seleccione rol —</option>
                <option value="1">Instructor</option>
                <option value="2">Aprendiz</option>
              </select>
              <div class="invalid-feedback" id="fRolError">Seleccione un rol.</div>
            </div>

          </div>


          <!-- ─ Credenciales ─ -->
          <div class="cc-form-section-title">
            <span class="material-symbols-outlined">lock</span> Credenciales de Acceso
          </div>

          <div class="row g-3 mb-4">

            <%-- name="username" → request.getParameter("username") --%>
            <div class="col-12">
              <label class="form-label" for="fUsername">Nombre de Usuario <span class="text-danger">*</span></label>
              <div class="input-group">
                <span class="input-group-text"><span class="material-symbols-outlined">alternate_email</span></span>
                <input id="fUsername" name="username" type="text" class="form-control"
                    placeholder="Ej: jperez01" maxlength="20" autocomplete="username"
                    spellcheck="false" required/>
                <div class="invalid-feedback" id="fUsernameError">4-20 caracteres: letras minúsculas, números o _</div>
              </div>
            </div>

            <%-- name="clave" → request.getParameter("clave") --%>
            <div class="col-12 col-sm-6">
              <label class="form-label" for="fPassword">Contraseña <span class="text-danger">*</span></label>
              <div class="input-group">
                <span class="input-group-text"><span class="material-symbols-outlined">lock</span></span>
                <input id="fPassword" name="clave" type="password" class="form-control"
                    placeholder="••••" autocomplete="new-password" required/>
                <button type="button" class="input-group-text cc-toggle-pass"
                    data-target="fPassword" aria-label="Mostrar contraseña">
                  <span class="material-symbols-outlined">visibility</span>
                </button>
                <div class="invalid-feedback" id="fPasswordError">Mínimo 8 caracteres.</div>
              </div>
              <!-- Barra de fortaleza -->
              <div id="pwStrength" class="cc-pw-strength d-none mt-2">
                <div class="cc-pw-bars d-flex gap-1 mb-1">
                  <div class="cc-pw-bar flex-fill"></div>
                  <div class="cc-pw-bar flex-fill"></div>
                  <div class="cc-pw-bar flex-fill"></div>
                  <div class="cc-pw-bar flex-fill"></div>
                  <div class="cc-pw-bar flex-fill"></div>
                </div>
                <small id="pwLabel" class="text-muted"></small>
              </div>
            </div>

            <%--
              SIN name → no viaja al servidor.
              Solo existe para la validación cruzada en el cliente (JS).
            --%>
            <div class="col-12 col-sm-6">
              <label class="form-label" for="fConfirm">Confirmar Contraseña <span class="text-danger">*</span></label>
              <div class="input-group">
                <span class="input-group-text"><span class="material-symbols-outlined">lock</span></span>
                <input id="fConfirm" type="password" class="form-control"
                    placeholder="••••" autocomplete="new-password" required/>
                <button type="button" class="input-group-text cc-toggle-pass"
                    data-target="fConfirm" aria-label="Mostrar contraseña">
                  <span class="material-symbols-outlined">visibility</span>
                </button>
                <div class="invalid-feedback" id="fConfirmError">Las contraseñas no coinciden.</div>
              </div>
            </div>

          </div>


          <!-- ─ Acciones ─ -->
          <div class="d-flex gap-3 align-items-center pt-2">
            <button id="submitBtn" type="submit" class="btn cc-btn-submit flex-fill">
              <span class="spinner-border spinner-border-sm d-none me-2" id="submitSpinner"
                    role="status" aria-hidden="true"></span>
              <span class="material-symbols-outlined me-1" id="submitIcon">person_add</span>
              <span id="submitText">Crear Usuario</span>
            </button>
            <button id="cancelBtn" type="button" class="btn cc-btn-cancel">
              Cancelar
            </button>
          </div>

        </form>
      </div><!-- /cc-reg-form-section -->

    </div><!-- /cc-reg-card -->
  </main>


  <!-- ════ FOOTER ════ -->
  <footer class="cc-reg-footer text-center">
    <p class="mb-0">© 2024 ClassControl (SENA) — Sistema de Control de Formación Profesional</p>
  </footer>


  <!-- ════ TOAST ════ -->
  <div id="toast" class="cc-toast d-flex align-items-center gap-2" role="alert" aria-live="polite">
    <span id="toastIcon" class="material-symbols-outlined cc-toast-icon">check_circle</span>
    <span class="cc-toast-msg"></span>
  </div>


  <!-- ── Bootstrap JS ── -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <!-- ── Lógica del cliente ── -->
  <script src="JS/ClassControl_ui.js"></script>
  <script src="JS/Registrar_usuarioJS.js"></script>
</body>
</html>
