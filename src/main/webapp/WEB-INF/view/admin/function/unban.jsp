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
                <h2 class="section-title">Xóa người dùng #ND001</h2>
                <p class="muted" style="margin:0;">Xác nhận mở khóa tài khoản người dùng này.</p>
              </div>
            </div>
            <div style="margin-top:1.25rem; padding:0.9rem 1rem; border:1px solid #c4f1e3; background:#fff4f5; border-radius: var(--radius-md); color:#249f32;">
                Bạn có chắc chắn muốn mở khóa người dùng này không? Hành động này không thể hoàn tác.
              </div>

              <div class="modal__actions" style="margin-top: 2rem; justify-content: flex-end;">
                <a href="/admin/users/block" class="btn btn-ghost">Hủy</a>
                <button type="submit" class="btn btn-primary" style="background:#249f32; border-color:#249f32;">
                  Xác nhận
                </button>
              </div>
          </div>
        </section>


    </main>
  </div><!-- /data-admin-table-root -->


  <!-- Toast stack -->

  </body>
</html>

