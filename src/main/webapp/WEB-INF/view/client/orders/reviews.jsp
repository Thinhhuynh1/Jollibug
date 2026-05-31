<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
            <h1 class="section-title" style="margin:0;">Đánh giá của tôi</h1>
            <a href="/orders/reviews/create" class="btn btn-primary">Đánh giá món ăn</a>
          </div>

          <c:if test="${not empty message}">
            <div class="manager-flash manager-flash--success" style="margin-bottom:1rem;">${message}</div>
          </c:if>
          <c:if test="${not empty error}">
            <div class="manager-flash manager-flash--error" style="margin-bottom:1rem;">${error}</div>
          </c:if>

          <div class="client-tabs" role="tablist" aria-label="Order views">
            <a class="client-tab" href="/orders">Tất cả</a>
            <a class="client-tab" href="/orders/pending">Đang xử lý</a>
            <a class="client-tab" href="/orders/confirmed">Đã xác nhận</a>
            <a class="client-tab" href="/orders/shipping">Đang giao hàng</a>
            <a class="client-tab" href="/orders/delivered">Đã giao</a>
            <a class="client-tab is-active" href="/orders/reviews">Đánh giá</a>
            <a class="client-tab" href="/orders/cancelled">Đã hủy</a>
          </div>

          <section class="orders-list">
            <article class="order-card">
              <div class="orders-table-wrap">
                <table class="orders-table">
                  <thead>
                    <tr>
                      <th>Món ăn</th>
                      <th>Đơn hàng</th>
                      <th>Sao</th>
                      <th>Ngày đánh giá</th>
                      <th>Hành động</th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach var="review" items="${reviews}">
                      <tr>
                        <td><strong>${review.tenMon}</strong></td>
                        <td>#${review.maDH}</td>
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
                          <a class="btn btn-ghost" href="/orders/reviews/view?reviewId=${review.maDG}">Xem</a>
                          <a class="btn btn-ghost" href="/orders/reviews/update?reviewId=${review.maDG}">Sửa</a>
                          <a class="btn btn-ghost" href="/orders/reviews/delete?reviewId=${review.maDG}">Xóa</a>
                        </td>
                      </tr>
                    </c:forEach>
                    <c:if test="${empty reviews}">
                      <tr>
                        <td colspan="5" style="text-align:center;padding:2rem;">
                          Bạn chưa có đánh giá nào.
                          <a href="/orders/reviews/create">Đánh giá món ăn ngay</a>
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
