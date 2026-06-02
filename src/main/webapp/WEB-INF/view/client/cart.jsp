<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
    <input type="hidden" id="maKH" value="1">
    <div class="container">
      <div class="page-intro">
        <h1 class="page-title">Giỏ hàng của tôi</h1>
      </div>

      <div class="cart-shell">
        <section class="cart-column">
          <article class="cart-panel">
            <div class="cart-item-list" id="cartItems">
              <c:set var="tongSoLuong" value="0" />
              <c:set var="tongTien" value="0" />
              <c:forEach var="cartItem" items="${sessionScope.cart}">
                <c:set var="tongSoLuong" value="${tongSoLuong + cartItem.soLuong}"/>
                <c:set var="tongTien" value="${tongTien + cartItem.thanhTien}" />
                <article class="cart-line"
                        id="cart-line-${cartItem.maMon}"
                        data-price="${cartItem.donGia}">
                  <div class="cart-line__thumb">
                    <img src="/images/${cartItem.imageUrl}"
                        alt="${cartItem.tenMon}"
                        style="width:100%;height:100%;object-fit:cover;border-radius:10px;" />
                  </div>

                  <div class="cart-line__meta">
                    <h3 class="cart-line__name">${cartItem.tenMon}</h3>
                    <p class="cart-line__unit">Mã món: ${cartItem.maMon}</p>

                    <div class="cart-line__controls">
                      <a class="cart-link-btn"
                        href="#"
                        data-action="remove"
                        onclick="removeCartItem(event, ${cartItem.maMon})">
                        Xóa
                      </a>

                      <div class="cart-line__purchase">
                        <div class="qty-stepper" aria-label="Chỉnh số lượng">
                          <button class="qty-stepper__btn"
                                  type="button"
                                  aria-label="Giảm số lượng"
                                  onclick="changeQuantity(${cartItem.maMon}, -1)">
                            -
                          </button>

                          <span class="qty-stepper__value" id="qty-${cartItem.maMon}">
                            ${cartItem.soLuong}
                          </span>

                          <button class="qty-stepper__btn"
                                  type="button"
                                  aria-label="Tăng số lượng"
                                  onclick="changeQuantity(${cartItem.maMon}, 1)">
                            +
                          </button>
                        </div>
                        <div style="display: flex; flex-direction: column; align-items: flex-end;">
                          <c:if test="${cartItem.donGia < cartItem.donGiaGoc}">
                            <span style="text-decoration: line-through; color: #999; font-size: 0.85em; font-weight: 500;">
                              <fmt:formatNumber type="number" value="${cartItem.donGiaGoc * cartItem.soLuong}" /> đ
                            </span>
                          </c:if>
                          <strong class="cart-line__sum" id="sum-${cartItem.maMon}">
                            <fmt:formatNumber type="number" value="${cartItem.thanhTien}" /> đ
                          </strong>
                        </div>
                      </div>
                    </div>
                  </div>
                </article>
              </c:forEach>
            </div>

            <div id="cartMessage" class="cart-message"><c:out value="${cartMessage}" /></div>
          </article>
        </section>

        <aside class="summary-column">
          <article class="summary-panel">
            <div style="display:flex; align-items:center; width:100%;">
              <h2 class="summary-panel__title">Tổng sản phẩm</h2>
              <h2 class="summary-count" id="summary-item-count" style="margin-left:auto;">${tongSoLuong} MÓN</h2>
            </div>

            <div class="summary-lines">
              <div class="summary-line summary-line--strong">
                <span>Tổng tiền</span>
                <strong id="summary-total"><fmt:formatNumber type="number" value="${tongTien}" /> đ</strong>
              </div>
            </div>

            <c:choose>
              <c:when test="${tongSoLuong > 0}">
                <a class="btn btn-primary btn-block"
                   id="checkout-button"
                   href="${pageContext.request.contextPath}/checkout"
                   data-checkout-url="${pageContext.request.contextPath}/checkout">Đặt hàng</a>
              </c:when>
              <c:otherwise>
                <a class="btn btn-primary btn-block is-disabled"
                   id="checkout-button"
                   href="#"
                   data-checkout-url="${pageContext.request.contextPath}/checkout"
                   aria-disabled="true"
                   tabindex="-1">Đặt hàng</a>
              </c:otherwise>
            </c:choose>
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
