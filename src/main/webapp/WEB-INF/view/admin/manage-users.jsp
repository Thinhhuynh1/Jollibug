<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Admin â€” Manage Users</title>
  <meta name="description" content="Jollibug Super Admin â€” centralized user management for all roles: Staff, Manager, and Client." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
</head>

<!--
  data-admin-role="admin"       â†’ Spring Security: ROLE_SUPER_ADMIN
  data-admin-page="manage-users" â†’ manage-users.js render branch
-->
<body data-admin-role="admin" data-admin-page="manage-users">

  <div class="admin-shell admin-body" data-admin-table-root>

    <jsp:include page="layout/sidebar.jsp" />

    <main class="admin-main">

      <jsp:include page="layout/topbar.jsp" />

      <div class="metric-grid" style="margin-bottom:var(--space-6);">
        <article class="metric-card">
          <span class="muted">Tổng người dùng</span>
          <strong id="metric-total-users">0</strong>
          <span class="metric-delta" data-tone="info">Tất cả vai trò</span>
        </article>
        <article class="metric-card">
          <span class="muted">Quản lý</span>
          <strong id="metric-managers">0</strong>
          <span class="metric-delta" data-tone="info">Hoạt động</span>
        </article>
        <article class="metric-card">
          <span class="muted">Nhân viên</span>
          <strong id="metric-staff">0</strong>
          <span class="metric-delta" data-tone="up">Hoạt động</span>
        </article>
        <article class="metric-card">
          <span class="muted">Tài khoản bị hạn chế</span>
          <strong id="metric-blocked">0</strong>
          <span class="metric-delta" data-tone="warm">Bị khóa</span>
        </article>
      </div>

      <section class="admin-panel">
        <div class="panel-header">
          <div class="stack" style="gap:0.3rem;">
            <h1 class="section-title">Quản lý người dùng</h1>
          </div>
          <div class="panel-controls">
            <!-- Status tabs -->
            <div class="order-filter-strip__pills" role="tablist" aria-label="Filter by account status" style="gap:0.35rem;">
              <button class="btn btn-primary" role="tab" type="button" data-status-tab="active" id="tab-active" style="background-color: green; color: white;">Đang hoạt động</button>
              <button class="btn btn-primary" role="tab" type="button" data-status-tab="blocked" id="tab-blocked" style="background-color: red; color: white;">Bị khóa</button>
            </div>
            <!-- Role filter dropdown -->
            <div class="select-group" style="gap:0;">
              <select id="role-filter" aria-label="Filter by role" style="padding:0.8rem 1rem;">
                <option value="all">Vai trò</option>
                <option value="manager">Quản lý</option>
                <option value="staff">Nhân viên</option>
                <option value="client">Khách hàng</option>
              </select>
            </div>
            <!-- Search -->
            <label class="table-search">
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/>
              </svg>
              <input id="user-search" type="search" placeholder="Search name or email" />
            </label>
            <button class="btn btn-primary" type="button" id="btn-add-user">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15" aria-hidden="true">
                <path d="M5 12h14M12 5v14"/>
              </svg>
              Thêm người dùng
            </button>
          </div>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table" id="users-table">
            <thead>
              <tr>
                <th>Người dùng</th>
                <th>Email</th>
                <th>Vai trò</th>
                <th>Trạng thái</th>
                <th>Ngày tham gia</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody id="users-table-body">
              <tr>
                <td>Nguyễn Minh Quân</td>
                <td>quan.nguyen@jollibug.vn</td>
                <td>Quản lý</td>
                <td>Đang hoạt động</td>
                <td>12/01/2026</td>
                <td>
                  <button class="btn btn-ghost" type="button">Xem</button>
                  <button class="btn btn-ghost" type="button">Sửa</button>
                  <button class="btn btn-ghost" type="button">Xóa</button>
                  <button class="btn btn-ghost" type="button">Khóa</button>
                </td>
              </tr>
              <tr>
                <td>Trần Thu Hà</td>
                <td>ha.tran@jollibug.vn</td>
                <td>Nhân viên</td>
                <td>Đang hoạt động</td>
                <td>25/11/2025</td>
                <td>
                  <button class="btn btn-ghost" type="button">Xem</button>
                  <button class="btn btn-ghost" type="button">Sửa</button>
                  <button class="btn btn-ghost" type="button">Xóa</button>
                  <button class="btn btn-ghost" type="button">Khóa</button>
                </td>
              </tr>
              <tr>
                <td>Lê Hoàng Phúc</td>
                <td>phuc.le@gmail.com</td>
                <td>Khách hàng</td>
                <td>Đang hoạt động</td>
                <td>03/08/2025</td>
                <td>
                  <button class="btn btn-ghost" type="button">Xem</button>
                  <button class="btn btn-ghost" type="button">Sửa</button>
                  <button class="btn btn-ghost" type="button">Xóa</button>
                  <button class="btn btn-ghost" type="button">Khóa</button>
                </td>
              </tr>
              <tr>
                <td>Phạm Gia Huy</td>
                <td>huy.pham@yahoo.com</td>
                <td>Khách hàng</td>
                <td>Bị khóa</td>
                <td>17/06/2025</td>
                <td>
                  <button class="btn btn-ghost" type="button">Xem</button>
                  <button class="btn btn-ghost" type="button">Sửa</button>
                  <button class="btn btn-ghost" type="button">Xóa</button>
                  <button class="btn btn-ghost" type="button">Mở khóa</button>
                </td>
              </tr>
              <tr>
                <td>Đỗ Khánh Linh</td>
                <td>linh.do@jollibug.vn</td>
                <td>Nhân viên</td>
                <td>Bị khóa</td>
                <td>30/09/2025</td>
                <td>
                  <button class="btn btn-ghost" type="button">Xem</button>
                  <button class="btn btn-ghost" type="button">Sửa</button>
                  <button class="btn btn-ghost" type="button">Xóa</button>
                  <button class="btn btn-ghost" type="button">Mở khóa</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>


    </main>
  </div><!-- /data-admin-table-root -->


  <!-- Toast stack -->

<script src="<c:url value='/js/admin/manage-users.js'/>" defer></script>
  </body>
</html>

