<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="userName"     value="${sessionScope.user.hoTen}" />
<c:set var="userRoleName" value="${sessionScope.user.vaiTro.tenVT}" />
<c:set var="userInitials" value="${not empty userName ? fn:toUpperCase(fn:substring(userName, 0, 1)) : 'M'}" />
<div class="admin-topbar">
    <div class="admin-topbar__copy">
        <h2>Jollibug Control Center</h2>
    </div>

    <%-- Nút toggle Phantom Read Demo – chỉ hiển thị trên trang thống kê đơn hàng --%>
    <c:set var="bodyPage" value="${pageScope['body-page']}" />
    <div id="phantom-read-toggle-wrap" style="display:flex; align-items:center; gap:0.5rem; margin-right:auto; padding-left:1.5rem;">
        <button class="demo-mode-toggle"
                type="button"
                id="phantomReadToggle"
                data-mode="SAFE"
                title="Chế độ SAFE: SERIALIZABLE – ngăn Phantom Read"
                style="display:none;">
            <span class="demo-mode-toggle__track" aria-hidden="true">
                <span class="demo-mode-toggle__knob"></span>
            </span>
            <span id="phantomReadLabel">SAFE</span>
        </button>
        <span id="phantomReadModeDesc" class="muted" style="display:none; font-size:0.75rem;"></span>
    </div>

    <div class="admin-topbar__user">
        <div class="admin-avatar" id="topbar-user-initials" aria-hidden="true">${userInitials}</div>
        <div class="stack" style="gap:0.15rem;">
            <strong id="topbar-user-name">${not empty userName ? userName : 'Tài khoản'}</strong>
            <span class="muted" id="topbar-user-role">${not empty userRoleName ? userRoleName : 'Quản lý'}</span>
        </div>
        <button class="btn btn-outline" type="button" id="btn-logout"
                onclick="window.location.href='/logout'">Đăng xuất</button>
    </div>
</div>