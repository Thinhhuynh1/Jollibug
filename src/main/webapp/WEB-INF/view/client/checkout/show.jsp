<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Thanh toán</title>
  <meta name="description" content="Trang đặt món và thanh toán đơn hàng Jollibug" />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/client/client-checkout.css" />
</head>
<body class="checkout-page" data-page="checkout">
  <jsp:include page="../layout/header.jsp"/>

  <main class="page-shell checkout-main">
    <input type="hidden" id="maKH" value="${sessionScope.userId != null ? sessionScope.userId : (sessionScope.user != null ? sessionScope.user.maTK : '')}">
    <input type="hidden" id="addressSelect" value="${defaultAddress != null ? defaultAddress.maDC : ''}">
    <input type="hidden" id="currentUserName" value="${currentUser != null ? currentUser.hoTen : ''}">
    <input type="hidden" id="currentUserPhone" value="${currentUser != null ? currentUser.sdt : ''}">
    <input type="hidden" id="currentUserEmail" value="${currentUser != null ? currentUser.email : ''}">
    <input type="hidden" id="defaultAddressName" value="${defaultAddress != null ? defaultAddress.tenNguoiNhan : ''}">
    <input type="hidden" id="defaultAddressPhone" value="${defaultAddress != null ? defaultAddress.sdtNguoiNhan : ''}">
    <input type="hidden" id="defaultAddressLine" value="${defaultAddress != null ? defaultAddress.diaChiCuThe : ''}">
    <input type="hidden" id="defaultWard" value="${defaultAddress != null ? defaultAddress.phuongXa : ''}">
    <input type="hidden" id="defaultDistrict" value="${defaultAddress != null ? defaultAddress.quanHuyen : ''}">
    <input type="hidden" id="defaultProvince" value="${defaultAddress != null ? defaultAddress.tinhThanh : ''}">

    <div class="container">
      <div class="page-intro">
        <h1 class="section-title">Thông tin đặt hàng</h1>
      </div>

      <div class="checkout-layout">

        <section class="checkout-card checkout-sticky">
          <form class="delivery-form" id="delivery-form">
            <label class="field-label">
              <span>Họ tên</span>
              <input type="text" id="delivery-name" required />
            </label>

            <label class="field-label">
              <span>Số điện thoại</span>
              <input type="tel" id="delivery-phone" required />
            </label>

            <label class="field-label">
              <span>Địa chỉ email</span>
              <input type="text" id="delivery-email" required />
            </label>

            <label class="field-label" style="position: relative;">
              <span>Địa chỉ đặt hàng</span>
              <input type="text" id="delivery-address" autocomplete="off" required />
              <div id="address-suggestions" style="display: none; position: absolute; top: 100%; left: 0; width: 100%; background: #fff; border: 1px solid #ddd; border-radius: 6px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); z-index: 10; max-height: 250px; overflow-y: auto; margin-top: 4px;"></div>
            </label>
          </form>

          <div style="text-align: end;">
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/checkout/change-address">
              Đổi địa chỉ
            </a>
          </div>

          <section class="payment-method-section">
            <h3 class="section-subtitle">Phương thức thanh toán</h3>

            <div class="payment-options" role="radiogroup" aria-label="Payment method">
              <label class="payment-option">
                <input type="radio" name="payment-method" value="COD" checked>
                <div>
                  <strong>Thanh toán khi nhận hàng (COD)</strong>
                </div>
              </label>

              <label class="payment-option">
                <input type="radio" name="payment-method" value="CREDIT_CARD">
                <div>
                  <strong>Thẻ tín dụng / Ghi nợ</strong>
                </div>
              </label>

              <label class="payment-option">
                <input type="radio" name="payment-method" value="BANK">
                <div>
                  <strong>Chuyển khoản ngân hàng</strong>
                </div>
              </label>

              <label class="payment-option">
                <input type="radio" name="payment-method" value="EWALLET">
                <div>
                  <strong>Ví điện tử</strong>
                </div>
              </label>
            </div>
          </section>

          <div id="checkoutMessage" class="checkout-message"></div>

          <button type="button" id="checkoutButton" class="btn btn-primary btn-block">
            Thanh toán
          </button>
        </section>

        <section class="checkout-card">
          <h2 class="checkout-card__title">Tóm tắt đơn hàng</h2>
          <div id="checkoutItemList">
            <c:set var="checkoutSubtotal" value="0" />

            <c:forEach var="cartItem" items="${sessionScope.cart}">
              <c:set var="checkoutSubtotal" value="${checkoutSubtotal + cartItem.thanhTien}" />

              <div class="invoice-line checkout-session-item" data-line-total="${cartItem.thanhTien}">
                <strong>${cartItem.soLuong}x ${cartItem.tenMon}</strong>
                <strong>
                  <fmt:formatNumber type="number" value="${cartItem.thanhTien}" /> VND
                </strong>
              </div>
            </c:forEach>

            <c:if test="${empty sessionScope.cart}">
              <div class="invoice-line">
                <span>Giỏ hàng đang trống</span>
                <strong>0 VND</strong>
              </div>
            </c:if>
          </div>

          <hr class="checkout-divider" />
          <div class="voucher-inline">
            <p class="section-subtitle">Bạn có mã giảm giá?</p>
            <div class="voucher-box__row">
              <input id="voucher-code" type="text" placeholder="Nhập mã giảm giá" style="min-width: 0; box-sizing: border-box;" />
              <button class="btn btn-outline voucher-inline__apply" id="voucher-apply" type="button">Áp dụng</button>
            </div>
            <div id="voucher-message" style="min-height: 1.4rem; margin-top: 0.75rem; font-size: 0.95rem;"></div>

            <div class="voucher-carousel-wrapper">
              <button class="voucher-carousel-arrow voucher-carousel-arrow--prev" data-voucher-arrow="prev">❮</button>

              <div class="voucher-carousel-list" data-voucher-list>
                <c:forEach var="coupon" items="${activeCoupons}">
                  <div class="voucher-card">
                    <div class="voucher-card__header">
                      <span class="voucher-card__title">${coupon.tenMa}</span>
                      <span class="voucher-card__tag voucher-card__tag--blue">
                        <c:choose>
                          <c:when test="${coupon.loaiGiam == 'AMOUNT'}">Tiền mặt</c:when>
                          <c:otherwise>Phần trăm</c:otherwise>
                        </c:choose>
                      </span>
                    </div>
                    <p class="voucher-card__desc">
                      <c:choose>
                        <c:when test="${not empty coupon.moTa}">
                          ${coupon.moTa}
                        </c:when>
                        <c:otherwise>
                          Giảm ${coupon.discountDisplay}
                          <c:if test="${coupon.dieuKien != null && coupon.dieuKien > 0}">
                            cho đơn từ <fmt:formatNumber type="number" value="${coupon.dieuKien}" /> VND
                          </c:if>
                        </c:otherwise>
                      </c:choose>
                    </p>
                    <div class="voucher-card__actions">
                      <button type="button" class="btn btn-primary voucher-card__btn" onclick="document.getElementById('voucher-code').value='${coupon.tenMa}'">Chọn</button>
                    </div>
                  </div>
                </c:forEach>
              </div>

              <button class="voucher-carousel-arrow voucher-carousel-arrow--next" data-voucher-arrow="next">❯</button>
            </div>
          </div>

          <div>
            <div class="invoice-line">
              <span>Tạm tính</span>
              <strong id="invoice-subtotal">
                <fmt:formatNumber type="number" value="${checkoutSubtotal}" /> VND
              </strong>
            </div>

            <div class="invoice-line">
              <span>Phí giao hàng</span>
              <strong id="invoice-delivery-fee">0 VND</strong>
            </div>

            <div class="invoice-line">
              <span>Giảm giá</span>
              <strong id="invoice-discount">-0 VND</strong>
            </div>

            <div class="invoice-line summary-line--strong">
              <span>Tổng cộng</span>
              <strong id="invoice-total">
                <fmt:formatNumber type="number" value="${checkoutSubtotal}" /> VND
              </strong>
            </div>
          </div>
        </section>
      </div>
    </div>
  </main>

  <jsp:include page="../layout/footer.jsp" />

  <script src="/js/client/main.js"></script>
  <script src="${pageContext.request.contextPath}/resources/js/client/checkout-api.js" defer></script>
</body>
</html>
