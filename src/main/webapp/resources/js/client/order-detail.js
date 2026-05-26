const CLIENT_ORDER_API = "/api/orders";

document.addEventListener("DOMContentLoaded", () => {
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
        const response = await fetch(`${CLIENT_ORDER_API}/${maDH}?maKH=${maKH}`);
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

    items.forEach((item) => {
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

    try {
        const response = await fetch(`${CLIENT_ORDER_API}/${maDH}/reviews`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                maKH,
                maMon,
                sao,
                noiDung
            })
        });

        const data = await response.json();
        alert(data.message || (response.ok ? "Đánh giá thành công." : "Không thể gửi đánh giá."));

        if (response.ok) {
            closeReviewModal();
        }
    } catch (error) {
        alert("Lỗi khi gửi đánh giá.");
    }
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
    preview.innerHTML = `<img src="${imageUrl}" alt="Review preview">`;
    preview.classList.remove("hidden");
}

document.addEventListener("DOMContentLoaded", () => {
    const stars = document.querySelectorAll(".review-star");
    const saoInput = document.getElementById("reviewSao");

    if (!stars.length || !saoInput) return;

    const updateStars = (rating) => {
        stars.forEach((star) => {
            const value = Number(star.dataset.rating || 0);
            star.classList.toggle("is-active", value <= rating);
        });
    };

    updateStars(Number(saoInput.value || 5));

    stars.forEach((star) => {
        star.addEventListener("click", () => {
            const rating = Number(star.dataset.rating || 0);
            saoInput.value = String(rating);
            updateStars(rating);
        });
    });
});

function showMessage(message) {
    const el = document.getElementById("message");
    if (el) el.textContent = message;
}

function normalizeStatus(status) {
    return (status || "").trim().toUpperCase();
}

function displayStatus(status) {
    const map = {
        PENDING: "Chờ xác nhận",
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

    if (!modal || !maMonInput) return;

    maMonInput.value = maMon;

    if (noiDungInput) noiDungInput.value = "";
    if (saoInput) saoInput.value = "5";

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

function renderOrderTimeline(status) {
    const timeline = document.getElementById("orderTimeline");
    if (!timeline) return;

    const currentStatus = normalizeStatus(status);
    let steps = [
        { key: "PENDING", label: "Chờ xác nhận" },
        { key: "CONFIRMED", label: "Đã xác nhận" },
        { key: "SHIPPING", label: "Đang giao" },
        { key: "DELIVERED", label: "Đã giao" }
    ];

    if (currentStatus === "CANCEL_REQUESTED") {
        steps = [
            { key: "PENDING", label: "Chờ xác nhận" },
            { key: "CONFIRMED", label: "Đã xác nhận" },
            { key: "CANCEL_REQUESTED", label: "Yêu cầu hủy" }
        ];
    }

    if (currentStatus === "CANCELLED") {
        steps = [
            { key: "PENDING", label: "Chờ xác nhận" },
            { key: "CANCELLED", label: "Đã hủy" }
        ];
    }

    const currentIndex = steps.findIndex((step) => step.key === currentStatus);

    timeline.innerHTML = steps.map((step, index) => {
        let stateClass = "";

        if (index < currentIndex) {
            stateClass = "is-done";
        } else if (index === currentIndex) {
            stateClass = "is-current";
        }

        if (currentStatus === "CANCELLED" && step.key === "CANCELLED") {
            stateClass = "is-cancelled";
        }

        return `
            <div class="timeline-step ${stateClass}">
                <div class="timeline-step__dot">
                    ${index < currentIndex ? "✓" : ""}
                </div>
                <div class="timeline-step__content">
                    <strong>${step.label}</strong>
                </div>
            </div>
        `;
    }).join("");
}
