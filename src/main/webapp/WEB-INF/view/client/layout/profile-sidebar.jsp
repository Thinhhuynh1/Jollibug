<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Hồ sơ người dùng</title>
</head>
<body>
<aside class="profile-sidebar">
  <div class="profile-sidebar__avatar">U</div>
  <div class="profile-sidebar__greeting">Xin chào,</div>
  <div class="profile-sidebar__name">User Demo!</div>
  <a class="profile-sidebar__logout" href="/logout">Đăng xuất</a>

  <nav class="profile-nav" aria-label="Profile navigation">
    <a href="/orders">Đơn hàng</a>
    <a href="/address">Địa chỉ của bạn</a>
    <a href="/profile">Chi tiết tài khoản</a>
    <a href="/forgot-password">Đặt lại mật khẩu</a>
  </nav>
</aside>
<script>
  document.addEventListener('DOMContentLoaded', function () {
    var current = window.location.pathname.replace(/\/$/, '');
    document.querySelectorAll('.profile-nav a').forEach(function (link) {
      var linkPath = link.pathname.replace(/\/$/, '');
      if (linkPath === current) {
        link.classList.add('is-active');
      } else {
        link.classList.remove('is-active');
      }
    });
  });
</script>
</body>
</html>