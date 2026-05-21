<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Thức Ăn Nhanh Ngon - Giao Hàng Tận Nơi</title>
  <meta name="description" content="Jollibug - Burger thơm ngon, gà giòn rụm, combo hấp dẫn. Đặt món ngay và nhận giao hàng trong 30 phút. Hương vị đậm đà, giá cả phải chăng!" />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
</head>

<body data-page="home">

  <%-- 1. NAVBAR (shared header) --%>
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">

    <%-- 2. HERO --%>
    <section class="hp-hero">
      <div class="container">
        <div class="hp-hero__grid">
          <div class="hp-hero__copy reveal-up">
            <span class="eyebrow">Thức Ăn Nhanh #1 Việt Nam</span>
            <h1>Đói bụng? <span>Jollibug</span> giao ngay trong 30 phút!</h1>
            <div class="hp-hero__cta">
              <a class="btn btn-primary" href="/menu" id="hero-order-btn">Đặt Món Ngay</a>
              <a class="btn btn-secondary" href="/menu" id="hero-menu-btn">Xem Thực Đơn</a>
            </div>
            <div class="hp-stats">
              <div class="hp-stats__item">
                <strong>30 phút</strong>
                <span>Giao hàng nhanh</span>
              </div>
              <div class="hp-stats__item">
                <strong>50+</strong>
                <span>Món ngon đa dạng</span>
              </div>
              <div class="hp-stats__item">
                <strong>4.9 ★</strong>
                <span>Đánh giá khách hàng</span>
              </div>
              <div class="hp-stats__item">
                <strong>10.000+</strong>
                <span>Khách hài lòng</span>
              </div>
            </div>
          </div>
          <div class="hp-hero__image reveal-up">
            <div class="hp-hero__img-wrap">
              <img src="/images/homepage.png" alt="Jollibug combo ngon" />
            </div>
          </div>
        </div>
      </div>
    </section>
<!-- 
    <%-- 3. CATEGORY BAR --%>
    <section class="hp-cat">
      <div class="container">
        <nav class="hp-cat__list" aria-label="Danh mục thực đơn">
          <a class="hp-cat__item active" href="/menu?category=burger" id="cat-burger">
            <span class="hp-cat__icon"></span>
            Burger
          </a>
          <a class="hp-cat__item" href="/menu?category=chicken" id="cat-chicken">
            <span class="hp-cat__icon"></span>
            Gà Rán
          </a>
          <a class="hp-cat__item" href="/menu?category=combo" id="cat-combo">
            <span class="hp-cat__icon"></span>
            Combo
          </a>
          <a class="hp-cat__item" href="/menu?category=drink" id="cat-drink">
            <span class="hp-cat__icon"></span>
            Thức Uống
          </a>
          <a class="hp-cat__item" href="/menu?category=dessert" id="cat-dessert">
            <span class="hp-cat__icon"></span>
            Tráng Miệng
          </a>
          <a class="hp-cat__item" href="/menu?category=fries" id="cat-fries">
            <span class="hp-cat__icon"></span>
            Khoai Chiên
          </a>
        </nav>
      </div>
    </section> -->

    <%-- 4. MENU GRID — 6 món nổi bật (2×3) --%>
    <section class="section">
      <div class="container">
        <div class="page-intro reveal-up">
          <h2 class="section-title">Những món được yêu thích nhất</h2>
          <p class="lead">Được chọn lọc từ hàng nghìn đánh giá thực tế của khách hàng — đây là những món bạn không thể bỏ qua.</p>
        </div>

        <div class="hp-menu-grid">
          <c:forEach var="monAn" items="${listMonAn}" end="5">
            <article class="hp-prod-card reveal-up">
              <div class="hp-prod-card__img">
              <img src="/images/${monAn.img}" alt="${monAn.tenMon}" />
            </div>
              <div class="hp-prod-card__body">
                <div class="hp-prod-card__meta">
                  <span>${monAn.danhMuc.tenDM}</span>
                  <span class="stars">★ 4.9</span>
                </div>
                <div class="hp-prod-card__name">${monAn.tenMon}</div>
                <div class="hp-prod-card__desc">
                  ${monAn.moTa}
                </div>
                <div class="hp-prod-card__footer">
                  <span class="hp-prod-card__price"><fmt:formatNumber value="${monAn.gia}" type="number" />đ</span>
                  <c:if test="${not empty sessionScope.user}">
                    <form method="post" action="/addCart" >
                      <input type="hidden" name="productID" value="${monAn.maMon}">
                      <button class="hp-prod-card__btn"  type="submit">+ Thêm</button>
                    </form>
                  </c:if >
                </div>
              </div>
            </article>
          </c:forEach>

        </div>

        <div style="text-align:center; margin-top:2rem;">
          <a class="btn btn-outline" href="/menu" id="view-all-menu-btn">Xem Toàn Bộ Thực Đơn →</a>
        </div>
      </div>
    </section>

    <%-- 5. BANNER KHUYẾN MÃI --%>
    <section class="hp-promo">
      <div class="container">
        <div class="hp-promo__inner">
          <div class="hp-promo__content">
            <div class="hp-promo__tag">Ưu Đãi Hôm Nay</div>
            <div class="hp-promo__title">Giảm 30% cho đơn hàng<br></div>
            <div class="hp-promo__sub">có giá trị đến hết ngày 30/09/2026.</div>
          </div>
          <div class="hp-promo__cta">
            <a class="hp-promo__btn" href="/menu" id="promo-order-btn">Đặt Ngay</a>
          </div>
        </div>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <div class="page-intro reveal-up" style="text-align:center; justify-items:center;">
          <span class="eyebrow">💡 Vì Sao Chọn Jollibug?</span>
          <h2 class="section-title">Chất lượng — Tốc độ — Ngon</h2>
          <p class="lead" style="text-align:center;">Không chỉ bán thức ăn nhanh, chúng tôi mang đến trải nghiệm ẩm thực tiện lợi cho bạn.</p>
        </div>

        <div class="hp-why__grid">
          <article class="hp-why__card reveal-up">
            <div class="hp-why__icon"><img src="/images/icon-delivery.png" alt="Giao hàng nhanh" /></div>
            <div class="hp-why__title">Giao Hàng Siêu Tốc</div>
            <div class="hp-why__desc">Đảm bảo đồ ăn luôn nóng hổi.</div>
          </article>
          <article class="hp-why__card reveal-up">
            <div class="hp-why__icon"><img src="/images/icon-fresh.png" alt="Chất lượng cao" /></div>
            <div class="hp-why__title">Nguyên Liệu Tươi </div>
            <div class="hp-why__desc">100% nguyên liệu được nhập từ các nhà cung cấp uy tín, không dùng chất bảo quản.</div>
          </article>
          <article class="hp-why__card reveal-up">
            <div class="hp-why__icon"><img src="/images/icon-save.png" alt="Giá cả hợp lý" /></div>
            <div class="hp-why__title">Giá Cả Hợp Lý</div>
            <div class="hp-why__desc">Chất lượng cao đi với giá. Jollibug luôn có ưu đãi hấp dẫn giúp bạn ăn ngon mà không lo chi phí.</div>
          </article>
        </div>
      </div>
    </section>

  </main>

  <%-- 7. FOOTER (shared) --%>
  <jsp:include page="layout/footer.jsp" />

  <!-- <script>
    /* Category bar active toggle */
    document.querySelectorAll('.hp-cat__item').forEach(function(el) {
      el.addEventListener('click', function() {
        document.querySelectorAll('.hp-cat__item').forEach(function(i){ i.classList.remove('active'); });
        el.classList.add('active');
      });
    });
  </script> -->

</body>
</html>
