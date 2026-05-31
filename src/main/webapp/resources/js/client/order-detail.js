const CLIENT_ORDER_API = "/api/orders";

document.addEventListener("DOMContentLoaded", () => {
    const orderId = getOrderIdFromUrl();

    if (!orderId) {
        showMessage("Thiếu mã đơn hàng. Vui lòng mở chi tiết từ trang Đơn hàng của tôi.");
        return;
    }

    initReviewStarRating();
    loadOrderDetail(orderId);
});

function getOrderIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("orderId") || params.get("maDH") || params.get("id");
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
        const reviews = data.reviews || [];

        renderOrderInfo(order);
        renderOrderTimeline(order.trangThaiDon);
        renderOrderItems(items, order, reviews);
        renderOrderActions(order, items, reviews);

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
                    ${
                        order.canReview && order.reviewDeadlineDisplay
                        ? `<p><strong>Hạn đánh giá:</strong> ${order.reviewDeadlineDisplay}</p>`
                        : ""
                    }
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

function renderOrderItems(items, order, reviews) {
    const itemBody = document.getElementById("orderItemBody");

    if (!itemBody) return;

    const canReview = Boolean(order.canReview);
    const orderId = getOrderIdFromUrl();
    const reviewByMon = buildReviewMap(reviews);

    if (!items.length) {
        const reviewLink = canReview && orderId && hasUnreviewedItems(items, reviews)
            ? `<div style="margin-top:0.75rem;"><a class="btn btn-primary" href="${buildReviewOrderUrl(orderId)}">Đánh giá món</a></div>`
            : "";

        itemBody.innerHTML = `
            <tr>
                <td colspan="5" class="empty-cell">
                    Đơn hàng chưa có món.
                    ${reviewLink}
                </td>
            </tr>
        `;
        return;
    }

    itemBody.innerHTML = "";

    items.forEach(item => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${item.tenMon || `Món #${item.maMon}`}</td>
            <td>${item.soLuong || 0}</td>
            <td>${formatMoney(item.donGia)}</td>
            <td><strong>${formatMoney(item.thanhTien)}</strong></td>
            <td>${renderItemReviewActions(item, canReview, reviewByMon, order.trangThaiDon)}</td>
        `;

        itemBody.appendChild(row);
    });
}

function buildReviewMap(reviews) {
    const map = {};
    (reviews || []).forEach(review => {
        map[review.maMon] = review;
    });
    return map;
}

function hasUnreviewedItems(items, reviews) {
    const reviewed = new Set((reviews || []).map(review => Number(review.maMon)));
    return (items || []).some(item => !reviewed.has(Number(item.maMon)));
}

function renderItemReviewActions(item, canReview, reviewByMon, orderStatus) {
    const review = reviewByMon[item.maMon];

    if (review) {
        const viewBtn = `<a class="btn btn-ghost" href="/orders/reviews/view?reviewId=${review.maDG}">Xem</a>`;
        const editBtn = review.canEdit
            ? `<a class="btn btn-ghost" href="/orders/reviews/update?reviewId=${review.maDG}">Sửa</a>`
            : `<span class="order-note">Hết hạn sửa</span>`;

        return `<div class="order-item-review-actions">${viewBtn}${editBtn}</div>`;
    }

    if (canReview) {
        return `<button type="button" class="btn btn-ghost" onclick="openReviewModal(${item.maMon})">Đánh giá</button>`;
    }

    if (normalizeStatus(orderStatus) === "DELIVERED") {
        return `<span class="order-note">Đã hết hạn đánh giá</span>`;
    }

    return `<span class="order-note">Chưa thể đánh giá</span>`;
}

function renderOrderActions(order, items, reviews) {
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

    if (Boolean(order.canReview) && hasUnreviewedItems(items, reviews)) {
        buttons += `
            <a class="btn btn-primary" href="${buildReviewOrderUrl(order.maDH)}">
                Đánh giá món
            </a>
        `;
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

    if (response.ok) {
        loadOrderDetail(orderId);
        return;
    }

    showMessage(data.message || "Không thể hủy đơn hàng.");
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

function showMessage(message, type = "error") {
    const el = document.getElementById("message");
    if (!el) return;

    el.textContent = message || "";
    el.classList.remove("is-success", "is-error");

    if (message) {
        el.classList.add(type === "success" ? "is-success" : "is-error");
    }
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

    initReviewStarRating();
    modal.classList.remove("hidden");
}

function closeReviewModal() {
    const modal = document.getElementById("reviewModal");
    if (modal) modal.classList.add("hidden");
}

function initReviewStarRating() {
    const stars = document.querySelectorAll("#reviewStarRating .review-star");
    const saoInput = document.getElementById("reviewSao");

    if (!stars.length || !saoInput) return;

    const setRating = (rating) => {
        saoInput.value = String(rating);
        stars.forEach(star => {
            const starValue = Number(star.dataset.rating || 0);
            star.classList.toggle("is-active", starValue <= rating);
        });
    };

    stars.forEach(star => {
        star.onclick = () => setRating(Number(star.dataset.rating || 0));
    });

    setRating(Number(saoInput.value || 5));
}

async function submitReview() {
    const orderId = getOrderIdFromUrl();
    const maMon = Number(document.getElementById("reviewMaMon")?.value || 0);
    const sao = Number(document.getElementById("reviewSao")?.value || 0);
    const noiDung = (document.getElementById("reviewNoiDung")?.value || "").trim();

    if (!orderId || !maMon) {
        showMessage("Không thể gửi đánh giá. Thiếu thông tin đơn hoặc món.");
        return;
    }

    if (!sao || sao < 1 || sao > 5) {
        showMessage("Vui lòng chọn số sao.");
        return;
    }

    if (!noiDung) {
        showMessage("Vui lòng nhập nội dung đánh giá.");
        return;
    }

    try {
        const response = await fetch(`/api/reviews?orderId=${orderId}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ maMon, sao, noiDung })
        });

        const data = await response.json();

        if (!response.ok || !data.success) {
            showMessage(data.message || "Không thể gửi đánh giá.");
            return;
        }

        closeReviewModal();
        showMessage(data.message || "Đánh giá thành công.", "success");
        loadOrderDetail(orderId);
    } catch (error) {
        showMessage("Lỗi khi gửi đánh giá.");
    }
}

function previewReviewImage(event) {
    const preview = document.getElementById("reviewImagePreview");
    const file = event.target?.files?.[0];

    if (!preview) return;

    if (!file) {
        preview.innerHTML = "";
        preview.classList.add("hidden");
        return;
    }

    preview.innerHTML = `<img src="${URL.createObjectURL(file)}" alt="Ảnh đánh giá" style="max-width:100%;border-radius:8px;">`;
    preview.classList.remove("hidden");
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
