<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="vn.fastfood.entity.ChuongTrinhKhuyenMai"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
ChuongTrinhKhuyenMai promotion = (ChuongTrinhKhuyenMai) request.getAttribute("promotion");
%>
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

<div class="profile-form">
<div class="profile-grid">
<label class="profile-field">
<span>Tên chương trình</span>
<input type="text" value="${promotion.tenKM}" readonly />
</label>

<label class="profile-field">
<span>Mức giảm</span>
<input type="text" value="${promotion.phanTramGiam}%" readonly />
</label>

<label class="profile-field">
<span>Ngày bắt đầu</span>
<input type="text" value="<%= promotion.getNgayBatDau() == null ? "" : promotion.getNgayBatDau().toLocalDate() %>" readonly />
</label>

<label class="profile-field">
<span>Ngày kết thúc</span>
<input type="text" value="<%= promotion.getNgayKetThuc() == null ? "" : promotion.getNgayKetThuc().toLocalDate() %>" readonly />
</label>

<label class="profile-field">
<span>Trạng thái</span>
<input type="text" value="<%= promotion.getNgayBatDau() != null && promotion.getNgayBatDau().isAfter(java.time.LocalDateTime.now())
        ? "Sắp diễn ra"
        : ((promotion.getNgayKetThuc() != null && promotion.getNgayKetThuc().isBefore(java.time.LocalDateTime.now()))
                ? "Đã kết thúc"
                : "Đang hoạt động") %>" readonly />
</label>

<label class="profile-field">
<span>Phạm vi áp dụng</span>
<input type="text" value="<%= "ITEM".equals(promotion.getPhamViApDung()) ? "Chọn món" : "Tất cả món" %>" readonly />
</label>

<c:if test="${not empty apDungMonAnList}">
<label class="profile-field" style="grid-column:1/-1;">
<span>Danh sách món</span>
<textarea rows="5" readonly style="width:100%;">
<c:forEach items="${apDungMonAnList}" var="food">${food.tenMon}
</c:forEach>
</textarea>
</label>
</c:if>
</div>

<div class="profile-actions" style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:2rem;">
<a href="<c:url value='/manager/promotions'/>" class="btn btn-ghost">Quay lại</a>
<a href="<c:url value='/manager/promotions/update'/>?promotionID=${promotion.maKM}" class="profile-submit" style="display:inline-flex; align-items:center; justify-content:center; text-decoration:none; max-width:180px;">Chỉnh sửa</a>
</div>
</div>
</section>
</section>
</div>
</main>
</div>
</body>
</html>
