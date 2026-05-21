<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Jollibug | Quản lý đơn hàng</title>
    <meta name="description" content="Bảng điều phối đơn hàng dành cho nhân viên Jollibug.">

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/admin.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/staff/staff-orders.css'/>">
</head>
<body data-admin-role="staff" data-admin-page="orders">
<input type="hidden" id="currentStaffId" value="${sessionScope.userId}">
<div class="admin-shell admin-body">
    <jsp:include page="../layout/sidebar.jsp" />

    <main class="admin-main">
        <jsp:include page="../layout/topbar.jsp" />

        <section class="admin-panel staff-orders-board">
            <div class="staff-orders-board__header">
                <div class="stack" style="gap:0.35rem;">
                    <h1 class="section-title">Quản lý đơn hàng</h1>
                </div>
                <button onclick="loadStaffOrders()" class="primary-btn" type="button">Tải lại</button>
            </div>

            <section class="filter-card" aria-label="Bộ lọc đơn hàng">
                <div class="filter-grid">
                    <div class="form-group">
                        <label for="statusFilter">Trạng thái</label>
                        <select id="statusFilter">
                            <option value="">Tất cả</option>
                            <option value="PENDING">Chờ xác nhận</option>
                            <option value="CONFIRMED">Đã xác nhận</option>
                            <option value="SHIPPING">Đang giao</option>
                            <option value="DELIVERED">Đã giao</option>
                            <option value="CANCEL_REQUESTED">Yêu cầu hủy</option>
                            <option value="CANCELLED">Đã hủy</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="fromDateFilter">Từ ngày</label>
                        <input type="date" id="fromDateFilter">
                    </div>

                    <div class="form-group">
                        <label for="toDateFilter">Đến ngày</label>
                        <input type="date" id="toDateFilter">
                    </div>

                    <div class="form-group form-group--wide">
                        <label for="keywordFilter">Từ khóa</label>
                        <input type="text" id="keywordFilter" placeholder="Mã đơn, mã khách, ghi chú...">
                    </div>

                    <div class="filter-actions">
                        <button onclick="loadStaffOrders()" class="primary-btn" type="button">Lọc</button>
                        <button onclick="resetFilters()" class="secondary-btn" type="button">Xóa lọc</button>
                    </div>
                </div>
            </section>

            <div id="message" class="message" role="status" aria-live="polite"></div>

            <section class="table-card">
                <div class="table-card__header">
                    <h2>Danh sách đơn</h2>
                </div>

                <div class="table-wrap admin-table-wrap">
                    <table class="admin-table staff-order-table">
                        <thead>
                        <tr>
                            <th>Mã đơn</th>
                            <th>Mã khách</th>
                            <th>Ngày đặt</th>
                            <th>Thành tiền</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                        </thead>
                        <tbody id="staffOrderTableBody"></tbody>
                    </table>
                </div>
            </section>
        </section>
    </main>
</div>

<div id="statusModal" class="modal hidden">
    <div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="statusModalTitle">
        <div class="modal-header">
            <h2 id="statusModalTitle">Cập nhật trạng thái</h2>
            <button onclick="closeStatusModal()" class="close-btn" type="button" aria-label="Đóng">×</button>
        </div>

        <input type="hidden" id="selectedOrderId">

        <div class="status-form">
            <div class="form-group">
                <label for="staffIdInput">Mã nhân viên</label>
                <input type="number" id="staffIdInput" value="2">
            </div>

            <div class="form-group">
                <label for="nextStatusSelect">Trạng thái mới</label>
                <select id="nextStatusSelect"></select>
            </div>
        </div>

        <div class="modal-actions">
            <button onclick="submitUpdateStatus()" class="primary-btn" type="button">Cập nhật</button>
            <button onclick="closeStatusModal()" class="secondary-btn" type="button">Đóng</button>
        </div>
    </div>
</div>

<script src="<c:url value='/js/staff/staff-order-status-modal.js'/>"></script>
<script src="<c:url value='/js/staff/staff-orders.js'/>"></script>
</body>
</html>
