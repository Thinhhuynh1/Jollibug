(function () {
  "use strict";

  function formatMoney(value) {
    return new Intl.NumberFormat("vi-VN").format(value) + "đ";
  }

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

  /* ── Tính năng 1: Tìm kiếm + Tính năng 3: Sắp xếp ── */
  function initProductFilters() {
    const form = document.getElementById("product-filter-form");
    if (!form) return;

    ["categoryID", "filter", "status"].forEach(function (id) {
      const el = document.getElementById(id);
      if (el) {
        el.addEventListener("change", function () {
          form.submit();
        });
      }
    });
  }

  /* ── Tính năng 2: Xem chi tiết (panel + API) ── */
  function initProductDetailPanel() {
    const panel = document.getElementById("product-detail-panel");
    if (!panel) return;

    const fields = {
      name: document.getElementById("sdp-product-name"),
      price: document.getElementById("sdp-product-price"),
      cat: document.getElementById("sdp-product-cat"),
      badge: document.getElementById("sdp-product-badge"),
      img: document.getElementById("sdp-product-image"),
      id: document.getElementById("sdp-product-id"),
      stock: document.getElementById("sdp-product-stock"),
      sold: document.getElementById("sdp-product-sold"),
      desc: document.getElementById("sdp-product-desc"),
      editBtn: document.getElementById("sdp-edit-btn"),
    };

    function closePanel() {
      panel.classList.add("sdp--hidden");
    }

    function openPanel() {
      panel.classList.remove("sdp--hidden");
    }

    function setStatusBadge(badgeEl, product) {
      if (!product.available) {
        badgeEl.textContent = "Tạm ẩn";
        badgeEl.dataset.status = "inactive";
      } else if (product.soLuongTon === 0) {
        badgeEl.textContent = "Hết hàng";
        badgeEl.dataset.status = "out-of-stock";
      } else {
        badgeEl.textContent = "Đang bán";
        badgeEl.dataset.status = "active";
      }
    }

    function fillPanel(product) {
      fields.name.textContent = product.tenMon || "—";
      fields.price.textContent = formatMoney(product.gia || 0);
      fields.cat.textContent = product.tenDM || "—";
      fields.id.textContent = product.maMon || "—";
      fields.stock.textContent = product.soLuongTon ?? "—";
      fields.sold.textContent = product.soLuongDaBan ?? "—";
      fields.desc.textContent = product.moTa || "Chưa có mô tả";
      setStatusBadge(fields.badge, product);

      if (product.img) {
        fields.img.src = "/images/" + product.img;
        fields.img.style.display = "block";
      } else {
        fields.img.removeAttribute("src");
        fields.img.style.display = "none";
      }

      fields.editBtn.href = "/manager/products/update?productID=" + product.maMon;
    }

    function loadDetail(productId) {
      fetch("/api/manager/products/" + productId)
        .then(function (res) { return res.json(); })
        .then(function (json) {
          if (!json.success || !json.data) {
            alert(json.message || "Không tải được chi tiết món ăn.");
            return;
          }
          fillPanel(json.data);
          openPanel();
        })
        .catch(function () {
          alert("Lỗi kết nối khi tải chi tiết món ăn.");
        });
    }

    document.querySelectorAll("[data-product-detail]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        loadDetail(btn.getAttribute("data-product-detail"));
      });
    });

    panel.querySelectorAll("[data-sdp-close]").forEach(function (el) {
      el.addEventListener("click", closePanel);
    });

    document.addEventListener("keydown", function (e) {
      if (e.key === "Escape" && !panel.classList.contains("sdp--hidden")) {
        closePanel();
      }
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    initImagePreview();
    initProductForm();
    initDeletePage();
    initProductFilters();
    initProductDetailPanel();
  });
})();
