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
              <span>Địa chỉ giao hàng</span>
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

        <!-- Cột phải: danh sách địa chỉ -->
        <section class="checkout-card">
          <h2 class="checkout-card__title">Danh sách địa chỉ</h2>

          <div class="address-picker-list">
            <c:choose>
              <c:when test="${empty listAddress}">
                <div class="empty-state">
                  <p>Bạn chưa có địa chỉ giao hàng nào.</p>

                  <a href="/address/create" class="btn btn-primary">
                    Thêm địa chỉ mới
                  </a>

                  <a href="/checkout" class="btn btn-outline" style="margin-left: 8px;">
                    Quay lại
                  </a>
                </div>
              </c:when>

              <c:otherwise>
                <c:forEach var="address" items="${listAddress}">
                  <c:set
                    var="fullAddress"
                    value="${address.diaChiCuThe}, ${address.phuongXa}, ${address.quanHuyen}, ${address.tinhThanh}"
                  />

                  <div
                    class="address-picker-item"
                    data-madc="${address.maDC}"
                    data-name="${address.tenNguoiNhan}"
                    data-phone="${address.sdtNguoiNhan}"
                    data-email="${checkoutUser.email}"
                    data-address="${fullAddress}"
                  >
                    <div class="address-picker-info">
                      <p class="address-picker-title">
                        <strong>${address.tenNguoiNhan}</strong>
                        -
                        ${address.sdtNguoiNhan}

                        <c:if test="${address.defaultAddress}">
                          <span class="badge">Mặc định</span>
                        </c:if>
                      </p>

                      <p class="address-picker-desc">
                        <c:if test="${not empty address.tenDiaChi}">
                          <strong>${address.tenDiaChi}:</strong>
                        </c:if>
                        ${fullAddress}
                      </p>
                    </div>

                    <button
                      type="button"
                      class="btn btn-primary address-picker-btn"
                      onclick="selectAddress(this)"
                    >
                      Chọn
                    </button>
                  </div>
                </c:forEach>
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
        name: item.dataset.name || "",
        phone: item.dataset.phone || "",
        email: item.dataset.email || "",
        address: item.dataset.address || ""
      };

      localStorage.setItem("selectedCheckoutAddress", JSON.stringify(selectedAddress));

      fillSelectedAddressPreview(selectedAddress);

      const confirmButton = document.getElementById("confirm-address-btn");
      if (confirmButton) {
        confirmButton.disabled = false;
        confirmButton.style.opacity = "1";
        confirmButton.style.cursor = "pointer";
        confirmButton.textContent = "Xác nhận địa chỉ";
      }
    }

    function restoreSelectedAddressPreview() {
      const selectedAddressRaw = localStorage.getItem("selectedCheckoutAddress");

      if (!selectedAddressRaw) {
        return;
      }

      try {
        const selectedAddress = JSON.parse(selectedAddressRaw);

        const selector = '.address-picker-item[data-madc="' + selectedAddress.maDC + '"]';
        const existingAddressItem = document.querySelector(selector);

        if (!existingAddressItem) {
          localStorage.removeItem("selectedCheckoutAddress");
          return;
        }

        fillSelectedAddressPreview(selectedAddress);

        const confirmButton = document.getElementById("confirm-address-btn");
        if (confirmButton && selectedAddress.maDC) {
          confirmButton.disabled = false;
          confirmButton.style.opacity = "1";
          confirmButton.style.cursor = "pointer";
          confirmButton.textContent = "Xác nhận địa chỉ";
        }
      } catch (error) {
        localStorage.removeItem("selectedCheckoutAddress");
      }
    }

    function fillSelectedAddressPreview(selectedAddress) {
      const nameInput = document.getElementById("delivery-name");
      const phoneInput = document.getElementById("delivery-phone");
      const emailInput = document.getElementById("delivery-email");
      const addressInput = document.getElementById("delivery-address");

      if (nameInput) {
        nameInput.value = selectedAddress.name || "";
      }

      if (phoneInput) {
        phoneInput.value = selectedAddress.phone || "";
      }

      if (emailInput) {
        emailInput.value = selectedAddress.email || "";
      }

      if (addressInput) {
        addressInput.value = selectedAddress.address || "";
      }
    }

    function goBackToCheckout() {
      const selectedAddressRaw = localStorage.getItem("selectedCheckoutAddress");

      if (!selectedAddressRaw) {
        alert("Vui lòng chọn địa chỉ giao hàng.");
        return;
      }

      window.location.href = "/checkout";
    }
  </script>
</body>
</html>