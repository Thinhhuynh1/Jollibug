<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Staff - Quản lý đơn hàng</title>
  <meta name="description" content="Jollibug Staff portal - quản lý đơn hàng đã xác nhận và chưa xác nhận." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
</head>

<body data-admin-role="staff" data-admin-page="orders">

  <div class="admin-shell admin-body" data-admin-table-root>

    <jsp:include page="layout/sidebar.jsp" />

    <main class="admin-main">

      <jsp:include page="layout/topbar.jsp" />

      <c:set var="activeTab" value="${empty orderTab ? 'confirmed' : orderTab}" />

      <section class="admin-panel orders-page__panel">
        <div class="panel-header">
          <div class="stack" style="gap:0.3rem;">
            <h1 class="section-title">Quản lý đơn hàng</h1>
          </div>
          <div class="panel-controls orders-page__filters">
            <div class="order-filter-strip__pills orders-page__tabs" role="tablist" aria-label="Filter by order confirmation status">
              <a href="<c:url value='/staff/orders/confirmed'/>" class="btn btn-primary" style="background-color: green; color: white;">
                Đã xác nhận
              </a>
              <a href="<c:url value='/staff/orders/unconfirmed'/>" class="btn btn-primary" style="background-color: red; color: white;">
                Chưa xác nhận
              </a>
            </div>

            <div class="select-group" style="gap:0;">
              <select id="order-status-filter" aria-label="Lọc theo trạng thái đơn hàng" style="padding:0.8rem 1rem;">
                <option value="all">Trạng thái đơn hàng</option>
                <option value="pending">Chờ xác nhận</option>
                <option value="confirmed">Đã xác nhận</option>
                <option value="preparing">Đang chuẩn bị</option>
                <option value="shipping">Đang giao</option>
                <option value="delivered">Đã giao</option>
                <option value="cancelled">Đã hủy</option>
              </select>
            </div>

            <label class="table-search">
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/>
              </svg>
              <input id="order-search" type="search" placeholder="Tìm mã đơn hoặc tên khách hàng" />
            </label>
          </div>
        </div>

        <div class="table-wrap admin-table-wrap orders-page__table-wrap">
          <table class="admin-table" id="orders-table">
            <thead>
              <tr>
                <th>Mã đơn hàng</th>
                <th>Khách hàng</th>
                <th>Trạng thái đơn</th>
                <th>Thời gian đặt</th>
                <th>Tổng tiền</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody id="orders-table-body">
              <c:choose>
                <c:when test="${activeTab == 'unconfirmed'}">
                  <tr>
                    <td>#JB-UN-1001</td>
                    <td>Nguyễn Minh Quân</td>
                    <td><span class="status-badge" data-status="featured">Chờ xác nhận</span></td>
                    <td>27/04/2026 10:20</td>
                    <td>245,000đ</td>
                    <td>
                      <a href="<c:url value='/staff/orders/detail'/>" class="btn btn-ghost" type="button">Xem chi tiết</a>
                      <a href="<c:url value='/staff/orders/confirm'/>" class="btn btn-ghost" type="button">Xác nhận đơn</a>
                    </td>
                  </tr>
                  <tr>
                    <td>#JB-UN-1002</td>
                    <td>Trần Thu Hà</td>
                    <td><span class="status-badge" data-status="featured">Chờ xác nhận</span></td>
                    <td>27/04/2026 10:35</td>
                    <td>178,000đ</td>
                    <td>
                      <a href="<c:url value='/staff/orders/detail'/>" class="btn btn-ghost" type="button">Xem chi tiết</a>
                      <a href="<c:url value='/staff/orders/confirm'/>" class="btn btn-ghost" type="button">Xác nhận đơn</a>
                    </td>
                  </tr>
                </c:when>
                <c:otherwise>
                  <tr>
                    <td>#JB-CF-2001</td>
                    <td>Lê Hoàng Phúc</td>
                    <td><span class="status-badge" data-status="active">Đã xác nhận</span></td>
                    <td>27/04/2026 09:40</td>
                    <td>312,000đ</td>
                    <td>
                      <a href="<c:url value='/staff/orders/detail'/>" class="btn btn-ghost" type="button">Xem chi tiết</a>
                      <a href="<c:url value='/staff/orders/update-status'/>" class="btn btn-ghost" type="button">Cập nhật trạng thái</a>
                    </td>
                  </tr>
                  <tr>
                    <td>#JB-CF-2002</td>
                    <td>Phạm Quỳnh Anh</td>
                    <td><span class="status-badge" data-status="active">Đang giao</span></td>
                    <td>27/04/2026 08:50</td>
                    <td>199,000đ</td>
                    <td>
                      <a href="<c:url value='/staff/orders/detail'/>" class="btn btn-ghost" type="button">Xem chi tiết</a>
                      <a href="<c:url value='/staff/orders/update-status'/>" class="btn btn-ghost" type="button">Cập nhật trạng thái</a>
                    </td>
                  </tr>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>
      </section>
    </main>
  </div>
</body>
</html>

