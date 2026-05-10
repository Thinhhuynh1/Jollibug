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

    // Preview tạm trên frontend để người dùng thấy thay đổi.
    // Backend vẫn là nơi tính giảm giá chính xác khi POST /api/checkout.
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
    const maDC = Number(getValue("addressSelect") || 1);
    const discountCode = getValue("voucher-code").trim();
    const ghiChu = buildCheckoutNote();

    const selectedPayment = document.querySelector('input[name="payment-method"]:checked');
    const maPT = selectedPayment ? selectedPayment.value : "COD";

    if (!customerId) {
        showCheckoutMessage("Không tìm thấy thông tin khách hàng.");
        return;
    }

    if (!maDC) {
        showCheckoutMessage("Vui lòng chọn địa chỉ giao hàng.");
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
            window.location.href = `/client/orders/detail?orderId=${data.orderId}&customerId=${customerId}`;
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