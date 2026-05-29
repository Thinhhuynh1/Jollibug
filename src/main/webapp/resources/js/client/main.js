// Gợi ý địa chỉ
document.addEventListener("DOMContentLoaded", function() {
    const addressInput = document.getElementById('delivery-address');
    const nameInput = document.getElementById('delivery-name');
    const phoneInput = document.getElementById('delivery-phone');
    const suggestionsBox = document.getElementById('address-suggestions');
    
    // Chỉ chạy script này trên trang có ô nhập địa chỉ checkout
    if (!addressInput || !suggestionsBox) return;
    
    let searchTimeout = null;

    // Lắng nghe sự kiện gõ phím
    addressInput.addEventListener('input', function() {
        const val = this.value.trim();
        
        if (!val) {
            suggestionsBox.style.display = 'none';
            return;
        }
        
        // Dùng Debounce (đợi 500ms sau khi ngừng gõ mới gọi API) để không bị block
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            // Thêm addressdetails=1 để lấy chi tiết số nhà, đường, phường...
            const apiUrl = 'https://nominatim.openstreetmap.org/search?q=' + encodeURIComponent(val) + '&countrycodes=vn&format=json&addressdetails=1&limit=5&accept-language=vi';
            
            fetch(apiUrl)
                .then(res => res.json())
                .then(data => {
                    if (data && data.length > 0) {
                        suggestionsBox.innerHTML = '';
                        data.forEach(place => {
                            // Format lại địa chỉ theo chuẩn Việt Nam
                            const addr = place.address || {};
                            const parts = [];
                            
                            if (addr.house_number) parts.push(addr.house_number);
                            if (addr.road) parts.push(addr.road);
                            if (addr.suburb || addr.quarter || addr.neighbourhood) parts.push(addr.suburb || addr.quarter || addr.neighbourhood);
                            if (addr.city_district || addr.district || addr.county) parts.push(addr.city_district || addr.district || addr.county);
                            if (addr.city || addr.state || addr.province) parts.push(addr.city || addr.state || addr.province);
                            
                            const formattedAddress = parts.length > 0 ? parts.join(', ') : place.display_name;

                            const item = document.createElement('div');
                            item.style.cssText = "padding: 10px 14px; border-bottom: 1px solid #eee; cursor: pointer; font-size: 0.9rem; transition: background 0.2s;";
                            item.innerHTML = '<strong style="color: var(--color-ink-900);">' + formattedAddress + '</strong>';
                            
                            // Sự kiện chọn địa chỉ
                            item.addEventListener('click', function() {
                                addressInput.value = formattedAddress;
                                suggestionsBox.style.display = 'none';
                            });
                            
                            // Hiệu ứng hover
                            item.addEventListener('mouseenter', () => item.style.background = 'rgba(230, 0, 0, 0.04)');
                            item.addEventListener('mouseleave', () => item.style.background = '#fff');
                            
                            suggestionsBox.appendChild(item);
                        });
                        suggestionsBox.style.display = 'block';
                    } else {
                        suggestionsBox.innerHTML = '<div style="padding: 10px 14px; color: #888; font-size: 0.9rem;">Không tìm thấy địa chỉ</div>';
                        suggestionsBox.style.display = 'block';
                    }
                })
                .catch(err => {
                    console.error('Lỗi lấy địa chỉ thực tế:', err);
                });
        }, 500);
    });

    // Ẩn dropdown khi click ra ngoài
    document.addEventListener('click', function(e) {
        if (e.target !== addressInput && !suggestionsBox.contains(e.target)) {
            suggestionsBox.style.display = 'none';
        }
    });
    
    // Hiện lại dropdown nếu click vào input và đã có giá trị khớp
    addressInput.addEventListener('focus', function() {
        if (this.value.trim() !== '' && suggestionsBox.innerHTML !== '') {
            suggestionsBox.style.display = 'block';
        }
    });
});

// Cuộn danh sách trai phai
(function () {
    var strip = document.querySelector("[data-voucher-list]");
    if (!strip) return;
  
    var prevBtn = document.querySelector("[data-voucher-arrow='prev']");
    var nextBtn = document.querySelector("[data-voucher-arrow='next']");
    if (!prevBtn || !nextBtn) return;
  
    function scrollStep() {
      return Math.max(180, Math.floor(strip.clientWidth * 0.75));
    }
  
    function updateArrows() {
      var maxScroll = strip.scrollWidth - strip.clientWidth;
      var hasOverflow = maxScroll > 1;
  
      prevBtn.style.display = (!hasOverflow || strip.scrollLeft <= 1) ? 'none' : 'flex';
      nextBtn.style.display = (!hasOverflow || strip.scrollLeft >= maxScroll - 1) ? 'none' : 'flex';
    }
  
    prevBtn.addEventListener("click", function () {
      strip.scrollBy({ left: -scrollStep(), behavior: "smooth" });
    });
  
    nextBtn.addEventListener("click", function () {
      strip.scrollBy({ left: scrollStep(), behavior: "smooth" });
    });
  
    strip.addEventListener("scroll", updateArrows, { passive: true });
    window.addEventListener("resize", updateArrows);
    
    // Cập nhật mũi tên sau khi DOM render xong
    setTimeout(updateArrows, 100);
})();

// Cuộn danh mục món ăn trái/phải ở trang menu
(function () {
    var strip = document.querySelector("[data-menu-cats]");
    if (!strip) return;

    var prevBtn = document.querySelector("[data-cat-arrow='prev']");
    var nextBtn = document.querySelector("[data-cat-arrow='next']");
    if (!prevBtn || !nextBtn) return;

    function scrollStep() {
        return Math.max(140, Math.floor(strip.clientWidth * 0.6));
    }

    function updateArrows() {
        var maxScroll = strip.scrollWidth - strip.clientWidth;
        var hasOverflow = maxScroll > 1;

        prevBtn.style.display = (!hasOverflow || strip.scrollLeft <= 1) ? "none" : "flex";
        nextBtn.style.display = (!hasOverflow || strip.scrollLeft >= maxScroll - 1) ? "none" : "flex";
    }

    prevBtn.addEventListener("click", function () {
        strip.scrollBy({ left: -scrollStep(), behavior: "smooth" });
    });

    nextBtn.addEventListener("click", function () {
        strip.scrollBy({ left: scrollStep(), behavior: "smooth" });
    });

    strip.addEventListener("scroll", updateArrows, { passive: true });
    window.addEventListener("resize", updateArrows);

    setTimeout(updateArrows, 100);
})();

// Xử lý chọn địa chỉ từ danh sách (Trang Đổi địa chỉ)
function selectAddress(btnElement) {
    // 1. Trích xuất dữ liệu động từ phần tử HTML vừa click
    var item = btnElement.closest('.address-picker-item');
    var titleEl = item.querySelector('.address-picker-title');
    var name = titleEl.querySelector('strong').innerText.trim();
    
    // Lấy số điện thoại (phần text đằng sau thẻ strong)
    var phoneText = titleEl.innerText.replace(name, '').replace('-', '').trim();
    var address = item.querySelector('.address-picker-desc').innerText.trim();

    // 2. Điền thông tin vào cột bên trái
    document.getElementById('delivery-name').value = name;
    document.getElementById('delivery-phone').value = phoneText;
    document.getElementById('delivery-address').value = address;

    // 2. Mở khóa nút Xác nhận
    var confirmBtn = document.getElementById('confirm-address-btn');
    if (confirmBtn) {
        confirmBtn.disabled = false;
        confirmBtn.style.opacity = '1';
        confirmBtn.style.cursor = 'pointer';
        confirmBtn.textContent = 'Xác nhận địa chỉ';
    }
}

// Xử lý chuyển đổi phương thức thanh toán (Trang Thanh toán)
document.addEventListener("DOMContentLoaded", function() {
    var paymentRadios = document.querySelectorAll('input[name="payment-method"]');
    if (paymentRadios.length > 0) {
        paymentRadios.forEach(function(radio) {
            radio.addEventListener('change', function() {
                // Ẩn tất cả các view
                var views = document.querySelectorAll('.payment-view');
                views.forEach(function(view) {
                    view.style.display = 'none';
                });

                // Hiện view tương ứng
                var targetId = 'view-' + this.value;
                var targetView = document.getElementById(targetId);
                if (targetView) {
                    targetView.style.display = 'block';
                }
            });
        });
    }

    // Định dạng số thẻ tín dụng tự động (Tùy chọn)
    var ccInput = document.getElementById('cc-number');
    if (ccInput) {
        ccInput.addEventListener('input', function(e) {
            var target = e.target;
            var position = target.selectionEnd;
            var length = target.value.length;
            
            // Xóa tất cả khoảng trắng và ký tự không phải số
            target.value = target.value.replace(/[^\d]/g, '').replace(/(.{4})/g, '$1 ').trim();
            
            // Khôi phục con trỏ nếu không ở cuối
            if (position !== length) {
                target.setSelectionRange(position, position);
            }
        });
    }

    // Xử lý Hủy đơn hàng (Trang Cancel)
    const cancelForm = document.getElementById('cancelOrderForm');
    if (cancelForm) {
        const otherReasonRadio = document.getElementById('otherReasonRadio');
        const otherReasonContainer = document.getElementById('otherReasonContainer');
        const reasonRadios = document.querySelectorAll('input[name="reason"]');
        const otherTextarea = otherReasonContainer.querySelector('textarea');

        function toggleOtherReason() {
            if (otherReasonRadio.checked) {
                otherReasonContainer.style.display = 'block';
                // Focus vào textarea khi hiện
                if (otherTextarea) otherTextarea.focus();
            } else {
                otherReasonContainer.style.display = 'none';
            }
        }

        reasonRadios.forEach(radio => {
            radio.addEventListener('change', toggleOtherReason);
        });

        // Chạy lần đầu để kiểm tra trạng thái mặc định
        toggleOtherReason();

        // Auto-expand textarea
        if (otherTextarea) {
            otherTextarea.addEventListener('input', function() {
                this.style.height = 'auto';
                this.style.height = (this.scrollHeight) + 'px';
            });
        }

        cancelForm.addEventListener('submit', function(e) {
            console.log('Đang kiểm tra trạng thái đơn hàng và gửi yêu cầu hủy...');
        });
    }
});

document.addEventListener('DOMContentLoaded', function() {
    const qtyInput = document.querySelector('[data-product-qty]');
    const qtyHidden = document.getElementById('product-qty-input');
    const minusBtn = document.querySelector('[data-action="product-qty-minus"]');
    const plusBtn = document.querySelector('[data-action="product-qty-plus"]');

    if (qtyInput && qtyHidden && minusBtn && plusBtn) {
        function setQty(value) {
            const qty = Math.max(1, value);
            qtyInput.value = qty;
            qtyHidden.value = qty;
        }

        minusBtn.addEventListener('click', function() {
            setQty(Number(qtyInput.value) - 1);
        });

        plusBtn.addEventListener('click', function() {
            setQty(Number(qtyInput.value) + 1);
        });

        setQty(Number(qtyInput.value));
    }

    const menuSearch = document.querySelector('[data-menu-search]');
    const menuSort = document.querySelector('[data-menu-sort]');
    const menuGrid = document.getElementById('menu-grid');

    if (menuGrid && menuSearch && menuSort) {
        const menuItems = Array.from(menuGrid.querySelectorAll('[data-product-id]'));

        function filterMenu() {
            const query = menuSearch.value.trim().toLowerCase();
            menuItems.forEach(item => {
                const name = item.dataset.productName.toLowerCase();
                const category = item.dataset.productCategory.toLowerCase();
                const description = item.querySelector('.muted')?.textContent.toLowerCase() || '';
                const matches = name.includes(query) || category.includes(query) || description.includes(query);
                item.style.display = matches ? '' : 'none';
            });
        }

        function sortMenu() {
            const value = menuSort.value;
            const sorted = menuItems.slice().sort((a, b) => {
                const priceA = parseFloat(a.dataset.productPrice || '0');
                const priceB = parseFloat(b.dataset.productPrice || '0');
                const ratingA = parseFloat(a.dataset.productRating || '0');
                const ratingB = parseFloat(b.dataset.productRating || '0');

                if (value === 'price-low') return priceA - priceB;
                if (value === 'price-high') return priceB - priceA;
                if (value === 'rating') return ratingB - ratingA;
                return 0;
            });

            sorted.forEach(item => menuGrid.appendChild(item));
        }

        menuSearch.addEventListener('input', filterMenu);
        menuSort.addEventListener('change', sortMenu);
    }
});

document.addEventListener('DOMContentLoaded', function() {
    const checkoutItemsContainer = document.getElementById('order-items');
    const invoiceSubtotal = document.getElementById('invoice-subtotal');
    const invoiceDeliveryFee = document.getElementById('invoice-delivery-fee');
    const invoiceDiscount = document.getElementById('invoice-discount');
    const invoiceTotal = document.getElementById('invoice-total');
    const voucherInput = document.getElementById('voucher-code');
    const voucherButton = document.getElementById('voucher-apply');
    const voucherMessage = document.getElementById('voucher-message');

    if (!checkoutItemsContainer || !invoiceSubtotal || !invoiceDeliveryFee || !invoiceDiscount || !invoiceTotal || !voucherInput || !voucherButton) {
        return;
    }

    const STORAGE_KEY = 'jollibug_cart';
    const COUPON_KEY = 'jollibug_coupon';
    const DELIVERY_FEE = 0;

    function readCart() {
        try {
            const value = localStorage.getItem(STORAGE_KEY);
            return value ? JSON.parse(value) : [];
        } catch (error) {
            return [];
        }
    }

    function readStoredCoupon() {
        try {
            const raw = localStorage.getItem(COUPON_KEY);
            return raw ? JSON.parse(raw) : null;
        } catch (error) {
            return null;
        }
    }

    function saveStoredCoupon(coupon) {
        if (coupon) {
            localStorage.setItem(COUPON_KEY, JSON.stringify(coupon));
        } else {
            localStorage.removeItem(COUPON_KEY);
        }
    }

    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    }

    function getCartTotals(cart) {
        const subtotal = cart.reduce((sum, item) => sum + (Number(item.price || 0) * Number(item.quantity || 0)), 0);
        const totalItems = cart.reduce((sum, item) => sum + Number(item.quantity || 0), 0);
        return { subtotal, totalItems };
    }

    function renderCartLines(cart) {
        if (!cart || cart.length === 0) {
            checkoutItemsContainer.innerHTML = '<div class="invoice-line"><span>Giỏ hàng trống.</span></div>';
            return;
        }

        checkoutItemsContainer.innerHTML = cart.map(item => {
            const lineTotal = Number(item.price || 0) * Number(item.quantity || 0);
            return `<div class="invoice-line"><strong>${item.quantity}x ${item.name}</strong><strong>${formatCurrency(lineTotal)}</strong></div>`;
        }).join('');
    }

    function updateSummary(subtotal, discountAmount) {
        const deliveryFee = DELIVERY_FEE;
        const total = Math.max(0, subtotal + deliveryFee - discountAmount);

        invoiceSubtotal.textContent = formatCurrency(subtotal);
        invoiceDeliveryFee.textContent = formatCurrency(deliveryFee);
        invoiceDiscount.textContent = formatCurrency(discountAmount || 0);
        invoiceTotal.textContent = formatCurrency(total);
    }

    function showVoucherMessage(message, isSuccess) {
        if (!voucherMessage) return;
        voucherMessage.textContent = message;
        voucherMessage.style.color = isSuccess ? '#166534' : '#b91c1c';
    }

    async function validateVoucher(code, subtotal) {
        const params = new URLSearchParams({ code: code.trim(), subtotal: String(subtotal) });
        try {
            const response = await fetch('/api/voucher/validate?' + params.toString());
            if (!response.ok) {
                throw new Error('Lỗi khi kiểm tra mã giảm giá');
            }
            return await response.json();
        } catch (error) {
            return { valid: false, message: 'Không thể kết nối đến máy chủ để kiểm tra mã giảm giá.' };
        }
    }

    async function applyCoupon(code, subtotal) {
        if (!code || !code.trim()) {
            showVoucherMessage('Vui lòng nhập mã giảm giá.', false);
            saveStoredCoupon(null);
            updateSummary(subtotal, 0);
            return;
        }

        const result = await validateVoucher(code, subtotal);
        if (!result.valid) {
            showVoucherMessage(result.message || 'Mã giảm giá không hợp lệ.', false);
            saveStoredCoupon(null);
            updateSummary(subtotal, 0);
            return;
        }

        showVoucherMessage(`Áp dụng mã ${code.trim().toUpperCase()} thành công. Tiết kiệm ${formatCurrency(result.discountAmount)}.`, true);
        saveStoredCoupon({ code: code.trim().toUpperCase(), discountAmount: result.discountAmount });
        updateSummary(subtotal, Number(result.discountAmount || 0));
    }

    async function refreshCheckout() {
        const cart = readCart();
        const { subtotal } = getCartTotals(cart);
        const coupon = readStoredCoupon();

        renderCartLines(cart);

        if (coupon && coupon.code) {
            voucherInput.value = coupon.code;
            const result = await validateVoucher(coupon.code, subtotal);
            if (result.valid) {
                saveStoredCoupon({ code: coupon.code, discountAmount: result.discountAmount });
                showVoucherMessage(`Mã ${coupon.code} vẫn hợp lệ. Tiết kiệm ${formatCurrency(result.discountAmount)}.`, true);
                updateSummary(subtotal, Number(result.discountAmount || 0));
                return;
            }
            saveStoredCoupon(null);
            voucherInput.value = '';
            showVoucherMessage(result.message || 'Mã giảm giá hiện không còn hợp lệ.', false);
        }

        updateSummary(subtotal, 0);
    }

    voucherButton.addEventListener('click', async function() {
        const cart = readCart();
        const { subtotal } = getCartTotals(cart);
        await applyCoupon(voucherInput.value, subtotal);
    });

    refreshCheckout();
});
