<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Cart</title>
  <meta name="description" content="Jollibug cart page: update item quantity and proceed to checkout." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
</head>
<body class="cart-page" data-page="cart">
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell section-tight">
    <div class="container">
      <div class="page-intro">
        <h1 class="page-title">Giỏ hàng của tôi</h1>
      </div>

      <div class="cart-shell">
        <section class="cart-column">
          <article class="cart-panel">
            <div class="cart-item-list">
              <article class="cart-line">
                <div class="cart-line__thumb">
                  <img src="https://static.kfcvietnam.com.vn/images/items/lg/6-COB-April.jpg?v=3ydVxg"
                       alt="Combo Cung Vui"
                       style="width:100%;height:100%;object-fit:cover;border-radius:10px;" />
                </div>
                <div class="cart-line__meta">
                  <h3 class="cart-line__name">Combo Cung Vui</h3>
                  <p class="cart-line__unit">6 x Ga Gion Cay | Tang 3 lon Pepsi</p>
                  <div class="cart-line__controls">
                    <a class="cart-link-btn" href="#" data-action="remove">Xoa</a>
                    <div style="margin-left:auto;display:inline-flex;align-items:center;gap:0.6rem;">
                      <div class="qty-stepper" aria-label="Chinh so luong">
                        <button class="qty-stepper__btn" type="button" aria-label="Giam so luong">-</button>
                        <span class="qty-stepper__value">1</span>
                        <button class="qty-stepper__btn" type="button" aria-label="Tang so luong">+</button>
                      </div>
                      <strong class="cart-line__sum">199.000 VND</strong>
                    </div>
                  </div>
                </div>
              </article>

              <article class="cart-line">
                <div class="cart-line__thumb">
                  <img src="https://static.kfcvietnam.com.vn/images/items/lg/6-COB-April.jpg?v=3ydVxg"
                       alt="Combo Cung Vui"
                       style="width:100%;height:100%;object-fit:cover;border-radius:10px;" />
                </div>
                <div class="cart-line__meta">
                  <h3 class="cart-line__name">Combo Cung Vui</h3>
                  <p class="cart-line__unit">6 x Ga Gion Cay | Tang 3 lon Pepsi</p>
                  <div class="cart-line__controls">
                    <a class="cart-link-btn" href="#" data-action="remove">Xoa</a>
                    <div style="margin-left:auto;display:inline-flex;align-items:center;gap:0.6rem;">
                      <div class="qty-stepper" aria-label="Chinh so luong">
                        <button class="qty-stepper__btn" type="button" aria-label="Giam so luong">-</button>
                        <span class="qty-stepper__value">1</span>
                        <button class="qty-stepper__btn" type="button" aria-label="Tang so luong">+</button>
                      </div>
                      <strong class="cart-line__sum">199.000 VND</strong>
                    </div>
                  </div>
                </div>
              </article>
            </div>
          </article>
        </section>

        <aside class="summary-column">
          <article class="summary-panel">
            <div style="display:flex; align-items:center; width:100%;">
              <h2 class="summary-panel__title">Tổng sản phẩm </h2>
              <h2 class="summary-count" id="summary-item-count" style="margin-left:auto;">0 MÓN</h2>
            </div>



            <div class="summary-lines">
              <div class="summary-line summary-line--strong"><span>Tổng tiền</span><strong id="summary-total">0 VND</strong></div>
            </div>

            <a class="btn btn-primary btn-block" type="button" id="checkout-button" href="/checkout">Đặt hàng</a>
          </article>
        </aside>
      </div>
    </div>
  </main>

  <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp" />

</body>
</html>
