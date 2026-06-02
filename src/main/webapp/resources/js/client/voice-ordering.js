(function () {
  const root = document.querySelector("[data-voice-ordering]");

  if (!root) {
    return;
  }

  const addCartUrl = root.dataset.addCartUrl || "/addCart";
  const cartApiUrl = root.dataset.cartApiUrl || "/api/cart";
  const cartUrl = root.dataset.cartUrl || "/cart";
  const checkoutUrl = root.dataset.checkoutUrl || "/checkout";
  const micButton = root.querySelector("[data-voice-start]");
  const transcriptEl = root.querySelector("[data-voice-transcript]");
  const statusEl = root.querySelector("[data-voice-status]");
  const suggestionsEl = root.querySelector("[data-voice-suggestions]");
  const manualForm = root.querySelector("[data-voice-manual-form]");
  const manualInput = root.querySelector("[data-voice-manual-input]");
  const Recognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  const quantityWords = {
    "1": 1,
    mot: 1,
    "2": 2,
    hai: 2,
    "3": 3,
    ba: 3,
    "4": 4,
    bon: 4,
    "5": 5,
    nam: 5,
    "6": 6,
    sau: 6,
    "7": 7,
    bay: 7,
    "8": 8,
    tam: 8,
    "9": 9,
    chin: 9,
    "10": 10,
    muoi: 10
  };
  const fillerPhrases = [
    "vao gio hang",
    "khoi gio hang",
    "vao gio",
    "khoi gio",
    "gio hang",
    "giup toi",
    "cho toi",
    "cho minh",
    "lam on",
    "vui long",
    "so luong",
    "khong lay",
    "di toi",
    "mon",
    "phan",
    "cai",
    "ly",
    "suat",
    "nua",
    "len",
    "xuong"
  ];

  let recognition = null;
  let lastUndo = null;
  let lastSuggestionIntent = { kind: "default", budget: null };
  let currentSuggestion = null;

  function setStatus(message, state) {
    if (!statusEl) {
      return;
    }

    statusEl.textContent = message;
    if (state) {
      statusEl.dataset.state = state;
    } else {
      delete statusEl.dataset.state;
    }
  }

  function setTranscript(text) {
    if (transcriptEl) {
      transcriptEl.textContent = text || "Chưa có lệnh";
    }
  }

  function setProcessing() {
    setStatus("Đang xử lý...", "listening");
  }

  function normalizeText(value) {
    return (value || "")
      .toString()
      .toLowerCase()
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .replace(/\u0111/g, "d")
      .replace(/[^a-z0-9\s]/g, " ")
      .replace(/\s+/g, " ")
      .trim();
  }

  function getQuantityTokenPattern() {
    return "\\b(10|1|2|3|4|5|6|7|8|9|mot|hai|ba|bon|nam|sau|bay|tam|chin|muoi)\\b";
  }

  function readQuantity(text) {
    const match = (" " + text + " ").match(new RegExp(getQuantityTokenPattern()));

    if (!match) {
      return null;
    }

    return {
      value: quantityWords[match[1]] || 1,
      token: match[1]
    };
  }

  function removePhrases(text, phrases) {
    let result = " " + text + " ";

    phrases.forEach(function (phrase) {
      result = result.replace(new RegExp("\\b" + phrase + "\\b", "g"), " ");
    });

    return result.replace(/\s+/g, " ").trim();
  }

  function getMenuFoods() {
    return Array.from(document.querySelectorAll("[data-food-id][data-food-name]"))
      .map(function (card) {
        const name = card.dataset.foodName || "";
        const category = card.dataset.foodCategory || "";

        return {
          id: String(card.dataset.foodId || ""),
          name: name,
          price: Number(card.dataset.foodPrice || 0),
          category: category,
          element: card,
          normalizedName: normalizeText(name),
          normalizedCategory: normalizeText(category)
        };
      })
      .filter(function (food) {
        return food.id && food.normalizedName;
      });
  }

  function formatMoney(value) {
    const amount = Number(value || 0);

    if (!amount) {
      return "Chưa có giá";
    }

    return amount.toLocaleString("vi-VN") + "đ";
  }

  function parseBudget(text) {
    const normalized = normalizeText(text);
    let match = normalized.match(/\b(\d{1,3})\s*k\b/);

    if (match) {
      return Number(match[1]) * 1000;
    }

    match = normalized.match(/\b(\d{1,3})\s*(nghin|ngan)\b/);
    if (match) {
      return Number(match[1]) * 1000;
    }

    match = normalized.match(/\b(\d{5,7})\b/);
    if (match) {
      return Number(match[1]);
    }

    return null;
  }

  function getAveragePrice(foods) {
    const pricedFoods = foods.filter(function (food) {
      return food.price > 0;
    });

    if (!pricedFoods.length) {
      return 0;
    }

    return pricedFoods.reduce(function (total, food) {
      return total + food.price;
    }, 0) / pricedFoods.length;
  }

  function isDrink(food) {
    const text = food.normalizedName + " " + food.normalizedCategory;

    return /\b(nuoc|tra|pepsi|coca|juice|sinh to|mirinda|7up|chanh|xoai dao)\b/.test(text);
  }

  function isSpicy(food) {
    const text = food.normalizedName + " " + food.normalizedCategory;

    return /\b(cay|spicy|sot cay|gion cay)\b/.test(text);
  }

  function isComboLike(food) {
    const text = food.normalizedName + " " + food.normalizedCategory;

    return /\b(combo|burger|ga ran|my y|bo|ca|ga)\b/.test(text) && !isDrink(food);
  }

  function isSnackLike(food, averagePrice) {
    const text = food.normalizedName + " " + food.normalizedCategory;

    return /\b(khoai|salad|popcorn|banh|kem|sundae|snack|mon phu)\b/.test(text) || (food.price > 0 && food.price <= averagePrice);
  }

  function shuffleFoods(foods) {
    return foods
      .map(function (food) {
        return { food: food, sort: Math.random() };
      })
      .sort(function (a, b) {
        return a.sort - b.sort;
      })
      .map(function (item) {
        return item.food;
      });
  }

  function parseSuggestionIntent(text) {
    const normalized = normalizeText(text);
    const budget = parseBudget(normalized);

    if (normalized.includes("combo")) {
      return { kind: "combo", budget: budget };
    }

    if (normalized.includes("an no")) {
      return { kind: "filling", budget: budget };
    }

    if (normalized.includes("an nhe")) {
      return { kind: "light", budget: budget };
    }

    if (normalized.includes("mon re") || /\bre\b/.test(normalized) || normalized.includes("duoi") || (normalized.includes("co ") && normalized.includes(" an gi"))) {
      return { kind: "cheap", budget: budget };
    }

    if (normalized.includes("mon nuoc") || normalized.includes("nuoc")) {
      return { kind: "drink", budget: budget };
    }

    if (normalized.includes("mon cay") || normalized.includes("cay") || normalized.includes("spicy")) {
      return { kind: "spicy", budget: budget };
    }

    return { kind: "default", budget: budget };
  }

  function getSuggestionTitle(intent) {
    if (intent.kind === "combo") {
      return "Combo gợi ý dành cho bạn";
    }

    return "Gợi ý dành cho bạn";
  }

  function getSuggestionReason(intent, item) {
    const budgetText = intent.budget ? " trong ngân sách " + formatMoney(intent.budget) : "";

    if (intent.kind === "filling") {
      return "Phù hợp khi bạn muốn ăn no, ưu tiên món chính hoặc món có giá trị cao hơn mặt bằng menu" + budgetText + ".";
    }

    if (intent.kind === "light") {
      return "Hợp cho lúc muốn ăn nhẹ, ưu tiên món phụ, snack hoặc món giá vừa phải" + budgetText + ".";
    }

    if (intent.kind === "cheap") {
      return "Đây là lựa chọn tiết kiệm nổi bật trong các món đang hiển thị" + budgetText + ".";
    }

    if (intent.kind === "drink") {
      return "Ưu tiên đồ uống theo danh mục hoặc tên món như nước, trà, Pepsi, Mirinda, 7Up.";
    }

    if (intent.kind === "spicy") {
      return "Có tín hiệu vị cay trong tên món, hợp khi bạn muốn món đậm vị.";
    }

    if (intent.kind === "combo") {
      return "Combo được ghép từ các món đang hiển thị để cân bằng món chính, món phụ và đồ uống" + budgetText + ".";
    }

    if (item && item.price) {
      return "Món đang hiển thị đầy đủ thông tin, dễ thêm nhanh vào giỏ.";
    }

    return "Một lựa chọn nhanh từ các món đang hiển thị trên menu.";
  }

  function rankFoodsForSuggestion(foods, intent) {
    const averagePrice = getAveragePrice(foods);

    return foods
      .filter(function (food) {
        return !intent.budget || !food.price || food.price <= intent.budget;
      })
      .map(function (food) {
        let score = food.price > 0 ? 8 : 0;

        if (food.category) {
          score += 4;
        }

        if (intent.kind === "filling") {
          score += isComboLike(food) ? 45 : 0;
          score += food.price >= averagePrice ? 22 : 0;
        } else if (intent.kind === "light") {
          score += isSnackLike(food, averagePrice) ? 45 : 0;
          score += food.price > 0 && food.price <= averagePrice ? 18 : 0;
        } else if (intent.kind === "cheap") {
          score += food.price > 0 ? Math.max(0, 60 - food.price / 1000) : 0;
        } else if (intent.kind === "drink") {
          score += isDrink(food) ? 70 : 0;
        } else if (intent.kind === "spicy") {
          score += isSpicy(food) ? 70 : 0;
        } else {
          score += Math.random() * 22;
        }

        return { food: food, score: score };
      })
      .filter(function (entry) {
        if (intent.kind === "drink") {
          return isDrink(entry.food);
        }

        if (intent.kind === "spicy") {
          return isSpicy(entry.food);
        }

        return true;
      })
      .sort(function (a, b) {
        if (b.score !== a.score) {
          return b.score - a.score;
        }

        return a.food.price - b.food.price;
      })
      .map(function (entry) {
        return entry.food;
      });
  }

  function buildComboSuggestion(foods, intent) {
    const budget = intent.budget || null;
    const averagePrice = getAveragePrice(foods);
    const candidates = shuffleFoods(foods.filter(function (food) {
      return food.price > 0 && (!budget || food.price <= budget);
    }));
    const mains = rankFoodsForSuggestion(candidates.filter(function (food) {
      return isComboLike(food);
    }), { kind: "filling", budget: budget });
    const snacks = rankFoodsForSuggestion(candidates.filter(function (food) {
      return isSnackLike(food, averagePrice) && !isDrink(food);
    }), { kind: "light", budget: budget });
    const drinks = candidates.filter(isDrink).sort(function (a, b) {
      return a.price - b.price;
    });
    const pools = [mains, snacks, drinks];
    let bestCombo = null;

    function considerCombo(combo) {
      const uniqueIds = new Set(combo.map(function (food) {
        return food.id;
      }));

      if (uniqueIds.size !== combo.length || combo.length < 2) {
        return;
      }

      const total = combo.reduce(function (sum, food) {
        return sum + food.price;
      }, 0);

      if (budget && total > budget) {
        return;
      }

      const diversity = combo.some(isDrink) ? 12 : 0;
      const hasMain = combo.some(isComboLike) ? 16 : 0;
      const score = combo.length * 10 + diversity + hasMain + total / 10000;

      if (!bestCombo || score > bestCombo.score || (score === bestCombo.score && total > bestCombo.total)) {
        bestCombo = { items: combo, total: total, score: score };
      }
    }

    pools.forEach(function (poolA) {
      poolA.slice(0, 5).forEach(function (first) {
        pools.forEach(function (poolB) {
          poolB.slice(0, 5).forEach(function (second) {
            considerCombo([first, second]);
            pools.forEach(function (poolC) {
              poolC.slice(0, 4).forEach(function (third) {
                considerCombo([first, second, third]);
              });
            });
          });
        });
      });
    });

    if (!bestCombo) {
      const sorted = candidates.sort(function (a, b) {
        return a.price - b.price;
      });
      const fallback = [];
      let total = 0;

      sorted.forEach(function (food) {
        if (fallback.length >= 3) {
          return;
        }

        if (!fallback.some(function (item) { return item.id === food.id; }) && (!budget || total + food.price <= budget)) {
          fallback.push(food);
          total += food.price;
        }
      });

      if (fallback.length >= 2) {
        bestCombo = { items: fallback, total: total, score: 0 };
      }
    }

    if (!bestCombo) {
      return null;
    }

    return {
      type: "combo",
      title: getSuggestionTitle(intent),
      items: bestCombo.items,
      total: bestCombo.total,
      reason: getSuggestionReason(intent)
    };
  }

  function buildSmartSuggestion(intent, excludeFoodId) {
    const foods = getMenuFoods();

    if (!foods.length) {
      return null;
    }

    if (intent.kind === "combo") {
      return buildComboSuggestion(foods, intent);
    }

    const rankedFoods = rankFoodsForSuggestion(foods, intent).filter(function (food) {
      return !excludeFoodId || food.id !== excludeFoodId;
    });
    const fallbackFoods = foods.filter(function (item) {
      return (item.price > 0 || item.category) && (!intent.budget || !item.price || item.price <= intent.budget);
    }).filter(function (item) {
      return !excludeFoodId || item.id !== excludeFoodId;
    });
    const food = rankedFoods[0] || shuffleFoods(fallbackFoods)[0];

    if (!food) {
      return null;
    }

    return {
      type: "single",
      title: getSuggestionTitle(intent),
      item: food,
      total: food.price || 0,
      reason: getSuggestionReason(intent, food)
    };
  }

  function normalizeCartItem(item) {
    const name = item.tenMon || item.name || ("Món #" + item.maMon);

    return {
      id: String(item.maMon),
      name: name,
      quantity: Number(item.soLuong || 0),
      price: Number(item.donGia || 0),
      normalizedName: normalizeText(name),
      raw: item
    };
  }

  function getTokenList(value) {
    return normalizeText(value)
      .split(" ")
      .filter(function (token) {
        return token.length > 1 && !["va", "la", "an", "toi", "minh"].includes(token);
      });
  }

  // Tìm món gần đúng theo tên đã bỏ dấu và các từ quan trọng.
  function findFoodMatches(keyword, foods) {
    const normalizedKeyword = normalizeText(keyword);
    const keywordTokens = getTokenList(normalizedKeyword);

    if (!normalizedKeyword || keywordTokens.length === 0) {
      return [];
    }

    return foods
      .map(function (food) {
        const foodTokens = new Set(getTokenList(food.normalizedName));
        let score = 0;

        if (food.normalizedName === normalizedKeyword) {
          score = 120;
        } else if (food.normalizedName.includes(normalizedKeyword)) {
          score = 95 + Math.min(15, normalizedKeyword.length);
        } else if (normalizedKeyword.includes(food.normalizedName)) {
          score = 90;
        } else {
          const commonTokens = keywordTokens.filter(function (token) {
            return foodTokens.has(token) || food.normalizedName.includes(token);
          });
          const coverage = commonTokens.length / keywordTokens.length;
          const density = commonTokens.length / Math.max(foodTokens.size, 1);

          score = coverage * 75 + density * 25;
        }

        return { food: food, score: score };
      })
      .filter(function (match) {
        return match.score >= 52;
      })
      .sort(function (a, b) {
        if (b.score !== a.score) {
          return b.score - a.score;
        }

        return a.food.normalizedName.length - b.food.normalizedName.length;
      });
  }

  function resolveSingleMatch(keyword, foods) {
    const matches = findFoodMatches(keyword, foods);

    if (matches.length === 0) {
      return { status: "none", keyword: keyword };
    }

    if (matches.length > 1 && matches[0].score - matches[1].score < 12) {
      return {
        status: "ambiguous",
        keyword: keyword,
        matches: matches.slice(0, 4).map(function (match) {
          return match.food;
        })
      };
    }

    return { status: "ok", food: matches[0].food, matches: matches };
  }

  function cleanKeyword(text, commandWords) {
    const quantity = readQuantity(text);
    let result = text;

    if (quantity) {
      result = result.replace(new RegExp("\\b" + quantity.token + "\\b"), " ");
    }

    return removePhrases(result, commandWords.concat(fillerPhrases));
  }

  // Parse câu lệnh tiếng Việt thành hành động cart/menu.
  function parseCommand(text) {
    const normalized = normalizeText(text);

    if (!normalized) {
      return { type: "empty" };
    }

    if (/(xem|mo|di toi)\s+gio hang/.test(normalized) || normalized === "gio hang") {
      return { type: "go-cart" };
    }

    if (normalized.includes("thanh toan") || normalized.includes("di toi thanh toan")) {
      return { type: "checkout" };
    }

    if (normalized.includes("xoa gio hang") || normalized.includes("lam trong gio hang") || normalized.includes("bo het mon")) {
      return { type: "clear-cart" };
    }

    if (normalized.includes("goi y") ||
        normalized.includes("hom nay an gi") ||
        normalized.includes("chon giup toi mot mon") ||
        normalized.includes("toi muon an no") ||
        normalized.includes("toi muon an nhe") ||
        normalized.includes("toi muon mon re") ||
        normalized.includes("toi muon mon nuoc") ||
        (normalized.includes("toi co") && normalized.includes("an gi"))) {
      return {
        type: "suggest",
        intent: parseSuggestionIntent(normalized)
      };
    }

    if (normalized.startsWith("tim ") || normalized.includes("tim mon ")) {
      return {
        type: "find",
        keyword: cleanKeyword(normalized, ["tim"])
      };
    }

    if (normalized.startsWith("xoa ") || normalized.startsWith("bo ") || normalized.startsWith("khong lay ") || normalized.includes(" khong lay ")) {
      return {
        type: "delete",
        keyword: cleanKeyword(normalized, ["xoa", "bo"])
      };
    }

    if (normalized.includes("giam") || normalized.includes("bot")) {
      const targetMatch = normalized.match(new RegExp("\\b(?:xuong|con)\\s+" + getQuantityTokenPattern()));
      const quantity = readQuantity(normalized);

      return {
        type: "decrease",
        mode: targetMatch ? "set" : "delta",
        quantity: targetMatch ? (quantityWords[targetMatch[1]] || 1) : (quantity ? quantity.value : 1),
        keyword: cleanKeyword(normalized, ["giam", "bot"])
      };
    }

    if (normalized.includes("tang") || normalized.includes("them so luong") || (normalized.includes("them") && normalized.includes("nua"))) {
      const targetMatch = normalized.match(new RegExp("\\blen\\s+" + getQuantityTokenPattern()));
      const quantity = readQuantity(normalized);

      return {
        type: "increase",
        mode: targetMatch ? "set" : "delta",
        quantity: targetMatch ? (quantityWords[targetMatch[1]] || 1) : (quantity ? quantity.value : 1),
        keyword: cleanKeyword(normalized, ["tang", "them"])
      };
    }

    if (normalized.includes("them") || normalized.includes("mua") || normalized.includes("cho toi") || normalized.includes("dat") || normalized.includes("goi")) {
      const quantity = readQuantity(normalized);

      return {
        type: "add",
        quantity: quantity ? quantity.value : 1,
        keyword: cleanKeyword(normalized, ["them", "mua", "dat", "goi"])
      };
    }

    return { type: "unknown", keyword: normalized };
  }

  function getCsrfHeaders(contentType) {
    const headers = {
      "X-Requested-With": "XMLHttpRequest"
    };
    const tokenMeta = document.querySelector('meta[name="_csrf"]');
    const headerMeta = document.querySelector('meta[name="_csrf_header"]');
    const tokenInput = document.querySelector('input[name="_csrf"]');

    if (contentType) {
      headers["Content-Type"] = contentType;
    }

    if (tokenMeta && headerMeta && tokenMeta.content && headerMeta.content) {
      headers[headerMeta.content] = tokenMeta.content;
    } else if (tokenInput && tokenInput.value) {
      headers["X-CSRF-TOKEN"] = tokenInput.value;
    }

    return headers;
  }

  async function fetchJson(url, options) {
    const response = await fetch(url, options || {});

    if (!response.ok) {
      throw new Error("Không thể xử lý yêu cầu giỏ hàng.");
    }

    return response.json();
  }

  async function getCartItems() {
    const items = await fetchJson(cartApiUrl, {
      credentials: "same-origin",
      headers: getCsrfHeaders()
    });

    return (items || []).map(normalizeCartItem);
  }

  function findCartItemById(cartItems, id) {
    return cartItems.find(function (item) {
      return String(item.id) === String(id);
    }) || null;
  }

  async function refreshCartBadge() {
    try {
      const items = await getCartItems();
      const count = items.reduce(function (total, item) {
        return total + item.quantity;
      }, 0);
      const cartCountEl = document.getElementById("header-cart-count") || document.querySelector("[data-cart-count]");

      if (cartCountEl) {
        cartCountEl.textContent = String(count);
      }
    } catch (error) {
      // Badge can stay stale if the cart API is unavailable.
    }
  }

  // Gọi cart endpoint hiện có; thêm vẫn dùng POST /addCart.
  async function addFoodToCart(food, quantity) {
    const body = new URLSearchParams();
    body.set("productID", food.id);

    for (let index = 0; index < quantity; index += 1) {
      const response = await fetch(addCartUrl, {
        method: "POST",
        headers: getCsrfHeaders("application/x-www-form-urlencoded;charset=UTF-8"),
        credentials: "same-origin",
        redirect: "follow",
        body: body.toString()
      });

      if (!response.ok) {
        throw new Error("Không thể thêm món vào giỏ hàng.");
      }
    }

    await refreshCartBadge();
  }

  async function updateCartQuantity(foodId, quantity) {
    await fetchJson(cartApiUrl + "/items", {
      method: "PUT",
      credentials: "same-origin",
      headers: getCsrfHeaders("application/json"),
      body: JSON.stringify({
        maMon: Number(foodId),
        soLuong: quantity
      })
    });
    await refreshCartBadge();
  }

  async function deleteCartItem(foodId) {
    await fetchJson(cartApiUrl + "/items?maMon=" + encodeURIComponent(foodId), {
      method: "DELETE",
      credentials: "same-origin",
      headers: getCsrfHeaders()
    });
    await refreshCartBadge();
  }

  // Toast và undo thao tác gần nhất.
  function ensureToastRegion() {
    let region = document.querySelector("[data-voice-toast-region]");

    if (!region) {
      region = document.createElement("div");
      region.className = "voice-toast-region";
      region.dataset.voiceToastRegion = "";
      document.body.appendChild(region);
    }

    return region;
  }

  function showToast(message, type, options) {
    const toast = document.createElement("div");
    const region = ensureToastRegion();
    const actions = (options && options.actions) || [];
    let timer = null;

    toast.className = "voice-toast";
    toast.dataset.type = type || "info";
    toast.innerHTML = '<p class="voice-toast__message"></p><div class="voice-toast__actions"></div>';
    toast.querySelector(".voice-toast__message").textContent = message;

    const actionsEl = toast.querySelector(".voice-toast__actions");

    if (options && typeof options.undo === "function") {
      const undoButton = document.createElement("button");
      undoButton.type = "button";
      undoButton.textContent = "Hoàn tác";
      undoButton.addEventListener("click", async function () {
        undoButton.disabled = true;
        try {
          await options.undo();
          lastUndo = null;
          showToast("Đã hoàn tác thao tác vừa rồi.", "success");
          toast.remove();
        } catch (error) {
          undoButton.disabled = false;
          showToast(error.message || "Không thể hoàn tác thao tác vừa rồi.", "error");
        }
      });
      actionsEl.appendChild(undoButton);
    }

    actions.forEach(function (action) {
      const button = document.createElement("button");
      button.type = "button";
      button.textContent = action.label;
      if (action.secondary) {
        button.dataset.action = "secondary";
      }
      button.addEventListener("click", async function () {
        button.disabled = true;
        try {
          await action.handler();
          toast.remove();
        } catch (error) {
          button.disabled = false;
          showToast(error.message || "Không thể xử lý thao tác.", "error");
        }
      });
      actionsEl.appendChild(button);
    });

    if (!actionsEl.childElementCount) {
      actionsEl.remove();
    }

    function scheduleHide() {
      window.clearTimeout(timer);
      timer = window.setTimeout(function () {
        toast.remove();
      }, 4800);
    }

    toast.addEventListener("mouseenter", function () {
      window.clearTimeout(timer);
    });
    toast.addEventListener("mouseleave", scheduleHide);

    region.appendChild(toast);
    scheduleHide();
  }

  function showSuggestions(foods, handler) {
    if (!suggestionsEl) {
      return;
    }

    suggestionsEl.innerHTML = "";
    foods.forEach(function (food) {
      const button = document.createElement("button");
      button.type = "button";
      button.textContent = food.name;
      button.addEventListener("click", function () {
        suggestionsEl.hidden = true;
        handler(food);
      });
      suggestionsEl.appendChild(button);
    });
    suggestionsEl.hidden = false;
  }

  function hideSuggestions() {
    if (suggestionsEl) {
      suggestionsEl.hidden = true;
      suggestionsEl.innerHTML = "";
    }
  }

  async function undoToPreviousQuantity(food, previousQuantity) {
    if (previousQuantity > 0) {
      await updateCartQuantity(food.id, previousQuantity);
    } else {
      await deleteCartItem(food.id);
    }
  }

  async function runAdd(food, quantity) {
    const beforeCart = await getCartItems();
    const previousItem = findCartItemById(beforeCart, food.id);
    const previousQuantity = previousItem ? previousItem.quantity : 0;

    await addFoodToCart(food, quantity);

    lastUndo = function () {
      return undoToPreviousQuantity(food, previousQuantity);
    };

    setStatus("Đã thêm món vào giỏ hàng.", "success");
    showToast("Đã thêm " + quantity + " " + food.name + " vào giỏ hàng.", "success", {
      undo: lastUndo
    });
  }

  async function runAddCombo(foods) {
    const beforeCart = await getCartItems();
    const previousQuantities = new Map();

    foods.forEach(function (food) {
      const previousItem = findCartItemById(beforeCart, food.id);
      previousQuantities.set(food.id, previousItem ? previousItem.quantity : 0);
    });

    for (const food of foods) {
      await addFoodToCart(food, 1);
    }

    lastUndo = async function () {
      for (const food of foods) {
        await undoToPreviousQuantity(food, previousQuantities.get(food.id) || 0);
      }
    };

    setStatus("Đã thêm combo gợi ý vào giỏ hàng.", "success");
    showToast("Đã thêm combo gợi ý vào giỏ hàng.", "success", {
      undo: lastUndo
    });
  }

  async function runDelete(food) {
    const beforeCart = await getCartItems();
    const item = findCartItemById(beforeCart, food.id);

    if (!item) {
      throw new Error(food.name + " chưa có trong giỏ hàng.");
    }

    await deleteCartItem(food.id);

    lastUndo = async function () {
      await addFoodToCart(food, item.quantity);
    };

    setStatus("Đã xóa món khỏi giỏ hàng.", "success");
    showToast("Đã xóa " + item.name + " khỏi giỏ hàng.", "success", {
      undo: lastUndo
    });
  }

  async function runQuantityChange(food, command) {
    const beforeCart = await getCartItems();
    const item = findCartItemById(beforeCart, food.id);

    if (!item) {
      throw new Error(food.name + " chưa có trong giỏ hàng.");
    }

    const previousQuantity = item.quantity;
    let nextQuantity = previousQuantity;

    if (command.mode === "set") {
      nextQuantity = command.quantity;
    } else if (command.type === "decrease") {
      nextQuantity = previousQuantity - command.quantity;
    } else {
      nextQuantity = previousQuantity + command.quantity;
    }

    if (nextQuantity <= 0) {
      await deleteCartItem(food.id);
    } else {
      await updateCartQuantity(food.id, nextQuantity);
    }

    lastUndo = async function () {
      if (previousQuantity > 0) {
        await updateCartQuantity(food.id, previousQuantity);
      }
    };

    setStatus("Đã cập nhật giỏ hàng.", "success");
    showToast("Đã cập nhật " + food.name + " thành " + Math.max(nextQuantity, 0) + " phần.", "success", {
      undo: lastUndo
    });
  }

  function highlightFood(food) {
    if (!food.element) {
      return;
    }

    document.querySelectorAll(".is-voice-highlight").forEach(function (card) {
      card.classList.remove("is-voice-highlight");
    });
    food.element.classList.add("is-voice-highlight");
    food.element.scrollIntoView({ behavior: "smooth", block: "center" });
    window.setTimeout(function () {
      food.element.classList.remove("is-voice-highlight");
    }, 4200);
  }

  function runLegacySuggestion() {
    const foods = getMenuFoods();

    if (!foods.length) {
      setStatus("Không có món nào đang hiển thị để gợi ý.", "error");
      showToast("Không có món nào đang hiển thị để gợi ý.", "error");
      return;
    }

    const food = foods[Math.floor(Math.random() * foods.length)];

    highlightFood(food);
    setStatus("Gợi ý hôm nay: " + food.name + ".", "success");
    showToast("Gợi ý hôm nay: " + food.name + ".", "info", {
      actions: [{
        label: "Thêm vào giỏ",
        secondary: true,
        handler: function () {
          return runAdd(food, 1);
        }
      }]
    });
  }

  function ensureSmartSuggestionPanel() {
    let panel = root.querySelector("[data-smart-suggestion]");

    if (!panel) {
      panel = document.createElement("div");
      panel.className = "smart-suggestion";
      panel.dataset.smartSuggestion = "";

      if (suggestionsEl && suggestionsEl.parentElement) {
        suggestionsEl.insertAdjacentElement("afterend", panel);
      } else {
        root.appendChild(panel);
      }
    }

    return panel;
  }

  function createSmartButton(label, variant, handler) {
    const button = document.createElement("button");

    button.type = "button";
    button.textContent = label;
    button.className = variant ? "smart-suggestion__btn smart-suggestion__btn--" + variant : "smart-suggestion__btn";
    button.addEventListener("click", async function () {
      button.disabled = true;
      try {
        await handler();
      } catch (error) {
        showToast(error.message || "Không thể xử lý gợi ý.", "error");
      } finally {
        button.disabled = false;
      }
    });

    return button;
  }

  function renderEmptySmartSuggestion(message) {
    const panel = ensureSmartSuggestionPanel();
    const title = document.createElement("div");
    const text = document.createElement("p");

    panel.innerHTML = "";
    title.className = "smart-suggestion__eyebrow";
    title.textContent = "Smart Suggestion";
    text.className = "smart-suggestion__empty";
    text.textContent = message || "Chưa tìm thấy món phù hợp, bạn thử tiêu chí khác nha.";
    panel.appendChild(title);
    panel.appendChild(text);
  }

  function renderSmartSuggestion(suggestion, intent, isDefault) {
    const panel = ensureSmartSuggestionPanel();
    const eyebrow = document.createElement("div");
    const title = document.createElement("h3");
    const meta = document.createElement("div");
    const reason = document.createElement("p");
    const actions = document.createElement("div");

    panel.innerHTML = "";
    eyebrow.className = "smart-suggestion__eyebrow";
    eyebrow.textContent = isDefault ? "Bạn chưa biết ăn gì?" : "Smart Suggestion";
    title.className = "smart-suggestion__title";
    title.textContent = suggestion.title;
    meta.className = "smart-suggestion__meta";
    reason.className = "smart-suggestion__reason";
    reason.textContent = isDefault
      ? "Hãy thử nói: 'Tôi muốn ăn no', 'Gợi ý combo dưới 100k' hoặc 'Tôi muốn món rẻ'."
      : suggestion.reason;
    actions.className = "smart-suggestion__actions";

    panel.appendChild(eyebrow);
    panel.appendChild(title);

    if (suggestion.type === "combo") {
      const list = document.createElement("ul");

      list.className = "smart-suggestion__combo";
      suggestion.items.forEach(function (food) {
        const item = document.createElement("li");
        const name = document.createElement("span");
        const price = document.createElement("strong");

        name.textContent = food.name;
        price.textContent = formatMoney(food.price);
        item.appendChild(name);
        item.appendChild(price);
        list.appendChild(item);
      });
      meta.textContent = "Tổng: " + formatMoney(suggestion.total);
      actions.appendChild(createSmartButton("Thêm combo vào giỏ", "primary", function () {
        return runAddCombo(suggestion.items);
      }));
      panel.appendChild(list);
    } else {
      const food = suggestion.item;

      meta.textContent = food.category
        ? food.category + " · " + formatMoney(food.price)
        : formatMoney(food.price);
      actions.appendChild(createSmartButton("Thêm vào giỏ", "primary", function () {
        return runAdd(food, 1);
      }));
    }

    actions.appendChild(createSmartButton("Gợi ý món khác", "ghost", function () {
      return runSuggestion(intent, { reroll: true });
    }));
    panel.appendChild(meta);
    panel.appendChild(reason);
    panel.appendChild(actions);
  }

  function runSuggestion(intent, options) {
    const resolvedIntent = intent || lastSuggestionIntent || { kind: "default", budget: null };
    const excludeFoodId = options && options.reroll && currentSuggestion && currentSuggestion.type === "single"
      ? currentSuggestion.item.id
      : null;
    const suggestion = buildSmartSuggestion(resolvedIntent, excludeFoodId);

    lastSuggestionIntent = resolvedIntent;

    if (!suggestion) {
      currentSuggestion = null;
      renderEmptySmartSuggestion("Chưa tìm thấy món phù hợp, bạn thử tiêu chí khác nha.");
      setStatus("Chưa tìm thấy món phù hợp để gợi ý.", "error");
      return;
    }

    currentSuggestion = suggestion;

    if (suggestion.type === "single") {
      highlightFood(suggestion.item);
    }

    renderSmartSuggestion(suggestion, resolvedIntent, options && options.defaultPanel);
    setStatus(resolvedIntent.kind === "combo" ? "Đã tạo combo gợi ý." : "Đã có gợi ý phù hợp.", "success");

    if (!options || !options.silent) {
      showToast(suggestion.type === "combo" ? "Đã tạo combo gợi ý cho bạn." : "Đã chọn một món gợi ý phù hợp.", "info");
    }
  }

  async function resolveCartFood(command) {
    const cartItems = await getCartItems();
    const match = resolveSingleMatch(command.keyword, cartItems);

    return { match: match, cartItems: cartItems };
  }

  async function handleMatchedMenuCommand(command, action) {
    const match = resolveSingleMatch(command.keyword, getMenuFoods());

    if (match.status === "none") {
      setStatus("Không tìm thấy món phù hợp.", "error");
      showToast('Không tìm thấy món phù hợp với "' + command.keyword + '".', "error");
      return;
    }

    if (match.status === "ambiguous") {
      setStatus("Có nhiều món phù hợp, hãy chọn một món.", "error");
      showSuggestions(match.matches, action);
      return;
    }

    hideSuggestions();
    await action(match.food);
  }

  async function handleMatchedCartCommand(command, action) {
    const result = await resolveCartFood(command);
    const match = result.match;

    if (match.status === "none") {
      setStatus("Không tìm thấy món đó trong giỏ hàng.", "error");
      showToast('Không tìm thấy món trong giỏ phù hợp với "' + command.keyword + '".', "error");
      return;
    }

    if (match.status === "ambiguous") {
      setStatus("Có nhiều món trong giỏ phù hợp, hãy chọn một món.", "error");
      showSuggestions(match.matches, action);
      return;
    }

    hideSuggestions();
    await action(match.food);
  }

  async function handleCommand(text) {
    const spokenText = (text || "").trim();

    setTranscript(spokenText);
    hideSuggestions();
    setProcessing();

    const command = parseCommand(spokenText);

    try {
      if (command.type === "go-cart") {
        setStatus("Đã chuyển đến giỏ hàng.", "success");
        showToast("Đã chuyển đến giỏ hàng.", "success");
        window.location.href = cartUrl;
        return;
      }

      if (command.type === "checkout") {
        setStatus("Đã chuyển đến thanh toán.", "success");
        showToast("Đã chuyển đến thanh toán.", "success");
        window.location.href = checkoutUrl;
        return;
      }

      if (command.type === "clear-cart") {
        setStatus("Chưa hỗ trợ xóa toàn bộ giỏ hàng.", "error");
        showToast("Chưa hỗ trợ xóa toàn bộ giỏ hàng vì project chưa có endpoint clear cart.", "error");
        return;
      }

      if (command.type === "suggest") {
        runSuggestion(command.intent || { kind: "default", budget: null });
        return;
      }

      if (command.type === "find") {
        await handleMatchedMenuCommand(command, function (food) {
          highlightFood(food);
          setStatus("Đã tìm thấy " + food.name + ".", "success");
          showToast("Đã tìm thấy " + food.name + ".", "success");
        });
        return;
      }

      if (command.type === "add") {
        await handleMatchedMenuCommand(command, function (food) {
          return runAdd(food, command.quantity);
        });
        return;
      }

      if (command.type === "increase" || command.type === "decrease") {
        await handleMatchedCartCommand(command, function (food) {
          return runQuantityChange(food, command);
        });
        return;
      }

      if (command.type === "delete") {
        await handleMatchedCartCommand(command, runDelete);
        return;
      }

      setStatus("Chưa nhận ra lệnh. Hãy thử nói tên món rõ hơn.", "error");
      showToast("Chưa nhận ra lệnh. Hãy thử lại bằng câu ngắn hơn.", "error");
    } catch (error) {
      setStatus(error.message || "Có lỗi khi xử lý lệnh.", "error");
      showToast(error.message || "Có lỗi khi xử lý lệnh.", "error");
    }
  }

  // Nhận diện giọng nói bằng Web Speech API.
  if (Recognition) {
    recognition = new Recognition();
    recognition.lang = "vi-VN";
    recognition.continuous = false;
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;

    recognition.onstart = function () {
      if (micButton) {
        micButton.disabled = true;
      }
      setStatus("Đang nghe...", "listening");
    };

    recognition.onend = function () {
      if (micButton) {
        micButton.disabled = false;
      }
    };

    recognition.onerror = function () {
      if (micButton) {
        micButton.disabled = false;
      }
      setStatus("Trình duyệt không nghe được. Hãy thử lại hoặc dùng ô nhập tay.", "error");
      showToast("Trình duyệt không nghe được. Hãy thử Chrome/Edge hoặc dùng ô nhập tay.", "error");
    };

    recognition.onresult = function (event) {
      const result = event.results && event.results[0] && event.results[0][0];
      handleCommand(result ? result.transcript : "");
    };
  } else {
    if (micButton) {
      micButton.disabled = true;
    }
    setStatus("Trình duyệt chưa hỗ trợ nhận giọng nói. Hãy dùng Chrome hoặc Edge.", "error");
  }

  if (micButton) {
    micButton.addEventListener("click", function () {
      if (!recognition) {
        setStatus("Trình duyệt chưa hỗ trợ nhận giọng nói. Hãy dùng Chrome hoặc Edge.", "error");
        showToast("Trình duyệt chưa hỗ trợ nhận giọng nói. Hãy dùng Chrome hoặc Edge.", "error");
        return;
      }

      try {
        recognition.start();
      } catch (error) {
        setStatus("Micro đang bận. Hãy thử lại sau vài giây.", "error");
      }
    });
  }

  if (manualForm && manualInput) {
    manualForm.addEventListener("submit", function (event) {
      event.preventDefault();
      handleCommand(manualInput.value);
    });
  }

}());
