<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Staff - Chăm sóc khách hàng</title>
  <meta name="description" content="Jollibug Staff portal - chat hỗ trợ, xử lý khiếu nại và phản hồi đánh giá." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/admin.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/staff.css?v=2'/>" />
</head>

<body data-admin-role="staff" data-admin-page="support">

  <div class="admin-shell admin-body" data-admin-table-root>

    <jsp:include page="layout/sidebar.jsp" />

    <main class="admin-main">

      <jsp:include page="layout/topbar.jsp" />

      <c:set var="supportTab" value="${empty param.tab ? 'chat' : param.tab}" />

      <section class="admin-panel">
        <div class="panel-header">
          <div class="header-content">
            <div class="header-title-group">
              <h1 class="section-title">Chăm sóc khách hàng</h1>
              <p class="section-subtitle">Quản lý đánh giá từ khách hàng</p>
            </div>
          </div>
          <div class="panel-controls support-toolbar">
            <div class="tab-buttons">
              <a href="<c:url value='/staff/support?tab=chat'/>" class="tab-btn tab-btn--outline<c:if test='${supportTab == "chat"}'> is-active</c:if>" role="tab" aria-selected="${supportTab == 'chat'}" aria-controls="tab-chat">
                Chat
              </a>
              <a href="<c:url value='/staff/support?tab=review'/>" class="tab-btn tab-btn--solid<c:if test='${supportTab == "review"}'> is-active</c:if>" role="tab" aria-selected="${supportTab == 'review'}" aria-controls="tab-review">
                Đánh giá
              </a>
            </div>
          </div>
        </div>

        <div id="tab-chat" class="support-tab-panel<c:if test='${supportTab == "chat"}'> is-active</c:if>" <c:if test='${supportTab != "chat"}'>hidden</c:if>>
          <div class="support-shell">
            <aside class="ticket-list-panel">
              <div class="ticket-list-panel__header">
                <strong>Hội thoại đang mở</strong>
                <span class="status-badge" data-status="active">${onlineCount} online</span>
              </div>
              <div class="ticket-list">
                <c:choose>
                  <c:when test="${empty tickets}">
                    <p style="padding:1rem;color:var(--color-text-muted)">Chưa có hội thoại nào.</p>
                  </c:when>
                  <c:otherwise>
                    <c:forEach var="ticket" items="${tickets}">
                      <a href="<c:url value='/staff/support?maYC=${ticket.maYC}'/>" style="text-decoration:none;color:inherit">
                        <article class="ticket-item<c:if test='${ticket.maYC == activeConvId}'> is-active</c:if>">
                          <div class="ticket-item__header">
                            <span class="ticket-item__name"><c:out value="${ticket.khachHang.hoTen}"/></span>
                            <span class="ticket-item__time">${ticket.createdAtDisplay}</span>
                          </div>
                          <p class="ticket-item__preview"><c:out value="${ticket.tieuDe}"/></p>
                          <span class="status-badge" style="font-size:.7rem;margin-top:.25rem;display:inline-block"
                                data-status="${ticket.trangThai == 'PENDING' ? 'pending' : ticket.trangThai == 'PROCESSING' ? 'active' : 'done'}">
                            <c:out value="${ticket.trangThai}"/>
                          </span>
                        </article>
                      </a>
                    </c:forEach>
                  </c:otherwise>
                </c:choose>
              </div>
            </aside>

            <section class="chat-workspace">
              <article class="chat-panel"
                       data-chat-root
                       data-chat-variant="staff"
                       data-chat-vaitrogui="NhanVien"
                       data-chat-ma-tk-gui="${sessionScope.user.maTK}"
                       data-chat-ten="${sessionScope.user.hoTen}"
                       data-chat-mayc="${activeConvId}">
                <header class="chat-panel__header">
                  <div class="chat-panel__avatar">
                    <c:choose>
                      <c:when test="${not empty activeClientName}">
                        ${fn:substring(activeClientName, 0, 2)}
                      </c:when>
                      <c:otherwise>--</c:otherwise>
                    </c:choose>
                  </div>
                  <div class="chat-panel__meta">
                    <strong><c:out value="${not empty activeClientName ? activeClientName : 'Chọn hội thoại'}"/></strong>
                    <c:choose>
                      <c:when test="${not empty activeYC}">
                        <span>Yêu cầu #${activeYC.maYC} – <c:out value="${activeYC.trangThai}"/></span>
                      </c:when>
                      <c:otherwise><span>Đang hoạt động</span></c:otherwise>
                    </c:choose>
                  </div>
                  <c:if test="${not empty activeYC and activeYC.trangThai != 'DONE'}">
                    <a href="/api/chat/close?maYC=${activeYC.maYC}"
                       class="btn btn-ghost" style="margin-left:auto;font-size:.8rem"
                       onclick="return confirm('Đánh dấu hoàn thành yêu cầu này?')">✓ Hoàn thành</a>
                  </c:if>
                </header>

                <div class="chat-messages" data-chat-messages>
                  <c:forEach var="msg" items="${chatHistory}">
                    <c:choose>
                      <c:when test="${msg.vaiTroGui == 'Khach'}">
                        <div class="chat-bubble chat-bubble--client">
                          <div class="chat-bubble__avatar">
                            ${fn:substring(activeClientName, 0, 2)}
                          </div>
                          <div class="chat-bubble__body">
                            <div class="chat-bubble__text"><c:out value="${msg.noiDung}"/></div>
                            <span class="chat-bubble__time">${msg.timeDisplay}</span>
                          </div>
                        </div>
                      </c:when>
                      <c:otherwise>
                        <div class="chat-bubble chat-bubble--staff">
                          <div class="chat-bubble__avatar">NV</div>
                          <div class="chat-bubble__body">
                            <div class="chat-bubble__text"><c:out value="${msg.noiDung}"/></div>
                            <span class="chat-bubble__time">${msg.timeDisplay}</span>
                          </div>
                        </div>
                      </c:otherwise>
                    </c:choose>
                  </c:forEach>
                  <c:if test="${empty chatHistory and not empty activeConvId}">
                    <p style="padding:1rem;color:var(--color-text-muted);text-align:center">Chưa có tin nhắn nào.</p>
                  </c:if>
                </div>

                <form class="chat-input-bar" data-chat-form>
                  <input id="chat-input" data-chat-input name="message" type="text"
                         placeholder="Nhập phản hồi cho khách hàng..."
                         <c:if test="${empty activeConvId or activeYC.trangThai == 'DONE'}">disabled</c:if>/>
                  <button class="chat-send-btn" type="submit" aria-label="Gửi tin nhắn"
                          <c:if test="${empty activeConvId or activeYC.trangThai == 'DONE'}">disabled</c:if>>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="m22 2-7 20-4-9-9-4z" />
                      <path d="M22 2 11 13" />
                    </svg>
                  </button>
                </form>
              </article>
            </section>
          </div>
        </div>

        <div id="tab-review" class="support-tab-panel<c:if test='${supportTab == "review"}'> is-active</c:if>" <c:if test='${supportTab != "review"}'>hidden</c:if>>
          <div class="review-panel">
            <!-- Review 1: Low Score (2 stars) -->
            <article class="review-card review-card--low-score">
              <div class="review-header-row">
                <div class="review-header-left">
                  <h3 class="review-id">#DG001</h3>
                  <div class="customer-info">
                    <p class="customer-name">Lê Hoàng Phúc</p>
                    <p class="customer-phone">0912 345 678</p>
                  </div>
                </div>
                <div class="review-timestamp">28/05/2024 14:35</div>
              </div>

              <div class="review-stars">
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="star empty">★</span>
                <span class="star empty">★</span>
                <span class="star empty">★</span>
                <span class="rating-badge">2/5 Tệ</span>
              </div>

              <div class="review-metadata">
                <p><strong>Món:</strong> Cơm tấm sườn nướng</p>
              </div>

              <div class="review-text-box">
                <p>Giao hàng muộn hơn dự kiến 30 phút. Cơm bị lạnh, sườn cũng không còn mềm như vừa nướng xong. Rất không hài lòng với chất lượng lần này.</p>
              </div>
            </article>

            <!-- Review 2: Medium Score (3 stars) -->
            <article class="review-card review-card--medium-score">
              <div class="review-header-row">
                <div class="review-header-left">
                  <h3 class="review-id">#DG002</h3>
                  <div class="customer-info">
                    <p class="customer-name">Trần Thu Hà</p>
                    <p class="customer-phone">0987 654 321</p>
                  </div>
                </div>
                <div class="review-timestamp">27/05/2024 18:20</div>
              </div>

              <div class="review-stars">
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="star empty">★</span>
                <span class="star empty">★</span>
                <span class="rating-badge">3/5 Trung bình</span>
              </div>

              <div class="review-metadata">
                <p><strong>Món:</strong> Gà rán combo 5 miếng</p>
              </div>

              <div class="review-text-box">
                <p>Gà rán tươi, giòn ngon. Nhưng phần mì ốc quên trong combo bị mềm và ẩm, không còn độ giòn. Tuy nhiên vẫn có thể ăn được.</p>
              </div>
            </article>

            <!-- Review 3: Low Score (1 star) -->
            <article class="review-card review-card--low-score">
              <div class="review-header-row">
                <div class="review-header-left">
                  <h3 class="review-id">#DG003</h3>
                  <div class="customer-info">
                    <p class="customer-name">Nguyễn Văn A</p>
                    <p class="customer-phone">0901 234 567</p>
                  </div>
                </div>
                <div class="review-timestamp">26/05/2024 12:15</div>
              </div>

              <div class="review-stars">
                <span class="star filled">★</span>
                <span class="star empty">★</span>
                <span class="star empty">★</span>
                <span class="star empty">★</span>
                <span class="star empty">★</span>
                <span class="rating-badge">1/5 Rất tệ</span>
              </div>

              <div class="review-metadata">
                <p><strong>Món:</strong> Bánh mì thịt lợn</p>
              </div>

              <div class="review-text-box">
                <p>Nhận được bánh mì sai người. Hỗ trợ khách hàng rất chậm. Đã chờ đợi 2 ngày không ai xử lý. Sẽ không quay lại Jollibug nữa.</p>
              </div>
            </article>

            <!-- Review 4: High Score (4 stars) -->
            <article class="review-card">
              <div class="review-header-row">
                <div class="review-header-left">
                  <h3 class="review-id">#DG004</h3>
                  <div class="customer-info">
                    <p class="customer-name">Phạm Minh Đức</p>
                    <p class="customer-phone">0945 678 901</p>
                  </div>
                </div>
                <div class="review-timestamp">25/05/2024 19:45</div>
              </div>

              <div class="review-stars">
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="star empty">★</span>
                <span class="rating-badge">4/5 Tốt</span>
              </div>

              <div class="review-metadata">
                <p><strong>Món:</strong> Khoai tây chiên vàng ơi là vàng</p>
              </div>

              <div class="review-text-box">
                <p>Khoai tây rất ngon, giòn vàng ươm. Giao hàng nhanh chóng. Chỉ tiếc hơi ít muối một chút, nhưng vẫn rất hài lòng.</p>
              </div>
            </article>

            <!-- Review 5: Perfect Score (5 stars) -->
            <article class="review-card">
              <div class="review-header-row">
                <div class="review-header-left">
                  <h3 class="review-id">#DG005</h3>
                  <div class="customer-info">
                    <p class="customer-name">Dương Thu Trang</p>
                    <p class="customer-phone">0938 765 432</p>
                  </div>
                </div>
                <div class="review-timestamp">24/05/2024 11:30</div>
              </div>

              <div class="review-stars">
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="rating-badge">5/5 Xuất sắc</span>
              </div>

              <div class="review-metadata">
                <p><strong>Món:</strong> Burger gà sốt phô mai</p>
              </div>

              <div class="review-text-box">
                <p>Tuyệt vời! Burger rất ngon, thịt gà mềm, phô mai tan chảy. Giao hàng lại vô cùng nhanh. Sẽ tiếp tục đặt hàng từ Jollibug!</p>
              </div>
            </article>

            <!-- Review 6: Low Score (2 stars) -->
            <article class="review-card review-card--low-score">
              <div class="review-header-row">
                <div class="review-header-left">
                  <h3 class="review-id">#DG006</h3>
                  <div class="customer-info">
                    <p class="customer-name">Võ Quang Huy</p>
                    <p class="customer-phone">0912 348 765</p>
                  </div>
                </div>
                <div class="review-timestamp">23/05/2024 15:50</div>
              </div>

              <div class="review-stars">
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="star empty">★</span>
                <span class="star empty">★</span>
                <span class="star empty">★</span>
                <span class="rating-badge">2/5 Tệ</span>
              </div>

              <div class="review-metadata">
                <p><strong>Món:</strong> Trà sữa trân châu đen</p>
              </div>

              <div class="review-text-box">
                <p>Trà sữa bị lạnh quá, không còn vị trà. Trân châu cũng cứng chứ không mềm. Mong Jollibug cải thiện chất lượng nước uống.</p>
              </div>
            </article>

            <!-- Review 7: Good Score (4 stars) -->
            <article class="review-card">
              <div class="review-header-row">
                <div class="review-header-left">
                  <h3 class="review-id">#DG007</h3>
                  <div class="customer-info">
                    <p class="customer-name">Bùi Kim Anh</p>
                    <p class="customer-phone">0923 456 789</p>
                  </div>
                </div>
                <div class="review-timestamp">22/05/2024 20:10</div>
              </div>

              <div class="review-stars">
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="star filled">★</span>
                <span class="star empty">★</span>
                <span class="rating-badge">4/5 Tốt</span>
              </div>

              <div class="review-metadata">
                <p><strong>Món:</strong> Combo gia đình (2 người)</p>
              </div>

              <div class="review-text-box">
                <p>Đồ ăn tươi ngon, giao hàng nhanh chóng. Chất lượng ổn định. Giá hơi cao một chút so với quán khác nhưng chấp nhận được.</p>
              </div>
            </article>
          </div>
        </div>

      </section>
    </main>
  </div>

  <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
  <script src="<c:url value='/js/chat.js?v=3'/>"></script>
  <script src="<c:url value='/js/staff/support.js'/>"></script>
</body>
</html>


