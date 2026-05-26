<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Quản lý món ăn</title>
  <meta name="description" content="Jollibug Manager - thêm, sửa, xóa món ăn, quản lý giá và tồn kho." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
</head>

<body data-admin-role="manager" data-admin-page="products">

  <div class="admin-shell admin-body" data-admin-table-root>

    <jsp:include page="../layout/sidebar.jsp" />

    <main class="admin-main">

      <jsp:include page="../layout/topbar.jsp" />

      <c:if test="${not empty message}">
        <div style="margin:0 var(--space-6) var(--space-4); padding:0.85rem 1rem; border-radius:var(--radius-md); background:#ecfdf3; color:#166534; border:1px solid #bbf7d0;">
          ${message}
        </div>
      </c:if>
      <c:if test="${not empty error}">
        <div style="margin:0 var(--space-6) var(--space-4); padding:0.85rem 1rem; border-radius:var(--radius-md); background:#fff4f5; color:#9f1d24; border:1px solid #f1c0c4;">
          ${error}
        </div>
      </c:if>

      <section class="admin-panel" style="margin-bottom:var(--space-6);">
        <div class="panel-header" style="display:flex; flex-direction:column; align-items:stretch; gap:1rem;">
          <div class="stack" style="gap:0.35rem; max-width:42rem;">
            <h1 class="section-title" id="admin-table-title" style="margin:0;">Quản lý món ăn</h1>
            <p class="muted" style="margin:0;">Thêm, sửa, xóa món ăn theo danh mục. Dùng nút <strong>Thêm món ăn mới</strong> hoặc cột Thao tác.</p>
          </div>
          <form class="panel-controls" action="<c:url value='/manager/products'/>" method="get" style="display:flex; flex-wrap:wrap; align-items:center; gap:0.75rem; width:100%;">
            <div class="select-group" style="gap:0; min-width:14rem; flex:0 0 auto;">
              <select name="categoryID" onchange="this.form.submit()" aria-label="Lọc theo danh mục">
                <option value="" ${empty selectCategoryID ? 'selected' : ''}>Tất cả danh mục</option>
                <c:forEach var="dm" items="${danhMuc}">
                  <option value="${dm.maDM}" ${selectCategoryID != null && selectCategoryID == dm.maDM ? 'selected' : ''}>${dm.tenDM}</option>
                </c:forEach>
              </select>
            </div>
            <div class="select-group" style="gap:0; min-width:12rem; flex:0 0 auto;">
              <select name="filter" onchange="this.form.submit()" aria-label="Sắp xếp">
                <option value="popular" ${selectedFilter == 'popular' ? 'selected' : ''}>Mặc định</option>
                <option value="price-low" ${selectedFilter == 'price-low' ? 'selected' : ''}>Giá: thấp → cao</option>
                <option value="price-high" ${selectedFilter == 'price-high' ? 'selected' : ''}>Giá: cao → thấp</option>
                <option value="rating" ${selectedFilter == 'rating' ? 'selected' : ''}>Bán chạy</option>
              </select>
            </div>
            <div class="select-group" style="gap:0; min-width:12rem; flex:0 0 auto;">
              <select name="status" onchange="this.form.submit()" aria-label="Lọc trạng thái">
                <option value="" ${empty selectedStatus ? 'selected' : ''}>Tất cả trạng thái</option>
                <option value="active" ${selectedStatus == 'active' ? 'selected' : ''}>Đang bán</option>
                <option value="inactive" ${selectedStatus == 'inactive' ? 'selected' : ''}>Tạm ẩn</option>
                <option value="out_of_stock" ${selectedStatus == 'out_of_stock' ? 'selected' : ''}>Hết hàng</option>
              </select>
            </div>
            <label class="table-search" style="flex:1 1 16rem; min-width:14rem;">
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"></circle>
                <path d="m20 20-3.5-3.5"></path>
              </svg>
              <input type="search" placeholder="Tìm tên món..." name="keyword" value="${keyword}"
                onkeydown="if(event.key === 'Enter'){ this.form.submit(); }"/>
            </label>
            <a href="<c:url value='/manager/products'/>" class="btn btn-ghost">Xóa lọc</a>
            <a href="<c:url value='/manager/products/create'/>" class="btn btn-primary">+ Thêm món ăn mới</a>
          </form>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table">
            <thead>
              <tr>
                <th>Món ăn</th>
                <th>Ảnh</th>
                <th>Danh mục</th>
                <th>Giá</th>
                <th>Tồn kho</th>
                <th>Đã bán</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty listMonAn}">
                  <tr>
                    <td colspan="8">
                      <div class="empty-state" style="padding:2rem; text-align:center;">
                        <h3 style="margin:0 0 0.5rem;">Chưa có món ăn</h3>
                        <p class="muted" style="margin:0 0 1rem;">Bấm &quot;Thêm món ăn mới&quot; để tạo món đầu tiên.</p>
                        <a href="<c:url value='/manager/products/create'/>" class="btn btn-primary">Thêm món ăn mới</a>
                      </div>
                    </td>
                  </tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="monAn" items="${listMonAn}">
                    <tr>
                      <td><strong>${monAn.tenMon}</strong></td>
                      <td>
                        <c:choose>
                          <c:when test="${not empty monAn.img}">
                            <img src="<c:url value='/images/${monAn.img}'/>" alt="${monAn.tenMon}"
                              style="width:56px;height:56px;object-fit:cover;border-radius:8px;border:1px solid #e5e7eb;" />
                          </c:when>
                          <c:otherwise>
                            <span class="muted">—</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>${monAn.danhMuc.tenDM}</td>
                      <td><fmt:formatNumber value="${monAn.gia}" type="number" groupingUsed="true"/>đ</td>
                      <td>${monAn.soLuongTon}</td>
                      <td>${monAn.soLuongDaBan}</td>
                      <td>
                        <c:choose>
                          <c:when test="${!monAn.available}">
                            <span class="status-badge" data-status="inactive">Tạm ẩn</span>
                          </c:when>
                          <c:when test="${monAn.soLuongTon == 0}">
                            <span class="status-badge" data-status="out-of-stock">Hết hàng</span>
                          </c:when>
                          <c:otherwise>
                            <span class="status-badge" data-status="active">Đang bán</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>
                        <div class="action-row">
                          <a href="<c:url value='/manager/products/detail'><c:param name='productID' value='${monAn.maMon}'/></c:url>" class="btn btn-ghost icon-btn">Xem</a>
                          <a href="<c:url value='/manager/products/update'><c:param name='productID' value='${monAn.maMon}'/></c:url>" class="btn btn-ghost icon-btn">Sửa</a>
                          <a href="<c:url value='/manager/products/delete'><c:param name='productID' value='${monAn.maMon}'/></c:url>" class="btn btn-ghost icon-btn">Xóa</a>
                        </div>
                      </td>
                    </tr>
                  </c:forEach>
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
