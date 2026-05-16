<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Chi tiết khách hàng</title>
  <meta name="description" content="Quản lý thông tin chi tiết khách hàng." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/manager.css'/>" />
</head>

<body data-admin-role="admin" data-admin-page="manage-users">
  <div class="admin-shell admin-body" data-admin-table-root>
    <jsp:include page="../layout/sidebar.jsp" />

    <main class="admin-main">
      <jsp:include page="../layout/topbar.jsp" />

      <section class="profile-content">
        <h1>Mã khách hàng: #${client.maTK}</h1>

        <section class="order-detail__section">
          <h2 class="order-detail__title">Thông tin cá nhân & Liên hệ</h2>
          <div class="order-detail__info-grid">
            <div class="order-detail__info-card">
              <span class="order-detail__label">Họ và tên</span>
              <strong>${client.hoTen}</strong>
            </div>
            <div class="order-detail__info-card">
              <span class="order-detail__label">Email</span>
              <strong>${client.email}</strong>
            </div>
            <div class="order-detail__info-card">
              <span class="order-detail__label">Số điện thoại</span>
              <strong>${empty client.sdt ? '-' : client.sdt}</strong>
            </div>
            <div class="order-detail__info-card">
              <span class="order-detail__label">Ngày tham gia</span>
              <strong>${client.createdAtDisplay}</strong>
            </div>
            <div class="order-detail__info-card order-detail__info-card--full">
              <span class="order-detail__label">Địa chỉ mặc định</span>
              <strong>
                <c:choose>
                  <c:when test="${defaultAddress != null}">
                    ${defaultAddress.diaChiCuThe}
                    <c:if test="${not empty defaultAddress.phuongXa}">, ${defaultAddress.phuongXa}</c:if>
                    <c:if test="${not empty defaultAddress.quanHuyen}">, ${defaultAddress.quanHuyen}</c:if>
                    <c:if test="${not empty defaultAddress.tinhThanh}">, ${defaultAddress.tinhThanh}</c:if>
                  </c:when>
                  <c:otherwise>-</c:otherwise>
                </c:choose>
              </strong>
            </div>
          </div>
        </section>

        <br>

        <section class="order-detail__section">
          <h2 class="order-detail__title">Tổng quan mua sắm</h2>
          <div class="client-metrics-row">
            <article class="metric-card">
              <span class="muted">Tổng số đơn hàng</span>
              <strong>${totalOrders}</strong>
              <span class="metric-delta" data-tone="info">Từ lúc gia nhập</span>
            </article>
            <article class="metric-card">
              <span class="muted">Đơn hoàn thành</span>
              <strong>${completedOrders}</strong>
              <span class="metric-delta" data-tone="up">Đã giao thành công</span>
            </article>
            <article class="metric-card">
              <span class="muted">Tổng chi tiêu</span>
              <strong><fmt:formatNumber value="${totalSpent}" pattern="#,##0" />đ</strong>
              <span class="metric-delta" data-tone="warm">Theo dữ liệu đơn hàng</span>
            </article>
          </div>
        </section>

        <br>

        <section class="order-detail__section">
          <h2 class="order-detail__title">Lịch sử đơn hàng gần đây</h2>
          <div class="orders-table-wrap">
            <table class="orders-table" style="text-align: center;">
              <thead>
                <tr>
                  <th style="text-align: center;">Mã đơn</th>
                  <th style="text-align: center;">Ngày đặt</th>
                  <th style="text-align: center;">Ghi chú</th>
                  <th style="text-align: center;">Tổng tiền</th>
                  <th style="text-align: center;">Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                <c:choose>
                  <c:when test="${not empty recentOrders}">
                    <c:forEach var="order" items="${recentOrders}">
                      <tr>
                        <td><strong>#${order.maDH}</strong></td>
                        <td class="muted"><fmt:formatDate value="${order.ngayDat}" pattern="dd/MM/yyyy HH:mm" /></td>
                        <td>${empty order.ghiChu ? '-' : order.ghiChu}</td>
                        <td style="font-weight:700;"><fmt:formatNumber value="${order.thanhTien}" pattern="#,##0" />đ</td>
                        <td><span class="status-badge">${order.trangThaiDon}</span></td>
                      </tr>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <tr>
                      <td colspan="5" style="text-align:center;">Khách hàng này chưa có đơn hàng nào.</td>
                    </tr>
                  </c:otherwise>
                </c:choose>
              </tbody>
            </table>
          </div>
        </section>

        <br>

        <section class="order-detail__section">
          <h2 class="order-detail__title">Thông tin thanh toán</h2>
          <div class="client-metrics-row" style="grid-template-columns: repeat(2, 1fr);">
            <c:choose>
              <c:when test="${not empty paymentSummary}">
                <c:forEach var="paymentMethod" items="${paymentSummary}">
                  <div class="client-payment-card">
                    <div class="client-payment-card__icon">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24" aria-hidden="true">
                        <rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/>
                      </svg>
                    </div>
                    <div class="client-payment-card__info">
                      <strong>${paymentMethod}</strong>
                      <span>Phương thức đã từng sử dụng</span>
                    </div>
                  </div>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <div class="client-payment-card">
                  <div class="client-payment-card__icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24" aria-hidden="true">
                      <rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/>
                    </svg>
                  </div>
                  <div class="client-payment-card__info">
                    <strong>Chưa có dữ liệu</strong>
                    <span>Khách hàng chưa phát sinh thanh toán</span>
                  </div>
                </div>
              </c:otherwise>
            </c:choose>
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
  </div>
</body>
</html>
