<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Jollibug | Cập nhật sản phẩm</title>
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
                        <form action="/manager/products/update" method="post" enctype="multipart/form-data" class="profile-form">
                            <input type="hidden" name="productID" value="${monAn.maMon}" />
                            <div class="profile-grid">
                                <label class="profile-field">
                                    <span>Tên sản phẩm <span style="color:var(--color-red-500);">*</span></span>
                                    <input type="text" name="tenMon" value="${monAn.tenMon}" required />
                                </label>
                                <label class="profile-field">
                                    <span>Danh mục</span>
                                    <select name="maDM" required>
                                        <c:forEach var="danhMuc" items="${listDanhMuc}">
                                            <option value="${danhMuc.maDM}" <c:if test="${monAn.danhMuc.maDM == danhMuc.maDM}">selected</c:if>>${danhMuc.tenDM}</option>
                                        </c:forEach>
                                    </select>
                                </label>
                                <label class="profile-field">
                                    <span>Giá bán (VND)</span>
                                    <input type="number" name="gia" min="0" value="${monAn.gia}" required />
                                </label>
                                <label class="profile-field">
                                    <span>Tồn kho</span>
                                    <input type="number" name="soLuongTon" min="0" value="${monAn.soLuongTon}" required />
                                </label>
                                <label class="profile-field">
                                    <span>Trạng thái</span>
                                    <select name="available">
                                        <option value="true" <c:if test="${monAn.available}">selected</c:if>>Đang hoạt động</option>
                                        <option value="false" <c:if test="${!monAn.available}">selected</c:if>>Tạm ẩn</option>
                                    </select>
                                </label>
                                <label class="profile-field">
                                    <span>Mô tả</span>
                                    <input type="text" name="moTa" value="${monAn.moTa}" />
                                </label>
                                <label class="profile-field">
                                    <span>Ảnh sản phẩm</span>
                                    <input id="productFile" type="file" name="productFile" accept="image/*" />
                                </label>
                                <div></div>
                                <div class="profile-field">
                                    <span>Xem trước ảnh</span>
                                    <img id="productPreview" src="${empty monAn.img ? '' : '/images/'}${monAn.img}" alt="Xem trước" />
                                </div>
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
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    <script>
        $(document).ready(() => {
            const productFile = $("#productFile");
            productFile.change(function (e) {
                const imgURL = URL.createObjectURL(e.target.files[0]);
                $("#productPreview").attr("src", imgURL);
                $("#productPreview").css({ "display": "block" });
            });
        });
    </script>
</body>
</html>
