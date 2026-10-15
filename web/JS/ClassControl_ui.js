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
  }
});
