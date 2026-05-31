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
    <link rel="stylesheet" href="<c:url value='/css/manager.css'/>" />
</head>
<body data-admin-role="manager" data-admin-page="products">
    <div class="admin-shell admin-body">
        <jsp:include page="../layout/sidebar.jsp" />
        <main class="admin-main">
            <jsp:include page="../layout/topbar.jsp" />

            <div class="manager-delete-wrap" style="max-width:48rem;">
                <div class="manager-delete-card">
                    <div class="manager-delete-card__header" style="background:linear-gradient(135deg,#fff9f5,#fff4e8); border-bottom-color:rgba(111,82,55,0.12);">
                        <div class="manager-delete-card__icon" style="background:var(--color-red-500,#d94436);">🍔</div>
                        <div>
                            <h1>Chi tiết món ăn</h1>
                            <p>Mã #${monAn.maMon}</p>
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
                                <span>Đã bán: ${monAn.soLuongDaBan}</span>
                                <span>Trạng thái: ${monAn.available ? 'Đang bán' : 'Tạm ẩn'}</span>
                            </div>
                        </div>
                    </div>

                    <c:if test="${not empty monAn.moTa}">
                        <div style="padding:0 1.5rem 1rem;">
                            <strong style="font-size:0.88rem;color:var(--color-ink-600);">Mô tả</strong>
                            <p style="margin:0.35rem 0 0;line-height:1.55;">${monAn.moTa}</p>
                        </div>
                    </c:if>

                    <div class="manager-delete-actions">
                        <a href="<c:url value='/manager/products'/>" class="btn btn-ghost">← Quay lại danh sách</a>
                        <a href="<c:url value='/manager/products/update'><c:param name='productID' value='${monAn.maMon}'/></c:url>" class="btn btn-primary">Sửa món</a>
                        <a href="<c:url value='/manager/products/delete'><c:param name='productID' value='${monAn.maMon}'/></c:url>" class="btn btn-ghost" style="color:#d32f2f;">Xóa món</a>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
