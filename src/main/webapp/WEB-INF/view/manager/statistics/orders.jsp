<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Thống kê đơn hàng</title>

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
  <script src="https://cdn.jsdelivr.net/npm/chart.js" defer></script>
  <style>
    /* Keyframes for spinning loader */
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
  </style>
</head>

<body data-admin-role="manager" data-admin-page="statistics-orders">

  <div class="admin-shell admin-body">

    <jsp:include page="../layout/sidebar.jsp" />

    <main class="admin-main">
      <jsp:include page="../layout/topbar.jsp" />

      <section class="admin-dashboard">

        <!-- Period Filter & Phantom Read Demo Reload Button -->
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:var(--space-4); flex-wrap:wrap; gap:0.75rem;">
          <div style="display:flex; gap:0.5rem; flex-wrap:wrap;">
            <a href="<c:url value='/manager/statistics/orders?period=today'/>"
               class="btn ${selectedPeriod == 'today' ? 'btn-primary' : 'btn-outline'}">Hôm nay</a>
            <a href="<c:url value='/manager/statistics/orders?period=week'/>"
               class="btn ${selectedPeriod == 'week' ? 'btn-primary' : 'btn-outline'}">7 ngày</a>
            <a href="<c:url value='/manager/statistics/orders?period=month'/>"
               class="btn ${selectedPeriod == 'month' ? 'btn-primary' : 'btn-outline'}">30 ngày</a>
            <a href="<c:url value='/manager/statistics/orders?period=year'/>"
               class="btn ${selectedPeriod == 'year' ? 'btn-primary' : 'btn-outline'}">Năm nay</a>
          </div>

          <!-- Nút Tải lại và demo 5s -->
          <div>
            <button id="btn-phantom-read-run" class="btn btn-outline" style="border-color:#9b59b6; color:#9b59b6; gap:0.4rem; display:flex; align-items:center; font-weight:600; min-height: 2.85rem;">
              🔄 Tải lại & Thống kê (5s)
            </button>
          </div>
        </div>

        <!-- STATS LOADING AREA (Hidden by default, shown only during demo reload) -->
        <div id="stats-loading-area" class="admin-panel" style="display: none; padding: 3rem 2rem; text-align: center; flex-direction: column; align-items: center; justify-content: center; gap: 1rem; min-height: 250px; background: #ffffff; border-radius: var(--radius-xl); box-shadow: var(--shadow-sm); border: 1px solid rgba(26, 26, 26, 0.05); margin-bottom: var(--space-5);">
          <!-- Circular spinning red loader -->
          <div class="loader-spinner" style="width: 3.5rem; height: 3.5rem; border: 4px solid rgba(230,0,0,0.1); border-top: 4px solid var(--color-red-500, #e60000); border-radius: 50%; animation: spin 0.8s linear infinite; margin-bottom: 0.5rem;"></div>
          
          <div style="font-weight: 800; font-size: 1.15rem; color: var(--color-ink-900, #1a1a1a);">Đang truy xuất thông tin thống kê đơn hàng...</div>
          <div class="muted" style="font-size: 0.9rem; color: var(--color-ink-500, #666);">Demo chờ 5 giây theo chế độ SAFE/UNSAFE trên header.</div>
          
          <!-- Countdown indicator -->
          <div style="font-size: 2.5rem; font-weight: 900; color: var(--color-red-500, #e60000); margin-top: 0.5rem;" id="loading-countdown-number">5</div>
        </div>

        <!-- Order Metric Cards -->
        <div class="metric-grid" style="margin-bottom:var(--space-5);">
          <article class="metric-card">
            <span class="muted">Tổng đơn hàng</span>
            <strong id="card-total-orders">${stats.totalOrders}</strong>
            <div style="display: flex; align-items: center; gap: 0.5rem; margin-top: 0.5rem;">
                <span class="badge badge--${stats.ordersGrowth >= 0 ? 'success' : 'danger'}" style="font-size: 0.8rem;">
                    ${stats.ordersGrowth >= 0 ? '↑' : '↓'} <fmt:formatNumber value="${stats.ordersGrowth}" maxFractionDigits="1"/>%
                </span>
                <span class="muted" style="font-size: 0.8rem;">so với kỳ trước (${stats.prevTotalOrders})</span>
            </div>
          </article>
          <article class="metric-card">
            <span class="muted">Chờ xác nhận</span>
            <strong id="card-pending">${stats.pending}</strong>
            <span class="metric-delta" data-tone="warm">PENDING</span>
          </article>
          <article class="metric-card">
            <span class="muted">Đã xác nhận</span>
            <strong id="card-confirmed">${stats.confirmed}</strong>
            <span class="metric-delta" data-tone="info">CONFIRMED</span>
          </article>
          <article class="metric-card">
            <span class="muted">Đang giao</span>
            <strong id="card-shipping">${stats.shipping}</strong>
            <span class="metric-delta" data-tone="warm">SHIPPING</span>
          </article>
          <article class="metric-card">
            <span class="muted">Đã giao</span>
            <strong id="card-delivered">${stats.delivered}</strong>
            <span class="metric-delta" data-tone="up">DELIVERED</span>
          </article>
          <article class="metric-card">
            <span class="muted">Đã hủy</span>
            <strong id="card-cancelled">${stats.cancelled}</strong>
            <span class="metric-delta" data-tone="down">CANCELLED</span>
          </article>
        </div>

        <!-- Charts Row -->
        <div class="admin-dashboard__bottom" style="margin-bottom:var(--space-6);">
          <!-- Order Status Donut -->
          <section class="admin-panel">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Phân bố trạng thái đơn hàng</strong>
                <span class="muted">Tỷ lệ đơn theo trạng thái trong kỳ.</span>
              </div>
            </div>
            <div style="position:relative; height:280px; display:grid; place-items:center;">
              <canvas id="chart-order-status" aria-label="Order status donut" role="img" style="max-height:280px;"></canvas>
            </div>
          </section>

          <!-- Orders By Day Line Chart -->
          <section class="admin-panel">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Số đơn hàng theo ngày</strong>
                <span class="muted">Xu hướng đơn hàng trong kỳ đã chọn.</span>
              </div>
            </div>
            <div style="position:relative; height:280px;">
              <canvas id="chart-orders-daily" aria-label="Orders daily line chart" role="img"></canvas>
            </div>
          </section>
        </div>

        <!-- Recent Orders Table -->
        <section class="admin-panel" style="margin-bottom:var(--space-5);">
          <div class="panel-header">
            <strong>Đơn hàng gần đây</strong>
          </div>
          <div style="overflow-x:auto;">
            <table class="admin-table" style="width:100%; border-collapse:collapse;">
              <thead>
                <tr>
                  <th style="padding:0.75rem; text-align:left; border-bottom:1px solid var(--clr-border);">Mã ĐH</th>
                  <th style="padding:0.75rem; text-align:left; border-bottom:1px solid var(--clr-border);">Khách hàng</th>
                  <th style="padding:0.75rem; text-align:right; border-bottom:1px solid var(--clr-border);">Tổng tiền</th>
                  <th style="padding:0.75rem; text-align:center; border-bottom:1px solid var(--clr-border);">Trạng thái</th>
                  <th style="padding:0.75rem; text-align:left; border-bottom:1px solid var(--clr-border);">Ngày đặt</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="order" items="${stats.recentOrders}">
                  <tr>
                    <td style="padding:0.75rem; border-bottom:1px solid var(--clr-border);">#${order.maDH}</td>
                    <td style="padding:0.75rem; border-bottom:1px solid var(--clr-border);">${order.hoTen}</td>
                    <td style="padding:0.75rem; text-align:right; border-bottom:1px solid var(--clr-border);"><fmt:formatNumber value="${order.tongTien}" pattern="#,###"/>đ</td>
                    <td style="padding:0.75rem; text-align:center; border-bottom:1px solid var(--clr-border);">
                      <span class="badge badge--${order.trangThai == 'DELIVERED' ? 'success' : order.trangThai == 'CANCELLED' ? 'danger' : order.trangThai == 'PENDING' ? 'warning' : 'info'}">${order.trangThai}</span>
                    </td>
                    <td style="padding:0.75rem; border-bottom:1px solid var(--clr-border);">${order.ngayDat}</td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </section>

      </section>
    </main>
  </div>

<script>
document.addEventListener('DOMContentLoaded', function() {
  // --- Order Status Donut ---
  const statusData = {
    <c:forEach var="entry" items="${stats.ordersByStatus}" varStatus="st">
      '${entry.key}': ${entry.value}<c:if test="${!st.last}">,</c:if>
    </c:forEach>
  };
  const statusColors = {
    'PENDING': '#f39c12', 'CONFIRMED': '#3498db',
    'SHIPPING': '#9b59b6', 'DELIVERED': '#27ae60', 'CANCELLED': '#e74c3c'
  };

  new Chart(document.getElementById('chart-order-status'), {
    type: 'doughnut',
    data: {
      labels: Object.keys(statusData),
      datasets: [{
        data: Object.values(statusData),
        backgroundColor: Object.keys(statusData).map(k => statusColors[k] || '#95a5a6'),
        borderWidth: 2
      }]
    },
    options: { responsive: true, maintainAspectRatio: false }
  });

  // --- Orders By Day Line Chart ---
  const dailyData = [
    <c:forEach var="entry" items="${stats.ordersByDay}" varStatus="st">
      { date: '${entry.date}', count: ${entry.count} }<c:if test="${!st.last}">,</c:if>
    </c:forEach>
  ];

  new Chart(document.getElementById('chart-orders-daily'), {
    type: 'line',
    data: {
      labels: dailyData.map(d => d.date.substring(5)),
      datasets: [{
        label: 'Số đơn',
        data: dailyData.map(d => d.count),
        borderColor: '#3498db',
        backgroundColor: 'rgba(52,152,219,0.1)',
        fill: true, tension: 0.3, pointRadius: 5,
        pointBackgroundColor: '#3498db'
      }]
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } },
      plugins: { legend: { display: false } }
    }
  });
});
</script>
<script src="<c:url value='/js/manager/phantom-read-stats-demo.js'/>"></script>
</body>
</html>
