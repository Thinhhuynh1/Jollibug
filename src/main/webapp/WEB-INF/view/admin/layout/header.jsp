<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="admin-topbar">
    <div class="admin-topbar__copy">
        <h2>Jollibug Control Center</h2>
    </div>
    <div class="admin-topbar__user">
        <div class="admin-avatar" id="topbar-user-initials">JB</div>
        <div class="stack" style="gap:0.15rem;">
        <span class="muted" id="topbar-user-role">Admin</span>
        </div>
        <button class="btn btn-outline" type="button" data-admin-logout id="btn-logout">Đăng xuất</button>
    </div>
</div>