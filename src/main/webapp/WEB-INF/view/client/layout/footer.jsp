<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<footer class="site-footer" id="site-footer">
  <div class="container">
    <div class="footer-grid">
      <div class="stack">
        <div class="brand">
          <span class="brand__mark"><img src="/images/jollibug.png" alt="JB Logo"></span>
          <span class="brand__copy">
            <span class="brand__title">Jollibug</span>
          </span>
        </div>
      </div>
      <div>
        <h3>Explore</h3>
        <div class="footer-links">
          <a href="/menu">Menu</a>
        </div>
      </div>
      <div>
        <h3>Tài khoản</h3>
        <div class="footer-links">
          <a href="/register">Đăng Kí</a>
          <a href="/login">Đăng Nhập</a>
        </div>
      </div>
      <div>
        <h3>Contact</h3>
        <div class="footer-links">
          <span>hello@Jollibug.vn</span>
        </div>
      </div>
    </div>
    <div class="footer-note">
      <span>&copy; <span data-current-year id="footer-year"></span> Jollibug.</span>
    </div>
  </div>
</footer>

<div class="toast-stack" data-toast-stack id="toast-stack"></div>
