<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Support</title>
  <meta name="description" content="Chat with Jollibug support about your order, payment, and delivery questions." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
</head>
<body data-page="chat">
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">
        <div class="page-intro">
          <h1 class="section-title">Chat hỗ trợ</h1>
        </div>

        <div class="client-chat-shell">

          <section class="support-chat" aria-label="Chat conversation"
                   data-chat-root
                   data-chat-variant="client"
                   data-chat-role="client"
                   data-chat-sender="${sessionScope.user.hoTen}"
                   data-chat-conversation-id="${sessionScope.user.maTK}">
            <header class="support-chat__head">
              <div class="support-chat__avatar">JT</div>
              <div class="support-chat__meta">
                <strong>Nhân viên Jollibug</strong>
                <span>Đang hoạt động</span>
              </div>
            </header>

            <div class="support-chat__messages" aria-live="polite" data-chat-messages>
              <div class="support-chat__day">Hôm nay</div>

              <%-- Hiển thị lịch sử chat từ DB --%>
              <c:forEach var="msg" items="${chatHistory}">
                <c:choose>
                  <c:when test="${msg.senderRole == 'client'}">
                    <article class="support-msg support-msg--user">
                      <div class="support-msg__bubble"><c:out value="${msg.content}"/></div>
                      <span class="support-msg__time">${msg.timeDisplay}</span>
                    </article>
                  </c:when>
                  <c:otherwise>
                    <article class="support-msg support-msg--agent">
                      <div class="support-msg__bubble"><c:out value="${msg.content}"/></div>
                      <span class="support-msg__time">${msg.timeDisplay}</span>
                    </article>
                  </c:otherwise>
                </c:choose>
              </c:forEach>

              <c:if test="${empty chatHistory}">
                <article class="support-msg support-msg--agent">
                  <div class="support-msg__bubble">Xin chào, mình là nhân viên hỗ trợ của Jollibug. Mình có thể giúp gì cho bạn?</div>
                  <span class="support-msg__time"></span>
                </article>
              </c:if>
            </div>

            <form class="support-chat__composer" data-chat-form>
              <label class="sr-only" for="support-chat-input">Tin nhắn</label>
              <input id="support-chat-input" data-chat-input type="text" placeholder="Nhập tin nhắn của bạn..." autocomplete="off" />
              <button class="btn btn-primary" type="submit">Gửi</button>
            </form>
          </section>
        </div>
      </div>
    </section>
  </main>

  <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp" />

  <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
  <script src="<c:url value='/js/chat.js?v=2'/>"></script>
</body>
</html>
