<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%!
  public static String escaparHtml(Object valor) {
    if (valor == null) return "";
    String t = String.valueOf(valor);
    return t.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
            .replace("\"","&quot;").replace("'","&#39;");
  }
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Nueva contraseña - ClassControl (SENA)</title>

  <link rel="icon" type="image/png" href="img/logo.png" />
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=DM+Mono:wght@400;500&display=swap" />
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" />

  <link rel="stylesheet" href="CSS/ClassControl_base.css" />
  <link rel="stylesheet" href="CSS/Recuperar_ContraseñaCSS.css" />
  <link rel="stylesheet" href="CSS/ClassControl_theme.css" />
</head>

<body>
  <div class="blob blob-tl"></div>
  <div class="blob blob-br"></div>

  <div class="page-wrapper">

    <div class="brand">
      <div class="brand-icon">
        <span class="material-symbols-outlined" style="font-size:34px">key</span>
      </div>
      <div class="brand-name">ClassControl <span>SENA</span></div>
    </div>

    <div class="card">

      <% String invalido = (String) request.getAttribute("invalido");
         String mensaje  = (String) request.getAttribute("mensaje");
         Boolean exito   = (Boolean) request.getAttribute("exito");
         String token    = (String) request.getAttribute("token");
      %>

      <% if (invalido != null) { %>
        <!-- Enlace inválido o vencido -->
        <div class="card-header">
          <div class="header-icon">
            <span class="material-symbols-outlined">link_off</span>
          </div>
          <h2>Enlace no válido</h2>
          <p><%= escaparHtml(invalido) %></p>
        </div>
        <a class="back-link" style="margin:0 auto" href="Recuperar_Contraseña.jsp">
          <span class="material-symbols-outlined">arrow_back</span>
          Solicitar un nuevo enlace
        </a>

      <% } else if (exito != null && exito) { %>
        <!-- Contraseña actualizada -->
        <div class="card-header">
          <div class="header-icon">
            <span class="material-symbols-outlined">check_circle</span>
          </div>
          <h2>Contraseña actualizada</h2>
          <p>Tu contraseña se cambió correctamente. Ya puedes iniciar sesión con ella.</p>
        </div>
        <a class="back-link" style="margin:0 auto" href="Inicio_de_sesion.jsp">
          <span class="material-symbols-outlined">login</span>
          Ir a iniciar sesión
        </a>

      <% } else { %>
        <!-- Formulario nueva contraseña -->
        <div class="card-header">
          <div class="header-icon">
            <span class="material-symbols-outlined">password</span>
          </div>
          <h2>Elige una nueva contraseña</h2>
          <p>Debe tener al menos 8 caracteres.</p>
        </div>

        <% if (mensaje != null) { %>
          <div class="success-panel visible" style="margin-bottom:16px;flex-direction:row">
            <span class="material-symbols-outlined" style="color:#ef4444">error</span>
            <span style="text-align:left;font-size:0.8125rem"><%= escaparHtml(mensaje) %></span>
          </div>
        <% } %>

        <form id="resetForm" class="recovery-form" action="RestablecerPassword" method="post" novalidate>
          <input type="hidden" name="token" value="<%= escaparHtml(token) %>" />

          <div class="form-group">
            <label for="clave">Nueva contraseña</label>
            <div class="input-wrap">
              <span class="material-symbols-outlined field-icon">lock</span>
              <input id="clave" name="clave" type="password"
                     placeholder="Mínimo 8 caracteres" autocomplete="new-password" required />
            </div>
            <span id="claveError" class="field-error">
              <span class="material-symbols-outlined" style="font-size:13px">error</span><span></span>
            </span>
          </div>

          <div class="form-group">
            <label for="confirmar">Confirmar contraseña</label>
            <div class="input-wrap">
              <span class="material-symbols-outlined field-icon">lock</span>
              <input id="confirmar" name="confirmar" type="password"
                     placeholder="Repite la contraseña" autocomplete="new-password" required />
            </div>
            <span id="confirmarError" class="field-error">
              <span class="material-symbols-outlined" style="font-size:13px">error</span><span></span>
            </span>
          </div>

          <button id="submitBtn" type="submit" class="btn-submit" style="margin-top:8px">
            <div class="btn-spinner"></div>
            <span class="btn-text">Restablecer contraseña</span>
            <span class="material-symbols-outlined btn-arrow">key</span>
          </button>
        </form>

        <hr class="card-divider" />
        <a class="back-link" style="margin:0 auto" href="Inicio_de_sesion.jsp">
          <span class="material-symbols-outlined">arrow_back</span>
          Volver a iniciar sesión
        </a>
      <% } %>

    </div>

    <div class="footer-links">
      <a href="#">Privacidad</a>
      <a href="#">Términos</a>
      <a href="#">Soporte</a>
    </div>

  </div>

  <div id="toast" class="toast" role="alert" aria-live="polite">
    <span class="material-symbols-outlined t-icon">check_circle</span>
    <span class="t-msg"></span>
  </div>

  <script src="JS/ClassControl_ui.js"></script>
  <script src="JS/Restablecer_ContraseñaJS.js"></script>
</body>
</html>
