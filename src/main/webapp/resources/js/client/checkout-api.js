const CHECKOUT_API_BASE = "/api/checkout";
const CART_API_BASE = "/api/cart";

let checkoutSubtotal = 0;
let checkoutDiscount = 0;
let checkoutTotal = 0;
const deliveryFee = 0;

document.addEventListener("DOMContentLoaded", function () {
    initSelectedAddress();

    const checkoutButton = document.getElementById("checkoutButton");

    if (checkoutButton) {
        checkoutButton.addEventListener("click", submitCheckout);
        console.log("[CHECKOUT] Đã gắn sự kiện cho nút thanh toán.");
    } else {
        console.error("[CHECKOUT] Không tìm thấy nút #checkoutButton.");
    }
});

function initSelectedAddress() {
    const selectedAddressRaw = localStorage.getItem("selectedCheckoutAddress");

    if (!selectedAddressRaw) {
        return;
    }

    try {
        const selectedAddress = JSON.parse(selectedAddressRaw);

        const addressSelect = document.getElementById("addressSelect");
        const nameInput = document.getElementById("delivery-name");
        const phoneInput = document.getElementById("delivery-phone");
        const emailInput = document.getElementById("delivery-email");
        const addressInput = document.getElementById("delivery-address");

        if (addressSelect) {
            addressSelect.value = selectedAddress.maDC || "";
        }

        if (nameInput) {
            nameInput.value = selectedAddress.name || "";
        }

        if (phoneInput) {
            phoneInput.value = selectedAddress.phone || "";
        }

        if (emailInput) {
            emailInput.value = selectedAddress.email || "";
        }

        if (addressInput) {
            addressInput.value = selectedAddress.address || "";
        }
    } catch (error) {
        localStorage.removeItem("selectedCheckoutAddress");
    }
}

function getValue(id) {
    const el = document.getElementById(id);
    return el ? el.value : "";
}

function getCustomerId() {
    return Number(getValue("customerId"));
}

function loadCheckoutSummary() {
    const itemList = document.getElementById("checkoutItemList");
    const messageEl = document.getElementById("checkoutMessage");

    if (messageEl) messageEl.textContent = "";

    if (!itemList) {
        updateInvoice(0, 0);
        return;
    }

    const rows = itemList.querySelectorAll(".checkout-session-item");

    if (!rows || rows.length === 0) {
        itemList.innerHTML = `
            <div class="invoice-line">
                <span>Giỏ hàng đang trống</span>
                <strong>0 VND</strong>
            </div>
        `;

        updateInvoice(0, 0);
        return;
    }

    let subtotal = 0;

    rows.forEach(row => {
        subtotal += Number(row.dataset.lineTotal || 0);
    });

    updateInvoice(subtotal, 0);
}

function applyVoucherPreview() {
    const code = getValue("voucher-code").trim().toUpperCase();

    if (!code) {
        checkoutDiscount = 0;
        updateInvoice(checkoutSubtotal, checkoutDiscount);
        showCheckoutMessage("");
        return;
    }

    // Preview tạm trên frontend để người dùng thấy thay đổi.
    // Backend vẫn là nơi tính giảm giá chính xác khi POST /api/checkout.
    if (code === "JOLLI10") {
        checkoutDiscount = Math.round(checkoutSubtotal * 0.1);
        showCheckoutMessage("Đã áp dụng mã JOLLI10. Giảm 10% tạm tính.");
    } else if (code === "FREESHIP20") {
        checkoutDiscount = checkoutSubtotal >= 80000 ? 20000 : 0;
        showCheckoutMessage(checkoutDiscount > 0
            ? "Đã áp dụng mã FREESHIP20."
            : "Mã FREESHIP20 chỉ áp dụng cho đơn từ 80.000 VND.");
    } else {
        checkoutDiscount = 0;
        showCheckoutMessage("Mã giảm giá sẽ được kiểm tra khi đặt hàng.");
    }

    updateInvoice(checkoutSubtotal, checkoutDiscount);
}

function updateInvoice(subtotal, discount) {
    const subtotalEl = document.getElementById("invoice-subtotal");
    const deliveryFeeEl = document.getElementById("invoice-delivery-fee");
    const discountEl = document.getElementById("invoice-discount");
    const totalEl = document.getElementById("invoice-total");

    checkoutSubtotal = Number(subtotal || 0);
    checkoutDiscount = Number(discount || 0);
    checkoutTotal = Math.max(checkoutSubtotal + deliveryFee - checkoutDiscount, 0);

    if (subtotalEl) subtotalEl.textContent = formatMoney(checkoutSubtotal);
    if (deliveryFeeEl) deliveryFeeEl.textContent = formatMoney(deliveryFee);
    if (discountEl) discountEl.textContent = "-" + formatMoney(checkoutDiscount);
    if (totalEl) totalEl.textContent = formatMoney(checkoutTotal);
}

async function submitCheckout() {
    const customerId = Number(document.getElementById("customerId")?.value);

    const maDCValue = document.getElementById("addressSelect")?.value;
    const maDC = maDCValue ? Number(maDCValue) : null;

    const selectedPayment = document.querySelector('input[name="payment-method"]:checked');
    const maPT = selectedPayment ? selectedPayment.value : null;
    const discountCode = getValue("voucher-code").trim() || null;
    const ghiChu = buildCheckoutNote();

    console.log("[CHECKOUT FRONTEND]", {
        customerId,
        maDC,
        maPT
    });

    if (!customerId) {
        alert("Không tìm thấy thông tin tài khoản. Vui lòng đăng nhập lại.");
        return;
    }

    if (!maDC) {
        alert("Vui lòng chọn địa chỉ giao hàng.");
        return;
    }

    if (!maPT) {
        alert("Vui lòng chọn phương thức thanh toán.");
        return;
    }

    try {
        const response = await fetch("/api/checkout", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                customerId,
                maDC,
                maPT,
                discountCode,
                ghiChu
            })
        });

        const data = await response.json();

        console.log("[CHECKOUT RESPONSE]", data);

        if (!response.ok || !data.success) {
            alert(data.message || "Thanh toán thất bại.");
            return;
        }

        if (maPT === "COD") {
            alert("Đặt hàng thành công");

            localStorage.removeItem("selectedCheckoutAddress");

            window.location.href = "/orders/detail?orderId=" + encodeURIComponent(data.orderId);
            return;
        }

        window.location.href = "/pay?orderId="
            + encodeURIComponent(data.orderId)
            + "&customerId="
            + encodeURIComponent(customerId)
            + "&maPT="
            + encodeURIComponent(maPT);
    } catch (error) {
        console.error("[CHECKOUT ERROR]", error);
        alert("Có lỗi xảy ra khi thanh toán. Vui lòng thử lại.");
    }
}

function buildCheckoutNote() {
    const name = getValue("delivery-name");
    const phone = getValue("delivery-phone");
    const email = getValue("delivery-email");
    const address = getValue("delivery-address");

    return `Người nhận: ${name}; SĐT: ${phone}; Email: ${email}; Địa chỉ nhập: ${address}`;
}

function showCheckoutMessage(message) {
    const messageEl = document.getElementById("checkoutMessage");

    if (messageEl) {
        messageEl.textContent = message || "";
    }
}

function formatMoney(value) {
    return Number(value || 0).toLocaleString("vi-VN") + " VND";
}

function restoreAddressFromStorage() {
    const addressSelect = document.getElementById("addressSelect");
    const addressInput = document.getElementById("delivery-address");
    const nameInput = document.getElementById("delivery-name");
    const phoneInput = document.getElementById("delivery-phone");
    const emailInput = document.getElementById("delivery-email");

    if (addressSelect && localStorage.getItem("checkout_maDC")) {
        addressSelect.value = localStorage.getItem("checkout_maDC");
    }

    if (addressInput && localStorage.getItem("checkout_address")) {
        addressInput.value = localStorage.getItem("checkout_address");
    }

    if (nameInput && localStorage.getItem("checkout_name")) {
        nameInput.value = localStorage.getItem("checkout_name");
    }

    if (phoneInput && localStorage.getItem("checkout_phone")) {
        phoneInput.value = localStorage.getItem("checkout_phone");
    }

    if (emailInput && localStorage.getItem("checkout_email")) {
        emailInput.value = localStorage.getItem("checkout_email");
    }
}
