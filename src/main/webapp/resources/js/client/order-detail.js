const API_BASE = "/api/orders";

document.addEventListener("DOMContentLoaded", () => {
    loadOrderDetail();
});

async function loadOrderDetail() {
    const params = new URLSearchParams(window.location.search);
    const orderId = params.get("orderId");
    const customerId = params.get("customerId");

    const message = document.getElementById("message");
    const orderInfo = document.getElementById("orderInfo");
    const itemBody = document.getElementById("orderItemBody");

    if (!orderId || !customerId) {
        message.textContent = "Thiếu mã đơn hàng hoặc mã khách hàng.";
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/${orderId}?customerId=${customerId}`);

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || "Không thể tải chi tiết đơn hàng.");
        }

        const data = await response.json();
        const order = data.donHang;
        const items = data.chiTietDH || [];

        orderInfo.innerHTML = `
            <p><strong>Mã đơn:</strong> #${order.maDH}</p>
            <p><strong>Ngày đặt:</strong> ${formatDate(order.ngayDat)}</p>
            <p><strong>Trạng thái:</strong> 
                <span class="status ${getStatusClass(order.trangThaiDon)}">
                    ${displayStatus(order.trangThaiDon)}
                </span>
            </p>
            <p><strong>Tổng tiền món:</strong> ${formatMoney(order.tongTienMon)}</p>
            <p><strong>Giảm giá:</strong> ${formatMoney(order.tienGiamGia)}</p>
            <p><strong>Thành tiền:</strong> ${formatMoney(order.thanhTien)}</p>
            <p><strong>Ghi chú:</strong> ${order.ghiChu || "Không có"}</p>
        `;

        itemBody.innerHTML = "";

        items.forEach(item => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${item.tenMon}</td>
                <td>${item.soLuong}</td>
                <td>${formatMoney(item.donGia)}</td>
                <td>${formatMoney(item.thanhTien)}</td>
                <td>${renderReviewButton(order.trangThaiDon, item.maMon)}</td>
            `;

            itemBody.appendChild(row);
        });

    } catch (error) {
        message.textContent = error.message;
    }
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
        DELIVERED: "Đã nhận hàng",
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