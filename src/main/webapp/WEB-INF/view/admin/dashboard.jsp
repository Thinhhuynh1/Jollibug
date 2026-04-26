<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Super Admin Dashboard</title>
  <meta name="description" content="Jollibug Super Admin - govern role access, branch leadership, and system health." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />

</head>

<!--
  data-admin-role="admin" -> Spring Security enforces ROLE_SUPER_ADMIN
  data-admin-page="dashboard"   -> dashboard.js picks the correct render branch
-->
<body data-admin-role="admin" data-admin-page="dashboard">

  <div class="admin-shell admin-body" data-super-dashboard-root>

    <jsp:include page="layout/sidebar.jsp" />

    <!-- MAIN CONTENT -->
    <main class="admin-main">

      
      <jsp:include page="layout/topbar.jsp" />

      <!-- DASHBOARD SECTION -->
      <section class="admin-dashboard">

           <!-- METRIC CARDS
             Each <strong id="..."> is an injection target.
             JS: document.getElementById('active-staffs-count').textContent = n;
           -->
        <div class="metric-grid">

          <article class="metric-card">
            <span class="muted">Active Staffs</span>
            <strong id="active-staffs-count">0</strong>
            <span class="metric-delta" data-tone="up">
              <span id="total-staffs-count">0</span> total members
            </span>
          </article>

          <article class="metric-card">
            <span class="muted">Managers</span>
            <strong id="managers-count">0</strong>
            <span class="metric-delta" data-tone="info">
              <span id="active-managers-count">0</span> active
            </span>
          </article>

          <article class="metric-card">
            <span class="muted">Managed Hubs</span>
            <strong id="managed-hubs-count">0</strong>
            <span class="metric-delta" data-tone="warm">3 live regions</span>
          </article>

          <article class="metric-card">
            <span class="muted">Policy Flags</span>
            <!-- Static value - not derived from data; JS leaves this untouched -->
            <strong id="policy-flags-count">02</strong>
            <span class="metric-delta" data-tone="warm">Review this week</span>
          </article>

        </div><!-- /metric-grid -->

        <!-- BOTTOM PANELS -->
        <div class="admin-dashboard__bottom">

          <!-- Leadership pulse panel -->
          <section class="admin-panel">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Leadership pulse</strong>
                <span class="muted">Quick scan of manager readiness and staffing.</span>
              </div>
            </div>
            <div class="mini-stat-grid">
              <article class="mini-stat">
                <span class="muted">Active Managers</span>
                <strong id="leadership-active-managers">0</strong>
              </article>
              <article class="mini-stat">
                <span class="muted">Inactive Staffs</span>
                <strong id="leadership-inactive-staffs">0</strong>
              </article>
              <article class="mini-stat">
                <span class="muted">Coverage Areas</span>
                <strong id="leadership-coverage-areas">0</strong>
              </article>
            </div>
          </section>

          <!-- Governance activity panel -->
          <section class="admin-activity">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Recent governance actions</strong>
                <span class="muted">A narrative block for future audit events.</span>
              </div>
            </div>
            <!--
              Empty container - JS clones #admin-activity-item-template
              for each event and appends clones here.
            -->
            <div class="activity-list" id="admin-activity-list">
              <article class="activity-item">
                <strong>Manager role review initialized</strong>
                <span class="muted">Fallback view: assign or revoke manager roles from Manage Managers.</span>
              </article>
              <article class="activity-item">
                <strong>Staff onboarding policy synced</strong>
                <span class="muted">Fallback view: onboarding summary is shown even when JS is disabled.</span>
              </article>
            </div>
          </section>

        </div><!-- /admin-dashboard__bottom -->

      </section><!-- /admin-dashboard -->
    </main>
  </div><!-- /data-super-dashboard-root -->


  <!-- TEMPLATES -->

  <!--
    Governance activity item template.
    JS usage:
      const tpl   = document.getElementById('admin-activity-item-template');
      const clone = tpl.content.cloneNode(true);
      clone.querySelector('[data-activity-title]').textContent  = event.title;
      clone.querySelector('[data-activity-detail]').textContent = event.detail;
      document.getElementById('admin-activity-list').appendChild(clone);
  -->
  <template id="admin-activity-item-template">
    <article class="activity-item">
      <strong data-activity-title></strong>
      <span class="muted" data-activity-detail></span>
    </article>
  </template>


  <!-- MODAL - structural chrome only -->
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

  <div class="toast-stack" data-admin-toast-stack id="admin-toast-stack"></div>

  <!--
    Path resolution:
      Spring MVC: <mvc:resources mapping="/resources/**" location="/resources/" />
      Served from: webapp/resources/admin/js/dashboard.js
      URL in browser: /[context-root]/resources/admin/js/dashboard.js
  -->
<script src="js/admin/dashboard.js" defer></script>
  </body>
</html>




