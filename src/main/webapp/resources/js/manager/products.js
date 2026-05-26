(function () {
  "use strict";

  function initImagePreview() {
    const fileInput = document.getElementById("productFile");
    const preview = document.getElementById("productPreview");
    if (!fileInput || !preview) return;

    fileInput.addEventListener("change", function (e) {
      const file = e.target.files && e.target.files[0];
      if (!file) return;
      preview.src = URL.createObjectURL(file);
      preview.style.display = "block";
    });

    if (preview.getAttribute("src")) {
      preview.style.display = "block";
    }
  }

  function initProductForm() {
    const form = document.querySelector("[data-product-form]");
    if (!form) return;

    form.addEventListener("submit", function (e) {
      const tenMon = form.querySelector('[name="tenMon"]');
      const gia = form.querySelector('[name="gia"]');
      const soLuongTon = form.querySelector('[name="soLuongTon"]');

      if (!tenMon || !tenMon.value.trim()) {
        e.preventDefault();
        alert("Vui lòng nhập tên món ăn.");
        tenMon && tenMon.focus();
        return;
      }
      if (gia && Number(gia.value) < 0) {
        e.preventDefault();
        alert("Giá bán không được âm.");
        gia.focus();
        return;
      }
      if (soLuongTon && Number(soLuongTon.value) < 0) {
        e.preventDefault();
        alert("Tồn kho không được âm.");
        soLuongTon.focus();
        return;
      }
    });
  }

  function initDeletePage() {
    const form = document.querySelector("[data-product-delete-form]");
    if (!form) return;

    const checkbox = form.querySelector("[data-delete-confirm-check]");
    const submitBtn = form.querySelector("[data-delete-submit]");
    if (!checkbox || !submitBtn) return;

    function syncSubmitState() {
      submitBtn.disabled = !checkbox.checked;
    }

    checkbox.addEventListener("change", syncSubmitState);
    syncSubmitState();
  }

  document.addEventListener("DOMContentLoaded", function () {
    initImagePreview();
    initProductForm();
    initDeletePage();
  });
})();
