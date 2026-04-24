<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Cart</title>
  <meta name="description" content="Jollibug cart page: update item quantity and proceed to checkout." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
</head>
<body class="cart-page" data-page="cart">
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell section-tight">
    <div class="container">
      <div class="page-intro">
        <span class="eyebrow">Cart</span>
        <h1 class="page-title">My Cart</h1>
        <p class="lead">Review your selected meals before placing the order.</p>
      </div>

      <div class="cart-shell">
        <section class="cart-column">
          <article class="cart-panel">
            <h2 class="cart-panel__title">My Cart</h2>
            <div class="cart-item-list" id="cart-item-list" aria-live="polite"></div>
            <div class="empty-state hidden" id="cart-empty-state">
              <h3>Your cart is empty</h3>
              <p class="muted">Add delicious items from the menu to continue.</p>
              <a class="btn btn-primary" href="/menu">Browse Menu</a>
            </div>
          </article>
        </section>

        <aside class="summary-column">
          <article class="summary-panel">
            <h2 class="summary-panel__title">Order Summary</h2>
            <p class="summary-count" id="summary-item-count">0 ITEMS</p>

            <div class="voucher-inline">
              <p class="section-subtitle">Have a promo code?</p>
              <div class="voucher-inline__row">
                <input id="voucher-code" type="text" placeholder="Enter promo code" />
                <button class="btn btn-outline voucher-inline__apply" id="voucher-apply" type="button">Apply</button>
              </div>
              <p class="summary-note" id="voucher-note"></p>
            </div>

            <div class="summary-lines">
              <div class="summary-line"><span>Subtotal</span><strong id="summary-subtotal">0 VND</strong></div>
              <div class="summary-line summary-line--strong"><span>Total</span><strong id="summary-total">0 VND</strong></div>
            </div>

            <button class="btn btn-primary btn-block" type="button" id="checkout-button">Checkout</button>
          </article>
        </aside>
      </div>
    </div>
  </main>

  <script src="js/client/nav.js" defer></script>
  <script src="js/client/cart.js" defer></script>
</body>
</html>

