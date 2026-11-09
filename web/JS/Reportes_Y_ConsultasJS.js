/* ClassControl - Reportes y Consultas (Bootstrap 5 + Chart.js + DataTables + Exportación) */

(() => {
  'use strict';

  const demoData = {
    kpiByPeriodo: {
      mes: { instructores: 142, horas: 2840, ocupacion: 92.4, cumplimiento: 88.7, hint: '+4% vs mes anterior' },
      trimestre: { instructores: 138, horas: 8120, ocupacion: 89.1, cumplimiento: 91.2, hint: 'Cierre trimestral estable' },
      anio: { instructores: 155, horas: 31500, ocupacion: 87.6, cumplimiento: 93.0, hint: 'Meta anual superada' }
    },
    barByPrograma: {
      todos: [
        { label: 'ADSO', value: 98 },
        { label: 'Multimedia', value: 74 },
        { label: 'Ciberseguridad', value: 85 },
        { label: 'Gestión Administrativa', value: 62 }
      ],
      adso: [{ label: 'ADSO', value: 98 }],
      multimedia: [{ label: 'Multimedia', value: 74 }],
      ciber: [{ label: 'Ciberseguridad', value: 85 }],
      gestion: [{ label: 'Gestión Administrativa', value: 62 }]
    },
    lineByPeriodo: {
      mes: [640, 710, 580, 912],
      trimestre: [1820, 2010, 2150, 2140],
      anio: [6800, 7400, 7850, 9450]
    },
    donutByPeriodo: {
      mes: [42, 28, 19, 11],
      trimestre: [39, 31, 20, 10],
      anio: [36, 33, 22, 9]
    },
    tableRows: [
      { reporte: 'Ocupación de Ambientes', programa: 'ADSO', instructor: 'Carlos Martínez', ambiente: 'Lab 204', fecha: '2026-06-12', horas: 132, estado: 'Generado' },
      { reporte: 'Programación de Actividades', programa: 'Multimedia', instructor: 'Ana Lucía Rojas', ambiente: 'Auditorio A', fecha: '2026-06-13', horas: 96, estado: 'Generado' },
      { reporte: 'Cumplimiento de Competencias', programa: 'Ciberseguridad', instructor: 'Andrés Felipe', ambiente: 'Ambiente 105', fecha: '2026-06-10', horas: 104, estado: 'Pendiente' },
      { reporte: 'Carga por Instructor', programa: 'Gestión Administrativa', instructor: 'Laura Cárdenas', ambiente: 'Sala de Cómputo', fecha: '2026-06-09', horas: 88, estado: 'Generado' },
      { reporte: 'Reporte Consolidado', programa: 'ADSO', instructor: 'Carlos Martínez', ambiente: 'Lab 204', fecha: '2026-06-08', horas: 154, estado: 'Aprobado' },
      { reporte: 'Reporte Consolidado', programa: 'Multimedia', instructor: 'Ana Lucía Rojas', ambiente: 'Auditorio A', fecha: '2026-06-07', horas: 112, estado: 'Aprobado' },
      { reporte: 'Programación de Actividades', programa: 'ADSO', instructor: 'Laura Cárdenas', ambiente: 'Ambiente 105', fecha: '2026-06-06', horas: 118, estado: 'Generado' },
      { reporte: 'Ocupación de Ambientes', programa: 'Ciberseguridad', instructor: 'Andrés Felipe', ambiente: 'Sala de Cómputo', fecha: '2026-06-05', horas: 86, estado: 'Pendiente' }
    ]
  };

  let dataTable = null;
  let barChart = null;
  let lineChart = null;
  let donutChart = null;

  const toastEl = document.getElementById('appToast');
  const toastBody = document.getElementById('appToastBody');
  const appToast = new bootstrap.Toast(toastEl, { delay: 2500 });

  const filtersForm = document.getElementById('filtersForm');
  const filterPeriodo = document.getElementById('filterPeriodo');
  const filterPrograma = document.getElementById('filterPrograma');
  const filterInstructor = document.getElementById('filterInstructor');
  const filterAmbiente = document.getElementById('filterAmbiente');
  const filterFrom = document.getElementById('filterFrom');
  const filterTo = document.getElementById('filterTo');

  function showToast(message, type = 'success') {
    toastEl.classList.remove('toast-success', 'toast-info', 'toast-error');
    if (type === 'error') toastEl.classList.add('toast-error');
    else if (type === 'info') toastEl.classList.add('toast-info');
    else toastEl.classList.add('toast-success');

    toastBody.textContent = message;
    appToast.show();
  }

  function formatHoras(value) {
    return `${value.toLocaleString('es-CO')}h`;
  }

  function setDefaultDates() {
    const now = new Date();
    const y = now.getFullYear();
    const m = String(now.getMonth() + 1).padStart(2, '0');
    const d = String(now.getDate()).padStart(2, '0');
    filterTo.value = `${y}-${m}-${d}`;

    const first = new Date(now.getFullYear(), now.getMonth(), 1);
    const fm = String(first.getMonth() + 1).padStart(2, '0');
    const fd = String(first.getDate()).padStart(2, '0');
    filterFrom.value = `${first.getFullYear()}-${fm}-${fd}`;
  }

  function updateKpis(periodo) {
    const kpi = demoData.kpiByPeriodo[periodo] || demoData.kpiByPeriodo.mes;

    document.getElementById('kpiInstructores').textContent = kpi.instructores.toLocaleString('es-CO');
    document.getElementById('kpiHoras').textContent = formatHoras(kpi.horas);
    document.getElementById('kpiOcupacion').textContent = `${kpi.ocupacion.toFixed(1)}%`;
    document.getElementById('kpiCumplimiento').textContent = `${kpi.cumplimiento.toFixed(1)}%`;

    document.getElementById('kpiInstructoresSub').textContent = kpi.hint;
    document.getElementById('kpiHorasSub').textContent = 'Horas ejecutadas del periodo';
    document.getElementById('kpiOcupacionSub').textContent = 'Promedio de uso en ambientes';
    document.getElementById('kpiCumplimientoSub').textContent = 'Avance sobre planificación';
  }

  function createCharts() {
    const barCtx = document.getElementById('barChart');
    const lineCtx = document.getElementById('lineChart');
    const donutCtx = document.getElementById('donutChart');

    barChart = new Chart(barCtx, {
      type: 'bar',
      data: {
        labels: [],
        datasets: [{
          label: 'Ocupación (%)',
          data: [],
          borderRadius: 8,
          backgroundColor: [ClassControl.colors.primary, ClassControl.colors.navy, ClassControl.colors.accent, ClassControl.colors.warning]
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: false }
        },
        scales: {
          y: { beginAtZero: true, max: 100, ticks: { callback: v => `${v}%` } }
        }
      }
    });

    lineChart = new Chart(lineCtx, {
      type: 'line',
      data: {
        labels: ['Semana 1', 'Semana 2', 'Semana 3', 'Semana 4'],
        datasets: [{
          label: 'Horas',
          data: [],
          borderColor: ClassControl.colors.primary,
          backgroundColor: 'rgba(56, 168, 0, .15)',
          fill: true,
          tension: 0.35,
          pointRadius: 4,
          pointHoverRadius: 5
        }]
      },
      options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true }
        }
      }
    });

    donutChart = new Chart(donutCtx, {
      type: 'doughnut',
      data: {
        labels: ['Académico', 'Programación', 'Logística', 'Financiero'],
        datasets: [{
          data: [],
          backgroundColor: [ClassControl.colors.primary, ClassControl.colors.navy, ClassControl.colors.accent, ClassControl.colors.warning]
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            position: 'bottom'
          }
        }
      }
    });
  }

  function updateCharts(periodo, programa) {
    const barData = demoData.barByPrograma[programa] || demoData.barByPrograma.todos;
    barChart.data.labels = barData.map(r => r.label);
    barChart.data.datasets[0].data = barData.map(r => r.value);
    barChart.update();

    lineChart.data.datasets[0].data = demoData.lineByPeriodo[periodo] || demoData.lineByPeriodo.mes;
    lineChart.update();

    donutChart.data.datasets[0].data = demoData.donutByPeriodo[periodo] || demoData.donutByPeriodo.mes;
    donutChart.update();

    document.getElementById('barChartHint').textContent = `Periodo: ${filterPeriodo.options[filterPeriodo.selectedIndex].text}`;
  }

  function stateBadge(status) {
    const map = {
      Generado: 'text-bg-success',
      Aprobado: 'text-bg-primary',
      Pendiente: 'text-bg-warning'
    };
    const cls = map[status] || 'text-bg-secondary';
    return `<span class="badge ${cls}">${status}</span>`;
  }

  function filteredRows() {
    const p = filterPrograma.value;
    const i = filterInstructor.value;
    const a = filterAmbiente.value;
    const from = filterFrom.value;
    const to = filterTo.value;
    const searchText = document.getElementById('searchInput').value.trim().toLowerCase();

    return demoData.tableRows.filter((row) => {
      const rowDate = row.fecha;
      const matchPrograma = !p || p === 'todos' || row.programa.toLowerCase().includes(p);
      const matchInstructor = i === 'todos' || row.instructor.toLowerCase().includes(i);
      const matchAmbiente = a === 'todos' || row.ambiente.toLowerCase().includes(a);
      const matchDate = (!from || rowDate >= from) && (!to || rowDate <= to);
      const matchSearch = !searchText || [row.reporte, row.programa, row.instructor, row.ambiente, row.estado].join(' ').toLowerCase().includes(searchText);

      return matchPrograma && matchInstructor && matchAmbiente && matchDate && matchSearch;
    });
  }

  function initTable() {
    dataTable = $('#reportsTable').DataTable({
      data: [],
      columns: [
        { title: 'Reporte' },
        { title: 'Programa' },
        { title: 'Instructor' },
        { title: 'Ambiente' },
        { title: 'Fecha' },
        { title: 'Horas' },
        { title: 'Estado' }
      ],
      pageLength: 5,
      lengthMenu: [5, 10, 25],
      order: [[4, 'desc']],
      language: {
        search: 'Buscar:',
        lengthMenu: 'Mostrar _MENU_ registros',
        info: 'Mostrando _START_ a _END_ de _TOTAL_ resultados',
        infoEmpty: 'Mostrando 0 a 0 de 0 resultados',
        zeroRecords: 'No se encontraron resultados',
        paginate: { first: 'Primero', last: 'Último', next: 'Siguiente', previous: 'Anterior' }
      }
    });

    refreshTable();
  }

  function refreshTable() {
    const rows = filteredRows();

    dataTable.clear();
    rows.forEach((r) => {
      dataTable.row.add([
        r.reporte,
        r.programa,
        r.instructor,
        r.ambiente,
        r.fecha,
        formatHoras(r.horas).replace('h', ''),
        stateBadge(r.estado)
      ]);
    });
    dataTable.draw();
  }

  function getExportRows() {
    return filteredRows().map((r) => ({
      Reporte: r.reporte,
      Programa: r.programa,
      Instructor: r.instructor,
      Ambiente: r.ambiente,
      Fecha: r.fecha,
      Horas: r.horas,
      Estado: r.estado
    }));
  }

  function exportCSV() {
    const rows = getExportRows();
    if (!rows.length) {
      showToast('No hay datos para exportar.', 'error');
      return;
    }

    const headers = Object.keys(rows[0]);
    const csv = [headers.join(','), ...rows.map(r => headers.map(h => `"${String(r[h]).replace(/"/g, '""')}"`).join(','))].join('\n');
    const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `reportes_${new Date().toISOString().slice(0,10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
    showToast('Reporte CSV exportado.', 'success');
  }

  function exportExcel() {
    const rows = getExportRows();
    if (!rows.length) {
      showToast('No hay datos para exportar.', 'error');
      return;
    }

    const ws = XLSX.utils.json_to_sheet(rows);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Reportes');
    XLSX.writeFile(wb, `reportes_${new Date().toISOString().slice(0,10)}.xlsx`);
    showToast('Reporte Excel exportado.', 'success');
  }

  function exportPDF() {
    const rows = getExportRows();
    if (!rows.length) {
      showToast('No hay datos para exportar.', 'error');
      return;
    }

    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();
    doc.setFontSize(14);
    doc.text('ClassControl - Reportes y Consultas', 14, 16);
    doc.setFontSize(10);
    doc.text(`Generado: ${new Date().toLocaleString('es-CO')}`, 14, 22);

    let y = 32;
    rows.forEach((r, idx) => {
      const line = `${idx + 1}. ${r.Reporte} | ${r.Programa} | ${r.Instructor} | ${r.Ambiente} | ${r.Fecha} | ${r.Horas}h | ${r.Estado}`;
      doc.text(line, 14, y);
      y += 6;
      if (y > 280) {
        doc.addPage();
        y = 20;
      }
    });

    doc.save(`reportes_${new Date().toISOString().slice(0,10)}.pdf`);
    showToast('Reporte PDF exportado.', 'success');
  }

  function validateFilters() {
    filtersForm.classList.remove('was-validated');
    let valid = true;

    const requiredIds = ['filterPeriodo', 'filterPrograma', 'filterFrom', 'filterTo'];
    requiredIds.forEach((id) => {
      const el = document.getElementById(id);
      el.classList.remove('is-invalid');
      if (!el.value) {
        el.classList.add('is-invalid');
        valid = false;
      }
    });

    if (filterFrom.value && filterTo.value && filterFrom.value > filterTo.value) {
      filterTo.classList.add('is-invalid');
      valid = false;
    }

    if (!valid) filtersForm.classList.add('was-validated');
    return valid;
  }

  function applyFilters() {
    if (!validateFilters()) return;

    updateKpis(filterPeriodo.value);
    updateCharts(filterPeriodo.value, filterPrograma.value);
    refreshTable();
    showToast('Filtros aplicados correctamente.', 'info');
  }

  function bindEvents() {
    filtersForm.addEventListener('submit', (e) => {
      e.preventDefault();
      applyFilters();
    });

    document.getElementById('btnApplyFilters').addEventListener('click', (e) => {
      e.preventDefault();
      applyFilters();
    });

    document.getElementById('btnClearFilters').addEventListener('click', () => {
      filtersForm.reset();
      setDefaultDates();
      filterPeriodo.value = 'mes';
      filterPrograma.value = 'todos';
      filterInstructor.value = 'todos';
      filterAmbiente.value = 'todos';
      filtersForm.classList.remove('was-validated');
      ['filterPeriodo', 'filterPrograma', 'filterFrom', 'filterTo'].forEach((id) => {
        document.getElementById(id).classList.remove('is-invalid');
      });
      updateKpis('mes');
      updateCharts('mes', 'todos');
      refreshTable();
      showToast('Filtros restablecidos.', 'info');
    });

    document.getElementById('searchInput').addEventListener('input', () => {
      refreshTable();
    });

    document.getElementById('btnGenerateReport').addEventListener('click', () => {
      applyFilters();
      showToast('Reporte consolidado generado.', 'success');
    });

    document.getElementById('btnExportCSV').addEventListener('click', exportCSV);
    document.getElementById('btnExportExcel').addEventListener('click', exportExcel);
    document.getElementById('btnExportPDF').addEventListener('click', exportPDF);

    document.getElementById('btnTableCSV').addEventListener('click', exportCSV);
    document.getElementById('btnTableExcel').addEventListener('click', exportExcel);
    document.getElementById('btnTablePDF').addEventListener('click', exportPDF);
  }

  document.addEventListener('DOMContentLoaded', () => {
    setDefaultDates();
    createCharts();
    initTable();
    updateKpis('mes');
    updateCharts('mes', 'todos');
    bindEvents();
  });
})();
