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
