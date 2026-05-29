<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Tài khoản</title>
  <meta name="description" content="Quản lý thông tin tài khoản của bạn tại Jollibug" />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/client/profile.css">
</head>
<body data-page="profile">

  <jsp:include page="layout/header.jsp" />

  <main class="profile-page">
    <div class="container container--account-wide">
      <div class="profile-layout">
        <jsp:include page="layout/sidebar-profile.jsp" />

        <section class="profile-content">
          <h1 class="profile-title">Thông tin tài khoản</h1>

          <c:if test="${not empty successMsg}">
              <div style="color: #155724; background-color: #d4edda; border: 1px solid #c3e6cb; padding: 12px; margin-bottom: 20px; border-radius: 5px; font-weight: 600;">
                  ${successMsg}
              </div>
          </c:if>

          <c:if test="${not empty errorMsg}">
              <div style="color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 12px; margin-bottom: 20px; border-radius: 5px; font-weight: 600;">
                  ${errorMsg}
              </div>
          </c:if>

          <form class="profile-form" action="${pageContext.request.contextPath}/profile/update" method="post">
            <div class="profile-grid">
              <label class="profile-field">
                <span>Họ và tên</span>
                <input type="text" name="hoTen" value="${user.hoTen}" placeholder="Nhập họ và tên" />
              </label>
            </div>

            <div class="profile-grid">
              <label class="profile-field">
                <span>Số điện thoại</span>
                <input type="tel" name="sdt" value="${user.sdt}" placeholder="Nhập số điện thoại" />
              </label>

              <label class="profile-field">
                <span>Địa chỉ email</span>
                <input type="email" name="email" value="${user.email}" readonly style="background-color: #f5f5f5;" />
              </label>
            </div>

            <div class="profile-actions">
              <button class="profile-submit" type="submit">Cập nhật tài khoản</button>
            </div>
          </form>
        </section>
      </div>
    </div>
  </main>

</body>
</html>
