<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>Jollibug | Cập nhật sản phẩm</title>
	<meta name="description" content="Jollibug Manager - cập nhật thông tin sản phẩm." />

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
						<h1 class="profile-title">Cập nhật sản phẩm</h1>

						<form action="#" method="post" class="profile-form">
							<div class="profile-grid">
								<label class="profile-field">
									<span>Tên sản phẩm <span style="color:var(--color-red-500);">*</span></span>
									<input type="text" name="name" value="Burger đôi khói" required />
								</label>

								<label class="profile-field">
									<span>Danh mục</span>
									<select name="category">
										<option value="burgers" selected>Burger đặc trưng</option>
										<option value="sides">Món ăn kèm</option>
										<option value="drinks">Đồ uống</option>
									</select>
								</label>

								<label class="profile-field">
									<span>Giá bán (VND)</span>
									<input type="number" name="price" value="249000" />
								</label>

								<label class="profile-field">
									<span>Tồn kho</span>
									<input type="number" name="stock" value="36" />
								</label>

								<label class="profile-field">
									<span>Trạng thái</span>
									<select name="status">
										<option value="active" selected>Đang hoạt động</option>
										<option value="out-of-stock">Hết hàng</option>
										<option value="inactive">Tạm ẩn</option>
									</select>
								</label>
							</div>

							<div class="profile-actions" style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:2rem;">
								<a href="/manager/products" class="btn btn-ghost">Hủy</a>
								<button type="submit" class="profile-submit" style="max-width:180px;">Lưu thay đổi</button>
							</div>
						</form>
					</section>
				</section>
			</div>

		</main>
	</div>
</body>
</html>
