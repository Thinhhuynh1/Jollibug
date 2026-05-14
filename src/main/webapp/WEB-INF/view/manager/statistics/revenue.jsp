<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Thống kê doanh thu</title>

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
  <script src="https://cdn.jsdelivr.net/npm/chart.js" defer></script>
</head>

<body data-admin-role="manager" data-admin-page="statistics-revenue">

  <div class="admin-shell admin-body">

    <jsp:include page="../layout/sidebar.jsp" />

    <main class="admin-main">
      <jsp:include page="../layout/topbar.jsp" />

      <section class="admin-dashboard">

        <!-- Period Filter -->
        <div style="display:flex; gap:0.5rem; margin-bottom:var(--space-4); flex-wrap:wrap;">
          <a href="<c:url value='/manager/statistics/revenue?period=today'/>"
             class="btn ${selectedPeriod == 'today' ? 'btn-primary' : 'btn-outline'}">Hôm nay</a>
          <a href="<c:url value='/manager/statistics/revenue?period=week'/>"
             class="btn ${selectedPeriod == 'week' ? 'btn-primary' : 'btn-outline'}">7 ngày</a>
          <a href="<c:url value='/manager/statistics/revenue?period=month'/>"
             class="btn ${selectedPeriod == 'month' ? 'btn-primary' : 'btn-outline'}">30 ngày</a>
          <a href="<c:url value='/manager/statistics/revenue?period=year'/>"
             class="btn ${selectedPeriod == 'year' ? 'btn-primary' : 'btn-outline'}">Năm nay</a>
        </div>

        <!-- Revenue Ribbon -->
        <div class="stats-ribbon" style="margin-bottom:var(--space-5);">
          <div class="stats-ribbon__item">
            <span class="muted">Hôm nay</span>
            <strong><fmt:formatNumber value="${stats.revenueToday}" pattern="#,###"/>đ</strong>
          </div>
          <div class="stats-ribbon__item">
            <span class="muted">7 ngày qua</span>
            <strong><fmt:formatNumber value="${stats.revenueWeek}" pattern="#,###"/>đ</strong>
          </div>
          <div class="stats-ribbon__item">
            <span class="muted">Tháng này</span>
            <strong><fmt:formatNumber value="${stats.revenueMonth}" pattern="#,###"/>đ</strong>
          </div>
          <div class="stats-ribbon__item">
            <span class="muted">Giá trị TB/đơn</span>
            <strong><fmt:formatNumber value="${stats.avgOrderValue}" pattern="#,###"/>đ</strong>
          </div>
        </div>

        <!-- Metric Cards -->
        <div class="metric-grid" style="margin-bottom:var(--space-5);">
          <article class="metric-card">
            <span class="muted">Tổng doanh thu (kỳ chọn)</span>
            <strong><fmt:formatNumber value="${stats.totalRevenue}" pattern="#,###"/>đ</strong>
            <div style="display: flex; align-items: center; gap: 0.5rem; margin-top: 0.5rem;">
                <span class="badge badge--${stats.revenueGrowth >= 0 ? 'success' : 'danger'}" style="font-size: 0.8rem;">
                    ${stats.revenueGrowth >= 0 ? '↑' : '↓'} <fmt:formatNumber value="${stats.revenueGrowth}" maxFractionDigits="1"/>%
                </span>
                <span class="muted" style="font-size: 0.8rem;">so với kỳ trước (<fmt:formatNumber value="${stats.prevTotalRevenue}" pattern="#,###"/>đ)</span>
            </div>
          </article>
        </div>

        <!-- Charts Row -->
        <div class="admin-dashboard__bottom" style="margin-bottom:var(--space-6);">
          <!-- Revenue Bar Chart -->
          <section class="admin-panel">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Doanh thu theo ngày</strong>
                <span class="muted">Biểu đồ doanh thu trong kỳ đã chọn.</span>
              </div>
            </div>
            <div style="position:relative; height:280px;">
              <canvas id="chart-revenue-daily" aria-label="Revenue daily bar chart" role="img"></canvas>
            </div>
          </section>

          <!-- Revenue By Month Chart -->
          <section class="admin-panel">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Doanh thu theo tháng (năm nay)</strong>
                <span class="muted">Xu hướng doanh thu các tháng.</span>
              </div>
            </div>
            <div style="position:relative; height:280px;">
              <canvas id="chart-revenue-monthly" aria-label="Revenue monthly chart" role="img"></canvas>
            </div>
          </section>
        </div>

      </section>
    </main>
  </div>

<script>
document.addEventListener('DOMContentLoaded', function() {
  // --- Daily Revenue Chart ---
  const dailyData = [
    <c:forEach var="entry" items="${stats.revenueByDay}" varStatus="st">
      { date: '${entry.date}', revenue: ${entry.revenue} }<c:if test="${!st.last}">,</c:if>
    </c:forEach>
  ];

  new Chart(document.getElementById('chart-revenue-daily'), {
    type: 'bar',
    data: {
      labels: dailyData.map(d => d.date.substring(5)),
      datasets: [{
        label: 'Doanh thu (VNĐ)',
        data: dailyData.map(d => d.revenue),
        backgroundColor: 'rgba(232, 65, 24, 0.7)',
        borderColor: 'rgba(232, 65, 24, 1)',
        borderWidth: 1,
        borderRadius: 6
      }]
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      scales: { y: { beginAtZero: true, ticks: { callback: v => v.toLocaleString('vi-VN') + 'đ' } } },
      plugins: { legend: { display: false } }
    }
  });

  // --- Monthly Revenue Chart ---
  const monthlyData = [
    <c:forEach var="entry" items="${stats.revenueByMonth}" varStatus="st">
      { month: ${entry.month}, revenue: ${entry.revenue} }<c:if test="${!st.last}">,</c:if>
    </c:forEach>
  ];
  const monthLabels = ['T1','T2','T3','T4','T5','T6','T7','T8','T9','T10','T11','T12'];
  const monthValues = new Array(12).fill(0);
  monthlyData.forEach(d => { monthValues[d.month - 1] = d.revenue; });

  new Chart(document.getElementById('chart-revenue-monthly'), {
    type: 'line',
    data: {
      labels: monthLabels,
      datasets: [{
        label: 'Doanh thu (VNĐ)',
        data: monthValues,
        borderColor: 'rgba(232, 65, 24, 1)',
        backgroundColor: 'rgba(232, 65, 24, 0.1)',
        fill: true, tension: 0.3, pointRadius: 5,
        pointBackgroundColor: 'rgba(232, 65, 24, 1)'
      }]
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      scales: { y: { beginAtZero: true, ticks: { callback: v => v.toLocaleString('vi-VN') + 'đ' } } },
      plugins: { legend: { display: false } }
    }
  });
});
</script>
</body>
</html>
