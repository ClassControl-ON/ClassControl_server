/* ═══════════════════════════════════════════════════════════════
   ClassControl — Bloque de catálogos (reemplaza el script inline
   al final de Pagina_Principal.jsp)
   ═══════════════════════════════════════════════════════════════ */
(function () {
  "use strict";

  /* ── Mapa: data-catalogo → Servlet ── */
  const SERVLET_MAP = {
    programa           : "RegistrarPrograma",
    sede               : "RegistrarSede",
    jornada            : "RegistrarJornada",
    modalidad          : "RegistrarModalidad",
    nivel              : "RegistrarNivel",
    etapa              : "RegistrarEtapa",
    estado             : "RegistrarEstado",
    tipoDocumento      : "RegistrarTipoDocumento",
    tipoVinculacion    : "RegistrarTipoVinculacion",
    rol                : "RegistrarRol",
    trimestre          : "RegistrarTrimestre",
    competencia        : "RegistrarCompetencia",
    resultado          : "RegistrarResultado",
    vinculacionLaboral : "RegistrarVinculacionLaboral",
    programacion       : "RegistrarProgramacion",
  };

  /* ── Mapa: data-catalogo → campos HTML del form ──
     Los `name` coinciden exactamente con lo que lee cada Servlet.
  ── */
  const FIELDS_MAP = {

    /* RegistrarRol → texto(request, "descripcion_Roles") */
    rol: `
      <div class="col-12">
        <label class="form-label">Descripción del rol *</label>
        <input name="descripcion_Roles" class="form-control"
               placeholder="ej. Instructor" required />
        <div class="invalid-feedback">Ingresa la descripción</div>
      </div>`,

    /* RegistrarSede → texto(request, "nombre_sede") */
    sede: `
      <div class="col-12">
        <label class="form-label">Nombre de la sede *</label>
        <input name="nombre_sede" class="form-control"
               placeholder="ej. Sede Centro" required />
        <div class="invalid-feedback">Ingresa el nombre de la sede</div>
      </div>`,

    /* RegistrarPrograma → entero("codigo_programa") + texto("nombre_programa") */
    programa: `
      <div class="col-5">
        <label class="form-label">Código *</label>
        <input name="codigo_programa" type="number" class="form-control"
               placeholder="ej. 228122" required />
        <div class="invalid-feedback">Ingresa el código</div>
      </div>
      <div class="col-7">
        <label class="form-label">Nombre del programa *</label>
        <input name="nombre_programa" class="form-control"
               placeholder="ej. Análisis y Desarrollo de Software" required />
        <div class="invalid-feedback">Ingresa el nombre</div>
      </div>`,

    /* RegistrarJornada → texto("descripcion_Jornada") */
    jornada: `
      <div class="col-12">
        <label class="form-label">Descripción de la jornada *</label>
        <input name="descripcion_Jornada" class="form-control"
               placeholder="ej. Diurna, Nocturna, Madrugada" required />
        <div class="invalid-feedback">Ingresa la descripción</div>
      </div>`,

    /* RegistrarModalidad → texto("descripcion_Modalidad") */
    modalidad: `
      <div class="col-12">
        <label class="form-label">Descripción de la modalidad *</label>
        <input name="descripcion_Modalidad" class="form-control"
               placeholder="ej. Presencial, Virtual, A distancia" required />
        <div class="invalid-feedback">Ingresa la descripción</div>
      </div>`,

    /* RegistrarNivel → texto("descripcion_Nivel_Formacion") */
    nivel: `
      <div class="col-12">
        <label class="form-label">Descripción del nivel *</label>
        <input name="descripcion_Nivel_Formacion" class="form-control"
               placeholder="ej. Técnico, Tecnólogo, Especialización" required />
        <div class="invalid-feedback">Ingresa la descripción</div>
      </div>`,

    /* RegistrarEtapa → texto("descripcion_Etapa") */
    etapa: `
      <div class="col-12">
        <label class="form-label">Descripción de la etapa *</label>
        <input name="descripcion_Etapa" class="form-control"
               placeholder="ej. Lectiva, Productiva" required />
        <div class="invalid-feedback">Ingresa la descripción</div>
      </div>`,

    /* RegistrarEstado → texto("descripcion_Estado") */
    estado: `
      <div class="col-12">
        <label class="form-label">Descripción del estado *</label>
        <input name="descripcion_Estado" class="form-control"
               placeholder="ej. Activo, Inactivo, En proceso" required />
        <div class="invalid-feedback">Ingresa la descripción</div>
      </div>`,

    /* RegistrarTipoDocumento → texto("descripcion_TipoDoc") */
    tipoDocumento: `
      <div class="col-12">
        <label class="form-label">Descripción del tipo de documento *</label>
        <input name="descripcion_TipoDoc" class="form-control"
               placeholder="ej. Cédula de ciudadanía, Tarjeta de identidad" required />
        <div class="invalid-feedback">Ingresa la descripción</div>
      </div>`,

    /* RegistrarTipoVinculacion → texto("descripcion_vinculacion") */
    tipoVinculacion: `
      <div class="col-12">
        <label class="form-label">Descripción del tipo de vinculación *</label>
        <input name="descripcion_vinculacion" class="form-control"
               placeholder="ej. Planta, Contratista, Hora cátedra" required />
        <div class="invalid-feedback">Ingresa la descripción</div>
      </div>`,

    /* RegistrarTrimestre → entero("num_trimestre") + texto("descripcion")
                           + fecha("fecha_inicio") + fecha("fecha_fin") */
    trimestre: `
      <div class="col-4">
        <label class="form-label">Número *</label>
        <input name="num_trimestre" type="number" min="1" max="6"
               class="form-control" placeholder="ej. 1" required />
        <div class="invalid-feedback">Ingresa el número</div>
      </div>
      <div class="col-8">
        <label class="form-label">Descripción *</label>
        <input name="descripcion" class="form-control"
               placeholder="ej. Trimestre 1 - 2025" required />
        <div class="invalid-feedback">Ingresa la descripción</div>
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

    /* RegistrarCompetencia → entero("codigoCompetencias")
                             + texto("descripcionCompetencias")
                             + entero("Programas_idProgramas") */
    competencia: `
      <div class="col-5">
        <label class="form-label">Código *</label>
        <input name="codigoCompetencias" type="number" class="form-control"
               placeholder="ej. 210201501" required />
        <div class="invalid-feedback">Ingresa el código</div>
      </div>
      <div class="col-7">
        <label class="form-label">Descripción *</label>
        <input name="descripcionCompetencias" class="form-control"
               placeholder="ej. Construir soluciones de software" required />
        <div class="invalid-feedback">Ingresa la descripción</div>
      </div>
      <div class="col-12">
        <label class="form-label">ID Programa *</label>
        <input name="Programas_idProgramas"
               type="number" class="form-control"
               placeholder="ID del programa asociado" required />
        <div class="invalid-feedback">Ingresa el ID del programa</div>
      </div>`,

    /* RegistrarResultado → entero("codigoResultadoAp")
                           + texto("descripcionResul")
                           + entero("Competencias_id_competencias") */
    resultado: `
      <div class="col-5">
        <label class="form-label">Código *</label>
        <input name="codigoResultadoAp" type="number" class="form-control"
               placeholder="ej. 2102015010101" required />
        <div class="invalid-feedback">Ingresa el código</div>
      </div>
      <div class="col-7">
        <label class="form-label">Descripción *</label>
        <input name="descripcionResul" class="form-control"
               placeholder="ej. Identificar requerimientos del sistema" required />
        <div class="invalid-feedback">Ingresa la descripción</div>
      </div>
      <div class="col-12">
        <label class="form-label">ID Competencia *</label>
        <input name="Competencias_id_competencias" type="number"
               class="form-control" placeholder="ID de la competencia asociada" required />
        <div class="invalid-feedback">Ingresa el ID de la competencia</div>
      </div>`,

    /* RegistrarVinculacionLaboral → texto("descripcion") + texto("numeroContrato")
                                    + fecha("fechaInicio") + fecha("fechaFin")
                                    + entero("Usuarios_id_usuarios")
       NOTA: el Servlet usa "fechaInicio"/"fechaFin" pero el INSERT usa
             "fechaInIcio"/"fechafin" — los names aquí deben coincidir con
             lo que el Servlet lee en request.getParameter().                */
    vinculacionLaboral: `
      <div class="col-12">
        <label class="form-label">Descripción *</label>
        <input name="descripcion" class="form-control"
               placeholder="ej. Contrato prestación de servicios" required />
        <div class="invalid-feedback">Ingresa la descripción</div>
      </div>
      <div class="col-12">
        <label class="form-label">Número de contrato *</label>
        <input name="numeroContrato" class="form-control"
               placeholder="ej. 2025-0342" required />
        <div class="invalid-feedback">Ingresa el número de contrato</div>
      </div>
      <div class="col-6">
        <label class="form-label">Fecha inicio *</label>
        <input name="fechaInicio" type="date" class="form-control" required />
        <div class="invalid-feedback">Selecciona la fecha de inicio</div>
      </div>
      <div class="col-6">
        <label class="form-label">Fecha fin *</label>
        <input name="fechaFin" type="date" class="form-control" required />
        <div class="invalid-feedback">Selecciona la fecha de fin</div>
      </div>
      <div class="col-12">
        <label class="form-label">ID Usuario *</label>
        <input name="Usuarios_id_usuarios" type="number" class="form-control"
               placeholder="ID del instructor o usuario" required />
        <div class="invalid-feedback">Ingresa el ID del usuario</div>
      </div>`,

    /* RegistrarProgramacion → textoOpcional("Observaciones")
                              + fecha("fecha_inicial_Prog") + fecha("fecha_fin_Prog")
                              + texto("diasSemana")
                              + hora("hora_inicio") + hora("hora_fin")
                              + entero("Ficha_id_ficha")
                              + entero("Usuarios_id_usuarios")
                              + entero("Ambientes_id_ambientes")
                              + entero("Actividades_id_actividades")
                              + entero("Trimestre_id_trimestre")
                              + entero("Estado_id_estado")  */
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
        <label class="form-label">Días de la semana *</label>
        <input name="diasSemana" class="form-control"
               placeholder="ej. LUNES,MIERCOLES,VIERNES" required />
        <div class="invalid-feedback">Ingresa los días</div>
      </div>
      <div class="col-6">
        <label class="form-label">ID Ficha *</label>
        <input name="Ficha_id_ficha" type="number" class="form-control"
               placeholder="ID de la ficha" required />
        <div class="invalid-feedback">Ingresa el ID de la ficha</div>
      </div>
      <div class="col-6">
        <label class="form-label">ID Instructor *</label>
        <input name="Usuarios_id_usuarios" type="number" class="form-control"
               placeholder="ID del instructor" required />
        <div class="invalid-feedback">Ingresa el ID del instructor</div>
      </div>
      <div class="col-6">
        <label class="form-label">ID Ambiente *</label>
        <input name="Ambientes_id_ambientes" type="number" class="form-control"
               placeholder="ID del ambiente" required />
        <div class="invalid-feedback">Ingresa el ID del ambiente</div>
      </div>
      <div class="col-6">
        <label class="form-label">ID Actividad *</label>
        <input name="Actividades_id_actividades" type="number" class="form-control"
               placeholder="ID de la actividad" required />
        <div class="invalid-feedback">Ingresa el ID de la actividad</div>
      </div>
      <div class="col-6">
        <label class="form-label">ID Trimestre *</label>
        <input name="Trimestre_id_trimestre" type="number" class="form-control"
               placeholder="ID del trimestre" required />
        <div class="invalid-feedback">Ingresa el ID del trimestre</div>
      </div>
      <div class="col-6">
        <label class="form-label">ID Estado *</label>
        <input name="Estado_id_estado" type="number" class="form-control"
               value="1" required />
        <div class="invalid-feedback">Ingresa el ID del estado</div>
      </div>
      <div class="col-12">
        <label class="form-label">Observaciones</label>
        <textarea name="Observaciones" class="form-control" rows="2"
                  placeholder="Observaciones opcionales…"></textarea>
      </div>`,
  };

  /* ── Nodos del modal de catálogo ── */
  const form   = document.getElementById("form-crear-catalogo");
  const modal  = document.getElementById("modal-crear-catalogo");
  const title  = document.getElementById("title-crear-catalogo");
  const fields = document.getElementById("catalogo-fields");

  /* ── Listener en cada botón con data-catalogo ── */
  // Los modales de catálogo se controlan exclusivamente desde
  // catalogo-modal.js, que genera selects con opciones reales. Este bloque
  // antiguo se conserva solo como referencia y no registra eventos.
  document.querySelectorAll("[data-catalogo-legacy]").forEach(btn => {
    btn.addEventListener("click", function () {
      const tipo    = this.dataset.catalogo;
      const servlet = SERVLET_MAP[tipo];
      const html    = FIELDS_MAP[tipo];

      if (!servlet || !html) {
        console.warn("ClassControl: tipo de catálogo no definido →", tipo);
        return;
      }

      /* 1. Actualizar action y título */
      form.action      = servlet;
      form.classList.remove("was-validated"); // limpiar validación anterior
      title.textContent = this.querySelector("strong")?.textContent ?? tipo;

      /* 2. Inyectar los campos */
      fields.innerHTML = html;

      /* 3. Cerrar el modal padre (selector de tabla) */
      bootstrap.Modal.getInstance(
        document.getElementById("modal-crear-registro")
      )?.hide();

      /* 4. Abrir el modal de catálogo */
      bootstrap.Modal.getOrCreateInstance(modal).show();
    });
  });

  /* ── Listener en botones Ficha / Actividad / Ambiente
        (usan data-bs-target pero no tienen toggle automático
         porque están dentro de otro modal) ── */
  document.querySelectorAll(".cc-create-item[data-bs-target-legacy]").forEach(btn => {
    btn.addEventListener("click", function () {
      const targetId = this.dataset.bsTarget;

      /* Cerrar el modal padre primero */
      bootstrap.Modal.getInstance(
        document.getElementById("modal-crear-registro")
      )?.hide();

      /* Pequeño delay para que Bootstrap termine el hide antes del show */
      setTimeout(() => {
        bootstrap.Modal.getOrCreateInstance(
          document.querySelector(targetId)
        ).show();
      }, 200);
    });
  });

  /* ═══════════════════════════════════════════════════════════════
     DASHBOARD — contadores, próximas actividades y estado de programas
     Antes venían pre-calculados por un scriptlet SQL en el JSP;
     ahora se piden por fetch() a ConsultarDashboard.
     ═══════════════════════════════════════════════════════════════ */
  async function fetchJSON(url) {
    const resp = await fetch(url);
    if (!resp.ok) throw new Error(`${url} respondió ${resp.status}`);
    return resp.json();
  }

  function escapeHtml(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, ch => ({
      "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    }[ch]));
  }

  const PROG_COLORS = ["cc-prog-green", "cc-prog-blue", "cc-prog-orange", "cc-prog-green", "cc-prog-blue"];
  const BADGE_COLORS = ["blue", "purple", "orange", "green"];

  function actividadRowHTML(act, index) {
    const bc = BADGE_COLORS[index % BADGE_COLORS.length];
    const puedeGestionar = window.ccPuedeGestionarInicio === true;
    return `
      <tr>
        <td>
          <p class="cc-ficha-num mb-0">${escapeHtml(act.codigoFicha)}</p>
          <p class="cc-ficha-prog mb-0">${escapeHtml(act.programa)} – ${escapeHtml(act.jornada)}</p>
        </td>
        <td>${escapeHtml(act.actividad)}</td>
        <td><span class="cc-amb-badge ${bc}">${escapeHtml(act.ambiente)}</span></td>
        <td>${escapeHtml(act.horario)}</td>
        <td>${escapeHtml(act.instructor)}</td>
        <td class="text-end">
          ${puedeGestionar ? `
          <div class="dropdown">
            <button class="btn cc-icon-btn" data-bs-toggle="dropdown" aria-expanded="false">
              <span class="material-symbols-outlined">more_vert</span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end">
              <li><a class="dropdown-item" href="#">
                <span class="material-symbols-outlined me-2" style="font-size:.9rem">edit</span>Editar</a></li>
              <li><a class="dropdown-item text-danger" href="#">
                <span class="material-symbols-outlined me-2" style="font-size:.9rem">delete</span>Eliminar</a></li>
            </ul>
          </div>` : ''}
        </td>
      </tr>`;
  }

  function renderEstadoProgramas(estadoProgramas) {
    const cont = document.getElementById("estado-programas-container");
    const tpl  = document.getElementById("tpl-estado-programa");
    if (!cont || !tpl) return;

    cont.querySelectorAll(":scope > *:not(template)").forEach(n => n.remove());

    if (!estadoProgramas.length) {
      const vacio = document.createElement("p");
      vacio.className = "text-muted";
      vacio.style.fontSize = ".8rem";
      vacio.textContent = "Sin datos de programas.";
      cont.appendChild(vacio);
      return;
    }

    estadoProgramas.forEach((prog, i) => {
      const nodo = tpl.content.cloneNode(true);
      nodo.querySelector('[data-field="nombre"]').textContent = prog.nombre;
      nodo.querySelector('[data-field="pct-label"]').textContent = `${prog.pct}%`;
      const barra = nodo.querySelector('[data-field="barra"]');
      barra.classList.add(PROG_COLORS[i % PROG_COLORS.length]);
      barra.style.width = `${prog.pct}%`;
      cont.appendChild(nodo);
    });
  }

  let actividadesDataTable = null;

  function renderActividades(actividades) {
    const tbody = document.getElementById("tbody-actividades");
    if (!tbody) return;
    tbody.innerHTML = actividades.map(actividadRowHTML).join("");

    if (actividadesDataTable) {
      actividadesDataTable.destroy();
    }
    actividadesDataTable = $("#actividades-table").DataTable({
      language: {
        url: "https://cdn.datatables.net/plug-ins/1.13.8/i18n/es-ES.json",
        emptyTable: "No hay actividades programadas próximamente."
      },
      pageLength: 5,
      lengthMenu: [5, 10, 25],
      order: [[3, "asc"]],
      autoWidth: false,
      columnDefs: [
        { targets: 0, width: "22%" }, // Ficha / Programa
        { targets: 1, width: "18%" }, // Actividad
        { targets: 2, width: "16%" }, // Ambiente
        { targets: 3, width: "16%" }, // Horario
        { targets: 4, width: "16%" }, // Instructor
        { targets: 5, width: "12%", orderable: false } // Acción
      ]
    });
  }

  function setStat(id, valor) {
    const el = document.getElementById(id);
    if (el) el.textContent = valor;
  }

  /* ═══════════════════════════════════════════════════════════════
     OCUPACIÓN DE AMBIENTES — la gráfica del dashboard
     Se calcula a partir de las programaciones reales
     (ConsultarProgramaciones). Solo aparecen los ambientes que
     tienen alguna actividad programada dentro del período elegido
     ("Esta semana" o "Hoy").
     ═══════════════════════════════════════════════════════════════ */

  // Mapa: código de día (ENUM de la BD) -> 0=Lu ... 6=Do
  const OCU_DIA_INDEX = {
    "LUN": 0, "MAR": 1, "MIE": 2, "JUE": 3, "VIE": 4, "SAB": 5, "DOM": 6
  };
  const OCU_DIAS_ETIQUETA = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"];

  const OCU_COLORS = [
    ClassControl.colors.primary,
    ClassControl.colors.accent,
    ClassControl.colors.warning,
    ClassControl.colors.danger,
    ClassControl.colors.navy,
    ClassControl.colors.info,
    ClassControl.colors.primaryDark,
    ClassControl.colors.muted,
  ];

  let ocupacionChart = null;

  /* Lunes = inicio de semana */
  function inicioDeSemana(fecha) {
    const d = new Date(fecha);
    d.setHours(0, 0, 0, 0);
    const dia = d.getDay(); // 0 = Domingo
    const diff = (dia === 0 ? -6 : 1 - dia);
    d.setDate(d.getDate() + diff);
    return d;
  }

  function sumarDias(fecha, n) {
    const d = new Date(fecha);
    d.setDate(d.getDate() + n);
    return d;
  }

  /* Convierte un código de día ("LUN") al índice JS real (0=Do..6=Sá)
     para poder comparar con getDay(). */
  function codigoADiaJS(codigo) {
    if (!(codigo in OCU_DIA_INDEX)) return -1;
    const idx = OCU_DIA_INDEX[codigo];   // 0=Lu ... 6=Do
    return (idx + 1) % 7;                // 0=Do,1=Lu,...,6=Sá
  }

  function obtenerDiasSemana(strDias) {
    if (!strDias) return [];
    return String(strDias)
      .split(",")
      .map(d => d.trim().toUpperCase().slice(0, 3))
      .filter(d => d in OCU_DIA_INDEX);
  }

  /* Devuelve el rango [inicio, fin] del período seleccionado. */
  function rangoDelPeriodo(periodo) {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    if (periodo === "Hoy") {
      return { inicio: hoy, fin: hoy };
    }
    const inicio = inicioDeSemana(hoy);
    return { inicio, fin: sumarDias(inicio, 6) };
  }

  function fechasSeIntersectan(aIni, aFin, bIni, bFin) {
    return aIni <= bFin && bIni <= aFin;
  }

  function calcularOcupacion(programaciones, periodo) {
    const { inicio, fin } = rangoDelPeriodo(periodo);

    // acumulador[ambienteId] = { nombre, horasPorDia: [7] }
    const ocupacion = new Map();

    (Array.isArray(programaciones) ? programaciones : []).forEach(prog => {
      // Solo programaciones activas y con ambiente asignado
      if (Number(prog.estadoId) !== 1) return;
      const idAmbiente = Number(prog.ambienteId);
      if (!(idAmbiente > 0)) return;

      const pIni = prog.fechaInicio ? new Date(prog.fechaInicio + "T00:00:00") : null;
      const pFin = prog.fechaFin ? new Date(prog.fechaFin + "T00:00:00") : null;
      if (!pIni || !pFin || isNaN(pIni.getTime()) || isNaN(pFin.getTime())) return;

      // El rango útil = intersección entre la programación y el período
      if (!fechasSeIntersectan(pIni, pFin, inicio, fin)) return;

      const utilIni = pIni > inicio ? new Date(pIni) : new Date(inicio);
      const utilFin = pFin < fin ? new Date(pFin) : new Date(fin);

      const horas = duracionHoras(prog.horaInicio, prog.horaFin);
      if (horas <= 0) return;

      const diasSemana = obtenerDiasSemana(prog.diasSemana).map(codigoADiaJS);
      if (!diasSemana.length) return;

      // Recorrer cada día dentro del rango útil de la programación
      for (let d = new Date(utilIni); d <= utilFin; d = sumarDias(d, 1)) {
        if (!diasSemana.includes(d.getDay())) continue;

        const clave = d.getDay(); // 0=Do..6=Sá
        if (!ocupacion.has(idAmbiente)) {
          ocupacion.set(idAmbiente, {
            nombre: prog.ambiente || ("Ambiente #" + idAmbiente),
            horasPorDia: [0, 0, 0, 0, 0, 0, 0],
          });
        }
        ocupacion.get(idAmbiente).horasPorDia[clave] += horas;
      }
    });

    // Solo ambientes con actividad real en el período
    const resultado = [...ocupacion.values()]
      .filter(amb => amb.horasPorDia.some(h => h > 0))
      .sort((a, b) =>
        sumarArray(b.horasPorDia) - sumarArray(a.horasPorDia) ||
        a.nombre.localeCompare(b.nombre));

    return resultado;
  }

  function duracionHoras(hIni, hFin) {
    if (!hIni || !hFin) return 0;
    const [hiH, hiM] = String(hIni).split(":").map(Number);
    const [hfH, hfM] = String(hFin).split(":").map(Number);
    if ([hiH, hiM, hfH, hfM].some(x => isNaN(x))) return 0;
    const diff = (hfH * 60 + hfM) - (hiH * 60 + hiM);
    return diff > 0 ? diff / 60 : 0;
  }

  function sumarArray(arr) {
    return arr.reduce((s, n) => s + n, 0);
  }

  function renderOcupacion(ambientesOcupados, periodo) {
    const canvas = document.getElementById("ocupacionChart");
    const vacio  = document.getElementById("ocupacion-vacio");
    if (!canvas) return;

    if (!ambientesOcupados.length) {
      if (canvas) canvas.classList.add("d-none");
      if (vacio) vacio.classList.remove("d-none");
      if (ocupacionChart) { ocupacionChart.destroy(); ocupacionChart = null; }
      return;
    }

    if (vacio) vacio.classList.add("d-none");
    if (canvas) canvas.classList.remove("d-none");

    const { inicio, fin } = rangoDelPeriodo(periodo);
    const etiquetas = [];
    for (let d = new Date(inicio); d <= fin; d = sumarDias(d, 1)) {
      etiquetas.push(OCU_DIAS_ETIQUETA[d.getDay()]);
    }

    const datasets = ambientesOcupados.map((amb, i) => {
      // Recortar los 7 días al rango del período
      const datos = [];
      for (let d = new Date(inicio); d <= fin; d = sumarDias(d, 1)) {
        datos.push(amb.horasPorDia[d.getDay()]);
      }
      return {
        label: amb.nombre,
        data: datos,
        backgroundColor: OCU_COLORS[i % OCU_COLORS.length],
        borderRadius: 4,
        maxBarThickness: 26,
      };
    });

    if (ocupacionChart) { ocupacionChart.destroy(); ocupacionChart = null; }

    ocupacionChart = new Chart(canvas, {
      type: "bar",
      data: { labels: etiquetas, datasets },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: "bottom",
            labels: { boxWidth: 12, font: { family: "DM Sans", size: 11, weight: "600" } },
          },
        },
        scales: {
          x: {
            stacked: false,
            grid: { display: false },
            ticks: { font: { family: "DM Sans", size: 11 } },
          },
          y: {
            beginAtZero: true,
            title: {
              display: true,
              text: "Horas ocupadas",
              font: { family: "DM Sans", size: 11, weight: "600" },
            },
            ticks: { font: { family: "DM Sans", size: 11 }, callback: v => `${v}h` },
          },
        },
      },
    });
  }

  async function cargarOcupacion(periodo) {
    try {
      const progs = await fetchJSON("ConsultarProgramaciones");
      const ocupados = calcularOcupacion(progs, periodo);
      renderOcupacion(ocupados, periodo);
    } catch (err) {
      console.error("ClassControl: no se pudo cargar la ocupación de ambientes", err);
      renderOcupacion([], periodo);
    }
  }

  async function cargarDashboard() {
    try {
      const data = await fetchJSON("ConsultarDashboard");
      setStat("stat-fichas-activas",   data.totalFichasActivas);
      setStat("stat-ambientes-hoy",    data.totalAmbientesHoy);
      setStat("stat-actividades-curso", data.totalActividadesEnCurso);
      setStat("stat-instructores",     data.totalInstructores);
      renderEstadoProgramas(data.estadoProgramas || []);
      renderActividades(data.proximasActividades || []);
    } catch (err) {
      console.error("ClassControl: no se pudo cargar el dashboard", err);
    }
  }

  /* ═══════════════════════════════════════════════════════════════
     CATÁLOGOS — opciones de los <select> del formulario "Crear registro"
     y de los selects fuente que usa catalogo-modal.js.
     Antes venían de SELECTs SQL directos en el JSP; ahora cada uno
     se pide a su propio servlet Consultar*, igual que en el resto
     de las vistas del proyecto.
     ═══════════════════════════════════════════════════════════════ */
  function fillSelect(id, items, buildLabel) {
    const select = document.getElementById(id) ||
                   document.querySelector(`#catalogo-option-sources select[data-options="${id}"]`);
    if (!select) return;
    const placeholder = select.querySelector('option[value=""]');
    select.innerHTML = "";
    if (placeholder) select.appendChild(placeholder);
    items.forEach(item => {
      const opt = document.createElement("option");
      opt.value = item.id;
      opt.textContent = buildLabel(item);
      select.appendChild(opt);
    });
  }

  async function cargarCatalogos() {
    try {
      const [
        programas, jornadas, modalidades, niveles, sedes, etapas,
        resultados, competencias, usuarios, ambientes,
        actividades, trimestres, estados, fichas, tiposEstado
      ] = await Promise.all([
        fetchJSON("ConsultarProgramas"),
        fetchJSON("ConsultarJornadas"),
        fetchJSON("ConsultarModalidades"),
        fetchJSON("ConsultarNiveles"),
        fetchJSON("ConsultarSedes"),
        fetchJSON("ConsultarEtapas"),
        fetchJSON("ConsultarResultados"),
        fetchJSON("ConsultarCompetencias"),
        fetchJSON("ConsultarUsuarios"),
        fetchJSON("ConsultarAmbientes"),
        fetchJSON("ConsultarActividades"),
        fetchJSON("ConsultarTrimestres"),
        fetchJSON("ConsultarEstados"),
        fetchJSON("ConsultarFichas"),
        fetchJSON("ConsultarTiposEstado"),
      ]);

      const nombrePrograma = new Map(programas.map(p => [p.id, p.nombre]));

      /* Selects de los formularios "Nueva ficha / actividad / ambiente" */
      fillSelect("ficha-programa",  programas,   p => p.nombre);
      fillSelect("ficha-jornada",   jornadas,    j => j.descripcion);
      fillSelect("ficha-modalidad", modalidades, m => m.descripcion);
      fillSelect("ficha-nivel",     niveles,     n => n.descripcion);
      fillSelect("ficha-sede",      sedes,       s => s.nombre);
      fillSelect("ficha-etapa",     etapas,      e => e.descripcion);
      fillSelect("act-resultado",   resultados,  r => `${r.codigo} - ${r.descripcion}`);
      fillSelect("amb-sede",        sedes,       s => s.nombre);

      /* Selects fuente que lee catalogo-modal.js (data-options="...") */
      fillSelect("programas",      programas,      p => `${p.codigo} - ${p.nombre}`);
      fillSelect("competencias",   competencias,   c => `${c.codigo} - ${c.descripcion}`);
      fillSelect("usuarios",       usuarios,       u => `${u.nombres} ${u.apellidos} - ${u.identificacion}`);
      fillSelect("fichas",         fichas,         f => `${f.codigo} - ${nombrePrograma.get(f.programaId) ?? ""}`);
      fillSelect("ambientes",      ambientes,      a => a.descripcion);
      fillSelect("actividades",    actividades,    a => `${a.codigoActividad} - ${a.nombre}`);
      fillSelect("trimestres",     trimestres,     t => `${t.numTrimestre} - ${t.descripcion}`);
      fillSelect("estados",        estados,        e => e.descripcion);
      fillSelect("tipoEstado",     tiposEstado,    t => t.descripcion);
    } catch (err) {
      console.error("ClassControl: no se pudieron cargar los catálogos", err);
    }
  }

  document.addEventListener("DOMContentLoaded", () => {
    cargarDashboard();
    cargarCatalogos();

    /* Ocupación de ambientes según el período elegido */
    const selectPeriodo = document.getElementById("chart-select");
    const cargarOcupacionActual = () => {
      const periodo = selectPeriodo ? selectPeriodo.value : "Esta semana";
      cargarOcupacion(periodo);
    };
    if (selectPeriodo) {
      selectPeriodo.addEventListener("change", cargarOcupacionActual);
    }
    cargarOcupacionActual();
  });

})();
