<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Forgot Password</title>
  <meta name="description" content="Reset your Jollibug password - enter your email to receive a recovery link." />
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
    <div class="container">
      <div class="profile-layout">
        <jsp:include page="layout/profile-sidebar.jsp" />

        <section class="profile-content">
          <div class="auth-panel__content">
              <div class="page-intro" style="margin-bottom:0;">
                <span class="eyebrow">Forgot Password</span>
                <h2 class="section-title">Đặt lại mật khẩu</h2>
              </div>
              <!-- Future Spring MVC: action="/forgot-password" method="post" -->
              <form class="floating-grid" data-demo-form
                    data-success-message="Reset link sent in demo mode."
                    id="forgot-form" novalidate>
                <div class="floating-field">
                  <input id="forgot-email" name="email" type="email" placeholder=" " required />
                  <label for="forgot-email">Email address</label>
                </div>
                <button class="btn btn-primary btn-block" type="submit">Xác nhận</button>
              </form>

            </div>
        </section>
      </div>
    </div>
  </main>

</body>
</html>
