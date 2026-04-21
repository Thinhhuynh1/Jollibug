(function () {
  // Minimal shared store placeholder so page scripts can load safely.
  window.appStore = window.appStore || {
    cart: [],
    add: function (item) {
      this.cart.push(item);
    }
  };
})();
