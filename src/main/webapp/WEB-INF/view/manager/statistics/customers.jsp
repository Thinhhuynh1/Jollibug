<%@page contentType="text/html" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
      <!DOCTYPE html>
      <html lang="vi">

      <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Jollibug | Thống kê khách hàng</title>

        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
        <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap"
          rel="stylesheet" />

        <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
        <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
        <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
      </head>

      <body data-admin-role="manager" data-admin-page="statistics-customers">

        <div class="admin-shell admin-body">

          <jsp:include page="../layout/sidebar.jsp" />

          <main class="admin-main">
            <jsp:include page="../layout/topbar.jsp" />

            <section class="admin-dashboard">

              <!-- Period Filter -->
              <div style="display:flex; gap:0.5rem; margin-bottom:var(--space-4); flex-wrap:wrap;">
                <a href="<c:url value='/manager/statistics/customers?period=today'/>"
                  class="btn ${selectedPeriod == 'today' ? 'btn-primary' : 'btn-outline'}">Hôm nay</a>
                <a href="<c:url value='/manager/statistics/customers?period=week'/>"
                  class="btn ${selectedPeriod == 'week' ? 'btn-primary' : 'btn-outline'}">7 ngày</a>
                <a href="<c:url value='/manager/statistics/customers?period=month'/>"
                  class="btn ${selectedPeriod == 'month' ? 'btn-primary' : 'btn-outline'}">30 ngày</a>
                <a href="<c:url value='/manager/statistics/customers?period=year'/>"
                  class="btn ${selectedPeriod == 'year' ? 'btn-primary' : 'btn-outline'}">Năm nay</a>
              </div>

              <!-- Customer Metric Cards -->
              <div class="metric-grid" style="margin-bottom:var(--space-5);">
                <article class="metric-card">
                  <span class="muted">Tổng khách hàng</span>
                  <strong>${stats.totalCustomers}</strong>
                  <span class="metric-delta" data-tone="info">Tất cả tài khoản CLIENT</span>
                </article>
                <article class="metric-card">
                  <span class="muted">Khách hàng đang hoạt động</span>
                  <strong>${stats.totalActiveCustomers}</strong>
                  <span class="metric-delta" data-tone="up">Đang hoạt động</span>
                </article>
                <article class="metric-card">
                  <span class="muted">Khách hàng mới</span>
                  <strong>${stats.newCustomers}</strong>
                  <span class="metric-delta" data-tone="up">Trong kỳ đã chọn</span>
                </article>
                <article class="metric-card">
                  <span class="muted">Khách hàng đặt hàng</span>
                  <strong>${stats.orderingCustomers}</strong>
                  <span class="metric-delta" data-tone="info">Có đơn trong kỳ</span>
                </article>
              </div>

              <!-- Top Customers Table -->
              <section class="admin-panel" style="margin-bottom:var(--space-5);">
                <div class="panel-header">
                  <strong>Top 10 khách hàng chi tiêu nhiều nhất</strong>
                </div>
                <div style="overflow-x:auto;">
                  <table class="admin-table" style="width:100%; border-collapse:collapse;">
                    <thead>
                      <tr>
                        <th style="padding:0.75rem; text-align:left; border-bottom:1px solid var(--clr-border);">#</th>
                        <th style="padding:0.75rem; text-align:left; border-bottom:1px solid var(--clr-border);">Họ tên
                        </th>
                        <th style="padding:0.75rem; text-align:left; border-bottom:1px solid var(--clr-border);">Email
                        </th>
                        <th style="padding:0.75rem; text-align:left; border-bottom:1px solid var(--clr-border);">SĐT
                        </th>
                        <th style="padding:0.75rem; text-align:right; border-bottom:1px solid var(--clr-border);">Tổng
                          chi tiêu</th>
                        <th style="padding:0.75rem; text-align:center; border-bottom:1px solid var(--clr-border);">Số
                          đơn</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach var="customer" items="${stats.topCustomers}" varStatus="st">
                        <tr>
                          <td style="padding:0.75rem; border-bottom:1px solid var(--clr-border);">${st.index + 1}</td>
                          <td style="padding:0.75rem; border-bottom:1px solid var(--clr-border);">${customer.hoTen}</td>
                          <td style="padding:0.75rem; border-bottom:1px solid var(--clr-border);">${customer.email}</td>
                          <td style="padding:0.75rem; border-bottom:1px solid var(--clr-border);">${customer.sdt}</td>
                          <td style="padding:0.75rem; text-align:right; border-bottom:1px solid var(--clr-border);">
                            <fmt:formatNumber value="${customer.totalSpent}" pattern="#,###" />đ
                          </td>
                          <td style="padding:0.75rem; text-align:center; border-bottom:1px solid var(--clr-border);">
                            ${customer.orderCount}</td>
                        </tr>
                      </c:forEach>
                      <c:if test="${empty stats.topCustomers}">
                        <tr>
                          <td colspan="6" style="padding:1.5rem; text-align:center; color:var(--clr-muted);">Chưa có dữ
                            liệu khách hàng.</td>
                        </tr>
                      </c:if>
                    </tbody>
                  </table>
                </div>
              </section>

            </section>
          </main>
        </div>
      </body>

      </html>