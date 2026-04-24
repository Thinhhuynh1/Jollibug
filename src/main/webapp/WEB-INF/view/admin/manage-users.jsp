<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Admin â€” Manage Users</title>
  <meta name="description" content="Jollibug Super Admin â€” centralized user management for all roles: Staff, Manager, and Client." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
</head>

<!--
  data-admin-role="admin"       â†’ Spring Security: ROLE_SUPER_ADMIN
  data-admin-page="manage-users" â†’ manage-users.js render branch
-->
<body data-admin-role="admin" data-admin-page="manage-users">

  <div class="admin-shell admin-body" data-admin-table-root>

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
          <span class="admin-role">Super Admin</span>
          <p>System governance and role control</p>
        </div>

        <nav class="admin-nav">
          <span class="admin-nav__section">Workspace</span>
          <a href="/admin">Dashboard</a>
          <a class="is-active" href="/admin/users">Manage Users</a>
          <span class="admin-nav__section">Quick links</span>
          <a href="client-home.html">Back to site</a>
        </nav>
      </div>
    </aside>

    <main class="admin-main">

      <!-- Topbar -->
      <div class="admin-topbar">
        <div class="admin-topbar__copy">
          <strong>Jollibug Control Center</strong>
          <span class="muted">Centralized user management â€” all roles in one place.</span>
        </div>
        <div class="admin-topbar__user">
          <span class="admin-role">Super Admin</span>
          <div class="admin-avatar" id="topbar-user-initials" aria-hidden="true">SA</div>
          <div class="stack" style="gap:0.15rem;">
            <strong id="topbar-user-name">Super Admin</strong>
            <span class="muted" id="topbar-user-role">Super Admin</span>
          </div>
          <button class="btn btn-outline" type="button" id="btn-logout">Logout</button>
        </div>
      </div>

      <div class="metric-grid" style="margin-bottom:var(--space-6);">
        <article class="metric-card">
          <span class="muted">Total Users</span>
          <strong id="metric-total-users">0</strong>
          <span class="metric-delta" data-tone="info">All roles</span>
        </article>
        <article class="metric-card">
          <span class="muted">Managers</span>
          <strong id="metric-managers">0</strong>
          <span class="metric-delta" data-tone="info">Active</span>
        </article>
        <article class="metric-card">
          <span class="muted">Staff</span>
          <strong id="metric-staff">0</strong>
          <span class="metric-delta" data-tone="up">Operational</span>
        </article>
        <article class="metric-card">
          <span class="muted">Blocked</span>
          <strong id="metric-blocked">0</strong>
          <span class="metric-delta" data-tone="warm">Restricted accounts</span>
        </article>
      </div>

      <!-- â”€â”€ USER TABLE PANEL â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€ -->
      <section class="admin-panel">
        <div class="panel-header">
          <div class="stack" style="gap:0.3rem;">
            <span class="eyebrow">Super Admin</span>
            <h1 class="section-title">Manage Users</h1>
            <p class="muted" id="users-subtitle">All accounts across all roles.</p>
          </div>
          <div class="panel-controls">
            <!-- Status tabs -->
            <div class="order-filter-strip__pills" role="tablist" aria-label="Filter by account status" style="gap:0.35rem;">
              <button class="btn btn-primary" role="tab" type="button" data-status-tab="active" id="tab-active" style="background-color: green; color: white;">Active Accounts</button>
              <button class="btn btn-primary" role="tab" type="button" data-status-tab="blocked" id="tab-blocked" style="background-color: red; color: white;">Blocked Accounts</button>
            </div>
            <!-- Role filter dropdown -->
            <div class="select-group" style="gap:0;">
              <select id="role-filter" aria-label="Filter by role" style="padding:0.8rem 1rem;">
                <option value="all">All Roles</option>
                <option value="manager">Manager</option>
                <option value="staff">Staff</option>
                <option value="client">Client</option>
              </select>
            </div>
            <!-- Search -->
            <label class="table-search">
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/>
              </svg>
              <input id="user-search" type="search" placeholder="Search name or emailâ€¦" />
            </label>
            <button class="btn btn-primary" type="button" id="btn-add-user">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15" aria-hidden="true">
                <path d="M5 12h14M12 5v14"/>
              </svg>
              Add User
            </button>
          </div>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table" id="users-table">
            <thead>
              <tr>
                <th>User</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Joined</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody id="users-table-body">
              <!-- Populated by manage-users.js -->
            </tbody>
          </table>
        </div>


    </main>
  </div><!-- /data-admin-table-root -->


  <template id="user-row-template">
    <tr>
      <td data-cell="user"></td>
      <td data-cell="email"></td>
      <td data-cell="role"></td>
      <td data-cell="status"></td>
      <td data-cell="joined"></td>
      <td data-cell="actions"></td>
    </tr>
  </template>

  <template id="user-empty-template">
    <tr>
      <td colspan="6">
        <div class="empty-state">
          <h3>No users found.</h3>
          <p class="muted">Adjust the filter or search query, or add a new account.</p>
        </div>
      </td>
    </tr>
  </template>


 
  <aside class="sdp sdp--hidden"
         id="udp-panel"
         role="dialog"
         aria-modal="true"
         aria-labelledby="udp-name"
         data-detail-panel>

    <div class="sdp__backdrop" data-sdp-close></div>

    <div class="sdp__card">

      <!-- Close -->
      <button class="sdp__close-btn" type="button" data-sdp-close id="btn-udp-close" aria-label="Close user details">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" width="20" height="20" aria-hidden="true">
          <path d="M18 6 6 18M6 6l12 12"/>
        </svg>
      </button>

      <!-- Hero -->
      <div class="sdp__hero">
        <div class="admin-avatar admin-avatar--lg sdp__icon" id="udp-avatar" data-tone="sun" aria-hidden="true">U</div>
        <div class="sdp__hero-meta">
          <h2 class="sdp__title" id="udp-name">User Name</h2>
          <span class="status-badge sdp__status" id="udp-status-badge" data-status="active">active</span>
        </div>
      </div>

      <!-- Metrics -->
      <div class="sdp__metrics">
        <div class="sdp__metric">
          <span class="sdp__metric-label">Role</span>
          <strong class="sdp__metric-value" id="udp-role-metric">â€“</strong>
        </div>
        <div class="sdp__metric">
          <span class="sdp__metric-label">Joined</span>
          <strong class="sdp__metric-value" id="udp-joined-metric">â€“</strong>
        </div>
      </div>

      <!-- Identity -->
      <section class="sdp__section">
        <h3 class="sdp__section-title">Identity</h3>
        <dl class="sdp__fields">
          <div class="sdp__field">
            <dt>User ID</dt>
            <dd id="udp-id">â€“</dd>
          </div>
          <div class="sdp__field">
            <dt>Full Name</dt>
            <dd id="udp-fullname">â€“</dd>
          </div>
          <div class="sdp__field">
            <dt>Email</dt>
            <dd><a class="sdp__link" id="udp-email" href="#">â€“</a></dd>
          </div>
        </dl>
      </section>

      <!-- Role & Status -->
      <section class="sdp__section">
        <h3 class="sdp__section-title">Role &amp; Status</h3>
        <dl class="sdp__fields">
          <div class="sdp__field">
            <dt>Role</dt>
            <dd id="udp-role">â€“</dd>
          </div>
          <div class="sdp__field">
            <dt>Status</dt>
            <dd><span class="status-badge" id="udp-status-field" data-status="active">active</span></dd>
          </div>
          <div class="sdp__field">
            <dt>Joined</dt>
            <dd id="udp-joined">â€“</dd>
          </div>
        </dl>
      </section>

      <!-- Footer actions -->
      <div class="sdp__actions">
        <button class="btn btn-primary sdp__action-btn" type="button" id="udp-edit-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16" aria-hidden="true">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
          </svg>
          Edit User
        </button>
        <button class="btn btn-outline sdp__action-btn" type="button" id="udp-block-btn">
          Block Account
        </button>
        <button class="btn btn-ghost sdp__action-btn" type="button" data-sdp-close>Close</button>
      </div>

    </div><!-- /sdp__card -->
  </aside>


  <!--  CRUD MODAL  -->
  <div class="modal admin-modal" data-admin-modal id="admin-modal"
       aria-modal="true" role="dialog" aria-labelledby="admin-modal-title">
    <div class="modal__dialog">
      <div class="modal__header">
        <div class="stack" style="gap:0.25rem;">
          <h2 data-admin-modal-title id="admin-modal-title">Add User</h2>
          <span class="muted" data-admin-modal-copy>Fill in the user details below.</span>
        </div>
        <button class="btn btn-outline" type="button" data-admin-close-modal id="btn-close-modal">Close</button>
      </div>
      <form data-admin-form class="admin-modal__grid" id="admin-modal-form" novalidate></form>
    </div>
  </div>

  <!-- Toast stack -->
  <div class="toast-stack" data-admin-toast-stack id="admin-toast-stack"></div>

<script src="<c:url value='/js/admin/manage-users.js'/>" defer></script>
  </body>
</html>




