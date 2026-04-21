<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | About</title>
  <meta name="description" content="Learn about the Jollibug brand mission, design system, and frontend architecture ready for Spring MVC." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/global.css" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/components.css" />
    <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>
<body data-page="about">

  <!-- SHARED HEADER -->
  <jsp:include page="layout/header.jsp"/>

  <!-- SECTION -->
  <main class="page-shell">
    <section class="section">
      <div class="container">
        <div class="page-intro reveal-up">
          <span class="eyebrow">Our Story</span>
          <h1 class="page-title">Jollibug turns everyday comfort food into a polished brand moment.</h1>
          <p class="lead">This concept is built for the premium-fast-food lane: bold colors, round typography, clear hierarchy, and a frontend structure clean enough to evolve into a real ordering platform.</p>
        </div>
        <div class="story-grid">
          <article class="story-block reveal-up">
            <span class="eyebrow">Brand Mission</span>
            <h2 class="section-title">Serve joyful food with a smoother digital experience.</h2>
            <p class="lead">Jollibug is designed around appetite, warmth, and convenience. From browsing to cart to profile management, every step aims to feel quick, friendly, and reassuring.</p>
            <div class="story-stat-list">
              <article class="story-stat"><span class="muted">Brand direction</span><strong>Playful Premium</strong></article>
              <article class="story-stat"><span class="muted">Core promise</span><strong>Fast &amp; Fresh</strong></article>
              <article class="story-stat"><span class="muted">Audience</span><strong>Students + Families</strong></article>
            </div>
          </article>
          <article class="story-block story-highlight reveal-up">
            <img src="/webapp/resources/shared/images/brand-feast.svg" alt="Jollibug illustrated feast"
                 style="max-width:28rem;margin:auto;filter:drop-shadow(0 26px 36px rgba(86,48,17,0.18));" />
          </article>
        </div>
      </div>
    </section>
    <section class="section-tight">
      <div class="container">
        <div class="card-grid">
          <article class="card feature-card reveal-up">
            <div class="feature-card__icon">01</div>
            <h3>Natural visual hierarchy</h3>
            <p class="muted">Rounded headings and readable body copy help the site feel branded without sacrificing clarity.</p>
          </article>
          <article class="card feature-card reveal-up">
            <div class="feature-card__icon">02</div>
            <h3>Shared architecture</h3>
            <p class="muted">Global CSS tokens and modular JS keep every page consistent while staying easy to maintain.</p>
          </article>
          <article class="card feature-card reveal-up">
            <div class="feature-card__icon">03</div>
            <h3>Backend-ready scaffolding</h3>
            <p class="muted">The page markup uses semantic sections and data hooks that can map cleanly to dynamic rendering later.</p>
          </article>
        </div>
      </div>
    </section>
  </main>

  <!-- SHARED FOOTER -->
  <footer class="site-footer" id="site-footer">
    <div class="container">
      <div class="footer-grid">
        <div class="stack"><div class="brand"><span class="brand__mark">JB</span><span class="brand__copy"><span class="brand__title">Jollibug</span><span class="brand__tag">Crave-worthy comfort, delivered fast</span></span></div><p>Ready for Spring MVC storefront.</p></div>
        <div><h3>Explore</h3><div class="footer-links"><a href="/menu">Menu</a><a href="/about">About</a><a href="/contact">Contact</a></div></div>
        <div><h3>Support</h3><div class="footer-links"><a href="/cart">Cart</a><a href="/login">Sign In</a></div></div>
        <div><h3>Contact</h3><div class="footer-links"><span>hello@Jollibug.vn</span><span>+84 28 5555 8899</span></div></div>
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





