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
  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="client/css/profile.css">
</head>
<body data-page="orders">

  <jsp:include page="layout/header.jsp" />

  <main class="profile-page">
    <div class="container">
      <div class="profile-layout">
        <jsp:include page="layout/sidebar-profile.jsp" />

        <section class="profile-content">
          <div class="page-intro">
            <h1 class="section-title">Đơn hàng của tôi</h1>
          </div>

          <div class="client-tabs" role="tablist" aria-label="Order views">
            <button class="client-tab is-active" type="button" role="tab">Tất cả</button>
            <button class="client-tab" type="button" role="tab">Đang xử lý</button>
            <button class="client-tab" type="button" role="tab">Lịch sử</button>
          </div>

          <section class="orders-list">
            <article class="order-card">
              <div class="orders-table-wrap">
                <table class="orders-table">
                  <thead>
                    <tr>
                      <th>Tên đơn hàng</th>
                      <th>Số lượng</th>
                      <th>Giá sản phẩm</th>
                      <th>Tổng tiền</th>
                      <th>Trạng thái</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>
                        <div class="orders-product">
                          <img class="orders-product__thumb" src="https://placehold.co/64x64?text=Burger" alt="Burger combo" />
                          <span>Combo Gà Rán 2 Miếng</span>
                        </div>
                      </td>
                      <td>2</td>
                      <td>89.000đ</td>
                      <td>178.000đ</td>
                      <td>
                        <span class="order-card__status" data-status="preparing">Đang chuẩn bị</span>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <div class="orders-product">
                          <img class="orders-product__thumb" src="https://placehold.co/64x64?text=Pizza" alt="Pizza hải sản" />
                          <span>Pizza Hải Sản Cỡ Vừa</span>
                        </div>
                      </td>
                      <td>2</td>
                      <td>129.000đ</td>
                      <td>258.000đ</td>
                      <td>
                        <span class="order-card__status" data-status="on_the_way">Đang giao</span>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <div class="orders-product">
                          <img class="orders-product__thumb" src="https://placehold.co/64x64?text=Tea" alt="Trà sữa trân châu" />
                          <span>Trà Sữa Trân Châu Đường Đen</span>
                        </div>
                      </td>
                      <td>2</td>
                      <td>45.000đ</td>
                      <td>90.000đ</td>
                      <td>
                        <span class="order-card__status" data-status="delivered">Đã giao</span>
                      </td>
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

</body>
</html>
