<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Jollibug | Chi tiết món ăn</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
    <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
    <link rel="stylesheet" href="<c:url value='/css/client/profile.css'/>" />
</head>
<body data-admin-role="manager" data-admin-page="products">
    <div class="admin-shell admin-body">
        <jsp:include page="../layout/sidebar.jsp" />
        <main class="admin-main">
            <jsp:include page="../layout/topbar.jsp" />
            <div style="max-width:52rem; margin:0 auto; width:100%;">
                <section class="profile-content">
                    <section class="profile-section">
                        <h1 class="profile-title">Chi tiết món ăn</h1>
                        <div class="profile-form">
                            <div class="profile-grid">
                                <fmt:formatNumber var="giaFormatted" value="${monAn.gia}" type="number" groupingUsed="true" />
                                <label class="profile-field">
                                    <span>Mã món</span>
                                    <input type="text" value="${monAn.maMon}" readonly />
                                </label>
                                <label class="profile-field">
                                    <span>Tên món</span>
                                    <input type="text" value="${monAn.tenMon}" readonly />
                                </label>
                                <label class="profile-field">
                                    <span>Danh mục</span>
                                    <input type="text" value="${monAn.danhMuc.tenDM}" readonly />
                                </label>
                                <label class="profile-field">
                                    <span>Giá bán</span>
                                    <input type="text" value="${giaFormatted}đ" readonly />
                                </label>
                                <label class="profile-field">
                                    <span>Tồn kho</span>
                                    <input type="text" value="${monAn.soLuongTon}" readonly />
                                </label>
                                <label class="profile-field">
                                    <span>Đã bán</span>
                                    <input type="text" value="${monAn.soLuongDaBan}" readonly />
                                </label>
                                <label class="profile-field">
                                    <span>Trạng thái</span>
                                    <input type="text" value="${monAn.available ? 'Đang bán' : 'Tạm ẩn'}" readonly />
                                </label>
                                <label class="profile-field" style="grid-column:1/-1;">
                                    <span>Mô tả</span>
                                    <textarea rows="3" readonly style="width:100%;">${monAn.moTa}</textarea>
                                </label>
                                <div class="profile-field" style="grid-column:1/-1;">
                                    <span>Hình ảnh</span>
                                    <c:if test="${not empty monAn.img}">
                                        <img src="<c:url value='/images/${monAn.img}'/>" alt="${monAn.tenMon}"
                                             style="max-width:220px; border-radius:8px; margin-top:0.5rem;" />
                                    </c:if>
                                    <c:if test="${empty monAn.img}">
                                        <p class="muted">Chưa có ảnh</p>
                                    </c:if>
                                </div>
                            </div>
                            <div class="profile-actions" style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:2rem;">
                                <a href="<c:url value='/manager/products'/>" class="btn btn-ghost">Quay lại</a>
                                <a href="<c:url value='/manager/products/update'><c:param name='productID' value='${monAn.maMon}'/></c:url>"
                                   class="profile-submit" style="display:inline-flex; align-items:center; justify-content:center; text-decoration:none; max-width:180px;">Sửa món</a>
                                <a href="<c:url value='/manager/products/delete'><c:param name='productID' value='${monAn.maMon}'/></c:url>"
                                   class="btn btn-ghost" style="color:#d32f2f;">Xóa món</a>
                            </div>
                        </div>
                    </section>
                </section>
            </div>
        </main>
    </div>
</body>
</html>
