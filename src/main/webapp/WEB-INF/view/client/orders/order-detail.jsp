<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết đơn hàng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/client/order.css">
</head>
<body>
<div class="page">
    <button onclick="history.back()" class="back-btn">← Quay lại</button>

    <h1>Chi tiết đơn hàng</h1>

    <div id="message" class="message"></div>

    <section class="card">
        <h2>Thông tin đơn</h2>
        <div id="orderInfo" class="order-info"></div>
    </section>

    <section class="card">
        <h2>Danh sách món ăn</h2>

        <table class="order-table">
            <thead>
            <tr>
                <th>Món ăn</th>
                <th>Số lượng</th>
                <th>Đơn giá</th>
                <th>Thành tiền</th>
                <th>Đánh giá</th>
            </tr>
            </thead>
            <tbody id="orderItemBody"></tbody>
        </table>
    </section>
</div>

<div id="reviewModal" class="review-modal hidden">
    <div class="review-box">
        <h2>Đánh giá món ăn</h2>

        <input type="hidden" id="reviewMaMon">

        <label>Số sao</label>
        <select id="reviewSao">
            <option value="5">5 sao - Rất hài lòng</option>
            <option value="4">4 sao - Hài lòng</option>
            <option value="3">3 sao - Bình thường</option>
            <option value="2">2 sao - Chưa hài lòng</option>
            <option value="1">1 sao - Không hài lòng</option>
        </select>

        <label>Nội dung đánh giá</label>
        <textarea id="reviewNoiDung" rows="4" placeholder="Nhập cảm nhận của bạn..."></textarea>

        <div class="modal-actions">
            <button onclick="submitReview()" class="primary">Gửi đánh giá</button>
            <button onclick="closeReviewModal()" class="danger">Đóng</button>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/client/order-detail.js"></script>
</body>
</html>