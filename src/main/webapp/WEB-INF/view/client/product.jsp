<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Product Detail</title>
  <meta name="description" content="View full product details, ratings, and customer reviews for this dish." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/global.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/components.css" />
</head>
<body data-page="menu">
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">
        <div class="product-spotlight" data-product-page id="product-page">
          <article class="product-detail-card reveal-up">
            <div class="product-spotlight__media">
              <img src="${pageContext.request.contextPath}/resources/images/${monAn.img}"
                   alt="${monAn.tenMon}" />
            </div>
          </article>

          <article class="product-detail-card reveal-up">
            <div class="stack">
              <h1 class="page-title" data-product-name id="product-name">${monAn.tenMon}</h1>
              <p class="lead" data-product-description id="product-description">${monAn.moTa}</p>

              <div class="product-rating-summary">
                <div class="product-rating-summary__score">
                  <span class="product-rating-summary__value">
                    <fmt:formatNumber value="${averageRating}" type="number" minFractionDigits="1" maxFractionDigits="1" />
                  </span>
                  <span class="product-rating-summary__stars">★★★★★</span>
                </div>
                <p class="product-rating-summary__meta">Dựa trên ${reviewCount} đánh giá từ khách hàng</p>
              </div>

              <div class="product-specs">
                <article class="product-spec">
                  <span class="muted">Đánh giá trung bình</span>
                  <strong data-product-rating id="product-rating">
                    <fmt:formatNumber value="${averageRating}" type="number" minFractionDigits="1" maxFractionDigits="1" /> / 5
                  </strong>
                </article>
                <article class="product-spec">
                  <span class="muted">Danh mục</span>
                  <strong data-product-category id="product-category">${monAn.danhMuc.tenDM}</strong>
                </article>
                <article class="product-spec">
                  <span class="muted">Lượt đánh giá</span>
                  <strong>${reviewCount}</strong>
                </article>
              </div>

              <div class="price-row">
                <span class="price" data-product-price id="product-price">
                  <fmt:formatNumber value="${monAn.gia}" type="number" /> đ
                </span>
              </div>

              <div class="cluster">
                <a class="btn btn-outline" href="${pageContext.request.contextPath}/menu">Quay lại</a>
                <c:if test="${not empty sessionScope.user}">
                  <form method="post" action="${pageContext.request.contextPath}/addCart">
                    <input type="hidden" name="productID" value="${monAn.maMon}">
                    <button class="btn btn-primary" type="submit">+ Thêm</button>
                  </form>
                </c:if>
              </div>
            </div>
          </article>
        </div>

        <section class="product-reviews reveal-up">
          <div class="product-reviews__header">
            <div>
              <p class="eyebrow">Khách hàng nói gì</p>
              <h2 class="section-title">Đánh giá về ${monAn.tenMon}</h2>
            </div>
            <span class="product-reviews__pill">${reviewCount} nhận xét</span>
          </div>

          <c:choose>
            <c:when test="${not empty productReviews}">
              <div class="product-reviews__list">
                <c:forEach var="review" items="${productReviews}">
                  <article class="product-review-card">
                    <div class="product-review-card__top">
                      <div>
                        <h3 class="product-review-card__name">${review.khachHang.hoTen}</h3>
                        <p class="product-review-card__date">
                          <fmt:formatDate value="${review.ngayDG}" pattern="dd/MM/yyyy HH:mm" />
                        </p>
                      </div>
                      <div class="product-review-card__rating">
                        <span class="product-review-card__stars">
                          <c:forEach begin="1" end="5" var="star">
                            <span class="${star <= review.sao ? 'is-filled' : ''}">★</span>
                          </c:forEach>
                        </span>
                        <strong>${review.sao}.0</strong>
                      </div>
                    </div>
                    <p class="product-review-card__content">${review.noiDung}</p>
                  </article>
                </c:forEach>
              </div>
            </c:when>
            <c:otherwise>
              <article class="product-reviews__empty">
                <h3>Chưa có đánh giá nào</h3>
                <p>Món này chưa có nhận xét từ khách hàng. Bạn có thể là người đầu tiên chia sẻ trải nghiệm.</p>
              </article>
            </c:otherwise>
          </c:choose>
        </section>
      </div>
    </section>
  </main>

  <jsp:include page="layout/footer.jsp"/>
</body>
</html>
