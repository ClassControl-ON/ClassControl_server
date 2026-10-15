/**
 * Registrar_usuarioJS.js — ClassControl
 *
 * - Validación Bootstrap 5 (is-invalid / is-valid)
 * - Barra de fortaleza de contraseña
 * - Toggle mostrar/ocultar contraseña
 * - Indicador de pasos (sidebar)
 * - Toast y spinner de envío
 */

'use strict';

/* ============================================================
   Utilidades
   ============================================================ */

function showToast(message, type = 'success') {
  const toast = document.getElementById('toast');
  const icon  = document.getElementById('toastIcon');
  if (!toast) return;

  toast.querySelector('.cc-toast-msg').textContent = message;
  icon.className = type === 'success'
    ? 'material-symbols-outlined cc-toast-icon text-success'
    : 'material-symbols-outlined cc-toast-icon text-danger';
  icon.textContent = type === 'success' ? 'check_circle' : 'error';

  toast.className = `cc-toast d-flex align-items-center gap-2 ${type} show`;

  clearTimeout(toast._timer);
  toast._timer = setTimeout(() => toast.classList.remove('show'), 3800);
}

function setFieldError(fieldId, message) {
  const field = document.getElementById(fieldId);
  const error = document.getElementById(fieldId + 'Error');
  if (!field) return;
  field.classList.add('is-invalid');
  field.classList.remove('is-valid');
  if (error) error.textContent = message;
}

function setFieldValid(fieldId) {
  const field = document.getElementById(fieldId);
  if (!field) return;
  field.classList.remove('is-invalid');
  field.classList.add('is-valid');
}

function clearField(fieldId) {
  const field = document.getElementById(fieldId);
  if (!field) return;
  field.classList.remove('is-invalid', 'is-valid');
}

/* ============================================================
   Reglas de validación
   ============================================================ */

const VALIDATORS = {
  fNombres:   v => v.trim().length >= 2                          ? null : 'Mínimo 2 caracteres',
  fApellidos: v => v.trim().length >= 2                          ? null : 'Mínimo 2 caracteres',
  fTipoDoc:   v => v !== ''                                      ? null : 'Seleccione un tipo',
  fDocumento: v => /^\d{6,12}$/.test(v.trim())                  ? null : 'Entre 6 y 12 dígitos numéricos',
  fFechaNacimiento: v => {
    if (!v) return 'Ingrese una fecha válida (debe ser mayor de 14 años)';
    const birth = new Date(v);
    const today = new Date();
    const age = today.getFullYear() - birth.getFullYear() -
      (today < new Date(today.getFullYear(), birth.getMonth(), birth.getDate()) ? 1 : 0);
    return age >= 14 ? null : 'Debe ser mayor de 14 años';
  },
  fNivelEducativo: v => v !== ''                                 ? null : 'Seleccione un nivel educativo',
  fProfesion: v => v.trim().length <= 100                        ? null : 'Máximo 100 caracteres',
  fEmail:     v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v.trim()) ? null : 'Correo inválido',
  fTelefono:  v => /^[0-9\s+\-]{7,15}$/.test(v.trim())         ? null : 'Teléfono inválido',
  fRol:       v => v !== ''                                      ? null : 'Seleccione un rol',
  fUsername:  v => /^[a-z0-9_]{4,20}$/.test(v.trim())          ? null : '4-20 caracteres: letras minúsculas, números o _',
  fPassword:  v => v.length >= 8                                 ? null : 'Mínimo 8 caracteres',
  fConfirm:   v => {
    const pw = document.getElementById('fPassword');
    return (pw && v === pw.value) ? null : 'Las contraseñas no coinciden';
  },
};

function validateField(id) {
  const field = document.getElementById(id);
  if (!field) return true;
  const rule  = VALIDATORS[id];
  if (!rule)  return true;
  const error = rule(field.value);
  if (error) { setFieldError(id, error); return false; }
  setFieldValid(id);
  return true;
}

function validateAll() {
  return Object.keys(VALIDATORS)
    .map(id => validateField(id))
    .every(Boolean);
}

/* ============================================================
   Fortaleza de contraseña
   ============================================================ */

function getPasswordStrength(pw) {
  let score = 0;
  if (pw.length >= 8)           score++;
  if (pw.length >= 12)          score++;
  if (/[A-Z]/.test(pw))        score++;
  if (/[0-9]/.test(pw))        score++;
  if (/[^a-zA-Z0-9]/.test(pw)) score++;
  return score; // 0-5
}

function updateStrengthBar(pw) {
  const wrap  = document.getElementById('pwStrength');
  const bars  = wrap?.querySelectorAll('.cc-pw-bar');
  const label = document.getElementById('pwLabel');
  if (!wrap || !bars || !label) return;

  if (!pw) { wrap.classList.add('d-none'); return; }
  wrap.classList.remove('d-none');

  const score = getPasswordStrength(pw);
  const levels = [
    { cls: '',       text: '' },
    { cls: 'weak',   text: 'Muy débil' },
    { cls: 'weak',   text: 'Débil' },
    { cls: 'fair',   text: 'Regular' },
    { cls: 'strong', text: 'Buena' },
    { cls: 'strong', text: 'Muy fuerte' },
  ];
  const level = levels[Math.min(score, 5)];

  bars.forEach((bar, i) => {
    bar.className = 'cc-pw-bar flex-fill';
    if (i < score) bar.classList.add(level.cls);
  });
  label.textContent = level.text;
}

/* ============================================================
   Toggle contraseña
   ============================================================ */

function initPasswordToggles() {
  document.querySelectorAll('.cc-toggle-pass').forEach(btn => {
    btn.addEventListener('click', () => {
      const target = document.getElementById(btn.dataset.target);
      if (!target) return;
      const visible  = target.type === 'password';
      target.type    = visible ? 'text' : 'password';
      const icon     = btn.querySelector('.material-symbols-outlined');
      if (icon) icon.textContent = visible ? 'visibility_off' : 'visibility';
    });
  });
}

/* ============================================================
   Pasos del sidebar
   ============================================================ */

const STEP_FIELDS = {
  step1: ['fNombres', 'fApellidos', 'fTipoDoc', 'fDocumento', 'fFechaNacimiento', 'fNivelEducativo', 'fEmail', 'fTelefono'],
  step2: ['fUsername', 'fPassword', 'fConfirm'],
  step3: ['fRol'],
};

function updateSteps() {
  Object.entries(STEP_FIELDS).forEach(([stepId, fields]) => {
    const el = document.getElementById(stepId);
    if (!el) return;

    const allDone = fields.every(id => {
      const f = document.getElementById(id);
      return f && f.value.trim() !== '' && !f.classList.contains('is-invalid');
    });

    const iconEl = el.querySelector('.cc-step-icon');
    const iEl    = iconEl?.querySelector('.material-symbols-outlined');
    if (!iconEl) return;

    if (allDone) {
      iconEl.className = 'cc-step-icon cc-step-done';
      if (iEl) { iEl.className = 'material-symbols-outlined'; iEl.textContent = 'check_circle'; }
    } else {
      // Restaurar ícono original si no está completo
      if (iconEl.classList.contains('cc-step-done')) {
        iconEl.className = 'cc-step-icon cc-step-inactive';
        if (iEl) {
          const icons = { step1: 'info', step2: 'encrypted', step3: 'badge' };
          iEl.className = 'material-symbols-outlined';
          iEl.textContent = icons[stepId] || 'circle';
        }
      }
    }
  });
}

/* ============================================================
   Validación en tiempo real
   ============================================================ */

function initLiveValidation() {
  Object.keys(VALIDATORS).forEach(id => {
    const field = document.getElementById(id);
    if (!field) return;

    field.addEventListener('blur', () => validateField(id));

    field.addEventListener('input', () => {
      if (field.classList.contains('is-invalid')) validateField(id);
      updateSteps();

      if (id === 'fPassword') {
        updateStrengthBar(field.value);
        // Re-validar confirm si ya tiene valor
        const confirm = document.getElementById('fConfirm');
        if (confirm?.value) validateField('fConfirm');
      }
    });
  });
}

/* ============================================================
   Estado de carga del botón
   ============================================================ */

function setSubmitLoading(loading) {
  const btn     = document.getElementById('submitBtn');
  const spinner = document.getElementById('submitSpinner');
  const icon    = document.getElementById('submitIcon');
  const text    = document.getElementById('submitText');
  if (!btn) return;

  btn.disabled = loading;
  spinner?.classList.toggle('d-none', !loading);
  icon?.classList.toggle('d-none', loading);
  if (text) text.textContent = loading ? 'Creando usuario…' : 'Crear Usuario';
}

/* ============================================================
   Submit
   ============================================================ */

function handleSubmit(e) {
  if (!validateAll()) {
    e.preventDefault(); // Solo cancelar el envío si hay errores de validación
    const first = document.querySelector('.is-invalid');
    if (first) first.scrollIntoView({ behavior: 'smooth', block: 'center' });
    showToast('Revisa los campos marcados en rojo', 'error');
    return;
  }

  // Validación OK → mostrar spinner y dejar que el form haga POST al servlet
  setSubmitLoading(true);
}

/* ============================================================
   Cancelar
   ============================================================ */

function handleCancel() {
  const anyFilled = Object.keys(VALIDATORS).some(id => {
    const f = document.getElementById(id);
    return f && f.value.trim() !== '';
  });

  if (!anyFilled || confirm('¿Descartar los datos ingresados?')) {
    window.location.href = 'Inicio_de_sesion.jsp';
  }
}

/* ============================================================
   Init
   ============================================================ */

document.addEventListener('DOMContentLoaded', () => {
  initPasswordToggles();
  initLiveValidation();

  document.getElementById('registerForm')?.addEventListener('submit', handleSubmit);
  document.getElementById('cancelBtn')?.addEventListener('click', handleCancel);
});
