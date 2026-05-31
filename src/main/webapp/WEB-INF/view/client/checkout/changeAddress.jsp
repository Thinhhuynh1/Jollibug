<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Chọn địa chỉ giao hàng</title>
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
    <div class="container">
      <div class="page-intro">
        <h1 class="section-title">Chọn địa chỉ giao hàng</h1>
      </div>

      <div class="checkout-layout">
        <section class="checkout-card">
          <c:choose>
            <c:when test="${empty listAddress}">
              <p>Bạn chưa có địa chỉ nào.</p>
              <a class="btn btn-primary" href="/address/create">Thêm địa chỉ mới</a>
            </c:when>
            <c:otherwise>
              <div class="address-picker-list">
                <c:forEach var="address" items="${listAddress}">
                  <div class="address-picker-item"
                       data-madc="${address.maDC}"
                       data-name="${address.tenNguoiNhan}"
                       data-phone="${address.sdtNguoiNhan}"
                       data-email="${sessionScope.user.email}"
                       data-address="${address.diaChiCuThe}, ${address.phuongXa}, ${address.quanHuyen}, ${address.tinhThanh}">
                    <div class="address-picker-info">
                      <p class="address-picker-title">
                        <strong>${address.tenNguoiNhan}</strong> - ${address.sdtNguoiNhan}
                        <c:if test="${address.defaultAddress}"><span class="tag-default">Mặc định</span></c:if>
                      </p>
                      <p class="address-picker-desc">${address.tenDiaChi}: ${address.diaChiCuThe}, ${address.phuongXa}, ${address.quanHuyen}, ${address.tinhThanh}</p>
                    </div>
                    <button type="button" class="btn btn-primary address-picker-btn" onclick="selectAddress(this)">Chọn</button>
                  </div>
                </c:forEach>
              </div>
            </c:otherwise>
          </c:choose>

          <div class="address-picker-actions" style="margin-top:1.5rem;">
            <a href="/checkout" class="btn btn-secondary">Quay lại checkout</a>
            <a href="/address/create" class="btn btn-outline">Thêm địa chỉ mới</a>
          </div>
        </section>
      </div>
    </div>
  </main>

  <jsp:include page="../layout/footer.jsp" />
  <script src="/js/client/main.js"></script>
  <script>
  function selectAddress(button) {
      const item = button.closest(".address-picker-item");
      if (!item) return;

      const selectedAddress = {
          maDC: item.dataset.madc,
          name: item.dataset.name,
          phone: item.dataset.phone,
          email: item.dataset.email,
          address: item.dataset.address
      };

      localStorage.setItem("selectedCheckoutAddress", JSON.stringify(selectedAddress));
      window.location.href = "/checkout";
  }
  </script>
</body>
</html>
