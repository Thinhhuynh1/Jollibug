<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Manager Dashboard</title>
  <meta name="description" content="Jollibug Manager Dashboard - monitor menu health, open orders, and delivered revenue in real time." />

  <!-- Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <!--
    CSS paths are relative to the server context root, e.g. /Jollibug/
    Spring MVC maps /resources/** -> webapp/resources/ via <mvc:resources>
  -->
  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
  <script src="https://cdn.jsdelivr.net/npm/chart.js" defer></script>
</head>

<!--
  data-admin-role  -> read by dashboard.js to identify which user data to load
  data-admin-page  -> read by dashboard.js to know which render branch to enter
-->
<body data-admin-role="manager" data-admin-page="dashboard">

  <!-- SECTION
       LAYOUT ROOT - detected by dashboard.js as the
       static dashboard root (replaces data-admin-shell)
  -->
  <div class="admin-shell admin-body" data-manager-dashboard-root>

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
          <a class="is-active" href="/admin">Dashboard</a>
          <a href="/categories">Manage Categories</a>
          <a href="/products">Manage Products</a>
          <a href="manager-orders.html">Manage Orders</a>
          <span class="admin-nav__section">Quick links</span>
          <a href="index.html">Back to site</a>
        </nav>
      </div>
    </aside>

    <!-- SECTION -->
    <main class="admin-main">

      <!-- Top bar -->
      <div class="admin-topbar">
        <div class="admin-topbar__copy">
          <strong>Jollibug Control Center</strong>
          <span class="muted">Live admin-ready UI with modal CRUD and role-based navigation.</span>
        </div>
        <div class="admin-topbar__user">
          <span class="admin-role">Manager</span>
          <!--
            JS will populate initials + name at runtime.
            These IDs are the injection targets.
          -->
          <div class="admin-avatar" id="topbar-user-initials">--</div>
          <div class="stack" style="gap:0.15rem;">
            <strong id="topbar-user-name">Loading...</strong>
            <span class="muted" id="topbar-user-role">Manager</span>
          </div>
          <button class="btn btn-outline" type="button" data-admin-logout id="btn-logout">Logout</button>
        </div>
      </div>

      <!-- SECTION -->
      <section class="admin-dashboard">

        <!-- SECTION -->
        <div class="page-intro">
          <span class="eyebrow">Manager View</span>
          <h1 class="section-title">Run daily menu and order operations with confidence.</h1>
          <p class="lead">A focused dashboard for category hygiene, product availability, and live service flow.</p>
        </div>

        <!-- SECTION -->
        <div class="stats-ribbon" style="margin-bottom:var(--space-5);">
          <div class="stats-ribbon__item">
            <span class="muted">Today's Revenue</span>
            <strong id="rev-today">$0.00</strong>
          </div>
          <div class="stats-ribbon__item">
            <span class="muted">This Week</span>
            <strong id="rev-week">$0.00</strong>
          </div>
          <div class="stats-ribbon__item">
            <span class="muted">This Month</span>
            <strong id="rev-month">$0.00</strong>
          </div>
          <div class="stats-ribbon__item">
            <span class="muted">Avg Order Value</span>
            <strong id="rev-avg">$0.00</strong>
          </div>
        </div>

        <!-- SECTION -->
        <div class="metric-grid">

          <article class="metric-card">
            <span class="muted">Active Products</span>
            <strong id="active-products-count">0</strong>
            <span class="metric-delta" data-tone="up">
              <span id="total-products-count">0</span> total products
            </span>
          </article>

          <article class="metric-card">
            <span class="muted">Open Orders</span>
            <strong id="open-orders-count">0</strong>
            <span class="metric-delta" data-tone="warm">Rush window live</span>
          </article>

          <article class="metric-card">
            <span class="muted">Categories</span>
            <strong id="categories-count">0</strong>
            <span class="metric-delta" data-tone="info">
              <span id="active-categories-count">0</span> active
            </span>
          </article>

          <article class="metric-card">
            <span class="muted">Delivered Revenue</span>
            <!--
              Rule 2 example:
              JS: document.getElementById('delivered-revenue-amount').textContent = '$' + liveRevenue;
            -->
            <strong id="delivered-revenue-amount">$0.00</strong>
            <span class="metric-delta" data-tone="up">Last 48 hours</span>
          </article>

        </div><!-- /metric-grid -->

        <!-- SECTION -->
        <div class="admin-dashboard__bottom" style="margin-bottom:var(--space-6);">

          <!-- Revenue Bar Chart -->
          <section class="admin-panel">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Revenue - Last 7 Days</strong>
                <span class="muted">Daily delivered revenue trend.</span>
              </div>
            </div>
            <div style="position:relative; height:220px;">
              <canvas id="chart-revenue" aria-label="Revenue bar chart" role="img"></canvas>
            </div>
          </section>

          <!-- Order Status Donut -->
          <section class="admin-panel">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Order Status Breakdown</strong>
                <span class="muted">Distribution of today's orders by status.</span>
              </div>
            </div>
            <div style="position:relative; height:220px; display:grid; place-items:center;">
              <canvas id="chart-orders" aria-label="Order status donut chart" role="img" style="max-height:220px;"></canvas>
            </div>
          </section>

        </div><!-- /chart-row -->

        <!-- SECTION -->
        <div class="admin-dashboard__bottom">

          <!-- Operations Snapshot panel -->
          <section class="admin-panel">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Operations snapshot</strong>
                <span class="muted">A lightweight overview of today's service health.</span>
              </div>
            </div>
            <div class="mini-stat-grid">
              <article class="mini-stat">
                <span class="muted">Featured Menu Items</span>
                <strong id="featured-products-count">0</strong>
              </article>
              <article class="mini-stat">
                <span class="muted">Out of Stock</span>
                <strong id="out-of-stock-count">0</strong>
              </article>
              <article class="mini-stat">
                <span class="muted">Pending Orders</span>
                <strong id="pending-orders-count">0</strong>
              </article>
            </div>
          </section>

          <!-- Recent Activity panel -->
          <section class="admin-activity">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Recent team actions</strong>
                <span class="muted">Useful placeholders for future real activity events.</span>
              </div>
            </div>
            <div class="activity-list" id="manager-activity-list"></div>
          </section>

        </div><!-- /admin-dashboard__bottom -->

      </section><!-- /admin-dashboard -->
    </main>
  </div><!-- /data-manager-dashboard-root -->


  <!-- SECTION
       TEMPLATES
       Rule 3: Never build list HTML in JS strings.
       Clone these, fill the [data-*] slots, append.
  -->

  <!--
    Activity-item template.
    JS usage:
      const tpl = document.getElementById('manager-activity-item-template');
      const clone = tpl.content.cloneNode(true);
      clone.querySelector('[data-activity-title]').textContent = event.title;
      clone.querySelector('[data-activity-detail]').textContent = event.detail;
      document.getElementById('manager-activity-list').appendChild(clone);
  -->
  <template id="manager-activity-item-template">
    <article class="activity-item">
      <strong data-activity-title></strong>
      <span class="muted" data-activity-detail></span>
    </article>
  </template>


  <!-- SECTION
       MODAL - structure is static; only form fields
       inside [data-admin-form] are written by JS
  -->
  <div class="modal admin-modal" data-admin-modal id="admin-modal" aria-modal="true" role="dialog" aria-labelledby="admin-modal-title">
    <div class="modal__dialog">
      <div class="modal__header">
        <div class="stack" style="gap:0.25rem;">
          <h2 data-admin-modal-title id="admin-modal-title">Modal</h2>
          <span class="muted" data-admin-modal-copy>Update details and save instantly.</span>
        </div>
        <button class="btn btn-outline" type="button" data-admin-close-modal id="btn-close-modal">Close</button>
      </div>
      <!--
        JS writes <label> field markup inside this form only.
        The surrounding <form> + <div class="modal__dialog"> are static.
      -->
      <form data-admin-form class="admin-modal__grid" id="admin-modal-form" novalidate></form>
    </div>
  </div>

  <!-- SECTION
  -->
  <div class="toast-stack" data-admin-toast-stack id="admin-toast-stack"></div>

  <!--
    SCRIPT REFERENCE
    In a Spring MVC project, the <mvc:resources> mapping exposes:
      webapp/resources/ -> context path /resources/

    If your context root is /Jollibug/, the full URL is:
      http://localhost:8080/Jollibug/resources/manager/js/dashboard.js

    Use a root-relative path so it works from any view depth:
  -->
<script src="js/manager/dashboard.js" defer></script>
  </body>
</html>




