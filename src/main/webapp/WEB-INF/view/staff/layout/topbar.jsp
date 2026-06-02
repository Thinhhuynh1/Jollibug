<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="userName" value="${sessionScope.user.hoTen}" />
<c:set var="userInitials" value="${not empty userName ? fn:toUpperCase(fn:substring(userName, 0, 1)) : 'S'}" />

<div class="admin-topbar">
  <h2>Jollibug Control Center</h2>
  <div class="admin-topbar__user">
    <div class="admin-avatar" id="topbar-user-initials" aria-hidden="true">${userInitials}</div>
    <div class="stack" style="gap:0.15rem;">
      <strong id="topbar-user-name">${not empty userName ? userName : 'Tài khoản'}</strong>
      <span class="muted" id="topbar-user-role">Nhân viên</span>
    </div>
    <button class="btn btn-outline" type="button" id="btn-logout" onclick="window.location.href = '/logout'">Đăng xuất</button>
  </div>
</div>
