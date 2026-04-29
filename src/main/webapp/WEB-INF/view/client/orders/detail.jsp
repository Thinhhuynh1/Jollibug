<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Chi tiết đơn hàng</title>
  <meta name="description" content="Thông tin chi tiết đơn hàng của tôi" />

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
            <div class="card-actions">
                <h1 class="section-title">Chi tiết đơn hàng #DH001</h1>
                <span class="order-card__status" data-status="delivered">Đã giao</span>
            </div>
            <section class="order-detail__section">
              <h2 class="order-detail__title">Thông tin người đặt</h2>
              <div class="order-detail__info-grid">
                <div class="order-detail__info-card">
                  <span class="order-detail__label">Họ tên</span>
                  <strong>Nguyen Van A</strong>
                </div>
                <div class="order-detail__info-card">
                  <span class="order-detail__label">Số điện thoại</span>
                  <strong>0901234567</strong>
                </div>
                <div class="order-detail__info-card order-detail__info-card--full">
                  <span class="order-detail__label">Địa chỉ</span>
                  <strong>123 Nguyen Trai, Phuong Ben Thanh, Quan 1, TP. Ho Chi Minh</strong>
                </div>
              </div>
            </section>
            <br>
            <section class="order-detail__section">
              <h2 class="order-detail__title">Sản phẩm đã đặt</h2>
              <div class="orders-table-wrap">
                <table class="orders-table orders-table--detail">
                  <thead>
                    <tr>
                      <th>Sản phẩm</th>
                      <th>Số lượng</th>
                      <th>Giá món</th>
                      <th>Tổng tiền món</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>
                        <div class="orders-product orders-product--detail">
                          <img class="orders-product__thumb" src="https://placehold.co/96x96?text=Ga" alt="Combo Gà Rán 2 Miếng" />
                          <div class="orders-product__copy">
                            <strong>Combo Gà Rán 2 Miếng</strong>
                          </div>
                        </div>
                      </td>
                      <td>2</td>
                      <td>89,000đ</td>
                      <td>178,000đ</td>
                    </tr>
                    <tr>
                      <td>
                        <div class="orders-product orders-product--detail">
                          <img class="orders-product__thumb" src="https://placehold.co/96x96?text=Tra" alt="Trà Đào Cam Sả" />
                          <div class="orders-product__copy">
                            <strong>Trà Đào Cam Sả</strong>
                          </div>
                        </div>
                      </td>
                      <td>1</td>
                      <td>35,000đ</td>
                      <td>35,000đ</td>
                    </tr>
                    <tr>
                      <td>
                        <div class="orders-product orders-product--detail">
                          <img class="orders-product__thumb" src="https://placehold.co/96x96?text=Khoai" alt="Khoai Tây Chiên Lớn" />
                          <div class="orders-product__copy">
                            <strong>Khoai Tây Chiên Lớn</strong>
                          </div>
                        </div>
                      </td>
                      <td>1</td>
                      <td>29,000đ</td>
                      <td>29,000đ</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </section>

            <div class="order-detail__total">
              <span>Giá tổng bill</span>
              <strong>242,000đ</strong>
            </div>
            
            <div style="text-align: end;">
              <a href="/orders" class="btn btn-secondary">Trở về</a>
            </div>
        </section>
      </div>
    </div>
  </main>

  <jsp:include page="../layout/footer.jsp" />
</body>
</html>
