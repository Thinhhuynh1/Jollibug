<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Manage Categories</title>
  <meta name="description" content="Jollibug Manager - organize the menu taxonomy so shoppers can filter quickly." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>

<!--
  data-admin-role -> "manager"  - used by Spring Security path guard
  data-admin-page -> "categories" - read by category.js
-->
<body data-admin-role="manager" data-admin-page="categories">

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
          <a class="is-active" href="/categories">Manage Categories</a>
          <a href="/products">Manage Products</a>
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
          <span class="muted">Category taxonomy management with live CRUD.</span>
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

      <!-- SECTION
           JS slots:
             #admin-table-eyebrow   -> role label
             #admin-table-title     -> page title
             #admin-table-subtitle  -> subtitle
             #admin-table-search    -> placeholder + oninput
             #admin-table-add-button -> button label
             #admin-table-head-row  -> <th> nodes
             #admin-table-body      -> <tr> clones
      -->
      <section class="admin-panel">
        <div class="panel-header">
          <div class="stack" style="gap:0.3rem;">
            <span class="eyebrow" id="admin-table-eyebrow">Manager</span>
            <h1 class="section-title" id="admin-table-title">Manage Categories</h1>
            <p class="muted" id="admin-table-subtitle"></p>
          </div>
          <div class="panel-controls">
            <label class="table-search">
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"></circle>
                <path d="m20 20-3.5-3.5"></path>
              </svg>
              <input id="admin-table-search" type="search" placeholder="Search categories or slug" />
            </label>
            <button class="btn btn-primary" type="button" data-admin-open-modal id="admin-table-add-button">
              Add New Category
            </button>
          </div>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table">
            <thead>
              <tr id="admin-table-head-row">
                <th>Category</th>
                <th>Slug</th>
                <th>Items</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody id="admin-table-body">
              <tr>
                <td>Signature Burgers</td>
                <td>signature-burgers</td>
                <td>12</td>
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
       Row template: cells 0-4 -> [Category, Slug, Items, Status, Actions(View+Edit+Delete)].
       JS category.js populates [data-cell="n"] via config.columns[n].render().
  -->
  <template id="admin-table-row-template">
    <tr>
      <td data-cell="0"></td>
      <td data-cell="1"></td>
      <td data-cell="2"></td>
      <td data-cell="3"></td>
      <td data-cell="4"></td>
      <!-- SECTION -->
      <td data-cell="5"></td>
    </tr>
  </template>

  <template id="admin-table-empty-template">
    <tr>
      <td>
        <div class="empty-state">
          <h3>No categories found.</h3>
          <p class="muted">Adjust the search term or add a new category.</p>
        </div>
      </td>
    </tr>
  </template>


  <!-- SECTION
       Dialog chrome is static. JS builds <label> field nodes
       inside [data-admin-form] - no innerHTML on the form.
  -->
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
       CATEGORY DETAIL SLIDE-OVER PANEL
       Triggered by: [data-admin-view] on each table row.
       JS (showDetail / closeDetail in category.js):
         * removes .sdp--hidden to open, adds it back to close
         * fills [id="sdp-*"] slots via textContent / dataset only
         * NEVER uses innerHTML to inject structural markup

       SOC contract:
         -œ…  All structure is here - JS only injects data values.
         -œ…  Animation is pure CSS (transform + opacity transitions).
         -œ…  Backdrop [data-sdp-close] closes the panel on click.
         -Œ  JS does NOT build or return HTML strings.

       Element IDs:
         sdp-category -> overlay <aside>
         sdp-icon     -> category initial circle
         sdp-name     -> category name heading
         sdp-id       -> identity section - ID
         sdp-slug     -> identity section - URL slug
         sdp-status   -> status badge
         sdp-items    -> metric - total products
         sdp-edit-btn -> "Edit this category" shortcut

       Future Spring MVC:
         <%@ include file="/WEB-INF/view/shared/category-detail-panel.jsp" %>
  -->
  <aside class="sdp sdp--hidden"
         id="sdp-category"
         role="dialog"
         aria-modal="true"
         aria-labelledby="sdp-name"
         data-detail-panel>

    <!-- SECTION
    <div class="sdp__backdrop" data-sdp-close></div>

    -->
    <div class="sdp__card">

      <!-- SECTION
      <button class="sdp__close-btn"
              type="button"
              data-sdp-close
              id="btn-sdp-close"
              aria-label="Close category details">
      -->
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
             width="20" height="20" aria-hidden="true">
          <path d="M18 6 6 18M6 6l12 12"/>
        </svg>
      </button>

      <!-- SECTION -->
      <div class="sdp__hero">
        <!--
          Category icon circle.
          JS: sdpIcon.textContent = record.name[0].toUpperCase();
              sdpIcon.dataset.tone = tone;  (drives CSS gradient)
        -->
        <div class="sdp__icon" id="sdp-icon" data-tone aria-hidden="true">C</div>

        <div class="sdp__hero-meta">
          <!--
            JS: document.getElementById('sdp-name').textContent = record.name;
          -->
          <h2 class="sdp__title" id="sdp-name">Category Name</h2>
          <!--
            JS: statusEl.textContent = record.status;
                statusEl.dataset.status = record.status;
          -->
          <span class="status-badge sdp__status" id="sdp-status" data-status="active">active</span>
        </div>
      </div>

      <!-- SECTION -->
      <div class="sdp__metrics">
        <div class="sdp__metric">
          <span class="sdp__metric-label">Total Products</span>
          <!--
            JS: document.getElementById('sdp-items').textContent = record.items;
          -->
          <strong class="sdp__metric-value" id="sdp-items">--</strong>
        </div>
      </div>

      <!-- SECTION -->

      <section class="sdp__section">
        <h3 class="sdp__section-title">Identity</h3>
        <dl class="sdp__fields">

          <div class="sdp__field">
            <dt>ID</dt>
            <!--
              JS: document.getElementById('sdp-id').textContent = record.id;
            -->
            <dd id="sdp-id">--</dd>
          </div>

          <div class="sdp__field">
            <dt>Category Name</dt>
            <dd id="sdp-name-field">--</dd>
          </div>

          <div class="sdp__field">
            <dt>URL Slug</dt>
            <!--
              JS: document.getElementById('sdp-slug').textContent = record.slug;
            -->
            <dd><code class="sdp__code" id="sdp-slug">--</code></dd>
          </div>

          <div class="sdp__field">
            <dt>Status</dt>
            <dd>
              <!--
                Duplicate status as plain text for screen-reader clarity.
                JS: document.getElementById('sdp-status-text').textContent = record.status;
              -->
              <span id="sdp-status-text">--</span>
            </dd>
          </div>

        </dl>
      </section>

      <!-- SECTION -->
      <section class="sdp__section">
        <h3 class="sdp__section-title">Products in this Category</h3>
        <div style="overflow-x:auto; border-radius:var(--radius-md); border:1px solid rgba(111,82,55,0.08);">
          <table class="history-table" style="width:100%;">
            <thead>
              <tr>
                <th>Product</th>
                <th>Price</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody id="sdp-category-items-body">
              <tr>
                <td colspan="3" style="text-align:center; color:var(--color-ink-500); padding:1rem;">
                  Select a category to view its products.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- SECTION -->
      <div class="sdp__actions">
        <!--
          Edit shortcut - clicking opens the CRUD edit modal.
          JS: sdpEditBtn.dataset.detailEdit = record.id;
        -->
        <button class="btn btn-primary sdp__action-btn"
                type="button"
                data-detail-edit=""
                id="sdp-edit-btn">
          <!-- SECTION -->
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
               width="16" height="16" aria-hidden="true">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
          </svg>
          Edit Category
        </button>
        <button class="btn btn-ghost sdp__action-btn"
                type="button"
                data-sdp-close>
          Close
        </button>
      </div>

    </div><!-- /sdp__card -->
  </aside>
  <!-- /CATEGORY DETAIL SLIDE-OVER PANEL -->


  <div class="toast-stack" data-admin-toast-stack id="admin-toast-stack"></div>

  <!--
    category.js is the dedicated module for this page.
    It does NOT use the shared table.js.
  -->
<script src="js/manager/category.js" defer></script>
  </body>
</html>




