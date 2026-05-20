/**
 * support.js – Jollibug Staff Support Page
 * Handles: tab switching, quick reply chips, chat auto-scroll, toast
 */
(function () {
  "use strict";

  /* ── HELPERS ──────────────────────────────────────────── */
  function $(sel, root) { return (root || document).querySelector(sel); }
  function $$(sel, root) { return [...(root || document).querySelectorAll(sel)]; }

  /* ── TOAST ────────────────────────────────────────────── */
  function showToast(msg, type) {
    var stack = document.getElementById("admin-toast-stack");
    if (!stack) return;
    var t = document.createElement("div");
    t.className = "toast" + (type ? " toast--" + type : "");
    t.textContent = msg;
    stack.appendChild(t);
    setTimeout(function () { t.remove(); }, 3200);
  }

  /* ── TAB SWITCHING ────────────────────────────────────── */
  function initTabs() {
    var tabs   = $$(".support-tab");
    var panels = $$(".support-tab-panel");

    /* Restore tab from URL param ?tab=... */
    var urlTab = new URLSearchParams(location.search).get("tab");

    function activateTab(targetId) {
      tabs.forEach(function (btn) {
        var active = btn.getAttribute("aria-controls") === targetId;
        btn.classList.toggle("is-active", active);
        btn.setAttribute("aria-selected", active ? "true" : "false");
      });
      panels.forEach(function (panel) {
        var active = panel.id === targetId;
        panel.classList.toggle("is-active", active);
        if (active) {
          panel.removeAttribute("hidden");
          /* scroll chat to bottom when switching to chat tab */
          if (targetId === "tab-chat") scrollChatToBottom();
        } else {
          panel.setAttribute("hidden", "");
        }
      });
    }

    tabs.forEach(function (btn) {
      btn.addEventListener("click", function () {
        var id = btn.getAttribute("aria-controls");
        activateTab(id);
        /* update URL without reload */
        var params = new URLSearchParams(location.search);
        params.set("tab", id.replace("tab-", ""));
        history.replaceState(null, "", "?" + params.toString());
      });
    });

    /* Apply URL-driven tab on load */
    if (urlTab) {
      var target = "tab-" + urlTab;
      var match = panels.find(function (p) { return p.id === target; });
      if (match) activateTab(target);
    }
  }

  /* ── QUICK REPLY CHIPS ────────────────────────────────── */
  function initQuickReplies() {
    $$(".quick-reply-chip").forEach(function (chip) {
      chip.addEventListener("click", function () {
        var text  = chip.dataset.reply || chip.textContent.trim();
        var input = document.getElementById("chat-input");
        if (!input) return;
        input.value = (input.value ? input.value + " " : "") + text;
        input.focus();
      });
    });
  }

  /* ── CHAT AUTO-SCROLL ─────────────────────────────────── */
  function scrollChatToBottom() {
    var area = $(".chat-messages");
    if (area) area.scrollTop = area.scrollHeight;
  }

  /* ── AUTO-GROW TEXTAREA ───────────────────────────────── */
  function initAutoGrow() {
    $$("textarea").forEach(function (ta) {
      ta.addEventListener("input", function () {
        ta.style.height = "auto";
        ta.style.height = Math.min(ta.scrollHeight, 200) + "px";
      });
    });
  }

  /* ── REPLY FORMS: show toast on submit ────────────────── */
  function initReplyForms() {
    /* Complaint reply */
    $$("form[action*='complaint/reply']").forEach(function (form) {
      form.addEventListener("submit", function () {
        showToast("Đã gửi phản hồi khiếu nại!", "success");
      });
    });
    /* Review reply */
    $$("form[action*='review/reply']").forEach(function (form) {
      form.addEventListener("submit", function () {
        showToast("Đã gửi phản hồi đánh giá!", "success");
      });
    });
    /* Chat send */
    $$("form[action*='chat/send']").forEach(function (form) {
      form.addEventListener("submit", function () {
        scrollChatToBottom();
      });
    });
  }

  /* ── INIT ─────────────────────────────────────────────── */
  document.addEventListener("DOMContentLoaded", function () {
    initTabs();
    initQuickReplies();
    initAutoGrow();
    initReplyForms();
    scrollChatToBottom();
  });

})();