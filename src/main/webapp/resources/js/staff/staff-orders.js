const STAFF_API_BASE = "/api/staff/orders";

document.addEventListener("DOMContentLoaded", () => {
    loadStaffOrders();
});

async function loadStaffOrders() {
    const status = document.getElementById("statusFilter").value;
    const keyword = document.getElementById("keywordFilter").value;
    const fromDate = document.getElementById("fromDateFilter").value;
    const toDate = document.getElementById("toDateFilter").value;

    const message = document.getElementById("message");
    const tbody = document.getElementById("staffOrderTableBody");

    if (!tbody) return;

    if (message) message.textContent = "";
    tbody.innerHTML = "";

    const params = new URLSearchParams();
    if (status) params.append("status", status);
    if (keyword) params.append("keyword", keyword);
    if (fromDate) params.append("fromDate", fromDate);
    if (toDate) params.append("toDate", toDate);

    try {
        const url = params.toString() ? `${STAFF_API_BASE}?${params.toString()}` : STAFF_API_BASE;
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error("Không thể tải danh sách đơn hàng.");
        }

        const orders = await response.json();

        if (orders.length === 0) {
            message.textContent = "Không có đơn hàng phù hợp.";
            return;
        }

        orders.forEach((order) => {
            const row = document.createElement("tr");
            row.classList.add("clickable-row");
            row.dataset.maDh = order.maDH;

            row.innerHTML = `
                <td class="col-order">#${order.maDH}</td>
                <td class="col-customer">${order.maTKKH}</td>
                <td class="col-date">${formatDate(order.ngayDat)}</td>
                <td class="col-money">${formatMoney(order.thanhTien)}</td>
                <td class="col-status">
                    <span class="status ${getStatusClass(order.trangThaiDon)}">${displayStatus(order.trangThaiDon)}</span>
                </td>
                <td class="actions">
                    ${renderUpdateButton(order)}
                </td>
            `;

            row.addEventListener("click", (event) => {
                if (event.target.closest("button, a, input, select, textarea")) {
                    return;
                }

                window.location.href = `/staff/order-detail?maDH=${order.maDH}`;
            });

            tbody.appendChild(row);
        });
    } catch (error) {
        if (message) message.textContent = error.message;
    }
}

function renderUpdateButton(order) {
    const currentStatus = normalizeStatus(order.trangThaiDon);
    const nextStatuses = getNextStatuses(currentStatus);

    if (nextStatuses.length === 0) {
        return "";
    }

    return `
        <button
            type="button"
            class="primary-btn update-status-btn"
            onclick="event.preventDefault(); event.stopPropagation(); openStatusModal(${order.maDH}, '${currentStatus}', () => loadStaffOrders())">
            Cập nhật
        </button>
    `;
}

function resetFilters() {
    document.getElementById("statusFilter").value = "";
    document.getElementById("keywordFilter").value = "";
    document.getElementById("fromDateFilter").value = "";
    document.getElementById("toDateFilter").value = "";
    loadStaffOrders();
}

function normalizeStatus(status) {
    return (status || "").trim().toUpperCase();
}

function displayStatus(status) {
    const statusMap = {
        PENDING: "Chờ xác nhận",
        CONFIRMED: "Đã xác nhận",
        SHIPPING: "Đang giao",
        DELIVERED: "Đã giao",
        CANCEL_REQUESTED: "Yêu cầu hủy",
        CANCELLED: "Đã hủy"
    };

    return statusMap[normalizeStatus(status)] || status;
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
