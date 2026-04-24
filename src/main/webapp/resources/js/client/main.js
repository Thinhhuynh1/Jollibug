document.addEventListener("DOMContentLoaded", function () {
  // Ensure reveal blocks are visible even without advanced animation logic.
  document.querySelectorAll(".reveal-up").forEach(function (el) {
    el.classList.add("is-visible");
  });

  // Set current year in footer if placeholder exists.
  var yearEl = document.getElementById("footer-year");
  if (yearEl) {
    yearEl.textContent = String(new Date().getFullYear());
  }
});
