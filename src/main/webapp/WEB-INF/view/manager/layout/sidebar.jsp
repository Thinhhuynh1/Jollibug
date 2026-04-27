<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<aside class="admin-sidebar">
    <div class="admin-sidebar__inner">
    <div class="admin-brand">
        <div class="brand">
        <span class="brand__mark">JB</span>
        <span class="brand__copy">
            <span class="brand__title">Quản lý Jollibug</span>
            <span class="brand__tag">Control Center</span>
        </span>
        </div>
    </div>

    <nav class="admin-nav">
        <span class="admin-nav__section">Workspace</span>
        <a class="is-active" href="<c:url value='/manager'/>">Dashboard</a>
        <a href="<c:url value='/manager/categories'/>">Quản lý danh mục</a>
        <a href="<c:url value='/manager/products'/>">Quản lý món ăn</a>
        <a href="<c:url value='/manager/coupons'/>">Quản lý mã giảm giá</a>
        <a href="<c:url value='/manager/promotions'/>">Quản lý khuyến mãi</a>
        <a href="<c:url value='/manager/flash-sales'/>">Flash Sale</a>
    </nav>
    </div>
</aside>