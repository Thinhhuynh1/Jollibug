<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Giỏ hàng</title>
  <meta name="description" content="Jollibug cart page: update item quantity and proceed to checkout." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/client/cart-api.css" />
</head>
<body class="cart-page" data-page="cart">
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell section-tight">
    <input type="hidden" id="customerId" value="${sessionScope.user.maTK}">

    <div class="container">
      <div class="page-intro">
        <h1 class="page-title">Giỏ hàng của tôi</h1>
      </div>

      <div class="cart-shell">
        <section class="cart-column">
          <article class="cart-panel">
            <div class="cart-item-list" id="cartItems"></div>
            <div id="cartMessage" class="cart-message"></div>
          </article>
        </section>

        <aside class="summary-column">
          <article class="summary-panel">
            <div style="display:flex; align-items:center; width:100%;">
              <h2 class="summary-panel__title">Tổng sản phẩm</h2>
              <h2 class="summary-count" id="summary-item-count" style="margin-left:auto;">0 MÓN</h2>
            </div>

            <div class="summary-lines">
              <div class="summary-line summary-line--strong">
                <span>Tổng tiền</span>
                <strong id="summary-total">0 VND</strong>
              </div>
            </div>

            <a class="btn btn-primary btn-block" id="checkout-button" href="${pageContext.request.contextPath}/checkout">Đặt hàng</a>
          </article>
        </aside>
      </div>
    </div>
  </main>

  <div class="delete-confirm-modal" id="deleteConfirmModal" role="dialog" aria-modal="true" aria-labelledby="deleteModalTitle" aria-hidden="true">
    <div class="delete-confirm-card" role="document">
      <h2 id="deleteModalTitle">Xóa món khỏi giỏ hàng?</h2>
      <p id="deleteModalMessage">Bạn có chắc muốn xóa món này khỏi giỏ hàng không?</p>
      <div class="delete-confirm-actions">
        <button class="btn btn-ghost" type="button" id="cancelDeleteBtn">Hủy</button>
        <button class="btn btn-primary delete-confirm-btn" type="button" id="confirmDeleteBtn">Xóa món</button>
      </div>
    </div>
  </div>

  <jsp:include page="layout/footer.jsp" />
  <script src="${pageContext.request.contextPath}/resources/js/client/cart-api.js"></script>
</body>
</html>
