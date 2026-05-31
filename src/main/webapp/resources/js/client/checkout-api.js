const CHECKOUT_API_BASE = "/api/checkout";
const CART_API_BASE = "/api/cart";
const VOUCHER_API_BASE = "/api/voucher";

let checkoutSubtotal = 0;
let checkoutDiscount = 0;
let checkoutTotal = 0;
const deliveryFee = 0;

const VOUCHER_TAG_COLORS = ["blue", "yellow", "green", "purple"];

document.addEventListener("DOMContentLoaded", () => {
    initSelectedAddress();
    loadCheckoutSummary();

    const placeOrderBtn = document.getElementById("btn-place-order");
    if (placeOrderBtn) {
        placeOrderBtn.addEventListener("click", submitCheckout);
    }

    const voucherApplyBtn = document.getElementById("voucher-apply");
    if (voucherApplyBtn) {
        voucherApplyBtn.addEventListener("click", applyVoucherPreview);
    }

    window.addEventListener("pageshow", () => {
        loadCheckoutSummary();
    });

    document.addEventListener("visibilitychange", () => {
        if (!document.hidden) {
            loadCheckoutSummary();
        }
    });
});

function getValue(id) {
    const el = document.getElementById(id);
    return el ? el.value : "";
}

function getCustomerId() {
    return Number(getValue("customerId") || 1);
}

async function loadCheckoutSummary() {
    const customerId = getCustomerId();
    const itemList = document.getElementById("checkoutItemList");
    const messageEl = document.getElementById("checkoutMessage");

    if (itemList) itemList.innerHTML = "";
    if (messageEl) messageEl.textContent = "";

    try {
        let items = null;
        let serverSubtotal = 0;

        const cartRes = await fetch(`${CART_API_BASE}?customerId=${customerId}`);
        if (cartRes.ok) {
            items = await cartRes.json();
        }

        if (!items || items.length === 0) {
            const summaryRes = await fetch(`${CHECKOUT_API_BASE}/summary?customerId=${customerId}`);
            if (summaryRes.ok) {
                const summaryJson = await summaryRes.json();
                items = summaryJson.data;
                serverSubtotal = Number(summaryJson.subtotal || 0);
            }
        }

        if (!items || items.length === 0) {
            if (itemList) {
                itemList.innerHTML = `
                    <div class="invoice-line">
                        <span>Giỏ hàng đang trống</span>
                        <strong>0 VND</strong>
                    </div>
                `;
            }

            updateInvoice(0, 0);
            await loadAvailableVouchers(0);
            return;
        }

        checkoutSubtotal = serverSubtotal > 0 ? serverSubtotal : calculateSubtotal(items);

        items.forEach(item => {
            const quantity = Number(item.soLuong || 0);
            const lineTotal = resolveLineTotal(item);

            const row = document.createElement("div");
            row.className = "invoice-line";
            row.innerHTML = `
                <strong>${quantity}x ${item.tenMon || ("Món #" + item.maMon)}</strong>
                <strong>${formatMoney(lineTotal)}</strong>
            `;

            itemList.appendChild(row);
        });

        if (!serverSubtotal || serverSubtotal <= 0) {
            checkoutSubtotal = calculateSubtotal(items);
        }

        updateInvoice(checkoutSubtotal, checkoutDiscount);
        await loadAvailableVouchers(checkoutSubtotal);

        const appliedCode = getValue("voucher-code").trim();
        if (appliedCode) {
            await applyVoucherPreview();
        }

    } catch (error) {
        showCheckoutMessage(error.message);
    }
}

function resolveLineTotal(item) {
    const quantity = Number(item.soLuong || 0);
    const lineTotal = Number(item.thanhTien);

    if (Number.isFinite(lineTotal) && lineTotal > 0) {
        return lineTotal;
    }

    const unitPrice = Number(item.donGia || 0);
    if (Number.isFinite(unitPrice) && quantity > 0) {
        return unitPrice * quantity;
    }

    return 0;
}

function calculateSubtotal(items) {
    return Math.round(
        (items || []).reduce((sum, item) => sum + resolveLineTotal(item), 0)
    );
}

async function loadAvailableVouchers(subtotal) {
    const listEl = document.querySelector("[data-voucher-list]");
    const wrapper = document.querySelector(".voucher-carousel-wrapper");
    if (!listEl) {
        return;
    }

    const normalizedSubtotal = Math.max(0, Math.round(Number(subtotal || 0)));

    try {
        const response = await fetch(`${VOUCHER_API_BASE}/available?subtotal=${normalizedSubtotal}`);
        if (!response.ok) {
            listEl.innerHTML = '<p class="voucher-empty">Không thể tải mã khuyến mãi.</p>';
            return;
        }

        const data = await response.json();
        const vouchers = data.vouchers || [];

        if (vouchers.length === 0) {
            listEl.innerHTML = '<p class="voucher-empty">Không có mã khuyến mãi phù hợp với đơn hàng hiện tại.</p>';
            if (wrapper) {
                wrapper.querySelectorAll("[data-voucher-arrow]").forEach(btn => {
                    btn.style.display = "none";
                });
            }
            return;
        }

        listEl.innerHTML = vouchers.map((voucher, index) => {
            const tagColor = VOUCHER_TAG_COLORS[index % VOUCHER_TAG_COLORS.length];
            const tagLabel = voucher.couponType === "PERCENTAGE" ? "Giảm %" : "Giảm tiền";
            const desc = voucher.description || `Giảm ${voucher.discountDisplay}`;

            return `
                <div class="voucher-card">
                    <div class="voucher-card__header">
                        <span class="voucher-card__title">${escapeHtml(voucher.code)}</span>
                        <span class="voucher-card__tag voucher-card__tag--${tagColor}">${tagLabel}</span>
                    </div>
                    <p class="voucher-card__desc">${escapeHtml(desc)}</p>
                    <div class="voucher-card__actions">
                        <button type="button" class="btn btn-primary voucher-card__btn" data-voucher-select="${escapeHtml(voucher.code)}">
                            Chọn
                        </button>
                    </div>
                </div>
            `;
        }).join("");

        listEl.querySelectorAll("[data-voucher-select]").forEach(button => {
            button.addEventListener("click", () => {
                const codeInput = document.getElementById("voucher-code");
                if (codeInput) {
                    codeInput.value = button.dataset.voucherSelect;
                }
                applyVoucherPreview();
            });
        });

        if (wrapper) {
            wrapper.querySelectorAll("[data-voucher-arrow]").forEach(btn => {
                btn.style.display = "";
            });
        }

        window.dispatchEvent(new Event("resize"));
    } catch (error) {
        listEl.innerHTML = '<p class="voucher-empty">Không thể tải mã khuyến mãi.</p>';
    }
}

async function applyVoucherPreview() {
    const code = getValue("voucher-code").trim();

    if (!code) {
        checkoutDiscount = 0;
        updateInvoice(checkoutSubtotal, checkoutDiscount);
        showVoucherMessage("");
        return;
    }

    try {
        const response = await fetch(
            `${VOUCHER_API_BASE}/validate?code=${encodeURIComponent(code)}&subtotal=${checkoutSubtotal}`
        );
        const data = await response.json();

        checkoutDiscount = Number(data.discountAmount || 0);
        showVoucherMessage(data.message || "");
        updateInvoice(checkoutSubtotal, checkoutDiscount);
    } catch (error) {
        checkoutDiscount = 0;
        showVoucherMessage("Không thể kiểm tra mã giảm giá. Vui lòng thử lại.");
        updateInvoice(checkoutSubtotal, checkoutDiscount);
    }
}

function updateInvoice(subtotal, discount) {
    const subtotalEl = document.getElementById("invoice-subtotal");
    const deliveryFeeEl = document.getElementById("invoice-delivery-fee");
    const discountEl = document.getElementById("invoice-discount");
    const totalEl = document.getElementById("invoice-total");

    checkoutSubtotal = Number(subtotal || 0);
    checkoutDiscount = Number(discount || 0);
    checkoutTotal = Math.max(checkoutSubtotal + deliveryFee - checkoutDiscount, 0);

    if (subtotalEl) subtotalEl.textContent = formatMoney(checkoutSubtotal);
    if (deliveryFeeEl) deliveryFeeEl.textContent = formatMoney(deliveryFee);
    if (discountEl) discountEl.textContent = "-" + formatMoney(checkoutDiscount);
    if (totalEl) totalEl.textContent = formatMoney(checkoutTotal);
}

async function submitCheckout() {
    const customerId = getCustomerId();
    const maDC = Number(getValue("addressSelect") || 0);
    const discountCode = getValue("voucher-code").trim();
    const ghiChu = buildCheckoutNote();

    const selectedPayment = document.querySelector('input[name="payment-method"]:checked');
    const maPT = selectedPayment ? selectedPayment.value : "COD";

    if (!customerId) {
        showCheckoutMessage("Không tìm thấy thông tin khách hàng.");
        return;
    }

    if (!maDC) {
        showCheckoutMessage("Vui lòng chọn địa chỉ giao hàng. Bấm 'Đổi địa chỉ' hoặc thêm địa chỉ tại /address/create.");
        return;
    }

    if (!maPT) {
        showCheckoutMessage("Vui lòng chọn phương thức thanh toán.");
        return;
    }

    try {
        const response = await fetch(CHECKOUT_API_BASE, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                customerId,
                maDC,
                discountCode,
                maPT,
                ghiChu
            })
        });

        const data = await response.json();

        if (!response.ok || !data.success) {
            showCheckoutMessage(data.message || "Đặt hàng thất bại.");
            return;
        }

        showCheckoutMessage(data.message || "Đặt hàng thành công.");

        if (maPT === "COD") {
            window.location.href = `/orders/detail?orderId=${data.orderId}`;
        } else {
            window.location.href = `/pay?orderId=${data.orderId}`;
        }

    } catch (error) {
        showCheckoutMessage("Lỗi khi đặt hàng. Vui lòng thử lại.");
    }
}

function buildCheckoutNote() {
    const name = getValue("delivery-name");
    const phone = getValue("delivery-phone");
    const email = getValue("delivery-email");
    const address = getValue("delivery-address");

    return `Người nhận: ${name}; SĐT: ${phone}; Email: ${email}; Địa chỉ nhập: ${address}`;
}

function showCheckoutMessage(message) {
    const messageEl = document.getElementById("checkoutMessage");

    if (messageEl) {
        messageEl.textContent = message || "";
    }
}

function showVoucherMessage(message) {
    const messageEl = document.getElementById("voucher-message");

    if (messageEl) {
        messageEl.textContent = message || "";
    }
}

function escapeHtml(value) {
    return String(value || "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

function formatMoney(value) {
    return Number(value || 0).toLocaleString("vi-VN") + " VND";
}

function initSelectedAddress() {
    const params = new URLSearchParams(window.location.search);

    const maDC = params.get("maDC");
    const address = params.get("address");
    const name = params.get("name");
    const phone = params.get("phone");
    const email = params.get("email");

    if (maDC) {
        const addressSelect = document.getElementById("addressSelect");
        if (addressSelect) addressSelect.value = maDC;

        localStorage.setItem("checkout_maDC", maDC);
    }

    if (address) {
        const addressInput = document.getElementById("delivery-address");
        if (addressInput) addressInput.value = decodeURIComponent(address);

        localStorage.setItem("checkout_address", decodeURIComponent(address));
    }

    if (name) {
        const nameInput = document.getElementById("delivery-name");
        if (nameInput) nameInput.value = decodeURIComponent(name);

        localStorage.setItem("checkout_name", decodeURIComponent(name));
    }

    if (phone) {
        const phoneInput = document.getElementById("delivery-phone");
        if (phoneInput) phoneInput.value = phone;

        localStorage.setItem("checkout_phone", phone);
    }

    if (email) {
        const emailInput = document.getElementById("delivery-email");
        if (emailInput) emailInput.value = email;

        localStorage.setItem("checkout_email", email);
    }

    restoreAddressFromStorage();
}

function restoreAddressFromStorage() {
    const addressSelect = document.getElementById("addressSelect");
    const addressInput = document.getElementById("delivery-address");
    const nameInput = document.getElementById("delivery-name");
    const phoneInput = document.getElementById("delivery-phone");
    const emailInput = document.getElementById("delivery-email");

    if (addressSelect && localStorage.getItem("checkout_maDC")) {
        addressSelect.value = localStorage.getItem("checkout_maDC");
    }

    if (addressInput && localStorage.getItem("checkout_address")) {
        addressInput.value = localStorage.getItem("checkout_address");
    }

    if (nameInput && localStorage.getItem("checkout_name")) {
        nameInput.value = localStorage.getItem("checkout_name");
    }

    if (phoneInput && localStorage.getItem("checkout_phone")) {
        phoneInput.value = localStorage.getItem("checkout_phone");
    }

    if (emailInput && localStorage.getItem("checkout_email")) {
        emailInput.value = localStorage.getItem("checkout_email");
    }
}

function initSelectedAddress() {
    const selectedAddress = localStorage.getItem("selectedCheckoutAddress");

    if (!selectedAddress) {
        return;
    }

    const addressData = JSON.parse(selectedAddress);

    const addressSelect = document.getElementById("addressSelect");
    const nameInput = document.getElementById("delivery-name");
    const phoneInput = document.getElementById("delivery-phone");
    const emailInput = document.getElementById("delivery-email");
    const addressInput = document.getElementById("delivery-address");

    if (addressSelect) addressSelect.value = addressData.maDC;
    if (nameInput) nameInput.value = addressData.name;
    if (phoneInput) phoneInput.value = addressData.phone;
    if (emailInput) emailInput.value = addressData.email;
    if (addressInput) addressInput.value = addressData.address;
}