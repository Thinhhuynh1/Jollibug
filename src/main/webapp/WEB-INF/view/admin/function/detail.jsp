<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
                <h2 class="section-title">Chi tiết người dùng</h2>
            </div>
          </div>

                    <div class="profile-grid">
                        <div class="profile-field">
                            <span>Mã người dùng</span>
                            <div style="margin-top:0.45rem; padding:0.65rem 1rem; border:1px solid rgba(111,82,55,0.14); border-radius:var(--radius-md); background:#fff; color:var(--color-text-strong);">
                                <c:out value="${user.maTK}" default="-" />
                            </div>
                        </div>

                        <div class="profile-field">
                            <span>Họ và tên</span>
                            <div style="margin-top:0.45rem; padding:0.65rem 1rem; border:1px solid rgba(111,82,55,0.14); border-radius:var(--radius-md); background:#fff; color:var(--color-text-strong);">
                                <c:out value="${user.hoTen}" default="-" />
                            </div>
                        </div>

                        <div class="profile-field">
                            <span>Số điện thoại</span>
                            <div style="margin-top:0.45rem; padding:0.65rem 1rem; border:1px solid rgba(111,82,55,0.14); border-radius:var(--radius-md); background:#fff; color:var(--color-text-strong);">
                                <c:out value="${user.sdt}" default="-" />
                            </div>
                        </div>

                        <div class="profile-field">
                            <span>Email</span>
                            <div style="margin-top:0.45rem; padding:0.65rem 1rem; border:1px solid rgba(111,82,55,0.14); border-radius:var(--radius-md); background:#fff; color:var(--color-text-strong);">
                                <c:out value="${user.email}" default="-" />
                            </div>
                        </div>

                        <div class="profile-field">
                            <span>Vai trò</span>
                            <div style="margin-top:0.45rem; padding:0.65rem 1rem; border:1px solid rgba(111,82,55,0.14); border-radius:var(--radius-md); background:#fff; color:var(--color-text-strong);">
                                <c:out value="${user.vaiTro.tenVT}" default="-" />
                            </div>
                        </div>

                        <div class="profile-field">
                            <span>Trạng thái</span>
                            <div style="margin-top:0.45rem; padding:0.65rem 1rem; border:1px solid rgba(111,82,55,0.14); border-radius:var(--radius-md); background:#fff; color:var(--color-text-strong);">
                                <c:out value="${user.trangThai}" default="-" />
                            </div>
                        </div>

                        <div class="profile-field">
                            <span>Ngày tạo</span>
                            <div style="margin-top:0.45rem; padding:0.65rem 1rem; border:1px solid rgba(111,82,55,0.14); border-radius:var(--radius-md); background:#fff; color:var(--color-text-strong);">
                                <c:out value="${user.createdAtDisplay}" default="-" />
                            </div>
                        </div>

                        <div class="profile-field">
                            <span>Ngày cập nhật</span>
                            <div style="margin-top:0.45rem; padding:0.65rem 1rem; border:1px solid rgba(111,82,55,0.14); border-radius:var(--radius-md); background:#fff; color:var(--color-text-strong);">
                                <c:out value="${user.updatedAtDisplay}" default="-" />
                            </div>
                        </div>

                    
                    </div>

                    <div class="modal__actions" style="margin-top: 2.5rem; justify-content: flex-end;">
                        <a href="/admin/users" class="btn btn-ghost">Quay lại danh sách</a>
                    </div>
          
        </div>    </section>


    </main>
  </div><!-- /data-admin-table-root -->


  <!-- Toast stack -->

  </body>
</html>

