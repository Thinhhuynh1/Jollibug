<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Chi Tiết Món</title>
  <meta name="description" content="Xem chi tiết món ăn, đánh giá từ khách hàng và thêm món vào giỏ nhanh chóng." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
</head>
<body data-page="menu">
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">
        <div class="product-spotlight">
          <article class="product-detail-card reveal-up">
            <div class="product-spotlight__media">
              <img src="/images/${monAn.img}"
                   alt="${monAn.tenMon}" />
            </div>
          </article>

          <article class="product-detail-card reveal-up">
            <div class="stack">
              <h1 class="page-title">${monAn.tenMon}</h1>
              <p class="lead">${monAn.moTa}</p>

              <div class="product-specs">
                <article class="product-spec">
                  <span class="muted">Đánh giá trung bình</span>
                  <strong>
                    <fmt:formatNumber value="${averageRating}" maxFractionDigits="1" minFractionDigits="1" />
                    / 5
                  </strong>
                </article>
                <article class="product-spec">
                  <span class="muted">Lượt đánh giá</span>
                  <strong>${reviewCount}</strong>
                </article>
                <article class="product-spec">
                  <span class="muted">Danh mục</span>
                  <strong>${monAn.danhMuc.tenDM}</strong>
                </article>
              </div>

              <div class="price-row">
                <span class="price">
                  <fmt:formatNumber value="${monAn.gia}" type="number" /> đ
                </span>
              </div>

              <div class="cluster">
                <a class="btn btn-outline" href="/menu">Quay lại</a>
                <c:if test="${not empty sessionScope.user}">
                  <form method="post" data-add-cart-form data-add-cart-api="/api/cart/add">
                    <input type="hidden" name="productID" value="${monAn.maMon}">
                    <button class="btn btn-primary" type="submit">+ Thêm vào giỏ</button>
                  </form>
                </c:if>
              </div>
            </div>
          </article>
        </div>

        <section class="product-reviews reveal-up">
          <div class="product-rating-summary">
            <div>
              <p class="product-rating-summary__eyebrow">Cảm nhận từ khách hàng</p>
              <h2 class="section-title">Đánh giá món này</h2>
            </div>
            <div class="product-rating-summary__score">
              <strong>
                <fmt:formatNumber value="${averageRating}" maxFractionDigits="1" minFractionDigits="1" />
              </strong>
              <span>/ 5 từ ${reviewCount} lượt đánh giá</span>
            </div>
          </div>

          <c:choose>
            <c:when test="${not empty productReviews}">
              <div class="review-list">
                <c:forEach var="review" items="${productReviews}">
                  <article class="review-card">
                    <div class="review-card__header">
                      <div>
                        <h3 class="review-card__author">${review.tenKhachHang}</h3>
                        <p class="review-card__date">
                          <fmt:formatDate value="${review.ngayDG}" pattern="dd/MM/yyyy HH:mm" />
                        </p>
                      </div>
                      <div class="review-card__stars" aria-label="${review.sao} trên 5 sao">
                        <c:forEach begin="1" end="5" var="star">
                          <c:choose>
                            <c:when test="${star <= review.sao}">
                              <span class="is-active">&#9733;</span>
                            </c:when>
                            <c:otherwise>
                              <span>&#9733;</span>
                            </c:otherwise>
                          </c:choose>
                        </c:forEach>
                      </div>
                    </div>
                    <p class="review-card__body">${review.noiDung}</p>
                  </article>
                </c:forEach>
              </div>
            </c:when>
            <c:otherwise>
              <div class="review-empty">
                Chưa có đánh giá nào cho món này. Bạn có thể là người đầu tiên chia sẻ trải nghiệm.
              </div>
            </c:otherwise>
          </c:choose>
        </section>
      </div>
    </section>
  </main>

  <jsp:include page="layout/footer.jsp"/>

  <script src="/js/client/main.js"></script>
</body>
</html>
