<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
        <a class="is-active" href="/">Trang chủ</a>
        <a href="/menu">Thực đơn</a>
        <a href="/about">Giới thiệu</a>
        <a href="/contact">Liên hệ</a>
      </nav>

      <div class="header-actions">
        
        <a href="/cart" class="btn btn-primary" id="btn-open-cart">
  Giỏ hàng: <span id="header-cart-count">0</span>
        </a>
        </button>
        <a class="btn btn-outline" href="/login">Đăng nhập</a>
        <a class="btn btn-ghost" href="/profile">Tài khoản</a>
      </div>
    </div>

    <!-- Mobile navigation panel -->
    <div class="container mobile-nav" data-mobile-nav id="mobile-nav">
      <div class="mobile-nav__panel">
        <a class="is-active" href="/">Trang chủ</a>
        <a href="/menu">Thực đơn</a>
        <a href="/about">Giới thiệu</a>
        <a href="/contact">Liên hệ</a>
        <a href="/profile">Tài khoản</a>
        <a href="/login">Đăng nhập</a>
        <a href="/cart">Giỏ hàng</a>
      </div>
    </div>
  </header>