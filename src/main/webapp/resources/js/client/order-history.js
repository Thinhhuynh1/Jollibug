const CLIENT_ORDER_API = "/api/orders";
const DEFAULT_FOOD_IMAGE = "/images/jollibug.png";

let allOrders = [];
let currentTab = "active";
const orderDetailCache = new Map();

document.addEventListener("DOMContentLoaded", () => {
    loadOrders();
});

async function loadOrders() {
    const maKH = getCurrentMaKH();
    const list = document.getElementById("orderCardList");
    const message = document.getElementById("message");

    if (!list) return;
    if (message) message.textContent = "";

    if (!maKH) {
        list.innerHTML = `<p class="empty-cell">Bạn cần đăng nhập để xem lịch sử đơn hàng.</p>`;
        return;
    }

    list.innerHTML = `<p class="empty-cell">Đang tải đơn hàng...</p>`;

    try {
        const response = await fetch(`${CLIENT_ORDER_API}?maKH=${maKH}`);
        const orders = await response.json();

        if (!response.ok) {
            throw new Error("Không thể tải lịch sử đơn hàng.");
        }

        allOrders = Array.isArray(orders) ? orders : [];
        await preloadOrderSummaries(allOrders);
        renderOrdersByTab();
    } catch (error) {
        list.innerHTML = `<p class="empty-cell">${escapeHtml(error.message)}</p>`;
    }
}

async function preloadOrderSummaries(orders) {
    const maKH = getCurrentMaKH();

    const tasks = orders.map(async (order) => {
        if (!order || !order.maDH || orderDetailCache.has(order.maDH)) return;

        try {
            const response = await fetch(`${CLIENT_ORDER_API}/${order.maDH}?maKH=${maKH}`);
            if (!response.ok) return;

            const data = await response.json();
            const items = data.orderItems || data.chiTietDH || [];
            orderDetailCache.set(order.maDH, items);
        } catch (error) {
            orderDetailCache.set(order.maDH, []);
        }
    });

    await Promise.all(tasks);
}

function switchOrderTab(tab) {
    currentTab = tab;

    document.querySelectorAll(".order-tab").forEach((button) => {
        button.classList.toggle("is-active", button.dataset.tab === tab);
    });

    renderOrdersByTab();
}

function renderOrdersByTab() {
    const list = document.getElementById("orderCardList");
    const title = document.getElementById("orderSectionTitle");

    if (!list) return;

    let filteredOrders = allOrders.filter((order) => {
        const status = normalizeStatus(order.trangThaiDon);

        if (currentTab === "active") {
            return ["PENDING", "CONFIRMED", "SHIPPING", "CANCEL_REQUESTED"].includes(status);
        }

        if (currentTab === "history") {
            return ["DELIVERED", "CANCELLED"].includes(status);
        }

        if (currentTab === "review") {
            return Boolean(order.canReview);
        }

        return true;
    });

    filteredOrders = sortOrdersForCurrentTab(filteredOrders);
    updateSectionTitle(title);

    if (!filteredOrders.length) {
        list.innerHTML = `<p class="empty-cell">${getEmptyMessage()}</p>`;
        return;
    }

    list.innerHTML = "";
    filteredOrders.forEach((order) => list.appendChild(createOrderCard(order)));
}

function updateSectionTitle(title) {
    if (!title) return;

    if (currentTab === "active") {
        title.textContent = "Đơn đang đến";
        return;
    }

    if (currentTab === "history") {
        title.textContent = "Lịch sử";
        return;
    }

    title.textContent = "Đánh giá";
}

function getEmptyMessage() {
    if (currentTab === "active") return "Bạn chưa có đơn nào đang xử lý.";
    if (currentTab === "history") return "Bạn chưa có đơn hàng trong lịch sử.";
    return "Bạn chưa có đơn nào có thể đánh giá.";
}

function sortOrdersForCurrentTab(orders) {
    const copiedOrders = [...orders];

    if (currentTab === "active") {
        return copiedOrders.sort((a, b) => {
            const priorityA = getActiveOrderPriority(a);
            const priorityB = getActiveOrderPriority(b);

            if (priorityA !== priorityB) {
                return priorityA - priorityB;
            }

            return getOrderImportantTime(b) - getOrderImportantTime(a);
        });
    }

    return copiedOrders.sort((a, b) => getOrderImportantTime(b) - getOrderImportantTime(a));
}

function getActiveOrderPriority(order) {
    const status = normalizeStatus(order.trangThaiDon);

    if (status === "SHIPPING") return 1;
    if (status === "CONFIRMED") return 2;
    if (status === "PENDING") return 3;
    if (status === "CANCEL_REQUESTED") return 4;

    return 99;
}

function getOrderImportantTime(order) {
    const rawTime =
        order.thoiGianNhanHang ||
        order.thoiGianGiao ||
        order.deliveredAt ||
        order.updatedAt ||
        order.ngayDat;

    const time = new Date(rawTime).getTime();
    return Number.isNaN(time) ? 0 : time;
}

function createOrderCard(order) {
    const card = document.createElement("article");
    card.className = "customer-order-card";
    card.tabIndex = 0;

    const goToDetail = () => {
        window.location.href = `/orders/detail?maDH=${order.maDH}`;
    };

    card.onclick = goToDetail;
    card.onkeydown = (event) => {
        if (event.key === "Enter") {
            goToDetail();
        }
    };

    const items = orderDetailCache.get(order.maDH) || [];
    const previewItems = items.slice(0, 3);
    const remainingCount = Math.max(items.length - previewItems.length, 0);

    card.innerHTML = `
        <div class="customer-order-card__top">
            <div>
                <h3>Đơn hàng #${order.maDH}</h3>
                <p>${formatDate(order.ngayDat)}</p>
            </div>

            <span class="status ${getStatusClass(order.trangThaiDon)}">
                ${displayStatus(order.trangThaiDon)}
            </span>
        </div>

        <div class="customer-order-card__items">
            ${
                previewItems.length
                    ? previewItems.map((item) => renderOrderItemPreview(item)).join("")
                    : `<p class="order-note">Chưa có thông tin món ăn.</p>`
            }

            ${
                remainingCount > 0
                    ? `<p class="order-more-items">+ ${remainingCount} món khác</p>`
                    : ""
            }
        </div>

        <div class="customer-order-card__bottom">
            <div class="customer-order-total">
                <span>Thành tiền</span>
                <strong>${formatMoney(order.thanhTien)}</strong>
            </div>

            <div class="customer-order-actions">
                ${renderOrderAction(order)}
            </div>
        </div>
    `;

    return card;
}

function renderOrderItemPreview(item) {
    const imageSrc = buildFoodImageUrl(item);
    const itemName = item.tenMon || `Món #${item.maMon}`;

    return `
        <div class="customer-order-item">
            <img src="${imageSrc}" alt="${escapeHtml(itemName)}" loading="lazy" onerror="this.onerror=null;this.src='${DEFAULT_FOOD_IMAGE}';">
            <div>
                <strong>${escapeHtml(itemName)}</strong>
                <span>x${item.soLuong || 0}</span>
            </div>
            <b>${formatMoney(item.thanhTien)}</b>
        </div>
    `;
}

function renderOrderAction(order) {
    const status = normalizeStatus(order.trangThaiDon);

    if (status === "PENDING" || status === "CONFIRMED") {
        return `
            <button type="button" class="btn btn-danger" onclick="event.stopPropagation(); requestCancelOrder(${order.maDH});">
                Hủy đơn
            </button>
        `;
    }

    if (status === "SHIPPING") {
        return `
            <button type="button" class="btn btn-primary" onclick="event.stopPropagation(); confirmReceived(${order.maDH});">
                Đã nhận hàng
            </button>
        `;
    }

    if (status === "CANCEL_REQUESTED") {
        return `<span class="order-note">Đang chờ xử lý hủy</span>`;
    }

    if (currentTab === "review" && status === "DELIVERED") {
        return `
            <a class="btn btn-outline" href="/orders/detail?maDH=${order.maDH}" onclick="event.stopPropagation();">
                Đánh giá món
            </a>
        `;

        if (currentTab === "history") {
            return `
                ${reviewButton}
                <button type="button" class="btn btn-outline reorder-btn" onclick="event.stopPropagation(); reorderOrder(${order.maDH});">
                    Đặt lại
                </button>
            `;
        }

        return reviewButton;
    }

    return "";
}

async function reorderOrder(maDH) {
    const maKH = getCurrentMaKH();

    try {
        const response = await fetch(`${CLIENT_ORDER_API}/${maDH}/reorder?maKH=${maKH}`, {
            method: "POST"
        });

        const data = await response.json();
        alert(data.message || "Đã thêm lại món vào giỏ hàng.");

        if (response.ok && confirm("Bạn có muốn sang giỏ hàng ngay không?")) {
            window.location.href = "/cart";
        }
    } catch (error) {
        alert("Lỗi khi đặt lại đơn hàng.");
    }
}

async function requestCancelOrder(maDH) {
    const maKH = getCurrentMaKH();

    if (!confirm("Bạn có chắc muốn hủy đơn hàng này không?")) {
        return;
    }

    try {
        const response = await fetch(`${CLIENT_ORDER_API}/${maDH}/cancel?maKH=${maKH}`, {
            method: "POST"
        });

        const data = await response.json();
        alert(data.message || "Đã xử lý yêu cầu hủy đơn.");

        if (response.ok) {
            orderDetailCache.delete(maDH);
            await loadOrders();
            return;
        }
    } catch (error) {
        showMessage("Lỗi khi gửi yêu cầu hủy đơn.");
    }
}

async function confirmReceived(maDH) {
    const maKH = getCurrentMaKH();

    if (!confirm("Bạn xác nhận đã nhận được đơn hàng này?")) {
        return;
    }

    try {
        const response = await fetch(`${CLIENT_ORDER_API}/${maDH}/received?maKH=${maKH}`, {
            method: "POST"
        });

        const data = await response.json();
        alert(data.message || "Đã xác nhận nhận hàng.");

        if (response.ok) {
            orderDetailCache.delete(maDH);
            await loadOrders();
        }
    } catch (error) {
        alert("Lỗi khi xác nhận nhận hàng.");
    }
}

function getCurrentMaKH() {
    const input = document.getElementById("currentMaKH");
    return input ? input.value : "";
}

function showMessage(message) {
    const el = document.getElementById("message");
    if (el) {
        el.textContent = message || "";
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

function escapeHtml(value) {
    if (value === null || value === undefined) return "";

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
