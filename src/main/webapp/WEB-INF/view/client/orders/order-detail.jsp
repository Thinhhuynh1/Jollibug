<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Jollibug | Chi tiết đơn hàng</title>
    <meta name="description" content="Xem chi tiết đơn hàng, danh sách món đã đặt và đánh giá món ăn tại Jollibug.">

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
            <button onclick="history.back()" class="btn btn-outline order-back-btn" type="button">Quay lại</button>

            <div class="orders-header orders-header--detail">
                <div>
                    <span class="eyebrow">Theo dõi đơn hàng</span>
                    <h1 class="profile-title">Chi tiết đơn hàng</h1>
                    <p class="profile-subtitle">Kiểm tra thông tin đơn, món đã đặt và gửi đánh giá sau khi đơn được giao.</p>
                </div>
            </div>

            <div id="message" class="message" role="status" aria-live="polite"></div>

            <section class="orders-card order-detail-card">
                <div class="orders-card__header">
                    <h2>Thông tin đơn</h2>
                    <span class="orders-card__hint">Tóm tắt trạng thái và thanh toán</span>
                </div>
                <div id="orderInfo" class="order-info"></div>
            </section>

            <section class="orders-card">
                <div class="orders-card__header">
                    <h2>Danh sách món ăn</h2>
                    <span class="orders-card__hint">Có thể đánh giá từng món khi đơn đã giao</span>
                </div>

                <div class="orders-table-wrap">
                    <table class="order-table order-table--detail">
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
                </div>
            </section>
        </section>
    </div>
</main>

<div id="reviewModal" class="review-modal hidden">
    <div class="review-box" role="dialog" aria-modal="true" aria-labelledby="reviewModalTitle">
        <h2 id="reviewModalTitle">Đánh giá món ăn</h2>

        <input type="hidden" id="reviewMaMon">

        <label for="reviewSao">Số sao</label>
        <select id="reviewSao">
            <option value="5">5 sao - Rất hài lòng</option>
            <option value="4">4 sao - Hài lòng</option>
            <option value="3">3 sao - Bình thường</option>
            <option value="2">2 sao - Chưa hài lòng</option>
            <option value="1">1 sao - Không hài lòng</option>
        </select>

        <label for="reviewNoiDung">Nội dung đánh giá</label>
        <textarea id="reviewNoiDung" rows="4" placeholder="Nhập cảm nhận của bạn..."></textarea>

        <div class="modal-actions">
            <button onclick="submitReview()" class="primary" type="button">Gửi đánh giá</button>
            <button onclick="closeReviewModal()" class="danger" type="button">Đóng</button>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
<script src="<c:url value='/js/client/order-detail.js'/>"></script>
</body>
</html>
