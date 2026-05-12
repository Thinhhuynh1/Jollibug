const CLIENT_ORDER_API = "/api/orders";

let allOrders = [];
let currentTab = "active";
const orderDetailCache = new Map();

document.addEventListener("DOMContentLoaded", () => {
    loadOrders();
});

async function loadOrders() {
    const customerId = getCurrentCustomerId();
    const list = document.getElementById("orderCardList");
    const message = document.getElementById("message");

    if (!list) return;
    if (message) message.textContent = "";

    if (!customerId) {
        list.innerHTML = `<p class="empty-cell">Bạn cần đăng nhập để xem lịch sử đơn hàng.</p>`;
        return;
    }

    list.innerHTML = `<p class="empty-cell">Đang tải đơn hàng...</p>`;

    try {
        const response = await fetch(`${CLIENT_ORDER_API}?customerId=${customerId}`);
        const orders = await response.json();

        if (!response.ok) {
            throw new Error("Không thể tải lịch sử đơn hàng.");
        }

        allOrders = Array.isArray(orders) ? orders : [];

        await preloadOrderSummaries(allOrders);
        renderOrdersByTab();

    } catch (error) {
        list.innerHTML = `<p class="empty-cell">${error.message}</p>`;
    }
}

async function preloadOrderSummaries(orders) {
    const customerId = getCurrentCustomerId();

    const tasks = orders.map(async (order) => {
        if (!order || !order.maDH || orderDetailCache.has(order.maDH)) return;

        try {
            const response = await fetch(`${CLIENT_ORDER_API}/${order.maDH}?customerId=${customerId}`);
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

    document.querySelectorAll(".order-tab").forEach(button => {
        button.classList.toggle("is-active", button.dataset.tab === tab);
    });

    renderOrdersByTab();
}

function renderOrdersByTab() {
    const list = document.getElementById("orderCardList");
    const title = document.getElementById("orderSectionTitle");

    if (!list) return;

    let filteredOrders = allOrders.filter(order => {
        const status = normalizeStatus(order.trangThaiDon);

        if (currentTab === "active") {
            return ["PENDING", "CONFIRMED", "SHIPPING", "CANCEL_REQUESTED"].includes(status);
        }

        if (currentTab === "history") {
            return ["DELIVERED", "CANCELLED"].includes(status);
        }

        if (currentTab === "review") {
            return status === "DELIVERED";
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

    filteredOrders.forEach(order => {
        const card = createOrderCard(order);
        list.appendChild(card);
    });
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

    return copiedOrders.sort((a, b) => {
        return getOrderImportantTime(b) - getOrderImportantTime(a);
    });
}

function getActiveOrderPriority(order) {
    const status = normalizeStatus(order.trangThaiDon);

    // Ưu tiên cao nhất: đơn đang giao, tức khách cần xác nhận đã nhận hàng.
    if (status === "SHIPPING") return 1;

    // Sau đó là đơn đã xác nhận, đang chờ giao.
    if (status === "CONFIRMED") return 2;

    // Sau đó là đơn mới, chờ xác nhận.
    if (status === "PENDING") return 3;

    // Cuối cùng là đơn đang yêu cầu hủy.
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

    card.onclick = () => {
        window.location.href = `/orders/detail?orderId=${order.maDH}`;
    };

    card.onkeydown = (event) => {
        if (event.key === "Enter") {
            window.location.href = `/orders/detail?orderId=${order.maDH}`;
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
                ? previewItems.map(item => renderOrderItemPreview(item)).join("")
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
    const imageSrc = item.hinhAnh || item.imageUrl || getFallbackFoodImage(item.maMon);

    return `
        <div class="customer-order-item">
            <img src="${imageSrc}" alt="${item.tenMon || "Món ăn"}" loading="lazy">
            <div>
                <strong>${item.tenMon || `Món #${item.maMon}`}</strong>
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
            <a class="btn btn-outline" href="/orders/detail?orderId=${order.maDH}" onclick="event.stopPropagation();">
                Đánh giá món
            </a>
        `;
    }

    if (currentTab === "history" && status === "DELIVERED") {
        return `
            <button type="button" class="btn btn-outline reorder-btn" onclick="event.stopPropagation(); reorderOrder(${order.maDH});">
                Đặt lại
            </button>
        `;
    }

    return "";
}

async function reorderOrder(orderId) {
    const customerId = getCurrentCustomerId();

    try {
        const response = await fetch(`${CLIENT_ORDER_API}/${orderId}/reorder?customerId=${customerId}`, {
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

async function requestCancelOrder(orderId) {
    const customerId = getCurrentCustomerId();

    if (!confirm("Bạn có chắc muốn hủy đơn hàng này không?")) {
        return;
    }

    try {
        const response = await fetch(`${CLIENT_ORDER_API}/${orderId}/cancel?customerId=${customerId}`, {
            method: "POST"
        });

        const data = await response.json();

        alert(data.message || "Đã xử lý yêu cầu hủy đơn.");

        if (response.ok) {
            orderDetailCache.delete(orderId);
            await loadOrders();
        }

    } catch (error) {
        alert("Lỗi khi gửi yêu cầu hủy đơn.");
    }
}

async function confirmReceived(orderId) {
    const customerId = getCurrentCustomerId();

    if (!confirm("Bạn xác nhận đã nhận được đơn hàng này?")) {
        return;
    }

    try {
        const response = await fetch(`${CLIENT_ORDER_API}/${orderId}/received?customerId=${customerId}`, {
            method: "POST"
        });

        const data = await response.json();

        alert(data.message || "Đã xác nhận nhận hàng.");

        if (response.ok) {
            orderDetailCache.delete(orderId);
            await loadOrders();
        }

    } catch (error) {
        alert("Lỗi khi xác nhận nhận hàng.");
    }
}

function getCurrentCustomerId() {
    const input = document.getElementById("currentCustomerId");
    return input ? input.value : "";
}

function normalizeStatus(status) {
    return (status || "").trim().toUpperCase();
}

function displayStatus(status) {
    const statusMap = {
        PENDING: "Đã đặt hàng",
        CONFIRMED: "Đã xác nhận",
        SHIPPING: "Đang giao",
        DELIVERED: "Đã giao",
        CANCEL_REQUESTED: "Yêu cầu hủy",
        CANCELLED: "Đã hủy"
    };

    return statusMap[normalizeStatus(status)] || status || "-";
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

function getFallbackFoodImage(maMon) {
    const images = [
        "https://static.kfcvietnam.com.vn/images/items/lg/6-COB-April.jpg?v=3ydVxg",
        "https://static.kfcvietnam.com.vn/images/items/lg/D1-new.jpg?v=3ydVxg",
        "https://static.kfcvietnam.com.vn/images/items/lg/Burger-Zinger.jpg?v=3ydVxg",
        "https://static.kfcvietnam.com.vn/images/items/lg/FF-R.jpg?v=3ydVxg"
    ];

    const index = Math.abs(Number(maMon || 0)) % images.length;
    return images[index];
}
