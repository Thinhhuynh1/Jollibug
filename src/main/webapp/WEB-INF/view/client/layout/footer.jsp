<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<footer class="site-footer" id="site-footer">
  <div class="container">
    <div class="footer-grid">
      <div class="stack">
        <div class="brand">
          <span class="brand__mark"><img src="/images/jollibug.png" alt="Jollibug"></span>
          <span class="brand__copy">
            <span class="brand__title">Jollibug</span>
          </span>
        </div>
      </div>
      <div>
        <h3>Khám phá</h3>
        <div class="footer-links">
          <a href="/menu">Thực đơn</a>
        </div>
      </div>
      <div>
        <h3>Tài khoản</h3>
        <div class="footer-links">
          <a href="/register">Đăng ký</a>
          <a href="/login">Đăng nhập</a>
        </div>
      </div>
      <div>
        <h3>Liên hệ</h3>
        <div class="footer-links">
          <span>hello@jollibug.vn</span>
        </div>
      </div>
    </div>
    <div class="footer-note">
      <span>&copy; <span data-current-year id="footer-year"></span> Jollibug</span>
    </div>
  </div>
</footer>
