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
          <p class="lead">Trao đổi trực tiếp với nhân viên Jollibug vè đơn hàng, thanh toán và giao hàng</p>
        </div>

        <div class="client-chat-shell">

          <section class="support-chat" aria-label="Chat conversation">
            <header class="support-chat__head">
              <div class="support-chat__avatar">JT</div>
              <div class="support-chat__meta">
                <strong>Nhân viên Jollibug</strong>
                <span>Đang hoạt động</span>
              </div>
            </header>

            <div class="support-chat__messages" aria-live="polite">
              <div class="support-chat__day">Hôm nay</div>

              <article class="support-msg support-msg--agent">
                <div class="support-msg__bubble">Xin ch&#224;o, m&#236;nh l&#224; nh&#226;n vi&#234;n h&#7895; tr&#7907; c&#7911;a Jollibug. M&#236;nh c&#243; th&#7875; gi&#250;p g&#236; cho b&#7841;n?</div>
                <span class="support-msg__time">09:28</span>
              </article>

              <article class="support-msg support-msg--user">
                <div class="support-msg__bubble">M&#236;nh mu&#7889;n ki&#7875;m tra &#273;&#417;n h&#224;ng #JB2026. &#272;&#417;n n&#224;y &#273;&#227; giao ch&#432;a?</div>
                <span class="support-msg__time">09:29</span>
              </article>

              <article class="support-msg support-msg--agent">
                <div class="support-msg__bubble">B&#7841;n vui l&#242;ng ch&#7901; trong gi&#226;y l&#225;t, m&#236;nh &#273;ang ki&#7875;m tra tr&#7841;ng th&#225;i &#273;&#417;n h&#224;ng.</div>
                <span class="support-msg__time">09:29</span>
              </article>

              <article class="support-msg support-msg--agent">
                <div class="support-msg__bubble">&#272;&#417;n h&#224;ng c&#7911;a b&#7841;n &#273;ang &#273;&#432;&#7907;c giao v&#224; d&#7921; ki&#7871;n &#273;&#7871;n trong 15 ph&#250;t n&#7919;a.</div>
                <span class="support-msg__time">09:30</span>
              </article>
            </div>

            <form class="support-chat__composer">
              <label class="sr-only" for="support-chat-input">Tin nh&#7855;n</label>
              <input id="support-chat-input" type="text" placeholder="Nh&#7853;p tin nh&#7855;n c&#7911;a b&#7841;n..." autocomplete="off" />
              <button class="btn btn-primary" type="button">G&#7917;i</button>
            </form>
          </section>
        </div>
      </div>
    </section>
  </main>

    <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp" />

</body>
</html>
