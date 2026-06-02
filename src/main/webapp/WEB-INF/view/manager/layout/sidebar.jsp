<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pathWithinHandler" value="${requestScope['org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping']}" />
<c:set var="forwardUri" value="${requestScope['javax.servlet.forward.request_uri']}" />
<c:set var="requestUri" value="${empty forwardUri ? pageContext.request.requestURI : forwardUri}" />
<c:set var="resolvedPath" value="${not empty pathWithinHandler ? pathWithinHandler : fn:substringAfter(requestUri, pageContext.request.contextPath)}" />
<c:set var="currentPath" value="${empty resolvedPath ? '/' : resolvedPath}" />

<aside class="admin-sidebar">
  <div class="admin-sidebar__inner">
    <div class="admin-brand">
      <div class="brand">
        <span class="brand__mark">JB</span>
        <span class="brand__copy">
          <span class="brand__title">Quản lý Jollibug</span>
          <span class="brand__tag">Trung tâm điều hành</span>
        </span>
      </div>
    </div>

    <nav class="admin-nav">
      <span class="admin-nav__section">Điều hướng</span>
      <a class="<c:if test='${currentPath == "/manager"}'>is-active</c:if>" href="<c:url value='/manager'/>">Tổng quan</a>
      <a class="<c:if test='${fn:startsWith(currentPath, "/manager/categories")}'>is-active</c:if>" href="<c:url value='/manager/categories'/>">Quản lý danh mục</a>
      <a class="<c:if test='${fn:startsWith(currentPath, "/manager/products")}'>is-active</c:if>" href="<c:url value='/manager/products'/>">Quản lý món ăn</a>
      <a class="<c:if test='${fn:startsWith(currentPath, "/manager/coupons")}'>is-active</c:if>" href="<c:url value='/manager/coupons'/>">Quản lý mã giảm giá</a>
      <a class="<c:if test='${fn:startsWith(currentPath, "/manager/promotions")}'>is-active</c:if>" href="<c:url value='/manager/promotions'/>">Quản lý khuyến mãi</a>

      <span class="admin-nav__section">Thống kê</span>
      <a class="<c:if test='${fn:startsWith(currentPath, "/manager/statistics/revenue")}'>is-active</c:if>" href="<c:url value='/manager/statistics/revenue'/>">Thống kê doanh thu</a>
      <a class="<c:if test='${fn:startsWith(currentPath, "/manager/statistics/orders")}'>is-active</c:if>" href="<c:url value='/manager/statistics/orders'/>">Thống kê đơn hàng</a>
      <a class="<c:if test='${fn:startsWith(currentPath, "/manager/statistics/customers")}'>is-active</c:if>" href="<c:url value='/manager/statistics/customers'/>">Thống kê khách hàng</a>
    </nav>
  </div>
</aside>
