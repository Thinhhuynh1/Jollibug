<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Order</title>
  <meta name="description" content="Đơn hàng" />

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
        <aside class="profile-sidebar">
          <div class="profile-sidebar__avatar">U</div>
          <div class="profile-sidebar__greeting">Xin chào,</div>
          <div class="profile-sidebar__name">User Demo!</div>
          <a class="profile-sidebar__logout" href="/logout">Đăng xuất</a>

          <nav class="profile-nav" aria-label="Profile navigation">
            <a href="/orders">Đơn hàng</a>
            <a href="/address">Địa chỉ của bạn</a>
            <a class="is-active" href="/profile">Chi tiết tài khoản</a>
            <a href="/forgot-password">Đặt lại mật khẩu</a>
          </nav>
        </aside>

        <section class="profile-content">
          <div class="page-intro">
          <span class="eyebrow">Customer Area</span>
          <h1 class="section-title">My Orders</h1>
          <p class="lead">Track every stage of your delivery and browse your complete order timeline.</p>
        </div>

        <div class="client-tabs" role="tablist" aria-label="Order views">
          <button class="client-tab is-active" type="button" role="tab" data-order-tab="all">All Orders</button>
          <button class="client-tab" type="button" role="tab" data-order-tab="active">Active</button>
          <button class="client-tab" type="button" role="tab" data-order-tab="history">History</button>
        </div>

        <section class="orders-list" id="orders-list" aria-live="polite"></section>

        <section class="empty-state hidden" id="orders-empty" aria-live="polite">
          <h3>No orders in this tab</h3>
          <p class="muted">Place an order from our menu and it will appear here.</p>
          <a href="/menu" class="btn btn-primary">Browse Menu</a>
        </section>
      </div>
        </section>
      </div>
    </div>
  </main>

</body>
</html>
