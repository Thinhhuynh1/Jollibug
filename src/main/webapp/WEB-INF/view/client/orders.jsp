<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Đơn hàng</title>
  <meta name="description" content="Theo dõi trạng thái đơn hàng và xem lại lịch sử đặt món của bạn." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="client/css/profile.css">
</head>
<body data-page="orders">

  <jsp:include page="layout/header.jsp" />

  <main class="profile-page">
    <div class="container">
      <div class="profile-layout">
        <jsp:include page="layout/profile-sidebar.jsp" />

        <section class="profile-content">
          <div class="page-intro">
            <span class="eyebrow">Khu vực khách hàng</span>
            <h1 class="section-title">Đơn hàng của tôi</h1>
            <p class="lead">Theo dõi từng giai đoạn giao hàng và xem lại toàn bộ lịch sử đơn hàng của bạn.</p>
          </div>

          <div class="client-tabs" role="tablist" aria-label="Chế độ xem đơn hàng">
            <button class="client-tab is-active" type="button" role="tab" data-order-tab="all">Tất cả đơn</button>
            <button class="client-tab" type="button" role="tab" data-order-tab="active">Đang xử lý</button>
            <button class="client-tab" type="button" role="tab" data-order-tab="history">Lịch sử</button>
          </div>

          <section class="orders-list" id="orders-list" aria-live="polite"></section>

          <section class="empty-state hidden" id="orders-empty" aria-live="polite">
            <h3>Không có đơn hàng trong mục này</h3>
            <p class="muted">Hãy đặt món từ thực đơn để đơn hàng xuất hiện tại đây.</p>
            <a href="/menu" class="btn btn-primary">Xem thực đơn</a>
          </section>
        </section>
      </div>
    </div>
  </main>

</body>
</html>
