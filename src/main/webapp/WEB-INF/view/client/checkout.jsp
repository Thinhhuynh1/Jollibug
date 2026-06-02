<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Thanh toán</title>
  <meta name="description" content="Trang thanh toán Jollibug: xem lại đơn hàng, chọn địa chỉ giao hàng, áp dụng mã giảm giá và hoàn tất đặt món." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/client-checkout.css" />
</head>

<body class="checkout-page" data-page="checkout">
  <jsp:include page="../layout/header.jsp"/>

  <c:if test="${not empty sessionScope.user}">
    <input type="hidden" id="maKH" value="${sessionScope.user.maTK}" />
  </c:if>

  <input type="hidden" id="addressSelect" value="" />

  <main class="page-shell checkout-main">
    <div class="container">
      <div class="page-intro">
        <span class="eyebrow">Thanh toán</span>
        <h1 class="section-title">Xác nhận đơn hàng</h1>
        <p class="lead">Kiểm tra lại món đã chọn, cập nhật thông tin giao hàng và hoàn tất đặt món.</p>
      </div>

      <div class="checkout-layout">
        <section class="checkout-card">
          <h2 class="checkout-card__title">Chi tiết đơn hàng</h2>

          <div class="order-list" id="order-list" aria-live="polite"></div>

          <div class="voucher-box">
            <p class="section-subtitle">Mã giảm giá</p>
            <div class="voucher-box__row">
              <input type="text" id="voucher-input" placeholder="Nhập mã giảm giá, ví dụ GIAM20" />
              <button class="btn btn-outline" type="button" id="btn-apply-voucher">Áp dụng</button>
            </div>
            <p class="voucher-message" id="voucher-message"></p>
          </div>

          <section class="invoice-box" aria-label="Tóm tắt hóa đơn">
            <p class="section-subtitle">Tóm tắt thanh toán</p>
            <div class="invoice-line"><span>Tạm tính</span><strong id="invoice-subtotal">0 VND</strong></div>
            <div class="invoice-line"><span>Phí giao hàng</span><strong id="invoice-delivery-fee">0 VND</strong></div>
            <div class="invoice-line"><span>Giảm giá / Voucher</span><strong id="invoice-discount">0 VND</strong></div>
            <div class="invoice-line invoice-line--total"><span>Tổng cộng</span><strong id="invoice-total">0 VND</strong></div>
          </section>
        </section>

        <section class="checkout-card checkout-sticky">
          <h2 class="checkout-card__title">Thông tin giao hàng</h2>
          <form class="delivery-form" id="delivery-form">
            <label class="field-label">
              <span>Họ và tên</span>
              <input type="text" id="delivery-name" required />
            </label>

            <label class="field-label">
              <span>Số điện thoại</span>
              <input type="tel" id="delivery-phone" required />
            </label>

            <label class="field-label">
              <span>Địa chỉ giao hàng</span>
              <textarea id="delivery-address" required></textarea>
            </label>
          </form>

          <button class="btn btn-outline" type="button" id="btn-open-address-modal">Chọn địa chỉ khác</button>

          <section>
            <h3 class="section-subtitle">Phương thức thanh toán</h3>
            <div class="payment-options" role="radiogroup" aria-label="Phương thức thanh toán">
              <label class="payment-option">
                <input type="radio" name="payment-method" value="COD" checked />
                <div>
                  <strong>Thanh toán khi nhận hàng (COD)</strong>
                </div>
              </label>

              <label class="payment-option">
                <input type="radio" name="payment-method" value="EWALLET" />
                <div>
                  <strong>Thẻ / Ví điện tử</strong>
                </div>
              </label>
            </div>
          </section>

          <button class="btn btn-primary btn-block" type="button" id="btn-place-order">Đặt hàng</button>
        </section>
      </div>
    </div>
  </main>

  <div class="address-modal" id="address-modal" aria-hidden="true">
    <div class="address-modal__panel" role="dialog" aria-modal="true" aria-labelledby="address-modal-title">
      <div class="address-modal__header">
        <h2 id="address-modal-title">Chọn địa chỉ giao hàng</h2>
        <button class="btn btn-outline" type="button" id="btn-close-address-modal">Đóng</button>
      </div>
      <div class="address-list" id="address-list"></div>
    </div>
  </div>

  <div class="success-modal" id="success-modal" aria-hidden="true">
    <div class="success-modal__panel" role="dialog" aria-modal="true" aria-labelledby="success-modal-title">
      <h2 id="success-modal-title">Đặt hàng thành công</h2>
      <p class="muted">Mã đơn hàng của bạn: <strong id="success-order-code"></strong></p>
      <div class="cluster success-modal__actions">
        <a class="btn btn-primary" href="/orders">Theo dõi đơn hàng</a>
        <a class="btn btn-outline" href="/menu">Tiếp tục mua sắm</a>
      </div>
    </div>
  </div>

  <script src="js/client/nav.js" defer></script>
  <script src="js/client/checkout.js" defer></script>
</body>
</html>
