<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Quản lý sản phẩm</title>
  <meta name="description" content="Jollibug Manager - quản lý hình ảnh, giá bán, danh mục và trạng thái tồn kho của sản phẩm." />

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

      <section class="admin-panel" style="margin-bottom:var(--space-6);">
        <div class="panel-header" style="display:flex; flex-direction:column; align-items:stretch; gap:1rem;">
          <div class="stack" style="gap:0.35rem; max-width:42rem;">
            <h1 class="section-title" id="admin-table-title" style="margin:0;">Quản lý sản phẩm</h1>
          </div>
          <form class="panel-controls" action="<c:url value='/manager/products'/>" method="get" style="display:flex; flex-wrap:nowrap; align-items:center; gap:0.75rem; width:100%; overflow-x:auto; padding-bottom:2px;">
            <div class="select-group" style="gap:0; min-width: 14rem; flex:0 0 auto;">
              <select name="categoryID" onchange="this.form.submit()" aria-label="Lọc theo danh mục">
                <option value="" ${empty selectCategoryID ? 'selected' : ''}>Tất cả danh mục</option>
                <c:forEach var="dm" items="${danhMuc}">
                  <option value="${dm.maDM}" ${selectCategoryID != null && selectCategoryID == dm.maDM ? 'selected' : ''}>${dm.tenDM}</option>
                </c:forEach>
              </select>
            </div>
            <div class="select-group" style="gap:0; min-width: 12rem; flex:0 0 auto;">
              <select name="filter" id="menu-sort" data-menu-sort onchange="this.form.submit()" aria-label="Sắp xếp sản phẩm">
                <option value="popular" ${selectedFilter == 'popular' ? 'selected' : ''}>Bộ lọc</option>
                <option value="price-low" ${selectedFilter == 'price-low' ? 'selected' : ''}>Giá: thấp đến cao</option>
                <option value="price-high" ${selectedFilter == 'price-high' ? 'selected' : ''}>Giá: cao đến thấp</option>
                <option value="rating" ${selectedFilter == 'rating' ? 'selected' : ''}>Bán chạy</option>
              </select>
            </div>
            <div class="select-group" style="gap:0; min-width: 12rem; flex:0 0 auto;">
              <select name="status" onchange="this.form.submit()" aria-label="Lọc theo trạng thái">
                <option value="" ${empty selectedStatus ? 'selected' : ''}>Tất cả trạng thái</option>
                <option value="active" ${selectedStatus == 'active' ? 'selected' : ''}>Đang hoạt động</option>
                <option value="inactive" ${selectedStatus == 'inactive' ? 'selected' : ''}>Tạm ẩn</option>
                <option value="out_of_stock" ${selectedStatus == 'out_of_stock' ? 'selected' : ''}>Hết hàng</option>
              </select>
            </div>
              <label class="table-search" style="flex:0 0 19rem;">
                <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="11" cy="11" r="7"></circle>
                  <path d="m20 20-3.5-3.5"></path>
                </svg>
                <input type="search" placeholder="Tìm món ăn..." 
                    name="keyword" value="${keyword}"
                    onkeydown="if(event.key === 'Enter'){ this.form.submit(); }"/>
              </label>
            <a href="<c:url value='/manager/products'/>" class="btn btn-ghost" style="flex:0 0 auto;">Xóa lọc</a>
            <a href="/manager/products/create" class="btn btn-primary" style="flex:0 0 auto;">
              Thêm sản phẩm mới
            </a>
          </form>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table">
            <thead>
              <tr id="admin-table-head-row">
                <th>Sản phẩm</th>
                <th>Ảnh</th>
                <th>Danh mục</th>
                <th>Giá</th>
                <th>Tồn kho</th>
                <th>Đã bán</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody id="admin-table-body">
              <c:forEach var="monAn" items="${listMonAn}">
                <tr>
                  <td>${monAn.tenMon}</td>
                  <td>
                    <c:if test="${not empty monAn.img}">
                      <img src="/images/${monAn.img}" alt="${monAn.tenMon}" style="width:56px; height:56px; object-fit:cover; border-radius:8px; border:1px solid #ddd;" />
                    </c:if>
                  </td>
                  <td>${monAn.danhMuc.tenDM}</td>
                  <td><fmt:formatNumber value="${monAn.gia}" type="number" />đ</td>
                  <td>${monAn.soLuongTon}</td>
                  <td>${monAn.soLuongDaBan}</td>
                  <td>
                    <c:choose>
                      <c:when test="${!monAn.available}">
                        <span class="status-badge" data-status="inactive">tạm ẩn</span>
                      </c:when>
                      <c:when test="${monAn.soLuongTon == 0}">
                        <span class="status-badge" data-status="out-of-stock">hết hàng</span>
                      </c:when>
                      <c:otherwise>
                        <span class="status-badge" data-status="active">đang hoạt động</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <div class="action-row">
                      <a href="/manager/products/detail?productID=${monAn.maMon}" class="btn btn-ghost icon-btn" type="button">Xem</a>
                      <a href="/manager/products/update?productID=${monAn.maMon}" class="btn btn-ghost icon-btn" type="button">Sửa</a>
                      <a href="/manager/products/delete?productID=${monAn.maMon}" class="btn btn-ghost icon-btn" type="button">Xóa</a>
                    </div>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </section>

    </main>
  </div><!-- /data-admin-table-root -->
  </body>
</html>

