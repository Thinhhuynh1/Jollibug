<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>Jollibug | Chi tiết flash sale</title>
	<meta name="description" content="Jollibug Manager - xem chi tiết flash sale." />
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
						<h1 class="profile-title">Chi tiết flash sale</h1>
						<p class="profile-subtitle">Xem thông tin phiên sale với giờ bắt đầu và giờ kết thúc tách riêng.</p>

						<div class="profile-form">
							<div class="profile-grid">
								<label class="profile-field">
									<span>Tên phiên sale</span>
									<input type="text" value="Happy Friday" readonly />
								</label>

								<label class="profile-field">
									<span>Mức giảm</span>
									<input type="text" value="35%" readonly />
								</label>

								<label class="profile-field">
									<span>Giờ bắt đầu</span>
									<input type="text" value="12:00" readonly />
								</label>

								<label class="profile-field">
									<span>Giờ kết thúc</span>
									<input type="text" value="14:00" readonly />
								</label>

								<label class="profile-field">
									<span>Trạng thái</span>
									<input type="text" value="Đang hoạt động" readonly />
								</label>
							</div>

							<div class="profile-actions" style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:2rem;">
								<a href="<c:url value='/manager/flash-sales'/>" class="btn btn-ghost">Quay lại</a>
								<a href="<c:url value='/manager/flash-sales/update'/>" class="profile-submit" style="display:inline-flex; align-items:center; justify-content:center; text-decoration:none; max-width:180px;">Chỉnh sửa</a>
							</div>
						</div>
					</section>
				</section>
			</div>
		</main>
	</div>
</body>
</html>