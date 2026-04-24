<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Staff - Order Management</title>
  <meta name="description" content="Jollibug Staff portal - view, update, and manage incoming food orders." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
  <link rel="stylesheet" href="css/staff.css" />
</head>
<body data-admin-role="staff" data-admin-page="orders">

  <div class="admin-shell admin-body" data-staff-orders-root>

    <!-- SECTION -->
    <aside class="admin-sidebar">
      <div class="admin-sidebar__inner">
        <div class="admin-brand">
          <div class="brand">
            <span class="brand__mark">JB</span>
            <span class="brand__copy">
              <span class="brand__title">Jollibug Staff</span>
              <span class="brand__tag">Operations Portal</span>
            </span>
          </div>
          <span class="admin-role">Staff</span>
          <p>Order fulfilment and customer support</p>
        </div>
        <nav class="admin-nav">
          <span class="admin-nav__section">Workspace</span>
          <a class="is-active" href="/orders">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
              <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/>
              <rect x="9" y="3" width="6" height="4" rx="1"/>
              <path d="M9 12h6M9 16h4"/>
            </svg>
            Order Management
          </a>
          <a href="/support">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
            </svg>
            Customer Support
          </a>
          <span class="admin-nav__section">Quick links</span>
          <a href="client-home.html">Back to site</a>
          <a href="index.html">Dev Portal</a>
        </nav>
      </div>
    </aside>

    <!-- SECTION -->
    <main class="admin-main">

      <!-- Topbar -->
      <div class="admin-topbar">
        <div class="admin-topbar__copy">
          <strong>Jollibug Operations Center</strong>
          <span class="muted">Live order queue - view details, update status, or remove orders.</span>
        </div>
        <div class="admin-topbar__user">
          <span class="admin-role">Staff</span>
          <div class="admin-avatar" id="topbar-user-initials" aria-hidden="true">ST</div>
          <div class="stack" style="gap:0.15rem;">
            <strong id="topbar-user-name">Staff Member</strong>
            <span class="muted" id="topbar-user-role">Staff · Orders</span>
          </div>
          <button class="btn btn-outline" type="button" id="btn-logout">Logout</button>
        </div>
      </div>

      <!-- SECTION -->
      <div class="metric-grid" style="margin-bottom:var(--space-6);">
        <article class="metric-card">
          <span class="muted">Total Orders Today</span>
          <strong id="metric-total">0</strong>
          <span class="metric-delta" data-tone="info">All orders</span>
        </article>
        <article class="metric-card">
          <span class="muted">Pending</span>
          <strong id="metric-pending">0</strong>
          <span class="metric-delta" data-tone="warm">Awaiting approval</span>
        </article>
        <article class="metric-card">
          <span class="muted">Processing</span>
          <strong id="metric-processing">0</strong>
          <span class="metric-delta" data-tone="info">In kitchen</span>
        </article>
        <article class="metric-card">
          <span class="muted">Completed</span>
          <strong id="metric-completed">0</strong>
          <span class="metric-delta" data-tone="up">Delivered today</span>
        </article>
      </div>

      <!-- SECTION -->
      <div class="order-filter-strip">
        <div class="order-filter-strip__pills" role="group" aria-label="Filter by status">
          <button class="order-filter-pill is-active" type="button" data-status-filter="all">All</button>
          <button class="order-filter-pill" type="button" data-status-filter="pending">Pending</button>
          <button class="order-filter-pill" type="button" data-status-filter="processing">Processing</button>
          <button class="order-filter-pill" type="button" data-status-filter="completed">Completed</button>
          <button class="order-filter-pill" type="button" data-status-filter="cancelled">Cancelled</button>
        </div>
        <div class="order-filter-strip__extras">
          <label class="table-search" style="min-width:14rem;">
            <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/>
            </svg>
            <input id="order-search" type="search" placeholder="Search customer or ID..." />
          </label>
          <label class="select-group" style="gap:0;">
            <select id="order-date-filter" aria-label="Filter by date">
              <option value="today">Today</option>
              <option value="week">This week</option>
              <option value="month">This month</option>
            </select>
          </label>
        </div>
      </div>

      <!-- SECTION -->
      <section class="admin-panel">
        <div class="panel-header">
          <div class="stack" style="gap:0.3rem;">
            <span class="eyebrow">Staff View</span>
            <h1 class="section-title">Order Management</h1>
            <p class="muted">Showing active and historical orders.</p>
          </div>
          <div class="panel-controls">
            <span class="muted" id="orders-result-count" style="align-self:center;font-size:0.88rem;">0 orders</span>
          </div>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table" id="orders-table">
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Customer</th>
                <th>Items</th>
                <th>Total</th>
                <th>Time</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody id="orders-table-body"></tbody>
          </table>
        </div>
      </section>

    </main>
  </div><!-- /data-staff-orders-root -->


  <!-- SECTION -->
  <template id="order-row-template">
    <tr>
      <td data-cell="id"></td>
      <td data-cell="customer"></td>
      <td data-cell="items"></td>
      <td data-cell="total"></td>
      <td data-cell="time"></td>
      <td data-cell="status"></td>
      <td data-cell="actions"></td>
    </tr>
  </template>

  <template id="order-empty-template">
    <tr>
      <td colspan="7">
        <div class="empty-state">
          <h3>No orders found</h3>
          <p class="muted">Try adjusting the filter or search term.</p>
        </div>
      </td>
    </tr>
  </template>


  <!-- SECTION
       MODAL 1 - VIEW DETAIL (read-only)
  -->
  <div class="modal admin-modal" id="modal-view" role="dialog" aria-modal="true" aria-labelledby="mv-order-id">
    <div class="modal__dialog" style="max-width:42rem;">
      <div class="modal__header">
        <div class="stack" style="gap:0.25rem;">
          <div style="display:flex;align-items:center;gap:0.75rem;">
            <h2 id="mv-order-id" style="font-size:1.25rem;">Order #-</h2>
            <span class="status-badge" id="mv-status-badge" data-status="pending">pending</span>
          </div>
          <span class="muted">Read-only order details</span>
        </div>
        <button class="btn btn-outline" type="button" id="btn-mv-close">Close</button>
      </div>

      <!-- Metric strip -->
      <div class="sdp__metrics" style="margin:1.25rem 0; padding:1rem 1.25rem; background:rgba(255,248,238,0.7); border-radius:var(--radius-lg);">
        <div class="sdp__metric">
          <span class="sdp__metric-label">Total</span>
          <strong class="sdp__metric-value" id="mv-total">$0.00</strong>
        </div>
        <div class="sdp__metric">
          <span class="sdp__metric-label">Items</span>
          <strong class="sdp__metric-value" id="mv-item-count">0</strong>
        </div>
        <div class="sdp__metric">
          <span class="sdp__metric-label">Ordered at</span>
          <strong class="sdp__metric-value" id="mv-time">-</strong>
        </div>
      </div>

      <div style="display:grid;gap:1.25rem;padding:0 0.25rem;">
        <!-- Customer -->
        <section class="sdp__section">
          <h3 class="sdp__section-title">Customer Info</h3>
          <dl class="sdp__fields">
            <div class="sdp__field"><dt>Name</dt><dd id="mv-customer-name">-</dd></div>
            <div class="sdp__field"><dt>Phone</dt><dd id="mv-customer-phone">-</dd></div>
            <div class="sdp__field"><dt>Address</dt><dd id="mv-customer-addr">-</dd></div>
          </dl>
        </section>

        <!-- Items table -->
        <section class="sdp__section">
          <h3 class="sdp__section-title">Order Items</h3>
          <div style="overflow-x:auto;border-radius:var(--radius-md);border:1px solid rgba(111,82,55,0.08);">
            <table class="history-table">
              <thead><tr><th>Item</th><th>Qty</th><th>Price</th></tr></thead>
              <tbody id="mv-items-body"></tbody>
            </table>
          </div>
        </section>

        <!-- Status stepper -->
        <section class="sdp__section">
          <h3 class="sdp__section-title">Order Progress</h3>
          <div class="order-stepper" id="mv-stepper"></div>
        </section>
      </div>
    </div>
  </div>


  <!-- SECTION
       MODAL 2 - UPDATE STATUS
  -->
  <div class="modal admin-modal" id="modal-update" role="dialog" aria-modal="true" aria-labelledby="mu-heading">
    <div class="modal__dialog" style="max-width:28rem;">
      <div class="modal__header">
        <div class="stack" style="gap:0.25rem;">
          <h2 id="mu-heading" style="font-size:1.15rem;">Update Order Status</h2>
          <div style="display:flex;align-items:center;gap:0.6rem;margin-top:0.1rem;">
            <span class="muted" id="mu-order-id" style="font-size:0.9rem;"></span>
            <span class="status-badge" id="mu-current-badge" data-status="pending">pending</span>
          </div>
        </div>
        <button class="btn btn-outline" type="button" id="btn-mu-close">Close</button>
      </div>
      <div style="padding:1.5rem;">
        <label class="field-label">
          <span>New Status</span>
          <select id="mu-status-select">
            <option value="pending">Pending</option>
            <option value="processing">Processing (Confirmed)</option>
            <option value="delivering">Out for Delivery</option>
            <option value="completed">Completed</option>
            <option value="cancelled">Cancelled</option>
          </select>
        </label>
        <div class="cluster" style="margin-top:1.25rem;justify-content:flex-end;">
          <button class="btn btn-ghost" type="button" id="btn-mu-cancel-inner" onclick="document.getElementById('modal-update').classList.remove('is-open')">Cancel</button>
          <button class="btn btn-primary" type="button" id="btn-mu-save">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15" aria-hidden="true">
              <polyline points="20 6 9 17 4 12"/>
            </svg>
            Save Status
          </button>
        </div>
      </div>
    </div>
  </div>


  <!-- SECTION
       MODAL 3 - DELETE CONFIRM
  -->
  <div class="modal admin-modal" id="modal-delete" role="dialog" aria-modal="true" aria-labelledby="md-heading">
    <div class="modal__dialog" style="max-width:26rem;">
      <div class="modal__header">
        <div class="stack" style="gap:0.2rem;">
          <h2 id="md-heading" style="font-size:1.15rem;color:var(--color-danger);">-š ï¸ Delete Order</h2>
          <span class="muted">This action cannot be undone.</span>
        </div>
      </div>
      <div style="padding:1.5rem;">
        <p style="margin-bottom:1.25rem;">Are you sure you want to permanently delete order <strong id="md-order-id"></strong>?</p>
        <div class="cluster" style="justify-content:flex-end;">
          <button class="btn btn-outline" type="button" id="btn-md-cancel">Cancel</button>
          <button class="btn btn-primary" type="button" id="btn-md-confirm"
                  style="background:linear-gradient(135deg,var(--color-danger),#ee5f42);border-color:transparent;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14" aria-hidden="true">
              <polyline points="3 6 5 6 21 6"/>
              <path d="M19 6 18 20a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
            </svg>
            Yes, Delete
          </button>
        </div>
      </div>
    </div>
  </div>


  <div class="toast-stack" data-admin-toast-stack id="admin-toast-stack"></div>
<script src="js/staff/orders.js" defer></script>
  </body>
</html>




