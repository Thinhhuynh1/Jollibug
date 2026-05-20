<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Chi tiết đơn hàng</title>
  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
</head>
<body data-admin-role="staff" data-admin-page="orders">
  <div class="admin-shell admin-body">
    <jsp:include page="../layout/sidebar.jsp" />
    <main class="admin-main">
      <jsp:include page="../layout/topbar.jsp" />
      <section class="orders-detail-card">
        <h1 class="profile-title" style="margin-bottom:0.35rem;">Chi tiết đơn hàng #JB-CF-2001</h1>


        <section class="orders-section">
          <h2 class="orders-section__title">Thông tin người đặt</h2>
          <div class="orders-info-grid">
            <div class="orders-info-card">
              <span class="orders-info-label">Họ và tên</span>
              <strong>Nguyễn Minh Quân</strong>
            </div>
            <div class="orders-info-card">
              <span class="orders-info-label">Số điện thoại</span>
              <strong>0901234567</strong>
            </div>
            <div class="orders-info-card">
              <span class="orders-info-label">Email</span>
              <strong>quan.nguyen@jollibug.vn</strong>
            </div>
            <div class="orders-info-card">
              <span class="orders-info-label">Địa chỉ giao hàng</span>
              <strong>123 Nguyễn Trãi, Quận 1, TP. Hồ Chí Minh</strong>
            </div>
          </div>
        </section>

        <section class="orders-section">
          <h2 class="orders-section__title">Thông tin đơn hàng</h2>
          <div class="orders-summary">
            <div class="orders-summary-card">
              <span class="orders-info-label">Trạng thái</span>
              <strong><span class="status-badge" data-status="active">Đã xác nhận</span></strong>
            </div>
            <div class="orders-summary-card">
              <span class="orders-info-label">Ngày đặt</span>
              <strong>27/04/2026 09:40</strong>
            </div>
            <div class="orders-summary-card">
              <span class="orders-info-label">Tổng tiền</span>
              <strong>250,000đ</strong>
            </div>
          </div>
        </section>

        <section class="orders-section">
          <h2 class="orders-section__title">Các món đã đặt</h2>
          <div class="table-wrap admin-table-wrap">
            <table class="admin-table">
              <thead>
                <tr>
                  <th>Món</th>
                  <th>Số lượng</th>
                  <th>Đơn giá</th>
                  <th>Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr><td>Combo Gà Rán</td><td>2</td><td>89,000đ</td><td>178,000đ</td></tr>
                <tr><td>Khoai tây chiên</td><td>1</td><td>35,000đ</td><td>35,000đ</td></tr>
                <tr><td>Pepsi</td><td>2</td><td>18,000đ</td><td>36,000đ</td></tr>
              </tbody>
            </table>
          </div>
        </section>

        <div class="profile-actions" style="margin-top:0.5rem; display:flex; justify-content:flex-end;">
          <a href="<c:url value='/staff/orders/confirmed'/>" class="btn btn-secondary">Quay lại danh sách</a>
        </div>
      </section>
    </main>
  </div>
</body>
</html>
