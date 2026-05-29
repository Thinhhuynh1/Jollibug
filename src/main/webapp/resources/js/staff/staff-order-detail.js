const STAFF_API_BASE = "/api/staff/orders";

let currentOrder = null;

document.addEventListener("DOMContentLoaded", () => {
    const orderId = getOrderIdFromUrl();

    if (!orderId) {
        showMessage("Thiếu mã đơn hàng.");
        return;
    }

    loadOrderDetail(orderId);
});

function getOrderIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("orderId");
}

async function loadOrderDetail(orderId) {
    const content = document.getElementById("orderDetailContent");
    const itemBody = document.getElementById("staffOrderItemBody");
    const caption = document.getElementById("detailOrderCaption");

    if (content) {
        content.innerHTML = `
            <style>
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
                .demo-loading-spin {
                    width: 48px;
                    height: 48px;
                    border: 5px solid rgba(230, 0, 0, 0.1);
                    border-top-color: var(--color-red-500);
                    border-radius: 50%;
                    animation: spin 1s infinite linear;
                    margin-bottom: 1rem;
                }
            </style>
            <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 4rem 2rem; text-align: center;">
                <div class="demo-loading-spin"></div>
                <h3 style="margin-bottom: 0.5rem; color: var(--color-ink-900);">Đang truy xuất thông tin đơn hàng...</h3>
                <p class="muted">Demo chờ 5 giây theo chế độ SAFE/UNSAFE trên header.</p>
            </div>
        `;
    }
    if (itemBody) itemBody.innerHTML = "";
    if (caption) caption.textContent = "";

    try {
        const response = await fetch(`${STAFF_API_BASE}/${orderId}`);

        if (!response.ok) {
            throw new Error("Không thể tải chi tiết đơn hàng.");
        }

        const data = await response.json();
        const order = data.order || data.donHang;
        const items = data.orderItems || data.chiTietDH || [];

        if (!order) {
            throw new Error("Không tìm thấy dữ liệu đơn hàng.");
        }

        currentOrder = order;

        const pageTitle = document.getElementById("detailPageTitle");
        if (pageTitle)
            pageTitle.textContent = `Chi tiết đơn hàng #${order.maDH}`;

        let payment = null;
        try {
            const paymentResponse = await fetch(`/api/payments/order/${order.maDH}`);
            if (paymentResponse.ok) {
                payment = await paymentResponse.json();
            }
        } catch (e) {
            payment = null;
        }

        if (caption) {
            caption.textContent = "";
        }

        renderOrderDetail(order, payment, data);
        renderOrderItems(items);
        setupDetailUpdateButton(order);

    } catch (error) {
        showMessage(error.message);
        if (content) content.innerHTML = "";
    }
}

function renderOrderDetail(order, payment, demoData) {
    const content = document.getElementById("orderDetailContent");

    if (!content) return;

    const delivery = parseDeliveryInfo(order.ghiChu);
    const demoMode = demoData && demoData.demoMode === "UNSAFE" ? "UNSAFE" : "SAFE";
    const isolation = demoData && demoData.isolation ? demoData.isolation : (demoMode === "UNSAFE" ? "READ_COMMITTED" : "SERIALIZABLE");
    const firstStatus = demoData && demoData.firstStatus ? demoData.firstStatus : "-";
    const secondStatus = demoData && demoData.secondStatus ? demoData.secondStatus : "-";
    const changed = Boolean(demoData && demoData.changed);

    content.innerHTML = `
        <section class="detail-info-card" style="margin-bottom:1rem;">
            <h3>Demo Non-repeatable Read</h3>
            <div class="detail-info-list">
                <p><strong>Mode header:</strong> ${demoMode}</p>
                <p><strong>Isolation:</strong> ${isolation}</p>
                <p><strong>Lần đọc 1:</strong> ${displayStatus(firstStatus)}</p>
                <p><strong>Lần đọc 2:</strong> ${displayStatus(secondStatus)}</p>
                <p class="full"><strong>Kết quả:</strong> ${changed
                    ? "Có Non-repeatable Read: READ COMMITTED đọc lại thấy trạng thái mới."
                    : "Không đổi trong giao tác: SERIALIZABLE giữ snapshot ổn định."}</p>
            </div>
        </section>

        <div class="detail-two-columns">
            <section class="detail-info-card">
                <h3>Thông tin đơn hàng</h3>

                <div class="detail-info-list">
                    <p><strong>Ngày đặt:</strong> ${formatDate(order.ngayDat)}</p>
                    <p><strong>Trạng thái đơn:</strong> 
                        <span class="status ${getStatusClass(order.trangThaiDon)}">${displayStatus(order.trangThaiDon)}</span>
                    </p>
                    <p><strong>Phương thức thanh toán:</strong> ${order.tenPT || displayPaymentMethod(order.maPT)}</p>
                    <p><strong>Trạng thái thanh toán:</strong> ${displayPaymentStatus(order.trangThaiTT)}</p>                    <p><strong>Tổng tiền món:</strong> ${formatMoney(order.tongTienMon)}</p>
                    <p><strong>Giảm giá:</strong> ${formatMoney(order.tienGiamGia)}</p>
                    <p><strong>Thành tiền:</strong> ${formatMoney(order.thanhTien)}</p>
                    <p><strong>Mã giảm giá:</strong> ${order.maGG || "-"}</p>
                </div>
            </section>

            <section class="detail-info-card">
                <h3>Thông tin khách hàng</h3>

                <div class="detail-info-list">
                    <p><strong>Mã khách:</strong> ${order.maTKKH}</p>
                    <p><strong>Tên khách hàng:</strong> ${delivery.name || order.tenKhachHang || order.hoTenKhachHang || "Khách hàng #" + order.maTKKH}</p>
                    <p><strong>Số điện thoại:</strong> ${delivery.phone || order.sdtKhachHang || "-"}</p>
                    <p><strong>Email:</strong> ${delivery.email || order.emailKhachHang || "-"}</p>
                    <p class="full"><strong>Địa chỉ giao hàng:</strong><br>${delivery.address || order.diaChiGiaoHang || order.ghiChu || "-"}</p>
                </div>
            </section>
        </div>
    `;
}

function renderOrderItems(items) {
    const itemBody = document.getElementById("staffOrderItemBody");

    if (!itemBody) return;

    itemBody.innerHTML = "";

    if (!items.length) {
        itemBody.innerHTML = `<tr><td colspan="4" class="empty-cell">Đơn hàng chưa có món.</td></tr>`;
        return;
    }

    items.forEach(item => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${item.tenMon || `Món #${item.maMon}`}</td>
            <td>${item.soLuong || 0}</td>
            <td>${formatMoney(item.donGia)}</td>
            <td>${formatMoney(item.thanhTien)}</td>
        `;

        itemBody.appendChild(row);
    });
}

function setupDetailUpdateButton(order) {
    const updateBtn = document.getElementById("detailUpdateStatusBtn");

    if (!updateBtn) return;

    const nextStatuses = getNextStatuses(order.trangThaiDon);

    if (nextStatuses.length === 0) {
        updateBtn.style.display = "none";
        return;
    }

    updateBtn.style.display = "inline-flex";
    updateBtn.onclick = () => {
        openStatusModal(order.maDH, normalizeStatus(order.trangThaiDon), () => {
            loadOrderDetail(order.maDH);
        });
    };
}

function showMessage(message) {
    const messageEl = document.getElementById("message");
    if (messageEl) messageEl.textContent = message;
}

function normalizeStatus(status) {
    return (status || "").trim().toUpperCase();
}

function displayStatus(status) {
    const s = normalizeStatus(status);

    const statusMap = {
        PENDING: "Chờ xác nhận",
        CONFIRMED: "Đã xác nhận",
        SHIPPING: "Đang giao",
        DELIVERED: "Đã giao",
        CANCEL_REQUESTED: "Yêu cầu hủy",
        CANCELLED: "Đã hủy"
    };

    return statusMap[s] || status;
}

function getStatusClass(status) {
    return normalizeStatus(status).toLowerCase().replace("_", "-");
}

function formatMoney(value) {
    if (value === null || value === undefined) return "0đ";
    return Number(value).toLocaleString("vi-VN") + "đ";
}

function formatDate(value) {
    if (!value) return "";
    return new Date(value).toLocaleString("vi-VN");
}

function parseDeliveryInfo(note) {
    const result = {
        name: "",
        phone: "",
        email: "",
        address: ""
    };

    if (!note) return result;

    const nameMatch = note.match(/Người nhận:\s*([^;]+)/i);
    const phoneMatch = note.match(/SĐT:\s*([^;]+)/i);
    const emailMatch = note.match(/Email:\s*([^;]+)/i);
    const addressMatch = note.match(/Địa chỉ nhập:\s*([^;]+)/i) || note.match(/Địa chỉ:\s*([^;]+)/i);

    result.name = nameMatch ? nameMatch[1].trim() : "";
    result.phone = phoneMatch ? phoneMatch[1].trim() : "";
    result.email = emailMatch ? emailMatch[1].trim() : "";
    result.address = addressMatch ? addressMatch[1].trim() : "";

    return result;
}

function displayPaymentStatus(status) {
    const s = normalizeStatus(status);

    const map = {
        PENDING: "Chờ thanh toán",
        PAID: "Đã thanh toán",
        FAILED: "Thanh toán thất bại"
    };

    return map[s] || status || "-";
}

function displayPaymentMethod(method) {
    const m = normalizeStatus(method);

    const map = {
        COD: "Thanh toán khi nhận hàng",
        BANK: "Chuyển khoản ngân hàng",
        EWALLET: "Ví điện tử",
        CREDIT_CARD: "Thẻ tín dụng / Ghi nợ"
    };

    return map[m] || method || "-";
}
