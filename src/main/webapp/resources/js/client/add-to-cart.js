(function () {
  const API_URL = "/api/cart/items";

  function updateHeaderCartCount(cartCount) {
    const countEl = document.getElementById("header-cart-count");
    if (countEl && Number.isFinite(cartCount)) {
      countEl.textContent = String(cartCount);
    }
  }

  async function submitAddToCart(form) {
    const submitBtn = form.querySelector('button[type="submit"]');
    const formData = new FormData(form);

    if (submitBtn) {
      submitBtn.disabled = true;
    }

    try {
      const response = await fetch(API_URL, {
        method: "POST",
        body: formData
      });

      const data = await response.json();

      if (!response.ok || !data.success) {
        return;
      }

      updateHeaderCartCount(Number(data.cartCount));
    } catch (error) {
    } finally {
      if (submitBtn) {
        submitBtn.disabled = false;
      }
    }
  }

  document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("form[data-ajax-add-cart]").forEach((form) => {
      form.addEventListener("submit", (event) => {
        event.preventDefault();
        submitAddToCart(form);
      });
    });
  });
})();
