<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Xem đánh giá</title>
  <meta name="description" content="Chi tiết đánh giá đơn hàng của tôi" />

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
          <h1 class="section-title">Chi tiết đánh giá đơn hàng #DH003</h1>

          <div class="review-container">
            <div class="form-group">
              <label class="form-label">Mức đánh giá</label>
              <div class="star-rating" aria-label="Đánh giá 4 sao">
                <span aria-hidden="true">★★★★☆</span>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Nội dung đánh giá</label>
              <p class="form-control" style="min-height: 120px; margin: 0; white-space: pre-line;">Gà nóng giòn, đồ ăn được đóng gói cẩn thận. Thời gian giao hàng nhanh hơn dự kiến.</p>
            </div>

            <div class="form-group">
              <label class="form-label">Hình ảnh đính kèm</label>
              <div class="orders-table-wrap" style="padding: 12px; display: flex; gap: 12px; flex-wrap: wrap;">
                <img src="https://placehold.co/140x140?text=Mon+1" alt="Hình đánh giá món 1" style="border-radius: 10px;" />
                <img src="https://placehold.co/140x140?text=Mon+2" alt="Hình đánh giá món 2" style="border-radius: 10px;" />
              </div>
            </div>

            <div class="form-actions">
              <a href="/orders/reviews" class="btn btn-secondary">Trở về</a>
              <a href="/orders/reviews/update" class="btn btn-primary">Sửa đánh giá</a>
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
