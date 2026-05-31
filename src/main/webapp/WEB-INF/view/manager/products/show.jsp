<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Quản lý món ăn</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/manager.css'/>" />
</head>

<body data-admin-role="manager" data-admin-page="products" data-product-list>

  <div class="admin-shell admin-body">

    <jsp:include page="../layout/sidebar.jsp" />

    <main class="admin-main">
      <jsp:include page="../layout/topbar.jsp" />

      <c:if test="${not empty message}">
        <div class="manager-flash manager-flash--success">${message}</div>
      </c:if>
      <c:if test="${not empty error}">
        <div class="manager-flash manager-flash--error">${error}</div>
      </c:if>

      <section class="admin-panel">
        <div class="panel-header manager-products-header">
          <div class="stack">
            <h1 class="section-title">Quản lý món ăn</h1>
            <p class="muted">Tìm kiếm, xem chi tiết và sắp xếp danh sách món ăn.</p>
            <div class="manager-feature-tags">
              <span class="manager-feature-tag">🔍 Tìm kiếm</span>
              <span class="manager-feature-tag">👁 Xem chi tiết</span>
              <span class="manager-feature-tag">↕ Sắp xếp</span>
            </div>
          </div>

          <!-- Form: tìm kiếm + sắp xếp -->
          <form id="product-filter-form" class="panel-controls manager-products-filters"
                action="<c:url value='/manager/products'/>" method="get">
            <div class="select-group">
              <label class="sr-only" for="categoryID">Danh mục</label>
              <select name="categoryID" id="categoryID" aria-label="Lọc danh mục">
                <option value="" ${empty selectCategoryID ? 'selected' : ''}>Tất cả danh mục</option>
                <c:forEach var="dm" items="${danhMuc}">
                  <option value="${dm.maDM}" ${selectCategoryID != null && selectCategoryID == dm.maDM ? 'selected' : ''}>${dm.tenDM}</option>
                </c:forEach>
              </select>
            </div>

            <div class="select-group">
              <label class="sr-only" for="filter">Sắp xếp</label>
              <select name="filter" id="filter" aria-label="Sắp xếp món ăn">
                <option value="popular" ${selectedFilter == 'popular' ? 'selected' : ''}>Mới nhất</option>
                <option value="name-asc" ${selectedFilter == 'name-asc' ? 'selected' : ''}>Tên A → Z</option>
                <option value="price-low" ${selectedFilter == 'price-low' ? 'selected' : ''}>Giá thấp → cao</option>
                <option value="price-high" ${selectedFilter == 'price-high' ? 'selected' : ''}>Giá cao → thấp</option>
                <option value="rating" ${selectedFilter == 'rating' ? 'selected' : ''}>Bán chạy</option>
              </select>
            </div>

            <div class="select-group">
              <label class="sr-only" for="status">Trạng thái</label>
              <select name="status" id="status" aria-label="Lọc trạng thái">
                <option value="" ${empty selectedStatus ? 'selected' : ''}>Tất cả trạng thái</option>
                <option value="active" ${selectedStatus == 'active' ? 'selected' : ''}>Đang bán</option>
                <option value="inactive" ${selectedStatus == 'inactive' ? 'selected' : ''}>Tạm ẩn</option>
                <option value="out_of_stock" ${selectedStatus == 'out_of_stock' ? 'selected' : ''}>Hết hàng</option>
              </select>
            </div>

            <label class="table-search manager-search-field">
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"></circle>
                <path d="m20 20-3.5-3.5"></path>
              </svg>
              <input type="search" id="keyword" name="keyword" value="${keyword}"
                     placeholder="Tìm tên món, mô tả, danh mục..." autocomplete="off" />
            </label>

            <button type="submit" class="btn btn-primary">Tìm kiếm</button>
            <a href="<c:url value='/manager/products'/>" class="btn btn-ghost">Xóa lọc</a>
            <a href="<c:url value='/manager/products/create'/>" class="btn btn-primary">+ Thêm món</a>
          </form>
        </div>

        <p class="manager-result-summary" data-result-summary>
          <c:choose>
            <c:when test="${not empty keyword}">
              Tìm thấy <strong>${fn:length(listMonAn)}</strong> món cho &quot;${keyword}&quot;
            </c:when>
            <c:otherwise>
              Hiển thị <strong>${fn:length(listMonAn)}</strong> món ăn
            </c:otherwise>
          </c:choose>
        </p>

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
                      <div class="empty-state" style="padding:2rem;text-align:center;">
                        <h3 style="margin:0 0 0.5rem;">Không tìm thấy món ăn</h3>
                        <p class="muted">Thử đổi từ khóa hoặc bộ lọc sắp xếp.</p>
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
                          <c:otherwise><span class="muted">—</span></c:otherwise>
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
                          <button type="button" class="btn btn-ghost icon-btn" data-product-detail="${monAn.maMon}">Xem</button>
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

  <!-- Panel xem chi tiết (gọi API) -->
  <aside class="sdp sdp--hidden" id="product-detail-panel" role="dialog" aria-modal="true" aria-labelledby="sdp-product-name">
    <div class="sdp__backdrop" data-sdp-close></div>
    <div class="sdp__card">
      <button class="sdp__close-btn" type="button" data-sdp-close aria-label="Đóng">&times;</button>

      <div class="sdp__product-media">
        <img class="sdp__product-img" id="sdp-product-image" src="" alt="Món ăn" width="120" height="120" />
        <span class="status-badge sdp__product-badge" id="sdp-product-badge" data-status="active">—</span>
      </div>

      <div class="sdp__hero sdp__hero--product">
        <div class="sdp__hero-meta">
          <h2 class="sdp__title" id="sdp-product-name">—</h2>
          <span class="sdp__price" id="sdp-product-price">—</span>
        </div>
        <span class="sdp__cat-pill" id="sdp-product-cat">—</span>
      </div>

      <section class="sdp__section">
        <h3 class="sdp__section-title">Thông tin món</h3>
        <dl class="sdp__fields">
          <div class="sdp__field"><dt>Mã món</dt><dd id="sdp-product-id">—</dd></div>
          <div class="sdp__field"><dt>Tồn kho</dt><dd id="sdp-product-stock">—</dd></div>
          <div class="sdp__field"><dt>Đã bán</dt><dd id="sdp-product-sold">—</dd></div>
          <div class="sdp__field"><dt>Mô tả</dt><dd id="sdp-product-desc">—</dd></div>
        </dl>
      </section>

      <div class="sdp__actions">
        <a class="btn btn-primary sdp__action-btn" id="sdp-edit-btn" href="#">Sửa món</a>
        <button class="btn btn-ghost sdp__action-btn" type="button" data-sdp-close>Đóng</button>
      </div>
    </div>
  </aside>

  <script src="<c:url value='/js/manager/products.js'/>" defer></script>
</body>
</html>
