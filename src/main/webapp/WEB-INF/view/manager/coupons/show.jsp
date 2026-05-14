<%@page contentType="text/html" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <!DOCTYPE html>
    <html lang="vi">

    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <title>Jollibug | Quản lý mã giảm giá</title>
      <meta name="description" content="Jollibug Manager - quản lý mã giảm giá." />
      <link rel="preconnect" href="https://fonts.googleapis.com" />
      <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
      <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap"
        rel="stylesheet" />
      <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
      <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
      <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
    </head>

    <body data-admin-role="manager" data-admin-page="coupons">
      <div class="admin-shell admin-body" data-admin-table-root>
        <jsp:include page="../layout/sidebar.jsp" />
        <main class="admin-main">
          <jsp:include page="../layout/topbar.jsp" />
          <section class="admin-panel">
            <div class="panel-header">
              <div class="stack" style="gap:0.3rem;">
                <h1 class="section-title" style="margin:0;">Quản lý mã giảm giá</h1>
              </div>
              <div class="panel-controls">
                <form action="<c:url value='/manager/coupons'/>" method="get" class="table-search" style="display:flex; align-items:center; gap:0.5rem; width:auto;">
                  <div style="position:relative; display:flex; align-items:center;">
                    <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2" style="position:absolute; left:0.75rem; width:1.1rem; height:1.1rem; color:var(--text-muted);">
                      <circle cx="11" cy="11" r="7"></circle>
                      <path d="m20 20-3.5-3.5"></path>
                    </svg>
                    <input type="search" name="keyword" value="${keyword}" placeholder="Tìm mã, mô tả..." 
                      style="padding-left:2.5rem; width:250px;" />
                    <c:if test="${not empty keyword}">
                      <a href="<c:url value='/manager/coupons'/>" title="Xóa tìm kiếm" 
                        style="position:absolute; right:0.75rem; color:var(--text-muted); display:flex; align-items:center;">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:1rem; height:1rem;">
                          <path d="M18 6L6 18M6 6l12 12"></path>
                        </svg>
                      </a>
                    </c:if>
                  </div>
                  <button type="submit" class="btn btn-ghost" style="padding: 0.5rem 1rem;">Tìm</button>
                </form>
                <a href="<c:url value='/manager/coupons/create'/>" class="btn btn-primary">Thêm mã giảm giá</a>
              </div>
            </div>
            <div class="table-wrap admin-table-wrap">
              <table class="admin-table">
                <thead>
                  <tr>
                    <th>Mã</th>
                    <th>Mô tả</th>
                    <th>Giảm</th>
                    <th>Số lượng</th>
                    <th>Bắt đầu</th>
                    <th>Kết thúc</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach items="${coupons}" var="coupon">
                    <tr>
                      <td style="font-weight: 700; color: var(--primary-color);">${coupon.tenMa}</td>
                      <td>${coupon.moTa}</td>
                      <td>${coupon.discountDisplay}</td>
                      <td>${coupon.soLuong}</td>
                      <td>${coupon.ngayBatDauValue}</td>
                      <td>${coupon.ngayKetThucValue}</td>
                      <td>
                        <span class="status-badge" data-status="${coupon.status == 'Đang hoạt động' ? 'active' : (coupon.status == 'Sắp diễn ra' ? 'featured' : 'inactive')}">
                          ${coupon.status}
                        </span>
                      </td>
                      <td>
                        <div class="action-row">
                          <a href="<c:url value='/manager/coupons/detail?couponID=${coupon.maGG}'/>" class="btn btn-ghost icon-btn">Xem</a>
                          <a href="<c:url value='/manager/coupons/update?couponID=${coupon.maGG}'/>" class="btn btn-ghost icon-btn">Sửa</a>
                          <a href="<c:url value='/manager/coupons/delete?couponID=${coupon.maGG}'/>" class="btn btn-ghost icon-btn">Xóa</a>
                        </div>
                      </td>
                    </tr>
                  </c:forEach>
                  <c:if test="${empty coupons}">
                    <tr>
                      <td colspan="8" style="text-align: center;">Không có mã giảm giá nào</td>
                    </tr>
                  </c:if>
                </tbody>
              </table>
            </div>
          </section>
        </main>
      </div>
    </body>

    </html>