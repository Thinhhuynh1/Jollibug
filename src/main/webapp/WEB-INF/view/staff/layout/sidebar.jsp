<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!-- SECTION -->
<aside class="admin-sidebar">
  <div class="admin-sidebar__inner">
    <div class="admin-brand">
      <div class="brand">
        <span class="brand__mark">JB</span>
        <span class="brand__copy">
          <span class="brand__title">Nhân viên Jollibug</span>
          <span class="brand__tag">Operations Portal</span>
        </span>
      </div>
    </div>
    <nav class="admin-nav">
      <span class="admin-nav__section">Workspace</span>
      <a class="is-active" href="/staff">
        Quản lý đơn hàng
      </a>
      <a href="/staff/support">
        Chăm sóc khách hàng
      </a>
      <a href="/staff/clients">
        Quản lý khách hàng
      </a>
      
    </nav>
  </div>
</aside>