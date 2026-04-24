<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Product Detail</title>
  <meta name="description" content="View full product details, ratings, and nutritional notes. Add to cart with custom quantity." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/global.css" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/components.css" />
    <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>
<body data-page="menu">

  <!-- SHARED HEADER -->
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">
        <!--
          [data-product-page] - detected by main.js -> renderProductPage()
          JS fills all [data-product-*] slots via querySelector + textContent/src.
          No innerHTML is used on this section.
        -->
        <div class="product-spotlight" data-product-page id="product-page">

          <!-- Product image slot -->
          <article class="product-detail-card reveal-up">
            <div class="product-spotlight__media">
              <!--
                JS: root.querySelector('[data-product-image]').src = product.image;
                    root.querySelector('[data-product-image]').alt = product.name;
              -->
              <img src="/webapp/resources/shared/images/meal-burger.svg"
                   alt="Jollibug product" data-product-image id="product-image" />
            </div>
          </article>

          <!-- Product details slots -->
          <article class="product-detail-card reveal-up">
            <div class="stack">
              <span class="badge" data-product-badge id="product-badge">Best Seller</span>
              <h1 class="page-title" data-product-name id="product-name">Jollibug Product</h1>
              <p class="lead" data-product-description id="product-description">Product description.</p>

              <div class="price-row">
                <span class="price" data-product-price id="product-price">$0.00</span>
                <span class="muted" data-product-note id="product-note">0 kcal</span>
              </div>

              <div class="product-specs">
                <article class="product-spec">
                  <span class="muted">Guest rating</span>
                  <strong data-product-rating id="product-rating">0</strong>
                </article>
                <article class="product-spec">
                  <span class="muted">Reorder rate</span>
                  <strong data-product-popularity id="product-popularity">0</strong>
                </article>
                <article class="product-spec">
                  <span class="muted">Category</span>
                  <strong data-product-category id="product-category">Menu</strong>
                </article>
              </div>

              <!-- SECTION -->
              <div class="qty-control">
                <button type="button" data-action="product-qty-minus" id="btn-qty-minus">-</button>
                <input data-product-qty id="product-qty" value="1" readonly aria-label="Product quantity" />
                <button type="button" data-action="product-qty-plus" id="btn-qty-plus">+</button>
              </div>

              <div class="cluster">
                <!--
                  JS: root.querySelector('[data-action="add-product-detail"]').dataset.productId = product.id;
                -->
                <button class="btn btn-primary" type="button"
                        data-action="add-product-detail" data-product-id="1"
                        id="btn-add-to-cart">Add to cart</button>
                <a class="btn btn-outline" href="/menu">Back to menu</a>
              </div>
            </div>
          </article>

        </div><!-- /data-product-page -->
      </div>
    </section>

    <!-- Related products -->
    <section class="section-tight">
      <div class="container">
        <div class="page-intro reveal-up">
          <span class="eyebrow">You May Also Like</span>
          <h2 class="section-title">More items from the same flavor lane.</h2>
        </div>
        <!--
          JS (renderRelatedGrid) clones #product-card-template for related products.
        -->
        <div class="card-grid" data-related-grid id="related-grid"></div>
      </div>
    </section>
  </main>

  <!-- SHARED FOOTER -->
  <footer class="site-footer" id="site-footer">
    <div class="container">
      <div class="footer-grid">
        <div class="stack"><div class="brand"><span class="brand__mark">JB</span><span class="brand__copy"><span class="brand__title">Jollibug</span><span class="brand__tag">Crave-worthy comfort, delivered fast</span></span></div><p>Ready for Spring MVC storefront.</p></div>
        <div><h3>Explore</h3><div class="footer-links"><a href="/menu">Signature Menu</a><a href="/about">Brand Story</a></div></div>
        <div><h3>Support</h3><div class="footer-links"><a href="/cart">Cart &amp; Checkout</a><a href="/login">Sign In</a></div></div>
        <div><h3>Contact</h3><div class="footer-links"><span>hello@Jollibug.vn</span><span>+84 28 5555 8899</span></div></div>
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

  <!-- TEMPLATES -->
  <template id="product-card-template">
    <article class="card product-card reveal-up">
      <div class="product-card__media" data-product-tone><span class="product-card__chip" data-product-badge></span><img data-product-image alt="" /></div>
      <div class="stack">
        <div class="product-card__meta"><span data-product-category></span><span data-product-rating></span></div>
        <h3 class="product-card__title" data-product-name></h3>
        <p class="muted" data-product-description></p>
        <div class="price-row"><div class="stack" style="gap:0.15rem;"><span class="price" data-product-price></span><span class="muted" data-product-note></span></div></div>
        <div class="card-actions"><a class="btn btn-outline" data-product-detail-link href="#">Details</a><button class="btn btn-primary" type="button" data-action="add-product" data-product-id="">Add to cart</button></div>
      </div>
    </article>
  </template>
  <template id="mini-cart-item-template">
    <article class="mini-cart-item"><div class="mini-cart-item__media" style="background:rgba(255,248,238,0.92);"><img data-item-image alt="" /></div><div class="stack" style="gap:0.2rem;"><strong data-item-name></strong><span class="muted" data-item-price-qty></span></div><button class="btn btn-outline" type="button" data-action="remove-cart-item" data-cart-id="">Remove</button></article>
  </template>
  <template id="cart-drawer-empty-template">
    <div class="empty-state"><h3>Your cart is still warming up.</h3><p class="muted">Add a burger, combo, or drink to start your order.</p><a class="btn btn-secondary" href="/menu">Browse menu</a></div>
  </template>
<script src="js/client/nav.js" defer></script>
  <script src="js/client/store.js"></script>
<script src="js/client/main.js" defer></script>
  </body>
</html>





