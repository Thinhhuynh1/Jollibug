<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Đơn hàng</title>
  <meta name="description" content="Thông tin đơn hàng của tôi" />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/client/css/profile.css">
</head>
<body data-page="orders">

  <jsp:include page="../layout/header.jsp" />

  <main class="profile-page">
    <div class="container container--account-wide">
      <div class="profile-layout ">
        <jsp:include page="../layout/sidebar-profile.jsp" />

        <section class="profile-content">
          <h1 class="section-title">Đơn hàng của tôi</h1>

          <div class="client-tabs" role="tablist" aria-label="Order views">
            <a class="client-tab " type="button" role="tab" href="/orders">Tất cả</a>
            <a class="client-tab" type="button" role="tab" href="/orders/pending">Đang xử lý</a>
            <a class="client-tab" type="button" role="tab" href="/orders/confirmed">Đã xác nhận</a>
            <a class="client-tab is-active" type="button" role="tab" href="/orders/shipping">Đang giao hàng</a>
            <a class="client-tab" type="button" role="tab" href="/orders/delivered">Đã giao</a>
            <a class="client-tab" type="button" role="tab" href="/orders/reviews">Đánh giá</a>
          </div>

          <section class="orders-list">
            <article class="order-card">
              <div class="orders-table-wrap">
                <table class="orders-table">
                  <thead>
                    <tr>
                      <th>Mã đơn hàng</th>
                      <th>Số lượng</th>
                      <th>Tổng tiền</th>
                      <th>Trạng thái</th>
                      <th>Hành động</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>
                        <strong>#DH001</strong>
                      </td>
                      <td>2</td>
                      
                      <td>258.000đ</td>
                      <td>
                        <span class="order-card__status" data-status="on_the_way">Đang giao</span>
                      </td>
                      <td><a class="btn btn-ghost" type="button" href="/orders/detail">Chi tiết đơn hàng</a></td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </article>
          </section>
        </section>
      </div>
    </div>
  </main>
      <!-- SHARED FOOTER -->
  <jsp:include page="../layout/footer.jsp" />
</body>
</html>
