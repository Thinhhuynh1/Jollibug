const CART_API_BASE = "/api/cart";
let pendingDeleteMaMon = null;

document.addEventListener("DOMContentLoaded", () => {
    setupDeleteModal();
    loadCart();
});

function getCustomerId() {
    const input = document.getElementById("customerId");
    return input ? input.value : 1;
}

async function loadCart() {
    const customerId = getCustomerId();
    const cartItems = document.getElementById("cartItems");
    const message = document.getElementById("cartMessage");
    const totalQuantityEl = document.getElementById("summary-item-count");
    const totalEl = document.getElementById("summary-total");

    if (!cartItems) return;

    cartItems.innerHTML = "";
    if (message) message.textContent = "";

    try {
        const response = await fetch(`${CART_API_BASE}?customerId=${customerId}`);

        if (!response.ok) {
            throw new Error("Không thể tải giỏ hàng.");
        }

        const items = await response.json();

        if (!items || items.length === 0) {
            cartItems.innerHTML = `
                <div class="cart-empty">
                    <h3>Giỏ hàng của bạn đang trống</h3>
                    <p>Hãy chọn thêm món để tiếp tục đặt hàng.</p>
                    <a class="btn btn-primary" href="/menu">Xem thực đơn</a>
                </div>
            `;

            updateSummary(0, 0);
            return;
        }

        let totalQuantity = 0;
        let totalAmount = 0;

        items.forEach(item => {
            const soLuong = Number(item.soLuong || 0);
            const thanhTien = Number(item.thanhTien || 0);

            totalQuantity += soLuong;
            totalAmount += thanhTien;

            const imageUrl = buildProductImageUrl(item.imageUrl);

            const donGia = Number(item.donGia || 0);
            const donGiaGoc = Number(item.donGiaGoc || donGia);
            const hasDiscount = donGiaGoc > donGia;

            const line = document.createElement("article");
            line.className = "cart-line";
            line.id = `cart-line-${item.maMon}`;
            line.dataset.price = donGia;
            if (hasDiscount) {
                line.dataset.origPrice = donGiaGoc;
            }

            line.innerHTML = `
                <div class="cart-line__thumb">
                    <img 
                        src="${imageUrl}"
                        alt="${item.tenMon || "Món ăn"}"
                        onerror="this.onerror=null;this.src='/images/house-regular.png';"
                        style="width:100%;height:100%;object-fit:cover;border-radius:10px;"
                    />
                </div>

                <div class="cart-line__meta">
                    <h3 class="cart-line__name">${item.tenMon || `Món #${item.maMon}`}</h3>

                    <div class="cart-line__controls">
                        <a class="cart-link-btn" href="#" data-action="remove" onclick="removeCartItem(event, ${item.maMon})">
                            Xóa
                        </a>

                        <div class="cart-line__purchase">
                            <div class="qty-stepper" aria-label="Chỉnh số lượng">
                                <button class="qty-stepper__btn" type="button" aria-label="Giảm số lượng" onclick="changeQuantity(${item.maMon}, -1)">-</button>
                                <span class="qty-stepper__value" id="qty-${item.maMon}">${soLuong}</span>
                                <button class="qty-stepper__btn" type="button" aria-label="Tăng số lượng" onclick="changeQuantity(${item.maMon}, 1)">+</button>
                            </div>
                            <div style="display:flex;flex-direction:column;align-items:flex-end;">
                                ${hasDiscount ? `<span style="text-decoration:line-through;color:#999;font-size:0.85em;font-weight:500;" id="orig-${item.maMon}">${formatMoney(donGiaGoc * soLuong)}</span>` : ""}
                                <strong class="cart-line__sum" id="sum-${item.maMon}">
                                    ${formatMoney(thanhTien)}
                                </strong>
                            </div>
                        </div>
                    </div>
                </div>
            `;

            cartItems.appendChild(line);
        });

        updateSummary(totalQuantity, totalAmount);

    } catch (error) {
        if (message) {
            message.textContent = error.message;
        }
    }
}

function updateSummary(totalQuantity, totalAmount) {
    const totalQuantityEl = document.getElementById("summary-item-count");
    const totalEl = document.getElementById("summary-total");
    const headerCartCountEl = document.getElementById("header-cart-count");

    if (totalQuantityEl) {
        totalQuantityEl.textContent = `${totalQuantity} MÓN`;
    }

    if (totalEl) {
        totalEl.textContent = formatMoney(totalAmount);
    }

    if (headerCartCountEl) {
        headerCartCountEl.textContent = totalQuantity;
    }
}

async function changeQuantity(maMon, delta) {
    const qtyEl = document.getElementById(`qty-${maMon}`);
    const line = document.getElementById(`cart-line-${maMon}`);

    if (!qtyEl || !line) return;

    const currentQty = Number(qtyEl.textContent);
    const newQty = currentQty + delta;

    if (newQty <= 0) {
        openDeleteModal(maMon);
        return;
    }

    // Cập nhật UI trước cho mượt
    qtyEl.textContent = newQty;

    const price = Number(line.dataset.price || 0);
    const newLineTotal = price * newQty;

    const sumEl = document.getElementById(`sum-${maMon}`);
    if (sumEl) {
        sumEl.textContent = formatMoney(newLineTotal);
    }

    const origEl = document.getElementById(`orig-${maMon}`);
    if (origEl) {
        const origUnit = Number(line.dataset.origPrice || 0);
        if (origUnit > 0) {
            origEl.textContent = formatMoney(origUnit * newQty);
        }
    }

    recalculateSummaryFromDOM();

    const success = await updateCartItem(maMon, newQty);

    // Nếu API lỗi thì load lại để đồng bộ DB
    if (!success) {
        loadCart();
    }
}

async function updateCartItem(maMon, soLuong) {
    const customerId = Number(getCustomerId());

    try {
        const response = await fetch(`${CART_API_BASE}/items`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                customerId,
                maMon,
                soLuong
            })
        });

        const data = await response.json();

        if (!response.ok) {
            alert(data.message || "Không thể cập nhật số lượng.");
            return false;
        }

        return true;

    } catch (error) {
        alert("Lỗi khi cập nhật số lượng.");
        return false;
    }
}

async function removeCartItem(event, maMon) {
    event.preventDefault();

    openDeleteModal(maMon);
}

function setupDeleteModal() {
    const modal = document.getElementById("deleteConfirmModal");
    const cancelBtn = document.getElementById("cancelDeleteBtn");
    const confirmBtn = document.getElementById("confirmDeleteBtn");

    if (!modal || !cancelBtn || !confirmBtn) return;

    cancelBtn.addEventListener("click", closeDeleteModal);

    modal.addEventListener("click", (event) => {
        if (event.target === modal) {
            closeDeleteModal();
        }
    });

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape" && modal.classList.contains("is-open")) {
            closeDeleteModal();
        }
    });

    confirmBtn.addEventListener("click", async () => {
        if (!pendingDeleteMaMon) return;

        confirmBtn.disabled = true;
        const originalText = confirmBtn.textContent;
        confirmBtn.textContent = "Đang xóa...";

        const deleted = await deleteCartItem(pendingDeleteMaMon);

        confirmBtn.disabled = false;
        confirmBtn.textContent = originalText;

        if (deleted) {
            closeDeleteModal();
        }
    });
}

function openDeleteModal(maMon) {
    const modal = document.getElementById("deleteConfirmModal");
    const confirmBtn = document.getElementById("confirmDeleteBtn");

    if (!modal) return;

    pendingDeleteMaMon = maMon;
    modal.classList.add("is-open");
    modal.setAttribute("aria-hidden", "false");
    document.body.classList.add("is-lock-scroll");

    if (confirmBtn) {
        confirmBtn.focus();
    }
}

function closeDeleteModal() {
    const modal = document.getElementById("deleteConfirmModal");
    const confirmBtn = document.getElementById("confirmDeleteBtn");

    pendingDeleteMaMon = null;

    if (modal) {
        modal.classList.remove("is-open");
        modal.setAttribute("aria-hidden", "true");
    }

    if (confirmBtn) {
        confirmBtn.disabled = false;
        confirmBtn.textContent = "Xóa món";
    }

    document.body.classList.remove("is-lock-scroll");
}

async function deleteCartItem(maMon) {
    const customerId = getCustomerId();

    try {
        const response = await fetch(`${CART_API_BASE}/items?customerId=${customerId}&maMon=${maMon}`, {
            method: "DELETE"
        });

        const data = await response.json();

        if (!response.ok) {
            alert(data.message || "Không thể xóa món khỏi giỏ hàng.");
            return false;
        }

        const line = document.getElementById(`cart-line-${maMon}`);
        if (line) {
            line.remove();
        }

        recalculateSummaryFromDOM();

        const remainingItems = document.querySelectorAll(".cart-line").length;
        if (remainingItems === 0) {
            loadCart();
        }

        return true;

    } catch (error) {
        alert("Lỗi khi xóa món khỏi giỏ hàng.");
        return false;
    }
}

function recalculateSummaryFromDOM() {
    const lines = document.querySelectorAll(".cart-line");

    let totalQuantity = 0;
    let totalAmount = 0;

    lines.forEach(line => {
        const maMon = line.id.replace("cart-line-", "");
        const qtyEl = document.getElementById(`qty-${maMon}`);

        const quantity = qtyEl ? Number(qtyEl.textContent) : 0;
        const price = Number(line.dataset.price || 0);

        totalQuantity += quantity;
        totalAmount += quantity * price;
    });

    updateSummary(totalQuantity, totalAmount);
}

function formatMoney(value) {
    return Number(value || 0).toLocaleString("vi-VN") + " VND";
}

function buildProductImageUrl(rawImageUrl) {
    if (!rawImageUrl) {
        return "/images/house-regular.png";
    }

    const value = String(rawImageUrl).trim();
    if (!value) {
        return "/images/house-regular.png";
    }

    if (value.startsWith("http://") || value.startsWith("https://")) {
        return value;
    }

    if (value.startsWith("/images/")) {
        return value;
    }

    if (value.startsWith("/resources/images/")) {
        return value.replace("/resources/images/", "/images/");
    }

    if (value.startsWith("images/")) {
        return `/${value}`;
    }

    return `/images/${value}`;
}
