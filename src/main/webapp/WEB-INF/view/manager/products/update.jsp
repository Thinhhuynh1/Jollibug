<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Jollibug | Sửa món ăn</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
    <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
    <link rel="stylesheet" href="<c:url value='/css/client/profile.css'/>" />
    <style>
      #productPreview { max-width:200px; max-height:200px; border-radius:8px; margin-top:0.5rem; object-fit:cover; border:1px solid #e5e7eb; }
    </style>
</head>
<body data-admin-role="manager" data-admin-page="products">
    <div class="admin-shell admin-body">
        <jsp:include page="../layout/sidebar.jsp" />
        <main class="admin-main">
            <jsp:include page="../layout/topbar.jsp" />
            <div style="max-width:52rem; margin:0 auto; width:100%;">
                <section class="profile-content">
                    <section class="profile-section">
                        <h1 class="profile-title">Sửa món ăn</h1>
                        <p class="profile-subtitle">Mã món: #${monAn.maMon}</p>
                        <form action="<c:url value='/manager/products/update'/>" method="post" enctype="multipart/form-data"
                              class="profile-form" data-product-form>
                            <input type="hidden" name="productID" value="${monAn.maMon}" />
                            <div class="profile-grid">
                                <label class="profile-field">
                                    <span>Tên món ăn <span style="color:var(--color-red-500);">*</span></span>
                                    <input type="text" name="tenMon" value="${monAn.tenMon}" required />
                                </label>
                                <label class="profile-field">
                                    <span>Danh mục <span style="color:var(--color-red-500);">*</span></span>
                                    <select name="maDM" required>
                                        <c:forEach var="danhMuc" items="${listDanhMuc}">
                                            <option value="${danhMuc.maDM}" <c:if test="${monAn.danhMuc.maDM == danhMuc.maDM}">selected</c:if>>${danhMuc.tenDM}</option>
                                        </c:forEach>
                                    </select>
                                </label>
                                <label class="profile-field">
                                    <span>Giá bán (VNĐ) <span style="color:var(--color-red-500);">*</span></span>
                                    <input type="number" name="gia" min="0" step="1000" value="${monAn.gia}" required />
                                </label>
                                <label class="profile-field">
                                    <span>Tồn kho <span style="color:var(--color-red-500);">*</span></span>
                                    <input type="number" name="soLuongTon" min="0" value="${monAn.soLuongTon}" required />
                                </label>
                                <label class="profile-field">
                                    <span>Đơn vị</span>
                                    <input type="text" name="donVi" value="${empty monAn.donVi ? 'phần' : monAn.donVi}" />
                                </label>
                                <label class="profile-field">
                                    <span>Trạng thái</span>
                                    <select name="available">
                                        <option value="true" <c:if test="${monAn.available}">selected</c:if>>Đang bán</option>
                                        <option value="false" <c:if test="${!monAn.available}">selected</c:if>>Tạm ẩn</option>
                                    </select>
                                </label>
                                <label class="profile-field" style="grid-column:1/-1;">
                                    <span>Mô tả</span>
                                    <textarea name="moTa" rows="3" style="width:100%; resize:vertical;">${monAn.moTa}</textarea>
                                </label>
                                <label class="profile-field" style="grid-column:1/-1;">
                                    <span>Ảnh món ăn (để trống nếu giữ ảnh cũ)</span>
                                    <input id="productFile" type="file" name="productFile" accept="image/*" />
                                    <img id="productPreview"
                                         src="<c:if test='${not empty monAn.img}'><c:url value='/images/${monAn.img}'/></c:if>"
                                         alt="Xem trước" />
                                </label>
                            </div>
                            <div class="profile-actions" style="display:flex; justify-content:flex-end; gap:0.75rem; margin-top:2rem;">
                                <a href="<c:url value='/manager/products'/>" class="btn btn-ghost">Hủy</a>
                                <button type="submit" class="profile-submit" style="max-width:180px;">Lưu thay đổi</button>
                            </div>
                        </form>
                    </section>
                </section>
            </div>
        </main>
    </div>
    <script src="<c:url value='/js/manager/products.js'/>" defer></script>
</body>
</html>
