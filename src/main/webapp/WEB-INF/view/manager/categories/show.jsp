<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Quản lý danh mục</title>
  <meta name="description" content="Jollibug Manager - sắp xếp danh mục, làm nổi bật nhóm ưu tiên và giữ cấu trúc menu dễ theo dõi." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/admin.css" />
</head>

<!--
  data-admin-role -> "manager"  - dùng cho Spring Security
  data-admin-page -> "categories" - được category.js đọc
-->
<body data-admin-role="manager" data-admin-page="categories">

  <div class="admin-shell admin-body" data-admin-table-root>

    <jsp:include page="../layout/sidebar.jsp" />

    <main class="admin-main">

      <jsp:include page="../layout/topbar.jsp" />

      <section class="admin-panel">
        <div class="panel-header">
          <div class="stack" style="gap:0.3rem;">
            <h1 class="section-title" id="admin-table-title">Quản lý danh mục</h1>
          </div>
          <div class="panel-controls">
            <label class="table-search">
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"></circle>
                <path d="m20 20-3.5-3.5"></path>
              </svg>
              <input id="admin-table-search" type="search" placeholder="Tìm danh mục hoặc slug" />
            </label>
            <a href="/manager/categories/create" class="btn btn-primary" type="button" data-admin-open-modal id="admin-table-add-button">
              Thêm danh mục mới
            </a>
          </div>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table">
            <thead>
              <tr id="admin-table-head-row">
                <th>Danh mục</th>
                <th>Số món</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody id="admin-table-body">
              <tr>
                <td>Burger đặc trưng</td>
                <td>12</td>
                <td><span class="status-badge" data-status="active">đang hoạt động</span></td>
                <td>
                  <div class="action-row">
                    <a href="/manager/categories/detail" class="btn btn-ghost icon-btn" type="button">Xem</a>
                    <a href="/manager/categories/update" class="btn btn-ghost icon-btn" type="button">Sửa</a>
                    <a href="/manager/categories/delete" class="btn btn-ghost icon-btn" type="button">Xóa</a>
                  </div>
                </td>
              </tr>
              <tr>
                <td>Đồ uống & Sinh tố</td>
                <td>9</td>
                <td><span class="status-badge" data-status="featured">nổi bật</span></td>
                <td>
                  <div class="action-row">
                    <a href="/manager/categories/detail" class="btn btn-ghost icon-btn" type="button">Xem</a>
                    <a href="/manager/categories/update" class="btn btn-ghost icon-btn" type="button">Sửa</a>
                    <a href="/manager/categories/delete" class="btn btn-ghost icon-btn" type="button">Xóa</a>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

    </main>
  
  </body>
</html>




