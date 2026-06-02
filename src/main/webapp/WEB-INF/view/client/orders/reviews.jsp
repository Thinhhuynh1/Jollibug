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
          <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:1rem;margin-bottom:1rem;">
            <h1 class="section-title" style="margin:0;">
              <c:choose>
                <c:when test="${not empty orderId}">Đánh giá đơn hàng #${orderId}</c:when>
                <c:otherwise>Đánh giá của tôi</c:otherwise>
              </c:choose>
            </h1>
            <c:if test="${empty orderId}">
              <a href="/orders/reviews/create" class="btn btn-primary">Đánh giá món ăn</a>
            </c:if>
          </div>

          <c:if test="${not empty message}">
            <div class="client-flash client-flash--success">${message}</div>
          </c:if>
          <c:if test="${not empty error}">
            <div class="client-flash client-flash--error">${error}</div>
          </c:if>

          <div class="client-flash" style="background:#f8fafc;border:1px solid #e2e8f0;color:#334155;margin-bottom:1rem;">
            <c:choose>
              <c:when test="${not empty orderId}">
                Chỉ hiển thị đánh giá của <strong>đơn #${orderId}</strong>.
                Cùng tên món ở đơn khác là đánh giá riêng, không liên quan.
              </c:when>
              <c:otherwise>
                Mỗi đánh giá gắn với <strong>một món trong một đơn</strong>.
                Gà rán ở đơn #1 và đơn #19 là hai đánh giá khác nhau.
              </c:otherwise>
            </c:choose>
          </div>

          <div class="client-tabs" role="tablist" aria-label="Chế độ xem đơn hàng">
            <a class="client-tab" href="/orders">Tất cả</a>
            <a class="client-tab" href="/orders/pending">Đang xử lý</a>
            <a class="client-tab" href="/orders/confirmed">Đã xác nhận</a>
            <a class="client-tab" href="/orders/shipping">Đang giao hàng</a>
            <a class="client-tab" href="/orders/delivered">Đã giao</a>
            <a class="client-tab is-active" href="/orders/reviews">Đánh giá</a>
            <a class="client-tab" href="/orders/cancelled">Đã hủy</a>
          </div>

          <c:if test="${empty orderId && not empty pendingOrders}">
            <section class="orders-list" style="margin-top:1rem;margin-bottom:1.5rem;">
              <h2 class="review-section-title">Đơn chờ đánh giá</h2>
              <div class="review-pending-list">
                <c:forEach var="pendingOrder" items="${pendingOrders}">
                  <article class="review-pending-card">
                    <div>
                      <strong>Đơn #${pendingOrder.maDH}</strong>
                      <p class="order-note">${pendingOrder.ngayDatDisplay}</p>
                    </div>
                    <a class="btn btn-primary btn-sm" href="/orders/reviews/create?orderId=${pendingOrder.maDH}">Đánh giá ngay</a>
                  </article>
                </c:forEach>
              </div>
            </section>
          </c:if>

          <section class="orders-list" style="margin-top:1rem;">
            <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:0.75rem;margin-bottom:0.75rem;">
              <h2 class="review-section-title" style="margin:0;">
                <c:choose>
                  <c:when test="${not empty orderId}">Món đã đánh giá trong đơn #${orderId}</c:when>
                  <c:otherwise>Đánh giá đã gửi</c:otherwise>
                </c:choose>
              </h2>
              <c:if test="${not empty orderId}">
                <a class="btn btn-secondary btn-sm" href="/orders/reviews">Xem tất cả đánh giá</a>
              </c:if>
            </div>

            <article class="order-card">
              <div class="orders-table-wrap">
                <table class="orders-table">
                  <thead>
                    <tr>
                      <th>Món ăn</th>
                      <th>Sao</th>
                      <th>Ngày đánh giá</th>
                      <th>Hạn sửa</th>
                      <th>Hành động</th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach var="review" items="${reviews}">
                      <tr>
                        <td><strong>${review.tenMon}</strong></td>
                        <td>
                          <span aria-label="${review.sao} sao">
                            <c:forEach begin="1" end="5" var="i">
                              <c:choose>
                                <c:when test="${i <= review.sao}">★</c:when>
                                <c:otherwise>☆</c:otherwise>
                              </c:choose>
                            </c:forEach>
                          </span>
                        </td>
                        <td>${review.ngayDGDisplay}</td>
                        <td>
                          <c:choose>
                            <c:when test="${review.canEdit}">${review.editDeadlineDisplay}</c:when>
                            <c:otherwise><span class="order-note">Đã hết hạn</span></c:otherwise>
                          </c:choose>
                        </td>
                        <td>
                          <div class="review-table-actions">
                            <a class="btn btn-ghost btn-sm" href="/orders/reviews/view?reviewId=${review.maDG}">Xem</a>
                            <c:if test="${review.canEdit}">
                              <a class="btn btn-ghost btn-sm" href="/orders/reviews/update?reviewId=${review.maDG}">Sửa</a>
                            </c:if>
                            <a class="btn btn-ghost btn-sm" href="/orders/reviews/delete?reviewId=${review.maDG}">Xóa</a>
                          </div>
                        </td>
                      </tr>
                    </c:forEach>
                    <c:if test="${empty reviews}">
                      <tr>
                        <td colspan="5" style="text-align:center;padding:2rem;">
                          <c:choose>
                            <c:when test="${not empty orderId}">Chưa có đánh giá nào cho đơn #${orderId}.</c:when>
                            <c:otherwise>Bạn chưa có đánh giá nào.</c:otherwise>
                          </c:choose>
                        </td>
                      </tr>
                    </c:if>
                  </tbody>
                </table>
              </div>
            </article>
          </section>
        </section>
      </div>
    </div>
  </main>
  <jsp:include page="../layout/footer.jsp" />
</body>
</html>
