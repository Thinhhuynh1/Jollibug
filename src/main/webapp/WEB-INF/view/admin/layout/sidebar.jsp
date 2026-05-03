<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pathWithinHandler" value="${requestScope['org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping']}" />
<c:set var="forwardUri" value="${requestScope['javax.servlet.forward.request_uri']}" />
<c:set var="requestUri" value="${empty forwardUri ? pageContext.request.requestURI : forwardUri}" />
<c:set var="resolvedPath" value="${not empty pathWithinHandler ? pathWithinHandler : fn:substringAfter(requestUri, pageContext.request.contextPath)}" />
<c:set var="currentPath" value="${empty resolvedPath ? '/' : resolvedPath}" />

<!-- SIDEBAR -->
<aside class="admin-sidebar">
  <div class="admin-sidebar__inner">
    <div class="admin-brand">
      <div class="brand">
        <span class="brand__mark">JB</span>
        <span class="brand__copy">
          <span class="brand__title">Quản trị viên Jollibug</span>
          <span class="brand__tag">Control Center</span>
        </span>
      </div>
    </div>

    <nav class="admin-nav">
      <span class="admin-nav__section">Workspace</span>
      <a class="<c:if test='${currentPath == "/admin"}'>is-active</c:if>" href="<c:url value='/admin'/>">Dashboard</a>
      <a class="<c:if test='${fn:startsWith(currentPath, "/admin/users")}'>is-active</c:if>" href="<c:url value='/admin/users'/>">Quản lý người dùng</a>
    </nav>
  </div>
</aside>