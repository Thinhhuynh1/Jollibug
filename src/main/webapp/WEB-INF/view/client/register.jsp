<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Đăng ký</title>
  <meta name="description" content="Tạo tài khoản Jollibug để đặt món nhanh hơn và theo dõi đơn hàng thuận tiện hơn" />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/admin.css" />
</head>
<body data-page="register">
  <jsp:include page="layout/header.jsp" />

  <main class="page-shell">
    <section class="section">
      <div class="container" style="width: min(700px, calc(100% - 3rem));">
        <article class="auth-panel">
          <div class="auth-panel__content">
            <div class="page-intro" style="margin-bottom: 0;">
              <h2 class="section-title">Đăng ký Jollibug</h2>
            </div>

            <c:if test="${not empty error}">
              <div style="background-color: #fff3f4; border: 1px solid #f0b8bf; color: #b42318; padding: 12px; border-radius: 4px; margin-bottom: 15px;">
                ${error}
              </div>
            </c:if>

            <form class="floating-grid" action="/register" method="POST">
              <div class="floating-field">
                <input id="register-name" name="hoTen" type="text" placeholder=" " required />
                <label for="register-name">Họ tên</label>
              </div>

              <div class="floating-field">
                <input id="register-email" name="email" type="email" placeholder=" " required />
                <label for="register-email">Địa chỉ email</label>
              </div>

              <div class="floating-field">
                <input id="register-phone" name="sdt" type="tel" placeholder=" " required />
                <label for="register-phone">Số điện thoại</label>
              </div>

              <div class="floating-field">
                <input id="register-password" name="password" type="password" placeholder=" " required minlength="6" />
                <label for="register-password">Mật khẩu</label>
              </div>

              <button class="btn btn-primary btn-block" type="submit">Tạo tài khoản</button>
            </form>

            <div class="card-actions">
              <a href="/login">Bạn đã có tài khoản? Đăng nhập</a>
              <a href="/forgot-password">Quên mật khẩu</a>
            </div>
          </div>
        </article>
      </div>
    </section>
  </main>

  <jsp:include page="layout/footer.jsp" />
</body>
</html>
