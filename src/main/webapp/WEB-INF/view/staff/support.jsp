<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
  <link rel="stylesheet" href="<c:url value='/css/staff.css'/>" />
</head>

<body data-admin-role="staff" data-admin-page="support">

  <div class="admin-shell admin-body" data-admin-table-root>

    <jsp:include page="layout/sidebar.jsp" />

    <main class="admin-main">

      <jsp:include page="layout/topbar.jsp" />

      <c:set var="supportTab" value="${empty param.tab ? 'chat' : param.tab}" />

      <section class="admin-panel">
        <div class="panel-header">
          <div class="stack" style="gap:0.3rem;">
            <h1 class="section-title">Chăm sóc khách hàng</h1>
          </div>
          <div class="panel-controls support-toolbar">
            <div class="order-filter-strip__pills" role="tablist" aria-label="Bộ tab chăm sóc khách hàng">
              <a href="<c:url value='/staff/support?tab=chat'/>" class="btn btn-ghost support-tab<c:if test='${supportTab == "chat"}'> is-active</c:if>" role="tab" aria-selected="${supportTab == 'chat'}" aria-controls="tab-chat">
                Chat
              </a>
              <a href="<c:url value='/staff/support?tab=complaint'/>" class="btn btn-ghost support-tab<c:if test='${supportTab == "complaint"}'> is-active</c:if>" role="tab" aria-selected="${supportTab == 'complaint'}" aria-controls="tab-complaint">
                Khiếu nại
              </a>
              <a href="<c:url value='/staff/support?tab=review'/>" class="btn btn-ghost support-tab<c:if test='${supportTab == "review"}'> is-active</c:if>" role="tab" aria-selected="${supportTab == 'review'}" aria-controls="tab-review">
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
                <span class="status-badge" data-status="active">2 online</span>
              </div>
              <div class="ticket-list">
                <article class="ticket-item is-active">
                  <div class="ticket-item__header">
                    <span class="ticket-item__name">Nguyễn Minh Quân</span>
                    <span class="ticket-item__time">10:31</span>
                  </div>
                  <p class="ticket-item__preview">Cho mình hỏi đơn #JB-123 đã giao chưa?</p>
                </article>
                <article class="ticket-item">
                  <div class="ticket-item__header">
                    <span class="ticket-item__name">Trần Thu Hà</span>
                    <span class="ticket-item__time">10:22</span>
                  </div>
                  <p class="ticket-item__preview">Mình muốn đổi địa chỉ nhận đơn.</p>
                </article>
              </div>
            </aside>

            <section class="chat-workspace">
              <article class="chat-panel">
                <header class="chat-panel__header">
                  <div class="chat-panel__avatar">MQ</div>
                  <div class="chat-panel__meta">
                    <strong>Nguyễn Minh Quân</strong>
                    <span>Đang hoạt động</span>
                  </div>
                </header>

                <div class="chat-messages">
                  <div class="chat-bubble chat-bubble--client">
                    <div class="chat-bubble__avatar">MQ</div>
                    <div class="chat-bubble__body">
                      <div class="chat-bubble__text">Cho mình hỏi đơn #JB-123 đã giao chưa?</div>
                      <span class="chat-bubble__time">10:29</span>
                    </div>
                  </div>
                  <div class="chat-bubble chat-bubble--staff">
                    <div class="chat-bubble__avatar">ST</div>
                    <div class="chat-bubble__body">
                      <div class="chat-bubble__text">Đơn của bạn đang trên đường giao và dự kiến 15 phút nữa sẽ đến.</div>
                      <span class="chat-bubble__time">10:30</span>
                    </div>
                  </div>
                </div>

                <form class="chat-input-bar" action="<c:url value='/staff/support/chat/send'/>" method="post">
                  <input id="chat-input" name="message" type="text" placeholder="Nhập phản hồi cho khách hàng..." required />
                  <button class="chat-send-btn" type="submit" aria-label="Gửi tin nhắn">
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

        <div id="tab-complaint" class="support-tab-panel<c:if test='${supportTab == "complaint"}'> is-active</c:if>" <c:if test='${supportTab != "complaint"}'>hidden</c:if>>
          <div class="support-table-wrap">
            <table class="admin-table">
              <thead>
                <tr>
                  <th>Mã khiếu nại</th>
                  <th>Khách hàng</th>
                  <th>Mã đơn</th>
                  <th>Nội dung</th>
                  <th>Trạng thái</th>
                  <th>Phản hồi</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>#KN001</td>
                  <td>Phạm Quỳnh Anh</td>
                  <td>#JB-CF-2002</td>
                  <td>Đơn giao thiếu món khoai tây.</td>
                  <td><span class="status-badge" data-status="featured">Chờ xử lý</span></td>
                  <td>
                    <form class="support-reply-form" action="<c:url value='/staff/support/complaint/reply'/>" method="post">
                      <textarea name="reply" placeholder="Nhập phản hồi khiếu nại..." required></textarea>
                      <button class="btn btn-primary" type="submit">Gửi phản hồi</button>
                    </form>
                  </td>
                </tr>
                <tr>
                  <td>#KN002</td>
                  <td>Hoàng Anh Đức</td>
                  <td>#JB-CF-2001</td>
                  <td>Món ăn bị nguội khi nhận.</td>
                  <td><span class="status-badge" data-status="featured">Chờ xử lý</span></td>
                  <td>
                    <form class="support-reply-form" action="<c:url value='/staff/support/complaint/reply'/>" method="post">
                      <textarea name="reply" placeholder="Nhập phản hồi khiếu nại..." required></textarea>
                      <button class="btn btn-primary" type="submit">Gửi phản hồi</button>
                    </form>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div id="tab-review" class="support-tab-panel<c:if test='${supportTab == "review"}'> is-active</c:if>" <c:if test='${supportTab != "review"}'>hidden</c:if>>
          <div class="support-table-wrap">
            <table class="admin-table">
              <thead>
                <tr>
                  <th>Mã đánh giá</th>
                  <th>Khách hàng</th>
                  <th>Đơn hàng</th>
                  <th>Điểm</th>
                  <th>Nội dung</th>
                  <th>Phản hồi</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>#DG001</td>
                  <td>Lê Hoàng Phúc</td>
                  <td>#JB-CF-2004</td>
                  <td>3/5</td>
                  <td>Giao hàng hơi chậm.</td>
                  <td>
                    <form class="support-reply-form" action="<c:url value='/staff/support/review/reply'/>" method="post">
                      <textarea name="reply" placeholder="Nhập phản hồi đánh giá..." required></textarea>
                      <button class="btn btn-primary" type="submit">Gửi phản hồi</button>
                    </form>
                  </td>
                </tr>
                <tr>
                  <td>#DG002</td>
                  <td>Trần Thu Hà</td>
                  <td>#JB-CF-2005</td>
                  <td>5/5</td>
                  <td>Món ngon, đóng gói sạch sẽ.</td>
                  <td>
                    <form class="support-reply-form" action="<c:url value='/staff/support/review/reply'/>" method="post">
                      <textarea name="reply" placeholder="Nhập phản hồi đánh giá..." required></textarea>
                      <button class="btn btn-primary" type="submit">Gửi phản hồi</button>
                    </form>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

      </section>
    </main>
  </div>

</body>
</html>

