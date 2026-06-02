<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Đánh giá của tôi</title>
  <meta name="description" content="Quản lý các đánh giá món ăn của bạn trên Jollibug. Thêm, sửa và xóa nhận xét dễ dàng." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/styles.css" />
</head>
<body>

  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">

        <div class="page-intro">
          <span class="eyebrow">Tài khoản của tôi</span>
          <h1 class="section-title">Đánh giá của tôi</h1>
          <p class="lead">Đánh giá các món bạn đã đặt. Phản hồi của bạn giúp Jollibug phục vụ tốt hơn.</p>
        </div>

        <div style="display:flex; flex-wrap:wrap; align-items:center; justify-content:space-between; gap:1rem; margin-bottom:2rem;">
          <label class="searchbar" style="flex:1; max-width:24rem;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
              <circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/>
            </svg>
            <input type="search" id="review-search" placeholder="Tìm trong các đánh giá của bạn..." />
          </label>
          <button class="btn btn-primary" type="button" id="btn-write-review">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16" aria-hidden="true">
              <path d="M5 12h14M12 5v14"/>
            </svg>
            Viết đánh giá
          </button>
        </div>

        <div class="card-grid" id="reviews-grid" style="grid-template-columns: repeat(auto-fill, minmax(20rem, 1fr));">
        </div>

        <div class="empty-state hidden" id="reviews-empty">
          <p style="font-size:2.5rem; margin-bottom:0.75rem;">📝</p>
          <h3>Bạn chưa có đánh giá nào</h3>
          <p class="muted">Hãy đặt món và chia sẻ trải nghiệm của bạn. Đánh giá đầu tiên đang chờ bạn.</p>
          <a href="/menu" class="btn btn-primary" style="margin-top:1.25rem;">Xem thực đơn</a>
        </div>

      </div>
    </section>
  </main>

  <div class="modal admin-modal" id="review-modal" aria-modal="true" role="dialog" aria-labelledby="review-modal-title"
       style="display:none; position:fixed; inset:0; z-index:900; place-items:center; background:rgba(24,12,4,0.5); backdrop-filter:blur(4px);">
    <div class="modal__dialog" style="width:min(100%,38rem); background:#fffefb; border-radius:var(--radius-xl); padding:0; overflow:hidden; box-shadow:var(--shadow-lg);">
      <div class="modal__header" style="padding:1.5rem; border-bottom:1px solid rgba(111,82,55,0.08);">
        <div>
          <h2 id="review-modal-title" style="font-size:1.3rem;">Viết đánh giá</h2>
          <p class="muted" style="margin-top:0.2rem;">Chia sẻ trải nghiệm thật của bạn về món ăn này.</p>
        </div>
        <button class="btn btn-outline" type="button" id="btn-close-review-modal" style="flex-shrink:0;">Đóng</button>
      </div>
      <form id="review-form" class="admin-modal__grid" style="padding:1.5rem;" novalidate>
        <label class="field-label">
          <span>Sản phẩm</span>
          <select id="review-product">
            <option value="">Chọn một sản phẩm...</option>
            <option value="Smoky Double Burger">Smoky Double Burger</option>
            <option value="Grilled Chicken Wrap">Grilled Chicken Wrap</option>
            <option value="BBQ Beef Burger">BBQ Beef Burger</option>
            <option value="Crispy Fries (L)">Crispy Fries (L)</option>
            <option value="Spicy Chicken Burger">Spicy Chicken Burger</option>
            <option value="Classic Cheeseburger">Classic Cheeseburger</option>
          </select>
        </label>

        <div>
          <label class="field-label" style="margin-bottom:0.5rem;"><span>Đánh giá sao</span></label>
          <div class="cluster" id="star-picker" style="gap:0.3rem;" role="group" aria-label="Đánh giá sao">
          </div>
          <input type="hidden" id="review-rating" value="0" />
        </div>

        <label class="field-label">
          <span>Nhận xét của bạn</span>
          <textarea id="review-text" placeholder="Bạn thích hay chưa hài lòng điều gì ở món này?" style="min-height:7rem;"></textarea>
        </label>

        <button class="btn btn-primary" type="button" id="btn-save-review">
          Lưu đánh giá
        </button>
      </form>
    </div>
  </div>

  <template id="review-card-template">
    <article class="card" style="border-radius:var(--radius-xl);">
      <div class="card__body">
        <div style="display:flex; align-items:flex-start; justify-content:space-between; gap:0.75rem; margin-bottom:0.85rem;">
          <div style="display:grid; gap:0.2rem;">
            <strong data-product style="font-size:1rem;"></strong>
            <span class="muted" data-date style="font-size:0.8rem;"></span>
          </div>
          <span data-stars style="font-size:1.2rem; letter-spacing:0.05em;"></span>
        </div>
        <p data-text style="font-size:0.92rem; color:var(--color-ink-700); line-height:1.6; margin-bottom:1rem;"></p>
        <div class="cluster" style="gap:0.5rem;">
          <button class="btn btn-outline" type="button" data-edit style="padding:0.55rem 0.9rem; min-height:2.2rem; font-size:0.85rem;">Sửa</button>
          <button class="btn btn-ghost" type="button" data-delete style="padding:0.55rem 0.9rem; min-height:2.2rem; font-size:0.85rem; color:var(--color-danger);">Xóa</button>
        </div>
      </div>
    </article>
  </template>

  <footer class="site-footer">
    <div class="container">
      <div class="footer-note">
        <span>© 2026 Jollibug. Bảo lưu mọi quyền.</span>
        <a href="/">Về trang chủ</a>
      </div>
    </div>
  </footer>

  <div class="toast-stack" id="client-toast-stack" style="position:fixed; bottom:1.5rem; right:1.5rem; z-index:9999; display:grid; gap:0.65rem;"></div>

  <script src="js/client/nav.js" defer></script>
  <script src="js/client/reviews.js" defer></script>
</body>
</html>
