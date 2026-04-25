//cuộn danh sách trái phải
(function () {
  var strip = document.querySelector("[data-menu-cats]");
  if (!strip) return;

  var prevBtn = document.querySelector("[data-cat-arrow='prev']");
  var nextBtn = document.querySelector("[data-cat-arrow='next']");
  if (!prevBtn || !nextBtn) return;

  function scrollStep() {
    return Math.max(180, Math.floor(strip.clientWidth * 0.75));
  }

  function updateArrows() {
    var maxScroll = strip.scrollWidth - strip.clientWidth;
    var hasOverflow = maxScroll > 1;

    prevBtn.classList.toggle("is-hidden", !hasOverflow);
    nextBtn.classList.toggle("is-hidden", !hasOverflow);

    prevBtn.disabled = !hasOverflow || strip.scrollLeft <= 1;
    nextBtn.disabled = !hasOverflow || strip.scrollLeft >= maxScroll - 1;
  }

  prevBtn.addEventListener("click", function () {
    strip.scrollBy({ left: -scrollStep(), behavior: "smooth" });
  });

  nextBtn.addEventListener("click", function () {
    strip.scrollBy({ left: scrollStep(), behavior: "smooth" });
  });

  strip.querySelectorAll(".filter-pill").forEach(function (pill) {
    pill.addEventListener("click", function () {
      strip.querySelectorAll(".filter-pill").forEach(function (item) {
        item.classList.remove("is-active");
      });
      pill.classList.add("is-active");
    });
  });

  strip.addEventListener("scroll", updateArrows, { passive: true });
  window.addEventListener("resize", updateArrows);
  updateArrows();
})();
