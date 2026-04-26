<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pathWithinHandler" value="${requestScope['org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping']}" />
<c:set var="forwardUri" value="${requestScope['javax.servlet.forward.request_uri']}" />
<c:set var="requestUri" value="${empty forwardUri ? pageContext.request.requestURI : forwardUri}" />
<c:set var="currentPath" value="${not empty pathWithinHandler ? pathWithinHandler : fn:substringAfter(requestUri, pageContext.request.contextPath)}" />

<c:url var="menuUrl" value="/menu" />
<c:url var="chatUrl" value="/chat" />
<c:url var="cartUrl" value="/cart" />

  <header class="site-header" id="site-header">
    <div class="container site-header__inner">
      <a class="brand" href="/" aria-label="Jollibug home">
        <span class="brand__mark">JB</span>
        <span class="brand__copy">
          <span class="brand__title">Jollibug</span>
          <span class="brand__tag">Premium fast-food ordering</span>
        </span>
      </a>

      <nav class="main-nav" aria-label="Primary">
        <a href="${homeUrl}"<c:if test="${currentPath == '/'}"> class="is-active"</c:if>>Trang chủ</a>
        <a href="${menuUrl}"<c:if test="${fn:contains(currentPath, '/menu')}"> class="is-active"</c:if>>Thực đơn</a>
        <a href="${chatUrl}"<c:if test="${fn:contains(currentPath, '/chat')}"> class="is-active"</c:if>>Nhắn tin</a>
      </nav>

      <div class="header-actions">
        <a class="btn btn-primary" href="/cart"  >
          Giỏ hàng: <span data-cart-count id="header-cart-count">0</span>
        </a>
        <a class="btn btn-outline" href="/login">Đăng nhập</a>
        <a class="btn btn-ghost" href="/profile">Tài khoản</a>
      </div>
    </div>
  </header>