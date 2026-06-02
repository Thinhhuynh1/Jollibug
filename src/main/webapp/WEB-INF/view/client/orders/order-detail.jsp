<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Jollibug | Chi tiết đơn hàng</title>
    <meta
      name="description"
      content="Xem chi tiết đơn hàng, danh sách món đã đặt và đánh giá món ăn tại Jollibug."
    />

    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link
      href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap"
      rel="stylesheet"
    />
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
    <link rel="stylesheet" href="<c:url value='/css/client/profile.css'/>" />
    <link rel="stylesheet" href="<c:url value='/css/client/order.css'/>" />
  </head>
  <body data-page="orders">
    <jsp:include page="../layout/header.jsp" />

    <main class="profile-page order-page">
      <div class="container container--account-wide">
        <section class="profile-content orders-content">
          <input
            type="hidden"
            id="currentMaKH"
            value="${not empty sessionScope.user ? sessionScope.user.maTK : sessionScope.userId}"
          />

          <div class="client-order-top-actions">
            <a
              class="btn btn-outline order-back-btn"
              href="<c:url value='/orders'/>"
            >
              ← Quay lại lịch sử
            </a>

            <div
              id="orderDetailActions"
              class="client-order-main-actions"
            ></div>
          </div>

          <div
            class="orders-header orders-header--detail client-order-detail-header"
          >
            <div>
              <h1 class="profile-title" id="orderDetailTitle">
                Chi tiết đơn hàng
              </h1>
            </div>
          </div>

          <div
            id="message"
            class="message"
            role="status"
            aria-live="polite"
          ></div>

          <section
            class="orders-card order-timeline-card"
            aria-label="Tiến trình trạng thái đơn hàng"
          >
            <div id="orderTimeline" class="order-timeline"></div>
            <div
              id="orderTimelineCancelInfo"
              class="order-timeline-cancel-info hidden"
            ></div>
          </section>

          <section
            class="orders-card order-detail-card client-order-detail-card"
          >
            <div
              id="orderDetailContent"
              class="client-order-detail-content"
            ></div>
          </section>

          <section class="orders-card">
            <div class="orders-card__header">
              <h2 id="orderItemsTitle">Danh sách món ăn</h2>
              <span class="orders-card__hint"
                >Có thể đánh giá từng món khi đơn đã giao</span
              >
            </div>

            <div
              class="order-tabs order-review-tabs"
              role="tablist"
              aria-label="Bộ lọc đánh giá"
            >
              <button
                class="order-tab is-active"
                type="button"
                data-review-tab="pending"
                onclick="switchReviewTab('pending')"
              >
                Đánh giá
              </button>
              <button
                class="order-tab"
                type="button"
                data-review-tab="done"
                onclick="switchReviewTab('done')"
              >
                Đã đánh giá
              </button>
            </div>

            <div class="orders-table-wrap">
              <table class="order-table order-table--detail">
                <thead id="orderItemHead"></thead>
                <tbody id="orderItemBody"></tbody>
              </table>
            </div>
          </section>
        </section>
      </div>
    </main>

    <div id="reviewModal" class="review-modal hidden">
      <div
        class="review-box"
        role="dialog"
        aria-modal="true"
        aria-labelledby="reviewModalTitle"
      >
        <h2 id="reviewModalTitle">Đánh giá món ăn</h2>

        <input type="hidden" id="reviewMaMon" />
        <input type="hidden" id="reviewSao" value="" />

        <label>Chọn sao</label>
        <div class="review-rating-row">
          <div
            id="reviewStarRating"
            class="review-star-rating"
            role="radiogroup"
            aria-label="Chọn số sao đánh giá"
          >
            <button
              type="button"
              class="review-star"
              data-rating="1"
              aria-label="1 sao"
            >
              ★
            </button>
            <button
              type="button"
              class="review-star"
              data-rating="2"
              aria-label="2 sao"
            >
              ★
            </button>
            <button
              type="button"
              class="review-star"
              data-rating="3"
              aria-label="3 sao"
            >
              ★
            </button>
            <button
              type="button"
              class="review-star"
              data-rating="4"
              aria-label="4 sao"
            >
              ★
            </button>
            <button
              type="button"
              class="review-star"
              data-rating="5"
              aria-label="5 sao"
            >
              ★
            </button>
          </div>
          <span id="reviewRatingText" class="review-rating-text"
            >Chọn mức đánh giá</span
          >
        </div>

        <label for="reviewNoiDung">Nội dung đánh giá</label>
        <textarea
          id="reviewNoiDung"
          rows="4"
          placeholder="Nhập cảm nhận của bạn..."
        ></textarea>

        <label for="reviewImageInput">Ảnh đánh giá</label>
        <input
          id="reviewImageInput"
          class="review-image-input"
          type="file"
          accept="image/*"
          onchange="previewReviewImage(event)"
        />
        <div id="reviewImagePreview" class="review-image-preview hidden"></div>

        <div class="modal-actions">
          <button onclick="submitReview()" class="primary" type="button">
            Gửi đánh giá
          </button>
          <button onclick="closeReviewModal()" class="danger" type="button">
            Đóng
          </button>
        </div>
      </div>
    </div>

    <jsp:include page="../layout/footer.jsp" />
    <script src="<c:url value='/js/client/order-status.js'/>"></script>
    <script src="<c:url value='/js/client/order-detail.js'/>"></script>
  </body>
</html>
