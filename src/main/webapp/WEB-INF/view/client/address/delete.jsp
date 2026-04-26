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
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/client/profile.css">
</head>
<body data-page="profile">

  <jsp:include page="../layout/header.jsp" />

  <main class="profile-page">
    <div class="container">
      <div class="profile-layout">
        <jsp:include page="../layout/sidebar-profile.jsp" />

        <section class="profile-content">
          <h1 class="section-title">Xóa địa chỉ đặt hàng</h1>

          <div class="review-container">
            <div class="form-group">
              <label class="form-label">Xác nhận xóa địa chỉ</label>
              <p style="margin-top: 8px; color: #333; font-size: 1.05rem;">Bạn có chắc chắn muốn xóa địa chỉ giao hàng này không?</p>
              <p style="color: #e52b34; font-size: 0.95rem; margin-top: 4px;">* Lưu ý: Hành động này không thể hoàn tác.</p>
            </div>

            <input type="hidden" name="id" value="${address.id}" />

            <div class="form-actions">
              <a href="/address" class="btn btn-secondary">Hủy bỏ</a>
              <button type="submit" class="btn btn-primary">Xác Nhận</button>
            </div>

          </div>
        </section>
      </div>
    </div>
  </main>
      <!-- SHARED FOOTER -->
  <jsp:include page="../layout/footer.jsp" />
</body>
</html>
