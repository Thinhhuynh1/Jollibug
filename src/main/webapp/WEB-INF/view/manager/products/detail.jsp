<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Jollibug | Chi tiết sản phẩm</title>
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
                                <fmt:formatNumber var="giaFormatted" value="${monAn.gia}" type="number" />
                                <label class="profile-field">
                                    <span>Mã sản phẩm</span>
                                    <input type="text" value="${monAn.maMon}" readonly />
                                </label>
                                <label class="profile-field">
                                    <span>Tên sản phẩm</span>
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
                                    <input type="text" value="${monAn.available ? 'Đang hoạt động' : 'Tạm ẩn'}" readonly />
                                </label>
                                <label class="profile-field">
                                    <span>Mô tả</span>
                                    <input type="text" value="${monAn.moTa}" readonly />
                                </label>
                                <div class="profile-field">
                                    <span>Hình ảnh</span>
                                    <c:if test="${not empty monAn.img}">
                                        <img src="/images/${monAn.img}" alt="${monAn.tenMon}" />
                                    </c:if>
                                </div>
                            </div>
                            <div class="profile-actions" style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:2rem;">
                                <a href="/manager/products" class="btn btn-ghost">Quay lại</a>
                                <a href="/manager/products/update?productID=${monAn.maMon}" class="profile-submit" style="display:inline-flex; align-items:center; justify-content:center; text-decoration:none; max-width:180px;">Chỉnh sửa</a>
                            </div>
                        </div>
                    </section>
                </section>
            </div>
        </main>
    </div>
</body>
</html>
