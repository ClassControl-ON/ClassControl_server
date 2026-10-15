/**
 * ClassControl - Recuperar Contraseña (flujo por correo)
 *
 * Envía correo (validado como email) + documento a /RecuperarPassword.
 * El backend solo envía el enlace de restablecimiento si el correo está
 * registrado en la base de datos y la cuenta está activa.
 */

/* ============================================================
   Helpers
   ============================================================ */

function showToast(message, type = 'success') {
  const toast = document.getElementById('toast');
  toast.querySelector('.t-msg').textContent = message;
  toast.querySelector('.t-icon').textContent =
      type === 'success' ? 'check_circle' : 'error';
  toast.className = `toast ${type} show`;
  clearTimeout(toast._timer);
  toast._timer = setTimeout(() => toast.classList.remove('show'), 4000);
}

function marcar(input, errorEl, invalido, mensaje) {
  input.classList.toggle('is-invalid', invalido);
  input.classList.toggle('is-valid', !invalido && input.value.trim() !== '');
  if (invalido) {
    errorEl.querySelector('span:last-child').textContent = mensaje;
    errorEl.classList.add('visible');
  } else {
    errorEl.classList.remove('visible');
  }
  return !invalido;
}

function esCorreoValido(val) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(val);
}

function validateEmail() {
  const input = document.getElementById('email');
  const errorEl = document.getElementById('emailError');
  const val = input.value.trim();
  if (!val) return marcar(input, errorEl, true, 'El correo es obligatorio');
  if (!esCorreoValido(val)) return marcar(input, errorEl, true, 'Ingresa un correo válido (ej. usuario@sena.edu.co)');
  return marcar(input, errorEl, false, '');
}

function validateDocumento() {
  const input = document.getElementById('documento');
  const errorEl = document.getElementById('documentoError');
  const val = input.value.trim();
  if (!val) return marcar(input, errorEl, true, 'El documento es obligatorio');
  return marcar(input, errorEl, false, '');
}

/* ============================================================
   Pantallas
   ============================================================ */

function mostrarPanel(idPanel) {
  document.getElementById('recoveryForm').style.display = 'none';
  document.getElementById('successPanel').style.display = 'none';
  document.getElementById('errorPanel').style.display = 'none';
  const panel = document.getElementById(idPanel);
  if (panel) panel.style.display = 'flex';
}

/* ============================================================
   Envío al backend
   ============================================================ */

async function enviarSolicitud() {
  const correo = document.getElementById('email').value.trim();
  const documento = document.getElementById('documento').value.trim();
  const btn = document.getElementById('submitBtn');
  btn.classList.add('loading');
  btn.disabled = true;

  let data = {};
  let ok = false;
  try {
    const res = await fetch('/RecuperarPassword', {
      method: 'POST',
      headers: { 'Accept': 'application/json' },
      body: new URLSearchParams({ correo: correo, identificacion: documento })
    });
    ok = res.ok;
    try { data = await res.json(); } catch (_) {}
  } catch (err) {
    ok = false;
    data.error = 'Error de conexión con el servidor.';
  } finally {
    btn.classList.remove('loading');
    btn.disabled = false;
  }

  if (ok && data.ok) {
    mostrarPanel('successPanel');
    showToast('Solicitud enviada. Revisa tu correo.', 'success');
    iniciarReenvio();
  } else {
    document.getElementById('errorMsg').textContent =
        data.error || 'No se pudo procesar la solicitud. Intenta de nuevo.';
    mostrarPanel('errorPanel');
    showToast(data.error || 'No se pudo procesar la solicitud.', 'error');
  }
}

/* ============================================================
   Reenvío (enlace "¿No lo recibiste?")
   ============================================================ */

function iniciarReenvio() {
  const link = document.getElementById('resendLink');
  link.classList.remove('visible');
  link.classList.add('antiflood');
  setTimeout(() => {
    link.classList.add('visible');
    link.classList.remove('antiflood');
  }, 30000);
}

document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('recoveryForm');
  const email = document.getElementById('email');
  const documento = document.getElementById('documento');
  const resendLink = document.getElementById('resendLink');

  if (form) form.addEventListener('submit', (e) => {
    e.preventDefault();
    if (!validateEmail()) return;
    if (!validateDocumento()) return;
    enviarSolicitud();
  });

  if (resendLink) resendLink.addEventListener('click', () => {
    if (resendLink.classList.contains('antiflood')) {
      showToast('Espera unos segundos antes de reenviar.', 'error');
      return;
    }
    enviarSolicitud();
  });

  if (email) {
    email.addEventListener('blur', validateEmail);
    email.addEventListener('input', () => {
      if (email.classList.contains('is-invalid')) validateEmail();
    });
  }

  if (documento) {
    documento.addEventListener('blur', validateDocumento);
    documento.addEventListener('input', () => {
      if (documento.classList.contains('is-invalid')) validateDocumento();
    });
  }
});
