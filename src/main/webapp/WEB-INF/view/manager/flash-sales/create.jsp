<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>Jollibug | Thêm flash sale</title>
	<meta name="description" content="Jollibug Manager - thêm flash sale." />
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
						<h1 class="profile-title">Thêm flash sale</h1>
						<p class="profile-subtitle">Tạo khung giờ sale với thời gian bắt đầu và kết thúc theo giờ.</p>

						<form action="#" method="post" class="profile-form">
							<div class="profile-grid">
								<label class="profile-field">
									<span>Tên phiên sale</span>
									<input type="text" name="name" placeholder="Happy Friday" />
								</label>

								<label class="profile-field">
									<span>Mức giảm</span>
									<input type="text" name="discount" placeholder="35%" />
								</label>

								<label class="profile-field">
									<span>Giờ bắt đầu</span>
									<input type="time" name="startTime" />
								</label>

								<label class="profile-field">
									<span>Giờ kết thúc</span>
									<input type="time" name="endTime" />
								</label>

								<label class="profile-field">
									<span>Trạng thái</span>
									<select name="status"><option selected>Đang hoạt động</option><option>Tạm ẩn</option></select>
								</label>
							</div>

							<div class="profile-actions" style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:2rem;">
								<a href="<c:url value='/manager/flash-sales'/>" class="btn btn-ghost">Hủy</a>
								<button type="submit" class="profile-submit" style="max-width:180px;">Lưu flash sale</button>
							</div>
						</form>
					</section>
				</section>
			</div>
		</main>
	</div>
</body>
</html>