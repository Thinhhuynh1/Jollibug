<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Chi tiết đánh giá</title>
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
      <div class="profile-layout">
        <jsp:include page="../layout/sidebar-profile.jsp" />

        <section class="profile-content">
          <h1 class="section-title">Chi tiết đánh giá — ${review.monAn.tenMon}</h1>

          <div class="review-container">
            <div class="form-group">
              <label class="form-label">Đơn hàng</label>
              <p>#${review.donHang.maDH}</p>
            </div>

            <div class="form-group">
              <label class="form-label">Mức đánh giá</label>
              <div class="star-rating" aria-label="${review.sao} sao">
                <c:forEach begin="1" end="5" var="i">
                  <c:choose>
                    <c:when test="${i <= review.sao}">★</c:when>
                    <c:otherwise>☆</c:otherwise>
                  </c:choose>
                </c:forEach>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Nội dung đánh giá</label>
              <p class="form-control" style="min-height:120px;margin:0;white-space:pre-line;">${review.noiDung}</p>
            </div>

            <div class="form-group">
              <label class="form-label">Ngày đánh giá</label>
              <p>${review.ngayDGDisplay}</p>
            </div>

            <div class="form-actions">
              <a href="/orders/reviews" class="btn btn-secondary">Trở về</a>
              <a href="/orders/reviews/update?reviewId=${review.maDG}" class="btn btn-primary">Sửa đánh giá</a>
            </div>
          </div>
        </section>
      </div>
    </div>
  </main>
  <jsp:include page="../layout/footer.jsp" />
</body>
</html>
