const CHECKOUT_API_BASE = "/api/checkout";

let checkoutSubtotal = 0;
let checkoutDiscount = 0;
let checkoutTotal = 0;
const deliveryFee = 0;

document.addEventListener("DOMContentLoaded", function () {
    restoreCheckoutAddress();
    initializeInvoiceFromSession();
    bindVoucherPreview();

    const checkoutButton = document.getElementById("checkoutButton");
    if (checkoutButton) {
        checkoutButton.addEventListener("click", submitCheckout);
    }
});

function getValue(id) {
    const element = document.getElementById(id);
    return element ? element.value : "";
}

function setValue(id, value) {
    const element = document.getElementById(id);
    if (element) {
        element.value = value || "";
    }
}

function getCustomerId() {
    return Number(getValue("customerId") || 0);
}

function restoreCheckoutAddress() {
    const selectedAddress = readSelectedAddress();

    if (selectedAddress) {
        applyAddressToForm(selectedAddress);
        return;
    }

    const fallbackAddress = buildDefaultAddress();
    applyAddressToForm(fallbackAddress);
}

function readSelectedAddress() {
    const selectedAddressRaw = localStorage.getItem("selectedCheckoutAddress");

    if (!selectedAddressRaw) {
        return null;
    }

    try {
        return JSON.parse(selectedAddressRaw);
    } catch (error) {
        localStorage.removeItem("selectedCheckoutAddress");
        return null;
    }
}

function buildDefaultAddress() {
    const fullAddress = [
        getValue("defaultAddressLine").trim(),
        getValue("defaultWard").trim(),
        getValue("defaultDistrict").trim(),
        getValue("defaultProvince").trim()
    ].filter(Boolean).join(", ");

    return {
        maDC: getValue("addressSelect").trim() || null,
        name: getValue("defaultAddressName").trim() || getValue("currentUserName").trim(),
        phone: getValue("defaultAddressPhone").trim() || getValue("currentUserPhone").trim(),
        email: getValue("currentUserEmail").trim(),
        address: fullAddress
    };
}

function applyAddressToForm(address) {
    if (!address) {
        return;
    }

    setValue("addressSelect", address.maDC || "");
    setValue("delivery-name", address.name || "");
    setValue("delivery-phone", address.phone || "");
    setValue("delivery-email", address.email || "");
    setValue("delivery-address", address.address || "");
}

function initializeInvoiceFromSession() {
    const rows = document.querySelectorAll(".checkout-session-item");

    if (!rows.length) {
        updateInvoice(0, 0);
        return;
    }

    let subtotal = 0;
    rows.forEach(function (row) {
        subtotal += Number(row.dataset.lineTotal || 0);
    });

    updateInvoice(subtotal, 0);
}

function bindVoucherPreview() {
    const voucherInput = document.getElementById("voucher-code");
    const applyButton = document.getElementById("voucher-apply");

    if (voucherInput) {
        voucherInput.addEventListener("input", applyVoucherPreview);
    }

    if (applyButton) {
        applyButton.addEventListener("click", applyVoucherPreview);
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
        showCheckoutMessage("Da ap dung ma JOLLI10. Giam 10% tam tinh.");
    } else if (code === "FREESHIP20") {
        checkoutDiscount = checkoutSubtotal >= 80000 ? 20000 : 0;
        showCheckoutMessage(
            checkoutDiscount > 0
                ? "Da ap dung ma FREESHIP20."
                : "Ma FREESHIP20 chi ap dung cho don tu 80.000 VND."
        );
    } else {
        checkoutDiscount = 0;
        showCheckoutMessage("Ma giam gia se duoc kiem tra khi dat hang.");
    }

    updateInvoice(checkoutSubtotal, checkoutDiscount);
}

function updateInvoice(subtotal, discount) {
    checkoutSubtotal = Number(subtotal || 0);
    checkoutDiscount = Number(discount || 0);
    checkoutTotal = Math.max(checkoutSubtotal + deliveryFee - checkoutDiscount, 0);

    const subtotalEl = document.getElementById("invoice-subtotal");
    const deliveryFeeEl = document.getElementById("invoice-delivery-fee");
    const discountEl = document.getElementById("invoice-discount");
    const totalEl = document.getElementById("invoice-total");

    if (subtotalEl) {
        subtotalEl.textContent = formatMoney(checkoutSubtotal);
    }
    if (deliveryFeeEl) {
        deliveryFeeEl.textContent = formatMoney(deliveryFee);
    }
    if (discountEl) {
        discountEl.textContent = "-" + formatMoney(checkoutDiscount);
    }
    if (totalEl) {
        totalEl.textContent = formatMoney(checkoutTotal);
    }
}

async function submitCheckout() {
    const customerId = getCustomerId();
    const addressValue = getValue("addressSelect").trim();
    const maDC = addressValue ? Number(addressValue) : null;
    const selectedPayment = document.querySelector('input[name="payment-method"]:checked');
    const maPT = selectedPayment ? selectedPayment.value : null;

    const payload = {
        customerId: customerId,
        maDC: maDC,
        maPT: maPT,
        discountCode: getValue("voucher-code").trim() || null,
        ghiChu: "",
        deliveryName: getValue("delivery-name").trim(),
        deliveryPhone: getValue("delivery-phone").trim(),
        email: getValue("delivery-email").trim(),
        deliveryAddress: getValue("delivery-address").trim()
    };

    if (!payload.customerId) {
        alert("Khong tim thay thong tin tai khoan. Vui long dang nhap lai.");
        return;
    }

    if (!payload.maDC) {
        alert("Vui long chon dia chi giao hang.");
        return;
    }

    if (!payload.maPT) {
        alert("Vui long chon phuong thuc thanh toan.");
        return;
    }

    try {
        const response = await fetch(CHECKOUT_API_BASE, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (!response.ok || !data.success) {
            alert(data.message || "Thanh toan that bai.");
            return;
        }

        if (payload.maPT === "COD") {
            localStorage.removeItem("selectedCheckoutAddress");
            alert("Dat hang thanh cong");
            window.location.href = "/orders/detail?orderId=" + encodeURIComponent(data.orderId);
            return;
        }

        window.location.href = "/pay?orderId="
            + encodeURIComponent(data.orderId)
            + "&customerId="
            + encodeURIComponent(payload.customerId)
            + "&maPT="
            + encodeURIComponent(payload.maPT);
    } catch (error) {
        console.error("[CHECKOUT ERROR]", error);
        alert("Co loi xay ra khi thanh toan. Vui long thu lai.");
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
