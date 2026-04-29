<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Cập nhật trạng thái đơn hàng</title>
  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/client/profile.css'/>" />
</head>
<body data-admin-role="staff" data-admin-page="orders">
  <div class="admin-shell admin-body">
    <jsp:include page="../layout/sidebar.jsp" />
    <main class="admin-main">
      <jsp:include page="../layout/topbar.jsp" />
      <section class="orders-action-card">
        <h1 class="profile-title">Cập nhật trạng thái đơn hàng</h1>
        <p class="profile-subtitle">Đơn hàng: #JB-CF-2001</p>

        <div class="orders-action-note orders-action-note--neutral">
          Giao diện này dùng để đổi trạng thái đơn hàng sau khi đã xác nhận.
        </div>

        <form class="profile-form" action="#" method="post">
          <label class="profile-field">
            <span>Trạng thái đơn hàng</span>
            <select name="orderStatus" required>
              <option value="confirmed">Đã xác nhận</option>
              <option value="preparing">Đang chuẩn bị</option>
              <option value="shipping">Đang giao</option>
              <option value="delivered">Đã giao</option>
              <option value="cancelled">Đã hủy</option>
            </select>
          </label>

          <div class="profile-actions">
            <button class="profile-submit" type="submit">Lưu trạng thái</button>
          </div>
        </form>

        <div class="profile-actions" style="margin-top:0.5rem; display:flex; justify-content:flex-end;">
          <a href="<c:url value='/staff/orders/confirmed'/>" class="btn btn-secondary">Quay lại danh sách</a>
        </div>
      </section>
    </main>
  </div>
</body>
</html>
