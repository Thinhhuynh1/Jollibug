const CLIENT_ORDER_API = "/api/orders";

document.addEventListener("DOMContentLoaded", () => {
    const orderId = getOrderIdFromUrl();
    setupReviewStars();

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
        const statusHistory = await fetchOrderStatusHistory(orderId, customerId);

        renderOrderTimeline(order.trangThaiDon, statusHistory, order);
        renderOrderInfo(order);
        renderOrderItems(items, order.trangThaiDon);
        renderOrderActions(order);

    } catch (error) {
        showMessage(error.message);
        if (detailContent) detailContent.innerHTML = "";
    }
}

async function fetchOrderStatusHistory(orderId, customerId) {
    try {
        const response = await fetch(`${CLIENT_ORDER_API}/${orderId}/status-history?customerId=${customerId}`);

        if (!response.ok) {
            return [];
        }

        const data = await response.json();
        return Array.isArray(data) ? data : [];

    } catch (error) {
        return [];
    }
}

function renderOrderInfo(order) {
    const detailContent = document.getElementById("orderDetailContent");
    const title = document.getElementById("orderDetailTitle");

    if (title) {
        title.textContent = `Chi tiết đơn hàng #${order.maDH}`;
    }

    if (!detailContent) return;

    const delivery = parseDeliveryInfo(order.ghiChu);

    const receiverName = order.tenNguoiNhan || delivery.name || order.tenKhachHang || "-";
    const receiverPhone = order.sdtNguoiNhan || delivery.phone || order.sdtKhachHang || "-";
    const receiverEmail = order.emailKhachHang || delivery.email || "-";
    const deliveryAddress = order.diaChiGiaoHang || delivery.address || "-";

    detailContent.innerHTML = `
        <div class="client-detail-two-columns">
            <section class="client-detail-info-card">
                <h2>Thông tin đơn hàng</h2>

                <div class="client-detail-info-list">
                    <p><strong>Ngày đặt:</strong> ${formatDate(order.ngayDat)}</p>
                    <p><strong>Trạng thái đơn:</strong> 
                        <span class="status ${getStatusClass(order.trangThaiDon)}">${displayStatus(order.trangThaiDon)}</span>
                    </p>
                    <p><strong>Phương thức thanh toán:</strong> ${order.tenPT || displayPaymentMethod(order.maPT)}</p>
                    <p><strong>Trạng thái thanh toán:</strong> ${displayPaymentStatus(order.trangThaiTT)}</p>
                    <p><strong>Tổng tiền món:</strong> ${formatMoney(order.tongTienMon)}</p>
                    <p><strong>Giảm giá:</strong> ${formatMoney(order.tienGiamGia)}</p>
                    <p><strong>Thành tiền:</strong> ${formatMoney(order.thanhTien)}</p>
                    <p><strong>Mã giảm giá:</strong> ${order.maGG || "-"}</p>
                </div>
            </section>

            <section class="client-detail-info-card">
                <h2>Thông tin giao hàng</h2>

                <div class="client-detail-info-list">
                    <p><strong>Người nhận:</strong> ${receiverName}</p>
                    <p><strong>Số điện thoại:</strong> ${receiverPhone}</p>
                    <p><strong>Email:</strong> ${receiverEmail}</p>
                    <p class="full"><strong>Địa chỉ giao hàng:</strong><br>${deliveryAddress}</p>
                    <p class="full"><strong>Ghi chú / xử lý đơn:</strong><br>${order.ghiChu || "-"}</p>
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
                    canReview
                    ? `<button type="button" class="btn btn-ghost" onclick="openReviewModal(${item.maMon})">Đánh giá</button>`
                    : `<span class="order-note">Chưa thể đánh giá</span>`
                }
            </td>
        `;

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

    actionBox.innerHTML = buttons;
}

async function requestCancelOrder(orderId) {
    const customerId = getCurrentCustomerId();

    if (!confirm("Bạn có chắc muốn hủy đơn hàng này không?")) {
        return;
    }

    const response = await fetch(`${CLIENT_ORDER_API}/${orderId}/cancel?customerId=${customerId}`, {
        method: "POST"
    });

    const data = await response.json();
    alert(data.message || "Đã xử lý yêu cầu hủy đơn.");

    if (response.ok) {
        loadOrderDetail(orderId);
    }
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
        CANCEL_REQUESTED: "Yêu cầu hủy",
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

    setReviewRating(5);

    modal.classList.remove("hidden");
}

function closeReviewModal() {
    const modal = document.getElementById("reviewModal");
    if (modal) modal.classList.add("hidden");
}

function setupReviewStars() {
    const stars = document.querySelectorAll(".review-star");

    stars.forEach(star => {
        const rating = Number(star.dataset.rating || 0);

        star.addEventListener("click", () => setReviewRating(rating));
        star.addEventListener("mouseenter", () => highlightReviewStars(rating));
        star.addEventListener("focus", () => highlightReviewStars(rating));
    });

    const ratingBox = document.getElementById("reviewStarRating");
    if (ratingBox) {
        ratingBox.addEventListener("mouseleave", () => {
            const input = document.getElementById("reviewSao");
            highlightReviewStars(Number(input ? input.value : 5));
        });
    }

    setReviewRating(5);
}

function setReviewRating(rating) {
    const normalizedRating = Math.min(Math.max(Number(rating) || 5, 1), 5);
    const input = document.getElementById("reviewSao");

    if (input) {
        input.value = String(normalizedRating);
    }

    highlightReviewStars(normalizedRating);
}

function highlightReviewStars(rating) {
    document.querySelectorAll(".review-star").forEach(star => {
        const starRating = Number(star.dataset.rating || 0);
        star.classList.toggle("is-active", starRating <= rating);
    });
}

function previewReviewImage(event) {
    const input = event && event.target ? event.target : document.getElementById("reviewImageInput");
    const preview = document.getElementById("reviewImagePreview");

    if (!input || !preview) return;

    const file = input.files && input.files[0];

    if (!file) {
        preview.innerHTML = "";
        preview.classList.add("hidden");
        return;
    }

    const imageUrl = URL.createObjectURL(file);
    preview.innerHTML = `<img src="${imageUrl}" alt="Preview ảnh đánh giá">`;
    preview.classList.remove("hidden");
}

async function submitReview() {
    const orderId = getOrderIdFromUrl();
    const customerId = getCurrentCustomerId();
    const maMonInput = document.getElementById("reviewMaMon");
    const saoInput = document.getElementById("reviewSao");
    const noiDungInput = document.getElementById("reviewNoiDung");

    const maMon = maMonInput ? maMonInput.value : "";
    const sao = saoInput ? saoInput.value : "5";
    const noiDung = noiDungInput ? noiDungInput.value : "";

    if (!noiDung.trim()) {
        alert("Vui long nhap noi dung danh gia.");
        return;
    }

    try {
        // TODO: send reviewImageInput.files[0] with FormData when the backend supports persisted review images.
        const response = await fetch(`${CLIENT_ORDER_API}/${orderId}/reviews`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                customerId: Number(customerId),
                maMon: Number(maMon),
                sao: Number(sao),
                noiDung: noiDung.trim()
            })
        });

        const data = await response.json();
        alert(data.message || "Da gui danh gia.");

        if (response.ok) {
            closeReviewModal();
        }

    } catch (error) {
        alert("Loi khi gui danh gia.");
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
    const addressMatch =
        note.match(/Địa chỉ nhập:\s*([^;\n]+)/i) ||
        note.match(/Địa chỉ giao hàng:\s*([^;\n]+)/i) ||
        note.match(/Địa chỉ:\s*([^;\n]+)/i);

    result.name = nameMatch ? nameMatch[1].trim() : "";
    result.phone = phoneMatch ? phoneMatch[1].trim() : "";
    result.email = emailMatch ? emailMatch[1].trim() : "";
    result.address = addressMatch ? addressMatch[1].trim() : "";

    return result;
}

const ORDER_TIMELINE_BASE = [
    { key: "PENDING", label: "Đã đặt hàng" },
    { key: "CONFIRMED", label: "Đã xác nhận" },
    { key: "SHIPPING", label: "Đang giao" },
    { key: "DELIVERED", label: "Đã giao" }
];

function renderOrderTimeline(status, history = [], order = null) {
    const timeline = document.getElementById("orderTimeline");
    if (!timeline) return;

    const currentStatus = normalizeStatus(status);
    const timelineState = buildTimelineState(currentStatus, history);
    const steps = timelineState.steps;
    const currentIndex = timelineState.currentIndex;

    timeline.style.gridTemplateColumns = `repeat(${Math.max(steps.length, 1)}, minmax(0, 1fr))`;

    timeline.innerHTML = steps.map((step, index) => {
        const classes = ["timeline-step"];
        const stateClass = getTimelineStepState(step, index, currentIndex, currentStatus);

        if (stateClass) classes.push(stateClass);
        if (index === currentIndex) classes.push("is-current");

        const label = step.label || displayStatus(step.key);

        return `
            <div
                class="${classes.join(" ")}"
                data-tooltip="${escapeHtml(label)}"
                title="${escapeHtml(label)}"
                aria-label="${escapeHtml(label)}"
                tabindex="0"
            >
                <span class="timeline-step__dot"></span>
            </div>
        `;
    }).join("");

    renderCancellationInfo(currentStatus, history, order);
}

function getTimelineStepState(step, index, currentIndex, currentStatus) {
    if (step.key === "CANCELLED") return "is-cancelled";

    if (index < currentIndex) return "is-complete";

    if (index === currentIndex) {
        if (currentStatus === "SHIPPING" || currentStatus === "CANCEL_REQUESTED") {
            return "is-active";
        }

        return "is-complete";
    }

    return "";
}

function buildTimelineState(currentStatus, history) {
    const normalizedHistory = Array.isArray(history)
        ? history.filter(item => normalizeStatus(item.newStatus))
        : [];

    if (!normalizedHistory.length) {
        return buildFallbackTimelineState(currentStatus);
    }

    const steps = [];

    normalizedHistory.forEach(item => {
        const oldStatus = normalizeStatus(item.oldStatus);
        const newStatus = normalizeStatus(item.newStatus);

        if (oldStatus && (!steps.length || steps[steps.length - 1].key !== oldStatus)) {
            pushTimelineStep(steps, oldStatus, null);
        }

        pushTimelineStep(steps, newStatus, item);
    });

    if (!steps.length) {
        return buildFallbackTimelineState(currentStatus);
    }

    if (findLastStepIndex(steps, currentStatus) === -1) {
        pushTimelineStep(steps, currentStatus, null);
    }

    appendFutureTimelineSteps(steps, currentStatus);

    return {
        steps,
        currentIndex: Math.max(findLastStepIndex(steps, currentStatus), 0)
    };
}

function buildFallbackTimelineState(currentStatus) {
    let steps;

    if (currentStatus === "CANCEL_REQUESTED") {
        steps = [
            { key: "PENDING", label: displayStatus("PENDING") },
            { key: "CONFIRMED", label: displayStatus("CONFIRMED") },
            { key: "CANCEL_REQUESTED", label: displayStatus("CANCEL_REQUESTED") }
        ];
    } else if (currentStatus === "CANCELLED") {
        steps = [
            { key: "PENDING", label: displayStatus("PENDING") },
            { key: "CANCELLED", label: displayStatus("CANCELLED") }
        ];
    } else {
        steps = ORDER_TIMELINE_BASE.map(step => ({
            key: step.key,
            label: displayStatus(step.key)
        }));
    }

    let currentIndex = steps.findIndex(step => step.key === currentStatus);
    if (currentIndex < 0) currentIndex = 0;

    return { steps, currentIndex };
}

function pushTimelineStep(steps, status, historyItem) {
    if (!status) return;

    const label = displayStatus(status);

    if (steps.length && steps[steps.length - 1].key === status) {
        steps[steps.length - 1] = { key: status, label, historyItem };
        return;
    }

    steps.push({ key: status, label, historyItem });
}

function appendFutureTimelineSteps(steps, currentStatus) {
    if (currentStatus === "CANCELLED" || currentStatus === "CANCEL_REQUESTED") {
        return;
    }

    const baseIndex = ORDER_TIMELINE_BASE.findIndex(step => step.key === currentStatus);
    if (baseIndex < 0) return;

    for (let i = baseIndex + 1; i < ORDER_TIMELINE_BASE.length; i++) {
        pushTimelineStep(steps, ORDER_TIMELINE_BASE[i].key, null);
    }
}

function findLastStepIndex(steps, status) {
    for (let i = steps.length - 1; i >= 0; i--) {
        if (steps[i].key === status) {
            return i;
        }
    }

    return -1;
}

function renderCancellationInfo(currentStatus, history, order) {
    const info = document.getElementById("orderTimelineCancelInfo");
    if (!info) return;

    const cancellation = Array.isArray(history)
        ? [...history].reverse().find(item => normalizeStatus(item.newStatus) === "CANCELLED")
        : null;

    if (!cancellation && currentStatus !== "CANCELLED") {
        info.classList.add("hidden");
        info.innerHTML = "";
        return;
    }

    if (!cancellation) {
        const fallbackActor = order && order.maTKNV ? "nhân viên" : "khách hàng";
        const fallbackReason = order && order.ghiChu ? parseCancelReasonFromNote(order.ghiChu) : "";

        info.innerHTML = `
            <strong>Đơn hàng được hủy bởi ${fallbackActor}</strong>
            ${fallbackReason ? `<span>Lý do: ${escapeHtml(fallbackReason)}</span>` : ""}
        `;
        info.classList.remove("hidden");
        return;
    }

    const actor = displayActorType(cancellation.actorType);
    const reason = cancellation.reason && cancellation.reason.trim()
        ? cancellation.reason.trim()
        : "";

    info.innerHTML = `
        <strong>Đơn hàng được hủy bởi ${escapeHtml(actor)}</strong>
        ${reason ? `<span>Lý do: ${escapeHtml(reason)}</span>` : ""}
        ${cancellation.createdAt ? `<span>Thời gian hủy: ${escapeHtml(formatDate(cancellation.createdAt))}</span>` : ""}
    `;
    info.classList.remove("hidden");
}

function displayActorType(actorType) {
    const actor = normalizeStatus(actorType);

    if (actor === "CUSTOMER") return "khách hàng";
    if (actor === "STAFF") return "nhân viên";
    if (actor === "MANAGER") return "quản lý";
    if (actor === "ADMIN") return "quản trị viên";
    return "hệ thống";
}

function parseCancelReasonFromNote(note) {
    if (!note) return "";

    const match = note.match(/\[(?:Hủy đơn|Hủy đơn hàng)\]\s*Lý do:\s*([^\n]+)/i);
    return match ? match[1].trim() : "";
}

function escapeHtml(value) {
    return String(value || "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
