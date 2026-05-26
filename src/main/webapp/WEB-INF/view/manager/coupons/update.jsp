<%@page contentType="text/html" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <!DOCTYPE html>
    <html lang="vi">

    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <title>Jollibug | Cập nhật mã giảm giá</title>
      <meta name="description" content="Jollibug Manager - cập nhật mã giảm giá." />
      <link rel="preconnect" href="https://fonts.googleapis.com" />
      <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
      <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap"
        rel="stylesheet" />
      <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
      <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
      <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
      <link rel="stylesheet" href="<c:url value='/css/client/profile.css'/>" />
    </head>

    <body data-admin-role="manager" data-admin-page="coupons">
      <div class="admin-shell admin-body" data-admin-table-root>
        <jsp:include page="../layout/sidebar.jsp" />
        <main class="admin-main">
          <jsp:include page="../layout/topbar.jsp" />
          <div style="max-width:52rem; margin:0 auto; width:100%;">
            <section class="profile-content">
              <section class="profile-section">
                <h1 class="profile-title">Cập nhật mã giảm giá</h1>

                <form action="<c:url value='/manager/coupons/update'/>" method="post" class="profile-form">
                  <input type="hidden" name="couponID" value="${coupon.maGG}" />
                  <div class="profile-grid">
                    <label class="profile-field">
                      <span>Mã giảm giá (Code)</span>
                      <input type="text" name="code" value="${coupon.tenMa}" placeholder="WELCOME10" required
                        style="text-transform: uppercase;" />
                    </label>

                    <label class="profile-field">
                      <span>Loại giảm giá</span>
                      <select name="loaiGiam" required id="loaiGiamSelect">
                        <option value="PERCENTAGE" ${coupon.loaiGiam == 'PERCENTAGE' ? 'selected' : ''}>Giảm theo phần trăm (%)</option>
                        <option value="AMOUNT" ${coupon.loaiGiam == 'AMOUNT' ? 'selected' : ''}>Giảm theo số tiền (VNĐ)</option>
                      </select>
                    </label>

                    <label class="profile-field">
                      <span id="mucGiamLabel">${coupon.loaiGiam == 'AMOUNT' ? 'Mức giảm (VNĐ)' : 'Mức giảm (%)'}</span>
                      <input type="number" step="1" min="0" name="mucGiam" value="${coupon.mucGiam}" placeholder="10"
                        required id="mucGiamInput" />
                    </label>

                    <label class="profile-field" style="grid-column: span 2;">
                      <span>Mô tả ngắn gọn</span>
                      <input type="text" name="moTa" value="${coupon.moTa}" placeholder="Giảm 10% cho đơn hàng đầu tiên"
                        required />
                    </label>

                    <label class="profile-field">
                      <span>Số lượng phát hành</span>
                      <input type="number" min="0" name="soLuong" value="${coupon.soLuong}" placeholder="100"
                        required />
                    </label>

                    <label class="profile-field">
                      <span>Điều kiện đơn tối thiểu (VNĐ)</span>
                      <input type="number" min="0" step="1000" name="dieuKien" value="${coupon.dieuKien}"
                        placeholder="0 hoặc 100000" />
                    </label>

                    <label class="profile-field">
                      <span>Ngày bắt đầu</span>
                      <input type="date" name="startDate" value="${coupon.ngayBatDauValue}" required />
                    </label>

                    <label class="profile-field">
                      <span>Ngày kết thúc</span>
                      <input type="date" name="endDate" value="${coupon.ngayKetThucValue}" required />
                    </label>
                  </div>

                  <div class="profile-actions"
                    style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:2rem;">
                    <a href="<c:url value='/manager/coupons'/>" class="btn btn-ghost">Hủy</a>
                    <button type="submit" class="profile-submit" style="max-width:180px;">Lưu thay đổi</button>
                  </div>
                </form>
              </section>
            </section>
          </div>
        </main>
      </div>
    <script>
      const loaiGiamSelect = document.getElementById('loaiGiamSelect');
      const mucGiamLabel = document.getElementById('mucGiamLabel');
      const mucGiamInput = document.getElementById('mucGiamInput');

      loaiGiamSelect.addEventListener('change', function () {
        if (this.value === 'PERCENTAGE') {
          mucGiamLabel.textContent = 'Mức giảm (%)';
          mucGiamInput.placeholder = '10';
        } else {
          mucGiamLabel.textContent = 'Mức giảm (VNĐ)';
          mucGiamInput.placeholder = '50000';
        }
      });
    </script>
    </body>

    </html>
