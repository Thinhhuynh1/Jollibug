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

          <c:if test="${not empty error}">
            <div class="manager-flash manager-flash--error" style="margin-bottom:1rem;">${error}</div>
          </c:if>

          <div class="review-container">
            <form action="/orders/reviews/create" method="post" class="profile-form">
              <div class="form-group">
                <label class="form-label">Chọn đơn hàng đã giao</label>
                <select name="orderId" class="form-control" required
                        onchange="window.location.href='/orders/reviews/create?orderId=' + this.value">
                  <option value="">-- Chọn đơn hàng --</option>
                  <c:forEach var="order" items="${deliveredOrders}">
                    <option value="${order.maDH}" ${orderId != null && orderId == order.maDH ? 'selected' : ''}>
                      #${order.maDH} — ${order.ngayDatDisplay}
                    </option>
                  </c:forEach>
                </select>
              </div>

              <c:if test="${not empty orderId}">
                <div class="form-group">
                  <label class="form-label">Chọn món ăn</label>
                  <select name="maMon" class="form-control" required>
                    <option value="">-- Chọn món --</option>
                    <c:forEach var="item" items="${orderItems}">
                      <option value="${item.maMon}" ${maMon != null && maMon == item.maMon ? 'selected' : ''}>
                        ${item.monAn.tenMon}
                      </option>
                    </c:forEach>
                  </select>
                </div>

                <div class="form-group">
                  <label class="form-label">Chất lượng sản phẩm</label>
                  <div class="star-rating">
                    <input type="radio" id="star5" name="sao" value="5" required />
                    <label for="star5" title="5 sao">★</label>
                    <input type="radio" id="star4" name="sao" value="4" />
                    <label for="star4" title="4 sao">★</label>
                    <input type="radio" id="star3" name="sao" value="3" />
                    <label for="star3" title="3 sao">★</label>
                    <input type="radio" id="star2" name="sao" value="2" />
                    <label for="star2" title="2 sao">★</label>
                    <input type="radio" id="star1" name="sao" value="1" />
                    <label for="star1" title="1 sao">★</label>
                  </div>
                </div>

                <div class="form-group">
                  <label for="reviewContent" class="form-label">Nhận xét của bạn</label>
                  <textarea class="form-control" id="reviewContent" name="noiDung" rows="5"
                    placeholder="Hãy chia sẻ những điều bạn thích về món ăn này..." required></textarea>
                </div>

                <div class="form-actions">
                  <a class="btn btn-secondary" href="/orders/reviews">Hủy</a>
                  <button type="submit" class="btn btn-primary">Gửi đánh giá</button>
                </div>
              </c:if>
            </form>
          </div>
        </section>
      </div>
    </div>
  </main>
  <jsp:include page="../layout/footer.jsp" />
</body>
</html>
