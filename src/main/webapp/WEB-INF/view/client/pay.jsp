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
  <link rel="stylesheet" href="/css/client-checkout.css" />
</head>
<body class="checkout-page" data-page="checkout">
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell checkout-main">
    <div class="container">
      <div class="page-intro">
        <h1 class="section-title">Thanh toán đơn hàng</h1>
      </div>

      <div class="checkout-layout">

        <section class="checkout-card checkout-sticky">

          <section>
            <h3 class="section-subtitle">Phương thức thanh toán</h3>
            <div class="payment-options" role="radiogroup" aria-label="Payment method">
              <label class="payment-option">
                <input type="radio" name="payment-method" value="cod" checked />
                <div>
                  <strong>Thanh toán khi nhận hàng (COD)</strong>
                </div>
              </label>
                <label class="payment-option">
                <input type="radio" name="payment-method" value="credit-card" />
                <div>
                  <strong>Thẻ tín dụng / Ghi nợ</strong>
                </div>
              </label>

              <label class="payment-option">
                <input type="radio" name="payment-method" value="banking" />
                <div>
                  <strong>Chuyển khoản (Internet Banking)</strong>
                </div>
              </label>

              <label class="payment-option">
                <input type="radio" name="payment-method" value="ewallet" />
                <div>
                  <strong>Ví điện tử (Momo, ZaloPay, VNPAY)</strong>
                </div>
              </label>
            </div>

            <!-- Dynamic Payment Content -->
            <div id="payment-dynamic-content" style="margin-top: 1.5rem;">
              
              <!-- Form Thẻ Tín Dụng -->
              <div id="view-credit-card" class="payment-view" style="display: none;">
                <div class="credit-card-form" style="display: grid; gap: 1rem; background: #f8fafc; padding: 1.5rem; border-radius: 8px; border: 1px solid #e2e8f0;">
                  <label class="field-label" style="display: grid; gap: 6px;">
                    <span style="font-size: 0.85rem; font-weight: 600;">Số thẻ</span>
                    <input type="text" id="cc-number" placeholder="0000 0000 0000 0000" maxlength="19" style="padding: 10px; border-radius: 6px; border: 1px solid #cbd5e1;" />
                  </label>
                  <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <label class="field-label" style="display: grid; gap: 6px;">
                      <span style="font-size: 0.85rem; font-weight: 600;">Ngày hết hạn</span>
                      <input type="text" id="cc-expiry" placeholder="MM/YY" maxlength="5" style="padding: 10px; border-radius: 6px; border: 1px solid #cbd5e1;" />
                    </label>
                    <label class="field-label" style="display: grid; gap: 6px;">
                      <span style="font-size: 0.85rem; font-weight: 600;">CVV</span>
                      <input type="password" id="cc-cvv" placeholder="123" maxlength="3" style="padding: 10px; border-radius: 6px; border: 1px solid #cbd5e1;" />
                    </label>
                  </div>
                </div>
              </div>

              <!-- Internet Banking -->
              <div id="view-banking" class="payment-view" style="display: none; text-align: center; background: #f8fafc; padding: 1.5rem; border-radius: 8px; border: 1px solid #e2e8f0;">
                <p style="margin: 0 0 1rem 0; font-size: 0.9rem; color: #475569;">Quét mã QR dưới đây bằng ứng dụng ngân hàng của bạn:</p>
                <div style="background: #fff; display: inline-block; padding: 10px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); margin-bottom: 1rem;">
                  <!-- Dùng ảnh QR giả lập -->
                  <img src="https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=JollibugPaymentSystem" alt="QR Code" width="180" height="180" />
                </div>
                <p style="margin: 0; font-size: 0.85rem; color: var(--color-red-600); font-weight: 700;">Hệ thống sẽ tự động xác nhận sau khi thanh toán thành công.</p>
              </div>

              <!-- Ví điện tử -->
              <div id="view-ewallet" class="payment-view" style="display: none; text-align: center; background: #f8fafc; padding: 2rem; border-radius: 8px; border: 1px solid #e2e8f0;">
                <p style="margin: 0; font-size: 0.95rem; color: #475569;">Sau khi bấm <strong>Thanh toán</strong>, bạn sẽ được chuyển hướng an toàn đến cổng thanh toán của Ví điện tử.</p>
              </div>

              <!-- COD -->
              <div id="view-cod" class="payment-view" style="display: block; text-align: center; background: #f8fafc; padding: 2rem; border-radius: 8px; border: 1px solid #e2e8f0;">
                <p style="margin: 0; font-size: 0.95rem; color: #475569;">Bạn sẽ thanh toán bằng tiền mặt trực tiếp cho nhân viên giao hàng khi nhận được món ăn.</p>
              </div>

            </div>
          </section>

          <!-- <button class="btn btn-primary btn-block" type="button" id="btn-place-order">Thanh toán</button> -->
          <a href="/" class="btn btn-primary btn-block">Thanh toán</a>
        </section>
        <section class="checkout-card">
          <h2 class="checkout-card__title">Thông tin người đặt</h2>
          <div style="font-size: 0.95rem; color: #334155; display: grid; gap: 0.6rem; line-height: 1.4;">
            <p style="margin: 0;"><strong>Họ và tên:</strong> Nguyễn Văn A</p>
            <p style="margin: 0;"><strong>Điện thoại:</strong> 0123456789</p>
            <p style="margin: 0;"><strong>Giao đến:</strong> 123 Đường Số 1, Phường 2, Quận 3, TP.HCM</p>
          </div>

          <hr class="checkout-divider" />

          <h2 class="checkout-card__title">Thông tin đơn hàng</h2>
          <div>
            <div class="invoice-line ">
              <strong>1xGa</strong>
              <strong>0 VND</strong>
            </div>
            <div class="invoice-line ">
              <strong>2xHamburger</strong>
              <strong>0 VND</strong>
            </div>
            <div class="invoice-line ">
              <strong>10xPizza</strong>
              <strong>0 VND</strong>
            </div>
          </div>

          <hr class="checkout-divider" />

          
          <div>
            <div class="invoice-line "><span>Tổng cộng</span><strong id="invoice-total">0 VND</strong></div>
          </div>
        </section>
      </div>
    </div>
  </main>




    <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp" />

  <script src="/js/client/main.js"></script>
</body>
</html>

