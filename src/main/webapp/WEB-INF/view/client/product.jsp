<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | ${monAn.tenMon}</title>
  <meta name="description" content="Xem chi tiết món ăn ${monAn.tenMon} - Jollibug Fast Food" />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <style>
    /* ─── Banner CTGG  ─── */
    .promo-banner {
      display: flex;
      align-items: center;
      gap: 10px;
      background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
      border: 1.5px solid #ff9800;
      border-radius: 10px;
      padding: 12px 16px;
      margin-bottom: 14px;
    }
    .promo-banner__icon {
      font-size: 1.5rem;
    }
    .promo-banner__text {
      font-size: 0.9rem;
      color: #e65100;
      font-weight: 600;
    }
    .promo-banner__badge {
      margin-left: auto;
      background: #e53935;
      color: #fff;
      font-weight: 800;
      font-size: 1rem;
      border-radius: 8px;
      padding: 4px 12px;
      white-space: nowrap;
    }

    /* ─── Price Block ─── */
    .price-block {
      display: flex;
      align-items: baseline;
      gap: 12px;
      flex-wrap: wrap;
    }
    .price-block__current {
      font-size: 2rem;
      font-weight: 800;
      color: #e53935;
    }
    .price-block__original {
      font-size: 1.1rem;
      color: #999;
      text-decoration: line-through;
      font-weight: 500;
    }
    .price-block__saving {
      font-size: 0.85rem;
      color: #43a047;
      font-weight: 700;
      background: #e8f5e9;
      border-radius: 6px;
      padding: 2px 8px;
    }

    /* ─── Product  ─── */
    .product-specs {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;
      margin: 12px 0;
    }
    .product-spec {
      display: flex;
      flex-direction: column;
      gap: 2px;
      background: #f5f5f5;
      border-radius: 8px;
      padding: 8px 14px;
      min-width: 100px;
    }
    .product-spec .muted {
      font-size: 0.75rem;
      color: #888;
    }
    .product-spec strong {
      font-size: 0.95rem;
      font-weight: 700;
      color: #222;
    }

    /* ─── Spotlight Layout ─── */
    .product-spotlight {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 36px;
      align-items: start;
    }
    @media (max-width: 700px) {
      .product-spotlight { grid-template-columns: 1fr; }
    }
    .product-spotlight__media img {
      width: 100%;
      border-radius: 18px;
      object-fit: cover;
      box-shadow: 0 8px 32px rgba(0,0,0,0.12);
    }
    .stack { display: flex; flex-direction: column; gap: 14px; }
    .cluster { display: flex; gap: 10px; flex-wrap: wrap; margin-top: 8px; }
  </style>
</head>
<body data-page="menu">
  <fmt:setLocale value="vi_VN" />

  <!-- HEADER -->
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
                  <span class="muted">Tình trạng</span>
                  <strong style="color:${monAn.available ? '#43a047' : '#e53935'}">
                    ${monAn.available ? 'Còn hàng' : 'Hết hàng'}
                  </strong>
                </article>
                <article class="product-spec">
                  <span class="muted">Đã bán</span>
                  <strong>${monAn.soLuongDaBan}</strong>
                </article>
                <article class="product-spec">
                  <span class="muted">Lượt đánh giá</span>
                  <strong>${reviewCount}</strong>
                </article>
              </div>

              <!-- Banner khuyến mãi -->
              <c:if test="${monAn.hasGiamGia}">
                <div class="promo-banner">
                  <span class="promo-banner__icon">🏷️</span>
                  <span class="promo-banner__text">Đang áp dụng chương trình khuyến mãi!</span>
                  <span class="promo-banner__badge">-<fmt:formatNumber value="${monAn.phanTramGiam}" />%</span>
                </div>
              </c:if>

              <!-- Giá -->
              <div class="price-block" id="product-price-block">
                <c:choose>
                  <c:when test="${monAn.hasGiamGia}">
                    <span class="price-block__current">
                      <fmt:formatNumber value="${monAn.giaGiam}" type="number" />đ
                    </span>
                    <span class="price-block__original">
                      <fmt:formatNumber value="${monAn.gia}" type="number" />đ
                    </span>
                    <span class="price-block__saving">
                    Giảm <fmt:formatNumber value="${monAn.gia - monAn.giaGiam}" type="number" />đ
                    </span>
                  </c:when>
                  <c:otherwise>
                    <span class="price-block__current">
                      <fmt:formatNumber value="${monAn.gia}" type="number" />đ
                    </span>
                  </c:otherwise>
                </c:choose>
              </div>

              <div class="cluster">
                <a class="btn btn-outline" href="${pageContext.request.contextPath}/menu">Quay lại</a>
                <c:if test="${not empty sessionScope.user}">
                  <form method="post" action="${pageContext.request.contextPath}/addCart" data-ajax-add-cart>
                    <input type="hidden" name="productID" value="${monAn.maMon}">
                    <button class="btn btn-primary" type="submit">Thêm vào giỏ</button>
                  </form>
                </c:if>
                <c:if test="${empty sessionScope.user}">
                  <a class="btn btn-primary" href="/login">Đăng nhập để đặt hàng</a>
                </c:if>
              </div>

            </div>
          </article>
        </div>

        <section class="product-reviews reveal-up">
          <div class="product-reviews__header">
            <div>
              <p class="eyebrow">Danh sách Đánh giá</p>
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
  <script src="/js/client/add-to-cart.js"></script>
</body>
</html>
