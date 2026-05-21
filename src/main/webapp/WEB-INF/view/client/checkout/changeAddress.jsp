<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />

  <title>Địa chỉ giao hàng</title>

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link
    href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap"
    rel="stylesheet"
  />

  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/client/client-checkout.css" />
</head>

<body class="checkout-page" data-page="checkout">
  <jsp:include page="../layout/header.jsp" />

  <main class="page-shell checkout-main">
    <div class="container">
      <div class="page-intro">
        <h1 class="section-title">Chọn địa chỉ giao hàng</h1>
        <p class="lead">Vui lòng chọn một địa chỉ đã lưu để tiếp tục đặt hàng.</p>
      </div>

      <div class="checkout-layout">

        <!-- Cột trái: địa chỉ đang chọn -->
        <section class="checkout-card checkout-sticky">
          <h2 class="checkout-card__title">Địa chỉ đang chọn</h2>

          <form class="delivery-form" id="delivery-form">
            <label class="field-label">
              <span>Họ tên</span>
              <input type="text" id="delivery-name" disabled />
            </label>

            <label class="field-label">
              <span>Số điện thoại</span>
              <input type="tel" id="delivery-phone" disabled />
            </label>

            <label class="field-label">
              <span>Email</span>
              <input type="text" id="delivery-email" disabled />
            </label>

            <label class="field-label">
              <span>Địa chỉ đặt hàng</span>
              <input type="text" id="delivery-address" disabled />
            </label>
          </form>

          <button
            id="confirm-address-btn"
            class="btn btn-primary btn-block"
            type="button"
            disabled
            style="opacity: 0.5; cursor: not-allowed;"
            onclick="goBackToCheckout()"
          >
            Vui lòng chọn địa chỉ
          </button>
        </section>

        <section class="checkout-card">
          <h2 class="checkout-card__title">Chọn địa chỉ giao hàng</h2>

          <div class="address-picker-list">
            <c:choose>
              <c:when test="${not empty addresses}">
                <c:forEach var="address" items="${addresses}">
                  <div class="address-picker-item"
                      data-madc="${address.maDC}"
                      data-name="${address.tenNguoiNhan}"
                      data-phone="${address.sdtNguoiNhan}"
                      data-email="${currentUser != null ? currentUser.email : ''}"
                      data-address="${address.diaChiCuThe}, ${address.phuongXa}, ${address.quanHuyen}, ${address.tinhThanh}">
                    <div class="address-picker-info">
                      <p class="address-picker-title">
                        <strong>${address.tenNguoiNhan}</strong> - ${address.sdtNguoiNhan}
                        <c:if test="${address.defaultAddress}">
                          <span>(Mặc định)</span>
                        </c:if>
                      </p>
                      <p class="address-picker-desc">
                        <c:if test="${not empty address.tenDiaChi}">
                          ${address.tenDiaChi}:
                        </c:if>
                        ${address.diaChiCuThe}, ${address.phuongXa}, ${address.quanHuyen}, ${address.tinhThanh}
                      </p>
                    </div>
                    <button type="button" class="btn btn-primary address-picker-btn" onclick="selectAddress(this)">Chọn</button>
                  </div>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <div class="address-picker-item">
                  <div class="address-picker-info">
                    <p class="address-picker-title"><strong>Chưa có địa chỉ nào</strong></p>
                    <p class="address-picker-desc">Hãy thêm địa chỉ trong tài khoản trước khi đặt hàng.</p>
                  </div>
                  <a href="/address/create" class="btn btn-primary address-picker-btn">Thêm địa chỉ</a>
                </div>
              </c:otherwise>
            </c:choose>
          </div>

          <div class="address-picker-actions">
            <a href="/checkout" class="btn btn-secondary">Hủy bỏ</a>
            <a href="/address/create" class="btn btn-outline">Thêm địa chỉ mới</a>
          </div>
        </section>

      </div>
    </div>
  </main>

  <jsp:include page="../layout/footer.jsp" />

  <script src="/js/client/main.js"></script>

  <script>
    document.addEventListener("DOMContentLoaded", function () {
      restoreSelectedAddressPreview();
    });

    function selectAddress(button) {
      const item = button.closest(".address-picker-item");

      if (!item) {
        return;
      }

      const selectedAddress = {
        maDC: Number(item.dataset.madc),
        name: item.dataset.name,
        phone: item.dataset.phone,
        email: item.dataset.email,
        address: item.dataset.address
      };

      localStorage.setItem("selectedCheckoutAddress", JSON.stringify(selectedAddress));
      window.location.href = "/checkout";
    }

    function restoreSelectedAddressPreview() {
      const selectedAddressRaw = localStorage.getItem("selectedCheckoutAddress");
      const confirmButton = document.getElementById("confirm-address-btn");

      if (!selectedAddressRaw) {
        return;
      }

      try {
        const selectedAddress = JSON.parse(selectedAddressRaw);

        document.getElementById("delivery-name").value = selectedAddress.name || "";
        document.getElementById("delivery-phone").value = selectedAddress.phone || "";
        document.getElementById("delivery-email").value = selectedAddress.email || "";
        document.getElementById("delivery-address").value = selectedAddress.address || "";

        if (confirmButton) {
          confirmButton.disabled = false;
          confirmButton.style.opacity = "1";
          confirmButton.style.cursor = "pointer";
          confirmButton.textContent = "Xác nhận địa chỉ này";
        }
      } catch (error) {
        localStorage.removeItem("selectedCheckoutAddress");
      }
    }

    function goBackToCheckout() {
      window.location.href = "/checkout";
    }
  </script>
</body>
</html>
