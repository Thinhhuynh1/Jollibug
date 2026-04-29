
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Chi tiết danh mục</title>
  <meta name="description" content="Jollibug Manager - xem thông tin chi tiết của danh mục." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/admin.css" />
  <link rel="stylesheet" href="/css/client/profile.css" />

</head>

<body data-admin-role="manager" data-admin-page="categories">

  <div class="admin-shell admin-body" data-admin-table-root>

    <jsp:include page="../layout/sidebar.jsp" />

    <main class="admin-main">

      <jsp:include page="../layout/topbar.jsp" />

      <div style="max-width: 52rem; margin: 0 auto; width: 100%;">
        <section class="profile-content">
          <section class="profile-section">
            <h1 class="profile-title">Xóa danh mục #CAT001</h1>
            <p class="profile-subtitle">Xác nhận xóa danh mục này khỏi hệ thống quản trị.</p>


              <div style="margin-top:1.25rem; padding:0.9rem 1rem; border:1px solid #f1c0c4; background:#fff4f5; border-radius: var(--radius-md); color:#9f1d24;">
                Bạn có chắc chắn muốn xóa danh mục này không? Hành động này không thể hoàn tác.
              </div>

              <div class="profile-actions" style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:2rem;">
                <a href="/manager/categories" class="btn btn-ghost">Hủy</a>
                <button type="submit" class="profile-submit" style="max-width: 180px; background:#d32f2f; box-shadow:none;">
                  Xác nhận xóa
                </button>
              </div>
          </section>
        </section>
      </div>

    </main>
  </body>
</html>




