<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="/css/client/menu.css" />
  <link rel="stylesheet" href="/css/client/menu.css" />
</head>
<body data-page="menu">
  <fmt:setLocale value="vi_VN" />
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
              <input type="search" placeholder="Tìm kiếm món ăn?"
                      name="keyword" value="${keyword}"
                      onkeydown="if(event.key === 'Enter'){ event.preventDefault(); this.form.submit(); }"/>
            </div>

            <select id="menu-sort" name="filter" data-menu-sort onchange="this.form.submit()">
              <option value="popular" ${selectedFilter == 'popular' ? 'selected' : ''}>Bộ lọc</option>
              <option value="price-low" ${selectedFilter == 'price-low' ? 'selected' : ''}>Giá: thấp đến cao</option>
              <option value="price-high" ${selectedFilter == 'price-high' ? 'selected' : ''}>Giá: cao đến thấp</option>
              <option value="rating" ${selectedFilter == 'rating' ? 'selected' : ''}>Bán chạy</option>
            <select id="menu-sort" name="filter" data-menu-sort onchange="this.form.submit()">
              <option value="popular" ${selectedFilter == 'popular' ? 'selected' : ''}>Bộ lọc</option>
              <option value="price-low" ${selectedFilter == 'price-low' ? 'selected' : ''}>Giá: thấp đến cao</option>
              <option value="price-high" ${selectedFilter == 'price-high' ? 'selected' : ''}>Giá: cao đến thấp</option>
              <option value="rating" ${selectedFilter == 'rating' ? 'selected' : ''}>Bán chạy</option>
            </select>
          </form>
          </form>

          <div class="category-nav" aria-label="Menu categories">
            <button class="category-nav__arrow" type="button" data-cat-arrow="prev" aria-label="Previous categories">&#10094;</button>
            <div class="category-strip" data-menu-cats id="menu-categories">
              <a class="filter-pill ${selectCategoryID == null ? 'is-active' :''}" href="/menu?filter=${selectedFilter}&keyword=${keyword}">All</a>
              <c:forEach var="dm" items="${danhMuc}">
                <a class="filter-pill ${selectCategoryID != null && selectCategoryID == dm.maDM ? 'is-active' : ''}" href="/menu?categoryID=${dm.maDM}&filter=${selectedFilter}&keyword=${keyword}" >${dm.tenDM}</a>
              </c:forEach>
              <a class="filter-pill ${selectCategoryID == null ? 'is-active' :''}" href="/menu?filter=${selectedFilter}&keyword=${keyword}">All</a>
              <c:forEach var="dm" items="${danhMuc}">
                <a class="filter-pill ${selectCategoryID != null && selectCategoryID == dm.maDM ? 'is-active' : ''}" href="/menu?categoryID=${dm.maDM}&filter=${selectedFilter}&keyword=${keyword}" >${dm.tenDM}</a>
              </c:forEach>
            </div>
            <button class="category-nav__arrow" type="button" data-cat-arrow="next" aria-label="Next categories">&#10095;</button>
          </div>
        </section>

        <section class="card-grid" data-menu-grid id="menu-grid" aria-label="Menu items">
          <c:forEach var="monAn" items="${listMonAn}">
            <article class="hp-prod-card reveal-up">
            <div class="hp-prod-card__img">
              <img src="${monAn.img}" alt="${monAn.tenMon}" />
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
                
                <a class="hp-prod-card__btn" href="/product">Xem chi tiết</a>
                <button class="hp-prod-card__btn" type="button">+ Thêm</button>
              </div>
            </div>
          </article>
          </c:forEach>
        </section>
      </div>
    </section>
  </main>

    <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp" />
  

  </body>
  <script src="js/client/main.js"></script>  <!-- mui ten qua lai danh muc-->
</html>




