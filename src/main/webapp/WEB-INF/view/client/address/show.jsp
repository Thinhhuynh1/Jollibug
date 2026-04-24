<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Địa chỉ giao hàng</title>
  <meta name="description" content="Quản lý địa chỉ giao hàng của bạn." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="client/css/profile.css">
</head>
<body data-page="profile">

  <jsp:include page="../layout/header.jsp" />

  <main class="profile-page">
    <div class="container">
      <div class="profile-layout">
        <jsp:include page="../layout/sidebar-profile.jsp" />

        <section class="profile-content">
          <div class="panel-header">
            <div class="stack" style="gap:0.25rem;">
              <h2 class="section-title">Địa chỉ đặt hàng của bạn</h2>
            </div>
            <div style="display:flex;justify-content:end;">
              <a class="btn btn-primary" href="/address/create">Thêm địa chỉ mới</a>
            </div>
          </div>

          <div class="address-grid">
            <article class="address-card">
              <div class="address-card__header">
                <strong>Nguyễn Minh Khôi</strong>
                <span class="tag-default">Mặc định</span>
              </div>
              <p>0903 456 789</p>
              <p>128 Nguyễn Trãi, Phường 3, Quận 5, TP. Hồ Chí Minh</p>
              <div >
                <a class="btn btn-outline" href="/address/update">Sửa</a>
                <a class="btn btn-ghost" href="/address/delete">Xóa</a>
              </div>
            </article>

            </article>
          </div>

        </section>
      </div>
    </div>
  </main>

</body>
</html>
