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
        <span class="eyebrow">Checkout</span>
        <h1 class="section-title">Confirm Your Order</h1>
        <p class="lead">Review your selected items, update delivery details, and complete your order.</p>
      </div>

      <div class="checkout-layout">
        <section class="checkout-card">
          <h2 class="checkout-card__title">Order Details</h2>

          <div class="order-list" id="order-list" aria-live="polite"></div>

          <div class="voucher-box">
            <p class="section-subtitle">Voucher</p>
            <div class="voucher-box__row">
              <input type="text" id="voucher-input" placeholder="Enter voucher code (e.g. GIAM20)" />
              <button class="btn btn-outline" type="button" id="btn-apply-voucher">Apply</button>
            </div>
            <p class="voucher-message" id="voucher-message"></p>
          </div>

          <section class="invoice-box" aria-label="Invoice summary">
            <p class="section-subtitle">Payment Summary</p>
            <div class="invoice-line"><span>Subtotal</span><strong id="invoice-subtotal">0 VND</strong></div>
            <div class="invoice-line"><span>Delivery Fee</span><strong id="invoice-delivery-fee">0 VND</strong></div>
            <div class="invoice-line"><span>Discount / Voucher</span><strong id="invoice-discount">0 VND</strong></div>
            <div class="invoice-line invoice-line--total"><span>Total</span><strong id="invoice-total">0 VND</strong></div>
          </section>
        </section>

        <section class="checkout-card checkout-sticky">
          <h2 class="checkout-card__title">Delivery Information</h2>
          <form class="delivery-form" id="delivery-form">
            <label class="field-label">
              <span>Full Name</span>
              <input type="text" id="delivery-name" required />
            </label>

            <label class="field-label">
              <span>Phone Number</span>
              <input type="tel" id="delivery-phone" required />
            </label>

            <label class="field-label">
              <span>Delivery Address</span>
              <textarea id="delivery-address" required></textarea>
            </label>
          </form>

          <button class="btn btn-outline" type="button" id="btn-open-address-modal">Choose another address</button>

          <section>
            <h3 class="section-subtitle">Payment Method</h3>
            <div class="payment-options" role="radiogroup" aria-label="Payment method">
              <label class="payment-option">
                <input type="radio" name="payment-method" value="cod" checked />
                <div>
                  <strong>Cash on Delivery (COD)</strong>
                </div>
              </label>

              <label class="payment-option">
                <input type="radio" name="payment-method" value="online" />
                <div>
                  <strong>Card / E-wallet</strong>
                </div>
              </label>
            </div>
          </section>

          <button class="btn btn-primary btn-block" type="button" id="btn-place-order">Place Order</button>
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

