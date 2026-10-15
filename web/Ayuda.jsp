<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es" data-bs-theme="light">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>ClassControl - Centro de Ayuda</title>

  <link rel="icon" type="image/png" href="img/logo.png" />
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet" />
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet" />
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />

  <link rel="stylesheet" href="CSS/ClassControl_base.css"/>
  <link rel="stylesheet" href="CSS/AyudaCSS.css" />
</head>

<body class="cc-help-body">

  <!-- HEADER -->
  <header class="cc-help-topbar">
    <div class="cc-help-topbar-inner">
      <div class="d-flex align-items-center gap-2">
        <img src="img/logo.png" alt="Logo" style="width:32px;height:32px;border-radius:6px;" />
        <span class="cc-help-brand">ClassControl</span>
      </div>
      <a href="Inicio_de_sesion.jsp" class="cc-help-back">
        <span class="material-symbols-outlined">arrow_back</span>
        <span>Volver al login</span>
      </a>
    </div>
  </header>

  <!-- CONTENIDO -->
  <div class="cc-help-layout">

    <!-- MENU IZQUIERDA -->
    <nav class="cc-help-menu">
      <h4 class="cc-help-menu-title">Temas de ayuda</h4>
      <button class="cc-help-menu-item active" data-section="login">
        <span class="material-symbols-outlined">login</span>
        <span>Iniciar Sesion</span>
      </button>
      <button class="cc-help-menu-item" data-section="registro">
        <span class="material-symbols-outlined">person_add</span>
        <span>Crear una Cuenta</span>
      </button>
      <button class="cc-help-menu-item" data-section="general">
        <span class="material-symbols-outlined">explore</span>
        <span>Conoce ClassControl</span>
      </button>
      <button class="cc-help-menu-item" data-section="app">
        <span class="material-symbols-outlined">phone_android</span>
        <span>Descargar App</span>
      </button>
    </nav>

    <!-- CONTENIDO DERECHA -->
    <main class="cc-help-content">

      <!-- SECCION: Iniciar Sesion -->
      <section class="cc-help-section" id="section-login">
        <h2 class="cc-help-section-title">Iniciar Sesion</h2>
        <p class="cc-help-section-desc">Accede a tu cuenta con usuario y contrasena</p>

        <div class="cc-video-wrap">
          <div class="cc-video-placeholder">
            <span class="material-symbols-outlined">play_circle</span>
            <span>Video proximamente</span>
          </div>
          <!--
          <iframe src="https://www.youtube.com/embed/VIDEO_ID_AQUI"
                  title="Como iniciar sesion en ClassControl"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                  allowfullscreen></iframe>
          -->
        </div>

        <h4 class="mt-4 mb-2 fw-bold" style="font-size:.95rem;">Pasos para iniciar sesion:</h4>
        <ol class="cc-help-steps">
          <li>Abre la pagina de ClassControl en tu navegador.</li>
          <li>En la pantalla de inicio de sesion, ingresa tu <strong>correo electronico</strong> o <strong>nombre de usuario</strong> en el primer campo.</li>
          <li>En el segundo campo, escribe tu <strong>contrasena</strong>. Pulsar el icono de ojo para mostrarla u ocultarla.</li>
          <li>Si deseas que el sistema te recuerde en este dispositivo, marca la casilla <strong>Recordarme</strong>.</li>
          <li>Haz clic en el boton verde <strong>Iniciar sesion</strong>.</li>
          <li>Si los datos son correctos, seras redirigido al <strong>Dashboard Principal</strong>.</li>
        </ol>

        <div class="cc-help-tip">
          <span class="material-symbols-outlined">lightbulb</span>
          <span>Si olvidaste tu contrasena, haz clic en <a href="Recuperar_Contraseña.jsp" class="cc-help-link">Olvidaste tu contrasena?</a> en la pantalla de inicio de sesion. Recibiras un correo con las instrucciones para restablecerla.</span>
        </div>
      </section>

      <!-- SECCION: Crear una Cuenta -->
      <section class="cc-help-section d-none" id="section-registro">
        <h2 class="cc-help-section-title">Crear una Cuenta</h2>
        <p class="cc-help-section-desc">Registrate como usuario nuevo en el sistema</p>

        <div class="cc-video-wrap">
          <div class="cc-video-placeholder">
            <span class="material-symbols-outlined">play_circle</span>
            <span>Video proximamente</span>
          </div>
          <!--
          <iframe src="https://www.youtube.com/embed/VIDEO_ID_AQUI"
                  title="Como crear una cuenta en ClassControl"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                  allowfullscreen></iframe>
          -->
        </div>

        <h4 class="mt-4 mb-2 fw-bold" style="font-size:.95rem;">Pasos para registrarte:</h4>
        <ol class="cc-help-steps">
          <li>En la pantalla de inicio de sesion, haz clic en <strong>Registrarse como nuevo usuario</strong> en la parte inferior.</li>
          <li>Completa tus <strong>datos personales</strong>: nombres, apellidos, tipo y numero de documento, fecha de nacimiento y nivel educativo.</li>
          <li>Ingresa tu <strong>correo electronico</strong>, telefono y direccion de contacto.</li>
          <li>Selecciona tu <strong>rol</strong>: Instructor o Aprendiz.</li>
          <li>Crea tu <strong>nombre de usuario</strong> (entre 4 y 20 caracteres, solo minusculas, numeros o guion bajo).</li>
          <li>Crea y confirma tu <strong>contrasena</strong> (minimo 8 caracteres).</li>
          <li>Haz clic en <strong>Crear Usuario</strong>. Si todo sale bien, seras redirigido a la pantalla de inicio de sesion.</li>
        </ol>

        <div class="cc-help-tip">
          <span class="material-symbols-outlined">lightbulb</span>
          <span>Desde el registro publico solo puedes crear usuarios con rol <strong>Instructor</strong> o <strong>Aprendiz</strong>. El rol de <strong>Administrador</strong> solo puede ser asignado por un administrador desde la seccion de Gestion de Usuarios.</span>
        </div>
      </section>

      <!-- SECCION: Conoce ClassControl -->
      <section class="cc-help-section d-none" id="section-general">
        <h2 class="cc-help-section-title">Conoce ClassControl</h2>
        <p class="cc-help-section-desc">Vista general del sistema y sus funcionalidades</p>

        <div class="cc-video-wrap">
          <div class="cc-video-placeholder">
            <span class="material-symbols-outlined">play_circle</span>
            <span>Video proximamente</span>
          </div>
          <!--
          <iframe src="https://www.youtube.com/embed/VIDEO_ID_AQUI"
                  title="Conoce ClassControl - Vista general"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                  allowfullscreen></iframe>
          -->
        </div>

        <h4 class="mt-4 mb-2 fw-bold" style="font-size:.95rem;">Que es ClassControl?</h4>
        <p style="font-size:.9rem; color:var(--cc-text); line-height:1.6;">
          <strong>ClassControl</strong> es una plataforma web y movil diseñada para la gestion de la programacion
          de instructores en el SENA. Permite administrar fichas de formacion, ambientes, actividades,
          horarios, competencias y reportes de incidencias desde un solo lugar.
        </p>

        <h4 class="mt-3 mb-2 fw-bold" style="font-size:.95rem;">Secciones principales:</h4>
        <ol class="cc-help-steps">
          <li><strong>Dashboard (Inicio)</strong> &mdash; Panel principal con estadisticas: fichas activas, ambientes ocupados, actividades en curso e instructores activos.</li>
          <li><strong>Fichas</strong> &mdash; Consulta y gestiona las fichas de formacion.</li>
          <li><strong>Instructores</strong> &mdash; Informacion de contacto de los instructores registrados.</li>
          <li><strong>Programas</strong> &mdash; Catalogo de programas de formacion (solo administradores).</li>
          <li><strong>Ambientes</strong> &mdash; Gestion de espacios fisicos: laboratorios, salones y su capacidad.</li>
          <li><strong>Competencias</strong> &mdash; Consulta de competencias y resultados de aprendizaje.</li>
          <li><strong>Actividades</strong> &mdash; Administracion de actividades asociadas a las competencias.</li>
          <li><strong>Programacion</strong> &mdash; Asignacion de horarios, ambientes y actividades a instructores por ficha y trimestre.</li>
          <li><strong>Incidencias</strong> &mdash; Registro y seguimiento de reportes e incidencias.</li>
          <li><strong>Usuarios</strong> &mdash; Gestion de cuentas de usuarios del sistema (solo administradores).</li>
        </ol>

        <div class="cc-help-tip">
          <span class="material-symbols-outlined">lightbulb</span>
          <span>El contenido que ves varia segun tu <strong>rol</strong>. Un Aprendiz solo ve su horario, mientras que un Administrador tiene acceso completo.</span>
        </div>
      </section>

      <!-- SECCION: Descargar App -->
      <section class="cc-help-section d-none" id="section-app">
        <h2 class="cc-help-section-title">Descargar App</h2>
        <p class="cc-help-section-desc">Obtén la aplicacion movil de ClassControl en tu dispositivo</p>

        <div class="cc-download-card">
          <div class="cc-qr-box">
            <div id="cc-qrcode"></div>
            <span class="cc-qr-hint">Escanea con tu telefono</span>
          </div>
          <div class="cc-download-info">
            <span class="material-symbols-outlined cc-download-icon">smartphone</span>
            <h4>App movil ClassControl</h4>
            <p>Escanea el codigo QR con la camara de tu telefono para descargar e instalar la ultima version de la aplicacion movil de ClassControl.</p>
            <p class="cc-download-note">Esta disponible para dispositivos Android. Instala el archivo <strong>.apk</strong> que se descargara automaticamente.</p>
            <a id="cc-download-link" class="cc-btn-download" href="#" target="_blank" rel="noopener">
              <span class="material-symbols-outlined">download</span>
              <span>Descargar APK</span>
            </a>
          </div>
        </div>

        <div class="cc-help-tip">
          <span class="material-symbols-outlined">lightbulb</span>
          <span>Para instalar el APK, en tu telefono puede ser necesario habilitar la opcion <strong>Origenes desconocidos</strong> dentro de los ajustes de seguridad.</span>
        </div>
      </section>

    </main>
  </div>

  <script src="JS/qrcode.min.js"></script>
  <script>
    // APK_URL: reemplaza por la URL de descarga de tu ultima version del APK
    // (ej: https://github.com/TU_USUARIO/TU_REPO/releases/latest/download/app.apk)
    var APK_URL = 'https://github.com/Stivxn-n/classcontrol-apk/releases/latest/download/ClassControl.apk';

    document.querySelectorAll('.cc-help-menu-item').forEach(function(btn) {
      btn.addEventListener('click', function() {
        document.querySelectorAll('.cc-help-menu-item').forEach(function(b) { b.classList.remove('active'); });
        document.querySelectorAll('.cc-help-section').forEach(function(s) { s.classList.add('d-none'); });
        btn.classList.add('active');
        document.getElementById('section-' + btn.dataset.section).classList.remove('d-none');
      });
    });

    new QRCode(document.getElementById('cc-qrcode'), {
      text: APK_URL,
      width: 190,
      height: 190,
      colorDark: '#0f172a',
      colorLight: '#ffffff'
    });

    document.getElementById('cc-download-link').setAttribute('href', APK_URL);
  </script>
</body>
</html>
