const CLIENT_ORDER_API = "/api/orders";

document.addEventListener("DOMContentLoaded", () => {
    const orderId = getOrderIdFromUrl();

    if (!orderId) {
        showMessage("Thiếu mã đơn hàng.");
        return;
    }

    initReviewStars();
    loadOrderDetail(orderId);
});

function getOrderIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("orderId");
}

async function loadOrderDetail(orderId) {
    const customerId = getCurrentCustomerId();
    const detailContent = document.getElementById("orderDetailContent");
    const itemBody = document.getElementById("orderItemBody");

    if (!customerId) {
        showMessage("Bạn cần đăng nhập để xem chi tiết đơn hàng.");
        return;
    }

    if (detailContent) detailContent.innerHTML = "Đang tải...";
    if (itemBody) itemBody.innerHTML = "";

    try {
        const response = await fetch(`${CLIENT_ORDER_API}/${orderId}?customerId=${customerId}`);
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Không thể tải chi tiết đơn hàng.");
        }

        const order = data.order || data.donHang;
        const items = data.orderItems || data.chiTietDH || [];

        renderOrderInfo(order);
        renderOrderTimeline(order.trangThaiDon);
        renderOrderItems(items, order.trangThaiDon);
        renderOrderActions(order);

    } catch (error) {
        showMessage(error.message);
        if (detailContent) detailContent.innerHTML = "";
    }
}

function renderOrderInfo(order) {
    const detailContent = document.getElementById("orderDetailContent");
    const title = document.getElementById("orderDetailTitle");

    if (title) {
        title.textContent = `Chi tiết đơn hàng #${order.maDH}`;
    }

    if (!detailContent) return;

    const orderNote = getOrderNote(order);
    const receiverName = order.tenNguoiNhan || order.tenKhachHang || "-";
    const receiverPhone = order.sdtNguoiNhan || order.sdtKhachHang || "-";
    const receiverEmail = order.emailKhachHang || "-";
    const deliveryAddress = order.diaChiGiaoHang || "-";

    detailContent.innerHTML = `
        <div class="client-detail-two-columns">
            <section class="client-detail-info-card">
                <h2>Thông tin đơn hàng</h2>

                <div class="client-detail-info-list">
                    <p><strong>Ngày đặt:</strong> ${formatDate(order.ngayDat)}</p>
                    <p><strong>Trạng thái đơn:</strong> 
                        <span class="status ${getStatusClass(order.trangThaiDon)}">${displayStatus(order.trangThaiDon)}</span>
                    </p>
                    <p><strong>Phương thức thanh toán:</strong> ${escapeHtml(order.tenPT || displayPaymentMethod(order.maPT))}</p>
                    <p><strong>Trạng thái thanh toán:</strong> ${escapeHtml(displayPaymentStatus(order.trangThaiTT))}</p>
                    <p><strong>Tổng tiền món:</strong> ${formatMoney(order.tongTienMon)}</p>
                    <p><strong>Giảm giá:</strong> ${formatMoney(order.tienGiamGia)}</p>
                    <p><strong>Thành tiền:</strong> ${formatMoney(order.thanhTien)}</p>
                    <p><strong>Mã giảm giá:</strong> ${order.maGG || "-"}</p>
                </div>
            </section>

            <section class="client-detail-info-card">
                <h2>Thông tin giao hàng</h2>

                <div class="client-detail-info-list">
                    <p><strong>Người nhận:</strong> ${escapeHtml(receiverName)}</p>
                    <p><strong>Số điện thoại:</strong> ${escapeHtml(receiverPhone)}</p>
                    <p><strong>Email:</strong> ${escapeHtml(receiverEmail)}</p>
                    <p class="full"><strong>Địa chỉ giao hàng:</strong><br>${escapeHtml(deliveryAddress)}</p>
                    <p class="full"><strong>Ghi chú khách hàng:</strong><br>${formatMultilineText(orderNote || "-")}</p>
                </div>
            </section>
        </div>
    `;
}

function renderOrderItems(items, orderStatus) {
    const itemBody = document.getElementById("orderItemBody");

    if (!itemBody) return;

    if (!items.length) {
        itemBody.innerHTML = `
            <tr>
                <td colspan="5" class="empty-cell">Đơn hàng chưa có món.</td>
            </tr>
        `;
        return;
    }

    itemBody.innerHTML = "";

    const canReview = normalizeStatus(orderStatus) === "DELIVERED";

    items.forEach(item => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${item.tenMon || `Món #${item.maMon}`}</td>
            <td>${item.soLuong || 0}</td>
            <td>${formatMoney(item.donGia)}</td>
            <td><strong>${formatMoney(item.thanhTien)}</strong></td>
            <td>
                ${
                    canReview && !item.reviewed
                    ? `<button type="button" class="btn btn-ghost" onclick="openReviewModal(${item.maMon})">Đánh giá</button>`
                    : `<span class="order-note">Chưa thể đánh giá</span>`
                }
            </td>
        `;

        if (item.reviewed) {
            const reviewNote = row.querySelector(".order-note");
            if (reviewNote) reviewNote.textContent = "Đã đánh giá";
        }

        itemBody.appendChild(row);
    });
}

function renderOrderActions(order) {
    const actionBox = document.getElementById("orderDetailActions");
    if (!actionBox) return;

    const status = normalizeStatus(order.trangThaiDon);

    let buttons = "";

    if (status === "PENDING" || status === "CONFIRMED") {
        buttons += `
            <button type="button" class="btn btn-danger client-order-danger-btn" onclick="requestCancelOrder(${order.maDH})">
                Hủy đơn
            </button>
        `;
    }

    if (status === "SHIPPING") {
        buttons += `
            <button type="button" class="btn btn-primary" onclick="confirmReceived(${order.maDH})">
                Đã nhận hàng
            </button>
        `;
    }

    if (status === "CANCEL_REQUESTED") {
        buttons += `<span class="order-note">Đang chờ nhân viên xử lý yêu cầu hủy</span>`;
    }

    if (status === "CANCELLED" || status === "DELIVERED") {
        buttons += `
            <button type="button" class="btn btn-outline reorder-btn" onclick="goToReorderCheckout(${order.maDH})">
                Đặt lại
            </button>
        `;
    }

    actionBox.innerHTML = buttons;
}

function requestCancelOrder(orderId) {
    openClientCancelConfirmModal(orderId, () => loadOrderDetail(orderId));
}

async function confirmReceived(orderId) {
    const customerId = getCurrentCustomerId();

    if (!confirm("Bạn xác nhận đã nhận được đơn hàng này?")) {
        return;
    }

    const response = await fetch(`${CLIENT_ORDER_API}/${orderId}/received?customerId=${customerId}`, {
        method: "POST"
    });

    const data = await response.json();
    alert(data.message || "Đã xác nhận nhận hàng.");

    if (response.ok) {
        loadOrderDetail(orderId);
    }
}

function getCurrentCustomerId() {
    const input = document.getElementById("currentCustomerId");
    return input ? input.value : "";
}

function showMessage(message) {
    const el = document.getElementById("message");
    if (el) el.textContent = message;
}

function normalizeStatus(status) {
    return (status || "").trim().toUpperCase();
}

function displayStatus(status) {
    const map = {
        PENDING: "Đã đặt hàng",
        CONFIRMED: "Đã xác nhận",
        SHIPPING: "Đang giao",
        DELIVERED: "Đã giao",
        CANCEL_REQUESTED: "Đang yêu cầu hủy",
        CANCELLED: "Đã hủy"
    };

    return map[normalizeStatus(status)] || status || "-";
}

function getStatusClass(status) {
    return normalizeStatus(status).toLowerCase().replace("_", "-");
}

function formatMoney(value) {
    if (value === null || value === undefined) return "0đ";
    return Number(value).toLocaleString("vi-VN") + "đ";
}

function formatDate(value) {
    if (!value) return "-";
    return new Date(value).toLocaleString("vi-VN");
}

function openReviewModal(maMon) {
    const modal = document.getElementById("reviewModal");
    const maMonInput = document.getElementById("reviewMaMon");
    const noiDungInput = document.getElementById("reviewNoiDung");
    const saoInput = document.getElementById("reviewSao");
    const imageInput = document.getElementById("reviewImageInput");
    const imagePreview = document.getElementById("reviewImagePreview");

    if (!modal || !maMonInput) return;

    maMonInput.value = maMon;

    if (noiDungInput) noiDungInput.value = "";
    if (saoInput) saoInput.value = "5";
    if (imageInput) imageInput.value = "";
    if (imagePreview) {
        imagePreview.innerHTML = "";
        imagePreview.classList.add("hidden");
    }

    updateReviewRating(5);

    modal.classList.remove("hidden");
}

function closeReviewModal() {
    const modal = document.getElementById("reviewModal");
    if (modal) modal.classList.add("hidden");
}

function initReviewStars() {
    document.querySelectorAll(".review-star").forEach(star => {
        star.addEventListener("click", () => {
            updateReviewRating(Number(star.dataset.rating || 5));
        });
    });

    updateReviewRating(Number(document.getElementById("reviewSao")?.value || 5));
}

function updateReviewRating(rating) {
    const normalizedRating = Math.min(Math.max(Number(rating || 5), 1), 5);
    const saoInput = document.getElementById("reviewSao");
    const label = document.getElementById("reviewRatingLabel");

    if (saoInput) saoInput.value = String(normalizedRating);

    document.querySelectorAll(".review-star").forEach(star => {
        const starRating = Number(star.dataset.rating || 0);
        star.classList.toggle("is-active", starRating <= normalizedRating);
        star.setAttribute("aria-checked", starRating === normalizedRating ? "true" : "false");
    });

    if (label) {
        label.textContent = getReviewRatingText(normalizedRating);
        label.dataset.rating = String(normalizedRating);
    }
}

function getReviewRatingText(rating) {
    const map = {
        1: "Rất không hài lòng",
        2: "Không hài lòng",
        3: "Bình thường",
        4: "Hài lòng",
        5: "Rất hài lòng"
    };

    return map[rating] || "Rất hài lòng";
}

async function submitReview() {
    const orderId = getOrderIdFromUrl();
    const customerId = getCurrentCustomerId();
    const maMon = Number(document.getElementById("reviewMaMon")?.value);
    const sao = Number(document.getElementById("reviewSao")?.value || 5);
    const noiDung = document.getElementById("reviewNoiDung")?.value.trim() || "";

    if (!orderId || !customerId || !maMon) {
        alert("Thiếu thông tin đánh giá. Vui lòng tải lại trang.");
        return;
    }

    if (!noiDung) {
        alert("Vui lòng nhập nội dung đánh giá.");
        return;
    }

    try {
        const response = await fetch(`${CLIENT_ORDER_API}/${orderId}/reviews`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                customerId: Number(customerId),
                maMon,
                sao,
                noiDung
            })
        });

        const data = await response.json();
        alert(data.message || "Đã gửi đánh giá.");

        if (response.ok) {
            closeReviewModal();
            loadOrderDetail(orderId);
        }
    } catch (error) {
        alert("Lỗi khi gửi đánh giá. Vui lòng thử lại.");
    }
}

function previewReviewImage(event) {
    const file = event.target.files && event.target.files[0];
    const preview = document.getElementById("reviewImagePreview");

    if (!preview) return;

    preview.innerHTML = "";

    if (!file) {
        preview.classList.add("hidden");
        return;
    }

    const image = document.createElement("img");
    image.src = URL.createObjectURL(file);
    image.alt = "Ảnh đánh giá";
    image.onload = () => URL.revokeObjectURL(image.src);

    preview.appendChild(image);
    preview.classList.remove("hidden");
}

function openClientCancelConfirmModal(orderId, afterSuccessCallback) {
    const oldModal = document.getElementById("clientCancelConfirmModal");
    if (oldModal) oldModal.remove();

    window.afterClientOrderCancelled = afterSuccessCallback || null;

    const modal = document.createElement("div");
    modal.id = "clientCancelConfirmModal";
    modal.className = "client-cancel-modal-root";

    modal.innerHTML = `
        <div class="client-cancel-modal-box">
            <div class="client-cancel-modal-header">
                <h2>Xác nhận hủy đơn #${orderId}</h2>
                <button type="button" class="client-cancel-close-btn" onclick="closeClientCancelConfirmModal()">×</button>
            </div>

            <p class="client-cancel-confirm-text">
                Vui lòng chọn lý do hủy đơn trước khi xác nhận.
            </p>

            <div class="client-cancel-reason-list">
                ${renderClientCancelReason("Tôi muốn thay đổi món", true)}
                ${renderClientCancelReason("Tôi muốn thay đổi địa chỉ giao hàng", false)}
                ${renderClientCancelReason("Thời gian giao hàng quá lâu", false)}
                ${renderClientCancelReason("Tôi đặt nhầm đơn", false)}
                ${renderClientCancelReason("Khác", false, true)}
            </div>

            <textarea
                id="clientOtherCancelReason"
                class="client-other-cancel-reason hidden"
                placeholder="Nhập lý do hủy khác..."
            ></textarea>

            <div class="client-cancel-modal-actions">
                <button type="button" class="client-cancel-danger-btn" onclick="confirmClientCancelOrder(${orderId})">
                    Xác nhận hủy
                </button>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    document.querySelectorAll('input[name="clientCancelReason"]').forEach(input => {
        input.addEventListener("change", toggleClientOtherCancelReason);
    });
}

function renderClientCancelReason(label, checked, isOther) {
    return `
        <label class="client-cancel-reason-choice">
            <input
                type="radio"
                name="clientCancelReason"
                value="${escapeHtml(label)}"
                ${checked ? "checked" : ""}
                data-other="${isOther ? "true" : "false"}"
            >
            <span>${escapeHtml(label)}</span>
        </label>
    `;
}

function toggleClientOtherCancelReason() {
    const selected = document.querySelector('input[name="clientCancelReason"]:checked');
    const textarea = document.getElementById("clientOtherCancelReason");

    if (!selected || !textarea) return;

    if (selected.dataset.other === "true") {
        textarea.classList.remove("hidden");
        textarea.focus();
    } else {
        textarea.classList.add("hidden");
        textarea.value = "";
    }
}

function closeClientCancelConfirmModal() {
    const modal = document.getElementById("clientCancelConfirmModal");
    if (modal) modal.remove();
}

async function confirmClientCancelOrder(orderId) {
    const selectedReason = document.querySelector('input[name="clientCancelReason"]:checked');
    const otherReason = document.getElementById("clientOtherCancelReason");

    if (!selectedReason) {
        alert("Vui lòng chọn lý do hủy đơn.");
        return;
    }

    let reason = selectedReason.value;

    if (selectedReason.dataset.other === "true") {
        reason = otherReason ? otherReason.value.trim() : "";
    }

    if (!reason) {
        alert("Vui lòng nhập lý do hủy đơn.");
        return;
    }

    await submitClientCancelOrder(orderId, reason);
}

async function submitClientCancelOrder(orderId, reason) {
    const customerId = getCurrentCustomerId();
    const params = new URLSearchParams();
    params.append("customerId", customerId);
    params.append("cancelReason", reason);

    try {
        const response = await fetch(`${CLIENT_ORDER_API}/${orderId}/cancel?${params.toString()}`, {
            method: "POST"
        });

        const data = await response.json();
        alert(data.message || "Đã xử lý yêu cầu hủy đơn.");

        if (response.ok) {
            closeClientCancelConfirmModal();

            if (typeof window.afterClientOrderCancelled === "function") {
                window.afterClientOrderCancelled(orderId);
            }
        }
    } catch (error) {
        alert("Lỗi khi gửi yêu cầu hủy đơn.");
    }
}

function displayPaymentStatus(status) {
    const map = {
        PENDING: "Chờ thanh toán",
        PAID: "Đã thanh toán",
        FAILED: "Thanh toán thất bại"
    };

    return map[normalizeStatus(status)] || status || "-";
}

function displayPaymentMethod(method) {
    const map = {
        COD: "Thanh toán khi nhận hàng",
        CREDIT_CARD: "Thẻ tín dụng / Ghi nợ",
        BANK: "Chuyển khoản ngân hàng",
        EWALLET: "Ví điện tử"
    };

    return map[normalizeStatus(method)] || method || "-";
}

function getOrderNote(order) {
    const note = order.ghiChu ?? order.orderNote ?? order.note ?? "";
    return typeof note === "string" ? note.trim() : "";
}

function formatMultilineText(value) {
    return escapeHtml(value).replace(/\n/g, "<br>");
}

function escapeHtml(value) {
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

function renderOrderTimeline(status) {
    const timeline = document.getElementById("orderTimeline");
    if (!timeline) return;

    const currentStatus = normalizeStatus(status);

    let steps = [
        { key: "PENDING", label: "Đã đặt hàng" },
        { key: "CONFIRMED", label: "Đã xác nhận" },
        { key: "SHIPPING", label: "Đang giao" },
        { key: "DELIVERED", label: "Đã giao" }
    ];

    if (currentStatus === "CANCEL_REQUESTED") {
        steps = [
            { key: "PENDING", label: "Đã đặt hàng" },
            { key: "CONFIRMED", label: "Đã xác nhận" },
            { key: "CANCEL_REQUESTED", label: "Yêu cầu hủy" }
        ];
    }

    if (currentStatus === "CANCELLED") {
        steps = [
            { key: "PENDING", label: "Đã đặt hàng" },
            { key: "CANCELLED", label: "Đã hủy" }
        ];
    }

    const currentIndex = steps.findIndex(step => step.key === currentStatus);

    timeline.style.setProperty("--timeline-step-count", steps.length);

    timeline.innerHTML = steps.map((step, index) => {
        let stateClass = "";

        if (index < currentIndex) {
            stateClass = "is-complete";
        } else if (index === currentIndex) {
            stateClass = "is-active";
        } else if (currentIndex >= 0 && index === currentIndex + 1) {
            stateClass = "is-next";
        }

        if (currentStatus === "CANCELLED" && step.key === "CANCELLED") {
            stateClass = "is-cancelled";
        }

        const dotIcon = index <= currentIndex ? "✓" : "";

        return `
            <div class="timeline-step ${stateClass}" data-tooltip="${step.label}" aria-label="${step.label}">
                <div class="timeline-step__dot">
                    ${dotIcon}
                </div>
            </div>
        `;
    }).join("");
}

async function reorderOrder(orderId) {
    const customerId = getCurrentCustomerId();

    try {
        const response = await fetch(`${CLIENT_ORDER_API}/${orderId}/reorder?customerId=${customerId}`, {
            method: "POST"
        });

        const data = await response.json();
        const skippedItems = Array.isArray(data.skippedItems) ? data.skippedItems : [];
        const detailMessage = skippedItems.length ? "\n\n" + skippedItems.join("\n") : "";

        if (!response.ok || !data.success) {
            alert((data.message || "Không thể đặt lại đơn hàng này.") + detailMessage);
            return;
        }

        alert((data.message || "Đã tạo lại giỏ hàng từ đơn cũ.") + detailMessage);
        window.location.href = "/checkout";

    } catch (error) {
        alert("Lỗi khi đặt lại đơn hàng.");
    }
}

function goToReorderCheckout(orderId) {
    window.location.href = `/checkout?reorderOrderId=${encodeURIComponent(orderId)}`;
}
