<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>Jollibug | Xóa món ăn</title>
	<link rel="preconnect" href="https://fonts.googleapis.com" />
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
	<link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
	<link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
	<link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
	<link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
	<link rel="stylesheet" href="<c:url value='/css/manager.css'/>" />
</head>
<body data-admin-role="manager" data-admin-page="products">
	<div class="admin-shell admin-body">
		<jsp:include page="../layout/sidebar.jsp" />
		<main class="admin-main">
			<jsp:include page="../layout/topbar.jsp" />

			<div class="manager-delete-wrap">
				<form action="<c:url value='/manager/products/delete'/>" method="post" data-product-delete-form>
					<input type="hidden" name="productID" value="${monAn.maMon}" />

					<div class="manager-delete-card">
						<div class="manager-delete-card__header">
							<div class="manager-delete-card__icon" aria-hidden="true">!</div>
							<div>
								<h1>Xóa món ăn</h1>
								<p>Mã món #${monAn.maMon} — thao tác không thể hoàn tác</p>
							</div>
						</div>

						<div class="manager-delete-preview">
							<c:choose>
								<c:when test="${not empty monAn.img}">
									<img class="manager-delete-preview__img" src="<c:url value='/images/${monAn.img}'/>" alt="${monAn.tenMon}" />
								</c:when>
								<c:otherwise>
									<div class="manager-delete-preview__img manager-delete-preview__img--empty">Chưa có ảnh</div>
								</c:otherwise>
							</c:choose>
							<div class="manager-delete-preview__body">
								<strong>${monAn.tenMon}</strong>
								<div class="manager-delete-preview__meta">
									<span>Danh mục: ${monAn.danhMuc.tenDM}</span>
									<span>Giá: <fmt:formatNumber value="${monAn.gia}" type="number" groupingUsed="true"/>đ</span>
									<span>Tồn: ${monAn.soLuongTon}</span>
								</div>
							</div>
						</div>

						<div class="manager-delete-warning">
							Món ăn sẽ bị xóa vĩnh viễn khỏi thực đơn. Nếu món đã có trong đơn hàng, hệ thống có thể không cho xóa.
						</div>

						<label class="manager-delete-confirm">
							<input type="checkbox" data-delete-confirm-check />
							<span>Tôi hiểu và muốn xóa món <strong>${monAn.tenMon}</strong> khỏi hệ thống.</span>
						</label>

						<div class="manager-delete-actions">
							<a href="<c:url value='/manager/products'/>" class="btn btn-ghost">Hủy, quay lại danh sách</a>
							<button type="submit" class="btn-delete-submit" data-delete-submit disabled>
								Xác nhận xóa
							</button>
						</div>
					</div>
				</form>
			</div>
		</main>
	</div>
	<script src="<c:url value='/js/manager/products.js'/>" defer></script>
</body>
</html>
