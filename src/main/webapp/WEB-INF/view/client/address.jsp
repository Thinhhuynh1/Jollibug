<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Address</title>
  <meta name="description" content="Quản lý tài khoản, mật khẩu và địa chỉ giao hàng của bạn." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="client/css/profile.css">

  
</head>
<body data-page="profile">

  <jsp:include page="layout/header.jsp" />

  <main class="profile-page">
    <div class="container container--account-wide">
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
          <div class="panel-header">
                <div class="stack" style="gap:0.25rem;">
                  <span class="eyebrow">Address Book</span>
                  <h2 class="section-title">Địa chỉ đặt hàng của bạn</h2>
                </div>
                <button class="btn btn-primary" type="button" data-open-address-modal id="btn-open-address-modal">
                    Add New Address
                </button>
              </div>

              <div class="address-grid" data-address-list id="address-list"></div>
        </section>
      </div>
    </div>
  </main>
      <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp" />
</body> 
</html>
