<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>Jollibug | Quản lý khuyến mãi</title>
	<meta name="description" content="Jollibug Manager - quản lý khuyến mãi." />
	<link rel="preconnect" href="https://fonts.googleapis.com" />
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
	<link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
	<link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
	<link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
	<link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
</head>
<body data-admin-role="manager" data-admin-page="promotions">
	<div class="admin-shell admin-body" data-admin-table-root>
		<jsp:include page="../layout/sidebar.jsp" />
		<main class="admin-main">
			<jsp:include page="../layout/topbar.jsp" />
			<section class="admin-panel">
				<div class="panel-header">
					<div class="stack" style="gap:0.3rem;">
						<h1 class="section-title" style="margin:0;">Quản lý khuyến mãi</h1>
					</div>
					<div class="panel-controls">
						<label class="table-search">
							<svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
								<circle cx="11" cy="11" r="7"></circle>
								<path d="m20 20-3.5-3.5"></path>
							</svg>
							<input type="search" placeholder="Tìm tên chiến dịch hoặc trạng thái" />
						</label>
						<a href="<c:url value='/manager/promotions/create'/>" class="btn btn-primary">Thêm khuyến mãi</a>
					</div>
				</div>

				<div class="table-wrap admin-table-wrap">
					<table class="admin-table">
						<thead>
							<tr>
								<th>Chương trình</th>
								<th>Giảm</th>
								<th>Thời gian</th>
								<th>Trạng thái</th>
								<th>Thao tác</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>Combo trưa</td>
								<td>20%</td>
								<td>01/04 - 30/04</td>
								<td>
									<span class="status-badge" data-status="active">đang hoạt động</span>
								</td>
								<td>
									<div class="action-row">
										<a href="<c:url value='/manager/promotions/detail'/>" class="btn btn-ghost icon-btn">Xem</a>
										<a href="<c:url value='/manager/promotions/update'/>" class="btn btn-ghost icon-btn">Sửa</a>
										<a href="<c:url value='/manager/promotions/delete'/>" class="btn btn-ghost icon-btn">Xóa</a>
									</div>
								</td>
							</tr>
							<tr>
								<td>Happy Hour</td>
								<td>Giảm 30k</td>
								<td>17h - 19h</td>
								<td>
									<span class="status-badge" data-status="featured">đang chạy</span>
								</td>
								<td>
									<div class="action-row">
										<a href="<c:url value='/manager/promotions/detail'/>" class="btn btn-ghost icon-btn">Xem</a>
										<a href="<c:url value='/manager/promotions/update'/>" class="btn btn-ghost icon-btn">Sửa</a>
										<a href="<c:url value='/manager/promotions/delete'/>" class="btn btn-ghost icon-btn">Xóa</a>
									</div>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</section>
		</main>
	</div>
</body>
</html>