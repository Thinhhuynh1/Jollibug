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
  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/client/profile.css">
</head>
<body data-page="profile">

  <jsp:include page="../layout/header.jsp" />

  <main class="profile-page">
    <div class="container container--account-wide">
      <div class="profile-layout">
        <jsp:include page="../layout/sidebar-profile.jsp" />

        <section class="profile-content">
          <div class="panel-header">
            <div class="stack" style="gap:0.25rem;">
              <h2 class="section-title">Địa chỉ đặt hàng của bạn</h2>
            </div>
            <div style="display:flex;justify-content:end;">
              <a class="btn btn-primary" href="/address/create">Thêm địa chỉ mới</a>
            </div>
          </div>

          <div class="address-grid">
            <c:forEach var="address" items="${listAddress}">
              <article class="address-card">
                <div class="address-card__header">
                    <p><strong>Người nhận: </strong> ${address.tenNguoiNhan} - ${address.sdtNguoiNhan}</p>
                  <c:if test="${address.defaultAddress == true}"><span class="tag-default">Mặc định</span></c:if>
                </div>
                <P><strong>Nơi nhận: </strong> ${address.tenDiaChi}</P>
                <p><strong>Đia chỉ: </strong> ${address.diaChiCuThe}, ${address.phuongXa}, ${address.quanHuyen}, ${address.tinhThanh}</p>
                <div >
                  <a class="btn btn-outline" href="/address/update/${address.maDC}">Sửa</a>
                  <a class="btn btn-ghost" href="/address/delete/${address.maDC}">Xóa</a>
                  <c:if test="${!address.defaultAddress}">
                    <form method="post" action="/address/default/${address.maDC}" style="display:inline">
                      <button type="submit" class="btn btn-ghost">Chọn làm mặc định</button>
                    </form>
                  </c:if>
                </div>
              </article>
            </c:forEach>
          </div>

        </section>
      </div>
    </div>
  </main>
      <!-- SHARED FOOTER -->
  <jsp:include page="../layout/footer.jsp" />
</body>
</html>
