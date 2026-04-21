<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
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
          <span class="eyebrow">Support Desk</span>
          <h1 class="section-title">Live Support Chat</h1>
          <p class="lead">Tell us what you need and we will assist you right away.</p>
        </div>

        <div class="client-chat-shell">
          <aside class="support-topics" aria-label="Support topics">
            <header class="support-topics__head">
              <strong>Topics</strong>
              <span class="badge">Online</span>
            </header>
            <div class="support-topic-list" id="support-topic-list"></div>
          </aside>

          <section class="support-chat" aria-label="Chat conversation">
            <header class="support-chat__head">
              <div class="support-chat__avatar">FB</div>
              <div class="support-chat__meta">
                <strong id="chat-title">Jollibug Support</strong>
                <span id="chat-subtitle">Select a topic to begin.</span>
              </div>
            </header>

            <div class="support-chat__messages" id="support-messages" aria-live="polite"></div>

            <form class="support-chat__composer" id="support-chat-form">
              <label class="sr-only" for="support-chat-input">Message</label>
              <input id="support-chat-input" type="text" placeholder="Type your message..." autocomplete="off" />
              <button class="btn btn-primary" type="submit">Send</button>
            </form>
          </section>
        </div>
      </div>
    </section>
  </main>

  <footer class="site-footer">
    <div class="container">
      <div class="footer-note">
        <span>&copy; 2026 Jollibug. All rights reserved.</span>
        <a href="/">Back to Home</a>
      </div>
    </div>
  </footer>

  <script src="js/client/nav.js" defer></script>
  <script src="js/client/chat.js" defer></script>
  </body>
</html>




