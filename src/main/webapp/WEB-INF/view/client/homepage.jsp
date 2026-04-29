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
            <span class="eyebrow">🍔 Thức Ăn Nhanh #1 Việt Nam</span>
            <h1>Đói bụng? <span>Jollibug</span> giao ngay trong 30 phút!</h1>
            <p>Burger thơm ngon, gà giòn rụm, combo hấp dẫn — tất cả đều được chế biến tươi mỗi ngày và giao tận tay bạn khi còn nóng hổi.</p>
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
              <img src="/webapp/resources/shared/images/brand-feast.svg" alt="Jollibug combo ngon" />
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
            <span class="hp-cat__icon">🍔</span>
            Burger
          </a>
          <a class="hp-cat__item" href="/menu?category=chicken" id="cat-chicken">
            <span class="hp-cat__icon">🍗</span>
            Gà Rán
          </a>
          <a class="hp-cat__item" href="/menu?category=combo" id="cat-combo">
            <span class="hp-cat__icon">🍱</span>
            Combo
          </a>
          <a class="hp-cat__item" href="/menu?category=drink" id="cat-drink">
            <span class="hp-cat__icon">🥤</span>
            Thức Uống
          </a>
          <a class="hp-cat__item" href="/menu?category=dessert" id="cat-dessert">
            <span class="hp-cat__icon">🍦</span>
            Tráng Miệng
          </a>
          <a class="hp-cat__item" href="/menu?category=fries" id="cat-fries">
            <span class="hp-cat__icon">🍟</span>
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

          <%-- Món 1 --%>
          <article class="hp-prod-card reveal-up">
            <div class="hp-prod-card__img" style="background:linear-gradient(135deg,#fff3e0,#ffe0b2);">
              <span class="hp-prod-card__badge">Bán Chạy</span>
              🍔
            </div>
            <div class="hp-prod-card__body">
              <div class="hp-prod-card__meta">
                <span>Burger</span>
                <span class="stars">★ 4.9</span>
              </div>
              <div class="hp-prod-card__name">Burger Bò Phô Mai Đôi</div>
              <div class="hp-prod-card__desc">Hai miếng bò nướng, phô mai cheddar tan chảy, rau tươi và sốt đặc biệt.</div>
              <div class="hp-prod-card__footer">
                <span class="hp-prod-card__price">89.000 ₫</span>
                <button class="hp-prod-card__btn" type="button" id="add-burger-double">+ Thêm</button>
              </div>
            </div>
          </article>

          <%-- Món 2 --%>
          <article class="hp-prod-card reveal-up">
            <div class="hp-prod-card__img" style="background:linear-gradient(135deg,#e8f5e9,#c8e6c9);">
              <span class="hp-prod-card__badge">Mới</span>
              🍗
            </div>
            <div class="hp-prod-card__body">
              <div class="hp-prod-card__meta">
                <span>Gà Rán</span>
                <span class="stars">★ 4.8</span>
              </div>
              <div class="hp-prod-card__name">Gà Giòn Vị Cay</div>
              <div class="hp-prod-card__desc">Vỏ ngoài giòn tan, thịt bên trong mềm ngọt với gia vị cay đặc trưng Jollibug.</div>
              <div class="hp-prod-card__footer">
                <span class="hp-prod-card__price">59.000 ₫</span>
                <button class="hp-prod-card__btn" type="button" id="add-spicy-chicken">+ Thêm</button>
              </div>
            </div>
          </article>

          <%-- Món 3 --%>
          <article class="hp-prod-card reveal-up">
            <div class="hp-prod-card__img" style="background:linear-gradient(135deg,#e3f2fd,#bbdefb);">
              <span class="hp-prod-card__badge">Tiết Kiệm</span>
              🍱
            </div>
            <div class="hp-prod-card__body">
              <div class="hp-prod-card__meta">
                <span>Combo</span>
                <span class="stars">★ 4.9</span>
              </div>
              <div class="hp-prod-card__name">Combo Gia Đình 4 Người</div>
              <div class="hp-prod-card__desc">4 burger, 4 phần khoai chiên lớn, 4 nước uống và 1 phần nugget miễn phí.</div>
              <div class="hp-prod-card__footer">
                <span class="hp-prod-card__price">299.000 ₫</span>
                <button class="hp-prod-card__btn" type="button" id="add-family-combo">+ Thêm</button>
              </div>
            </div>
          </article>

          <%-- Món 4 --%>
          <article class="hp-prod-card reveal-up">
            <div class="hp-prod-card__img" style="background:linear-gradient(135deg,#fce4ec,#f8bbd0);">
              🥤
            </div>
            <div class="hp-prod-card__body">
              <div class="hp-prod-card__meta">
                <span>Thức Uống</span>
                <span class="stars">★ 4.7</span>
              </div>
              <div class="hp-prod-card__name">Trà Sữa Xoài Tươi</div>
              <div class="hp-prod-card__desc">Trà sữa kem tươi kết hợp xoài Cát Hòa Lộc nguyên chất, mát lạnh sảng khoái.</div>
              <div class="hp-prod-card__footer">
                <span class="hp-prod-card__price">39.000 ₫</span>
                <button class="hp-prod-card__btn" type="button" id="add-mango-milk-tea">+ Thêm</button>
              </div>
            </div>
          </article>

          <%-- Món 5 --%>
          <article class="hp-prod-card reveal-up">
            <div class="hp-prod-card__img" style="background:linear-gradient(135deg,#fff8e1,#ffecb3);">
              🍟
            </div>
            <div class="hp-prod-card__body">
              <div class="hp-prod-card__meta">
                <span>Khoai Chiên</span>
                <span class="stars">★ 4.8</span>
              </div>
              <div class="hp-prod-card__name">Khoai Chiên Phô Mai Núi Lửa</div>
              <div class="hp-prod-card__desc">Khoai chiên vàng giòn phủ sốt phô mai nóng chảy và tương ớt ngọt đặc biệt.</div>
              <div class="hp-prod-card__footer">
                <span class="hp-prod-card__price">45.000 ₫</span>
                <button class="hp-prod-card__btn" type="button" id="add-cheese-fries">+ Thêm</button>
              </div>
            </div>
          </article>

          <%-- Món 6 --%>
          <article class="hp-prod-card reveal-up">
            <div class="hp-prod-card__img" style="background:linear-gradient(135deg,#f3e5f5,#e1bee7);">
              🍦
            </div>
            <div class="hp-prod-card__body">
              <div class="hp-prod-card__meta">
                <span>Tráng Miệng</span>
                <span class="stars">★ 4.9</span>
              </div>
              <div class="hp-prod-card__name">Sundae Dâu Tây Creamy</div>
              <div class="hp-prod-card__desc">Kem vani mịn màng, sốt dâu tươi và bánh waffle giòn — kết thúc bữa ăn hoàn hảo.</div>
              <div class="hp-prod-card__footer">
                <span class="hp-prod-card__price">29.000 ₫</span>
                <button class="hp-prod-card__btn" type="button" id="add-strawberry-sundae">+ Thêm</button>
              </div>
            </div>
          </article>

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
            <div class="hp-promo__tag">🔥 Ưu Đãi Hôm Nay</div>
            <div class="hp-promo__title">Giảm 30% cho đơn hàng<br>đầu tiên của bạn!</div>
            <div class="hp-promo__sub">Nhập mã <strong>JOLLIBUG30</strong> khi thanh toán. Áp dụng cho tất cả các món trong thực đơn, có giá trị đến hết ngày 30/04/2026.</div>
          </div>
          <div class="hp-promo__cta">
            <a class="hp-promo__btn" href="/menu" id="promo-order-btn">Đặt Ngay &amp; Tiết Kiệm</a>
            <div class="hp-promo__note">* Điều kiện áp dụng. Xem chi tiết.</div>
          </div>
        </div>
      </div>
    </section>

    <%-- 6. TẠI SAO CHỌN CHÚNG TÔI --%>
    <section class="section">
      <div class="container">
        <div class="page-intro reveal-up" style="text-align:center; justify-items:center;">
          <span class="eyebrow">💡 Vì Sao Chọn Jollibug?</span>
          <h2 class="section-title">Chất lượng — Tốc độ — Hương vị</h2>
          <p class="lead" style="text-align:center;">Chúng tôi không chỉ bán thức ăn nhanh, chúng tôi mang đến trải nghiệm ẩm thực tuyệt vời nhất cho bạn.</p>
        </div>

        <div class="hp-why__grid">
          <article class="hp-why__card reveal-up">
            <div class="hp-why__icon">🚀</div>
            <div class="hp-why__title">Giao Hàng Siêu Tốc</div>
            <div class="hp-why__desc">Đội ngũ giao hàng chuyên nghiệp cam kết giao đến tay bạn trong vòng 30 phút, đảm bảo đồ ăn luôn nóng hổi.</div>
          </article>
          <article class="hp-why__card reveal-up">
            <div class="hp-why__icon">🥩</div>
            <div class="hp-why__title">Nguyên Liệu Tươi Mỗi Ngày</div>
            <div class="hp-why__desc">100% nguyên liệu được nhập từ các nhà cung cấp uy tín, chế biến tươi mỗi ngày, không dùng chất bảo quản.</div>
          </article>
          <article class="hp-why__card reveal-up">
            <div class="hp-why__icon">💰</div>
            <div class="hp-why__title">Giá Cả Phải Chăng</div>
            <div class="hp-why__desc">Chất lượng cao không đồng nghĩa với giá cao. Jollibug luôn có ưu đãi hấp dẫn giúp bạn ăn ngon mà không lo chi phí.</div>
          </article>
        </div>
      </div>
    </section>

  </main>

  <%-- 7. FOOTER (shared) --%>
  <jsp:include page="layout/footer.jsp" />

  <script>
    /* Category bar active toggle */
    document.querySelectorAll('.hp-cat__item').forEach(function(el) {
      el.addEventListener('click', function() {
        document.querySelectorAll('.hp-cat__item').forEach(function(i){ i.classList.remove('active'); });
        el.classList.add('active');
      });
    });
  </script>

</body>
</html>
