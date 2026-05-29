const CLIENT_ORDER_API = "/api/orders";
const DEFAULT_FOOD_IMAGE = "/images/jollibug.png";

let currentOrderItems = [];
let currentOrderStatus = "";
let currentReviews = [];
let currentReviewTab = "pending";

const RATING_TEXT = {
    1: "Rất tệ",
    2: "Không hài lòng",
    3: "Bình thường",
    4: "Hài lòng",
    5: "Rất hài lòng"
};

document.addEventListener("DOMContentLoaded", () => {
    initializeReviewStars();

    const maDH = getMaDHFromUrl();
    if (!maDH) {
        showMessage("Thiếu mã đơn hàng.");
        return;
    }

    loadOrderDetail(maDH);
});

function getMaDHFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("maDH");
}

async function loadOrderDetail(maDH) {
    const maKH = getCurrentMaKH();
    const detailContent = document.getElementById("orderDetailContent");
    const itemBody = document.getElementById("orderItemBody");

    if (!maKH) {
        showMessage("Bạn cần đăng nhập để xem chi tiết đơn hàng.");
        return;
    }

    if (detailContent) detailContent.innerHTML = "Đang tải...";
    if (itemBody) itemBody.innerHTML = "";

    try {
        const [detailResponse, reviewResponse] = await Promise.all([
            fetch(`${CLIENT_ORDER_API}/${maDH}?maKH=${maKH}`),
            fetch(`${CLIENT_ORDER_API}/${maDH}/reviews?maKH=${maKH}`)
        ]);

        const data = await detailResponse.json();
        if (!detailResponse.ok) {
            throw new Error(data.message || "Không thể tải chi tiết đơn hàng.");
        }

        const reviewData = reviewResponse.ok ? await reviewResponse.json() : [];
        const order = data.order || data.donHang;

        currentOrderItems = data.orderItems || data.chiTietDH || [];
        currentOrderStatus = order.trangThaiDon || "";
        currentReviews = Array.isArray(reviewData) ? reviewData : [];

        renderOrderInfo(order);
        renderOrderTimeline(order.trangThaiDon);
        renderOrderItems();
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

    const receiverName = order.tenNguoiNhan || order.tenKhachHang || "-";
    const receiverPhone = order.sdtNguoiNhan || order.sdtKhachHang || "-";
    const receiverEmail = order.emailKhachHang || "-";
    const deliveryAddress = order.diaChiGiaoHang || "-";
    const note = hasText(order.ghiChu) ? escapeHtml(order.ghiChu.trim()) : "-";

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
                    <p><strong>Người nhận:</strong> ${escapeHtml(receiverName)}</p>
                    <p><strong>Số điện thoại:</strong> ${escapeHtml(receiverPhone)}</p>
                    <p><strong>Email:</strong> ${escapeHtml(receiverEmail)}</p>
                    <p class="full"><strong>Địa chỉ giao hàng:</strong><br>${escapeHtml(deliveryAddress)}</p>
                    <p class="full"><strong>Ghi chú / xử lý đơn:</strong><br>${note}</p>
                </div>
            </section>
        </div>
    `;
}

function switchReviewTab(tab) {
    currentReviewTab = tab;
    document.querySelectorAll("[data-review-tab]").forEach((button) => {
        button.classList.toggle("is-active", button.dataset.reviewTab === tab);
    });
    renderOrderItems();
}

function renderOrderItems() {
    const itemHead = document.getElementById("orderItemHead");
    const itemBody = document.getElementById("orderItemBody");
    const title = document.getElementById("orderItemsTitle");

    if (!itemHead || !itemBody) return;

    if (title) {
        title.textContent = currentReviewTab === "done" ? "Đã đánh giá" : "Danh sách món ăn";
    }

    if (currentReviewTab === "done") {
        renderReviewedItems(itemHead, itemBody);
        return;
    }

    renderPendingReviewItems(itemHead, itemBody);
}

function renderPendingReviewItems(itemHead, itemBody) {
    itemHead.innerHTML = `
        <tr>
            <th>Món ăn</th>
            <th>Số lượng</th>
            <th>Đơn giá</th>
            <th>Thành tiền</th>
            <th>Đánh giá</th>
        </tr>
    `;

    const reviewedIds = new Set(currentReviews.map((review) => Number(review.maMon)));
    const items = currentOrderItems.filter((item) => !reviewedIds.has(Number(item.maMon)));

    if (!items.length) {
        itemBody.innerHTML = `
            <tr>
                <td colspan="5" class="empty-cell">Không còn món nào cần đánh giá.</td>
            </tr>
        `;
        return;
    }

    itemBody.innerHTML = "";
    const canReview = normalizeStatus(currentOrderStatus) === "DELIVERED";

    items.forEach((item) => {
        const row = document.createElement("tr");
        const itemName = item.tenMon || `Món #${item.maMon}`;
        const imageSrc = buildFoodImageUrl(item);

        row.innerHTML = `
            <td>
                <div class="order-detail-product">
                    <img src="${imageSrc}" alt="${escapeHtml(itemName)}" loading="lazy" onerror="this.onerror=null;this.src='${DEFAULT_FOOD_IMAGE}';">
                    <strong>${escapeHtml(itemName)}</strong>
                </div>
            </td>
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

function renderReviewedItems(itemHead, itemBody) {
    itemHead.innerHTML = `
        <tr>
            <th>Món ăn</th>
            <th>Số sao</th>
            <th>Nội dung</th>
            <th>Ảnh</th>
            <th>Ngày đánh giá</th>
        </tr>
    `;

    if (!currentReviews.length) {
        itemBody.innerHTML = `
            <tr>
                <td colspan="5" class="empty-cell">Bạn chưa gửi đánh giá nào cho đơn này.</td>
            </tr>
        `;
        return;
    }

    itemBody.innerHTML = "";
    currentReviews.forEach((review) => {
        const row = document.createElement("tr");
        const itemName = review.tenMon || findItemName(review.maMon);
        const productImage = buildFoodImageUrl(review);
        const reviewImage = buildReviewImageUrl(review.anhDG);

        row.innerHTML = `
            <td>
                <div class="order-detail-product">
                    <img src="${productImage}" alt="${escapeHtml(itemName)}" loading="lazy" onerror="this.onerror=null;this.src='${DEFAULT_FOOD_IMAGE}';">
                    <strong>${escapeHtml(itemName)}</strong>
                </div>
            </td>
            <td><span class="review-stars-text">${"★".repeat(review.sao || 0)}</span><br><span class="order-note">${getRatingText(review.sao)}</span></td>
            <td>${escapeHtml(review.noiDung || "-")}</td>
            <td>
                ${
                    reviewImage
                        ? `<img class="review-table-image" src="${reviewImage}" alt="Ảnh đánh giá" loading="lazy">`
                        : `<span class="order-note">-</span>`
                }
            </td>
            <td>${formatDate(review.ngayDG)}</td>
        `;
        itemBody.appendChild(row);
    });
}

function findItemName(maMon) {
    const item = currentOrderItems.find((orderItem) => Number(orderItem.maMon) === Number(maMon));
    return item?.tenMon || `Món #${maMon}`;
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

async function requestCancelOrder(maDH) {
    const maKH = getCurrentMaKH();

    if (!confirm("Bạn có chắc muốn hủy đơn hàng này không?")) {
        return;
    }

    const response = await fetch(`${CLIENT_ORDER_API}/${maDH}/cancel?maKH=${maKH}`, {
        method: "POST"
    });

    const data = await response.json();
    alert(data.message || "Đã xử lý yêu cầu hủy đơn.");

    if (response.ok) {
        loadOrderDetail(maDH);
    }
}

async function confirmReceived(maDH) {
    const maKH = getCurrentMaKH();

    if (!confirm("Bạn xác nhận đã nhận được đơn hàng này?")) {
        return;
    }

    const response = await fetch(`${CLIENT_ORDER_API}/${maDH}/received?maKH=${maKH}`, {
        method: "POST"
    });

    const data = await response.json();
    alert(data.message || "Đã xác nhận nhận hàng.");

    if (response.ok) {
        loadOrderDetail(maDH);
    }
}

function getCurrentMaKH() {
    const input = document.getElementById("currentMaKH");
    return input ? input.value : "";
}

async function submitReview() {
    const maDH = getMaDHFromUrl();
    const maKH = Number(getCurrentMaKH());
    const maMon = Number(document.getElementById("reviewMaMon")?.value || 0);
    const sao = Number(document.getElementById("reviewSao")?.value || 0);
    const noiDung = document.getElementById("reviewNoiDung")?.value?.trim() || "";
    const imageFile = document.getElementById("reviewImageInput")?.files?.[0] || null;

    if (!maDH || !maKH || !maMon) {
        alert("Thiếu thông tin đánh giá.");
        return;
    }

    if (!sao || sao < 1 || sao > 5) {
        alert("Vui lòng chọn số sao hợp lệ.");
        return;
    }

    if (!noiDung) {
        alert("Vui lòng nhập nội dung đánh giá.");
        return;
    }

    const formData = new FormData();
    formData.append("maKH", String(maKH));
    formData.append("maMon", String(maMon));
    formData.append("sao", String(sao));
    formData.append("noiDung", noiDung);
    if (imageFile) {
        formData.append("image", imageFile);
    }

    try {
        const response = await fetch(`${CLIENT_ORDER_API}/${maDH}/reviews`, {
            method: "POST",
            body: formData
        });

        const data = await response.json();
        alert(data.message || (response.ok ? "Đánh giá thành công." : "Không thể gửi đánh giá."));

        if (response.ok) {
            closeReviewModal();
            await loadReviews(maDH);
            renderOrderItems();
        }
    } catch (error) {
        alert("Lỗi khi gửi đánh giá.");
    }
}

async function loadReviews(maDH) {
    const maKH = getCurrentMaKH();
    const response = await fetch(`${CLIENT_ORDER_API}/${maDH}/reviews?maKH=${maKH}`);
    if (!response.ok) {
        currentReviews = [];
        return;
    }

    const data = await response.json();
    currentReviews = Array.isArray(data) ? data : [];
}

function previewReviewImage(event) {
    const file = event?.target?.files?.[0];
    const preview = document.getElementById("reviewImagePreview");

    if (!preview) return;

    if (!file) {
        preview.classList.add("hidden");
        preview.innerHTML = "";
        return;
    }

    const imageUrl = URL.createObjectURL(file);
    preview.innerHTML = `<img src="${imageUrl}" alt="Xem trước ảnh đánh giá">`;
    preview.classList.remove("hidden");
}

function initializeReviewStars() {
    const stars = document.querySelectorAll(".review-star");
    const saoInput = document.getElementById("reviewSao");

    if (!stars.length || !saoInput) return;

    stars.forEach((star) => {
        star.addEventListener("click", () => {
            const rating = Number(star.dataset.rating || 0);
            saoInput.value = String(rating);
            updateStars(rating);
        });
    });

    updateStars(Number(saoInput.value || 0));
}

function updateStars(rating) {
    document.querySelectorAll(".review-star").forEach((star) => {
        const value = Number(star.dataset.rating || 0);
        star.classList.toggle("is-active", value <= rating);
    });

    const ratingText = document.getElementById("reviewRatingText");
    if (ratingText) {
        ratingText.textContent = getRatingText(rating);
    }
}

function getRatingText(rating) {
    return RATING_TEXT[Number(rating)] || "Chọn mức đánh giá";
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
    if (saoInput) saoInput.value = "";
    if (imageInput) imageInput.value = "";
    if (imagePreview) {
        imagePreview.innerHTML = "";
        imagePreview.classList.add("hidden");
    }

    updateStars(0);
    modal.classList.remove("hidden");
}

function closeReviewModal() {
    const modal = document.getElementById("reviewModal");
    if (modal) modal.classList.add("hidden");
}

function displayPaymentStatus(status) {
    const map = {
        PENDING: "Chờ thanh toán",
        PAID: "Đã thanh toán",
        FAILED: "Thanh toán thất bại",
        CANCELLED: "Đã hủy"
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

function renderOrderTimeline(status) {
    const timeline = document.getElementById("orderTimeline");
    const cancelInfo = document.getElementById("orderTimelineCancelInfo");
    if (!timeline) return;

    const currentStatus = normalizeStatus(status);
    const steps = [
        { key: "PENDING", label: "Đã đặt hàng" },
        { key: "CONFIRMED", label: "Đã xác nhận" },
        { key: "SHIPPING", label: "Đang giao" },
        { key: "DELIVERED", label: "Đã giao" }
    ];
    const currentIndex = steps.findIndex((step) => step.key === currentStatus);
    const isCancelled = currentStatus === "CANCELLED";
    const isCancelRequested = currentStatus === "CANCEL_REQUESTED";

    timeline.classList.toggle("is-cancelled", isCancelled);
    timeline.innerHTML = steps.map((step, index) => {
        let stateClass = "";

        if (!isCancelled && currentIndex >= 0 && index <= currentIndex) {
            stateClass = "is-complete";
        }

        return `
            <div class="timeline-step ${stateClass}" data-tooltip="${step.label}" aria-label="${step.label}">
                <div class="timeline-step__dot">
                    ${index <= currentIndex && !isCancelled ? "✓" : ""}
                </div>
            </div>
        `;
    }).join("");

    if (!cancelInfo) return;

    if (isCancelled) {
        cancelInfo.innerHTML = `<strong>Đơn hàng đã hủy</strong><span>Trạng thái hủy được hiển thị riêng và không làm thay đổi các bước giao hàng.</span>`;
        cancelInfo.classList.remove("hidden");
        return;
    }

    if (isCancelRequested) {
        cancelInfo.innerHTML = `<strong>Đang yêu cầu hủy</strong><span>Nhân viên sẽ xử lý yêu cầu hủy đơn của bạn.</span>`;
        cancelInfo.classList.remove("hidden");
        return;
    }

    cancelInfo.innerHTML = "";
    cancelInfo.classList.add("hidden");
}

function buildFoodImageUrl(item) {
    const rawImage = item?.img || item?.imageUrl || item?.hinhAnh || "";
    const image = String(rawImage).trim();

    if (!image) {
        return DEFAULT_FOOD_IMAGE;
    }

    if (/^(https?:)?\/\//i.test(image) || image.startsWith("/")) {
        return image;
    }

    return `/images/${encodeURI(image)}`;
}

function buildReviewImageUrl(imagePath) {
    const image = String(imagePath || "").trim();
    if (!image) {
        return "";
    }

    if (/^(https?:)?\/\//i.test(image) || image.startsWith("/")) {
        return image;
    }

    return `/images/${encodeURI(image)}`;
}

function hasText(value) {
    return value !== null && value !== undefined && String(value).trim() !== "";
}

function escapeHtml(value) {
    if (!hasText(value)) return "-";

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
