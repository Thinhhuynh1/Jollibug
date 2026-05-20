
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Chi Tiết Khách Hàng</title>
  <meta name="description" content="Quản lý thông tin chi tiết khách hàng." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
  <!-- Import file CSS riêng cho phần Manager như yêu cầu -->
  <link rel="stylesheet" href="<c:url value='/css/manager.css'/>" />
</head>

<body data-admin-role="admin" data-admin-page="manage-users">

  <div class="admin-shell admin-body" data-admin-table-root>

    <jsp:include page="../layout/sidebar.jsp" />

    <main class="admin-main">

      <jsp:include page="../layout/topbar.jsp" />

      <section class="profile-content">
        

        <!-- 1. Thông tin cá nhân & Liên hệ -->
         <h1>Mã khách hàng: #JB001</h1>
        <section class="order-detail__section">
          <h2 class="order-detail__title">Thông tin cá nhân & Liên hệ</h2>
          <div class="order-detail__info-grid">
            <div class="order-detail__info-card">
              <span class="order-detail__label">Họ và tên</span>
              <strong>Nguyễn Minh Quân</strong>
            </div>
            <div class="order-detail__info-card">
              <span class="order-detail__label">Email</span>
              <strong>quan.nguyen@jollibug.vn</strong>
            </div>
            <div class="order-detail__info-card">
              <span class="order-detail__label">Số điện thoại</span>
              <strong>0901234567</strong>
            </div>
            <div class="order-detail__info-card">
              <span class="order-detail__label">Ngày sinh</span>
              <strong>25/08/1995</strong>
            </div>
            <div class="order-detail__info-card order-detail__info-card--full">
              <span class="order-detail__label">Địa chỉ mặc định</span>
              <strong>123 Nguyễn Trãi, Phường Bến Thành, Quận 1, TP. Hồ Chí Minh</strong>
            </div>
          </div>
        </section>
        
        <br>

        <!-- 2. Thống kê lịch sử đơn hàng -->
        <section class="order-detail__section">
          <h2 class="order-detail__title">Tổng quan mua sắm</h2>
          <div class="client-metrics-row">
            <article class="metric-card">
              <span class="muted">Tổng số đơn hàng</span>
              <strong>12</strong>
              <span class="metric-delta" data-tone="info">Từ lúc gia nhập</span>
            </article>
            <article class="metric-card">
              <span class="muted">Đơn hoàn thành</span>
              <strong>10</strong>
              <span class="metric-delta" data-tone="up">Thành công</span>
            </article>
            <article class="metric-card">
              <span class="muted">Tổng chi tiêu</span>
              <strong>1,250,000đ</strong>
              <span class="metric-delta" data-tone="warm">Khách hàng hạng vàng</span>
            </article>
          </div>
        </section>

        <br>

        <!-- 3. Lịch sử đơn hàng (Table) -->
        <section class="order-detail__section">
          <h2 class="order-detail__title">Lịch sử đơn hàng gần đây</h2>
          <div class="orders-table-wrap">
            <table class="orders-table" style="text-align: center;">
              <thead>
                <tr>
                  <th style="text-align: center;">Mã đơn</th>
                  <th style="text-align: center;">Ngày đặt</th>
                  <th style="text-align: center;">Sản phẩm (tóm tắt)</th>
                  <th style="text-align: center;">Tổng tiền</th>
                  <th style="text-align: center;">Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                <%-- JSTL Loop here --%>
                <tr>
                  <td><strong>#DH001</strong></td>
                  <td class="muted">12/01/2026</td>
                  <td>Combo Gà Rán 2 Miếng...</td>
                  <td style="font-weight:700;">242,000đ</td>
                  <td><span class="status-badge" data-status="delivered">Đã giao</span></td>
                </tr>
                <tr>
                  <td><strong>#DH005</strong></td>
                  <td class="muted">25/01/2026</td>
                  <td>Trà Đào Cam Sả x2</td>
                  <td style="font-weight:700;">70,000đ</td>
                  <td><span class="status-badge" data-status="delivered">Đã giao</span></td>
                </tr>
                <tr>
                  <td><strong>#DH009</strong></td>
                  <td class="muted">10/02/2026</td>
                  <td>Burger Bò Phô Mai</td>
                  <td style="font-weight:700;">55,000đ</td>
                  <td><span class="status-badge" data-status="cancelled" style="background:rgba(239, 68, 68, 0.15); color:#ef4444; border:1px solid rgba(239, 68, 68, 0.3);">Đã hủy</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <br>

        <!-- 4. Thông tin thanh toán -->
        <section class="order-detail__section">
          <h2 class="order-detail__title">Thông tin thanh toán</h2>
          <div class="client-metrics-row" style="grid-template-columns: repeat(2, 1fr);">
            <div class="client-payment-card">
              <div class="client-payment-card__icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24" aria-hidden="true">
                  <rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/>
                </svg>
              </div>
              <div class="client-payment-card__info">
                <strong>Thẻ tín dụng (Mặc định)</strong>
                <span>Visa **** 1234 • Hết hạn 12/28</span>
              </div>
            </div>
            <div class="client-payment-card">
              <div class="client-payment-card__icon" style="background:var(--color-ink-900);">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24" aria-hidden="true">
                  <rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
                </svg>
              </div>
              <div class="client-payment-card__info">
                <strong>Ví điện tử ZaloPay</strong>
                <span>Liên kết qua SĐT: 090****567</span>
              </div>
            </div>
          </div>
        </section>

        <div style="text-align: right; margin-top: 3rem;">
          <a href="/staff/clients" class="btn btn-secondary">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16" aria-hidden="true" style="margin-right: 0.3rem;">
              <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
            </svg>
            Quay lại danh sách
          </a>
        </div>
      </section>

    </main>
  </div><!-- /data-admin-table-root -->

</body>
</html>

