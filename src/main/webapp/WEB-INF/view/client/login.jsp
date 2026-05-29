<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Đăng nhập</title>
  <meta name="description" content="Đăng nhập vào Jollibug để theo dõi đơn hàng và sử dụng các tiện ích cá nhân" />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/admin.css" />
</head>
<body data-page="login">
  <jsp:include page="layout/header.jsp" />

  <main class="page-shell">
    <section class="section">
      <div class="container" style="width: min(700px, calc(100% - 3rem));">
        <article class="auth-panel reveal-up">
          <div class="auth-panel__content">
            <div class="page-intro" style="margin-bottom: 0;">
              <h2 class="section-title">Đăng nhập vào Jollibug</h2>
            </div>

            <c:if test="${not empty error}">
              <div style="background-color: #fbfbfb; border: 1px solid #ffebed; color: #721c24; padding: 12px; border-radius: 4px; margin-bottom: 15px;">
                ${error}
              </div>
            </c:if>

            <c:if test="${not empty success}">
              <div style="background-color: #f8fff9; border: 1px solid #e1f5e5; color: #1e7e34; padding: 12px; border-radius: 4px; margin-bottom: 15px;">
                ${success}
              </div>
            </c:if>

            <form class="floating-grid" action="/login" method="POST">
              <div class="floating-field">
                <input id="login-email" name="email" type="email" placeholder=" " required />
                <label for="login-email">Email</label>
              </div>

              <div class="floating-field">
                <input id="login-password" name="password" type="password" placeholder=" " required minlength="6" />
                <label for="login-password">Mật khẩu</label>
              </div>

              <button class="btn btn-primary btn-block" type="submit">Đăng nhập</button>
            </form>

            <div class="card-actions">
              <a href="/register">Bạn chưa có tài khoản? Đăng ký</a>
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
