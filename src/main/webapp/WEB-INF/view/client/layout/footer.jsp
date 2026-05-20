<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

