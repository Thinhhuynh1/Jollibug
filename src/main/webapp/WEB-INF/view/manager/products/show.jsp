<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Quản lý sản phẩm</title>
  <meta name="description" content="Jollibug Manager - quản lý hình ảnh, giá bán, danh mục và trạng thái tồn kho của sản phẩm." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/admin.css" />
</head>

  <!--
    data-admin-role -> "manager"  - dùng cho Spring Security
    data-admin-page -> "products" - được product.js đọc
  -->
<body data-admin-role="manager" data-admin-page="products">

  <div class="admin-shell admin-body" data-admin-table-root>

    <jsp:include page="../layout/sidebar.jsp" />

    <main class="admin-main">

      <jsp:include page="../layout/topbar.jsp" />

      <section class="admin-panel" style="margin-bottom:var(--space-6);">
        <div class="panel-header" style="align-items:flex-start;">
          <div class="stack" style="gap:0.35rem; max-width:42rem;">
            <h1 class="section-title" id="admin-table-title" style="margin:0;">Quản lý sản phẩm</h1>
          </div>
          <div class="panel-controls">
            <label class="table-search">
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"></circle>
                <path d="m20 20-3.5-3.5"></path>
              </svg>
              <input id="admin-table-search" type="search" placeholder="Tìm sản phẩm, danh mục hoặc trạng thái" />
            </label>
            <a href="/manager/products/create" class="btn btn-primary">
              Thêm sản phẩm mới
            </a>
          </div>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table">
            <thead>
              <tr id="admin-table-head-row">
                <th>Sản phẩm</th>
                <th>Danh mục</th>
                <th>Giá</th>
                <th>Tồn kho</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody id="admin-table-body">
              <tr>
                <td>Burger đôi khói</td>
                <td>Burger đặc trưng</td>
                <td>249.000đ</td>
                <td>36</td>
                <td><span class="status-badge" data-status="active">đang hoạt động</span></td>
                <td>
                  <div class="action-row">
                    <a href="/manager/products/detail" class="btn btn-ghost icon-btn" type="button">Xem</a>
                    <a href="/manager/products/update" class="btn btn-ghost icon-btn" type="button">Sửa</a>
                    <a href="/manager/products/delete" class="btn btn-ghost icon-btn" type="button">Xóa</a>
                  </div>
                </td>
              </tr>
              <tr>
                <td>Khoai tây lắc phô mai</td>
                <td>Món ăn kèm</td>
                <td>59.000đ</td>
                <td>0</td>
                <td><span class="status-badge" data-status="out-of-stock">hết hàng</span></td>
                <td>
                  <div class="action-row">
                    <a href="/manager/products/detail" class="btn btn-ghost icon-btn" type="button">Xem</a>
                    <a href="/manager/products/update" class="btn btn-ghost icon-btn" type="button">Sửa</a>
                    <a href="/manager/products/delete" class="btn btn-ghost icon-btn" type="button">Xóa</a>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

    </main>
  </div><!-- /data-admin-table-root -->
  </body>
</html>




