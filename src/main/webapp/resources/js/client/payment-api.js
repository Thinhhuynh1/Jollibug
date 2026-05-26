const PAYMENT_API_BASE = "/api/payments";

document.addEventListener("DOMContentLoaded", () => {
    const params = getPaymentParams();

    if (!params.maDH || !params.maKH || !params.maPT) {
        showPaymentMessage("Thiếu thông tin thanh toán, vui lòng quay lại lịch sử đơn hàng");
        disableConfirmButton("Không thể xử lý thanh toán");
        return;
    }

    setHiddenValue("maDH", params.maDH);
    setHiddenValue("maKH", params.maKH);
    setHiddenValue("maPT", params.maPT);
    lockPaymentMethods(params.maPT);
    showPaymentView(params.maPT);
    renderPaymentParams(params);
    bindConfirmButton(params);
    loadPaymentInfo(params).finally(() => enableConfirmButton(getConfirmButtonLabel(params.maPT)));
});

function getPaymentParams() {
    const params = new URLSearchParams(window.location.search);

    return {
        maDH: params.get("maDH"),
        maKH: params.get("maKH"),
        maPT: (params.get("maPT") || "").trim().toUpperCase()
    };
}

async function loadPaymentInfo(params) {
    try {
        const response = await fetch(
            `${PAYMENT_API_BASE}/order/${encodeURIComponent(params.maDH)}?maPT=${encodeURIComponent(params.maPT)}`
        );
        const data = await response.json();

        if (!response.ok) {
            showPaymentMessage(data.message || "Không tìm thấy thông tin thanh toán");
            return;
        }

        renderPaymentInfo(data);
    } catch (error) {
        showPaymentMessage("Lỗi khi tải thông tin thanh toán");
    }
}

function bindConfirmButton(params) {
    const confirmBtn = document.getElementById("confirmPaymentBtn");
    if (!confirmBtn) {
        return;
    }

    confirmBtn.addEventListener("click", async () => {
        if (confirmBtn.disabled) {
            return;
        }

        await completePayment(params);
    });
}

async function completePayment(params) {
    showPaymentMessage("Đang xử lý thanh toán...");
    disableConfirmButton("Đang xử lý thanh toán...");
    setText("paymentStatus", "Đang xử lý thanh toán");

    try {
        const response = await fetch(`${PAYMENT_API_BASE}/order/${encodeURIComponent(params.maDH)}/confirm`, {
            method: "PUT"
        });

        let data = {};
        try {
            data = await response.json();
        } catch (error) {
            data = {};
        }

        if (!response.ok || data.success === false) {
            showPaymentMessage(data.message || "Không thể cập nhật trạng thái thanh toán");
            enableConfirmButton(getConfirmButtonLabel(params.maPT));
            setText("paymentStatus", "Chờ thanh toán");
            return;
        }

        showPaymentMessage("Thanh toán thành công");
        disableConfirmButton("Thanh toán thành công");
        setText("paymentStatus", "Đã thanh toán");
        localStorage.removeItem("selectedCheckoutAddress");

        window.setTimeout(() => {
            window.location.href = "/orders/detail?maDH=" + encodeURIComponent(params.maDH);
        }, 1000);
    } catch (error) {
        showPaymentMessage("Không thể xử lý thanh toán, vui lòng thử lại");
        enableConfirmButton(getConfirmButtonLabel(params.maPT));
        setText("paymentStatus", "Chờ thanh toán");
    }
}

function getConfirmButtonLabel(maPT) {
    const normalized = normalizePaymentMethod(maPT);
    if (normalized === "COD") {
        return "Xác nhận đặt hàng";
    }
    return "Xác nhận thanh toán";
}

function renderPaymentParams(params) {
    setText("paymentOrderId", "#" + params.maDH);
    setText("paymentMethod", displayPaymentMethod(params.maPT));
    setText("paymentStatus", "Chờ thanh toán");
}

function renderPaymentInfo(payment) {
    setText("paymentOrderId", "#" + payment.maDH);
    setText("paymentMethod", displayPaymentMethod(payment.maPT));
    setText("paymentAmount", formatMoney(payment.soTien));
    setText("invoice-total", formatMoney(payment.soTien));
    setText("paymentStatus", displayPaymentStatus(payment.trangThaiTT));
    showPaymentView(payment.maPT);
    lockPaymentMethods(payment.maPT);

    if (normalizeStatus(payment.trangThaiTT) === "PAID") {
        disableConfirmButton("Thanh toán thành công");
        showPaymentMessage("Đơn hàng đã được thanh toán");
    }
}

function lockPaymentMethods(maPT) {
    const selectedValue = normalizePaymentMethod(maPT);

    document.querySelectorAll('input[name="payment-method"]').forEach((input) => {
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

    document.querySelectorAll(".payment-view").forEach((view) => {
        view.style.display = "none";
    });

    const view = document.getElementById(viewMap[normalized] || "view-banking");
    if (view) {
        view.style.display = "block";
    }
}

function normalizePaymentMethod(method) {
    const value = (method || "").trim().toUpperCase();

    if (value === "BANKING") return "BANK";
    if (value === "CREDIT-CARD") return "CREDIT_CARD";

    return value;
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
        FAILED: "Thanh toán thất bại",
        CANCELLED: "Đã hủy"
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

function enableConfirmButton(text) {
    const confirmBtn = document.getElementById("confirmPaymentBtn");

    if (confirmBtn) {
        confirmBtn.disabled = false;
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
