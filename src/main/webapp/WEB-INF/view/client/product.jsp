<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Product Detail</title>
  <meta name="description" content="View full product details, ratings, and nutritional notes. Add to cart with custom quantity." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/global.css" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/components.css" />
    <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>
<body data-page="menu">
  <c:set var="productId" value="${not empty param.id ? param.id : 1}" />

  <!-- SHARED HEADER -->
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">

        <div class="product-spotlight" data-product-page id="product-page">

          <article class="product-detail-card reveal-up">
            <div class="product-spotlight__media">

              <img src="/images/${monAn.img}"
                   alt="Jollibug product" />
            </div>
          </article>

          <!-- Product details slots -->
          <article class="product-detail-card reveal-up">
            <div class="stack">
              <h1 class="page-title">${product.tenMon}</h1>
              <h1 class="page-title" data-product-name id="product-name">${monAn.tenMon}</h1>
              <p class="lead" data-product-description id="product-description">${monAn.moTa}</p>

              <div class="product-specs">
                <article class="product-spec">
                  <span class="muted">Guest rating</span>
                  <strong data-product-rating id="product-rating">0</strong>
                </article>
                <article class="product-spec">
                  <span class="muted">Danh mục</span>
                  <strong data-product-category id="product-category">${monAn.danhMuc.tenDM}</strong>
                </article>
              </div>

              <div class="price-row">
                <span class="price" data-product-price id="product-price">
                  <fmt:formatNumber value="${monAn.gia}" type="number" /> đ
                </span>
              </div>

              <!-- SECTION -->

              <div class="cluster">
                <a class="btn btn-outline" href="/menu">Quay lại</a>
                <c:if test="${not empty sessionScope.user}">
                  <form method="post" action="/addCart" >
                    <input type="hidden" name="productID" value="${monAn.maMon}">
                    <button class="btn btn-primary" type="submit">+ Thêm</button>
                  </form>
                </c:if>
              </div>
            </div>
          </article>

        </div><!-- /data-product-page -->
      </div>
    </section>

  </main>

  <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp"/>

  </body>
</html>





