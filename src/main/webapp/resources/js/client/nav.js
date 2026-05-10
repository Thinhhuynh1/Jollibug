document.addEventListener("DOMContentLoaded", function () {
  var page = document.body ? document.body.getAttribute("data-page") : "";
  if (!page) return;

  var links = document.querySelectorAll(".main-nav a, .mobile-nav a");
  links.forEach(function (link) {
    var href = link.getAttribute("href") || "";
    if (href.indexOf(page) >= 0) {
      link.classList.add("is-active");
    }
  });
});
