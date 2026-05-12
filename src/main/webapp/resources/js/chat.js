(function () {
  "use strict";

  function $(sel, root) { return (root || document).querySelector(sel); }

  function formatTime(ts) {
    if (!ts) return "";
    return new Date(ts).toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" });
  }

  /** Scroll messages container to bottom */
  function scrollBottom(el) {
    if (el) el.scrollTop = el.scrollHeight;
  }

  /** Tạo bubble DOM cho phía staff */
  function buildStaffBubble(message, activeClientName) {
    var isClient = message.senderRole === "client";
    var bubble = document.createElement("div");
    bubble.className = isClient ? "chat-bubble chat-bubble--client" : "chat-bubble chat-bubble--staff";

    var avatar = document.createElement("div");
    avatar.className = "chat-bubble__avatar";
    if (isClient) {
      var name = activeClientName || message.sender || "?";
      avatar.textContent = name.trim().substring(0, 2).toUpperCase();
    } else {
      avatar.textContent = "NV";
    }

    var body = document.createElement("div");
    body.className = "chat-bubble__body";

    var text = document.createElement("div");
    text.className = "chat-bubble__text";
    text.textContent = message.content;

    var time = document.createElement("span");
    time.className = "chat-bubble__time";
    time.textContent = formatTime(message.timestamp);

    body.appendChild(text);
    body.appendChild(time);
    bubble.appendChild(avatar);
    bubble.appendChild(body);
    return bubble;
  }

  /** Tạo article DOM cho phía client */
  function buildClientMsg(message) {
    var isAgent = message.senderRole !== "client";
    var article = document.createElement("article");
    article.className = isAgent ? "support-msg support-msg--agent" : "support-msg support-msg--user";

    var bubble = document.createElement("div");
    bubble.className = "support-msg__bubble";
    bubble.textContent = message.content;

    var time = document.createElement("span");
    time.className = "support-msg__time";
    time.textContent = formatTime(message.timestamp);

    article.appendChild(bubble);
    article.appendChild(time);
    return article;
  }

  // Đợi SockJS + Stomp load xong
  var checkInterval = setInterval(function () {
    if (typeof SockJS !== "undefined" && typeof Stomp !== "undefined") {
      clearInterval(checkInterval);
      initChat();
    }
  }, 100);

  function initChat() {
    var root = document.querySelector("[data-chat-root]");
    if (!root) {
      console.warn("[Chat] data-chat-root not found");
      return;
    }

    var conversationId = root.dataset.chatConversationId;
    var role           = root.dataset.chatRole     || "client";
    var sender         = root.dataset.chatSender   || "";
    var variant        = root.dataset.chatVariant  || "client";
    // Tên khách hiển thị trên avatar (chỉ cần cho staff view)
    var activeClientName = root.querySelector(".chat-panel__meta strong")
                           ? root.querySelector(".chat-panel__meta strong").textContent.trim()
                           : "";

    console.log("[Chat] init — convId:", conversationId, "role:", role, "variant:", variant);

    var messagesEl = root.querySelector("[data-chat-messages]");
    var formEl     = root.querySelector("[data-chat-form]");
    var inputEl    = root.querySelector("[data-chat-input]");

    if (!messagesEl || !formEl || !inputEl) {
      console.warn("[Chat] Missing required DOM elements");
      return;
    }

    // Client bắt buộc phải có conversationId (là mã tài khoản)
    if (variant === "client" && !conversationId) {
      console.warn("[Chat] Client missing conversationId");
      return;
    }

    // Scroll xuống dưới với lịch sử đã render từ server
    scrollBottom(messagesEl);

    // --- Kết nối WebSocket ---
    var socket = new SockJS("/ws");
    var stompClient = Stomp.over(socket);
    stompClient.debug = null; // tắt log STOMP spam

    stompClient.connect({}, function () {
      console.log("[Chat] Connected.");

      // Lắng nghe tin nhắn của hội thoại hiện tại
      if (conversationId) {
        stompClient.subscribe("/topic/conversations/" + conversationId, function (frame) {
          try {
            var msg = JSON.parse(frame.body || "{}");
            console.log("[Chat] Received:", msg);
            appendMessage(msg);
          } catch (e) {
            console.error("[Chat] Parse error:", e);
          }
        });
      }

      // Nếu là Staff, lắng nghe thêm kênh global để cập nhật danh sách hội thoại mới
      if (variant === "staff") {
        stompClient.subscribe("/topic/staff/chat", function (frame) {
          try {
            var msg = JSON.parse(frame.body || "{}");
            // Nếu tin nhắn mới không thuộc hội thoại đang mở, reload trang để cập nhật sidebar
            if (msg.conversationId !== conversationId) {
              window.location.reload();
            }
          } catch (e) {}
        });
      }

    }, function (err) {
      console.error("[Chat] STOMP error:", err);
    });

    // --- Gửi tin nhắn ---
    formEl.addEventListener("submit", function (e) {
      e.preventDefault();
      var text = inputEl.value.trim();
      
      // Không gửi nếu rỗng, chưa kết nối, hoặc chưa chọn hội thoại
      if (!text || !stompClient.connected || !conversationId) return;

      var outgoing = {
        type:           "CHAT",
        conversationId: conversationId,
        sender:         sender,
        senderRole:     role,
        content:        text,
        timestamp:      Date.now()
      };

      console.log("[Chat] Sending:", outgoing);
      stompClient.send("/app/chat.send", {}, JSON.stringify(outgoing));
      inputEl.value = "";
      inputEl.focus();
    });

    // --- Thêm tin nhắn mới vào DOM (chỉ dùng cho realtime, không dùng cho lịch sử) ---
    function appendMessage(message) {
      if (!message || !message.content) return;
      var el = variant === "staff"
        ? buildStaffBubble(message, activeClientName)
        : buildClientMsg(message);
      messagesEl.appendChild(el);
      scrollBottom(messagesEl);
    }
  }

})();
