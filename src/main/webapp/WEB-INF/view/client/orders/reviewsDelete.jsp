<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Đơn hàng</title>
  <meta name="description" content="Thông tin đơn hàng của tôi" />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/client/profile.css">
</head>
<body data-page="orders">

  <jsp:include page="../layout/header.jsp" />

  <main class="profile-page">
    <div class="container container--account-wide">
      <div class="profile-layout ">
        <jsp:include page="../layout/sidebar-profile.jsp" />

        <section class="profile-content">
          <h1 class="section-title">Xóa đánh giá đơn hàng #DH001</h1>

          <div class="review-container">
            <div class="form-group">
              <label class="form-label">Xác nhận xóa đánh giá</label>
              <p style="margin-top: 8px; color: #333; font-size: 1.05rem;">Bạn có chắc chắn muốn xóa đánh giá của đơn hàng này không?</p>
              <p style="color: #e52b34; font-size: 0.95rem; margin-top: 4px;">* Lưu ý: Hành động này không thể hoàn tác.</p>
            </div>

              <input type="hidden" name="orderId" value="DH001" />
              <input type="hidden" name="reviewId" value="REV001" />

              <div class="form-actions">
                <a href="/orders/delivered" class="btn btn-secondary">Hủy bỏ</a>
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