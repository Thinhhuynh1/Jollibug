<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Manager Dashboard</title>
  <meta name="description" content="Jollibug Manager Dashboard - Tổng quan kinh doanh cửa hàng." />

  <!-- Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
  <script src="https://cdn.jsdelivr.net/npm/chart.js" defer></script>
</head>

<body data-admin-role="manager" data-admin-page="dashboard">

  <div class="admin-shell admin-body">

    <jsp:include page="layout/sidebar.jsp" />

    <main class="admin-main">
      <jsp:include page="layout/topbar.jsp" />

      <section class="admin-dashboard">

        <!-- SECTION: QUICK STATS -->
        <div class="stats-ribbon" style="margin-bottom:var(--space-5);">
          <a class="stats-ribbon__item" style="text-decoration:none;color:inherit;cursor:pointer;" href="<c:url value='/manager/statistics/revenue?period=today'/>">
            <span class="muted">Doanh thu hôm nay</span>
            <strong><fmt:formatNumber value="${revenueStats.revenueToday}" pattern="#,###"/>đ</strong>
          </a>
          <a class="stats-ribbon__item" style="text-decoration:none;color:inherit;cursor:pointer;" href="<c:url value='/manager/statistics/orders?period=month'/>">
            <span class="muted">Đơn hàng (tháng)</span>
            <strong>${orderStats.totalOrders}</strong>
          </a>
          <a class="stats-ribbon__item" style="text-decoration:none;color:inherit;cursor:pointer;" href="<c:url value='/manager/statistics/customers?period=month'/>">
            <span class="muted">Khách hàng mới (tháng)</span>
            <strong>${customerStats.newCustomers}</strong>
          </a>
          <a class="stats-ribbon__item" style="text-decoration:none;color:inherit;cursor:pointer;" href="<c:url value='/manager/statistics/orders'/>">
            <span class="muted">Chờ xác nhận</span>
            <strong style="color: var(--clr-warm-500);">${orderStats.pending}</strong>
          </a>
        </div>

        <!-- SECTION: MAIN METRICS -->
        <div class="metric-grid" style="margin-bottom:var(--space-5);">
          <article class="metric-card">
            <span class="muted">Doanh thu (tháng)</span>
            <strong><fmt:formatNumber value="${revenueStats.totalRevenue}" pattern="#,###"/>đ</strong>
            <span class="metric-delta" data-tone="${revenueStats.revenueGrowth >= 0 ? 'up' : 'down'}">
              ${revenueStats.revenueGrowth >= 0 ? '↑' : '↓'} <fmt:formatNumber value="${revenueStats.revenueGrowth}" maxFractionDigits="1"/>%
            </span>
          </article>

          <article class="metric-card">
            <span class="muted">Giá trị TB đơn hàng</span>
            <strong><fmt:formatNumber value="${orderStats.avgOrderValue}" pattern="#,###"/>đ</strong>
            <span class="metric-delta" data-tone="info">Tính trên đơn đã giao</span>
          </article>

          <article class="metric-card">
            <span class="muted">Khách hàng mới (tháng)</span>
            <strong>${customerStats.newCustomers}</strong>
            <span class="metric-delta" data-tone="${customerStats.customersGrowth >= 0 ? 'up' : 'down'}">
              ${customerStats.customersGrowth >= 0 ? '↑' : '↓'} <fmt:formatNumber value="${customerStats.customersGrowth}" maxFractionDigits="1"/>%
            </span>
          </article>

          <article class="metric-card">
            <span class="muted">Đơn đã giao (tháng)</span>
            <strong>${orderStats.delivered}</strong>
            <span class="metric-delta" data-tone="${orderStats.ordersGrowth >= 0 ? 'up' : 'down'}">
              ${orderStats.ordersGrowth >= 0 ? '↑' : '↓'} <fmt:formatNumber value="${orderStats.ordersGrowth}" maxFractionDigits="1"/>%
            </span>
          </article>
        </div>

        <!-- SECTION: CHARTS -->
        <div class="admin-dashboard__bottom" style="margin-bottom:var(--space-6);">
          <!-- Revenue trend -->
          <section class="admin-panel">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Biểu đồ doanh thu 30 ngày</strong>
                <span class="muted">Xu hướng doanh thu theo ngày.</span>
              </div>
              <a href="<c:url value='/manager/statistics/revenue'/>" class="btn btn-outline btn-sm">Chi tiết</a>
            </div>
            <div style="position:relative; height:240px;">
              <canvas id="chart-revenue-main" aria-label="Revenue main chart" role="img"></canvas>
            </div>
          </section>

          <!-- Order Status Distribution -->
          <section class="admin-panel">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Trạng thái đơn hàng</strong>
                <span class="muted">Phân bố đơn hàng trong tháng.</span>
              </div>
              <a href="<c:url value='/manager/statistics/orders'/>" class="btn btn-outline btn-sm">Chi tiết</a>
            </div>
            <div style="position:relative; height:240px; display:grid; place-items:center;">
              <canvas id="chart-orders-main" aria-label="Order status main chart" role="img" style="max-height:240px;"></canvas>
            </div>
          </section>
        </div>

        <!-- SECTION: TABLES -->
        <div class="admin-dashboard__bottom">
          <!-- Recent Orders -->
          <section class="admin-panel">
            <div class="panel-header">
              <strong>Đơn hàng mới nhất</strong>
              <a href="<c:url value='/manager/statistics/orders'/>" class="btn btn-outline btn-sm">Xem tất cả</a>
            </div>
            <div style="overflow-x:auto;">
              <table class="admin-table" style="width:100%; border-collapse:collapse; font-size: 0.9rem;">
                <thead>
                  <tr>
                    <th>Mã ĐH</th>
                    <th>Khách hàng</th>
                    <th style="text-align:right;">Tổng tiền</th>
                    <th style="text-align:center;">Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="order" items="${orderStats.recentOrders}" end="4">
                    <tr>
                      <td>#${order.maDH}</td>
                      <td>${order.hoTen}</td>
                      <td style="text-align:right;"><fmt:formatNumber value="${order.tongTien}" pattern="#,###"/>đ</td>
                      <td style="text-align:center;">
                        <span class="badge badge--${order.trangThai == 'DELIVERED' ? 'success' : order.trangThai == 'CANCELLED' ? 'danger' : order.trangThai == 'PENDING' ? 'warning' : 'info'}">${order.trangThai}</span>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </section>

          <!-- Top Customers -->
          <section class="admin-panel">
            <div class="panel-header">
              <strong>Top khách hàng chi tiêu</strong>
              <a href="<c:url value='/manager/statistics/customers'/>" class="btn btn-outline btn-sm">Chi tiết</a>
            </div>
            <div style="overflow-x:auto;">
              <table class="admin-table" style="width:100%; border-collapse:collapse; font-size: 0.9rem;">
                <thead>
                  <tr>
                    <th>Tên</th>
                    <th style="text-align:center;">Đơn hàng</th>
                    <th style="text-align:right;">Tổng chi tiêu</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="cust" items="${customerStats.topCustomers}" end="4">
                    <tr>
                      <td>${cust.hoTen}</td>
                      <td style="text-align:center;">${cust.orderCount}</td>
                      <td style="text-align:right;"><fmt:formatNumber value="${cust.totalSpent}" pattern="#,###"/>đ</td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </section>
        </div>

      </section>
    </main>
  </div>

  <script>
    document.addEventListener('DOMContentLoaded', function() {
      // --- Revenue Chart ---
      const revData = [
        <c:forEach var="entry" items="${revenueStats.revenueByDay}" varStatus="st">
          { date: '${entry.date}', rev: ${entry.revenue} }<c:if test="${!st.last}">,</c:if>
        </c:forEach>
      ];

      new Chart(document.getElementById('chart-revenue-main'), {
        type: 'line',
        data: {
          labels: revData.map(d => d.date.substring(5)),
          datasets: [{
            label: 'Doanh thu (VNĐ)',
            data: revData.map(d => d.rev),
            borderColor: '#e84118',
            backgroundColor: 'rgba(232, 65, 24, 0.1)',
            fill: true,
            tension: 0.4,
            pointRadius: 4,
            pointBackgroundColor: '#e84118'
          }]
        },
        options: {
          responsive: true, maintainAspectRatio: false,
          scales: {
            y: {
              beginAtZero: true,
              ticks: { callback: v => v.toLocaleString('vi-VN') + 'đ' }
            }
          },
          plugins: { legend: { display: false } }
        }
      });

      // --- Order Status Donut ---
      const orderStatusData = {
        <c:forEach var="entry" items="${orderStats.ordersByStatus}" varStatus="st">
          '${entry.key}': ${entry.value}<c:if test="${!st.last}">,</c:if>
        </c:forEach>
      };
      const statusColors = {
        'PENDING': '#f39c12', 'CONFIRMED': '#3498db',
        'SHIPPING': '#9b59b6', 'DELIVERED': '#27ae60', 'CANCELLED': '#e74c3c'
      };

      new Chart(document.getElementById('chart-orders-main'), {
        type: 'doughnut',
        data: {
          labels: Object.keys(orderStatusData),
          datasets: [{
            data: Object.values(orderStatusData),
            backgroundColor: Object.keys(orderStatusData).map(k => statusColors[k] || '#95a5a6'),
            borderWidth: 0,
            hoverOffset: 10
          }]
        },
        options: {
          responsive: true, maintainAspectRatio: false,
          cutout: '70%',
          plugins: {
            legend: { position: 'bottom', labels: { boxWidth: 12, padding: 15 } }
          }
        }
      });
    });
  </script>

  <!-- We keep this but it might need to be simplified if it conflicts with our inline scripts -->
  <%-- <script src="js/manager/dashboard.js" defer></script> --%>
</body>
</html>




