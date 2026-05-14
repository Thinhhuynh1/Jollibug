const PAYMENT_API_BASE = "/api/payments";

document.addEventListener("DOMContentLoaded", () => {
    const orderId = getOrderIdFromUrl();

    if (!orderId) {
        showPaymentMessage("Thiếu mã đơn hàng. Vui lòng quay lại đơn hàng để thanh toán.");
        disableConfirmButton();
        return;
    }

    loadPaymentInfo(orderId);

    const confirmBtn = document.getElementById("confirmPaymentBtn");
    if (confirmBtn) {
        confirmBtn.addEventListener("click", () => confirmPayment(orderId));
    }
});

function getOrderIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("orderId");
}

async function loadPaymentInfo(orderId) {
    try {
        const response = await fetch(`${PAYMENT_API_BASE}/order/${orderId}`);
        const data = await response.json();

        if (!response.ok) {
            showPaymentMessage(data.message || "Không tìm thấy thông tin thanh toán.");
            disableConfirmButton();
            return;
        }

        renderPaymentInfo(data);

    } catch (error) {
        showPaymentMessage("Lỗi khi tải thông tin thanh toán.");
        disableConfirmButton();
    }
}

function renderPaymentInfo(payment) {
    const orderIdEl = document.getElementById("paymentOrderId");
    const methodEl = document.getElementById("paymentMethod");
    const amountEl = document.getElementById("paymentAmount");
    const statusEl = document.getElementById("paymentStatus");
    const totalEl = document.getElementById("invoice-total");
    const confirmBtn = document.getElementById("confirmPaymentBtn");

    if (orderIdEl) orderIdEl.textContent = "#" + payment.maDH;
    if (methodEl) methodEl.textContent = displayPaymentMethod(payment.maPT);
    if (amountEl) amountEl.textContent = formatMoney(payment.soTien);
    if (totalEl) totalEl.textContent = formatMoney(payment.soTien);
    if (statusEl) statusEl.textContent = displayPaymentStatus(payment.trangThaiTT);

    showPaymentView(payment.maPT);
    lockPaymentMethods(payment.maPT);

    if (confirmBtn && normalizeStatus(payment.trangThaiTT) === "PAID") {
        confirmBtn.disabled = true;
        confirmBtn.textContent = "Đã thanh toán";
        showPaymentMessage("Đơn hàng này đã được thanh toán.");
    }
}

function lockPaymentMethods(maPT) {
    const selectedValue = normalizePaymentMethod(maPT);

    document.querySelectorAll('input[name="payment-method"]').forEach(input => {
        input.checked = input.value === selectedValue;
        input.disabled = true;
    });
}

async function confirmPayment(orderId) {
    const confirmBtn = document.getElementById("confirmPaymentBtn");

    if (confirmBtn) {
        confirmBtn.disabled = true;
        confirmBtn.textContent = "Đang xử lý...";
    }

    try {
        const response = await fetch(`${PAYMENT_API_BASE}/order/${orderId}/confirm`, {
            method: "PUT"
        });

        const data = await response.json();

        showPaymentMessage(data.message || "Đã xử lý thanh toán.");

        if (response.ok) {
            loadPaymentInfo(orderId);
            return;
        }

        if (confirmBtn) {
            confirmBtn.disabled = false;
            confirmBtn.textContent = "Xác nhận thanh toán";
        }

    } catch (error) {
        showPaymentMessage("Lỗi khi xác nhận thanh toán.");

        if (confirmBtn) {
            confirmBtn.disabled = false;
            confirmBtn.textContent = "Xác nhận thanh toán";
        }
    }
}

function selectPaymentMethod(maPT) {
    const normalized = normalizePaymentMethod(maPT);

    document.querySelectorAll('input[name="payment-method"]').forEach(input => {
        input.checked = input.value === normalized;
    });
}

function showPaymentView(maPT) {
    const normalized = normalizePaymentMethod(maPT);

    document.querySelectorAll(".payment-view").forEach(view => {
        view.style.display = "none";
    });

    const view = document.getElementById(`view-${normalized}`);
    if (view) {
        view.style.display = "block";
    }
}

function normalizePaymentMethod(method) {
    const m = (method || "").trim().toUpperCase();

    if (m === "COD") return "cod";
    if (m === "BANK") return "banking";
    if (m === "EWALLET") return "ewallet";
    if (m === "CREDIT_CARD") return "credit-card";

    return "cod";
}

function displayPaymentMethod(method) {
    const m = (method || "").trim().toUpperCase();

    const map = {
        COD: "Thanh toán khi nhận hàng (COD)",
        BANK: "Chuyển khoản ngân hàng",
        EWALLET: "Ví điện tử",
        CREDIT_CARD: "Thẻ tín dụng / Ghi nợ"
    };

    return map[m] || method;
}

function displayPaymentStatus(status) {
    const s = normalizeStatus(status);

    const map = {
        PENDING: "Chờ thanh toán",
        PAID: "Đã thanh toán",
        FAILED: "Thanh toán thất bại"
    };

    return map[s] || status;
}

function normalizeStatus(status) {
    return (status || "").trim().toUpperCase();
}

function formatMoney(value) {
    return Number(value || 0).toLocaleString("vi-VN") + " VND";
}

function showPaymentMessage(message) {
    const messageEl = document.getElementById("paymentMessage");

    if (messageEl) {
        messageEl.textContent = message || "";
    }
    const amountEl = document.getElementById("paymentAmount");
    const totalEl = document.getElementById("invoice-total");

    if (amountEl) amountEl.textContent = formatMoney(payment.soTien);
    if (totalEl) totalEl.textContent = formatMoney(payment.soTien);
}

function disableConfirmButton() {
    const confirmBtn = document.getElementById("confirmPaymentBtn");

    if (confirmBtn) {
        confirmBtn.disabled = true;
    }
}