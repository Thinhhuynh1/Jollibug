<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Khách hàng</title>
  <meta name="description" content="Danh sách khách hàng từ cơ sở dữ liệu." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
</head>

<body data-admin-role="admin" data-admin-page="manage-users">
  <div class="admin-shell admin-body" data-admin-table-root>
    <jsp:include page="../layout/sidebar.jsp" />

    <main class="admin-main">
      <jsp:include page="../layout/topbar.jsp" />

      <section class="admin-panel">
        <div class="panel-header">
          <div class="stack" style="gap:0.3rem;">
            <h1 class="section-title">Quản lý khách hàng</h1>
          </div>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table" id="users-table">
            <thead>
              <tr>
                <th>Người dùng</th>
                <th>Email</th>
                <th>Số điện thoại</th>
                <th>Trạng thái</th>
                <th>Ngày tham gia</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody id="users-table-body">
              <c:choose>
                <c:when test="${not empty clients}">
                  <c:forEach var="client" items="${clients}">
                    <tr>
                      <td>${client.hoTen}</td>
                      <td>${client.email}</td>
                      <td>${empty client.sdt ? '-' : client.sdt}</td>
                      <td>${empty client.trangThai ? '-' : client.trangThai}</td>
                      <td>${client.createdAtDisplay}</td>
                      <td>
                        <a href="<c:url value='/staff/clients/detail?clientId=${client.maTK}'/>" class="btn btn-ghost" type="button">Xem</a>
                      </td>
                    </tr>
                  </c:forEach>
                </c:when>
                <c:otherwise>
                  <tr>
                    <td colspan="6" style="text-align:center;">Không có khách hàng nào.</td>
                  </tr>
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
