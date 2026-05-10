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

                window.location.href = `/staff/order-detail?orderId=${order.maDH}`;
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
    if (s === "CONFIRMED") return ["SHIPPING", "CANCELLED"];
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

function closeRuntimeStatusModal() {
    const modal = document.getElementById("runtimeStatusModal");
    if (modal) {
        modal.remove();
    }
}

function handleRuntimeUpdateClick() {
    const selectedStatus = document.querySelector('input[name="runtimeNextStatus"]:checked');

    if (!selectedStatus) {
        alert("Vui lòng chọn trạng thái mới.");
        return;
    }

    const status = normalizeStatus(selectedStatus.value);

    if (status === "CANCELLED") {
        openCancelConfirmModal();
        return;
    }

    submitRuntimeUpdateStatus();
}

function closeCancelConfirmModal() {
    const confirmModal = document.getElementById("runtimeCancelConfirmModal");
    if (confirmModal) {
        confirmModal.remove();
    }
}

function confirmCancelOrder() {
    closeCancelConfirmModal();
    submitRuntimeUpdateStatus();
}

function displayActionLabel(currentStatus, nextStatus) {
    const current = normalizeStatus(currentStatus);
    const next = normalizeStatus(nextStatus);

    if (current === "PENDING" && next === "CONFIRMED") return "Xác nhận đơn";
    if (current === "PENDING" && next === "CANCELLED") return "Hủy đơn";

    if (current === "CONFIRMED" && next === "SHIPPING") return "Chuyển sang đang giao";
    if (current === "CONFIRMED" && next === "CANCELLED") return "Hủy đơn";

    if (current === "SHIPPING" && next === "DELIVERED") return "Xác nhận đã giao";

    if (current === "CANCEL_REQUESTED" && next === "CANCELLED") return "Chấp nhận hủy";
    if (current === "CANCEL_REQUESTED" && next === "CONFIRMED") return "Từ chối hủy";

    return displayStatus(nextStatus);
}

function getAllStatusActions() {
    return [
        { status: "CONFIRMED", label: "Xác nhận đơn" },
        { status: "CANCELLED", label: "Hủy đơn" },
        { status: "SHIPPING", label: "Chuyển sang đang giao" },
        { status: "DELIVERED", label: "Xác nhận đã giao" }
    ];
}

function isActionAllowed(currentStatus, nextStatus) {
    const allowed = getNextStatuses(currentStatus);
    return allowed.includes(normalizeStatus(nextStatus));
}

function getDefaultNextStatus(currentStatus) {
    const allowed = getNextStatuses(currentStatus);
    return allowed.length > 0 ? allowed[0] : "";
}

function openCancelConfirmModal() {
    const oldConfirm = document.getElementById("runtimeCancelConfirmModal");

    if (oldConfirm) {
        oldConfirm.remove();
    }

    const confirmModal = document.createElement("div");
    confirmModal.id = "runtimeCancelConfirmModal";
    confirmModal.className = "runtime-modal-root runtime-modal-root--confirm";

    confirmModal.innerHTML = `
        <div class="runtime-modal-box runtime-modal-box--confirm">
            <div class="runtime-modal-header">
                <h2>Xác nhận hủy đơn</h2>
                <button type="button" class="runtime-close-btn" onclick="closeCancelConfirmModal()">×</button>
            </div>

            <p class="runtime-confirm-text">
                Vui lòng chọn lý do hủy đơn trước khi xác nhận.
            </p>

            <div class="cancel-reason-list">
                ${renderCancelReason("Khách yêu cầu hủy", true)}
                ${renderCancelReason("Không liên hệ được khách hàng", false)}
                ${renderCancelReason("Hết món / Không đủ nguyên liệu", false)}
                ${renderCancelReason("Thông tin giao hàng không hợp lệ", false)}
                ${renderCancelReason("Khác", false, true)}
            </div>

            <textarea
                id="otherCancelReason"
                class="other-cancel-reason hidden"
                placeholder="Nhập lý do hủy khác..."
            ></textarea>

            <div class="runtime-modal-actions">
                <button type="button" class="runtime-danger-btn" onclick="confirmCancelOrder()">
                    Xác nhận hủy
                </button>
            </div>
        </div>
    `;

    document.body.appendChild(confirmModal);

    document.querySelectorAll('input[name="cancelReason"]').forEach(input => {
        input.addEventListener("change", toggleOtherCancelReason);
    });
}

function renderCancelReason(label, checked, isOther) {
    return `
        <label class="cancel-reason-choice">
            <input
                type="radio"
                name="cancelReason"
                value="${label}"
                ${checked ? "checked" : ""}
                data-other="${isOther ? "true" : "false"}"
            >
            <span>${label}</span>
        </label>
    `;
}

function toggleOtherCancelReason() {
    const selected = document.querySelector('input[name="cancelReason"]:checked');
    const textarea = document.getElementById("otherCancelReason");

    if (!selected || !textarea) return;

    if (selected.dataset.other === "true") {
        textarea.classList.remove("hidden");
        textarea.focus();
    } else {
        textarea.classList.add("hidden");
        textarea.value = "";
    }
}

function closeCancelConfirmModal() {
    const confirmModal = document.getElementById("runtimeCancelConfirmModal");

    if (confirmModal) {
        confirmModal.remove();
    }
}

function confirmCancelOrder() {
    const selectedReason = document.querySelector('input[name="cancelReason"]:checked');
    const otherReason = document.getElementById("otherCancelReason");

    if (!selectedReason) {
        alert("Vui lòng chọn lý do hủy đơn.");
        return;
    }

    if (selectedReason.dataset.other === "true" && otherReason && !otherReason.value.trim()) {
        alert("Vui lòng nhập lý do hủy khác.");
        otherReason.focus();
        return;
    }

    closeCancelConfirmModal();
    submitRuntimeUpdateStatus();
}