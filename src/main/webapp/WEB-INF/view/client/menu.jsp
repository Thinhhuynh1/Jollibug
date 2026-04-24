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


        <!-- SECTION -->
        <section class="menu-toolbar reveal-up" aria-label="Menu filters">
          <div class="toolbar-row">
            <div class="searchbar">
              <!--
                main.js attaches an 'input' listener to this element.
                The search icon SVG is now static inline - no JS injection.
              -->
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"></circle><path d="m20 20-3.5-3.5"></path>
              </svg>
              <input data-menu-search id="menu-search" type="search"
                      placeholder="Tìm kiếm món ăn?" aria-label="Search menu items" />
            </div>

            <select id="menu-sort" data-menu-sort>
              <option value="popular">Bộ lọc</option>
              <option value="price-low">Giá: thấp đến cao</option>
              <option value="price-high">Giá: cao đến thấp</option>
              <option value="rating">Bán chạy</option>
            </select>
          </div>

          <div class="category-nav" aria-label="Menu categories">
            <button class="category-nav__arrow" type="button" data-cat-arrow="prev" aria-label="Previous categories">&#10094;</button>
            <div class="category-strip" data-menu-cats id="menu-categories">
              <button class="filter-pill is-active" type="button">All</button>
              <button class="filter-pill" type="button">Burger</button>
              <button class="filter-pill" type="button">Chicken</button>
              <button class="filter-pill" type="button">Combo</button>
              <button class="filter-pill" type="button">Drink</button>
              <button class="filter-pill" type="button">Snack</button>
              <button class="filter-pill" type="button">Family Box</button>
              <button class="filter-pill" type="button">Dessert</button>
              <button class="filter-pill" type="button">Spicy</button>
              <button class="filter-pill" type="button">Value Deal</button>
              <button class="filter-pill" type="button">Burger</button>
              <button class="filter-pill" type="button">Chicken</button>
              <button class="filter-pill" type="button">Combo</button>
              <button class="filter-pill" type="button">Drink</button>
              <button class="filter-pill" type="button">Snack</button>
              <button class="filter-pill" type="button">Family Box</button>
              <button class="filter-pill" type="button">Dessert</button>
              <button class="filter-pill" type="button">Spicy</button>
              <button class="filter-pill" type="button">Value Deal</button>
              
            </div>
            <button class="category-nav__arrow" type="button" data-cat-arrow="next" aria-label="Next categories">&#10095;</button>
          </div>
        </section>

        <div class="menu-results reveal-up">

          <strong data-menu-count id="menu-count">3 items ready</strong>
        </div>
        <section class="card-grid" data-menu-grid id="menu-grid" aria-label="Menu items">
          <article class="card product-card reveal-up">
            <div class="product-card__media" style="background:rgba(255,248,238,0.92);">
              <img src="/webapp/resources/shared/images/meal-burger.svg" alt="Smoky double burger" />
            </div>
            <div class="stack">
              <div class="product-card__meta"><span>Burger</span><span>4.9</span></div>
              <h3 class="product-card__title">Smoky Double Burger</h3>
              <p class="muted">Double patty, cheddar, onion jam, and signature sauce.</p>
              <div class="price-row"><span class="price">$9.90</span></div>
              <div class="card-actions">
                <a class="btn btn-outline" href="/product">Xem chi tiết</a>
                <button class="btn btn-primary" type="button">Thêm vào giỏ hàng</button>
              </div>
            </div>
          </article>
          <article class="card product-card reveal-up">
            <div class="product-card__media" style="background:rgba(245,250,255,0.92);">
              <img src="/webapp/resources/shared/images/meal-drink.svg" alt="Crispy combo meal" />
            </div>
            <div class="stack">
              <div class="product-card__meta"><span>Combo</span><span>4.8</span></div>
              <h3 class="product-card__title">Crispy Combo Meal</h3>
              <p class="muted">Chicken fillet burger, waffle fries, mango float.</p>
              <div class="price-row"><span class="price">$12.50</span></div>
              <div class="card-actions">
                <a class="btn btn-outline" href="/product">Xem chi tiết</a>
                <button class="btn btn-primary" type="button">Thêm vào giỏ hàng</button>
              </div>
            </div>
          </article>
          <article class="card product-card reveal-up">
            <div class="product-card__media" style="background:rgba(251,244,255,0.92);">
              <img src="/webapp/resources/shared/images/brand-feast.svg" alt="Family feast" />
            </div>
            <div class="stack">
              <div class="product-card__meta"><span>Family</span><span>4.7</span></div>
              <h3 class="product-card__title">Family Feast Box</h3>
              <p class="muted">A shareable box with burgers, chicken bites, and sides.</p>
              <div class="price-row"><span class="price">$24.00</span></div>
              <div class="card-actions">
                <a class="btn btn-outline" href="/product">Xem chi tiết</a>
                <button class="btn btn-primary" type="button">Thêm vào giỏ hàng</button>
              </div>
            </div> 
          </article>
        </section>
      </div>
    </section>
  </main>

    <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp" />
  

  </body>
  <script src="client/store.js"></script>  <!-- mui ten qua lai danh muc-->
</html>




