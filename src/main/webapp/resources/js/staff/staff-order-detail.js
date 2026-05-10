const STAFF_API_BASE = "/api/staff/orders";

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

    if (content) content.innerHTML = "Đang tải...";
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

        if (caption) {
            caption.textContent = `Mã đơn #${order.maDH}`;
        }

        content.innerHTML = `
            <div class="detail-grid">
                <p><strong>Mã đơn:</strong> #${order.maDH}</p>
                <p><strong>Mã khách:</strong> ${order.maTKKH}</p>
                <p><strong>Mã nhân viên:</strong> ${order.maTKNV || "-"}</p>
                <p><strong>Ngày đặt:</strong> ${formatDate(order.ngayDat)}</p>
                <p><strong>Mã địa chỉ:</strong> ${order.maDC || "-"}</p>
                <p><strong>Mã giảm giá:</strong> ${order.maGG || "-"}</p>
                <p><strong>Tổng tiền món:</strong> ${formatMoney(order.tongTienMon)}</p>
                <p><strong>Giảm giá:</strong> ${formatMoney(order.tienGiamGia)}</p>
                <p><strong>Thành tiền:</strong> ${formatMoney(order.thanhTien)}</p>
                <p><strong>Trạng thái:</strong> 
                    <span class="status ${getStatusClass(order.trangThaiDon)}">${displayStatus(order.trangThaiDon)}</span>
                </p>
                <p class="full detail-note"><strong>Thông tin giao hàng / ghi chú:</strong><br>${order.ghiChu || "-"}</p>
            </div>
        `;

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

    } catch (error) {
        showMessage(error.message);
        if (content) content.innerHTML = "";
    }
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
