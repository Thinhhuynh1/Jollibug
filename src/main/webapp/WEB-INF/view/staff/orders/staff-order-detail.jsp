<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Jollibug | Chi tiết đơn hàng</title>

    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link
      href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap"
      rel="stylesheet"
    />
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
    <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
    <link
      rel="stylesheet"
      href="<c:url value='/css/staff/staff-orders.css'/>"
    />
  </head>
  <body data-admin-role="staff" data-admin-page="orders">
    <div class="admin-shell admin-body">
      <jsp:include page="../layout/sidebar.jsp" />

      <main class="admin-main">
        <jsp:include page="../layout/topbar.jsp" />

        <section
          class="admin-panel staff-orders-board staff-order-detail-board"
        >
          <input
            type="hidden"
            id="currentStaffId"
            value="${sessionScope.userId}"
          />

          <div class="staff-detail-top-actions">
            <a
              href="${pageContext.request.contextPath}/staff/orders"
              class="secondary-btn back-btn"
            >
              ← Quay lại danh sách
            </a>
          </div>

          <div class="staff-orders-board__header">
            <div class="stack" style="gap: 0.35rem">
              <h1 class="section-title" id="detailPageTitle">
                Chi tiết đơn hàng
              </h1>
            </div>

            <button
              type="button"
              id="detailUpdateStatusBtn"
              class="primary-btn"
            >
              Cập nhật
            </button>
          </div>

          <div
            id="message"
            class="message"
            role="status"
            aria-live="polite"
          ></div>

          <section class="table-card detail-card">
            <div id="orderDetailContent"></div>
          </section>

          <section class="table-card">
            <div class="table-card__header">
              <h2>Danh sách món</h2>
            </div>

            <div class="table-wrap admin-table-wrap">
              <table
                class="admin-table staff-order-table staff-order-table--items"
              >
                <thead>
                  <tr>
                    <th>Món ăn</th>
                    <th>Số lượng</th>
                    <th>Đơn giá</th>
                    <th>Thành tiền</th>
                  </tr>
                </thead>
                <tbody id="staffOrderItemBody"></tbody>
              </table>
            </div>
          </section>
        </section>
      </main>
    </div>

    <script src="<c:url value='/js/staff/staff-order-status-modal.js'/>"></script>
    <script src="<c:url value='/js/staff/staff-order-detail.js'/>"></script>
  </body>
</html>
