<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pathWithinHandler" value="${requestScope['org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping']}" />
<c:set var="forwardUri" value="${requestScope['javax.servlet.forward.request_uri']}" />
<c:set var="requestUri" value="${empty forwardUri ? pageContext.request.requestURI : forwardUri}" />
<c:set var="currentPath" value="${not empty pathWithinHandler ? pathWithinHandler : fn:substringAfter(requestUri, pageContext.request.contextPath)}" />

<c:set var="ordersActive" value="${fn:contains(currentPath, '/orders')}" />
<c:set var="addressActive" value="${fn:contains(currentPath, '/address')}" />
<c:set var="profileActive" value="${fn:contains(currentPath, '/profile')}" />
<c:set var="resetPasswordActive" value="${fn:contains(currentPath, '/reset-password')}" />

<c:url var="ordersUrl" value="/orders" />
<c:url var="addressUrl" value="/address" />
<c:url var="profileUrl" value="/profile" />
<c:url var="resetPasswordUrl" value="/reset-password" />

<aside class="profile-sidebar">
    <div class="profile-sidebar__avatar">${fn:substring(sessionScope.user.hoTen, 0, 1)}</div>
    <div class="profile-sidebar__greeting">Xin chào,</div>
    <div class="profile-sidebar__name">${sessionScope.user.hoTen}</div>
    <a class="profile-sidebar__logout" href="/logout">Đăng xuất</a>

    <nav class="profile-nav" aria-label="Profile navigation">
    <a href="${ordersUrl}"<c:if test="${ordersActive}"> class="is-active"</c:if>>Đơn hàng</a>
    <a href="${addressUrl}"<c:if test="${addressActive}"> class="is-active"</c:if>>Địa chỉ của bạn</a>
    <a href="${profileUrl}"<c:if test="${profileActive}"> class="is-active"</c:if>>Chi tiết tài khoản</a>
    <a href="${resetPasswordUrl}"<c:if test="${resetPasswordActive}"> class="is-active"</c:if>>Đặt lại mật khẩu</a>
    </nav>
</aside>