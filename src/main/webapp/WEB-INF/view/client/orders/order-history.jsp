<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Jollibug | Lịch sử mua hàng</title>
    <meta name="description" content="Theo dõi lịch sử đơn hàng và trạng thái giao món của bạn tại Jollibug.">

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/client/profile.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/client/order.css'/>">
</head>
<body data-page="orders">
<jsp:include page="../layout/header.jsp" />

<main class="profile-page order-page">
    <div class="container container--account-wide">
        <section class="profile-content orders-content">
            <div class="orders-header">
                <div>
                    <span class="eyebrow">Đơn hàng của tôi</span>
                    <h1 class="profile-title">Lịch sử mua hàng</h1>
                    <p class="profile-subtitle">Theo dõi các đơn đã đặt, trạng thái giao hàng và thao tác với đơn khi cần.</p>
                </div>
                <a class="btn btn-outline orders-header__action" href="<c:url value='/menu'/>">Tiếp tục đặt món</a>
            </div>

            <div class="order-toolbar">
                <label class="order-field" for="customerIdInput">
                    <span>Mã khách hàng</span>
                    <input id="customerIdInput" type="number" value="1">
                </label>
                <button class="btn btn-primary order-toolbar__submit" type="button" onclick="loadOrders()">Tải đơn hàng</button>
            </div>

            <div id="message" class="message" role="status" aria-live="polite"></div>

            <section class="orders-card">
                <div class="orders-card__header">
                    <h2>Danh sách đơn hàng</h2>
                    <span class="orders-card__hint">Cập nhật theo dữ liệu mới nhất từ hệ thống</span>
                </div>

                <div class="orders-table-wrap">
                    <table class="order-table order-table--history">
                        <thead>
                        <tr>
                            <th>Mã đơn</th>
                            <th>Ngày đặt</th>
                            <th>Tổng tiền món</th>
                            <th>Giảm giá</th>
                            <th>Thành tiền</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                        </thead>
                        <tbody id="orderTableBody"></tbody>
                    </table>
                </div>
            </section>
        </section>
    </div>
</main>

<jsp:include page="../layout/footer.jsp" />
<script src="<c:url value='/js/client/order-history.js'/>"></script>
</body>
</html>
