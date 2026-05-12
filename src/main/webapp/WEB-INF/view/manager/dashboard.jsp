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
    <jsp:include page="layout/sidebar.jsp" />

    <!-- SECTION -->
    <main class="admin-main">

      <!-- Top bar -->
      <jsp:include page="layout/topbar.jsp" />

      <!-- SECTION -->
      <section class="admin-dashboard">

        <!-- SECTION -->
        <div class="stats-ribbon" style="margin-bottom:var(--space-5);">
          <a class="stats-ribbon__item" style="text-decoration:none;color:inherit;cursor:pointer;" href="<c:url value='/manager/statistics/revenue?period=today'/>">
            <span class="muted">Doanh thu hôm nay</span>
            <strong id="rev-today">0</strong>
          </a>
          <a class="stats-ribbon__item" style="text-decoration:none;color:inherit;cursor:pointer;" href="<c:url value='/manager/statistics/revenue?period=week'/>">
            <span class="muted">Doanh thu 7 ngày</span>
            <strong id="rev-week">0</strong>
          </a>
          <a class="stats-ribbon__item" style="text-decoration:none;color:inherit;cursor:pointer;" href="<c:url value='/manager/statistics/revenue?period=month'/>">
            <span class="muted">Doanh thu 30 ngày</span>
            <strong id="rev-month">0</strong>
          </a>
          <a class="stats-ribbon__item" style="text-decoration:none;color:inherit;cursor:pointer;" href="<c:url value='/manager/statistics/revenue'/>">
            <span class="muted">Giá trị đơn trung bình</span>
            <strong id="rev-avg">0</strong>
          </a>
        </div>

        <!-- SECTION -->
        <div class="metric-grid">

          <article class="metric-card">
              <span class="muted">Sản phẩm đang hoạt động</span>
              <strong id="active-products-count">0</strong>
            <span class="metric-delta" data-tone="up">
              <span id="total-products-count">0</span> total products
            </span>
          </article>

          <a href="<c:url value='/manager/statistics/orders?period=today'/>" style="text-decoration:none; color:inherit; display:block;">
            <article class="metric-card">
            <span class="muted">Đơn hàng cần xử lý</span>
            <strong id="open-orders-count">0</strong>
              <span class="metric-delta" data-tone="warm">Rush window live</span>
            </article>
          </a>

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
          <a href="<c:url value='/manager/statistics/orders?period=today'/>" style="text-decoration:none; color:inherit; display:block;">
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
          </a>

        </div><!-- /chart-row -->

        <!-- SECTION -->
        <div class="admin-dashboard__bottom">

          <!-- Operations Snapshot panel (link to customers stats) -->
          <a href="<c:url value='/manager/statistics/customers?period=today'/>" style="text-decoration:none; color:inherit; display:block;">
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
          </a>

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




