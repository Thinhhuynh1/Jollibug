<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>Jollibug | Chi tiết sản phẩm</title>
	<meta name="description" content="Jollibug Manager - xem thông tin chi tiết sản phẩm." />

	<link rel="preconnect" href="https://fonts.googleapis.com" />
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
	<link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

	<link rel="stylesheet" href="/css/global.css" />
	<link rel="stylesheet" href="/css/components.css" />
	<link rel="stylesheet" href="/css/admin.css" />
	<link rel="stylesheet" href="/css/client/profile.css" />
</head>

<body data-admin-role="manager" data-admin-page="products">

	<div class="admin-shell admin-body" data-admin-table-root>

		<jsp:include page="../layout/sidebar.jsp" />

		<main class="admin-main">

			<jsp:include page="../layout/topbar.jsp" />

			<div style="max-width: 52rem; margin: 0 auto; width: 100%;">
				<section class="profile-content">
					<section class="profile-section">
						<h1 class="profile-title">Chi tiết sản phẩm</h1>

						<div class="profile-form">
							<div class="profile-grid">
								<label class="profile-field">
									<span>Tên sản phẩm</span>
									<input type="text" value="Burger đôi khói" readonly />
								</label>

								<label class="profile-field">
									<span>Danh mục</span>
									<input type="text" value="Burger đặc trưng" readonly />
								</label>

								<label class="profile-field">
									<span>Giá bán</span>
									<input type="text" value="249.000đ" readonly />
								</label>

								<label class="profile-field">
									<span>Tồn kho</span>
									<input type="text" value="36" readonly />
								</label>

								<label class="profile-field">
									<span>Trạng thái</span>
									<input type="text" value="Đang hoạt động" readonly />
								</label>
							</div>

							<div class="profile-actions" style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:2rem;">
								<a href="/manager/products" class="btn btn-ghost">Quay lại</a>
								<a href="/manager/products/update" class="profile-submit" style="display:inline-flex; align-items:center; justify-content:center; text-decoration:none; max-width:180px;">Chỉnh sửa</a>
							</div>
						</div>
					</section>
				</section>
			</div>

		</main>
	</div>
</body>
</html>
