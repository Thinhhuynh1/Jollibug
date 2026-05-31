<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Quản lý danh mục</title>
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

      <c:if test="${not empty message}">
        <div class="manager-flash manager-flash--success">${message}</div>
      </c:if>
      <c:if test="${not empty error}">
        <div class="manager-flash manager-flash--error">${error}</div>
      </c:if>

      <section class="admin-panel">
        <div class="panel-header">
          <div class="stack" style="gap:0.3rem;">
            <h1 class="section-title">Quản lý danh mục</h1>
            <p class="muted">Thêm, sửa, xóa và xem chi tiết danh mục món ăn.</p>
          </div>
          <div class="panel-controls">
            <a href="/manager/categories/create" class="btn btn-primary">Thêm danh mục mới</a>
          </div>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table">
            <thead>
              <tr>
                <th>Danh mục</th>
                <th>Số món</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="danhMuc" items="${listDanhMuc}">
                <tr>
                  <td>${danhMuc.tenDM}</td>
                  <td>${danhMuc.soMon}</td>
                  <td>
                    <c:choose>
                      <c:when test="${danhMuc.available}">
                        <span class="status-badge" data-status="active">Đang hoạt động</span>
                      </c:when>
                      <c:otherwise>
                        <span class="status-badge" data-status="out-of-stock">Ẩn</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <div class="action-row">
                      <a href="/manager/categories/detail?categoryID=${danhMuc.maDM}" class="btn btn-ghost icon-btn">Xem</a>
                      <a href="/manager/categories/update?categoryID=${danhMuc.maDM}" class="btn btn-ghost icon-btn">Sửa</a>
                      <a href="/manager/categories/delete?categoryID=${danhMuc.maDM}" class="btn btn-ghost icon-btn">Xóa</a>
                    </div>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty listDanhMuc}">
                <tr>
                  <td colspan="4" style="text-align:center;padding:2rem;">Chưa có danh mục nào.</td>
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
