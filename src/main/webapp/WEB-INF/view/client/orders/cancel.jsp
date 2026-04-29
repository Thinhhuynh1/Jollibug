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
  <link rel="stylesheet" href="/css/client/profile.css">
</head>
<body data-page="orders">

  <jsp:include page="../layout/header.jsp" />

  <main class="profile-page">
    <div class="container container--account-wide">
      <div class="profile-layout ">
        <jsp:include page="../layout/sidebar-profile.jsp" />

        <section class="profile-content">
          <h1 class="section-title">Hủy đơn hàng #DH001</h1>

          <div class="review-container">
            <form action="/orders/cancel" method="POST" id="cancelOrderForm">
              <div class="form-group">
                <label class="form-label" style="font-weight: 700; font-size: 1.1rem;">Lý do hủy đơn hàng</label>
                <p style="margin-top: 4px; color: #555; font-size: 0.95rem;">Vui lòng chọn lý do để chúng tôi cải thiện dịch vụ tốt hơn.</p>
                
                <div class="cancellation-reasons">
                  <label class="reason-item">
                    <input type="radio" name="reason" value="change_mind" required checked />
                    <span>Thay đổi ý định mua hàng</span>
                  </label>
                  <label class="reason-item">
                    <input type="radio" name="reason" value="delivery_time" />
                    <span>Thời gian giao hàng quá lâu</span>
                  </label>
                  <label class="reason-item">
                    <input type="radio" name="reason" value="wrong_item" />
                    <span>Đặt nhầm món / Sai thông tin</span>
                  </label>
                  <label class="reason-item">
                    <input type="radio" name="reason" value="better_price" />
                    <span>Tìm thấy nơi khác giá tốt hơn</span>
                  </label>
                  <label class="reason-item">
                    <input type="radio" name="reason" value="other" id="otherReasonRadio" />
                    <span>Lý do khác</span>
                  </label>
                </div>

                <div id="otherReasonContainer" style="margin-top: 1.5rem; display: none;">
                  <label class="form-label" style="font-size: 0.9rem; color: var(--color-ink-500); margin-bottom: 8px; display: block;">Thông tin chi tiết:</label>
                  <textarea name="other_reason_text" placeholder="Nhập lý do cụ thể của bạn..." style="min-height: 120px;"></textarea>
                </div>
              </div>

              <div class="alert-box">
                <p class="alert-box__title">Lưu ý quan trọng:</p>
                <p class="alert-box__text">Hành động hủy đơn hàng không thể hoàn tác. Chúng tôi sẽ kiểm tra trạng thái đơn hàng ngay khi bạn xác nhận.</p>
              </div>

              <input type="hidden" name="orderId" value="DH001" />

              <div class="form-actions" style="margin-top: 2rem; display: flex; gap: 12px;">
                <a href="/orders" class="btn btn-secondary" style="flex: 1;">Quay lại</a>
                <button type="submit" class="btn btn-primary" style="flex: 1;">Xác nhận</button>
              </div>
            </form>
          </div>
        </section>
      </div>
    </div>
  </main>
      <!-- SHARED FOOTER -->
  <jsp:include page="../layout/footer.jsp" />

  <script>
    document.addEventListener('DOMContentLoaded', function() {
      const otherRadio = document.getElementById('otherReasonRadio');
      const otherContainer = document.getElementById('otherReasonContainer');
      const reasonRadios = document.querySelectorAll('input[name="reason"]');
      const otherTextarea = otherContainer.querySelector('textarea');

      function toggleOtherReason() {
        if (otherRadio && otherRadio.checked) {
          otherContainer.style.display = 'block';
          otherTextarea?.focus();
        } else if (otherContainer) {
          otherContainer.style.display = 'none';
        }
      }

      reasonRadios.forEach(radio => {
        radio.addEventListener('change', toggleOtherReason);
      });

      // Kiểm tra ngay khi load
      toggleOtherReason();
    });
  </script>
</body>
</html>