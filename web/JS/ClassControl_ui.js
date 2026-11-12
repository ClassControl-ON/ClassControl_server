"use strict";

/* Shared UI contract. Page scripts must use these values instead of literals. */
window.ClassControl = Object.freeze({
  colors: Object.freeze({
    primary: "#38A800",
    primaryDark: "#00502A",
    navy: "#00304D",
    accent: "#005580",
    mint: "#C8F0A0",
    surface: "#F4F6F8",
    muted: "#8A9BB0",
    text: "#1A1A2E",
    danger: "#DC3545",
    warning: "#F59E0B",
    info: "#005580"
  }),

  escapeHtml(value) {
    const text = String(value ?? "");
    return text.replace(/[&<>"']/g, character => ({
      "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    })[character]);
  },

  onReady(callback) {
    if (document.readyState === "loading") {
      document.addEventListener("DOMContentLoaded", callback, { once: true });
    } else {
      callback();
    }
  },

  /**
   * Dibuja las tarjetas de métricas de una página.
   * @param {HTMLElement} container contenedor destino
   * @param {Array<{label: string, valor: number, borde: string}>} datos tarjetas
   * @param {string} columnClass clases de la columna Bootstrap
   */
  renderMetricCards(container, datos, columnClass = "col-12 col-md-4") {
    if (!container) return;
    const tarjetas = datos.map(({ label, valor, borde }) => {
      const columna = document.createElement("div");
      columna.className = columnClass;
      columna.innerHTML = `
        <div class="cc-metric">
          <div class="cc-metric-label"></div>
          <div class="cc-metric-value"></div>
        </div>`;
      const tarjeta = columna.firstElementChild;
      tarjeta.querySelector(".cc-metric-label").textContent = label;
      tarjeta.querySelector(".cc-metric-value").textContent = valor;
      if (borde) tarjeta.style.borderLeftColor = borde;
      return columna;
    });
    container.replaceChildren(...tarjetas);
  },

  /**
   * Pinta la paginación estándar (anterior, números, siguiente).
   * @param {HTMLElement} container contenedor destino
   * @param {number} currentPage página actual (1-based)
   * @param {number} totalPages total de páginas
   * @param {(page: number) => void} onSelect callback al elegir una página
   */
  renderPagination(container, currentPage, totalPages, onSelect) {
    if (!container) return;
    const iconButton = (icon, page, disabled) => `
      <button type="button" class="cc-page-btn" data-page="${page}"${disabled ? " disabled" : ""}>
        <span class="material-symbols-outlined" style="font-size:1rem">${icon}</span>
      </button>`;
    const pageButton = page => `
      <button type="button" class="cc-page-btn${page === currentPage ? " active" : ""}" data-page="${page}">${page}</button>`;

    const controles = ClassControl.paginationRange(currentPage, totalPages)
      .map(page => page === "..." ? '<span class="cc-page-ellipsis">…</span>' : pageButton(page));

    container.innerHTML = [
      iconButton("chevron_left", currentPage - 1, currentPage <= 1),
      ...controles,
      iconButton("chevron_right", currentPage + 1, currentPage >= totalPages)
    ].join("");
    container.onclick = event => {
      const boton = event.target.closest("button[data-page]");
      if (!boton || boton.disabled) return;
      onSelect(Number(boton.dataset.page));
    };
  },

  /** Devuelve las páginas visibles, usando "..." para los saltos. */
  paginationRange(current, total) {
    if (total <= 7) return Array.from({ length: total }, (_, index) => index + 1);
    const visibles = new Set(
      [1, current - 1, current, current + 1, total].filter(page => page >= 1 && page <= total)
    );
    const ordenadas = Array.from(visibles).sort((a, b) => a - b);
    const rango = [];
    let anterior = 0;
    for (const page of ordenadas) {
      if (anterior && page - anterior > 1) rango.push("...");
      rango.push(page);
      anterior = page;
    }
    return rango;
  },

  /**
   * Envía un formulario por POST y lanza un Error con el mensaje del backend
   * cuando la respuesta no es satisfactoria.
   */
  async postForm(url, params) {
    const respuesta = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: params
    });
    if (respuesta.ok) return respuesta;
    const datos = await respuesta.json().catch(() => null);
    throw new Error(datos?.error || "Respuesta no exitosa del servidor");
  },

  /** Valida el formato básico de un correo electrónico. */
  esCorreoValido(value) {
    const correo = String(value ?? "").trim();
    const partes = correo.split("@");
    if (partes.length !== 2 || !partes[0] || !partes[1] || /\s/.test(correo)) return false;
    const dominio = partes[1];
    return dominio.includes(".") && !dominio.startsWith(".") && !dominio.endsWith(".");
  }
});
