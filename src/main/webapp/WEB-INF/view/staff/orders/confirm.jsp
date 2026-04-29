<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Xác nhận đơn hàng</title>
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
        <h1 class="profile-title">Xác nhận đơn hàng #JB-UN-1001</h1>
        <p class="profile-subtitle">Xác nhận để chuyển đơn sang trạng thái đã xác nhận.</p>

        <div class="orders-action-note">
          Sau khi xác nhận, đơn hàng sẽ xuất hiện trong tab Đã xác nhận để tiếp tục xử lý.
        </div>

        <div class="profile-actions" style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:0.5rem;">
          <a href="<c:url value='/staff/orders/unconfirmed'/>" class="btn btn-ghost">Hủy</a>
          <a href="<c:url value='/staff/orders/confirmed'/>" class="profile-submit" style="max-width:220px; text-decoration:none; display:inline-flex; align-items:center; justify-content:center;">Xác nhận đơn hàng</a>
        </div>
      </section>
    </main>
  </div>
</body>
</html>
