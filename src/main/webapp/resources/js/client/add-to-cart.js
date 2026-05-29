const ADD_TO_CART_API = "/api/cart/items";

function updateHeaderCartCount(cartCount) {
    const cartCountElements = document.querySelectorAll("[data-cart-count]");
    cartCountElements.forEach((element) => {
        element.textContent = String(cartCount);
    });
}

function ensureCartNotice() {
    let notice = document.getElementById("cart-ajax-notice");
    if (notice) {
        return notice;
    }

    notice = document.createElement("div");
    notice.id = "cart-ajax-notice";
    notice.style.position = "fixed";
    notice.style.right = "20px";
    notice.style.bottom = "20px";
    notice.style.zIndex = "9999";
    notice.style.padding = "12px 16px";
    notice.style.borderRadius = "12px";
    notice.style.boxShadow = "0 10px 30px rgba(0, 0, 0, 0.12)";
    notice.style.fontSize = "14px";
    notice.style.fontWeight = "700";
    notice.style.opacity = "0";
    notice.style.transform = "translateY(8px)";
    notice.style.transition = "opacity 0.2s ease, transform 0.2s ease";
    document.body.appendChild(notice);
    return notice;
}

function showCartNotice(message, isSuccess) {
    const notice = ensureCartNotice();
    notice.textContent = message;
    notice.style.background = isSuccess ? "#1f7a1f" : "#b42318";
    notice.style.color = "#ffffff";
    notice.style.opacity = "1";
    notice.style.transform = "translateY(0)";

    window.clearTimeout(showCartNotice.timeoutId);
    showCartNotice.timeoutId = window.setTimeout(() => {
        notice.style.opacity = "0";
        notice.style.transform = "translateY(8px)";
    }, 2200);
}

async function submitAddToCart(form) {
    const formData = new FormData(form);
    const submitButton = form.querySelector('button[type="submit"]');
    const originalLabel = submitButton ? submitButton.innerHTML : "";

    if (submitButton) {
        submitButton.disabled = true;
        submitButton.innerHTML = "Đang thêm...";
    }

    try {
        const response = await fetch(ADD_TO_CART_API, {
            method: "POST",
            body: formData,
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        });

        const data = await response.json().catch(() => ({
            success: false,
            message: "Không thể xử lý phản hồi từ máy chủ."
        }));

        if (!response.ok || !data.success) {
            showCartNotice(data.message || "Thêm vào giỏ hàng thất bại.", false);
            return;
        }

        updateHeaderCartCount(data.cartCount ?? 0);
        showCartNotice(data.message || "Thêm vào giỏ hàng thành công.", true);
    } catch (error) {
        console.error("Add to cart request failed", error);
        showCartNotice("Không thể kết nối đến máy chủ.", false);
    } finally {
        if (submitButton) {
            submitButton.disabled = false;
            submitButton.innerHTML = originalLabel;
        }
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const forms = document.querySelectorAll("form[data-ajax-add-cart]");
    forms.forEach((form) => {
        form.addEventListener("submit", (event) => {
            event.preventDefault();
            submitAddToCart(form);
        });
    });
});
