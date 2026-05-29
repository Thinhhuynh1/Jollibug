<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Topbar -->
      <div class="admin-topbar">
        <h2>Jollibug Control Center</h2>

        <!-- Concurrency Demo Control -->
        <div class="demo-mode-control" style="margin-left: auto; margin-right: 1.5rem;" aria-label="Chế độ demo đồng thời">
          <button class="demo-mode-toggle" type="button" id="orderConcurrencyToggle" data-mode="SAFE">
            <span class="demo-mode-toggle__track" aria-hidden="true">
              <span class="demo-mode-toggle__knob"></span>
            </span>
            <strong id="orderConcurrencyLabel">SAFE</strong>
          </button>
        </div>

        <div class="admin-topbar__user">
          <div class="admin-avatar" id="topbar-user-initials" aria-hidden="true">ST</div>
          <div class="stack" style="gap:0.15rem;">
            <strong id="topbar-user-name">Duong vu</strong>
            <span class="muted" id="topbar-user-role">Nhân viên</span>
          </div>
          <button class="btn btn-outline" type="button" id="btn-logout" onclick="window.location.href = '/logout'">Đăng xuất</button>
        </div>
      </div>
<script src="<c:url value='/js/staff/order-concurrency-toggle.js'/>" defer></script>