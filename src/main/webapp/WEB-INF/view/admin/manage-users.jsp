<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Admin â€” Manage Users</title>
  <meta name="description" content="Jollibug Super Admin â€” centralized user management for all roles: Staff, Manager, and Client." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
</head>

<!--
  data-admin-role="admin"       â†’ Spring Security: ROLE_SUPER_ADMIN
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
            <div class="select-group" style="gap:0;">
              <select id="role-filter" aria-label="Filter by role" style="padding:0.8rem 1rem;">
                <option value="all">Vai trò</option>
                <option value="manager">Quản lý</option>
                <option value="staff">Nhân viên</option>
                <option value="client">Khách hàng</option>
              </select>
            </div>
            <!-- Search -->
            <label class="table-search">
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/>
              </svg>
              <input id="user-search" type="search" placeholder="Search name or email" />
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
                <th>Ngày tham gia</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody id="users-table-body">
              <c:choose>
                <c:when test="${activeUserTab == 'blocked'}">
                  <tr>
                    <td>Phan Mỹ Linh</td>
                    <td>linh.phan@gmail.com</td>
                    <td>Khách hàng</td>
                    <td><span class="status-badge" data-status="banned">Bị khóa</span></td>
                    <td>15/03/2026</td>
                    <td>
                      <a href="<c:url value='/admin/users/detail'/>" class="btn btn-ghost" type="button">Xem</a>
                      <a href="<c:url value='/admin/users/unban'/>" class="btn btn-ghost" type="button">Mở khóa</a>
                    </td>
                  </tr>
                  <tr>
                    <td>Hoàng Anh Đức</td>
                    <td>duc.hoang@jollibug.vn</td>
                    <td>Nhân viên</td>
                    <td><span class="status-badge" data-status="banned">Bị khóa</span></td>
                    <td>08/02/2026</td>
                    <td>
                      <a href="<c:url value='/admin/users/detail'/>" class="btn btn-ghost" type="button">Xem</a>
                      <a href="<c:url value='/admin/users/unban'/>" class="btn btn-ghost" type="button">Mở khóa</a>
                    </td>
                  </tr>
                </c:when>
                <c:otherwise>
                  <tr>
                    <td>Nguyễn Minh Quân</td>
                    <td>quan.nguyen@jollibug.vn</td>
                    <td>Quản lý</td>
                    <td><span class="status-badge" data-status="active">Đang hoạt động</span></td>
                    <td>12/01/2026</td>
                    <td>
                      <a href="<c:url value='/admin/users/detail'/>" class="btn btn-ghost" type="button">Xem</a>
                      <a href="<c:url value='/admin/users/update'/>" class="btn btn-ghost" type="button">Sửa</a>
                      <a href="<c:url value='/admin/users/delete'/>" class="btn btn-ghost" type="button">Xóa</a>
                      <a href="<c:url value='/admin/users/ban'/>" class="btn btn-ghost" type="button">Khóa</a>
                    </td>
                  </tr>
                  <tr>
                    <td>Trần Thu Hà</td>
                    <td>ha.tran@jollibug.vn</td>
                    <td>Nhân viên</td>
                    <td><span class="status-badge" data-status="active">Đang hoạt động</span></td>
                    <td>25/11/2025</td>
                    <td>
                      <a href="<c:url value='/admin/users/detail'/>" class="btn btn-ghost" type="button">Xem</a>
                      <a href="<c:url value='/admin/users/update'/>" class="btn btn-ghost" type="button">Sửa</a>
                      <a href="<c:url value='/admin/users/delete'/>" class="btn btn-ghost" type="button">Xóa</a>
                      <a href="<c:url value='/admin/users/ban'/>" class="btn btn-ghost" type="button">Khóa</a>
                    </td>
                  </tr>
                  <tr>
                    <td>Lê Hoàng Phúc</td>
                    <td>phuc.le@gmail.com</td>
                    <td>Khách hàng</td>
                    <td><span class="status-badge" data-status="active">Đang hoạt động</span></td>
                    <td>03/08/2025</td>
                    <td>
                      <a href="<c:url value='/admin/users/detail'/>" class="btn btn-ghost" type="button">Xem</a>
                      <a href="<c:url value='/admin/users/update'/>" class="btn btn-ghost" type="button">Sửa</a>
                      <a href="<c:url value='/admin/users/delete'/>" class="btn btn-ghost" type="button">Xóa</a>
                      <a href="<c:url value='/admin/users/ban'/>" class="btn btn-ghost" type="button">Khóa</a>
                    </td>
                  </tr>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>
      </section>
      </main>
  </div><!-- /data-admin-table-root -->


  <!-- Toast stack -->
  </body>
</html>

