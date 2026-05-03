<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Topbar -->
      <div class="admin-topbar">
        <h2>Jollibug Control Center</h2>
        <div class="admin-topbar__user">
          <div class="admin-avatar" id="topbar-user-initials" aria-hidden="true">ST</div>
          <div class="stack" style="gap:0.15rem;">
            <strong id="topbar-user-name">Duong vu</strong>
            <span class="muted" id="topbar-user-role">Nhân viên</span>
          </div>
          <button class="btn btn-outline" type="button" id="btn-logout">Đăng xuất</button>
        </div>
      </div>