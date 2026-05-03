<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Profile</title>
  <meta name="description" content="Quản lý tài khoản, mật khẩu và địa chỉ giao hàng của bạn." />

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
          <h1 class="profile-title">Chi tiết tài khoản</h1>

          <form class="profile-form" action="#" method="post">
            <div class="profile-grid">
              <label class="profile-field">
                <span>Họ của bạn</span>
                <input type="text" name="lastName" placeholder="Nhập họ của bạn" />
              </label>

              <label class="profile-field">
                <span>Tên của bạn</span>
                <input type="text" name="firstName" placeholder="Nhập tên của bạn" />
              </label>
            </div>

            <div class="profile-grid">
              <label class="profile-field">
                <span>Số điện thoại</span>
                <input type="tel" name="phone" placeholder="Nhập số điện thoại" />
              </label>

              <label class="profile-field">
                <span>Địa chỉ email</span>
                <input type="email" name="email" value="userdemo@jollibug.vn" readonly disabled />
              </label>
            </div>

            <div class="profile-grid">
              <label class="profile-field">
                <span>Giới tính</span>
                <select name="gender">
                  <option value="">Chọn giới tính</option>
                  <option value="male">Nam</option>
                  <option value="female">Nữ</option>
                  <option value="other">Khác</option>
                </select>
              </label>

              <div class="profile-field">
                <span>Ngày sinh</span>
                <div class="profile-grid profile-grid--three">
                  <select name="birthDay">
                    <option value="">Ngày</option>
                    <c:forEach begin="1" end="31" var="day">
                      <option value="${day}">${day}</option>
                    </c:forEach>
                  </select>
                  <select name="birthMonth">
                    <option value="">Tháng</option>
                    <c:forEach begin="1" end="12" var="month">
                      <option value="${month}">${month}</option>
                    </c:forEach>
                  </select>
                  <select name="birthYear">
                    <option value="">Năm</option>
                    <c:forEach begin="1950" end="2026" var="year">
                      <option value="${year}">${year}</option>
                    </c:forEach>
                  </select>
                </div>
              </div>
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
