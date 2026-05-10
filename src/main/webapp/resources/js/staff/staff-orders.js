const STAFF_API_BASE = "/api/staff/orders";

document.addEventListener("DOMContentLoaded", () => {
    loadStaffOrders();
});

document.addEventListener("click", (event) => {
    const updateButton = event.target.closest(".update-status-btn");

    if (!updateButton) {
        return;
    }

    event.preventDefault();
    event.stopPropagation();

    const orderId = updateButton.dataset.orderId;
    const currentStatus = updateButton.dataset.currentStatus;

    openStatusModal(orderId, currentStatus);
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
        const url = params.toString()
            ? `${STAFF_API_BASE}?${params.toString()}`
            : STAFF_API_BASE;

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error("Không thể tải danh sách đơn hàng.");
        }

        const orders = await response.json();

        if (orders.length === 0) {
            message.textContent = "Không có đơn hàng phù hợp.";
            return;
        }

        orders.forEach(order => {
            const row = document.createElement("tr");
            row.classList.add("clickable-row");
            row.dataset.orderId = order.maDH;

            row.innerHTML = `
                <td class="col-order">#${order.maDH}</td>
                <td class="col-customer">${order.maTKKH}</td>
                <td class="col-staff">${order.maTKNV || "-"}</td>
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

                window.location.href = `/order-staff/orders/detail?orderId=${order.maDH}`;
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
            data-order-id="${order.maDH}"
            data-current-status="${currentStatus}">
            Cập nhật
        </button>
    `;
}



function closeStatusModal() {
    const modal = document.getElementById("statusModal");
    if (modal) {
        modal.classList.add("hidden");
    }
}

async function submitUpdateStatus() {
    const orderId = document.getElementById("selectedOrderId").value;
    const staffId = document.getElementById("staffIdInput").value;
    const status = document.getElementById("nextStatusSelect").value;

    if (!staffId) {
        alert("Vui lòng nhập mã nhân viên.");
        return;
    }

    try {
        const response = await fetch(`${STAFF_API_BASE}/${orderId}/status?staffId=${staffId}&status=${status}`, {
            method: "PUT"
        });

        const data = await response.json();

        alert(data.message || "Đã cập nhật trạng thái.");

        if (response.ok) {
            closeStatusModal();
            loadStaffOrders();
        }

    } catch (error) {
        alert("Lỗi khi cập nhật trạng thái.");
    }
}

function resetFilters() {
    document.getElementById("statusFilter").value = "";
    document.getElementById("keywordFilter").value = "";
    document.getElementById("fromDateFilter").value = "";
    document.getElementById("toDateFilter").value = "";
    loadStaffOrders();
}

function getNextStatuses(status) {
    const s = normalizeStatus(status);

    if (s === "PENDING") return ["CONFIRMED", "CANCELLED"];
    if (s === "CONFIRMED") return ["SHIPPING"];
    if (s === "SHIPPING") return ["DELIVERED"];
    if (s === "CANCEL_REQUESTED") return ["CANCELLED", "CONFIRMED"];

    return [];
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

function formatNote(note) {
    if (!note) return "-";
    return note;
}