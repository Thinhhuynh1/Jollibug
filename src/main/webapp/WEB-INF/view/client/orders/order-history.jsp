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
                    <h1 class="profile-title">Lịch sử mua hàng</h1>
                </div>

                <a class="btn btn-outline orders-header__action" href="<c:url value='/menu'/>">
                    Tiếp tục đặt món
                </a>
            </div>

            <input type="hidden" id="currentCustomerId" value="${sessionScope.userId}">

            <div class="order-tabs" role="tablist" aria-label="Order tabs">
                <button class="order-tab is-active" type="button" data-tab="active" onclick="switchOrderTab('active')">
                    Đơn đang đến
                </button>
                <button class="order-tab" type="button" data-tab="history" onclick="switchOrderTab('history')">
                    Lịch sử
                </button>
                <button class="order-tab" type="button" data-tab="review" onclick="switchOrderTab('review')">
                    Đánh giá
                </button>
            </div>

            <div id="message" class="message" role="status" aria-live="polite"></div>

            <section class="orders-card order-card-section">
                <div class="orders-card__header">
                    <h2 id="orderSectionTitle">Đơn đang đến</h2>
                </div>

                <div id="orderCardList" class="order-card-list"></div>
            </section>
        </section>
    </div>
</main>

<jsp:include page="../layout/footer.jsp" />
<script src="<c:url value='/js/client/order-history.js'/>"></script>
</body>
</html>
