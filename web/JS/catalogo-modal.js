"use strict";

document.addEventListener("DOMContentLoaded", function () {
  const SERVLET_MAP = {
    programa: "RegistrarPrograma",
    sede: "RegistrarSede",
    jornada: "RegistrarJornada",
    modalidad: "RegistrarModalidad",
    nivel: "RegistrarNivel",
    etapa: "RegistrarEtapa",
    estado: "RegistrarEstado",
    tipoDocumento: "RegistrarTipoDocumento",
    tipoVinculacion: "RegistrarTipoVinculacion",
    rol: "RegistrarRol",
    trimestre: "RegistrarTrimestre",
    competencia: "RegistrarCompetencia",
    resultado: "RegistrarResultado",
    vinculacionLaboral: "RegistrarVinculacionLaboral",
    programacion: "RegistrarProgramacion",
  };

  const form = document.getElementById("form-crear-catalogo");
  const modalEl = document.getElementById("modal-crear-catalogo");
  const titleEl = document.getElementById("title-crear-catalogo");
  const fieldsEl = document.getElementById("catalogo-fields");
  const modalPadre = document.getElementById("modal-crear-registro");

  if (!form || !modalEl || !titleEl || !fieldsEl || !modalPadre) return;

  function optionsHtml(key) {
    const source = document.querySelector(`#catalogo-option-sources select[data-options="${key}"]`);
    return source ? source.innerHTML : "";
  }

  function selectField(name, key, label, invalidText, colClass = "col-12") {
    return `
      <div class="${colClass}">
        <label class="form-label">${label} *</label>
        <select name="${name}" class="form-select" required>
          <option value="">Seleccionar...</option>
          ${optionsHtml(key)}
        </select>
        <div class="invalid-feedback">${invalidText}</div>
      </div>`;
  }

  const FIELDS_MAP = {
    rol: `
      <div class="col-12">
        <label class="form-label">Descripcion del rol *</label>
        <input name="descripcion_Roles" class="form-control" placeholder="ej. Instructor" required />
        <div class="invalid-feedback">Ingresa la descripcion</div>
      </div>`,

    sede: `
      <div class="col-12">
        <label class="form-label">Nombre de la sede *</label>
        <input name="nombre_sede" class="form-control" placeholder="ej. Sede Centro" required />
        <div class="invalid-feedback">Ingresa el nombre de la sede</div>
      </div>`,

    programa: `
      <div class="col-5">
        <label class="form-label">Codigo *</label>
        <input name="codigo_programa" type="number" class="form-control" placeholder="ej. 228122" required />
        <div class="invalid-feedback">Ingresa el codigo</div>
      </div>
      <div class="col-7">
        <label class="form-label">Nombre del programa *</label>
        <input name="nombre_programa" class="form-control" placeholder="ej. Análisis y Desarrollo de Software" required />
        <div class="invalid-feedback">Ingresa el nombre</div>
      </div>`,

    jornada: `
      <div class="col-12">
        <label class="form-label">Descripcion de la jornada *</label>
        <input name="descripcion_Jornada" class="form-control" placeholder="ej. Diurna, Nocturna" required />
        <div class="invalid-feedback">Ingresa la descripcion</div>
      </div>`,

    modalidad: `
      <div class="col-12">
        <label class="form-label">Descripcion de la modalidad *</label>
        <input name="descripcion_Modalidad" class="form-control" placeholder="ej. Presencial, Virtual" required />
        <div class="invalid-feedback">Ingresa la descripcion</div>
      </div>`,

    nivel: `
      <div class="col-12">
        <label class="form-label">Descripcion del nivel *</label>
        <input name="descripcion_Nivel_Formacion" class="form-control" placeholder="ej. Tecnico, Tecnologo" required />
        <div class="invalid-feedback">Ingresa la descripcion</div>
      </div>`,

    etapa: `
      <div class="col-12">
        <label class="form-label">Descripcion de la etapa *</label>
        <input name="descripcion_Etapa" class="form-control" placeholder="ej. Lectiva, Productiva" required />
        <div class="invalid-feedback">Ingresa la descripcion</div>
      </div>`,

    estado: `
      <div class="col-12">
        <label class="form-label">Descripcion del estado *</label>
        <input name="descripcion_Estado" class="form-control" placeholder="ej. Activo, Inactivo" required />
        <div class="invalid-feedback">Ingresa la descripcion</div>
      </div>
      ${selectField("Tipo_Estado_id_tipo_estado", "tipoEstado", "Tipo de estado", "Selecciona un tipo de estado")}`,

    tipoDocumento: `
      <div class="col-12">
        <label class="form-label">Tipo de documento *</label>
        <input name="descripcion_Tipo_Doc" class="form-control" placeholder="ej. Cedula de ciudadania" required />
        <div class="invalid-feedback">Ingresa la descripcion</div>
      </div>`,

    tipoVinculacion: `
      <div class="col-12">
        <label class="form-label">Tipo de vinculacion *</label>
        <input name="descripcion_vinculacion" class="form-control" placeholder="ej. Planta, Contratista" required />
        <div class="invalid-feedback">Ingresa la descripcion</div>
      </div>`,

    trimestre: `
      <div class="col-4">
        <label class="form-label">Numero *</label>
        <input name="num_trimestre" type="number" min="1" max="6" class="form-control" placeholder="ej. 1" required />
        <div class="invalid-feedback">Ingresa el numero</div>
      </div>
      <div class="col-8">
        <label class="form-label">Descripcion *</label>
        <input name="descripcion" class="form-control" placeholder="ej. Trimestre 1 - 2026" required />
        <div class="invalid-feedback">Ingresa la descripcion</div>
      </div>
      <div class="col-6">
        <label class="form-label">Fecha inicio *</label>
        <input name="fecha_inicio" type="date" class="form-control" required />
        <div class="invalid-feedback">Selecciona la fecha de inicio</div>
      </div>
      <div class="col-6">
        <label class="form-label">Fecha fin *</label>
        <input name="fecha_fin" type="date" class="form-control" required />
        <div class="invalid-feedback">Selecciona la fecha de fin</div>
      </div>`,

    competencia: `
      <div class="col-5">
        <label class="form-label">Codigo *</label>
        <input name="codigo_Competencias" type="number" class="form-control" placeholder="ej. 210201501" required />
        <div class="invalid-feedback">Ingresa el codigo</div>
      </div>
      <div class="col-7">
        <label class="form-label">Descripcion *</label>
        <input name="descripcion_Competencias" class="form-control" placeholder="ej. Construir soluciones de software" required />
        <div class="invalid-feedback">Ingresa la descripcion</div>
      </div>
      ${selectField("Programas_idProgramas", "programas", "Programa asociado", "Selecciona un programa")}`,

    resultado: `
      <div class="col-5">
        <label class="form-label">Codigo *</label>
        <input name="codigo_ResultadoAp" type="number" class="form-control" placeholder="ej. 21020150101" required />
        <div class="invalid-feedback">Ingresa el codigo</div>
      </div>
      <div class="col-7">
        <label class="form-label">Descripcion *</label>
        <input name="descripcion_Resul" class="form-control" placeholder="ej. Identificar requerimientos" required />
        <div class="invalid-feedback">Ingresa la descripcion</div>
      </div>
      ${selectField("Competencias_id_competencias", "competencias", "Competencia", "Selecciona una competencia")}`,

    vinculacionLaboral: `
      <div class="col-12">
        <label class="form-label">Descripcion *</label>
        <input name="descripcion" class="form-control" placeholder="ej. Contrato prestacion de servicios" required />
        <div class="invalid-feedback">Ingresa la descripcion</div>
      </div>
      <div class="col-12">
        <label class="form-label">Numero de contrato *</label>
        <input name="numero_Contrato" class="form-control" placeholder="ej. 2026-0342" required />
        <div class="invalid-feedback">Ingresa el numero de contrato</div>
      </div>
      <div class="col-6">
        <label class="form-label">Fecha inicio *</label>
        <input name="fecha_Inicio" type="date" class="form-control" required />
        <div class="invalid-feedback">Selecciona la fecha de inicio</div>
      </div>
      <div class="col-6">
        <label class="form-label">Fecha fin *</label>
        <input name="fecha_Fin" type="date" class="form-control" required />
        <div class="invalid-feedback">Selecciona la fecha de fin</div>
      </div>
      ${selectField("Usuarios_id_usuarios", "usuarios", "Usuario", "Selecciona un usuario")}`,

    programacion: `
      <div class="col-6">
        <label class="form-label">Fecha inicio *</label>
        <input name="fecha_inicial_Prog" type="date" class="form-control" required />
        <div class="invalid-feedback">Selecciona la fecha de inicio</div>
      </div>
      <div class="col-6">
        <label class="form-label">Fecha fin *</label>
        <input name="fecha_fin_Prog" type="date" class="form-control" required />
        <div class="invalid-feedback">Selecciona la fecha de fin</div>
      </div>
      <div class="col-6">
        <label class="form-label">Hora inicio *</label>
        <input name="hora_inicio" type="time" class="form-control" required />
        <div class="invalid-feedback">Ingresa la hora de inicio</div>
      </div>
      <div class="col-6">
        <label class="form-label">Hora fin *</label>
        <input name="hora_fin" type="time" class="form-control" required />
        <div class="invalid-feedback">Ingresa la hora de fin</div>
      </div>
      <div class="col-12">
        <label class="form-label">Dias de la semana *</label>
        <input name="dias_Semana" class="form-control" placeholder="ej. LUNES,MIERCOLES,VIERNES" required />
        <div class="invalid-feedback">Ingresa los dias</div>
      </div>
      ${selectField("Ficha_id_ficha", "fichas", "Ficha", "Selecciona una ficha", "col-6")}
      ${selectField("Usuarios_id_usuarios", "usuarios", "Instructor", "Selecciona un instructor", "col-6")}
      ${selectField("Ambientes_id_ambientes", "ambientes", "Ambiente", "Selecciona un ambiente", "col-6")}
      ${selectField("Actividades_id_actividades", "actividades", "Actividad", "Selecciona una actividad", "col-6")}
      ${selectField("Trimestre_id_trimestre", "trimestres", "Trimestre", "Selecciona un trimestre", "col-6")}
      ${selectField("Estado_id_estado", "estados", "Estado", "Selecciona un estado", "col-6")}
      <div class="col-12">
        <label class="form-label">Observaciones</label>
        <textarea name="Observaciones" class="form-control" rows="2" placeholder="Observaciones opcionales"></textarea>
      </div>`,
  };

  function openAfterParentHidden(targetEl) {
    const parentInstance = bootstrap.Modal.getInstance(modalPadre);
    if (parentInstance) {
      modalPadre.addEventListener("hidden.bs.modal", function handler() {
        modalPadre.removeEventListener("hidden.bs.modal", handler);
        bootstrap.Modal.getOrCreateInstance(targetEl).show();
      });
      parentInstance.hide();
    } else {
      bootstrap.Modal.getOrCreateInstance(targetEl).show();
    }
  }

  document.querySelectorAll("[data-catalogo]").forEach(function (btn) {
    btn.addEventListener("click", function (event) {
      event.preventDefault();
      event.stopImmediatePropagation();

      const tipo = this.dataset.catalogo;
      const servlet = SERVLET_MAP[tipo];
      const html = FIELDS_MAP[tipo];
      if (!servlet || !html) return;

      form.action = servlet;
      form.classList.remove("was-validated");
      titleEl.textContent = (this.querySelector("strong") || {}).textContent || tipo;
      fieldsEl.innerHTML = html;
      openAfterParentHidden(modalEl);
    });
  });

  document.querySelectorAll(".cc-create-item[data-bs-target]").forEach(function (btn) {
    btn.addEventListener("click", function (event) {
      event.preventDefault();
      event.stopImmediatePropagation();
      const targetEl = document.querySelector(this.dataset.bsTarget);
      if (targetEl) openAfterParentHidden(targetEl);
    });
  });

  [form, document.getElementById("form-nueva-ficha"), document.getElementById("form-nueva-actividad"), document.getElementById("form-nuevo-ambiente")]
    .filter(Boolean)
    .forEach(function (targetForm) {
      targetForm.addEventListener("submit", function (event) {
        targetForm.classList.add("was-validated");
        if (!targetForm.checkValidity()) {
          event.preventDefault();
          event.stopPropagation();
        }
      });
    });

  modalEl.addEventListener("hidden.bs.modal", function () {
    fieldsEl.innerHTML = "";
    form.classList.remove("was-validated");
    form.action = "";
  });
});