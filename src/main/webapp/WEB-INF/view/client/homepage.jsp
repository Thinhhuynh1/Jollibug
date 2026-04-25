<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Premium Fast-Food Experience</title>
  <meta name="description" content="Jollibug - bold comfort food elevated with a polished ordering experience. Order from our signature menu and get delivery in 25 minutes." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />


  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
</head>

<body data-page="home">

  <jsp:include page="layout/header.jsp"/>
  <!-- /SHARED HEADER -->


  <!-- SECTION -->
  <main class="page-shell">

    <!-- Hero Section -->
    <section class="section">
      <div class="container">
        <div class="hero-banner reveal-up">
          <div class="hero-banner__grid">
            <div class="hero-copy">
              <span class="eyebrow">Premium Fast-Food Brand Feel</span>
              <h1>Bold comfort food, elevated with a polished ordering experience.</h1>
              <p>Jollibug is crafted to feel playful, premium, and production-ready, blending the appetite appeal of top global quick-service brands with a clean frontend architecture ready for backend integration.</p>
              <div class="hero-cta">
                <a class="btn btn-primary" href="/menu">Order the menu</a>
                <a class="btn btn-secondary" href="/about">Explore the story</a>
              </div>
              <div class="stats-ribbon">
                <article class="stats-ribbon__item"><span class="muted">Delivery promise</span><strong>25 min</strong></article>
                <article class="stats-ribbon__item"><span class="muted">Signature bundles</span><strong>12+</strong></article>
                <article class="stats-ribbon__item"><span class="muted">Guest rating</span><strong>4.9/5</strong></article>
              </div>
            </div>
            <div class="hero-media">
              <article class="hero-card hero-card--primary">
                <div class="hero-card__bg hero-card__bg--peach"></div>
                <img class="hero-card__media" src="/webapp/resources/shared/images/brand-feast.svg" alt="Jollibug combo illustration" />
                <span class="hero-card__label">Family combo spotlight</span>
              </article>
              <article class="hero-card">
                <div class="hero-card__bg hero-card__bg--butter"></div>
                <img class="hero-card__media" src="/webapp/resources/shared/images/meal-drink.svg" alt="Jollibug float drink" />
                <span class="hero-card__label">Seasonal mango float</span>
              </article>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- SECTION -->
    <section class="section-tight">
      <div class="container">
        <div class="promo-strip">
          <article class="promo-tile reveal-up">
            <strong>Built for cravings</strong>
            <p class="muted">Large product cards, warm gradients, and generous spacing make every dish feel desirable.</p>
          </article>
          <article class="promo-tile reveal-up">
            <strong>Ready for Spring MVC</strong>
            <p class="muted">Pages stay clean and semantic while shared JS/CSS handle repeated UI structure.</p>
          </article>
          <article class="promo-tile reveal-up">
            <strong>Slide-in cart UX</strong>
            <p class="muted">Guests can add items from anywhere and review totals instantly without losing context.</p>
          </article>
        </div>
      </div>
    </section>

    <!-- SECTION -->
    <section class="section">
      <div class="container">
        <div class="page-intro reveal-up">
          <span class="eyebrow">Top Picks</span>
          <h2 class="section-title">Guest favorites designed to convert fast.</h2>
          <p class="lead">A premium storefront needs a menu grid that feels energetic but intentional. These cards are driven from a shared catalog and can later be populated from your backend.</p>
        </div>

        <div class="card-grid" data-home-featured id="home-featured-grid">
          <article class="card product-card reveal-up">
            <div class="product-card__media" style="background:rgba(255,248,238,0.92);">
              <span class="product-card__chip">Best seller</span>
              <img src="/webapp/resources/shared/images/meal-burger.svg" alt="Smoky double burger" />
            </div>
            <div class="stack">
              <div class="product-card__meta"><span>Burger</span><span>4.9</span></div>
              <h3 class="product-card__title">Smoky Double Burger</h3>
              <p class="muted">Two grilled patties, cheddar, caramelized onion and house sauce.</p>
              <div class="price-row"><span class="price">$9.90</span></div>
              <div class="card-actions">
                <a class="btn btn-outline" href="/product">Details</a>
                <button class="btn btn-primary" type="button">Add to cart</button>
              </div>
            </div>
          </article>
          <article class="card product-card reveal-up">
            <div class="product-card__media" style="background:rgba(245,250,255,0.92);">
              <span class="product-card__chip">Combo</span>
              <img src="/webapp/resources/shared/images/meal-drink.svg" alt="Crispy combo with drink" />
            </div>
            <div class="stack">
              <div class="product-card__meta"><span>Combo</span><span>4.8</span></div>
              <h3 class="product-card__title">Crispy Combo Meal</h3>
              <p class="muted">Crispy chicken, waffle fries and mango float in one value set.</p>
              <div class="price-row"><span class="price">$12.50</span></div>
              <div class="card-actions">
                <a class="btn btn-outline" href="/menu">Browse</a>
                <button class="btn btn-primary" type="button">Add to cart</button>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>

    <!-- Story + Cravings -->
    <section class="section-tight">
      <div class="container">
        <div class="story-grid">
          <article class="story-block reveal-up">
            <span class="eyebrow">Why It Feels Premium</span>
            <h2 class="section-title">Pastel stages, floating food, and a brand language that feels alive.</h2>
            <p class="lead">Instead of generic cards on a flat white page, Jollibug uses layered surfaces, soft shadows, playful rounded typography, and local food illustrations to create a more intentional F&amp;B identity.</p>
            <div class="cluster">
              <a class="btn btn-primary" href="/menu">See all menu items</a>
              <a class="btn btn-outline" href="/contact">Book a tasting session</a>
            </div>
          </article>
          <article class="split-card story-highlight reveal-up">
            <div class="page-intro" style="margin-bottom:0;">
              <span class="eyebrow">Crave Moments</span>
              <h3 class="section-title">Jollibug signature moodboard</h3>
            </div>
            <!--

            -->
            <div class="grid" data-home-cravings id="home-cravings-grid">
              <article class="promo-tile reveal-up">
                <strong>Truffle Smash</strong>
                <p class="muted">Soft potato bun, truffle aioli, double beef patty.</p>
              </article>
              <article class="promo-tile reveal-up">
                <strong>Cheese Volcano Fries</strong>
                <p class="muted">Crispy fries covered with signature cheddar lava sauce.</p>
              </article>
              <article class="promo-tile reveal-up">
                <strong>Tropical Float</strong>
                <p class="muted">Mango soda float for a cool and sweet finish.</p>
              </article>
            </div>
          </article>
        </div>
      </div>
    </section>

    <!-- SECTION -->
    <section class="section">
      <div class="container">
        <div class="page-intro reveal-up">
          <span class="eyebrow">Brand Pillars</span>
          <h2 class="section-title">A frontend system that supports both delight and scale.</h2>
        </div>
        <div class="card-grid">
          <article class="card feature-card reveal-up">
            <div class="feature-card__icon">01</div>
            <h3>Consistent across pages</h3>
            <p class="muted">Navbar, footer, button behavior, and product data all come from shared assets instead of duplicated snippets.</p>
          </article>
          <article class="card feature-card reveal-up">
            <div class="feature-card__icon">02</div>
            <h3>Natural admin separation</h3>
            <p class="muted">Managers and super admins get distinct sidebars and workflows without branching the entire design language.</p>
          </article>
          <article class="card feature-card reveal-up">
            <div class="feature-card__icon">03</div>
            <h3>Conversion-minded details</h3>
            <p class="muted">Search-first menu, floating auth forms, a Shopee-style address book, and quick cart access all reduce friction.</p>
          </article>
        </div>
      </div>
    </section>

  </main>


  <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp" />


  </body>
</html>





