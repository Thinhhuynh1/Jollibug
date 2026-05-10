<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Manage Products</title>
  <meta name="description" content="Jollibug Manager - keep product visuals, pricing, categories, and stock states up to date." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>

<!--
  data-admin-role -> "manager"  - Spring Security path guard
  data-admin-page -> "products" - read by product.js
-->
<body data-admin-role="manager" data-admin-page="products">

  <div class="admin-shell admin-body" data-admin-table-root>

    <!-- SECTION -->
    <aside class="admin-sidebar">
      <div class="admin-sidebar__inner">
        <div class="admin-brand">
          <div class="brand">
            <span class="brand__mark">JB</span>
            <span class="brand__copy">
              <span class="brand__title">Jollibug Admin</span>
              <span class="brand__tag">Control Center</span>
            </span>
          </div>
          <span class="admin-role">Manager</span>
          <p>Menu, catalog, and order operations</p>
        </div>

        <nav class="admin-nav">
          <span class="admin-nav__section">Workspace</span>
          <a href="/admin">Dashboard</a>
          <a href="/categories">Manage Categories</a>
          <a class="is-active" href="/products">Manage Products</a>
          <a href="manager-orders.html">Manage Orders</a>
          <span class="admin-nav__section">Quick links</span>
          <a href="index.html">Back to site</a>
        </nav>
      </div>
    </aside>

    <!-- SECTION -->
    <main class="admin-main">

      <!-- SECTION -->
      <div class="admin-topbar">
        <div class="admin-topbar__copy">
          <strong>Jollibug Control Center</strong>
          <span class="muted">Product catalog management - visuals, pricing, and stock.</span>
        </div>
        <div class="admin-topbar__user">
          <span class="admin-role">Manager</span>
          <div class="admin-avatar" id="topbar-user-initials">--</div>
          <div class="stack" style="gap:0.15rem;">
            <strong id="topbar-user-name">Loading...</strong>
            <span class="muted" id="topbar-user-role">Manager</span>
          </div>
          <button class="btn btn-outline" type="button" data-admin-logout id="btn-logout">Logout</button>
        </div>
      </div>

      <!-- SECTION -->
      <section class="admin-panel">
        <div class="panel-header">
          <div class="stack" style="gap:0.3rem;">
            <span class="eyebrow" id="admin-table-eyebrow">Manager</span>
            <h1 class="section-title" id="admin-table-title">Manage Products</h1>
            <p class="muted" id="admin-table-subtitle"></p>
          </div>
          <div class="panel-controls">
            <label class="table-search">
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"></circle>
                <path d="m20 20-3.5-3.5"></path>
              </svg>
              <input id="admin-table-search" type="search" placeholder="Search products, category, or status" />
            </label>
            <button class="btn btn-primary" type="button" data-admin-open-modal id="admin-table-add-button">
              Add New Product
            </button>
          </div>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table">
            <thead>
              <!-- JS appends <th> elements here -->
              <tr id="admin-table-head-row">
                <th>Product</th>
                <th>Category</th>
                <th>Price</th>
                <th>Stock Qty</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody id="admin-table-body">
              <tr>
                <td>Smoky Double Burger</td>
                <td>Signature Burgers</td>
                <td>$9.90</td>
                <td><span class="status-badge" data-status="active">active</span></td>
                <td>
                  <div class="cluster">
                    <button class="btn btn-outline" type="button">View</button>
                    <button class="btn btn-outline" type="button">Edit</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

    </main>
  </div><!-- /data-admin-table-root -->


  <!-- SECTION
       TEMPLATES
       Row: cells 0-5 -> [Product(thumb), Category, Price, Status, Actions]
       Cell 5 is the Actions cell (View + Edit + Delete).
  -->
  <template id="admin-table-row-template">
    <tr>
      <td data-cell="0"></td>
      <td data-cell="1"></td>
      <td data-cell="2"></td>
      <td data-cell="3"></td>
      <!-- SECTION
      <td data-cell="4"></td>
      -->
      <td data-cell="5"></td>
    </tr>
  </template>

  <template id="admin-table-empty-template">
    <tr>
      <td>
        <div class="empty-state">
          <h3>No products found.</h3>
          <p class="muted">Adjust the search term or add a new product.</p>
        </div>
      </td>
    </tr>
  </template>


  <!-- SECTION -->
  <div class="modal admin-modal" data-admin-modal id="admin-modal"
       aria-modal="true" role="dialog" aria-labelledby="admin-modal-title">
    <div class="modal__dialog">
      <div class="modal__header">
        <div class="stack" style="gap:0.25rem;">
          <h2 data-admin-modal-title id="admin-modal-title">Modal</h2>
          <span class="muted" data-admin-modal-copy>Update details and save instantly.</span>
        </div>
        <button class="btn btn-outline" type="button" data-admin-close-modal id="btn-close-modal">Close</button>
      </div>
      <form data-admin-form class="admin-modal__grid" id="admin-modal-form" novalidate></form>
    </div>
  </div>


  <!-- SECTION
       PRODUCT DETAIL SLIDE-OVER PANEL
       Triggered by: [data-admin-view] on each product row.
       JS (showDetail / closeDetail in product.js):
         * removes .sdp--hidden to open, adds it back to close
         * fills [id="sdp-*"] slots via textContent / src / dataset only
         * NEVER uses innerHTML for structural markup

       SOC contract:
         -œ…  All structure is here - JS injects data values only.
         -œ…  Animation is pure CSS (transform + opacity transitions).
         -œ…  Backdrop [data-sdp-close] closes on click.
         -Œ  JS does NOT build or return HTML strings.

       Product-specific slots:
         sdp-product       -> overlay <aside>
         sdp-product-image -> <img> - src + alt set by JS
         sdp-product-name  -> product name heading
         sdp-product-price -> formatted price
         sdp-product-cat   -> category pill
         sdp-product-badge -> featured / stock badge
         sdp-product-id    -> ID field
         sdp-product-status -> status badge
         sdp-edit-btn      -> "Edit product" shortcut

       Future Spring MVC:
         <%@ include file="/WEB-INF/view/shared/product-detail-panel.jsp" %>
  -->
  <aside class="sdp sdp--hidden"
         id="sdp-product"
         role="dialog"
         aria-modal="true"
         aria-labelledby="sdp-product-name"
         data-detail-panel>

    <!-- Backdrop -->
    <div class="sdp__backdrop" data-sdp-close></div>

    <!-- Slide-over card -->
    <div class="sdp__card">

      <!-- SECTION -->
      <button class="sdp__close-btn"
              type="button"
              data-sdp-close
              id="btn-sdp-close"
              aria-label="Close product details">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
             width="20" height="20" aria-hidden="true">
          <path d="M18 6 6 18M6 6l12 12"/>
        </svg>
      </button>

      <!-- SECTION -->
      <div class="sdp__product-media">
        <!--
          JS: sdpImg.src = record.image;
              sdpImg.alt = record.name;
        -->
        <img class="sdp__product-img"
             id="sdp-product-image"
             src=""
             alt="Product"
             width="120" height="120"
             loading="lazy" />
        <!--
          Status badge overlaid on image.
          JS: sdpBadge.textContent = record.status;
              sdpBadge.dataset.status = record.status;
        -->
        <span class="status-badge sdp__product-badge"
              id="sdp-product-badge"
              data-status="active">active</span>
      </div>

      <!-- SECTION -->
      <div class="sdp__hero sdp__hero--product">
        <div class="sdp__hero-meta">
          <!--
            JS: document.getElementById('sdp-product-name').textContent = record.name;
          -->
          <h2 class="sdp__title" id="sdp-product-name">Product Name</h2>
          <!--
            JS: document.getElementById('sdp-product-price').textContent = formatMoney(record.price);
          -->
          <span class="sdp__price" id="sdp-product-price">$0.00</span>
        </div>
        <!--
          Category pill.
          JS: document.getElementById('sdp-product-cat').textContent = record.category;
        -->
        <span class="sdp__cat-pill" id="sdp-product-cat">Category</span>
      </div>

      <!-- SECTION -->

      <section class="sdp__section">
        <h3 class="sdp__section-title">Product Details</h3>
        <dl class="sdp__fields">

          <div class="sdp__field">
            <dt>Product ID</dt>
            <dd id="sdp-product-id">--</dd>
          </div>

          <div class="sdp__field">
            <dt>Category</dt>
            <dd id="sdp-product-cat-field">--</dd>
          </div>

          <div class="sdp__field">
            <dt>Price</dt>
            <dd id="sdp-product-price-field">--</dd>
          </div>

          <div class="sdp__field">
            <dt>Stock Qty</dt>
            <dd id="sdp-product-stock">--</dd>
          </div>

          <div class="sdp__field">
            <dt>Stock Status</dt>
            <dd>
              <span class="status-badge" id="sdp-product-status" data-status="active">active</span>
            </dd>
          </div>

          <div class="sdp__field" style="grid-template-columns:8rem 1fr; align-items:start;">
            <dt>Description</dt>
            <dd id="sdp-product-desc" style="white-space:pre-wrap; font-size:0.88rem; color:var(--color-ink-700); line-height:1.55;">--</dd>
          </div>

        </dl>
      </section>

      <!-- SECTION -->
      <div class="sdp__actions">
        <!--
          Clicking opens edit modal with this product pre-filled.
          JS: sdpEditBtn.dataset.detailEdit = record.id;
        -->
        <button class="btn btn-primary sdp__action-btn"
                type="button"
                data-detail-edit=""
                id="sdp-edit-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
               width="16" height="16" aria-hidden="true">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
          </svg>
          Edit Product
        </button>
        <button class="btn btn-ghost sdp__action-btn"
                type="button"
                data-sdp-close>
          Close
        </button>
      </div>

    </div><!-- /sdp__card -->
  </aside>
  <!-- /PRODUCT DETAIL SLIDE-OVER PANEL -->


  <div class="toast-stack" data-admin-toast-stack id="admin-toast-stack"></div>

  <!--
    product.js is the dedicated module for this page.
    It does NOT use the shared table.js.
  -->
<script src="js/manager/product.js" defer></script>
  </body>
</html>




