(function () {
  const root = document.querySelector("[data-jb-ai-chat]");
  if (!root) return;

  const panel = root.querySelector("[data-chat-panel]");
  const toggleBtn = root.querySelector("[data-chat-toggle]");
  const closeBtn = root.querySelector("[data-chat-close]");
  const form = root.querySelector("[data-chat-form]");
  const input = root.querySelector("[data-chat-input]");
  const messages = root.querySelector("[data-chat-messages]");
  const quickReplies = root.querySelectorAll("[data-quick-reply]");

  function setOpen(isOpen) {
    root.classList.toggle("is-open", isOpen);
    panel.setAttribute("aria-hidden", String(!isOpen));
    if (isOpen) {
      setTimeout(() => input.focus(), 120);
    }
  }

  function scrollToBottom() {
    messages.scrollTop = messages.scrollHeight;
  }

  function appendMessage(text, role) {
    const bubble = document.createElement("div");
    bubble.className = "jb-ai-chat__bubble jb-ai-chat__bubble--" + role;
    bubble.textContent = text;
    messages.appendChild(bubble);
    scrollToBottom();
    return bubble;
  }

  async function sendMessage(text) {
    const message = text.trim();
    if (!message) return;

    appendMessage(message, "user");
    input.value = "";
    input.disabled = true;
    form.querySelector("button").disabled = true;

    const typing = appendMessage("Jollibug AI đang trả lời...", "bot jb-ai-chat__bubble--typing");

    try {
      const response = await fetch("/api/ai/chat", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ message })
      });

      if (!response.ok) {
        throw new Error("Request failed");
      }

      const data = await response.json();
      typing.remove();
      appendMessage(data.reply || "Mình chưa có câu trả lời phù hợp. Bạn thử hỏi cách khác nhé.", "bot");
    } catch (error) {
      typing.remove();
      appendMessage("Hiện mình chưa kết nối được với Jollibug AI. Bạn thử lại sau một chút nhé.", "bot");
    } finally {
      input.disabled = false;
      form.querySelector("button").disabled = false;
      input.focus();
    }
  }

  toggleBtn.addEventListener("click", function () {
    setOpen(!root.classList.contains("is-open"));
  });

  closeBtn.addEventListener("click", function () {
    setOpen(false);
  });

  form.addEventListener("submit", function (event) {
    event.preventDefault();
    sendMessage(input.value);
  });

  quickReplies.forEach(function (button) {
    button.addEventListener("click", function () {
      setOpen(true);
      sendMessage(button.dataset.quickReply || button.textContent);
    });
  });
})();
