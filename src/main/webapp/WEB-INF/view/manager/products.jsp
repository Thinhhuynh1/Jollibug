<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Quản lý sản phẩm</title>
  <meta name="description" content="Jollibug Manager - cập nhật hình ảnh, giá bán, danh mục và tồn kho sản phẩm." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>
<body data-admin-role="manager" data-admin-page="products">
  <div class="admin-shell admin-body" data-admin-table-root>
    <aside class="admin-sidebar">
      <div class="admin-sidebar__inner">
        <div class="admin-brand">
          <div class="brand">
            <span class="brand__mark">JB</span>
            <span class="brand__copy">
              <span class="brand__title">Jollibug Admin</span>
              <span class="brand__tag">Trung tâm điều hành</span>
            </span>
          </div>
          <span class="admin-role">Quản lý</span>
          <p>Điều hành thực đơn, danh mục và đơn hàng</p>
        </div>

        <nav class="admin-nav">
          <span class="admin-nav__section">Không gian làm việc</span>
          <a href="/admin">Bảng điều khiển</a>
          <a href="/categories">Quản lý danh mục</a>
          <a class="is-active" href="/products">Quản lý sản phẩm</a>
          <a href="manager-orders.html">Quản lý đơn hàng</a>
          <span class="admin-nav__section">Liên kết nhanh</span>
          <a href="index.html">Quay lại trang chủ</a>
        </nav>
      </div>
    </aside>

    <main class="admin-main">
      <div class="admin-topbar">
        <div class="admin-topbar__copy">
          <strong>Trung tâm điều hành Jollibug</strong>
          <span class="muted">Quản lý danh mục sản phẩm, hình ảnh, giá bán và tồn kho.</span>
        </div>
        <div class="admin-topbar__user">
          <span class="admin-role">Quản lý</span>
          <div class="admin-avatar" id="topbar-user-initials">--</div>
          <div class="stack" style="gap:0.15rem;">
            <strong id="topbar-user-name">Đang tải...</strong>
            <span class="muted" id="topbar-user-role">Quản lý</span>
          </div>
          <button class="btn btn-outline" type="button" data-admin-logout id="btn-logout">Đăng xuất</button>
        </div>
      </div>

      <section class="admin-panel">
        <div class="panel-header">
          <div class="stack" style="gap:0.3rem;">
            <span class="eyebrow" id="admin-table-eyebrow">Quản lý</span>
            <h1 class="section-title" id="admin-table-title">Quản lý sản phẩm</h1>
            <p class="muted" id="admin-table-subtitle"></p>
          </div>
          <div class="panel-controls">
            <label class="table-search">
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"></circle>
                <path d="m20 20-3.5-3.5"></path>
              </svg>
              <input id="admin-table-search" type="search" placeholder="Tìm sản phẩm, danh mục hoặc trạng thái" />
            </label>
            <button class="btn btn-primary" type="button" data-admin-open-modal id="admin-table-add-button">
              Thêm sản phẩm mới
            </button>
          </div>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table">
            <thead>
              <tr id="admin-table-head-row">
                <th>Sản phẩm</th>
                <th>Danh mục</th>
                <th>Giá</th>
                <th>Số lượng tồn</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody id="admin-table-body">
              <tr>
                <td>Burger đôi xông khói</td>
                <td>Burger đặc trưng</td>
                <td>9,90 USD</td>
                <td>25</td>
                <td><span class="status-badge" data-status="active">đang hoạt động</span></td>
                <td>
                  <div class="cluster">
                    <button class="btn btn-outline" type="button">Xem</button>
                    <button class="btn btn-outline" type="button">Sửa</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </main>
  </div>

  <template id="admin-table-row-template">
    <tr>
      <td data-cell="0"></td>
      <td data-cell="1"></td>
      <td data-cell="2"></td>
      <td data-cell="3"></td>
      <td data-cell="5"></td>
    </tr>
  </template>

  <template id="admin-table-empty-template">
    <tr>
      <td>
        <div class="empty-state">
          <h3>Không tìm thấy sản phẩm.</h3>
          <p class="muted">Hãy đổi từ khóa tìm kiếm hoặc thêm sản phẩm mới.</p>
        </div>
      </td>
    </tr>
  </template>

  <div class="modal admin-modal" data-admin-modal id="admin-modal"
       aria-modal="true" role="dialog" aria-labelledby="admin-modal-title">
    <div class="modal__dialog">
      <div class="modal__header">
        <div class="stack" style="gap:0.25rem;">
          <h2 data-admin-modal-title id="admin-modal-title">Cửa sổ chỉnh sửa</h2>
          <span class="muted" data-admin-modal-copy>Cập nhật thông tin và lưu ngay.</span>
        </div>
        <button class="btn btn-outline" type="button" data-admin-close-modal id="btn-close-modal">Đóng</button>
      </div>
      <form data-admin-form class="admin-modal__grid" id="admin-modal-form" novalidate></form>
    </div>
  </div>

  <aside class="sdp sdp--hidden"
         id="sdp-product"
         role="dialog"
         aria-modal="true"
         aria-labelledby="sdp-product-name"
         data-detail-panel>
    <div class="sdp__backdrop" data-sdp-close></div>

    <div class="sdp__card">
      <button class="sdp__close-btn"
              type="button"
              data-sdp-close
              id="btn-sdp-close"
              aria-label="Đóng chi tiết sản phẩm">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
             width="20" height="20" aria-hidden="true">
          <path d="M18 6 6 18M6 6l12 12"/>
        </svg>
      </button>

      <div class="sdp__product-media">
        <img class="sdp__product-img"
             id="sdp-product-image"
             src=""
             alt="Sản phẩm"
             width="120" height="120"
             loading="lazy" />
        <span class="status-badge sdp__product-badge"
              id="sdp-product-badge"
              data-status="active">đang hoạt động</span>
      </div>

      <div class="sdp__hero sdp__hero--product">
        <div class="sdp__hero-meta">
          <h2 class="sdp__title" id="sdp-product-name">Tên sản phẩm</h2>
          <span class="sdp__price" id="sdp-product-price">0 VND</span>
        </div>
        <span class="sdp__cat-pill" id="sdp-product-cat">Danh mục</span>
      </div>

      <section class="sdp__section">
        <h3 class="sdp__section-title">Chi tiết sản phẩm</h3>
        <dl class="sdp__fields">
          <div class="sdp__field">
            <dt>Mã sản phẩm</dt>
            <dd id="sdp-product-id">--</dd>
          </div>

          <div class="sdp__field">
            <dt>Danh mục</dt>
            <dd id="sdp-product-cat-field">--</dd>
          </div>

          <div class="sdp__field">
            <dt>Giá</dt>
            <dd id="sdp-product-price-field">--</dd>
          </div>

          <div class="sdp__field">
            <dt>Số lượng tồn</dt>
            <dd id="sdp-product-stock">--</dd>
          </div>

          <div class="sdp__field">
            <dt>Trạng thái tồn kho</dt>
            <dd>
              <span class="status-badge" id="sdp-product-status" data-status="active">đang hoạt động</span>
            </dd>
          </div>

          <div class="sdp__field" style="grid-template-columns:8rem 1fr; align-items:start;">
            <dt>Mô tả</dt>
            <dd id="sdp-product-desc" style="white-space:pre-wrap; font-size:0.88rem; color:var(--color-ink-700); line-height:1.55;">--</dd>
          </div>
        </dl>
      </section>

      <div class="sdp__actions">
        <button class="btn btn-primary sdp__action-btn"
                type="button"
                data-detail-edit=""
                id="sdp-edit-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
               width="16" height="16" aria-hidden="true">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
          </svg>
          Chỉnh sửa sản phẩm
        </button>
        <button class="btn btn-ghost sdp__action-btn"
                type="button"
                data-sdp-close>
          Đóng
        </button>
      </div>
    </div>
  </aside>

  <div class="toast-stack" data-admin-toast-stack id="admin-toast-stack"></div>
  <script src="js/manager/product.js" defer></script>
</body>
</html>
