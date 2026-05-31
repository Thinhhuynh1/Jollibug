<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Đánh giá món ăn</title>
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
          <h1 class="section-title">Đánh giá món ăn</h1>

          <c:if test="${not empty message}">
            <div class="client-flash client-flash--success">${message}</div>
          </c:if>
          <c:if test="${not empty error}">
            <div class="client-flash client-flash--error">${error}</div>
          </c:if>

          <div class="client-flash" style="background:#f8fafc;border:1px solid #e2e8f0;color:#334155;margin-bottom:1rem;">
            Điền đánh giá cho <strong>từng món</strong> trong đơn đã chọn. Mỗi món một khung riêng.
            Đánh giá trong <strong>6 tháng</strong> sau giao hàng · Sửa trong <strong>2 tháng</strong> kể từ ngày đánh giá.
          </div>

          <div class="review-container">
            <c:choose>
              <c:when test="${empty orderId}">
                <form class="profile-form">
                  <div class="form-group">
                    <label class="form-label">Chọn đơn hàng đã giao</label>
                    <select class="form-control" required
                            onchange="window.location.href='/orders/reviews/create?orderId=' + this.value">
                      <option value="">-- Chọn đơn hàng --</option>
                      <c:forEach var="order" items="${deliveredOrders}">
                        <option value="${order.maDH}">
                          #${order.maDH} — ${order.ngayDatDisplay}
                        </option>
                      </c:forEach>
                    </select>
                    <c:if test="${empty deliveredOrders}">
                      <p class="order-note" style="margin-top:0.5rem;">Không còn đơn nào cần đánh giá.</p>
                    </c:if>
                  </div>
                </form>
              </c:when>

              <c:otherwise>
                <div class="review-order-header">
                  <div>
                    <h2 class="review-order-header__title">Đơn hàng #${orderId}</h2>
                    <c:if test="${order != null}">
                      <p class="order-note">Đặt ngày ${order.ngayDatDisplay}</p>
                    </c:if>
                  </div>
                  <a class="btn btn-secondary btn-sm" href="/orders/reviews/create">Chọn đơn khác</a>
                </div>

                <c:if test="${empty orderItems}">
                  <p class="order-note">Tất cả món trong đơn này đã được đánh giá.</p>
                  <a class="btn btn-secondary" href="/orders/detail?orderId=${orderId}">Xem chi tiết đơn</a>
                </c:if>

                <c:if test="${not empty orderItems}">
                  <form action="/orders/reviews/create" method="post" class="profile-form review-batch-form">
                    <input type="hidden" name="orderId" value="${orderId}" />

                    <c:forEach var="item" items="${orderItems}" varStatus="st">
                      <article class="review-item-panel">
                        <header class="review-item-panel__header">
                          <h3 class="review-item-panel__title">
                            <c:choose>
                              <c:when test="${item.monAn != null}">${item.monAn.tenMon}</c:when>
                              <c:otherwise>Món #${item.maMon}</c:otherwise>
                            </c:choose>
                          </h3>
                          <span class="review-item-panel__meta">Số lượng: x${item.soLuong}</span>
                        </header>

                        <input type="hidden" name="items[${st.index}].maMon" value="${item.maMon}" />

                        <div class="form-group">
                          <label class="form-label">Chất lượng sản phẩm</label>
                          <div class="star-rating">
                            <input type="radio" id="star5_${st.index}" name="items[${st.index}].sao" value="5" required />
                            <label for="star5_${st.index}" title="5 sao">★</label>
                            <input type="radio" id="star4_${st.index}" name="items[${st.index}].sao" value="4" />
                            <label for="star4_${st.index}" title="4 sao">★</label>
                            <input type="radio" id="star3_${st.index}" name="items[${st.index}].sao" value="3" />
                            <label for="star3_${st.index}" title="3 sao">★</label>
                            <input type="radio" id="star2_${st.index}" name="items[${st.index}].sao" value="2" />
                            <label for="star2_${st.index}" title="2 sao">★</label>
                            <input type="radio" id="star1_${st.index}" name="items[${st.index}].sao" value="1" />
                            <label for="star1_${st.index}" title="1 sao">★</label>
                          </div>
                        </div>

                        <div class="form-group">
                          <label for="reviewContent_${st.index}" class="form-label">Nhận xét của bạn</label>
                          <textarea class="form-control" id="reviewContent_${st.index}"
                            name="items[${st.index}].noiDung" rows="4"
                            placeholder="Hãy chia sẻ những điều bạn thích về món ăn này..." required></textarea>
                        </div>
                      </article>
                    </c:forEach>

                    <div class="form-actions review-batch-form__actions">
                      <a class="btn btn-secondary" href="/orders/detail?orderId=${orderId}">Hủy</a>
                      <button type="submit" class="btn btn-primary">Gửi ${orderItems.size()} đánh giá</button>
                    </div>
                  </form>
                </c:if>
              </c:otherwise>
            </c:choose>
          </div>
        </section>
      </div>
    </div>
  </main>
  <jsp:include page="../layout/footer.jsp" />
</body>
</html>
