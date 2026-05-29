<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Signature Menu</title>
  <meta name="description" content="Search, sort, and filter the Jollibug menu. Browse burgers, chicken, combos, drinks, sides, and wraps." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/client/menu.css" />
</head>
<body data-page="menu">
  <fmt:setLocale value="vi_VN" />

  <!-- SECTION -->
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">
        <div class="page-intro">
          <h1 class="section-title">Thực đơn Jollibug</h1>
        </div>

        <!-- SECTION -->
        <section class="menu-toolbar reveal-up" aria-label="Menu filters">
          
          <form class="toolbar-row" method="get" action="/menu">
            <input type="hidden" name="categoryID" value="${selectCategoryID}" />
            <div class="searchbar">
              <!--
                main.js attaches an 'input' listener to this element.
                The search icon SVG is now static inline - no JS injection.
              -->
              <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="7"></circle><path d="m20 20-3.5-3.5"></path>
              </svg>
              <input type="search" placeholder="Tìm kiếm món ăn?"
                      name="keyword" value="${keyword}"
                      onkeydown="if(event.key === 'Enter'){ event.preventDefault(); this.form.submit(); }"/>
            </div>

            <select id="menu-sort" name="filter" data-menu-sort onchange="this.form.submit()">
              <option value="popular" ${selectedFilter == 'popular' ? 'selected' : ''}>Bộ lọc</option>
              <option value="price-low" ${selectedFilter == 'price-low' ? 'selected' : ''}>Giá: thấp đến cao</option>
              <option value="price-high" ${selectedFilter == 'price-high' ? 'selected' : ''}>Giá: cao đến thấp</option>
              <option value="rating" ${selectedFilter == 'rating' ? 'selected' : ''}>Bán chạy</option>
            </select>
          </form>

          <div class="category-nav" aria-label="Menu categories">
            <button class="category-nav__arrow" type="button" data-cat-arrow="prev" aria-label="Previous categories">&#10094;</button>
            <div class="category-strip" data-menu-cats id="menu-categories">
              <a class="filter-pill ${selectCategoryID == null ? 'is-active' :''}" href="/menu?filter=${selectedFilter}&keyword=${keyword}">All</a>
              <c:forEach var="dm" items="${danhMuc}">
                <a class="filter-pill ${selectCategoryID != null && selectCategoryID == dm.maDM ? 'is-active' : ''}" href="/menu?categoryID=${dm.maDM}&filter=${selectedFilter}&keyword=${keyword}" >${dm.tenDM}</a>
              </c:forEach>
            </div>
            <button class="category-nav__arrow" type="button" data-cat-arrow="next" aria-label="Next categories">&#10095;</button>
          </div>
        </section>

        <section class="voice-ordering reveal-up"
                 data-voice-ordering
                 data-add-cart-url="${pageContext.request.contextPath}/addCart"
                 data-cart-api-url="${pageContext.request.contextPath}/api/cart"
                 data-cart-url="${pageContext.request.contextPath}/cart"
                 data-checkout-url="${pageContext.request.contextPath}/checkout"
                 aria-label="Voice ordering">
          <div class="voice-ordering__main">
            <button class="voice-ordering__mic" type="button" data-voice-start aria-label="Bắt đầu đặt món bằng giọng nói">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M12 3a3 3 0 0 0-3 3v6a3 3 0 0 0 6 0V6a3 3 0 0 0-3-3Z"></path>
                <path d="M19 10v2a7 7 0 0 1-14 0v-2"></path>
                <path d="M12 19v3"></path>
                <path d="M8 22h8"></path>
              </svg>
            </button>
            <div class="voice-ordering__content">
              <div class="voice-ordering__title">Đặt món bằng giọng nói</div>
              <div class="voice-ordering__heard">
                <span>Câu vừa nói:</span>
                <strong data-voice-transcript>Chưa có lệnh</strong>
              </div>
              <div class="voice-ordering__status" data-voice-status>Sẵn sàng nghe lệnh tiếng Việt.</div>
              <div class="voice-ordering__suggestions" data-voice-suggestions hidden></div>
            </div>
          </div>
          <form class="voice-ordering__fallback" data-voice-manual-form>
            <input type="text"
                   data-voice-manual-input
                   placeholder="thêm 2 pepsi"
                   autocomplete="off" />
            <button type="submit">Nhập</button>
          </form>
        </section>

        <section class="card-grid" data-menu-grid id="menu-grid" aria-label="Menu items">
          <c:forEach var="monAn" items="${listMonAn}">
            <article class="hp-prod-card reveal-up"
                     data-food-card
                     data-food-id="${monAn.maMon}"
                     data-food-name="${fn:escapeXml(monAn.tenMon)}"
                     data-food-price="${monAn.giaGiam}"
                     data-food-category="${fn:escapeXml(monAn.danhMuc.tenDM)}">
            <div class="hp-prod-card__img">
              <img src="/images/${monAn.img}" alt="${monAn.tenMon}" />
              <c:if test="${monAn.hasGiamGia}">
                <span class="hp-prod-card__badge">-${monAn.phanTramGiam}%</span>
              </c:if>
            </div>
            <div class="hp-prod-card__body">
              <div class="hp-prod-card__meta">
                <span>${monAn.danhMuc.tenDM}</span>
                <!-- <span class="stars">★ 4.9</span> -->
              </div>
              <div class="hp-prod-card__name">
                <div>${monAn.tenMon}</div>
                <div class="hp-prod-card__price-container">
                  <c:choose>
                    <c:when test="${monAn.hasGiamGia}">
                      <span class="hp-prod-card__price"><fmt:formatNumber value="${monAn.giaGiam}" type="number" />đ</span>
                      <span class="hp-prod-card__old-price"><fmt:formatNumber value="${monAn.gia}" type="number" />đ</span>
                    </c:when>
                    <c:otherwise>
                      <span class="hp-prod-card__price"><fmt:formatNumber value="${monAn.gia}" type="number" />đ</span>
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>
              <div class="hp-prod-card__desc">${monAn.moTa}</div>
              <div class="hp-prod-card__footer">
                
                <a class="hp-prod-card__btn" href="/product?productID=${monAn.maMon}">Xem chi tiết</a>
                <c:if test="${not empty sessionScope.user}">
                  <form method="post" action="/addCart" >
                    <input type="hidden" name="productID" value="${monAn.maMon}">
                    <button class="hp-prod-card__btn" type="submit">+ Thêm</button>
                  </form>
                </c:if>
              </div>
            </div>
          </article>
          </c:forEach>
        </section>
      </div>
    </section>
  </main>

    <!--  FOOTER -->
  <jsp:include page="layout/footer.jsp" />
  
    <div class="jb-ai-chat" data-jb-ai-chat>
    <section class="jb-ai-chat__panel" data-chat-panel aria-label="Jollibug AI chat" aria-hidden="true">
      <header class="jb-ai-chat__header">
        <div class="jb-ai-chat__avatar" aria-hidden="true">JB</div>
        <div class="jb-ai-chat__identity">
          <strong>Jollibug AI</strong>
          <span>Trợ lý đặt món 24/7</span>
        </div>
        <button class="jb-ai-chat__close" type="button" data-chat-close aria-label="Đóng chat">×</button>
      </header>

      <div class="jb-ai-chat__messages" data-chat-messages>
        <div class="jb-ai-chat__bubble jb-ai-chat__bubble--bot">
          Xin chào! 🍗 Tôi là Jollibug AI. Tôi có thể giúp bạn tìm món, gợi ý combo, hoặc giải đáp thắc mắc về menu!
        </div>
      </div>

      <div class="jb-ai-chat__quick" aria-label="Câu hỏi gợi ý">
        <button type="button" data-quick-reply="Gợi ý món hôm nay">Gợi ý món hôm nay</button>
        <button type="button" data-quick-reply="Combo tiết kiệm">Combo tiết kiệm</button>
        <button type="button" data-quick-reply="Xem menu gà rán">Xem menu gà rán</button>
      </div>

      <form class="jb-ai-chat__composer" data-chat-form>
        <input type="text" data-chat-input placeholder="Nhập tin nhắn..." autocomplete="off" />
        <button type="submit" aria-label="Gửi tin nhắn">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M4 12L20 4L16.5 20L11 13L4 12Z"></path>
          </svg>
        </button>
      </form>
    </section>

      <button class="jb-ai-chat__toggle" type="button" data-chat-toggle aria-label="Chat với AI Jollibug">
        <span class="jb-ai-chat__tooltip">Chat với AI Jollibug</span>
        <span class="jb-ai-chat__badge" aria-hidden="true"></span>
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="M12 5V3"></path>
          <path d="M8 3H16"></path>
          <rect x="5" y="7" width="14" height="11" rx="4"></rect>
          <path d="M9 12H9.01"></path>
          <path d="M15 12H15.01"></path>
          <path d="M10 16H14"></path>
          <path d="M4 11H3"></path>
          <path d="M21 11H20"></path>
        </svg>
      </button>
    </div>

  <script src="/js/client/main.js"></script>
  <script src="/js/client/voice-ordering.js"></script>
  <script src="/js/client/jollibug-ai-chat.js"></script>
</body>
</html>




