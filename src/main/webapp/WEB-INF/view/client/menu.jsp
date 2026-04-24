<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Signature Menu</title>
  <meta name="description" content="Search, sort, and filter the Jollibug menu. Browse burgers, chicken, combos, drinks, sides, and wraps." />
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

  <!-- SECTION -->
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">
        <div class="page-intro reveal-up">
          <span class="eyebrow">Signature Menu</span>
          <h1 class="page-title">Search, sort, and filter exactly what you are craving.</h1>
          <p class="lead">The menu experience is organized for real usage: prominent search, category pills, sort controls, and a grid that stays visually aligned on tablet and desktop.</p>
        </div>

        <!-- SECTION -->
        <section class="menu-toolbar reveal-up" aria-label="Menu filters">
          <div class="searchbar">
            <!--
              main.js attaches an 'input' listener to this element.
              The search icon SVG is now static inline - no JS injection.
            -->
            <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="7"></circle><path d="m20 20-3.5-3.5"></path>
            </svg>
            <input data-menu-search id="menu-search" type="search"
                   placeholder="What are you craving?" aria-label="Search menu items" />
          </div>
          <div class="toolbar-row">
            <div>
              <div class="select-group">
                <label>Browse by category</label>
                <!--
                  Category pills container.
                  JS (renderMenuCategoryPills) clones #category-pill-template
                  for each category and appends here.
                -->
                <div class="category-strip" data-menu-categories id="menu-categories"></div>
              </div>
            </div>
            <div class="select-group">
              <label for="menu-sort">Sort menu</label>
              <select id="menu-sort" data-menu-sort>
                <option value="popular">Popularity</option>
                <option value="price-low">Price: Low to High</option>
                <option value="price-high">Price: High to Low</option>
                <option value="rating">Top Rated</option>
              </select>
            </div>
          </div>
        </section>

        <div class="menu-results reveal-up">
          <!--
            JS: document.getElementById('menu-count').textContent = `${n} items ready`;
          -->
          <strong data-menu-count id="menu-count">3 items ready</strong>
          <span class="muted">Transparent-background visuals sit on pastel blocks for a premium QSR vibe.</span>
        </div>

        <!--
          Product cards container.
          JS (renderMenuGrid) clones #product-card-template for each result.
          Empty state comes from #menu-empty-template.
        -->
        <section class="card-grid" data-menu-grid id="menu-grid" aria-label="Menu items">
          <article class="card product-card reveal-up">
            <div class="product-card__media" style="background:rgba(255,248,238,0.92);">
              <span class="product-card__chip">Popular</span>
              <img src="/webapp/resources/shared/images/meal-burger.svg" alt="Smoky double burger" />
            </div>
            <div class="stack">
              <div class="product-card__meta"><span>Burger</span><span>4.9</span></div>
              <h3 class="product-card__title">Smoky Double Burger</h3>
              <p class="muted">Double patty, cheddar, onion jam, and signature sauce.</p>
              <div class="price-row"><span class="price">$9.90</span></div>
              <div class="card-actions">
                <a class="btn btn-outline" href="/product">Details</a>
                <button class="btn btn-primary" type="button" id = "add-to-cart">Add to cart</button>
              </div>
            </div>
          </article>
          <article class="card product-card reveal-up">
            <div class="product-card__media" style="background:rgba(245,250,255,0.92);">
              <span class="product-card__chip">New</span>
              <img src="/webapp/resources/shared/images/meal-drink.svg" alt="Crispy combo meal" />
            </div>
            <div class="stack">
              <div class="product-card__meta"><span>Combo</span><span>4.8</span></div>
              <h3 class="product-card__title">Crispy Combo Meal</h3>
              <p class="muted">Chicken fillet burger, waffle fries, mango float.</p>
              <div class="price-row"><span class="price">$12.50</span></div>
              <div class="card-actions">
                <a class="btn btn-outline" href="/product">Details</a>
                <button class="btn btn-primary" type="button">Add to cart</button>
              </div>
            </div>
          </article>
          <article class="card product-card reveal-up">
            <div class="product-card__media" style="background:rgba(251,244,255,0.92);">
              <span class="product-card__chip">Value</span>
              <img src="/webapp/resources/shared/images/brand-feast.svg" alt="Family feast" />
            </div>
            <div class="stack">
              <div class="product-card__meta"><span>Family</span><span>4.7</span></div>
              <h3 class="product-card__title">Family Feast Box</h3>
              <p class="muted">A shareable box with burgers, chicken bites, and sides.</p>
              <div class="price-row"><span class="price">$24.00</span></div>
              <div class="card-actions">
                <a class="btn btn-outline" href="/product">Details</a>
                <button class="btn btn-primary" type="button">Add to cart</button>
              </div>
            </div>
          </article>
        </section>
      </div>
    </section>
  </main>

  <!-- SECTION -->
  <footer class="site-footer" id="site-footer">
    <div class="container">
      <div class="footer-grid">
        <div class="stack">
          <div class="brand"><span class="brand__mark">JB</span><span class="brand__copy"><span class="brand__title">Jollibug</span><span class="brand__tag">Crave-worthy comfort, delivered fast</span></span></div>
          <p>Jollibug pairs playful energy with polished quick-service UX, ready for a Spring MVC storefront.</p>
        </div>
        <div><h3>Explore</h3><div class="footer-links"><a href="/menu">Signature Menu</a><a href="/about">Brand Story</a><a href="/contact">Store Locator</a></div></div>
        <div><h3>Support</h3><div class="footer-links"><a href="/cart">Cart &amp; Checkout</a><a href="/forgot-password">Reset Password</a><a href="/register">Create Account</a></div></div>
        <div><h3>Contact</h3><div class="footer-links"><span>88 Flavor Avenue, Ho Chi Minh City</span><span>+84 28 5555 8899</span><span>hello@Jollibug.vn</span></div></div>
      </div>
      <div class="footer-note">
        <span>&copy; <span data-current-year id="footer-year"></span> Jollibug.</span>
        <span>Freshly made. Smoothly managed. Ready for backend integration.</span>
      </div>
    </div>
  </footer>

  <!-- Cart Drawer -->
  <div class="drawer-backdrop" data-drawer-backdrop id="drawer-backdrop"></div>
  <aside class="cart-drawer" data-cart-drawer id="cart-drawer" aria-label="Shopping cart drawer">
    <div class="cart-drawer__header">
      <div class="card-actions">
        <div class="stack"><strong>Your order</strong><span class="muted">Fresh picks, saved instantly.</span></div>
        <button class="btn btn-outline" type="button" data-action="close-cart">Close</button>
      </div>
    </div>
    <div class="cart-drawer__body" data-drawer-items id="drawer-items"></div>
    <div class="cart-drawer__footer stack">
      <div class="summary-line"><span>Total</span><strong data-drawer-total id="drawer-total">$0.00</strong></div>
      <div class="cluster">
        <a class="btn btn-outline" href="/cart">Full Cart</a>
        <button class="btn btn-primary" type="button" data-action="checkout-demo">Checkout Demo</button>
      </div>
    </div>
  </aside>
  <div class="toast-stack" data-toast-stack id="toast-stack"></div>


  <!-- SECTION

  -->
  <template id="product-card-template">
    <article class="card product-card reveal-up">
      <div class="product-card__media" data-product-tone>
        <span class="product-card__chip" data-product-badge></span>
        <img data-product-image alt="" />
      </div>
      <div class="stack">
        <div class="product-card__meta">
          <span data-product-category></span>
          <span data-product-rating></span>
        </div>
        <h3 class="product-card__title" data-product-name></h3>
        <p class="muted" data-product-description></p>
        <div class="price-row">
          <div class="stack" style="gap:0.15rem;">
            <span class="price" data-product-price></span>
            <span class="muted" data-product-note></span>
          </div>
        </div>
        <div class="card-actions">
          <a class="btn btn-outline" data-product-detail-link href="#">Details</a>
          <button class="btn btn-primary" type="button" data-action="add-product" data-product-id="">Add to cart</button>
        </div>
      </div>
    </article>
  </template>

  <!-- SECTION -->
  <template id="category-pill-template">
    <button class="filter-pill" type="button" data-category=""><!-- label --></button>
  </template>

  <!-- Empty menu state -->
  <template id="menu-empty-template">
    <div class="empty-state">
      <h3>No dishes matched that craving.</h3>
      <p class="muted">Try a simpler keyword, or switch to another category.</p>
    </div>
  </template>

  <!-- Mini cart item template -->
  <template id="mini-cart-item-template">
    <article class="mini-cart-item">
      <div class="mini-cart-item__media" style="background:rgba(255,248,238,0.92);"><img data-item-image alt="" /></div>
      <div class="stack" style="gap:0.2rem;"><strong data-item-name></strong><span class="muted" data-item-price-qty></span></div>
      <button class="btn btn-outline" type="button" data-action="remove-cart-item" data-cart-id="">Remove</button>
    </article>
  </template>
  <template id="cart-drawer-empty-template">
    <div class="empty-state"><h3>Your cart is still warming up.</h3><p class="muted">Add a burger, combo, or drink to start your order.</p><a class="btn btn-secondary" href="/menu">Browse menu</a></div>
  </template>
<script src="js/client/nav.js" defer></script>
  <script src="js/client/store.js"></script>
<script src="js/client/main.js" defer></script>
  </body>
</html>





