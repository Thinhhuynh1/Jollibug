<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | ADMIN</title>
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
                <h2 class="section-title">Thêm mới người dùng</h2>
            </div>
          </div>

          <form action="/admin/users/create" method="post" class="admin-card" style="margin-top:1rem; padding: 1.75rem;">
              <div class="profile-grid">
                  <!-- Hàng 1: Họ tên + Số điện thoại -->
                  <label class="profile-field">
                      <span>Họ và tên <span style="color:var(--color-red-500);">*</span></span>
                      <input type="text" name="HoTen" placeholder="Nhập họ và tên" required />
                  </label>
                  
                  <label class="profile-field">
                      <span>Số điện thoại</span>
                      <input type="tel" name="SDT" placeholder="Nhập số điện thoại" />
                  </label>

                  <!-- Hàng 2: Email + Mật khẩu -->
                  <label class="profile-field">
                      <span>Email <span style="color:var(--color-red-500);">*</span></span>
                      <input type="email" name="Email" placeholder="Ví dụ: example@jollibug.vn" required />
                  </label>
                  
                  <label class="profile-field">
                      <span>Mật khẩu khởi tạo <span style="color:var(--color-red-500);">*</span></span>
                      <input type="password" name="Password" placeholder="Nhập mật khẩu" required />
                  </label>
                  
                  <!-- Hàng 3: Vai trò + Trạng thái -->
                  <label class="profile-field">
                      <span>Vai trò <span style="color:var(--color-red-500);">*</span></span>
                      <select name="TenVT" required>
                          <option value="">-- Chọn Vai trò --</option>
                          <option value="Client">Khách hàng </option>
                          <option value="Staff">Nhân viên </option>
                          <option value="Manager">Quản lý</option>
                          <option value="Admin">Quản trị viên</option>
                      </select>
                  </label>
                  
                  <label class="profile-field">
                      <span>Trạng thái</span>
                      <select name="TrangThai">
                          <option value="ACTIVE">Đang hoạt động</option>
                          <option value="BANNED">Khóa</option>
                      </select>
                  </label>
              </div>
              
              <div class="modal__actions" style="margin-top: 2.5rem; justify-content: flex-end;">
                  <a href="/admin/users" class="btn btn-ghost">Hủy</a>
                  <button type="submit" class="btn btn-primary">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16" aria-hidden="true" style="margin-right:0.3rem;">
                          <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="8.5" cy="7" r="4"/><line x1="20" y1="8" x2="20" y2="14"/><line x1="23" y1="11" x2="17" y2="11"/>
                      </svg>
                      Tạo người dùng
                  </button>
              </div>
          </form>
        </div>    
    </section>


    </main>
  </div><!-- /data-admin-table-root -->


  <!-- Toast stack -->

  </body>
</html>

