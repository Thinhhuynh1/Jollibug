<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Checkout</title>
  <meta name="description" content="Jollibug checkout page: review order details, choose delivery address, apply voucher, and place your order." />

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
    <input type="hidden" id="customerId" value="1">
    <input type="hidden" id="addressSelect" value="1">

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
              <!-- Dropdown gợi ý địa chỉ -->
              <div id="address-suggestions" style="display: none; position: absolute; top: 100%; left: 0; width: 100%; background: #fff; border: 1px solid #ddd; border-radius: 6px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); z-index: 10; max-height: 250px; overflow-y: auto; margin-top: 4px;"></div>
            </label>
          </form>

          <div style="text-align: end;">
            <a class="btn btn-outline" href="/checkout/changeAddress" >
              Đổi địa chỉ
            </a>
          </div>

          <!-- <section>
            <h3 class="section-subtitle">Phương thức thanh toán</h3>
            <div class="payment-options" role="radiogroup" aria-label="Payment method">
              <label class="payment-option">
                <input type="radio" name="payment-method" value="cod" checked />
                <div>
                  <strong>Thanh toán khi nhận hàng (COD)</strong>
                </div>
              </label>

              <label class="payment-option">
                <input type="radio" name="payment-method" value="online" />
                <div>
                  <strong>Thanh toán bằng ATM/ Ví điện tử</strong>
                </div>
              </label>
            </div>
          </section> -->

          <section class="payment-method-section">
            <h3 class="section-subtitle">Phương thức thanh toán</h3>

            <div class="payment-options" role="radiogroup" aria-label="Payment method">
              <label class="payment-option">
                <input type="radio" name="payment-method" value="COD" checked />
                <div>
                  <strong>Thanh toán khi nhận hàng (COD)</strong>
                </div>
              </label>

              <label class="payment-option">
                <input type="radio" name="payment-method" value="CREDIT_CARD" />
                <div>
                  <strong>Thẻ tín dụng / Ghi nợ</strong>
                </div>
              </label>

              <label class="payment-option">
                <input type="radio" name="payment-method" value="BANK" />
                <div>
                  <strong>Chuyển khoản ngân hàng</strong>
                </div>
              </label>

              <label class="payment-option">
                <input type="radio" name="payment-method" value="EWALLET" />
                <div>
                  <strong>Ví điện tử</strong>
                </div>
              </label>
            </div>
          </section>

          <div id="checkoutMessage" class="checkout-message"></div>

          <button class="btn btn-primary btn-block" type="button" id="btn-place-order">
            Thanh toán
          </button>
        </section>
        <section class="checkout-card">
          <h2 class="checkout-card__title">Tóm tắt đơn hàng</h2>
          <div id="checkoutItemList">
            <c:set var="checkoutSubtotal" value="0" />

            <c:forEach var="cartItem" items="${sessionScope.cart}">
                <c:set var="checkoutSubtotal" value="${checkoutSubtotal + cartItem.thanhTien}" />

                <div class="invoice-line checkout-session-item"
                    data-line-total="${cartItem.thanhTien}">
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
            <p class="section-subtitle">Bạn có Mã giảm giá?</p>
            <div class="voucher-box__row">
              <input id="voucher-code" type="text" placeholder="Nhập mã giảm giá" style="min-width: 0; box-sizing: border-box;" />
              <button class="btn btn-outline voucher-inline__apply" id="voucher-apply" type="button">Áp dụng</button>
            </div>
            <div id="voucher-message" style="min-height: 1.4rem; margin-top: 0.75rem; font-size: 0.95rem;"></div>

            
            <!-- Danh sách Voucher có sẵn -->
            <div class="voucher-carousel-wrapper">
              <button class="voucher-carousel-arrow voucher-carousel-arrow--prev" data-voucher-arrow="prev">❮</button>
              
              <div class="voucher-carousel-list" data-voucher-list>
                
                <!-- Fake Voucher 1 -->
                <div class="voucher-card">
                  <div class="voucher-card__header">
                    <span class="voucher-card__title">GIAM20K</span>
                    <span class="voucher-card__tag voucher-card__tag--blue">Freeship</span>
                  </div>
                  <p class="voucher-card__desc">Giảm 20k phí vận chuyển cho đơn từ 100k</p>
                  <div class="voucher-card__actions">
                    <button type="button" class="btn btn-primary voucher-card__btn" onclick="document.getElementById('voucher-code').value='GIAM20K'">Chọn</button>
                  </div>
                </div>

                <!-- Fake Voucher 2 -->
                <div class="voucher-card">
                  <div class="voucher-card__header">
                    <span class="voucher-card__title">NEWUSER50</span>
                    <span class="voucher-card__tag voucher-card__tag--yellow">Bạn mới</span>
                  </div>
                  <p class="voucher-card__desc">Giảm 50% tối đa 30k cho khách mới</p>
                  <div class="voucher-card__actions">
                    <button type="button" class="btn btn-primary voucher-card__btn" onclick="document.getElementById('voucher-code').value='NEWUSER50'">Chọn</button>
                  </div>
                </div>

                <!-- Fake Voucher 3 -->
                <div class="voucher-card">
                  <div class="voucher-card__header">
                    <span class="voucher-card__title">HOAN10K</span>
                    <span class="voucher-card__tag voucher-card__tag--green">Hoàn tiền</span>
                  </div>
                  <p class="voucher-card__desc">Hoàn ngay 10k vào ví Jollibug</p>
                  <div class="voucher-card__actions">
                    <button type="button" class="btn btn-primary voucher-card__btn" onclick="document.getElementById('voucher-code').value='HOAN10K'">Chọn</button>
                  </div>
                </div>

                <!-- Fake Voucher 4 -->
                <div class="voucher-card">
                  <div class="voucher-card__header">
                    <span class="voucher-card__title">LUNCH15</span>
                    <span class="voucher-card__tag voucher-card__tag--purple">Ăn trưa</span>
                  </div>
                  <p class="voucher-card__desc">Giảm 15% cho khung giờ 11h-13h</p>
                  <div class="voucher-card__actions">
                    <button type="button" class="btn btn-primary voucher-card__btn" onclick="document.getElementById('voucher-code').value='LUNCH15'">Chọn</button>
                  </div>
                </div>

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




    <!-- SHARED FOOTER -->
  <jsp:include page="../layout/footer.jsp" />

  <script src="/js/client/main.js"></script>
  <script src="${pageContext.request.contextPath}/resources/js/client/checkout-api.js"></script>
</body>
</html>

