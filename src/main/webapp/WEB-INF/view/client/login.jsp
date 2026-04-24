<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Sign In</title>
  <meta name="description" content="Sign in to your Jollibug account to access saved addresses, order history, and faster reordering." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/global.css" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/components.css" />
    <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>
<body data-page="login">

  <!-- SHARED HEADER -->
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">
        

          <!-- SECTION -->


          <!-- Form panel -->
          <article class="auth-panel reveal-up">
            <div class="auth-panel__content">
              <div class="page-intro" style="margin-bottom:0;">
                <span class="eyebrow">Sign In</span>
                <h2 class="section-title">Access your Jollibug account</h2>
              </div>
              <div class="social-grid">
                <button class="btn btn-outline social-btn" type="button">Continue with Google</button>
                <button class="btn btn-outline social-btn" type="button">Continue with Facebook</button>
              </div>
              <div class="divider">or continue with email</div>
              <!--
                [data-demo-form] -> main.js bindDemoForms() attaches a submit listener.
                data-success-message -> toast message on success.
                Future Spring MVC: action="/login" method="post" + remove data-demo-form.
              -->
              <form class="floating-grid" data-demo-form
                    data-success-message="Signed in demo successfully."
                    id="login-form" novalidate>
                <div class="floating-field">
                  <input id="login-email" name="email" type="email" placeholder=" " required />
                  <label for="login-email">Email address</label>
                </div>
                <div class="floating-field">
                  <input id="login-password" name="password" type="password" placeholder=" " required minlength="6" />
                  <label for="login-password">Password</label>
                </div>
                <button class="btn btn-primary btn-block" type="submit">Sign in</button>
              </form>
              <div class="card-actions">
                <a href="/register">Create new account</a>
              </div>
            </div>
          </article>


      </div>
    </section>
  </main>

  <!-- SHARED FOOTER -->
  <footer class="site-footer" id="site-footer">
    <div class="container">
      <div class="footer-grid">
        <div class="stack"><div class="brand"><span class="brand__mark">JB</span><span class="brand__copy"><span class="brand__title">Jollibug</span><span class="brand__tag">Crave-worthy comfort, delivered fast</span></span></div></div>
        <div><h3>Explore</h3><div class="footer-links"><a href="/menu">Menu</a><a href="/about">About</a></div></div>
        <div><h3>Auth</h3><div class="footer-links"><a href="/register">Register</a><a href="/forgot-password">Reset Password</a></div></div>
        <div><h3>Contact</h3><div class="footer-links"><span>hello@Jollibug.vn</span></div></div>
      </div>
      <div class="footer-note"><span>&copy; <span data-current-year id="footer-year"></span> Jollibug.</span></div>
    </div>
  </footer>

  <!-- Cart Drawer -->
  <div class="drawer-backdrop" data-drawer-backdrop id="drawer-backdrop"></div>
  <aside class="cart-drawer" data-cart-drawer id="cart-drawer" aria-label="Shopping cart drawer">
    <div class="cart-drawer__header"><div class="card-actions"><div class="stack"><strong>Your order</strong><span class="muted">Fresh picks, saved instantly.</span></div><button class="btn btn-outline" type="button" data-action="close-cart">Close</button></div></div>
    <div class="cart-drawer__body" data-drawer-items id="drawer-items"></div>
    <div class="cart-drawer__footer stack"><div class="summary-line"><span>Total</span><strong data-drawer-total id="drawer-total">$0.00</strong></div><div class="cluster"><a class="btn btn-outline" href="/cart">Full Cart</a><button class="btn btn-primary" type="button" data-action="checkout-demo">Checkout Demo</button></div></div>
  </aside>
  <div class="toast-stack" data-toast-stack id="toast-stack"></div>

  <template id="mini-cart-item-template">
    <article class="mini-cart-item"><div class="mini-cart-item__media" style="background:rgba(255,248,238,0.92);"><img data-item-image alt="" /></div><div class="stack" style="gap:0.2rem;"><strong data-item-name></strong><span class="muted" data-item-price-qty></span></div><button class="btn btn-outline" type="button" data-action="remove-cart-item" data-cart-id="">Remove</button></article>
  </template>
  <template id="cart-drawer-empty-template">
    <div class="empty-state"><h3>Your cart is still warming up.</h3><p class="muted">Add a burger, combo, or drink.</p><a class="btn btn-secondary" href="/menu">Browse menu</a></div>
  </template>
<script src="js/client/nav.js" defer></script>
  <script src="js/client/store.js"></script>
<script src="js/client/main.js" defer></script>
  </body>
</html>





