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

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/client-checkout.css" />
</head>
<body class="checkout-page" data-page="checkout">
  <jsp:include page="layout/header.jsp"/>

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
              <input type="text" id="delivery-name" required />
            </label>

            <label class="field-label">
              <span>Số điện thoại</span>
              <input type="tel" id="delivery-phone" required />
            </label>

            <label class="field-label">
              <span>Địa chỉ email</span>
              <input type="tel" id="delivery-phone" required />
            </label>

            <label class="field-label">
              <span>Địa chỉ đặt hàng</span>
              <textarea id="delivery-address" required></textarea>
            </label>
          </form>

          <button class="btn btn-outline" type="button" id="btn-open-address-modal">Đổi địa chỉ</button>

          <section>
            <h3 class="section-subtitle">Phương thức thanh toán</h3>
            <div class="payment-options" role="radiogroup" aria-label="Payment method">
              <label class="payment-option">
                <input type="radio" name="payment-method" value="cod" checked />
                <div>
                  <strong>Thanh toán khi nhận háng (COD)</strong>
                </div>
              </label>

              <label class="payment-option">
                <input type="radio" name="payment-method" value="online" />
                <div>
                  <strong>Thanh toán bằng ATM/ Ví điện tử</strong>
                </div>
              </label>
            </div>
          </section>

          <button class="btn btn-primary btn-block" type="button" id="btn-place-order">Đặt hàng</button>
        </section>
        <section class="checkout-card">
          <h2 class="checkout-card__title">Tóm tắt đơn hàng</h2>
          <div>
            <div class="invoice-line ">
              <strong>1xGa</strong>
              <strong>0 VND</strong>
            </div>
            <div class="invoice-line ">
              <strong>2xHamburger</strong>
              <strong>0 VND</strong>
            </div>
            <div class="invoice-line ">
              <strong>10xPizza</strong>
              <strong>0 VND</strong>
            </div>
          </div>

          <hr class="checkout-divider" />

          <div>
            <div class="invoice-line "><span>Subtotal</span><strong id="invoice-subtotal">0 VND</strong></div>
            <div class="invoice-line"><span>Delivery Fee</span><strong id="invoice-delivery-fee">0 VND</strong></div>
            <div class="invoice-line"><span>Discount / Voucher</span><strong id="invoice-discount">0 VND</strong></div>
            <div class="invoice-line "><span>Total</span><strong id="invoice-total">0 VND</strong></div>
          </div>
        
        </section>

        
      </div>
    </div>
  </main>

  <div class="address-modal" id="address-modal" aria-hidden="true">
    <div class="address-modal__panel" role="dialog" aria-modal="true" aria-labelledby="address-modal-title">
      <div class="address-modal__header">
        <h2 id="address-modal-title">Choose delivery address</h2>
        <button class="btn btn-outline" type="button" id="btn-close-address-modal">Close</button>
      </div>
      <div class="address-list" id="address-list"></div>
    </div>
  </div>

  <div class="success-modal" id="success-modal" aria-hidden="true">
    <div class="success-modal__panel" role="dialog" aria-modal="true" aria-labelledby="success-modal-title">
      <h2 id="success-modal-title">Order placed successfully</h2>
      <p class="muted">Your order code: <strong id="success-order-code"></strong></p>
      <div class="cluster success-modal__actions">
        <a class="btn btn-primary" href="/orders">Track Order</a>
        <a class="btn btn-outline" href="/menu">Continue Shopping</a>
      </div>
    </div>
  </div>

  <script src="js/client/nav.js" defer></script>
  <script src="js/client/checkout.js" defer></script>
</body>
</html>

