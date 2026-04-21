<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Staff - Customer Support</title>
  <meta name="description" content="Jollibug Staff portal - receive support tickets, chat with customers, and view their purchase history." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
  <link rel="stylesheet" href="css/staff.css" />
</head>

<!--
  data-admin-role="staff"    -> Spring Security path guard
  data-admin-page="support"  -> read by support.js
-->
<body data-admin-role="staff" data-admin-page="support">

  <div class="admin-shell admin-body" data-staff-support-root>

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
          <a href="/orders">
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
              <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/>
              <rect x="9" y="3" width="6" height="4" rx="1"/>
              <path d="M9 12h6M9 16h4"/>
            </svg>
            Order Management
          </a>
          <a class="is-active" href="/support">
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
            </svg>
            Customer Support
          </a>
          <span class="admin-nav__section">Quick links</span>
          <a href="client-home.html">Back to site</a>
        </nav>
      </div>
    </aside>

    <!-- SECTION -->
    <main class="admin-main">

      <!-- Topbar -->
      <div class="admin-topbar">
        <div class="admin-topbar__copy">
          <strong>Jollibug Customer Support</strong>
          <span class="muted">Handle complaints, assist customers, and view purchase history.</span>
        </div>
        <div class="admin-topbar__user">
          <span class="admin-role">Staff</span>
          <div class="admin-avatar" id="topbar-user-initials" aria-hidden="true">ST</div>
          <div class="stack" style="gap:0.15rem;">
            <strong id="topbar-user-name">Staff Member</strong>
            <span class="muted" id="topbar-user-role">Staff · Support</span>
          </div>
          <button class="btn btn-outline" type="button" id="btn-logout">Logout</button>
        </div>
      </div>

      <!-- SECTION -->
      <div class="page-intro" style="margin-bottom:var(--space-5);">
        <span class="eyebrow">Staff View</span>
        <h1 class="section-title">Customer Care Center</h1>
        <p class="lead">Select a support ticket to view the conversation thread and customer profile.</p>
      </div>

      <!-- SECTION -->
      <div class="support-shell">

        <!-- LEFT: Ticket List -->
        <div class="ticket-list-panel">
          <div class="ticket-list-panel__header">
            <strong>Support Tickets</strong>
            <span class="status-badge" data-status="pending" id="open-ticket-count">0 open</span>
          </div>
          <label class="table-search" style="margin:0.75rem 1rem;">
            <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="7"></circle>
              <path d="m20 20-3.5-3.5"></path>
            </svg>
            <input id="ticket-search" type="search" placeholder="Search tickets..." />
          </label>
          <div class="cluster" style="padding:0 1rem 0.75rem; gap: var(--space-2);">
            <button class="order-filter-pill is-active" type="button" data-ticket-filter="all" id="tf-all">All</button>
            <button class="order-filter-pill" type="button" data-ticket-filter="open" id="tf-open">Open</button>
            <button class="order-filter-pill" type="button" data-ticket-filter="resolved" id="tf-resolved">Resolved</button>
          </div>
          <div class="ticket-list" id="ticket-list" role="listbox" aria-label="Support tickets">
            <!-- Populated by support.js -->
          </div>
        </div>

        <!-- RIGHT: Chat Workspace -->
        <div class="chat-workspace" id="chat-workspace">

          <!-- Chat Panel -->
          <div class="chat-panel" id="chat-panel">
            <!-- Header filled by JS -->
            <div class="chat-panel__header" id="chat-header">
              <div class="chat-panel__avatar" id="chat-cust-avatar" aria-hidden="true">?</div>
              <div class="chat-panel__meta">
                <strong id="chat-cust-name">Select a ticket</strong>
                <span id="chat-cust-issue">-</span>
              </div>
              <span class="status-badge" id="chat-ticket-status" data-status="open" style="margin-left:auto;">open</span>
            </div>

            <!-- Quick replies -->
            <div class="quick-replies" id="quick-replies-bar">
              <button class="quick-reply-chip" type="button" data-reply="Thank you for contacting us!">ðŸ‘‹ Greet</button>
              <button class="quick-reply-chip" type="button" data-reply="I'm looking into this for you right now.">ðŸ” Investigating</button>
              <button class="quick-reply-chip" type="button" data-reply="Your refund has been processed.">ðŸ’° Refund</button>
              <button class="quick-reply-chip" type="button" data-reply="Is there anything else I can help you with?">-œ… Closing</button>
            </div>

            <!-- Messages -->
            <div class="chat-messages" id="chat-messages">
              <div class="chat-empty">
                <div class="chat-empty__icon">ðŸ’¬</div>
                <p>Select a ticket to start supporting</p>
              </div>
            </div>

            <!-- Input bar -->
            <div class="chat-input-bar">
              <input type="text" id="chat-input" placeholder="Type a message..." autocomplete="off" />
              <button class="chat-send-btn" type="button" id="chat-send-btn" aria-label="Send message">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                  <line x1="22" y1="2" x2="11" y2="13"/>
                  <polygon points="22 2 15 22 11 13 2 9 22 2"/>
                </svg>
              </button>
            </div>
          </div>

          <!-- Customer Profile Panel -->
          <div class="customer-profile-panel" id="customer-profile">
            <div class="customer-profile-panel__hero">
              <div class="customer-avatar--lg" id="profile-avatar" aria-hidden="true">?</div>
              <h3 id="profile-name" style="font-size:1.1rem; margin-bottom:0.25rem;">Select a ticket</h3>
              <span class="muted" id="profile-email">-</span>
            </div>
            <div class="customer-profile-panel__body">

              <!-- Stats row -->
              <div class="profile-stat-row">
                <div class="profile-stat">
                  <strong id="profile-orders">0</strong>
                  <span>Total Orders</span>
                </div>
                <div class="profile-stat">
                  <strong id="profile-spent">$0</strong>
                  <span>Total Spent</span>
                </div>
              </div>

              <!-- Purchase history table -->
              <div>
                <strong style="font-size:0.88rem; display:block; margin-bottom:0.65rem;">
                  Purchase History
                </strong>
                <div style="overflow-x:auto; border-radius:var(--radius-md); border:1px solid rgba(111,82,55,0.08);">
                  <table class="history-table">
                    <thead>
                      <tr>
                        <th>Order</th>
                        <th>Date</th>
                        <th>Total</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody id="profile-history-body">
                      <tr>
                        <td colspan="4" style="text-align:center; color:var(--color-ink-500); padding:1rem;">
                          No history available
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <!-- Ticket notes -->
              <div class="stack">
                <strong style="font-size:0.88rem;">Ticket Notes</strong>
                <textarea id="ticket-notes"
                          placeholder="Add internal notes about this ticket..."
                          style="min-height:6rem; font-size:0.88rem;"></textarea>
              </div>

              <div class="cluster">
                <button class="btn btn-primary" type="button" id="btn-resolve-ticket" style="flex:1;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15" aria-hidden="true">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                  Mark Resolved
                </button>
                <button class="btn btn-outline" type="button" id="btn-save-notes">
                  Save Notes
                </button>
              </div>

            </div>
          </div>

        </div><!-- /chat-workspace -->
      </div><!-- /support-shell -->

    </main>
  </div><!-- /data-staff-support-root -->


  <!-- Toast stack -->
  <div class="toast-stack" data-admin-toast-stack id="admin-toast-stack"></div>

<script src="js/staff/support.js" defer></script>
  </body>
</html>




