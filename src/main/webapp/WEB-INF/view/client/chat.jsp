<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Chat hỗ trợ</title>
  <meta name="description" content="Chat với nhân viên Jollibug để được hỗ trợ về đơn hàng, thanh toán và giao hàng." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
</head>
<body data-page="chat">
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">
        <div class="page-intro">
          <h1 class="section-title">Chat hỗ trợ</h1>
          <c:if test="${not empty yeuCau}">
            <p style="color:var(--color-ink-500);font-size:.88rem">
              Yêu cầu #<c:out value="${yeuCau.maYC}"/> –
              <c:out value="${yeuCau.tieuDe}"/> –
              <c:choose>
                <c:when test="${yeuCau.trangThai == 'PENDING'}">
                  <span style="font-weight:700;color:#d97706">Chờ tiếp nhận</span>
                </c:when>
                <c:when test="${yeuCau.trangThai == 'PROCESSING'}">
                  <span style="font-weight:700;color:#2563eb">Đang xử lý</span>
                </c:when>
                <c:otherwise>
                  <span style="font-weight:700;color:#16a34a"><c:out value="${yeuCau.trangThai}"/></span>
                </c:otherwise>
              </c:choose>
            </p>
          </c:if>
        </div>

        <div class="client-chat-shell">
          <c:choose>
            <c:when test="${empty yeuCau}">
              <div class="create-ticket-form" style="width: 100%; max-width: 800px; margin: 0 auto; background: var(--color-surface); padding: 3rem; border-radius: 16px; box-shadow: var(--shadow-md); border: 1px solid var(--color-ink-200);">
                <h2 style="margin-bottom: 2rem; font-size: 1.5rem; text-align: center;">Tạo yêu cầu hỗ trợ mới</h2>
                <form action="<c:url value='/chat/create'/>" method="POST">
                  <div class="form-group" style="margin-bottom: 1.5rem;">
                    <label style="display:block; margin-bottom: 0.75rem; font-weight: 600; font-size: 1.1rem;">Tiêu đề vấn đề</label>
                    <input type="text" name="tieuDe" required class="form-control" placeholder="Ví dụ: Đơn hàng bị giao trễ" style="width: 100%; padding: 1rem; font-size: 1.05rem; border: 1px solid var(--color-ink-300); border-radius: 8px;"/>
                  </div>
                  <div class="form-group" style="margin-bottom: 2rem;">
                    <label style="display:block; margin-bottom: 0.75rem; font-weight: 600; font-size: 1.1rem;">Nội dung chi tiết</label>
                    <textarea name="noiDung" required class="form-control" rows="6" placeholder="Mô tả chi tiết vấn đề bạn đang gặp phải..." style="width: 100%; padding: 1rem; font-size: 1.05rem; border: 1px solid var(--color-ink-300); border-radius: 8px; resize: vertical;"></textarea>
                  </div>
                  <button type="submit" class="btn btn-primary" style="width: 100%; padding: 1rem; font-size: 1.1rem; border-radius: 8px;">Gửi yêu cầu hỗ trợ</button>
                </form>
              </div>
            </c:when>
            <c:otherwise>
              <%-- data-chat-mayc truyền MaYC của YeuCauHoTro sang JS --%>
              <section class="support-chat" aria-label="Chat conversation"
                       data-chat-root
                       data-chat-variant="client"
                       data-chat-vaitrogui="Khach"
                       data-chat-ma-tk-gui="${sessionScope.user.maTK}"
                       data-chat-ten="${sessionScope.user.hoTen}"
                       data-chat-mayc="${yeuCau.maYC}">

                <header class="support-chat__head">
                  <div class="support-chat__avatar">JT</div>
                  <div class="support-chat__meta">
                    <strong>Nhân viên Jollibug</strong>
                    <c:choose>
                      <c:when test="${yeuCau.trangThai == 'PROCESSING' and not empty yeuCau.nhanVien}">
                        <span>Đang hỗ trợ bởi <c:out value="${yeuCau.nhanVien.hoTen}"/></span>
                      </c:when>
                      <c:otherwise>
                        <span>Đang chờ nhân viên hỗ trợ...</span>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </header>

                <div class="support-chat__messages" aria-live="polite" data-chat-messages>
                  <div class="support-chat__day">Hôm nay</div>

                  <%-- Lịch sử chat từ DB (ChiTietHoTro) --%>
                  <c:forEach var="msg" items="${chatHistory}">
                    <c:choose>
                      <c:when test="${msg.vaiTroGui == 'Khach'}">
                        <article class="support-msg support-msg--user">
                          <div class="support-msg__bubble"><c:out value="${msg.noiDung}"/></div>
                          <span class="support-msg__time">${msg.timeDisplay}</span>
                        </article>
                      </c:when>
                      <c:otherwise>
                        <article class="support-msg support-msg--agent">
                          <div class="support-msg__bubble"><c:out value="${msg.noiDung}"/></div>
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
                  <c:set var="isChatDisabled" value="${yeuCau.trangThai == 'PENDING' or yeuCau.trangThai == 'DONE'}"/>
                  <input id="support-chat-input" data-chat-input type="text"
                         placeholder="${yeuCau.trangThai == 'PENDING' ? 'Vui lòng chờ nhân viên tiếp nhận yêu cầu...' : yeuCau.trangThai == 'DONE' ? 'Yêu cầu đã hoàn thành' : 'Nhập tin nhắn của bạn...'}" 
                         autocomplete="off" 
                         <c:if test="${isChatDisabled}">disabled</c:if> />
                  <button class="btn btn-primary" type="submit" <c:if test="${isChatDisabled}">disabled</c:if>>
                    Gửi
                  </button>
                </form>
              </section>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </section>
  </main>

  <jsp:include page="layout/footer.jsp" />

  <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
  <script src="<c:url value='/js/chat.js?v=4'/>"></script>
</body>
</html>
