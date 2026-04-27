<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>Jollibug | Xóa flash sale</title>
	<meta name="description" content="Jollibug Manager - xác nhận xóa flash sale." />
	<link rel="preconnect" href="https://fonts.googleapis.com" />
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
	<link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
	<link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
	<link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
	<link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
	<link rel="stylesheet" href="<c:url value='/css/client/profile.css'/>" />
</head>
<body data-admin-role="manager" data-admin-page="flash-sales">
	<div class="admin-shell admin-body" data-admin-table-root>
		<jsp:include page="../layout/sidebar.jsp" />
		<main class="admin-main">
			<jsp:include page="../layout/topbar.jsp" />
			<div style="max-width:52rem; margin:0 auto; width:100%;">
				<section class="profile-content">
					<section class="profile-section">
						<h1 class="profile-title">Xóa flash sale Happy Friday</h1>
						<p class="profile-subtitle">Hành động này không thể hoàn tác.</p>

						<div style="margin-top:1.25rem; padding:0.9rem 1rem; border:1px solid #f1c0c4; background:#fff4f5; border-radius:var(--radius-md); color:#9f1d24;">
							Bạn có chắc chắn muốn xóa flash sale này không?
						</div>

						<div class="profile-actions" style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:2rem;">
							<a href="<c:url value='/manager/flash-sales'/>" class="btn btn-ghost">Hủy</a>
							<button type="submit" class="profile-submit" style="max-width:180px; background:#d32f2f; box-shadow:none;">Xác nhận xóa</button>
						</div>
					</section>
				</section>
			</div>
		</main>
	</div>
</body>
</html>