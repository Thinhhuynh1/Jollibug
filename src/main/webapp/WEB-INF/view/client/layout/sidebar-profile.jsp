<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<aside class="profile-sidebar">
    <div class="profile-sidebar__avatar">U</div>
    <div class="profile-sidebar__greeting">Xin chào,</div>
    <div class="profile-sidebar__name">User Demo!</div>
    <a class="profile-sidebar__logout" href="/">Đăng xuất</a>

    <nav class="profile-nav" aria-label="Profile navigation">
    <a href="/orders">Đơn hàng</a>
    <a href="/address">Địa chỉ của bạn</a>
    <a class="is-active" href="/profile">Chi tiết tài khoản</a>
    <a href="/reset-password">Đặt lại mật khẩu</a>
    </nav>
</aside>