<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Quản lý danh mục</title>
  <meta name="description" content="Jollibug Manager - quản lý danh mục thực đơn để khách hàng lọc món nhanh hơn." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>
<body data-admin-role="manager" data-admin-page="categories">
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
          <a class="is-active" href="/categories">Quản lý danh mục</a>
          <a href="/products">Quản lý sản phẩm</a>
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
          <span class="muted">Quản lý danh mục món ăn với thao tác cập nhật trực tiếp.</span>
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
            <h1 class="section-title" id="admin-table-title">Quản lý danh mục</h1>
            <p class="muted" id="admin-table-subtitle"></p>
          </div>
          <div class="panel-controls">
            <label class="table-search">
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"></circle>
                <path d="m20 20-3.5-3.5"></path>
              </svg>
              <input id="admin-table-search" type="search" placeholder="Tìm danh mục hoặc slug" />
            </label>
            <button class="btn btn-primary" type="button" data-admin-open-modal id="admin-table-add-button">
              Thêm danh mục mới
            </button>
          </div>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table">
            <thead>
              <tr id="admin-table-head-row">
                <th>Danh mục</th>
                <th>Slug</th>
                <th>Số món</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody id="admin-table-body">
              <tr>
                <td>Burger đặc trưng</td>
                <td>signature-burgers</td>
                <td>12</td>
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
      <td data-cell="4"></td>
      <td data-cell="5"></td>
    </tr>
  </template>

  <template id="admin-table-empty-template">
    <tr>
      <td>
        <div class="empty-state">
          <h3>Không tìm thấy danh mục.</h3>
          <p class="muted">Hãy đổi từ khóa tìm kiếm hoặc thêm danh mục mới.</p>
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
         id="sdp-category"
         role="dialog"
         aria-modal="true"
         aria-labelledby="sdp-name"
         data-detail-panel>
    <div class="sdp__card">
      <div class="sdp__hero">
        <div class="sdp__icon" id="sdp-icon" data-tone aria-hidden="true">C</div>

        <div class="sdp__hero-meta">
          <h2 class="sdp__title" id="sdp-name">Tên danh mục</h2>
          <span class="status-badge sdp__status" id="sdp-status" data-status="active">đang hoạt động</span>
        </div>
      </div>

      <div class="sdp__metrics">
        <div class="sdp__metric">
          <span class="sdp__metric-label">Tổng số món</span>
          <strong class="sdp__metric-value" id="sdp-items">--</strong>
        </div>
      </div>

      <section class="sdp__section">
        <h3 class="sdp__section-title">Thông tin nhận diện</h3>
        <dl class="sdp__fields">
          <div class="sdp__field">
            <dt>ID</dt>
            <dd id="sdp-id">--</dd>
          </div>

          <div class="sdp__field">
            <dt>Tên danh mục</dt>
            <dd id="sdp-name-field">--</dd>
          </div>

          <div class="sdp__field">
            <dt>Slug URL</dt>
            <dd><code class="sdp__code" id="sdp-slug">--</code></dd>
          </div>

          <div class="sdp__field">
            <dt>Trạng thái</dt>
            <dd><span id="sdp-status-text">--</span></dd>
          </div>
        </dl>
      </section>

      <section class="sdp__section">
        <h3 class="sdp__section-title">Sản phẩm trong danh mục này</h3>
        <div style="overflow-x:auto; border-radius:var(--radius-md); border:1px solid rgba(111,82,55,0.08);">
          <table class="history-table" style="width:100%;">
            <thead>
              <tr>
                <th>Sản phẩm</th>
                <th>Giá</th>
                <th>Trạng thái</th>
              </tr>
            </thead>
            <tbody id="sdp-category-items-body">
              <tr>
                <td colspan="3" style="text-align:center; color:var(--color-ink-500); padding:1rem;">
                  Chọn một danh mục để xem các sản phẩm bên trong.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
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
          Chỉnh sửa danh mục
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
  <script src="js/manager/category.js" defer></script>
</body>
</html>
