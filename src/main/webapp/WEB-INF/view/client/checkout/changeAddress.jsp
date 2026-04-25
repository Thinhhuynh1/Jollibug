<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Checkout</title>
  <meta name="description" content="Jollibug checkout page: review order details, choose delivery address, apply voucher, and place your order." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/client-checkout.css" />
</head>
<body class="checkout-page" data-page="checkout">
  <jsp:include page="../layout/header.jsp"/>

  <main class="page-shell checkout-main">
    <div class="container">
      <div class="page-intro">
        <h1 class="section-title">Thông tin đặt hàng</h1>
      </div>

      <div class="checkout-layout">

        <section class="checkout-card checkout-sticky">
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
              <span>Địa chỉ email</span>
              <input type="text" id="delivery-email" disabled />
            </label>

            <label class="field-label">
              <span>Địa chỉ đặt hàng</span>
              <input type="text" id="delivery-address" disabled />                
            </label>
          </form>

          <div style="text-align: end;">
            <button class="btn btn-outline" type="button" disabled style="opacity: 0.5; cursor: not-allowed;">
              Đổi địa chỉ
            </button>
          </div>

          <button id="confirm-address-btn" class="btn btn-primary btn-block" type="button" disabled style="opacity: 0.5; cursor: not-allowed;" onclick="window.location.href='/checkout'">Vui lòng chọn địa chỉ</button>
        </section>
        <section class="checkout-card">
          <h2 class="checkout-card__title">Chọn địa chỉ giao hàng</h2>
          
          <div class="address-picker-list">
            <!-- Fake Address 1 -->
            <div class="address-picker-item">
                <div class="address-picker-info">
                    <p class="address-picker-title"><strong>Nguyễn Văn A</strong> - 0123456789</p>
                    <p class="address-picker-desc">123 Đường Số 1, Phường 2, Quận 3, TP.HCM</p>
                </div>
                <button type="button" class="btn btn-primary address-picker-btn" onclick="selectAddress(this)">Chọn</button>
            </div>

            <!-- Fake Address 2 -->
            <div class="address-picker-item">
                <div class="address-picker-info">
                    <p class="address-picker-title"><strong>Trần Thị B</strong> - 0987654321</p>
                    <p class="address-picker-desc">456 Đường Số 4, Phường 5, Quận 6, TP.HCM</p>
                </div>
                <button type="button" class="btn btn-primary address-picker-btn" onclick="selectAddress(this)">Chọn</button>
            </div>
            
            <!-- Fake Address 3 -->
            <div class="address-picker-item">
                <div class="address-picker-info">
                    <p class="address-picker-title"><strong>Lê Văn C</strong> - 0909090909</p>
                    <p class="address-picker-desc">789 Đường Láng, Đống Đa, Hà Nội</p>
                </div>
                <button type="button" class="btn btn-primary address-picker-btn" onclick="selectAddress(this)">Chọn</button>
            </div>
          </div>

          <div class="address-picker-actions">
            <a href="/checkout" class="btn btn-secondary">Hủy bỏ</a>
          </div>
        </section>
      </div>
    </div>
  </main>






    <!-- SHARED FOOTER -->
  <jsp:include page="../layout/footer.jsp" />

  <script src="/js/client/main.js"></script>
</body>
</html>

