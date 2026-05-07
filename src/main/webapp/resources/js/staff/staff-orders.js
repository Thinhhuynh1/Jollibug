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

    message.textContent = "";
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

            row.innerHTML = `
                <td>#${order.maDH}</td>
                <td>${order.maTKKH}</td>
                <td>${order.maTKNV || "-"}</td>
                <td>${formatDate(order.ngayDat)}</td>
                <td>${formatMoney(order.thanhTien)}</td>
                <td><span class="status ${getStatusClass(order.trangThaiDon)}">${displayStatus(order.trangThaiDon)}</span></td>
                <td>${order.ghiChu || "-"}</td>
                <td class="actions">
                    <button onclick="openOrderDetail(${order.maDH})" class="secondary-btn">Chi tiết</button>
                    ${renderUpdateButton(order)}
                </td>
            `;

            tbody.appendChild(row);
        });

    } catch (error) {
        message.textContent = error.message;
    }
}

function renderUpdateButton(order) {
    const nextStatuses = getNextStatuses(order.trangThaiDon);

    if (nextStatuses.length === 0) {
        return "";
    }

    return `<button onclick="openStatusModal(${order.maDH}, '${normalizeStatus(order.trangThaiDon)}')" class="primary-btn">Cập nhật</button>`;
}

async function openOrderDetail(orderId) {
    const content = document.getElementById("orderDetailContent");
    const itemBody = document.getElementById("staffOrderItemBody");

    content.innerHTML = "Đang tải...";
    itemBody.innerHTML = "";

    try {
        const response = await fetch(`${STAFF_API_BASE}/${orderId}`);

        if (!response.ok) {
            throw new Error("Không thể tải chi tiết đơn hàng.");
        }

        const data = await response.json();

        const order = data.order || data.donHang;
        const items = data.orderItems || data.chiTietDH || [];

        content.innerHTML = `
            <div class="detail-grid">
                <p><strong>Mã đơn:</strong> #${order.maDH}</p>
                <p><strong>Mã khách:</strong> ${order.maTKKH}</p>
                <p><strong>Mã nhân viên:</strong> ${order.maTKNV || "-"}</p>
                <p><strong>Ngày đặt:</strong> ${formatDate(order.ngayDat)}</p>
                <p><strong>Tổng tiền món:</strong> ${formatMoney(order.tongTienMon)}</p>
                <p><strong>Giảm giá:</strong> ${formatMoney(order.tienGiamGia)}</p>
                <p><strong>Thành tiền:</strong> ${formatMoney(order.thanhTien)}</p>
                <p><strong>Trạng thái:</strong> 
                    <span class="status ${getStatusClass(order.trangThaiDon)}">${displayStatus(order.trangThaiDon)}</span>
                </p>
                <p class="full"><strong>Ghi chú:</strong> ${order.ghiChu || "-"}</p>
            </div>
        `;

        items.forEach(item => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${item.tenMon}</td>
                <td>${item.soLuong}</td>
                <td>${formatMoney(item.donGia)}</td>
                <td>${formatMoney(item.thanhTien)}</td>
            `;

            itemBody.appendChild(row);
        });

        document.getElementById("orderDetailModal").classList.remove("hidden");

    } catch (error) {
        content.innerHTML = `<p class="message">${error.message}</p>`;
        document.getElementById("orderDetailModal").classList.remove("hidden");
    }
}

function closeOrderDetailModal() {
    document.getElementById("orderDetailModal").classList.add("hidden");
}

function openStatusModal(orderId, currentStatus) {
    document.getElementById("selectedOrderId").value = orderId;

    const select = document.getElementById("nextStatusSelect");
    select.innerHTML = "";

    const nextStatuses = getNextStatuses(currentStatus);

    nextStatuses.forEach(status => {
        const option = document.createElement("option");
        option.value = status;
        option.textContent = displayStatus(status);
        select.appendChild(option);
    });

    document.getElementById("statusModal").classList.remove("hidden");
}

function closeStatusModal() {
    document.getElementById("statusModal").classList.add("hidden");
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