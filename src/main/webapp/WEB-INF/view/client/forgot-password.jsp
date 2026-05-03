<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Sign In</title>
  <meta name="description" content="Sign in to your Jollibug account to access saved addresses, order history, and faster reordering." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/global.css" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/components.css" />
    <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>
<body data-page="login">

  <!-- SHARED HEADER -->
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container" style="width: min(700px, calc(100% - 1.5rem * 2));">
          <article class="auth-panel reveal-up">
            <div class="auth-panel__content">
              <div class="page-intro" style="margin-bottom:0;">
                <h2 class="section-title">Quên mật khẩu</h2>
              </div>
              <form class="floating-grid">
                <div class="floating-field">
                  <input id="forgot-email" name="email" type="email" placeholder=" " required />
                  <label for="forgot-email">Địa chỉ email</label>
                </div>
                <button class="btn btn-primary btn-block" type="submit">Xác nhận</button>
              </form>
              <div class="card-actions">
                <a href="/register">Bạn chưa có tài khoản? Đăng ký</a>
                <a href="/login">Bạn đã có tài khoản? Đăng nhập</a>
              </div>
            </div>
          </article>


      </div>
    </section>
  </main>

  <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp" />

  </body>
</html>





