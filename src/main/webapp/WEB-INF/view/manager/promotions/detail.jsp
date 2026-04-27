<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>Jollibug | Chi tiết khuyến mãi</title>
	<meta name="description" content="Jollibug Manager - xem chi tiết khuyến mãi." />
	<link rel="preconnect" href="https://fonts.googleapis.com" />
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
	<link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
	<link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
	<link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
	<link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
	<link rel="stylesheet" href="<c:url value='/css/client/profile.css'/>" />
</head>
<body data-admin-role="manager" data-admin-page="promotions">
	<div class="admin-shell admin-body" data-admin-table-root>
		<jsp:include page="../layout/sidebar.jsp" />
		<main class="admin-main">
			<jsp:include page="../layout/topbar.jsp" />
			<div style="max-width:52rem; margin:0 auto; width:100%;">
				<section class="profile-content">
					<section class="profile-section">
						<h1 class="profile-title">Chi tiết khuyến mãi</h1>
						<p class="profile-subtitle">Xem tên chương trình, mức giảm và khoảng thời gian theo ngày.</p>

						<div class="profile-form">
							<div class="profile-grid">
								<label class="profile-field">
									<span>Tên chương trình</span>
									<input type="text" value="Combo trưa" readonly />
								</label>

								<label class="profile-field">
									<span>Mức giảm</span>
									<input type="text" value="20%" readonly />
								</label>

								<label class="profile-field">
									<span>Ngày bắt đầu</span>
									<input type="text" value="01/04/2026" readonly />
								</label>

								<label class="profile-field">
									<span>Ngày kết thúc</span>
									<input type="text" value="30/04/2026" readonly />
								</label>

								<label class="profile-field">
									<span>Trạng thái</span>
									<input type="text" value="Đang hoạt động" readonly />
								</label>
							</div>

							<div class="profile-actions" style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:2rem;">
								<a href="<c:url value='/manager/promotions'/>" class="btn btn-ghost">Quay lại</a>
								<a href="<c:url value='/manager/promotions/update'/>" class="profile-submit" style="display:inline-flex; align-items:center; justify-content:center; text-decoration:none; max-width:180px;">Chỉnh sửa</a>
							</div>
						</div>
					</section>
				</section>
			</div>
		</main>
	</div>
</body>
</html>