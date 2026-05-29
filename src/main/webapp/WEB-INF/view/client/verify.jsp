<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Xác thực tài khoản</title>
  <meta name="description" content="Nhập mã xác thực để hoàn tất đăng ký tài khoản Jollibug" />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/admin.css" />
</head>
<body data-page="verify">
  <jsp:include page="layout/header.jsp" />

  <main class="page-shell">
    <section class="section">
      <div class="container" style="width: min(700px, calc(100% - 3rem));">
        <article class="auth-panel reveal-up">
          <div class="auth-panel__content">
            <div class="page-intro" style="margin-bottom: 0;">
              <h2 class="section-title">Nhập mã xác thực</h2>
              <p style="color: #666; font-size: 0.95rem; margin-top: 10px;">
                Vui lòng nhập mã đã được gửi đến email của bạn để tiếp tục
              </p>
            </div>

            <c:if test="${not empty error}">
              <div style="background-color: #fbfbfb; border: 1px solid #ffebed; color: #721c24; padding: 12px; border-radius: 4px; margin: 15px 0;">
                ${error}
              </div>
            </c:if>

            <c:if test="${not empty message}">
              <div style="background-color: #f8fff9; border: 1px solid #e1f5e5; color: #1e7e34; padding: 12px; border-radius: 4px; margin: 15px 0;">
                ${message}
              </div>
            </c:if>

            <form class="floating-grid" method="POST" style="margin-top: 20px;">
              <div class="floating-field">
                <input id="verify-code" name="verify-code" type="text" placeholder=" " required maxlength="6" />
                <label for="verify-code">Mã xác thực</label>
              </div>

              <button class="btn btn-primary btn-block" type="submit">Xác thực</button>
            </form>

            <form action="/verify/resend" method="POST" style="margin-top: 12px;">
              <button class="btn btn-secondary btn-block" type="submit">Gửi lại mã</button>
            </form>

            <div class="card-actions">
              <a href="/login">Quay lại đăng nhập</a>
            </div>
          </div>
        </article>
      </div>
    </section>
  </main>

  <jsp:include page="layout/footer.jsp" />
</body>
</html>
