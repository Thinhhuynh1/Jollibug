<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Admin Dashboard</title>
  <meta name="description" content="Jollibug Super Admin dashboard for managing users, roles, and account status." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
</head>

  <body data-admin-role="admin" data-admin-page="manage-users">

  <div class="admin-shell admin-body" data-admin-table-root>
    <jsp:include page="layout/sidebar.jsp" />

    <main class="admin-main">

      <jsp:include page="layout/topbar.jsp" />

      <section class="admin-dashboard">
        <section class="admin-panel" style="margin-bottom:var(--space-6); position:relative; overflow:hidden;">
          <div style="position:absolute; inset:auto -6rem -5rem auto; width:16rem; height:16rem; border-radius:50%; background:radial-gradient(circle, rgba(255,209,120,0.32), rgba(255,209,120,0)); pointer-events:none;"></div>
          <div class="panel-header" style="margin-bottom:0; align-items:flex-start;">
            <div class="stack" style="gap:0.45rem; max-width:42rem;">              <h1 class="section-title" style="margin:0;">Dashboard quản trị người dùng</h1>
              <p class="muted" style="margin:0; line-height:1.65;">Theo dõi tổng quan tài khoản, trạng thái hoạt động và thao tác nhanh với người dùng. Phần doanh thu món ăn sẽ tách ra ở dashboard riêng sau.</p>
            </div>
            <div class="panel-controls">
              <a href="/admin/users/create" class="btn btn-primary">Thêm người dùng</a>
              <a href="/admin/users" class="btn btn-ghost">Xem danh sách</a>
            </div>
          </div>
        </section>

        <div class="metric-grid" style="margin-bottom:var(--space-6);">
          <article class="metric-card">
            <span class="muted">Tổng người dùng</span>
            <strong id="metric-total-users">128</strong>
            <span class="metric-delta" data-tone="info">Tất cả vai trò</span>
          </article>
          <article class="metric-card">
            <span class="muted">Đang hoạt động</span>
            <strong id="metric-active-users">112</strong>
            <span class="metric-delta" data-tone="up">Tài khoản live</span>
          </article>
          <article class="metric-card">
            <span class="muted">Quản lý & Staff</span>
            <strong id="metric-ops-users">34</strong>
            <span class="metric-delta" data-tone="info">Khối vận hành</span>
          </article>
          <article class="metric-card">
            <span class="muted">Tài khoản bị hạn chế</span>
            <strong id="metric-blocked">16</strong>
            <span class="metric-delta" data-tone="warm">Cần xử lý</span>
          </article>
        </div>

        <div class="admin-dashboard__bottom" style="margin-bottom:var(--space-6);">
          <section class="admin-panel">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Trạng thái người dùng</strong>
                <span class="muted">Bức tranh nhanh về các nhóm tài khoản trong hệ thống.</span>
              </div>
            </div>
            <div class="mini-stat-grid">
              <article class="mini-stat">
                <span class="muted">Khách hàng</span>
                <strong>94</strong>
              </article>
              <article class="mini-stat">
                <span class="muted">Nhân viên</span>
                <strong>22</strong>
              </article>
              <article class="mini-stat">
                <span class="muted">Quản lý</span>
                <strong>12</strong>
              </article>
            </div>
          </section>

          <section class="admin-activity">
            <div class="panel-header">
              <div class="stack" style="gap:0.2rem;">
                <strong>Thao tác nhanh</strong>
                <span class="muted">Các lối tắt phục vụ quản trị user thường xuyên.</span>
              </div>
            </div>
            <div class="activity-list">
              <article class="activity-item">
                <strong>Tạo người dùng mới</strong>
                <span class="muted">Thêm tài khoản khách hàng, staff hoặc manager.</span>
              </article>
              <article class="activity-item">
                <strong>Kiểm tra tài khoản bị khóa</strong>
                <span class="muted">Rà soát những user cần mở khóa hoặc xử lý.</span>
              </article>
              <article class="activity-item">
                <strong>Đi tới danh sách chi tiết</strong>
                <span class="muted">Xem, sửa và xóa từng người dùng trong hệ thống.</span>
              </article>
            </div>
          </section>
        </div>

        
      </section>
    </main>
  </div>

  </body> 
</html>

