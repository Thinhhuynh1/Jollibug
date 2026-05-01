<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | ADMIN</title>
  <meta name="description" content="Jollibug Super Admin â€” centralized user management for all roles: Staff, Manager, and Client." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
</head>

<!--
  data-admin-role="admin"       â†’ Spring Security: ROLE_SUPER_ ADMIN
  data-admin-page="manage-users" â†’ manage-users.js render branch
-->
<body data-admin-role="admin" data-admin-page="manage-users">

  <div class="admin-shell admin-body" data-admin-table-root>

    <jsp:include page="layout/sidebar.jsp" />

    <main class="admin-main">

      <jsp:include page="layout/topbar.jsp" />

      <c:set var="activeUserTab" value="${empty userTab ? 'active' : userTab}" />


      <section class="admin-panel">
        <div class="panel-header">
          <div class="stack" style="gap:0.3rem;">
            <h1 class="section-title">Quản lý người dùng</h1>
          </div>
          <div class="panel-controls">
            <!-- Status tabs -->
            <div class="order-filter-strip__pills" role="tablist" aria-label="Filter by account status" style="gap:0.35rem;">
              <a href="<c:url value='/admin/users'/>" class="btn btn-primary" style="background-color: green; color: white;">
                Đang hoạt động
              </a>
              <a href="<c:url value='/admin/users/block'/>" class="btn btn-primary" style="background-color: red; color: white;">
                Bị khóa
              </a>
            </div>
            <!-- Role filter dropdown -->
            <div class="select-group" style="gap:0;" >
              <select id="role-filter" aria-label="Filter by role" style="padding:0.8rem 1rem;"
                      onchange="handleRoleFilter(this.value)">
                <option value="All" <c:if test="${selectedRole == null || selectedRole == 'All'}">selected</c:if>>Tất cả</option>
                <option value="Manager" <c:if test="${selectedRole == 'Manager'}">selected</c:if>>Quản lý</option>
                <option value="Staff" <c:if test="${selectedRole == 'Staff'}">selected</c:if>>Nhân viên</option>
                <option value="Client" <c:if test="${selectedRole == 'Client'}">selected</c:if>>Khách hàng</option>
                <option value="Admin" <c:if test="${selectedRole == 'Admin'}">selected</c:if>>Quản trị viên</option>
              </select>
            </div>
            <!-- Search -->
            <label class="table-search">
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/>
              </svg>
              <input id="user-search" type="search" placeholder="Search name or email" 
                    onkeydown="handleSearch(event)"/>
            </label>
            <a href="<c:url value='/admin/users/create'/>" class="btn btn-primary" type="button" id="btn-add-user">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15" aria-hidden="true">
                <path d="M5 12h14M12 5v14"/>
              </svg>
              Thêm người dùng
            </a>
          </div>
        </div>

        <div class="table-wrap admin-table-wrap">
          <table class="admin-table" id="users-table">
            <thead>
              <tr>
                <th>Người dùng</th>
                <th>Email</th>
                <th>Vai trò</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody id="users-table-body">
              <c:choose>
                <c:when test="${activeUserTab == 'blocked'}">
                  <c:forEach var="user" items="${lisUsers}">
                      <tr>
                        <td>${user.hoTen}</td>
                        <td>${user.email}</td>
                        <td>${user.vaiTro.tenVt}</td>
                        <td><span class="status-badge" data-status="out-of-stock">${user.trangThai}</span></td>
                        <td>
                          <a href="/admin/users/${user.maTk}" class="btn btn-ghost" type="button">Xem</a>
                          <a href="/admin/users/update/${user.maTk}" class="btn btn-ghost" type="button">Sửa</a>
                          <a href="/admin/users/delete/${user.maTk}" class="btn btn-ghost" type="button">Xóa</a>
                          <a href="/admin/users/unban/${user.maTk}" class="btn btn-ghost" type="button">Mở khóa</a>
                        </td>
                      </tr>
                    </c:forEach>

                </c:when>
                <c:otherwise>
                  <c:forEach var="user" items="${lisUsers}">
                      <tr>
                        <td>${user.hoTen}</td>
                        <td>${user.email}</td>
                        <td>${user.vaiTro.tenVt}</td>
                        <td><span class="status-badge" data-status="active">${user.trangThai}</span></td>
                        <td>
                          <a href="/admin/users/${user.maTk}" class="btn btn-ghost" type="button">Xem</a>
                          <a href="/admin/users/update/${user.maTk}" class="btn btn-ghost" type="button">Sửa</a>
                          <a href="/admin/users/delete/${user.maTk}" class="btn btn-ghost" type="button">Xóa</a>
                          <a href="/admin/users/ban/${user.maTk}" class="btn btn-ghost" type="button">Khóa</a>
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
  </div><!-- /data-admin-table-root -->

  <script>
    function handleRoleFilter(role) {
      const currentTab = '${userTab}' === 'blocked' ? '/block' : '';
      const baseUrl = '/admin/users' + currentTab;
      const url = (role === 'All') ? baseUrl : baseUrl + '?role=' + (role);
      window.location.href = url;
    }
    
    function handleSearch(e) {
      if(e.key == "Enter"){
        const text = e.target.value;
        const role = document.getElementById("role-filter").value;

        const currentTab = '${userTab}' === 'blocked' ? '/block' : '';
        const basicUrl = '/admin/users' + currentTab;

        let url = basicUrl;

        if(role != 'All'){
          url += '?role=' + role;
        }

        if(text && text.trim() != ''){
          url += (url.includes('?') ? '&' : '?') + 'keyword=' + text;
        }

        window.location.href = url;
      }
      
    }

  </script>

  <!-- Toast stack -->
  </body>
</html>

