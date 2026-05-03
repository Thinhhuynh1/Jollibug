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
  <link rel="stylesheet" href="<c:url value='/css/client/profile.css'/>" />
</head>

<body data-admin-role="admin" data-admin-page="manage-users">

  <div class="admin-shell admin-body" data-admin-table-root>

    <jsp:include page="../layout/sidebar.jsp" />

    <main class="admin-main">

      <jsp:include page="../layout/topbar.jsp" />

      <section class="profile-content">
        <div style="max-width: 48rem; margin: 0 auto; width: 100%;">
          <div class="panel-header">
            <div class="stack" style="gap:0.25rem;">
                <h2 class="section-title">Cập nhật người dùng</h2>
            </div>
          </div>

          <form action="/admin/users/update" method="POST" class="admin-card" style="margin-top:1rem; padding: 1.75rem;">
              <input type="hidden" name="id" value="USR-000127" />
              <div class="profile-grid">
                  <!-- Hàng 1: Họ tên + Số điện thoại -->
                  <label class="profile-field">
                      <span>Họ và tên <span style="color:var(--color-red-500);">*</span></span>
                      <input type="text" name="fullName" value="Nguyễn Minh Quân" placeholder="Nhập họ và tên" required />
                  </label>
                  
                  <label class="profile-field">
                      <span>Số điện thoại</span>
                      <input type="tel" name="phone" value="0909123456" placeholder="Nhập số điện thoại" />
                  </label>

                  <!-- Hàng 2: Email + Mật khẩu mới -->
                  <label class="profile-field">
                      <span>Email <span style="color:var(--color-red-500);">*</span></span>
                      <input type="email" name="email" value="quan.nguyen@jollibug.vn" placeholder="Ví dụ: example@jollibug.vn" required disabled />
                  </label>
                  
                  <label class="profile-field">
                      <span>Mật khẩu mới</span>
                      <input type="password" name="password" placeholder="Để trống nếu không đổi mật khẩu" />
                  </label>
                  
                  <!-- Hàng 3: Vai trò + Trạng thái -->
                  <label class="profile-field">
                      <span>Vai trò <span style="color:var(--color-red-500);">*</span></span>
                      <select name="role" required style="width: 100%; border: 1px solid rgba(111,82,55,0.14); border-radius: var(--radius-md); padding: 0.65rem 1rem; font-family: var(--font-body); font-size: 0.9rem; background: #fff; outline: none; transition: border-color 0.15s;">
                          <option value="CLIENT">Khách hàng</option>
                          <option value="STAFF">Nhân viên (Staff)</option>
                          <option value="MANAGER" selected>Quản lý (Manager)</option>
                          <option value="ADMIN">Quản trị viên (Admin)</option>
                      </select>
                  </label>
                  
                  <label class="profile-field">
                      <span>Trạng thái</span>
                      <select name="status" style="width: 100%; border: 1px solid rgba(111,82,55,0.14); border-radius: var(--radius-md); padding: 0.65rem 1rem; font-family: var(--font-body); font-size: 0.9rem; background: #fff; outline: none; transition: border-color 0.15s;">
                          <option value="ACTIVE" selected>Đang hoạt động</option>
                          <option value="BLOCKED">Khóa</option>
                      </select>
                  </label>

                  <label class="profile-field">
                      <span>Mã người dùng</span>
                      <input type="text" value="USR-000127" disabled />
                  </label>

                  <label class="profile-field">
                      <span>Ngày tham gia</span>
                      <input type="text" value="12/01/2026" disabled />
                  </label>
              </div>
              
              <div class="modal__actions" style="margin-top: 2.5rem; justify-content: flex-end;">
                  <a href="/admin/users" class="btn btn-ghost">Quay lại</a>
                  <button type="submit" class="btn btn-primary">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16" aria-hidden="true" style="margin-right:0.3rem;">
                          <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/><polyline points="17 21 17 13 7 13 7 21"/><polyline points="7 3 7 8 15 8"/>
                      </svg>
                      Lưu thay đổi
                  </button>
              </div>
          </form>
        </div>    </section>


    </main>
  </div><!-- /data-admin-table-root -->


  <!-- Toast stack -->

  </body>
</html>

