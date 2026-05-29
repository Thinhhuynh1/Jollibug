const CHECKOUT_API_BASE = "/api/checkout";
const VOUCHER_VALIDATE_API = "/api/voucher/validate";

let checkoutSubtotal = 0;
let checkoutDiscount = 0;
let checkoutTotal = 0;
const deliveryFee = 0;

document.addEventListener("DOMContentLoaded", function () {
    restoreCheckoutAddress();
    initializeInvoiceFromSession();
    bindVoucherPreview();
    initializeVoucherCarousel();
    bindManualDeliveryInputs();

    const checkoutButton = document.getElementById("checkoutButton");
    if (checkoutButton) {
        checkoutButton.addEventListener("click", submitCheckout);
    }
});

function getValue(id) {
    const element = document.getElementById(id);
    return element ? element.value : "";
}

function setValue(id, value) {
    const element = document.getElementById(id);
    if (element) {
        element.value = value || "";
    }
}

function getMaKH() {
    return Number(getValue("maKH") || 0);
}

function restoreCheckoutAddress() {
    const selectedAddress = readSelectedAddress();

    if (selectedAddress) {
        applyAddressToForm(selectedAddress);
        return;
    }

    const fallbackAddress = buildDefaultAddress();
    applyAddressToForm(fallbackAddress);
}

function readSelectedAddress() {
    const selectedAddress = localStorage.getItem("selectedCheckoutAddress");

    if (!selectedAddress) {
        return null;
    }

    try {
        return JSON.parse(selectedAddress);
    } catch (error) {
        localStorage.removeItem("selectedCheckoutAddress");
        return null;
    }
}

function buildDefaultAddress() {
    const fullAddress = [
        getValue("defaultAddressLine").trim(),
        getValue("defaultWard").trim(),
        getValue("defaultDistrict").trim(),
        getValue("defaultProvince").trim()
    ].filter(Boolean).join(", ");

    return {
        maDC: getValue("addressSelect").trim() || null,
        name: getValue("defaultAddressName").trim() || getValue("currentUserName").trim(),
        phone: getValue("defaultAddressPhone").trim() || getValue("currentUserPhone").trim(),
        email: getValue("currentUserEmail").trim(),
        address: fullAddress
    };
}

function applyAddressToForm(address) {
    if (!address) {
        return;
    }

    setValue("addressSelect", address.maDC || "");
    setValue("delivery-name", address.name || "");
    setValue("delivery-phone", address.phone || "");
    setValue("delivery-email", address.email || "");
    setValue("delivery-address", address.address || "");
}

function bindManualDeliveryInputs() {
    const fieldIds = ["delivery-name", "delivery-phone", "delivery-email", "delivery-address"];

    fieldIds.forEach(function (id) {
        const input = document.getElementById(id);
        if (!input) {
            return;
        }

        input.addEventListener("input", function () {
            setValue("addressSelect", "");
        });
    });
}

function initializeInvoiceFromSession() {
    const rows = document.querySelectorAll(".checkout-session-item");

    if (!rows.length) {
        updateInvoice(0, 0);
        return;
    }

    let subtotal = 0;
    rows.forEach(function (row) {
        subtotal += Number(row.dataset.lineTotal || 0);
    });

    updateInvoice(subtotal, 0);
}

function bindVoucherPreview() {
    const voucherInput = document.getElementById("voucher-code");
    const applyButton = document.getElementById("voucher-apply");

    if (voucherInput) {
        voucherInput.addEventListener("keydown", function (event) {
            if (event.key === "Enter") {
                event.preventDefault();
                applyVoucherPreview();
            }
        });
    }

    if (applyButton) {
        applyButton.addEventListener("click", applyVoucherPreview);
    }
}

function initializeVoucherCarousel() {
    const list = document.querySelector("[data-voucher-list]");
    const prevButton = document.querySelector('[data-voucher-arrow="prev"]');
    const nextButton = document.querySelector('[data-voucher-arrow="next"]');

    if (!list || !prevButton || !nextButton) {
        return;
    }

    const getStep = function () {
        const firstCard = list.querySelector(".voucher-card");
        if (!firstCard) {
            return 260;
        }

        const listStyles = window.getComputedStyle(list);
        const gap = Number.parseFloat(listStyles.columnGap || listStyles.gap || "0");
        return firstCard.getBoundingClientRect().width + gap;
    };

    const updateArrowState = function () {
        const maxScrollLeft = Math.max(0, list.scrollWidth - list.clientWidth);
        const current = Math.ceil(list.scrollLeft);

        prevButton.classList.toggle("is-hidden", current <= 0);
        nextButton.classList.toggle("is-hidden", current >= maxScrollLeft - 2);
    };

    prevButton.addEventListener("click", function () {
        list.scrollBy({ left: -getStep(), behavior: "smooth" });
    });

    nextButton.addEventListener("click", function () {
        list.scrollBy({ left: getStep(), behavior: "smooth" });
    });

    list.addEventListener("scroll", updateArrowState, { passive: true });
    window.addEventListener("resize", updateArrowState);
    updateArrowState();
}

async function applyVoucherPreview() {
    const code = getValue("voucher-code").trim().toUpperCase();

    if (!code) {
        checkoutDiscount = 0;
        updateInvoice(checkoutSubtotal, checkoutDiscount);
        showVoucherMessage("");
        return;
    }

    try {
        const response = await fetch(
            `${VOUCHER_VALIDATE_API}?code=${encodeURIComponent(code)}&subtotal=${encodeURIComponent(checkoutSubtotal)}`
        );

        const data = await response.json();

        if (!response.ok || !data.valid) {
            checkoutDiscount = 0;
            updateInvoice(checkoutSubtotal, checkoutDiscount);
            showVoucherMessage(data.message || "Mã giảm giá không hợp lệ");
            return;
        }

        checkoutDiscount = Number(data.discountAmount || 0);
        updateInvoice(checkoutSubtotal, checkoutDiscount);
        showVoucherMessage(data.message || "Áp dụng mã giảm giá thành công");
    } catch (error) {
        console.error("[VOUCHER VALIDATE ERROR]", error);
        checkoutDiscount = 0;
        updateInvoice(checkoutSubtotal, checkoutDiscount);
        showVoucherMessage("Không thể kiểm tra mã giảm giá lúc này");
    }
}

function updateInvoice(subtotal, discount) {
    checkoutSubtotal = Number(subtotal || 0);
    checkoutDiscount = Number(discount || 0);
    checkoutTotal = Math.max(checkoutSubtotal + deliveryFee - checkoutDiscount, 0);

    const subtotalEl = document.getElementById("invoice-subtotal");
    const deliveryFeeEl = document.getElementById("invoice-delivery-fee");
    const discountEl = document.getElementById("invoice-discount");
    const totalEl = document.getElementById("invoice-total");

    if (subtotalEl) {
        subtotalEl.textContent = formatMoney(checkoutSubtotal);
    }
    if (deliveryFeeEl) {
        deliveryFeeEl.textContent = formatMoney(deliveryFee);
    }
    if (discountEl) {
        discountEl.textContent = "-" + formatMoney(checkoutDiscount);
    }
    if (totalEl) {
        totalEl.textContent = formatMoney(checkoutTotal);
    }
}

async function submitCheckout() {
    const maKH = getMaKH();
    const addressValue = getValue("addressSelect").trim();
    const maDC = addressValue ? Number(addressValue) : null;
    const selectedPayment = document.querySelector('input[name="payment-method"]:checked');
    const maPT = selectedPayment ? selectedPayment.value : null;

    const payload = {
        maKH,
        maDC,
        maPT,
        discountCode: getValue("voucher-code").trim() || null,
        ghiChu: "",
        deliveryName: getValue("delivery-name").trim(),
        deliveryPhone: getValue("delivery-phone").trim(),
        email: getValue("delivery-email").trim(),
        deliveryAddress: getValue("delivery-address").trim()
    };

    if (!payload.maKH) {
        alert("Không tìm thấy thông tin tài khoản, vui lòng đăng nhập lại");
        return;
    }

    if (!payload.maPT) {
        alert("Vui lòng chọn phương thức thanh toán");
        return;
    }

    if (!payload.maDC && !payload.deliveryAddress) {
        alert("Vui lòng nhập địa chỉ giao hàng");
        return;
    }

    try {
        const response = await fetch(CHECKOUT_API_BASE, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (!response.ok || !data.success) {
            alert(data.message || "Thanh toán thất bại");
            return;
        }

        if (payload.maPT === "COD") {
            localStorage.removeItem("selectedCheckoutAddress");
            alert("Đặt hàng thành công");
            window.location.href = "/orders/detail?maDH=" + encodeURIComponent(data.maDH);
            return;
        }

        window.location.href = "/pay?maDH="
            + encodeURIComponent(data.maDH)
            + "&maKH="
            + encodeURIComponent(payload.maKH)
            + "&maPT="
            + encodeURIComponent(payload.maPT);
    } catch (error) {
        console.error("[CHECKOUT ERROR]", error);
        alert("Có lỗi xảy ra khi thanh toán, vui lòng thử lại");
    }
}

function showVoucherMessage(message) {
    const messageEl = document.getElementById("voucher-message");
    if (messageEl) {
        messageEl.textContent = message || "";
    }
}

function formatMoney(value) {
    return Number(value || 0).toLocaleString("vi-VN") + " VND";
}
