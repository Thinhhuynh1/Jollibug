<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Xóa danh mục</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/manager.css'/>" />
</head>
<body data-admin-role="manager" data-admin-page="categories">
  <div class="admin-shell admin-body">
    <jsp:include page="../layout/sidebar.jsp" />
    <main class="admin-main">
      <jsp:include page="../layout/topbar.jsp" />

      <div class="manager-delete-wrap">
        <form action="<c:url value='/manager/categories/delete'/>" method="post" data-product-delete-form>
          <input type="hidden" name="categoryID" value="${danhMuc.maDM}" />

          <div class="manager-delete-card">
            <div class="manager-delete-card__header">
              <div class="manager-delete-card__icon" aria-hidden="true">!</div>
              <div>
                <h1>Xóa danh mục</h1>
                <p>Mã danh mục #${danhMuc.maDM} — thao tác không thể hoàn tác</p>
              </div>
            </div>

            <div class="manager-delete-preview">
              <div class="manager-delete-preview__img manager-delete-preview__img--empty" style="font-size:1.5rem;">📁</div>
              <div class="manager-delete-preview__body">
                <strong>${danhMuc.tenDM}</strong>
                <div class="manager-delete-preview__meta">
                  <c:if test="${not empty danhMuc.moTa}">
                    <span>Mô tả: ${danhMuc.moTa}</span>
                  </c:if>
                  <span>Trạng thái: ${danhMuc.available ? 'Đang hoạt động' : 'Đang ẩn'}</span>
                </div>
              </div>
            </div>

            <c:choose>
              <c:when test="${soLuongMon > 0}">
                <div class="manager-delete-warning">
                  Không thể xóa danh mục này vì còn <strong>${soLuongMon}</strong> món ăn. Hãy chuyển hoặc xóa các món trước.
                </div>
                <div class="manager-delete-actions">
                  <a href="<c:url value='/manager/categories'/>" class="btn btn-ghost">Quay lại danh sách</a>
                </div>
              </c:when>
              <c:otherwise>
                <div class="manager-delete-warning">
                  Danh mục sẽ bị xóa vĩnh viễn. Thao tác không thể hoàn tác.
                </div>

                <label class="manager-delete-confirm">
                  <input type="checkbox" data-delete-confirm-check />
                  <span>Tôi hiểu và muốn xóa danh mục <strong>${danhMuc.tenDM}</strong> khỏi hệ thống.</span>
                </label>

                <div class="manager-delete-actions">
                  <a href="<c:url value='/manager/categories'/>" class="btn btn-ghost">Hủy, quay lại danh sách</a>
                  <button type="submit" class="btn-delete-submit" data-delete-submit disabled>
                    Xác nhận xóa
                  </button>
                </div>
              </c:otherwise>
            </c:choose>
          </div>
        </form>
      </div>
    </main>
  </div>
  <script src="<c:url value='/js/manager/products.js'/>" defer></script>
</body>
</html>
