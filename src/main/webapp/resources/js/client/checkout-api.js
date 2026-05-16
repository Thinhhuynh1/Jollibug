const CHECKOUT_API_BASE = "/api/checkout";
const CART_API_BASE = "/api/cart";

let checkoutSubtotal = 0;
let checkoutDiscount = 0;
let checkoutTotal = 0;
const deliveryFee = 0;

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
});

window.addEventListener("pageshow", () => {
    loadCheckoutSummary();
});

function getValue(id) {
    const el = document.getElementById(id);
    return el ? el.value : "";
}

function getCustomerId() {
    return Number(getValue("customerId") || 0);
}

async function loadCheckoutSummary() {
    const customerId = getCustomerId();
    const itemList = document.getElementById("checkoutItemList");
    const messageEl = document.getElementById("checkoutMessage");

    if (itemList) itemList.innerHTML = "";
    if (messageEl) messageEl.textContent = "";

    if (!customerId) {
        showCheckoutMessage("Không tìm thấy thông tin khách hàng.");
        updateInvoice(0, 0);
        return;
    }

    try {
        const response = await fetch(`${CART_API_BASE}?customerId=${customerId}`);

        if (!response.ok) {
            throw new Error("Không thể tải tóm tắt giỏ hàng.");
        }

        const items = await response.json();

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
            return;
        }

        checkoutSubtotal = 0;

        items.forEach(item => {
            const quantity = Number(item.soLuong || 0);
            const lineTotal = Number(item.thanhTien || 0);
            checkoutSubtotal += lineTotal;

            const row = document.createElement("div");
            row.className = "invoice-line";
            row.innerHTML = `
                <strong>${quantity}x ${item.tenMon || ("Món #" + item.maMon)}</strong>
                <strong>${formatMoney(lineTotal)}</strong>
            `;

            itemList.appendChild(row);
        });

        updateInvoice(checkoutSubtotal, 0);
    } catch (error) {
        showCheckoutMessage(error.message);
    }
}

function applyVoucherPreview() {
    const code = getValue("voucher-code").trim().toUpperCase();

    if (!code) {
        checkoutDiscount = 0;
        updateInvoice(checkoutSubtotal, checkoutDiscount);
        showCheckoutMessage("");
        return;
    }

    if (code === "JOLLI10") {
        checkoutDiscount = Math.round(checkoutSubtotal * 0.1);
        showCheckoutMessage("Đã áp dụng mã JOLLI10. Giảm 10% tạm tính.");
    } else if (code === "FREESHIP20") {
        checkoutDiscount = checkoutSubtotal >= 80000 ? 20000 : 0;
        showCheckoutMessage(checkoutDiscount > 0
            ? "Đã áp dụng mã FREESHIP20."
            : "Mã FREESHIP20 chỉ áp dụng cho đơn từ 80.000 VND.");
    } else {
        checkoutDiscount = 0;
        showCheckoutMessage("Mã giảm giá sẽ được kiểm tra khi đặt hàng.");
    }

    updateInvoice(checkoutSubtotal, checkoutDiscount);
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
    const addressValue = getValue("addressSelect").trim();
    const maDC = addressValue ? Number(addressValue) : null;
    const discountCode = getValue("voucher-code").trim();
    const deliveryName = getValue("delivery-name").trim();
    const deliveryPhone = getValue("delivery-phone").trim();
    const email = getValue("delivery-email").trim();
    const deliveryAddress = getValue("delivery-address").trim();

    const selectedPayment = document.querySelector('input[name="payment-method"]:checked');
    const maPT = selectedPayment ? selectedPayment.value : "COD";

    if (!customerId) {
        showCheckoutMessage("Không tìm thấy thông tin khách hàng.");
        return;
    }

    if (!deliveryName || !deliveryPhone || !deliveryAddress) {
        showCheckoutMessage("Vui lòng nhập đủ thông tin giao hàng.");
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
                ghiChu: "",
                deliveryName,
                deliveryPhone,
                email,
                deliveryAddress
            })
        });

        const data = await response.json();

        if (!response.ok || !data.success) {
            showCheckoutMessage(data.message || "Đặt hàng thất bại.");
            return;
        }

        showCheckoutMessage(data.message || "Đặt hàng thành công.");

        if (maPT === "COD") {
            window.location.href = `/client/orders/detail?orderId=${data.orderId}&customerId=${customerId}`;
        } else {
            window.location.href = `/pay?orderId=${data.orderId}`;
        }
    } catch (error) {
        showCheckoutMessage("Lỗi khi đặt hàng. Vui lòng thử lại.");
    }
}

function showCheckoutMessage(message) {
    const messageEl = document.getElementById("checkoutMessage");

    if (messageEl) {
        messageEl.textContent = message || "";
    }
}

function formatMoney(value) {
    return Number(value || 0).toLocaleString("vi-VN") + " VND";
}

function initSelectedAddress() {
    const selectedAddress = localStorage.getItem("selectedCheckoutAddress");
    const addressSelect = document.getElementById("addressSelect");
    const nameInput = document.getElementById("delivery-name");
    const phoneInput = document.getElementById("delivery-phone");
    const emailInput = document.getElementById("delivery-email");
    const addressInput = document.getElementById("delivery-address");

    const currentUserName = getValue("currentUserName").trim();
    const currentUserPhone = getValue("currentUserPhone").trim();
    const currentUserEmail = getValue("currentUserEmail").trim();
    const defaultAddressId = getValue("addressSelect").trim();
    const defaultAddressName = getValue("defaultAddressName").trim();
    const defaultAddressPhone = getValue("defaultAddressPhone").trim();
    const defaultAddressFull = [
        getValue("defaultAddressLine").trim(),
        getValue("defaultWard").trim(),
        getValue("defaultDistrict").trim(),
        getValue("defaultProvince").trim()
    ].filter(Boolean).join(", ");

    if (selectedAddress) {
        const addressData = JSON.parse(selectedAddress);

        if (addressSelect) addressSelect.value = addressData.maDC || "";
        if (nameInput) nameInput.value = addressData.name || currentUserName;
        if (phoneInput) phoneInput.value = addressData.phone || currentUserPhone;
        if (emailInput) emailInput.value = addressData.email || currentUserEmail;
        if (addressInput) addressInput.value = addressData.address || "";
        return;
    }

    if (addressSelect) addressSelect.value = defaultAddressId || "";
    if (nameInput) nameInput.value = defaultAddressName || currentUserName || "";
    if (phoneInput) phoneInput.value = defaultAddressPhone || currentUserPhone || "";
    if (emailInput) emailInput.value = currentUserEmail || "";
    if (addressInput) addressInput.value = defaultAddressFull || "";
}
