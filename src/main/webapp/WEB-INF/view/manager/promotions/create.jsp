<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Jollibug | Thêm khuyến mãi</title>
<meta name="description" content="Jollibug Manager - thêm khuyến mãi." />
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
<h1 class="profile-title">Thêm khuyến mãi</h1>
<c:if test="${not empty errorMessage}">
<div class="alert alert-danger" style="margin-bottom:1rem;">${errorMessage}</div>
</c:if>

<form action="<c:url value='/manager/promotions/create'/>" method="post" class="profile-form">
<div class="profile-grid">
<label class="profile-field">
<span>Tên chương trình</span>
<input type="text" name="tenKM" placeholder="Combo trưa" required />
</label>

<label class="profile-field">
<span>Mức giảm (%)</span>
<input type="number" step="0.1" min="0" max="100" name="phanTramGiam" placeholder="20" required />
</label>

<label class="profile-field">
<span>Ngày bắt đầu</span>
<input type="date" name="startDate" required />
</label>

<label class="profile-field">
<span>Ngày kết thúc</span>
<input type="date" name="endDate" required />
</label>
</div>

<div class="promo-apply-panel">
    <div class="promo-apply-header">
        <span>Phạm vi áp dụng</span>
        <small>Chọn mức áp dụng phù hợp nhất cho chương trình giảm giá</small>
    </div>
    <div class="promo-apply-options">
        <label class="promo-option-card">
            <input type="radio" name="phamViApDung" value="ALL" checked />
            <div>
                <strong>Tất cả món</strong>
                <small>Áp dụng giảm giá cho toàn bộ thực đơn.</small>
            </div>
        </label>

        <label class="promo-option-card">
            <input type="radio" name="phamViApDung" value="ITEM" />
            <div>
                <strong>Chọn món</strong>
                <small>Lựa chọn riêng từng món để áp dụng khuyến mãi.</small>
            </div>
        </label>
    </div>

    <div id="apply-items" class="promo-apply-details">
        <label class="promo-detail-label">Chọn món áp dụng</label>
        <div class="promo-items-list">
            <c:forEach items="${monAnList}" var="monAn">
                <label class="promo-item-card">
                    <input type="checkbox" name="selectedMonAnIds" value="${monAn.maMon}" />
                    <span>${monAn.tenMon}</span>
                </label>
            </c:forEach>
        </div>
    </div>
</div>

<div class="profile-actions" style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:2rem;">
<a href="<c:url value='/manager/promotions'/>" class="btn btn-ghost">Hủy</a>
<button type="submit" class="profile-submit" style="max-width:180px;">Lưu khuyến mãi</button>
</div>
</form>
</section>
</section>
</div>
</main>
</div>
<script>
const applyTypeRadios = document.querySelectorAll('input[name="phamViApDung"]');
const optionCards = document.querySelectorAll('.promo-option-card');
const itemsSection = document.getElementById('apply-items');
function updateApplySections() {
  const selected = document.querySelector('input[name="phamViApDung"]:checked');
  optionCards.forEach(card => {
    const radio = card.querySelector('input[type="radio"]');
    card.classList.toggle('active', radio === selected);
  });
  itemsSection.classList.toggle('active', selected.value === 'ITEM');
}
applyTypeRadios.forEach(radio => radio.addEventListener('change', updateApplySections));
updateApplySections();
</script>
</body>
</html>
