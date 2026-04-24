<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | About</title>
  <meta name="description" content="Learn about the Jollibug brand mission, design system, and frontend architecture ready for Spring MVC." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/global.css" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/components.css" />
    <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>
<body data-page="about">

  <!-- SHARED HEADER -->
  <jsp:include page="layout/header.jsp"/>

  <!-- SECTION -->
  <main class="page-shell">
    <section class="section">
      <div class="container">
        <div class="page-intro reveal-up">
          <span class="eyebrow">Our Story</span>
          <h1 class="page-title">Jollibug turns everyday comfort food into a polished brand moment.</h1>
          <p class="lead">This concept is built for the premium-fast-food lane: bold colors, round typography, clear hierarchy, and a frontend structure clean enough to evolve into a real ordering platform.</p>
        </div>
        <div class="story-grid">
          <article class="story-block reveal-up">
            <span class="eyebrow">Brand Mission</span>
            <h2 class="section-title">Serve joyful food with a smoother digital experience.</h2>
            <p class="lead">Jollibug is designed around appetite, warmth, and convenience. From browsing to cart to profile management, every step aims to feel quick, friendly, and reassuring.</p>
            <div class="story-stat-list">
              <article class="story-stat"><span class="muted">Brand direction</span><strong>Playful Premium</strong></article>
              <article class="story-stat"><span class="muted">Core promise</span><strong>Fast &amp; Fresh</strong></article>
              <article class="story-stat"><span class="muted">Audience</span><strong>Students + Families</strong></article>
            </div>
          </article>
          <article class="story-block story-highlight reveal-up">
            <img src="/webapp/resources/shared/images/brand-feast.svg" alt="Jollibug illustrated feast"
                 style="max-width:28rem;margin:auto;filter:drop-shadow(0 26px 36px rgba(86,48,17,0.18));" />
          </article>
        </div>
      </div>
    </section>
    <section class="section-tight">
      <div class="container">
        <div class="card-grid">
          <article class="card feature-card reveal-up">
            <div class="feature-card__icon">01</div>
            <h3>Natural visual hierarchy</h3>
            <p class="muted">Rounded headings and readable body copy help the site feel branded without sacrificing clarity.</p>
          </article>
          <article class="card feature-card reveal-up">
            <div class="feature-card__icon">02</div>
            <h3>Shared architecture</h3>
            <p class="muted">Global CSS tokens and modular JS keep every page consistent while staying easy to maintain.</p>
          </article>
          <article class="card feature-card reveal-up">
            <div class="feature-card__icon">03</div>
            <h3>Backend-ready scaffolding</h3>
            <p class="muted">The page markup uses semantic sections and data hooks that can map cleanly to dynamic rendering later.</p>
          </article>
        </div>
      </div>
    </section>
  </main>

  <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp" />


  </body>
</html>





