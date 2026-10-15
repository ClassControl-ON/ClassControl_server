/**
 * ClassControl - Restablecer Contraseña
 * Valida que la nueva contraseña tenga al menos 8 caracteres y que ambas
 * coincidan antes de enviar al servlet /RestablecerPassword.
 */

(function () {
  'use strict';

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

  function validateClave() {
    const input = document.getElementById('clave');
    const err = document.getElementById('claveError');
    const val = input.value;
    if (!val) return marcar(input, err, true, 'La contraseña es obligatoria');
    if (val.length < 8) return marcar(input, err, true, 'Debe tener al menos 8 caracteres');
    return marcar(input, err, false, '');
  }

  function validateConfirmar() {
    const input = document.getElementById('confirmar');
    const err = document.getElementById('confirmarError');
    const clave = document.getElementById('clave').value;
    const val = input.value;
    if (!val) return marcar(input, err, true, 'Confirma tu contraseña');
    if (val !== clave) return marcar(input, err, true, 'Las contraseñas no coinciden');
    return marcar(input, err, false, '');
  }

  document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('resetForm');
    const clave = document.getElementById('clave');
    const confirmar = document.getElementById('confirmar');
    const btn = document.getElementById('submitBtn');

    if (!form) return;

    form.addEventListener('submit', function (e) {
      if (!validateClave() || !validateConfirmar()) {
        e.preventDefault();
        return;
      }
      if (btn) {
        btn.classList.add('loading');
        btn.disabled = true;
      }
    });

    if (clave) {
      clave.addEventListener('blur', validateClave);
      clave.addEventListener('input', function () {
        if (clave.classList.contains('is-invalid')) validateClave();
      });
    }
    if (confirmar) {
      confirmar.addEventListener('blur', validateConfirmar);
      confirmar.addEventListener('input', function () {
        if (confirmar.classList.contains('is-invalid')) validateConfirmar();
      });
    }
  });
})();
