<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Create Account</title>
  <meta name="description" content="Create a Jollibug account to manage addresses, reorder faster, and track your orders." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/global.css" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/components.css" />
    <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>
<body data-page="register">

  <!-- SHARED HEADER -->
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container" style="width: min(700px, calc(100% - 1.5rem * 2));">


          <article class="auth-panel reveal-up">
            <div class="auth-panel__content">
              <div class="page-intro" style="margin-bottom:0;">
                <h2 class="section-title">Đăng ký Jollibug</h2>
              </div>
              <form class="floating-grid" >
                <div class="floating-field">
                  <input id="register-name" name="name" type="text" placeholder=" " required />
                  <label for="register-name">Họ tên</label>
                </div>
                <div class="floating-field">
                  <input id="register-email" name="email" type="email" placeholder=" " required />
                  <label for="register-email">Địa chỉ email</label>
                </div>
                <div class="floating-field">
                  <input id="register-phone" name="phone" type="tel" placeholder=" " required />
                  <label for="register-phone">Số điện thoại</label>
                </div>
                <div class="floating-field">
                  <input id="register-password" name="password" type="password" placeholder=" " required minlength="6" />
                  <label for="register-password">Tạo mật khẩu</label>
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





