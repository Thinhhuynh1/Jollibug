<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Địa chỉ giao hàng</title>
  <meta name="description" content="Quản lý địa chỉ giao hàng của bạn." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/client/profile.css">
</head>
<body data-page="profile">

  <jsp:include page="../layout/header.jsp" />

  <main class="profile-page">
    <div class="container">
      <div class="profile-layout">
        <jsp:include page="../layout/sidebar-profile.jsp" />

        <section class="profile-content">
          <div class="panel-header">
            <div class="stack" style="gap:0.25rem;">
              <h2 class="section-title">Sửa địa chỉ đặt hàng</h2>
            </div>
          </div>

          <article class="address-card">
            <form method="post" action="/address/update/${address.maDC}">
              <div class="profile-grid">
                <label class="profile-field">
                  <span>Tên người nhận</span>
                  <input name="tenNguoiNhan" type="text" id="delivery-name" value="${address.tenNguoiNhan}" required />
                </label>
                <label class="profile-field">
                  <span>Số điện thoại</span>
                  <input type="text" name="sdtNguoiNhan" id="delivery-phone" value="${address.sdtNguoiNhan}" required />
                </label>
              </div>
              <div class="profile-grid" style="margin-top:1rem;">
                <label class="profile-field">
                  <span>Tên địa chỉ</span>
                  <input type="text" id="ten-dia-chi" name="tenDiaChi" value="${address.tenDiaChi}" maxlength="100"  required />
                </label>
                <label for=""></label>
                <label class="profile-field">
                  <span>Tỉnh/Thành</span>
                  <input type="text" id="tinh-thanh" name="tinhThanh" value="${address.tinhThanh}" required />
                </label>
                <label class="profile-field">
                  <span>Quận/Huyện</span>
                  <input type="text" id="quan-huyen" name="quanHuyen" value="${address.quanHuyen}" required />
                </label>
                <label class="profile-field">
                  <span>Phường/Xã</span>
                  <input type="text" id="phuong-xa" name="phuongXa" value="${address.phuongXa}" required />
                </label>
                  <label class="profile-field">
                  <span>Địa chỉ cụ thể</span>
                  <input type="text" id="dia-chi-cu-the" name="diaChiCuThe" value="${address.diaChiCuThe}" required />
                </label>
              </div>
              <div class="modal__actions">
                <a href="/address" class="btn btn-ghost">Hủy</a>
                <button type="submit" class="btn btn-primary">Lưu địa chỉ</button>
              </div>
            </form>
          </article>
        </section>
      </div>
    </div>
  </main>
      <!-- SHARED FOOTER -->
  <jsp:include page="../layout/footer.jsp" />
  
  <script src="/js/client/main.js"></script>
</body>
</html>
