<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Client - My Reviews</title>
  <meta name="description" content="Jollibug - manage your personal food reviews. Add, edit, and delete your ratings." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/styles.css" />
</head>
<body>

  <!-- SECTION -->
  <jsp:include page="layout/header.jsp"/>

  <!-- SECTION -->
  <main class="page-shell">
    <section class="section">
      <div class="container">

        <!-- Page intro -->
        <div class="page-intro">
          <span class="eyebrow">My Account</span>
          <h1 class="section-title">My Reviews</h1>
          <p class="lead">Rate and review the food you've ordered. Your feedback helps everyone.</p>
        </div>

        <!-- Toolbar -->
        <div style="display:flex; flex-wrap:wrap; align-items:center; justify-content:space-between; gap:1rem; margin-bottom:2rem;">
          <label class="searchbar" style="flex:1; max-width:24rem;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
              <circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/>
            </svg>
            <input type="search" id="review-search" placeholder="Search your reviews..." />
          </label>
          <button class="btn btn-primary" type="button" id="btn-write-review">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16" aria-hidden="true">
              <path d="M5 12h14M12 5v14"/>
            </svg>
            Write a Review
          </button>
        </div>

        <!-- Review cards grid -->
        <div class="card-grid" id="reviews-grid" style="grid-template-columns: repeat(auto-fill, minmax(20rem, 1fr));">
          <!-- Populated by reviews.js -->
        </div>

        <!-- Empty state -->
        <div class="empty-state hidden" id="reviews-empty">
          <p style="font-size:2.5rem; margin-bottom:0.75rem;">-­</p>
          <h3>No reviews yet</h3>
          <p class="muted">Order some food and share your experience! Your first review is waiting.</p>
          <a href="/menu" class="btn btn-primary" style="margin-top:1.25rem;">Browse Menu</a>
        </div>

      </div>
    </section>
  </main>


  <!-- SECTION -->
  <div class="modal admin-modal" id="review-modal" aria-modal="true" role="dialog" aria-labelledby="review-modal-title"
       style="display:none; position:fixed; inset:0; z-index:900; display:none; place-items:center; background:rgba(24,12,4,0.5); backdrop-filter:blur(4px);">
    <div class="modal__dialog" style="width:min(100%,38rem); background:#fffefb; border-radius:var(--radius-xl); padding:0; overflow:hidden; box-shadow:var(--shadow-lg);">
      <div class="modal__header" style="padding:1.5rem; border-bottom:1px solid rgba(111,82,55,0.08);">
        <div>
          <h2 id="review-modal-title" style="font-size:1.3rem;">Write a Review</h2>
          <p class="muted" style="margin-top:0.2rem;">Share your honest experience with this dish.</p>
        </div>
        <button class="btn btn-outline" type="button" id="btn-close-review-modal" style="flex-shrink:0;">Close</button>
      </div>
      <form id="review-form" class="admin-modal__grid" style="padding:1.5rem;" novalidate>
        <label class="field-label">
          <span>Product</span>
          <select id="review-product">
            <option value="">Select a product...</option>
            <option value="Smoky Double Burger">Smoky Double Burger</option>
            <option value="Grilled Chicken Wrap">Grilled Chicken Wrap</option>
            <option value="BBQ Beef Burger">BBQ Beef Burger</option>
            <option value="Crispy Fries (L)">Crispy Fries (L)</option>
            <option value="Spicy Chicken Burger">Spicy Chicken Burger</option>
            <option value="Classic Cheeseburger">Classic Cheeseburger</option>
          </select>
        </label>

        <div>
          <label class="field-label" style="margin-bottom:0.5rem;"><span>Rating</span></label>
          <div class="cluster" id="star-picker" style="gap:0.3rem;" role="group" aria-label="Star rating">
            <!-- Stars rendered by JS -->
          </div>
          <input type="hidden" id="review-rating" value="0" />
        </div>

        <label class="field-label">
          <span>Your Review</span>
          <textarea id="review-text" placeholder="What did you enjoy (or not) about this dish?" style="min-height:7rem;"></textarea>
        </label>

        <button class="btn btn-primary" type="button" id="btn-save-review">
          Save Review
        </button>
      </form>
    </div>
  </div>


  <!-- SECTION -->
  <template id="review-card-template">
    <article class="card" style="border-radius:var(--radius-xl);">
      <div class="card__body">
        <div style="display:flex; align-items:flex-start; justify-content:space-between; gap:0.75rem; margin-bottom:0.85rem;">
          <div style="display:grid; gap:0.2rem;">
            <strong data-product style="font-size:1rem;"></strong>
            <span class="muted" data-date style="font-size:0.8rem;"></span>
          </div>
          <span data-stars style="font-size:1.2rem; letter-spacing:0.05em;"></span>
        </div>
        <p data-text style="font-size:0.92rem; color:var(--color-ink-700); line-height:1.6; margin-bottom:1rem;"></p>
        <div class="cluster" style="gap:0.5rem;">
          <button class="btn btn-outline" type="button" data-edit style="padding:0.55rem 0.9rem; min-height:2.2rem; font-size:0.85rem;">Edit</button>
          <button class="btn btn-ghost" type="button" data-delete style="padding:0.55rem 0.9rem; min-height:2.2rem; font-size:0.85rem; color:var(--color-danger);">Delete</button>
        </div>
      </div>
    </article>
  </template>


  <!-- SECTION -->
  <footer class="site-footer">
    <div class="container">
      <div class="footer-note">
        <span>© 2026 Jollibug. All rights reserved.</span>
        <a href="/">Back to Home</a>
      </div>
    </div>
  </footer>

  <!-- Toast stack -->
  <div class="toast-stack" id="client-toast-stack" style="position:fixed; bottom:1.5rem; right:1.5rem; z-index:9999; display:grid; gap:0.65rem;"></div>

<script src="js/client/nav.js" defer></script>
  <script src="js/client/reviews.js" defer></script>
  </body>
</html>





