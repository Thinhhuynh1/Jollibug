<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lịch sử mua hàng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/client/order.css">
</head>
<body>
<div class="page">
    <h1>Lịch sử mua hàng</h1>

    <div class="toolbar">
        <label for="customerIdInput">Mã khách hàng:</label>
        <input id="customerIdInput" type="number" value="1">
        <button onclick="loadOrders()">Tải đơn hàng</button>
    </div>

    <div id="message" class="message"></div>

    <table class="order-table">
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

<script src="${pageContext.request.contextPath}/resources/js/client/order-history.js"></script>
</body>
</html>