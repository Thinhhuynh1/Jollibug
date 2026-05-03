const API_BASE = "/api/orders";

document.addEventListener("DOMContentLoaded", () => {
    loadOrders();
});

async function loadOrders() {
    const customerId = document.getElementById("customerIdInput").value;
    const message = document.getElementById("message");
    const tbody = document.getElementById("orderTableBody");

    message.textContent = "";
    tbody.innerHTML = "";

    try {
        const response = await fetch(`${API_BASE}?customerId=${customerId}`);

        if (!response.ok) {
            throw new Error("Không thể tải lịch sử đơn hàng.");
        }

        const orders = await response.json();

        if (orders.length === 0) {
            message.textContent = "Khách hàng này chưa có đơn hàng nào.";
            return;
        }

        orders.forEach(order => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>#${order.maDH}</td>
                <td>${formatDate(order.ngayDat)}</td>
                <td>${formatMoney(order.tongTienMon)}</td>
                <td>${formatMoney(order.tienGiamGia)}</td>
                <td>${formatMoney(order.thanhTien)}</td>
                <td><span class="status ${getStatusClass(order.trangThaiDon)}">${displayStatus(order.trangThaiDon)}</span></td>
                <td class="actions">
                    <button onclick="goToDetail(${order.maDH}, ${customerId})">Chi tiết</button>
                    ${renderActionButton(order, customerId)}
                </td>
            `;

            tbody.appendChild(row);
        });

    } catch (error) {
        message.textContent = error.message;
    }
}

function renderActionButton(order, customerId) {
    const status = normalizeStatus(order.trangThaiDon);

    if (status === "PENDING") {
        return `<button class="danger" onclick="cancelOrder(${order.maDH}, ${customerId})">Hủy đơn</button>`;
    }

    if (status === "CONFIRMED") {
        return `<button class="warning" onclick="cancelOrder(${order.maDH}, ${customerId})">Yêu cầu hủy</button>`;
    }

    if (status === "SHIPPING") {
        return `<button class="success" onclick="confirmReceived(${order.maDH}, ${customerId})">Đã nhận hàng</button>`;
    }

    if (status === "DELIVERED") {
        return `<button class="primary" onclick="alert('Bước sau sẽ làm đánh giá đơn hàng')">Đánh giá</button>`;
    }

    return "";
}

async function cancelOrder(orderId, customerId) {
    const confirmed = confirm("Bạn chắc chắn muốn hủy/yêu cầu hủy đơn hàng này?");
    if (!confirmed) return;

    try {
        const response = await fetch(`${API_BASE}/${orderId}/cancel?customerId=${customerId}`, {
            method: "POST"
        });

        const data = await response.json();

        alert(data.message || "Đã xử lý yêu cầu.");
        loadOrders();

    } catch (error) {
        alert("Lỗi khi hủy đơn hàng.");
    }
}

async function confirmReceived(orderId, customerId) {
    const confirmed = confirm("Bạn xác nhận đã nhận được đơn hàng này?");
    if (!confirmed) return;

    try {
        const response = await fetch(`${API_BASE}/${orderId}/received?customerId=${customerId}`, {
            method: "POST"
        });

        const data = await response.json();

        alert(data.message || "Đã xác nhận nhận hàng.");
        loadOrders();

    } catch (error) {
        alert("Lỗi khi xác nhận nhận hàng.");
    }
}

function goToDetail(orderId, customerId) {
    window.location.href = `/client/orders/detail?orderId=${orderId}&customerId=${customerId}`;
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