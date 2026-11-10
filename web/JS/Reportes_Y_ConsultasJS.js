/* ============================================================
   Reportes_Y_ConsultasJS.js
   Carga los datos reales de /ConsultarReportesMetricas, pinta los
   indicadores y gráficos, filtra la tabla y exporta Excel/CSV/PDF.
   ============================================================ */

(() => {
  'use strict';

  const ENDPOINT = 'ConsultarReportesMetricas';
  const EXPORT_ENDPOINT = 'ExportarReporte';
  const CABECERAS = [
    'Programa', 'Ficha', 'Instructor', 'Ambiente', 'Día',
    'Horario', 'Horas/sem', 'Estado', 'Vigencia'
  ];
  const MESES = ['ene', 'feb', 'mar', 'abr', 'may', 'jun',
                 'jul', 'ago', 'sep', 'oct', 'nov', 'dic'];

  const state = {
    periodo: 'mes',
    detalle: [],
    filtrado: [],
    charts: {}
  };

  const fmtNumero = new Intl.NumberFormat('es-CO');
  const fmtHoras = new Intl.NumberFormat('es-CO', { maximumFractionDigits: 1 });
  const colores = (window.ClassControl && window.ClassControl.colors) || {
    primary: '#38A800', primaryDark: '#00502A', navy: '#00304D',
    accent: '#005580', muted: '#8A9BB0', danger: '#DC3545'
  };

  const $ = (id) => document.getElementById(id);

  function texto(id, valor) {
    const elemento = $(id);
    if (elemento) { elemento.textContent = valor; }
  }

  function mostrarCarga(visible) {
    $('estadoCarga')?.classList.toggle('d-none', !visible);
  }

  function mostrarError(mensaje) {
    const alerta = $('alertaError');
    if (!alerta) { return; }
    alerta.textContent = mensaje;
    alerta.classList.remove('d-none');
  }

  function limpiarError() {
    $('alertaError')?.classList.add('d-none');
  }

  function horaCorta(valor) {
    const textoHora = String(valor ?? '');
    return textoHora.length >= 5 ? textoHora.slice(0, 5) : textoHora;
  }

  function horario(fila) {
    const inicio = horaCorta(fila.horaInicio);
    const fin = horaCorta(fila.horaFin);
    if (!inicio && !fin) { return '--'; }
    return `${inicio || '--'} - ${fin || '--'}`;
  }

  function vigencia(fila) {
    return `${fila.fechaInicio || '--'} / ${fila.fechaFin || '--'}`;
  }

  function etiquetaMes(mes) {
    const partes = String(mes ?? '').split('-');
    const indice = Number(partes[1]) - 1;
    const nombre = MESES[indice] || partes[1] || '';
    return `${nombre} ${partes[0] || ''}`.trim();
  }

  function esActiva(fila) {
    return Number(fila.estadoId) === 1;
  }

  /* ── Carga de datos ─────────────────────────────────────── */

  async function cargar() {
    limpiarError();
    mostrarCarga(true);
    const periodo = $('selectPeriodo')?.value || 'mes';

    try {
      const respuesta = await fetch(
        `${ENDPOINT}?periodo=${encodeURIComponent(periodo)}`,
        { headers: { Accept: 'application/json' } }
      );

      if (respuesta.status === 403) {
        throw new Error('No tienes permiso para consultar los reportes.');
      }
      if (!respuesta.ok) {
        throw new Error('No se pudieron cargar los reportes. Intenta de nuevo.');
      }

      const datos = await respuesta.json();
      if (datos.error) { throw new Error(datos.error); }

      state.periodo = datos.periodo || periodo;
      state.detalle = Array.isArray(datos.detalle) ? datos.detalle : [];

      pintarKpis(datos.kpis || {});
      pintarGraficos(datos);
      llenarFiltros();
      aplicarFiltros();
    } catch (error) {
      state.detalle = [];
      state.filtrado = [];
      renderTabla();
      mostrarError(error.message || 'Ocurrió un error al cargar los reportes.');
    } finally {
      mostrarCarga(false);
    }
  }

  /* ── Indicadores ────────────────────────────────────────── */

  function pintarKpis(kpis) {
    const programaciones = Number(kpis.programaciones) || 0;
    const instructores = Number(kpis.instructores) || 0;
    const horas = Number(kpis.horas) || 0;
    const cumplimiento = Number(kpis.cumplimiento) || 0;
    const ocupacion = Number(kpis.ocupacion) || 0;
    const ambientes = Number(kpis.ambientes) || 0;
    const ambientesTotales = Number(kpis.ambientesTotales) || 0;

    texto('kpiProgramaciones', fmtNumero.format(programaciones));
    texto('kpiProgramacionesHint',
      `${fmtNumero.format(Number(kpis.fichas) || 0)} ficha(s) en el periodo`);

    texto('kpiInstructores', fmtNumero.format(instructores));
    texto('kpiInstructoresHint',
      `${fmtNumero.format(Number(kpis.instructoresActivos) || 0)} activo(s) en total`);

    texto('kpiHoras', fmtHoras.format(horas));
    const promedio = programaciones > 0 ? horas / programaciones : 0;
    texto('kpiHorasHint', `Promedio ${fmtHoras.format(promedio)} h por bloque`);

    texto('kpiCumplimiento', `${fmtHoras.format(cumplimiento)}%`);
    texto('kpiCumplimientoHint', 'Programaciones en estado activo');

    texto('textoOcupacion',
      `Ocupación de ambientes: ${fmtHoras.format(ocupacion)}% ` +
      `(${fmtNumero.format(ambientes)} de ${fmtNumero.format(ambientesTotales)})`);
  }

  /* ── Gráficos ───────────────────────────────────────────── */

  function crearGrafico(id, configuracion) {
    const lienzo = $(id);
    if (!lienzo || typeof window.Chart === 'undefined') { return; }

    if (state.charts[id]) {
      state.charts[id].destroy();
    }
    state.charts[id] = new window.Chart(lienzo, configuracion);
  }

  function pintarGraficos(datos) {
    if (typeof window.Chart === 'undefined') {
      mostrarError('No se pudo cargar la librería de gráficos.');
      return;
    }

    window.Chart.defaults.font.family = "'DM Sans', system-ui, sans-serif";
    window.Chart.defaults.color = colores.muted;

    const porPrograma = Array.isArray(datos.porPrograma) ? datos.porPrograma : [];
    const porEstado = Array.isArray(datos.porEstado) ? datos.porEstado : [];
    const tendencia = Array.isArray(datos.tendencia) ? datos.tendencia : [];

    crearGrafico('graficoProgramas', {
      type: 'bar',
      data: {
        labels: porPrograma.map((item) => item.nombre),
        datasets: [{
          label: 'Programaciones',
          data: porPrograma.map((item) => item.total),
          backgroundColor: colores.primary,
          hoverBackgroundColor: colores.primaryDark,
          borderRadius: 6,
          maxBarThickness: 46
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, ticks: { precision: 0 } },
          x: { grid: { display: false }, ticks: { maxRotation: 40, minRotation: 0 } }
        }
      }
    });

    crearGrafico('graficoEstados', {
      type: 'doughnut',
      data: {
        labels: porEstado.map((item) => item.nombre),
        datasets: [{
          data: porEstado.map((item) => item.total),
          backgroundColor: [
            colores.primary, colores.accent, '#F59E0B',
            colores.danger, '#0EA5E9', '#64748B'
          ],
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '62%',
        plugins: { legend: { position: 'bottom', labels: { boxWidth: 12 } } }
      }
    });

    crearGrafico('graficoTendencia', {
      type: 'line',
      data: {
        labels: tendencia.map((item) => etiquetaMes(item.mes)),
        datasets: [{
          label: 'Programaciones',
          data: tendencia.map((item) => item.total),
          borderColor: colores.accent,
          backgroundColor: 'rgba(0, 85, 128, 0.12)',
          fill: true,
          tension: 0.35,
          pointRadius: 3,
          pointBackgroundColor: colores.accent
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, ticks: { precision: 0 } },
          x: { grid: { display: false } }
        }
      }
    });
  }

  /* ── Filtros y tabla ────────────────────────────────────── */

  function llenarFiltros() {
    llenarSelect('selectPrograma', 'Todos los programas',
      state.detalle.map((fila) => fila.programa));
    llenarSelect('selectEstado', 'Todos los estados',
      state.detalle.map((fila) => fila.estado));
  }

  function llenarSelect(id, etiquetaTodos, valores) {
    const select = $(id);
    if (!select) { return; }

    const anterior = select.value;
    select.replaceChildren(new Option(etiquetaTodos, 'todos'));

    [...new Set(valores)]
      .filter((valor) => valor !== null && valor !== undefined && String(valor).trim() !== '')
      .sort((a, b) => String(a).localeCompare(String(b), 'es'))
      .forEach((valor) => select.appendChild(new Option(String(valor), String(valor))));

    const existe = [...select.options].some((opcion) => opcion.value === anterior);
    select.value = existe ? anterior : 'todos';
  }

  function aplicarFiltros() {
    const busqueda = ($('inputBuscar')?.value || '').trim().toLowerCase();
    const programa = $('selectPrograma')?.value || 'todos';
    const estado = $('selectEstado')?.value || 'todos';

    state.filtrado = state.detalle.filter((fila) => {
      const coincideTexto = !busqueda || [
        fila.programa, fila.ficha, fila.instructor, fila.ambiente
      ].some((valor) => String(valor ?? '').toLowerCase().includes(busqueda));

      const coincidePrograma = programa === 'todos' || fila.programa === programa;
      const coincideEstado = estado === 'todos' || fila.estado === estado;

      return coincideTexto && coincidePrograma && coincideEstado;
    });

    renderTabla();
    actualizarBotonesExportacion();
  }

  function renderTabla() {
    const cuerpo = $('cuerpoDetalle');
    if (!cuerpo) { return; }

    cuerpo.replaceChildren();

    if (!state.filtrado.length) {
      const fila = document.createElement('tr');
      const celda = document.createElement('td');
      celda.colSpan = CABECERAS.length;
      celda.className = 'text-center text-muted py-4';
      celda.textContent = state.detalle.length
        ? 'No hay programaciones que coincidan con los filtros.'
        : 'No hay programaciones registradas para este periodo.';
      fila.appendChild(celda);
      cuerpo.appendChild(fila);
    } else {
      const fragmento = document.createDocumentFragment();
      state.filtrado.forEach((fila) => fragmento.appendChild(crearFila(fila)));
      cuerpo.appendChild(fragmento);
    }

    texto('resumenTabla', state.detalle.length
      ? `${fmtNumero.format(state.filtrado.length)} de ` +
        `${fmtNumero.format(state.detalle.length)} programaciones`
      : 'Sin datos para mostrar.');
  }

  function crearFila(fila) {
    const tr = document.createElement('tr');

    tr.appendChild(celda(fila.programa));
    tr.appendChild(celda(fila.ficha, 'cc-mono'));
    tr.appendChild(celda(fila.instructor));
    tr.appendChild(celda(fila.ambiente));
    tr.appendChild(celda(fila.dia || '--'));
    tr.appendChild(celda(horario(fila), 'cc-mono'));
    tr.appendChild(celda(fmtHoras.format(Number(fila.horasSemana) || 0), 'text-end'));

    const tdEstado = document.createElement('td');
    const insignia = document.createElement('span');
    insignia.className = 'cc-badge-estado ' + (esActiva(fila)
      ? 'cc-badge-estado--activo'
      : 'cc-badge-estado--inactivo');
    insignia.textContent = fila.estado || 'Sin estado';
    tdEstado.appendChild(insignia);
    tr.appendChild(tdEstado);

    tr.appendChild(celda(vigencia(fila)));
    return tr;
  }

  function celda(valor, clase) {
    const td = document.createElement('td');
    td.textContent = valor === null || valor === undefined || valor === '' ? '--' : String(valor);
    if (clase) { td.className = clase; }
    return td;
  }

  function actualizarBotonesExportacion() {
    const hayDatos = state.filtrado.length > 0;
    ['btnExcel', 'btnCsv', 'btnPdf'].forEach((id) => {
      const boton = $(id);
      if (boton) { boton.disabled = !hayDatos; }
    });
  }

  /* ── Exportación ────────────────────────────────────────── */

  function parametrosExportacion(formato) {
    const parametros = new URLSearchParams();
    parametros.set('formato', formato);
    parametros.set('periodo', state.periodo);

    const programa = $('selectPrograma')?.value || 'todos';
    const estado = $('selectEstado')?.value || 'todos';
    const busqueda = ($('inputBuscar')?.value || '').trim();

    if (programa !== 'todos') { parametros.set('programa', programa); }
    if (estado !== 'todos') { parametros.set('estado', estado); }
    if (busqueda) { parametros.set('q', busqueda); }

    return parametros;
  }

  function exportarServidor(formato) {
    const parametros = parametrosExportacion(formato);
    window.location.assign(`${EXPORT_ENDPOINT}?${parametros.toString()}`);
  }

  function exportarPdf() {
    if (!state.filtrado.length) {
      mostrarError('No hay datos para exportar.');
      return;
    }
    if (!window.jspdf || typeof window.jspdf.jsPDF === 'undefined') {
      mostrarError('La librería para generar PDF no está disponible.');
      return;
    }

    const { jsPDF } = window.jspdf;
    const documento = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' });

    documento.setFontSize(14);
    documento.text('ClassControl - Reporte de programaciones', 14, 14);

    documento.setFontSize(9);
    documento.text(
      `Periodo: ${state.periodo} · Generado: ${new Date().toLocaleString('es-CO')}`,
      14, 20
    );

    const filas = state.filtrado.map((fila) => [
      fila.programa, fila.ficha, fila.instructor, fila.ambiente, fila.dia || '--',
      horario(fila), fmtHoras.format(Number(fila.horasSemana) || 0),
      fila.estado, vigencia(fila)
    ]);

    documento.autoTable({
      head: [CABECERAS],
      body: filas,
      startY: 25,
      margin: { left: 10, right: 10 },
      styles: { fontSize: 7, cellPadding: 1.5 },
      headStyles: { fillColor: [56, 168, 0], textColor: 255 },
      alternateRowStyles: { fillColor: [244, 246, 248] }
    });

    documento.save(`reporte_classcontrol_${fechaArchivo()}.pdf`);
  }

  function fechaArchivo() {
    const hoy = new Date();
    const dosDigitos = (numero) => String(numero).padStart(2, '0');
    return `${hoy.getFullYear()}${dosDigitos(hoy.getMonth() + 1)}${dosDigitos(hoy.getDate())}`;
  }

  /* ── Eventos ────────────────────────────────────────────── */

  function conectarEventos() {
    $('selectPeriodo')?.addEventListener('change', () => { void cargar(); });
    $('btnActualizar')?.addEventListener('click', () => { void cargar(); });
    $('inputBuscar')?.addEventListener('input', aplicarFiltros);
    $('selectPrograma')?.addEventListener('change', aplicarFiltros);
    $('selectEstado')?.addEventListener('change', aplicarFiltros);

    $('btnLimpiar')?.addEventListener('click', () => {
      if ($('inputBuscar')) { $('inputBuscar').value = ''; }
      if ($('selectPrograma')) { $('selectPrograma').value = 'todos'; }
      if ($('selectEstado')) { $('selectEstado').value = 'todos'; }
      aplicarFiltros();
    });

    $('btnExcel')?.addEventListener('click', () => exportarServidor('xlsx'));
    $('btnCsv')?.addEventListener('click', () => exportarServidor('csv'));
    $('btnPdf')?.addEventListener('click', exportarPdf);
  }

  const alCargar = window.ClassControl
    ? window.ClassControl.onReady
    : (callback) => document.addEventListener('DOMContentLoaded', callback, { once: true });

  alCargar(() => {
    conectarEventos();
    actualizarBotonesExportacion();
    void cargar();
  });
})();
