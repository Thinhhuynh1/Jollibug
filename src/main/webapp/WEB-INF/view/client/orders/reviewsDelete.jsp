<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Xóa đánh giá</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/client/profile.css">
  <link rel="stylesheet" href="/css/manager.css">
</head>
<body data-page="orders">

  <jsp:include page="../layout/header.jsp" />

  <main class="profile-page">
    <div class="container container--account-wide">
      <div class="profile-layout">
        <jsp:include page="../layout/sidebar-profile.jsp" />

        <section class="profile-content">
          <h1 class="section-title">Xóa đánh giá</h1>

          <div class="review-container">
            <form action="/orders/reviews/delete" method="post" data-product-delete-form>
              <input type="hidden" name="reviewId" value="${review.maDG}" />

              <div class="manager-delete-preview" style="margin-bottom:1.5rem;">
                <div class="manager-delete-preview__body">
                  <strong>${review.monAn.tenMon}</strong>
                  <div class="manager-delete-preview__meta">
                    <span>Đơn hàng #${review.donHang.maDH}</span>
                    <span>
                      <c:forEach begin="1" end="5" var="i">
                        <c:choose>
                          <c:when test="${i <= review.sao}">★</c:when>
                          <c:otherwise>☆</c:otherwise>
                        </c:choose>
                      </c:forEach>
                    </span>
                  </div>
                  <p style="margin-top:0.5rem;">${review.noiDung}</p>
                </div>
              </div>

              <div class="manager-delete-warning">
                Đánh giá sẽ bị xóa vĩnh viễn. Thao tác không thể hoàn tác.
              </div>

              <label class="manager-delete-confirm">
                <input type="checkbox" data-delete-confirm-check />
                <span>Tôi hiểu và muốn xóa đánh giá này.</span>
              </label>

              <div class="form-actions" style="margin-top:1.5rem;">
                <a href="/orders/reviews" class="btn btn-secondary">Hủy</a>
                <button type="submit" class="btn-delete-submit" data-delete-submit disabled>Xác nhận xóa</button>
              </div>
            </form>
          </div>
        </section>
      </div>
    </div>
  </main>
  <jsp:include page="../layout/footer.jsp" />
  <script src="/js/manager/products.js" defer></script>
</body>
</html>
