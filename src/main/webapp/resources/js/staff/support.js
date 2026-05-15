(function () {
  "use strict";

  function $(sel, root) { return (root || document).querySelector(sel); }
  function $$(sel, root) { return [...(root || document).querySelectorAll(sel)]; }

  var ticketItems = $$(".ticket-item");
  var chatPanel = $("[data-chat-root]");

  if (!ticketItems || ticketItems.length === 0 || !chatPanel) {
    return;
  }

  ticketItems.forEach(function (item) {
    item.addEventListener("click", function () {
      var conversationId = item.dataset.conversationId;
      if (!conversationId) return;

      // Remove active class from all items
      ticketItems.forEach(function (ti) {
        ti.classList.remove("is-active");
      });

      // Add active class to clicked item
      item.classList.add("is-active");

      // Update chat panel conversation ID and reload page
      // (Simplest approach: reload with new conversationId)
      var currentTab = new URLSearchParams(location.search).get("tab") || "chat";
      window.location.href = "?tab=" + currentTab + "&conversationId=" + conversationId;
    });
  });
})();
