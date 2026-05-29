function openStatusModal(maDH, currentStatus, afterSuccessCallback) {
    const current = normalizeStatus(currentStatus);

    const oldModal = document.getElementById("runtimeStatusModal");
    if (oldModal) oldModal.remove();

    window.afterOrderStatusUpdated = afterSuccessCallback || null;

    const choicesHtml = getAllStatusActions().map((action) => {
        const enabled = isActionAllowed(current, action.status);
        const disabled = enabled ? "" : "disabled";
        const checked = enabled && action.status === getDefaultNextStatus(current) ? "checked" : "";
        const extraClass = enabled ? "" : "status-choice--disabled";

        return `
            <label class="status-choice ${extraClass}">
                <input
                    type="radio"
                    name="runtimeNextStatus"
                    value="${action.status}"
                    ${checked}
                    ${disabled}
                >
                <span>${action.label}</span>
            </label>
        `;
    }).join("");

    const modal = document.createElement("div");
    modal.id = "runtimeStatusModal";
    modal.className = "runtime-modal-root";

    modal.innerHTML = `
        <div class="runtime-modal-box">
            <div class="runtime-modal-header">
                <h2>Cập nhật trạng thái đơn #${maDH}</h2>
                <button type="button" class="runtime-close-btn" onclick="closeRuntimeStatusModal()">×</button>
            </div>

            <input type="hidden" id="runtimeMaDH" value="${maDH}">
            <input type="hidden" id="runtimeCurrentStatus" value="${current}">

            <div class="status-choice-list">
                ${choicesHtml}
            </div>

            <div class="runtime-modal-actions runtime-modal-actions--single">
                <button type="button" class="runtime-primary-btn runtime-update-btn" onclick="handleRuntimeUpdateClick()">
                    Cập nhật
                </button>
            </div>
        </div>
    `;

    document.body.appendChild(modal);
}

function closeRuntimeStatusModal() {
    const modal = document.getElementById("runtimeStatusModal");
    if (modal) modal.remove();
}

function handleRuntimeUpdateClick() {
    const selectedStatus = document.querySelector('input[name="runtimeNextStatus"]:checked');

    if (!selectedStatus) {
        alert("Vui lòng chọn thao tác cập nhật.");
        return;
    }

    const status = normalizeStatus(selectedStatus.value);

    if (status === "CANCELLED") {
        openCancelConfirmModal();
        return;
    }

    submitRuntimeUpdateStatus();
}

function openCancelConfirmModal() {
    const oldConfirm = document.getElementById("runtimeCancelConfirmModal");
    if (oldConfirm) oldConfirm.remove();

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

            <div class="runtime-modal-actions runtime-modal-actions--single">
                <button type="button" class="runtime-danger-btn" onclick="confirmCancelOrder()">
                    Xác nhận hủy
                </button>
            </div>
        </div>
    `;

    document.body.appendChild(confirmModal);

    document.querySelectorAll('input[name="cancelReason"]').forEach((input) => {
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
    if (confirmModal) confirmModal.remove();
}

function confirmCancelOrder() {
    const selectedReason = document.querySelector('input[name="cancelReason"]:checked');
    const otherReason = document.getElementById("otherCancelReason");

    if (!selectedReason) {
        alert("Vui lòng chọn lý do hủy đơn.");
        return;
    }

    let reason = selectedReason.value;

    if (selectedReason.dataset.other === "true") {
        reason = otherReason.value.trim();
    }

    window.selectedCancelReason = reason;

    closeCancelConfirmModal();
    submitRuntimeUpdateStatus();
}

async function submitRuntimeUpdateStatus() {
    const maDH = document.getElementById("runtimeMaDH").value;
    const selectedStatus = document.querySelector('input[name="runtimeNextStatus"]:checked');
    const status = selectedStatus ? selectedStatus.value : "";

    const staffIdInput = document.getElementById("currentStaffId");
    const staffId = staffIdInput && staffIdInput.value ? staffIdInput.value : "2";

    if (!status) {
        alert("Vui lòng chọn thao tác cập nhật.");
        return;
    }

    try {
        const params = new URLSearchParams();
        params.append("staffId", staffId);
        params.append("status", status);

        if (normalizeStatus(status) === "CANCELLED" && window.selectedCancelReason) {
            params.append("cancelReason", window.selectedCancelReason);
        }

        const response = await fetch(`/api/staff/orders/${maDH}/status?${params.toString()}`, {
            method: "PUT"
        });

        const data = await response.json();

        alert(data.message || "Đã cập nhật trạng thái.");
        window.selectedCancelReason = "";

        if (response.ok) {
            closeRuntimeStatusModal();

            if (typeof window.afterOrderStatusUpdated === "function") {
                window.afterOrderStatusUpdated(maDH);
            }
        }
    } catch (error) {
        alert("Lỗi khi cập nhật trạng thái.");
    }
}

function getAllStatusActions() {
    return [
        { status: "CONFIRMED", label: "Xác nhận đơn" },
        { status: "SHIPPING", label: "Chuyển sang đang giao" },
        { status: "DELIVERED", label: "Xác nhận đã giao" },
        { status: "CANCELLED", label: "Hủy đơn" }
    ];
}

function getNextStatuses(status) {
    const normalized = normalizeStatus(status);

    if (normalized === "PENDING") return ["CONFIRMED", "CANCELLED"];
    if (normalized === "CONFIRMED") return ["SHIPPING", "CANCELLED"];
    if (normalized === "SHIPPING") return ["DELIVERED"];
    if (normalized === "CANCEL_REQUESTED") return ["CANCELLED", "CONFIRMED"];

    return [];
}

function isActionAllowed(currentStatus, nextStatus) {
    return getNextStatuses(currentStatus).includes(normalizeStatus(nextStatus));
}

function getDefaultNextStatus(currentStatus) {
    const allowed = getNextStatuses(currentStatus);
    return allowed.length > 0 ? allowed[0] : "";
}
