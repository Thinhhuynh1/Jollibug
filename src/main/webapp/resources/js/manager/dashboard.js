
(function () {
  "use strict";

  // SECTION
  const dashboardRoot = document.querySelector("[data-manager-dashboard-root]");
  if (!dashboardRoot) return;

  const role = document.body.dataset.adminRole || "manager";
  const page = document.body.dataset.adminPage || "dashboard";

  // Only handle the manager dashboard in this module.
  if (role !== "manager" || page !== "dashboard") return;

  // SECTION
  const CURRENT_USER = { name: "Khanh Vo", roleLabel: "Manager", initials: "KV" };

  // SECTION
  const STORAGE = {
    products:   "fastbite_admin_products_v1",
    categories: "fastbite_admin_categories_v1",
    orders:     "fastbite_admin_orders_v1"
  };

  // SECTION
  const ASSET_PREFIX = "/resources/shared/images/";

  const seeds = {
    products: [
      { id: "PR-101", name: "Crunch Deluxe Burger",  image: ASSET_PREFIX + "meal-burger.svg",  price: 6.9,  category: "Burger",  status: "active"      },
      { id: "PR-102", name: "Crispy Bucket Share",   image: ASSET_PREFIX + "meal-chicken.svg", price: 11.9, category: "Chicken", status: "active"      },
      { id: "PR-103", name: "Mango Burst Float",     image: ASSET_PREFIX + "meal-drink.svg",   price: 4.1,  category: "Drinks",  status: "featured"    },
      { id: "PR-104", name: "Sea Salt Fries",        image: ASSET_PREFIX + "meal-fries.svg",   price: 3.2,  category: "Sides",   status: "out-of-stock" }
    ],
    categories: [
      { id: "CT-01", name: "Burger",  slug: "burger",  items: 12, status: "active"   },
      { id: "CT-02", name: "Chicken", slug: "chicken", items: 8,  status: "active"   },
      { id: "CT-03", name: "Drinks",  slug: "drinks",  items: 6,  status: "active"   },
      { id: "CT-04", name: "Sides",   slug: "sides",   items: 5,  status: "inactive" }
    ],
    orders: [
      { id: "OD-2201", customer: "Trang Nguyen", phone: "+84 903 000 123", total: 19.5, status: "processing", channel: "Delivery", orderedAt: "2026-04-10" },
      { id: "OD-2202", customer: "An Vu",        phone: "+84 987 444 122", total: 8.4,  status: "pending",    channel: "Takeaway", orderedAt: "2026-04-10" },
      { id: "OD-2203", customer: "Gia Han",      phone: "+84 901 222 567", total: 29.8, status: "delivered",  channel: "Delivery", orderedAt: "2026-04-09" },
      { id: "OD-2204", customer: "Minh Chau",    phone: "+84 934 111 987", total: 14.3, status: "cancelled",  channel: "Dine-in",  orderedAt: "2026-04-09" }
    ]
  };

  // SECTION
  const ACTIVITY_EVENTS = [
    { title: "Combo pricing refreshed",           detail: "Family bundle updated for campaign launch."          },
    { title: "Order OD-2201 moved to processing", detail: "Kitchen queue accepted the ticket."                  },
    { title: "Sea Salt Fries flagged out of stock", detail: "Surface this clearly on both storefront and dashboard." }
  ];


  // SECTION
  // UTILITIES
  // SECTION

  /**
   * readCollection
   * Reads a JSON array from localStorage; falls back to seed data on failure.
   * @param {"products"|"categories"|"orders"} key
   * @returns {Array}
   */
  function readCollection(key) {
    try {
      return JSON.parse(localStorage.getItem(STORAGE[key]) || "null") || seeds[key];
    } catch (_) {
      return seeds[key];
    }
  }

  /**
   * formatMoney
   * Formats a number as a USD currency string.
   * @param {number} value
   * @returns {string}  e.g. "$29.80"
   */
  function formatMoney(value) {
    return new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(Number(value || 0));
  }

  /**
   * setText
   * Safely sets the textContent of an element by ID.
   * No-ops silently if the element is not found (template-safe).
   * @param {string} id     - matches an id="..." attribute in show.html
   * @param {string|number} value
   */
  function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.textContent = String(value);
  }

  /**
   * toast
   * Appends a self-removing <div class="toast"> to the stack defined in show.html.
   * @param {string} message
   * @param {"success"|"warning"|"error"} tone
   */
  function toast(message, tone = "success") {
    const stack = document.querySelector("[data-admin-toast-stack]");
    if (!stack) return;

    const node = document.createElement("div");
    node.className = "toast";
    node.dataset.tone = tone;
    node.textContent = message;
    stack.appendChild(node);

    requestAnimationFrame(() => node.classList.add("is-visible"));
    window.setTimeout(() => {
      node.classList.remove("is-visible");
      window.setTimeout(() => node.remove(), 250);
    }, 2400);
  }


  // SECTION
  // SECTION
  // Targets: #topbar-user-initials, #topbar-user-name, #topbar-user-role
  // SECTION

  function renderUserIdentity() {
    setText("topbar-user-initials", CURRENT_USER.initials);
    setText("topbar-user-name",     CURRENT_USER.name);
    setText("topbar-user-role",     CURRENT_USER.roleLabel);
  }


  // SECTION
  // SECTION
  //
  // Each setText() call targets an element already present in show.html.
  // Example from show.html:
  //   <strong id="delivered-revenue-amount">$0.00</strong>
  // SECTION

  function renderMetrics() {
    const products   = readCollection("products");
    const categories = readCollection("categories");
    const orders     = readCollection("orders");

    // SECTION

    /** Products that are currently sellable on the storefront */
    const activeProducts = products.filter(
      (p) => p.status === "active" || p.status === "featured"
    ).length;

    /** Orders awaiting action (pending + processing) */
    const openOrders = orders.filter(
      (o) => o.status === "pending" || o.status === "processing"
    ).length;

    /** Categories currently visible to customers */
    const activeCategories = categories.filter(
      (c) => c.status === "active"
    ).length;

    /**
     * Revenue from successfully completed orders only.
     * Rule: "delivered" status = confirmed revenue.
     */
    const liveRevenue = orders
      .filter((o) => o.status === "delivered")
      .reduce((sum, o) => sum + Number(o.total || 0), 0);

    /** Products promoted at top of menu */
    const featuredProducts = products.filter(
      (p) => p.status === "featured"
    ).length;

    /** Products that cannot be ordered right now */
    const outOfStockProducts = products.filter(
      (p) => p.status === "out-of-stock"
    ).length;

    /** Orders awaiting kitchen pickup */
    const pendingOrders = orders.filter(
      (o) => o.status === "pending"
    ).length;

    // SECTION
    //
    // Pattern: document.getElementById('[id in show.html]').textContent = value
    // SECTION

    setText("active-products-count",    activeProducts);
    setText("total-products-count",     products.length);
    setText("open-orders-count",        openOrders);
    setText("categories-count",         categories.length);
    setText("active-categories-count",  activeCategories);
    setText("delivered-revenue-amount", formatMoney(liveRevenue));
    setText("featured-products-count",  featuredProducts);
    setText("out-of-stock-count",       outOfStockProducts);
    setText("pending-orders-count",     pendingOrders);
  }


  // SECTION
  // SECTION
  //
  // Rule 3: The HTML structure lives in show.html as a <template>.
  // This function NEVER builds HTML strings. It only:
  //   1. Clones the inert template.
  //   2. Fills the named [data-*] slots with text.
  //   3. Appends each clone to the list container.
  //
  // Template defined in show.html:
  //   <template id="manager-activity-item-template">
  //     <article class="activity-item">
  //       <strong data-activity-title></strong>
  //       <span class="muted" data-activity-detail></span>
  //     </article>
  //   </template>
  // SECTION

  function renderActivityList() {
    const list     = document.getElementById("manager-activity-list");
    const template = document.getElementById("manager-activity-item-template");

    // Defensive: bail if either DOM anchor is missing.
    if (!list || !template) return;

    // Clear previous renders without innerHTML = ''
    list.replaceChildren();

    const fragment = document.createDocumentFragment();

    ACTIVITY_EVENTS.forEach((event) => {
      // Clone the full <template> subtree (deep clone = true).
      const clone = template.content.cloneNode(true);

      // SECTION
      clone.querySelector("[data-activity-title]").textContent  = event.title;
      clone.querySelector("[data-activity-detail]").textContent = event.detail;

      fragment.appendChild(clone);
    });

    // SECTION
    list.appendChild(fragment);
  }


  // SECTION
  // SECTION
  // SECTION

  function renderRevenueStrip() {
    // SECTION
    const revenueData = {
      today:   247.80,
      week:   1842.50,
      month:  7230.00,
      avgOrder: 18.40
    };

    setText("rev-today", formatMoney(revenueData.today));
    setText("rev-week",  formatMoney(revenueData.week));
    setText("rev-month", formatMoney(revenueData.month));
    setText("rev-avg",   formatMoney(revenueData.avgOrder));
  }


  // SECTION
  // SECTION
  // Revenue bar (last 7 days) + Order status donut
  // SECTION

  function renderCharts() {
    // SECTION
    if (typeof Chart === "undefined") {
      setTimeout(renderCharts, 300);
      return;
    }

    const chartDefaults = {
      font: { family: "'Plus Jakarta Sans', sans-serif" }
    };
    Chart.defaults.font.family = chartDefaults.font.family;

    // SECTION
    const revenueCtx = document.getElementById("chart-revenue");
    if (revenueCtx) {
      const labels  = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];
      const data    = [185.40, 220.80, 198.50, 312.00, 278.60, 389.90, 247.80];

      new Chart(revenueCtx, {
        type: "bar",
        data: {
          labels,
          datasets: [{
            label: "Revenue ($)",
            data,
            backgroundColor: data.map((_, i) =>
              i === data.length - 1
                ? "rgba(217, 68, 54, 0.85)"
                : "rgba(255, 159, 67, 0.65)"
            ),
            borderRadius: 8,
            borderSkipped: false,
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: false },
            tooltip: {
              callbacks: {
                label: ctx => ` $${ctx.parsed.y.toFixed(2)}`
              }
            }
          },
          scales: {
            x: {
              grid: { color: "rgba(111, 82, 55, 0.06)" },
              ticks: { color: "#8c7664", font: { weight: "700" } }
            },
            y: {
              grid: { color: "rgba(111, 82, 55, 0.06)" },
              ticks: {
                color: "#8c7664",
                callback: v => `$${v}`
              },
              beginAtZero: true
            }
          }
        }
      });
    }

    // SECTION
    const ordersCtx = document.getElementById("chart-orders");
    if (ordersCtx) {
      const orders = readCollection("orders");
      const counts = {
        pending:    orders.filter(o => o.status === "pending").length    || 1,
        processing: orders.filter(o => o.status === "processing").length || 1,
        delivered:  orders.filter(o => o.status === "delivered").length  || 1,
        cancelled:  orders.filter(o => o.status === "cancelled").length  || 1,
      };

      new Chart(ordersCtx, {
        type: "doughnut",
        data: {
          labels: ["Pending", "Processing", "Delivered", "Cancelled"],
          datasets: [{
            data: Object.values(counts),
            backgroundColor: [
              "rgba(255, 210, 74, 0.85)",
              "rgba(61, 122, 214, 0.8)",
              "rgba(47, 158, 102, 0.8)",
              "rgba(201, 58, 55, 0.75)",
            ],
            borderWidth: 0,
            hoverOffset: 8,
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: "65%",
          plugins: {
            legend: {
              position: "bottom",
              labels: {
                color: "#604a39",
                font: { weight: "700", size: 12 },
                padding: 16,
                boxWidth: 12,
                boxHeight: 12,
              }
            }
          }
        }
      });
    }
  }


  // SECTION
  // EVENT HANDLERS
  // SECTION

  function attachEvents() {
    // SECTION
    const btnLogout = document.getElementById("btn-logout");
    if (btnLogout) {
      btnLogout.addEventListener("click", () => {
        toast("Logout is ready for Spring Security /logout wiring.", "warning");
        // Future: window.location.href = '/logout';
      });
    }
  }


  // SECTION
  // SECTION
  // SECTION

  function init() {
    // Seed localStorage once on first visit.
    Object.keys(STORAGE).forEach((key) => {
      if (!localStorage.getItem(STORAGE[key])) {
        localStorage.setItem(STORAGE[key], JSON.stringify(seeds[key]));
      }
    });

    renderUserIdentity(); // Step 1: identity slots
    renderMetrics();      // Step 2: metric card text nodes
    renderActivityList(); // Step 3: <template> clone & append
    renderRevenueStrip(); // Step 4: revenue summary strip
    attachEvents();       // Step 5: event listeners

    // Charts need Chart.js to load first (CDN deferred)
    if (typeof Chart !== "undefined") {
      renderCharts();
    } else {
      window.addEventListener("load", renderCharts);
    }
  }

  init();
})();
