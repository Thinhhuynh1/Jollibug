<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Contact</title>
  <meta name="description" content="Contact the Jollibug team - reach us by email, hotline, or visit our flagship store." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/global.css" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/components.css" />
    <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>
<body data-page="contact">

  <!-- SHARED HEADER -->
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">
        <div class="page-intro reveal-up">
          <span class="eyebrow">Contact Jollibug</span>
          <h1 class="page-title">Talk to the team behind the flavor.</h1>
          <p class="lead">The contact page pairs a structured form with store details and a map-style visual block, giving the website a more credible real-world presence.</p>
        </div>

        <div class="contact-grid">
          <!-- SECTION -->
          <article class="contact-panel reveal-up">
            <div class="contact-info">
              <article class="contact-info__item">
                <strong>Customer Care</strong>
                <p class="muted">hello@Jollibug.vn</p>
              </article>
              <article class="contact-info__item">
                <strong>Hotline</strong>
                <p class="muted">+84 28 5555 8899</p>
              </article>
              <article class="contact-info__item">
                <strong>Main Flagship</strong>
                <p class="muted">88 Flavor Avenue, District 1, Ho Chi Minh City</p>
              </article>
            </div>
            <div class="contact-map">Store Map</div>
          </article>

          <!-- SECTION
          -->
          <form class="contact-panel form-grid reveal-up"
                data-demo-form
                data-success-message="Thanks! The Jollibug team will reach out shortly."
                data-reset-form="true"
                id="contact-form" novalidate>
            <label class="field-label">
              <span>Full name</span>
              <input type="text" name="name" id="contact-name" placeholder="Your full name" required />
            </label>
            <label class="field-label">
              <span>Email</span>
              <input type="email" name="email" id="contact-email" placeholder="you@example.com" required />
            </label>
            <label class="field-label">
              <span>Phone</span>
              <input type="tel" name="phone" id="contact-phone" placeholder="+84 ..." required />
            </label>
            <label class="field-label">
              <span>How can we help?</span>
              <textarea name="message" id="contact-message" placeholder="Tell us about your request" required></textarea>
            </label>
            <button class="btn btn-primary" type="submit">Send message</button>
          </form>
        </div>
      </div>
    </section>
  </main>

  <!-- SHARED FOOTER -->
  <footer class="site-footer" id="site-footer">
    <div class="container">
      <div class="footer-grid">
        <div class="stack"><div class="brand"><span class="brand__mark">JB</span><span class="brand__copy"><span class="brand__title">Jollibug</span><span class="brand__tag">Crave-worthy comfort, delivered fast</span></span></div><p>Ready for Spring MVC storefront.</p></div>
        <div><h3>Explore</h3><div class="footer-links"><a href="/menu">Menu</a><a href="/about">About</a></div></div>
        <div><h3>Support</h3><div class="footer-links"><a href="/cart">Cart</a><a href="/login">Sign In</a></div></div>
        <div><h3>Contact</h3><div class="footer-links"><span>88 Flavor Avenue, Ho Chi Minh City</span><span>hello@Jollibug.vn</span><span>+84 28 5555 8899</span></div></div>
      </div>
      <div class="footer-note"><span>&copy; <span data-current-year id="footer-year"></span> Jollibug.</span></div>
    </div>
  </footer>

  <div class="drawer-backdrop" data-drawer-backdrop id="drawer-backdrop"></div>
  <aside class="cart-drawer" data-cart-drawer id="cart-drawer" aria-label="Shopping cart drawer">
    <div class="cart-drawer__header"><div class="card-actions"><div class="stack"><strong>Your order</strong><span class="muted">Fresh picks, saved instantly.</span></div><button class="btn btn-outline" type="button" data-action="close-cart">Close</button></div></div>
    <div class="cart-drawer__body" data-drawer-items id="drawer-items"></div>
    <div class="cart-drawer__footer stack"><div class="summary-line"><span>Total</span><strong data-drawer-total id="drawer-total">$0.00</strong></div><div class="cluster"><a class="btn btn-outline" href="/cart">Full Cart</a><button class="btn btn-primary" type="button" data-action="checkout-demo">Checkout Demo</button></div></div>
  </aside>
  <div class="toast-stack" data-toast-stack id="toast-stack"></div>
  <template id="mini-cart-item-template"><article class="mini-cart-item"><div class="mini-cart-item__media" style="background:rgba(255,248,238,0.92);"><img data-item-image alt="" /></div><div class="stack" style="gap:0.2rem;"><strong data-item-name></strong><span class="muted" data-item-price-qty></span></div><button class="btn btn-outline" type="button" data-action="remove-cart-item" data-cart-id="">Remove</button></article></template>
  <template id="cart-drawer-empty-template"><div class="empty-state"><h3>Your cart is still warming up.</h3><p class="muted">Add a burger, combo, or drink.</p><a class="btn btn-secondary" href="/menu">Browse menu</a></div></template>
<script src="js/client/nav.js" defer></script>
  <script src="js/client/store.js"></script>
<script src="js/client/main.js" defer></script>
  </body>
</html>





