const PAYMENT_API_BASE = "/api/payments";
const PAYMENT_REDIRECT_DELAY_SECONDS = 5;

let paymentCountdownTimer = null;

document.addEventListener("DOMContentLoaded", () => {
    const params = getPaymentParams();

    if (!params.orderId || !params.customerId || !params.maPT) {
        showPaymentMessage("Thiếu thông tin thanh toán. Vui lòng quay lại lịch sử đơn hàng.");
        disableConfirmButton("Không thể xử lý thanh toán");
        return;
    }

    setHiddenValue("orderId", params.orderId);
    setHiddenValue("customerId", params.customerId);
    setHiddenValue("maPT", params.maPT);
    lockPaymentMethods(params.maPT);
    showPaymentView(params.maPT);
    renderPaymentParams(params);
    loadPaymentInfo(params).finally(() => startFakePayment(params));
});

function getPaymentParams() {
    const params = new URLSearchParams(window.location.search);

    return {
        orderId: params.get("orderId"),
        customerId: params.get("customerId"),
        maPT: (params.get("maPT") || "").trim().toUpperCase()
    };
}

async function loadPaymentInfo(params) {
    try {
        const response = await fetch(`${PAYMENT_API_BASE}/order/${encodeURIComponent(params.orderId)}`);
        const data = await response.json();

        if (!response.ok) {
            showPaymentMessage(data.message || "Không tìm thấy thông tin thanh toán.");
            return;
        }

        renderPaymentInfo(data);
    } catch (error) {
        showPaymentMessage("Lỗi khi tải thông tin thanh toán. Hệ thống vẫn tiếp tục xác thực thử.");
    }
}

function startFakePayment(params) {
    let remaining = PAYMENT_REDIRECT_DELAY_SECONDS;

    updateProcessingState(remaining);

    clearInterval(paymentCountdownTimer);
    paymentCountdownTimer = setInterval(async () => {
        remaining -= 1;

        if (remaining > 0) {
            updateProcessingState(remaining);
            return;
        }

        clearInterval(paymentCountdownTimer);
        await completeFakePayment(params);
    }, 1000);
}

async function completeFakePayment(params) {
    disableConfirmButton("Thanh toán thành công");

    try {
        const response = await fetch(`${PAYMENT_API_BASE}/order/${encodeURIComponent(params.orderId)}/confirm`, {
            method: "PUT"
        });

        let data = {};
        try {
            data = await response.json();
        } catch (error) {
            data = {};
        }

        if (!response.ok && !data.success) {
            showPaymentMessage(data.message || "Không thể cập nhật trạng thái thanh toán.");
            return;
        }

        showPaymentMessage("Thanh toán thành công");
        localStorage.removeItem("selectedCheckoutAddress");

        window.setTimeout(() => {
            window.location.href = "/orders/detail?orderId=" + encodeURIComponent(params.orderId);
        }, 1000);
    } catch (error) {
        showPaymentMessage("Thanh toán thành công, nhưng chưa cập nhật được trạng thái thanh toán.");
    }
}

function updateProcessingState(remaining) {
    showPaymentMessage(`Đang xử lý thanh toán. Vui lòng chờ ${remaining} giây...`);
    disableConfirmButton(`Đang xử lý thanh toán (${remaining}s)`);
    setText("paymentStatus", "Đang xử lý thanh toán");
}

function renderPaymentParams(params) {
    setText("paymentOrderId", "#" + params.orderId);
    setText("paymentMethod", displayPaymentMethod(params.maPT));
    setText("paymentStatus", "Đang xử lý thanh toán");
}

function renderPaymentInfo(payment) {
    setText("paymentOrderId", "#" + payment.maDH);
    setText("paymentMethod", displayPaymentMethod(payment.maPT));
    setText("paymentAmount", formatMoney(payment.soTien));
    setText("invoice-total", formatMoney(payment.soTien));
    setText("paymentStatus", displayPaymentStatus(payment.trangThaiTT));
    showPaymentView(payment.maPT);
    lockPaymentMethods(payment.maPT);
}

function lockPaymentMethods(maPT) {
    const selectedValue = normalizePaymentMethod(maPT);

    document.querySelectorAll('input[name="payment-method"]').forEach(input => {
        input.checked = normalizePaymentMethod(input.value) === selectedValue;
        input.disabled = true;
    });
}

function showPaymentView(maPT) {
    const normalized = normalizePaymentMethod(maPT);
    const viewMap = {
        COD: "view-cod",
        BANK: "view-banking",
        EWALLET: "view-ewallet",
        CREDIT_CARD: "view-credit-card"
    };

    document.querySelectorAll(".payment-view").forEach(view => {
        view.style.display = "none";
    });

    const view = document.getElementById(viewMap[normalized] || "view-banking");
    if (view) {
        view.style.display = "block";
    }
}

function normalizePaymentMethod(method) {
    const m = (method || "").trim().toUpperCase();

    if (m === "BANKING") return "BANK";
    if (m === "CREDIT-CARD") return "CREDIT_CARD";

    return m;
}

function displayPaymentMethod(method) {
    const map = {
        COD: "Thanh toán khi nhận hàng (COD)",
        BANK: "Chuyển khoản ngân hàng",
        EWALLET: "Ví điện tử",
        CREDIT_CARD: "Thẻ tín dụng / Ghi nợ"
    };

    return map[normalizePaymentMethod(method)] || method || "-";
}

function displayPaymentStatus(status) {
    const map = {
        PENDING: "Chờ thanh toán",
        PAID: "Đã thanh toán",
        FAILED: "Thanh toán thất bại"
    };

    return map[normalizeStatus(status)] || status || "-";
}

function normalizeStatus(status) {
    return (status || "").trim().toUpperCase();
}

function formatMoney(value) {
    return Number(value || 0).toLocaleString("vi-VN") + " VND";
}

function showPaymentMessage(message) {
    setText("paymentMessage", message || "");
}

function disableConfirmButton(text) {
    const confirmBtn = document.getElementById("confirmPaymentBtn");

    if (confirmBtn) {
        confirmBtn.disabled = true;
        if (text) {
            confirmBtn.textContent = text;
        }
    }
}

function setHiddenValue(id, value) {
    const el = document.getElementById(id);
    if (el) {
        el.value = value || "";
    }
}

function setText(id, value) {
    const el = document.getElementById(id);
    if (el) {
        el.textContent = value;
    }
}
