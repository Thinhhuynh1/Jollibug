(function () {
  "use strict";

  function formatTime(ts) {
    if (!ts) return "";
    return new Date(ts).toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" });
  }

  function scrollBottom(el) {
    if (el) el.scrollTop = el.scrollHeight;
  }

  /** Tạo bubble DOM cho giao diện Staff */
  function buildStaffBubble(message, activeClientName) {
    var isKhach = message.vaiTroGui === "Khach";
    var bubble = document.createElement("div");
    bubble.className = isKhach ? "chat-bubble chat-bubble--client" : "chat-bubble chat-bubble--staff";

    var avatar = document.createElement("div");
    avatar.className = "chat-bubble__avatar";
    if (isKhach) {
      var name = activeClientName || message.tenNguoiGui || "KH";
      avatar.textContent = name.trim().substring(0, 2).toUpperCase();
    } else {
      avatar.textContent = "NV";
    }

    var body = document.createElement("div");
    body.className = "chat-bubble__body";

    var text = document.createElement("div");
    text.className = "chat-bubble__text";
    text.textContent = message.noiDung;

    var time = document.createElement("span");
    time.className = "chat-bubble__time";
    time.textContent = formatTime(message.timestamp);

    body.appendChild(text);
    body.appendChild(time);
    bubble.appendChild(avatar);
    bubble.appendChild(body);
    return bubble;
  }

  /** Tạo article DOM cho giao diện Client */
  function buildClientMsg(message) {
    var isAgent = message.vaiTroGui !== "Khach";
    var article = document.createElement("article");
    article.className = isAgent ? "support-msg support-msg--agent" : "support-msg support-msg--user";

    var bubble = document.createElement("div");
    bubble.className = "support-msg__bubble";
    bubble.textContent = message.noiDung;

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

    // Đọc các data-attribute từ JSP
    var maYC          = root.dataset.chatMayc;        // ID của YeuCauHoTro
    var maTKGui       = root.dataset.chatMaTkGui;      // MaTK người dùng
    var vaiTroGui     = root.dataset.chatVaitrogui;    // "Khach" | "NhanVien"
    var tenNguoiGui   = root.dataset.chatTen || "";
    var variant       = root.dataset.chatVariant || "client";
    var activeClientName = "";

    if (variant === "staff") {
      var metaEl = root.querySelector(".chat-panel__meta strong");
      if (metaEl) activeClientName = metaEl.textContent.trim();
    }

    console.log("[Chat] init — maYC:", maYC, "vaiTroGui:", vaiTroGui, "variant:", variant);

    var messagesEl = root.querySelector("[data-chat-messages]");
    var formEl     = root.querySelector("[data-chat-form]");
    var inputEl    = root.querySelector("[data-chat-input]");

    if (!messagesEl || !formEl || !inputEl) {
      console.warn("[Chat] Missing required DOM elements");
      return;
    }

    if (variant === "client" && !maYC) {
      console.warn("[Chat] Client missing maYC");
      return;
    }

    scrollBottom(messagesEl);

    // --- Kết nối WebSocket ---
    var socket = new SockJS("/ws");
    var stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, function () {
      console.log("[Chat] STOMP connected.");

      // Dò kênh của phòng chat hiện tại
      if (maYC) {
        subscribeToChat(maYC);
      }

      // Staff dò thêm kênh global để biết có yêu cầu mới
      if (variant === "staff") {
        stompClient.subscribe("/topic/staff/chat", function (frame) {
          try {
            var msg = JSON.parse(frame.body || "{}");
            // Nếu tin nhắn thuộc phòng khác → reload sidebar
            if (String(msg.maYC) !== String(maYC)) {
              window.location.reload();
            }
          } catch (e) {}
        });
      }

    }, function (err) {
      console.error("[Chat] STOMP error:", err);
    });

    function subscribeToChat(channelId) {
      stompClient.subscribe("/topic/chat/" + channelId, function (frame) {
        try {
          var msg = JSON.parse(frame.body || "{}");
          console.log("[Chat] Received:", msg);
          appendMessage(msg);
        } catch (e) {
          console.error("[Chat] Parse error:", e);
        }
      });
    }

    function sendStompMessage(text) {
      if (!maYC) return;
      var outgoing = {
        type:         "CHAT",
        maYC:         parseInt(maYC, 10),
        maTKGui:      maTKGui ? parseInt(maTKGui, 10) : null,
        vaiTroGui:    vaiTroGui,
        noiDung:      text,
        tenNguoiGui:  tenNguoiGui,
        timestamp:    Date.now()
      };

      console.log("[Chat] Sending:", outgoing);
      stompClient.send("/app/chat.send", {}, JSON.stringify(outgoing));
      inputEl.value = "";
      inputEl.focus();
    }

    // --- Gửi tin nhắn ---
    formEl.addEventListener("submit", function (e) {
      e.preventDefault();
      var text = inputEl.value.trim();
      if (!text || !stompClient.connected || !maYC) return;
      sendStompMessage(text);
    });

    // --- Thêm tin nhắn mới vào DOM (realtime) ---
    function appendMessage(message) {
      if (!message || !message.noiDung) return;
      var el = variant === "staff"
        ? buildStaffBubble(message, activeClientName)
        : buildClientMsg(message);
      messagesEl.appendChild(el);
      scrollBottom(messagesEl);

      // Nếu Client đang bị khóa chat (do trạng thái Pending), mở khóa khi có tin nhắn từ Staff
      if (variant === "client" && message.vaiTroGui !== "Khach") {
        if (inputEl.disabled) {
          inputEl.disabled = false;
          inputEl.placeholder = "Nhập tin nhắn của bạn...";
          var btnEl = formEl.querySelector("button[type='submit']");
          if (btnEl) btnEl.disabled = false;

          // Cập nhật trạng thái hiển thị trên màn hình từ Pending -> Processing
          var statusEl = document.querySelector(".page-intro p span");
          if (statusEl && statusEl.textContent === "Pending") {
            statusEl.textContent = "Processing";
            statusEl.style.color = "#2563eb"; // Đổi màu xanh
          }
        }
      }
    }
  }

})();
