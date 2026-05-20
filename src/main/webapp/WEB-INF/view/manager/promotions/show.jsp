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
<form action="<c:url value='/manager/promotions'/>" method="get" class="table-search" style="display:flex; align-items:center; gap:0.5rem; width:auto;">
  <div style="position:relative; display:flex; align-items:center;">
    <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2" style="position:absolute; left:0.75rem; width:1.1rem; height:1.1rem; color:var(--text-muted);">
      <circle cx="11" cy="11" r="7"></circle>
      <path d="m20 20-3.5-3.5"></path>
    </svg>
    <input type="search" name="keyword" placeholder="Tìm tên chiến dịch hoặc trạng thái" value="${keyword}" style="padding-left:2.5rem; width:250px;" />
    <c:if test="${not empty keyword}">
      <a href="<c:url value='/manager/promotions'/>" title="Xóa tìm kiếm" style="position:absolute; right:0.75rem; color:var(--text-muted); display:flex; align-items:center;">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:1rem; height:1rem;">
          <path d="M18 6L6 18M6 6l12 12"></path>
        </svg>
      </a>
    </c:if>
  </div>
  <button type="submit" class="btn btn-ghost" style="padding: 0.5rem 1rem;">Tìm</button>
</form>
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
<c:choose>
<c:when test="${not empty promotions}">
<c:forEach var="promotion" items="${promotions}">
<tr>
<td>${promotion.tenCT}</td>
<td>${promotion.discountDisplay}</td>
<td>${promotion.thoiGianDisplay}</td>
<td>
<span class="status-badge" data-status="${promotion.status == 'Đang hoạt động' ? 'active' : (promotion.status == 'Sắp diễn ra' ? 'featured' : 'inactive')}">${promotion.status}</span>
</td>
<td>
<div class="action-row">
<a href="<c:url value='/manager/promotions/detail'/>?promotionID=${promotion.maCT}" class="btn btn-ghost icon-btn">Xem</a>
<a href="<c:url value='/manager/promotions/update'/>?promotionID=${promotion.maCT}" class="btn btn-ghost icon-btn">Sửa</a>
<a href="<c:url value='/manager/promotions/delete'/>?promotionID=${promotion.maCT}" class="btn btn-ghost icon-btn">Xóa</a>
</div>
</td>
</tr>
</c:forEach>
</c:when>
<c:otherwise>
<tr>
<td colspan="5" class="text-center">Không có khuyến mãi nào.</td>
</tr>
</c:otherwise>
</c:choose>
</tbody>
</table>
</div>
</section>
</main>
</div>
</body>
</html>
