(function() {
  'use strict';

  // Cart state management
  let cart = JSON.parse(localStorage.getItem('jollibug_cart') || '[]');

  // DOM elements
  const cartItemList = document.getElementById('cart-item-list');
  const emptyState = document.getElementById('cart-empty-state');
  const itemCount = document.getElementById('summary-item-count');
  const subtotal = document.getElementById('summary-subtotal');
  const total = document.getElementById('summary-total');
  const checkoutButton = document.getElementById('checkout-button');

  // Initialize cart display
  function initCart() {
    renderCart();
    updateSummary();
  }

  // Render cart items
  function renderCart() {
    if (!cartItemList) return;

    if (cart.length === 0) {
      cartItemList.innerHTML = '';
      if (emptyState) emptyState.classList.remove('hidden');
      return;
    }

    if (emptyState) emptyState.classList.add('hidden');

    cartItemList.innerHTML = cart.map((item, index) => `
      <div class="cart-item" data-index="${index}">
        <div class="cart-item__image">
          <img src="${item.image || '/images/placeholder.png'}" alt="${item.name}" />
        </div>
        <div class="cart-item__details">
          <h3 class="cart-item__name">${item.name}</h3>
          <p class="cart-item__price">${formatCurrency(item.price)}</p>
          <div class="cart-item__quantity">
            <button class="quantity-btn" onclick="changeQuantity(${index}, -1)">-</button>
            <span class="quantity-value">${item.quantity}</span>
            <button class="quantity-btn" onclick="changeQuantity(${index}, 1)">+</button>
          </div>
        </div>
        <button class="cart-item__remove" onclick="removeItem(${index})">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
      </div>
    `).join('');
  }

  // Update order summary
  function updateSummary() {
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    const totalPrice = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);

    if (itemCount) itemCount.textContent = `${totalItems} ITEM${totalItems !== 1 ? 'S' : ''}`;
    if (subtotal) subtotal.textContent = formatCurrency(totalPrice);
    if (total) total.textContent = formatCurrency(totalPrice);

    // Enable/disable checkout button
    //test xong mo lai
    // if (checkoutButton) {
    //     checkoutButton.disabled = (cart.length === 0);
    // }
  }

  // Format currency
  function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(amount);
  }

  // Change item quantity
  window.changeQuantity = function(index, delta) {
    if (cart[index]) {
      cart[index].quantity = Math.max(1, cart[index].quantity + delta);
      saveCart();
      renderCart();
      updateSummary();
    }
  };

  // Remove item from cart
  window.removeItem = function(index) {
    cart.splice(index, 1);
    saveCart();
    renderCart();
    updateSummary();
  };

  // Save cart to localStorage
  function saveCart() {
    localStorage.setItem('jollibug_cart', JSON.stringify(cart));
  }

  // Add item to cart (utility function for other pages)
  window.addToCart = function(item) {
    const existingIndex = cart.findIndex(cartItem => cartItem.id === item.id);
    if (existingIndex >= 0) {
      cart[existingIndex].quantity += item.quantity || 1;
    } else {
      cart.push({
        id: item.id,
        name: item.name,
        price: item.price,
        image: item.image,
        quantity: item.quantity || 1
      });
    }
    saveCart();
    updateSummary();
    // Could add toast notification here
  };

  // Initialize when DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initCart);
  } else {
    initCart();
  }

  if (checkoutButton) {
  checkoutButton.addEventListener('click', function() {
    window.location.href = '/checkout';
    });
  }
  // Make cart available globally for debugging
  window.cart = cart;
})();