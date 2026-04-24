<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Contact</title>
  <meta name="description" content="Contact the Jollibug team - reach us by email, hotline, or visit our flagship store." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/global.css" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/components.css" />
    <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>
<body data-page="contact">

  <!-- SHARED HEADER -->
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">
        <div class="page-intro reveal-up">
          <span class="eyebrow">Contact Jollibug</span>
          <h1 class="page-title">Talk to the team behind the flavor.</h1>
          <p class="lead">The contact page pairs a structured form with store details and a map-style visual block, giving the website a more credible real-world presence.</p>
        </div>

        <div class="contact-grid">
          <!-- SECTION -->
          <article class="contact-panel reveal-up">
            <div class="contact-info">
              <article class="contact-info__item">
                <strong>Customer Care</strong>
                <p class="muted">hello@Jollibug.vn</p>
              </article>
              <article class="contact-info__item">
                <strong>Hotline</strong>
                <p class="muted">+84 28 5555 8899</p>
              </article>
              <article class="contact-info__item">
                <strong>Main Flagship</strong>
                <p class="muted">88 Flavor Avenue, District 1, Ho Chi Minh City</p>
              </article>
            </div>
            <div class="contact-map">Store Map</div>
          </article>

          <!-- SECTION
          -->
          <form class="contact-panel form-grid reveal-up"
                data-demo-form
                data-success-message="Thanks! The Jollibug team will reach out shortly."
                data-reset-form="true"
                id="contact-form" novalidate>
            <label class="field-label">
              <span>Full name</span>
              <input type="text" name="name" id="contact-name" placeholder="Your full name" required />
            </label>
            <label class="field-label">
              <span>Email</span>
              <input type="email" name="email" id="contact-email" placeholder="you@example.com" required />
            </label>
            <label class="field-label">
              <span>Phone</span>
              <input type="tel" name="phone" id="contact-phone" placeholder="+84 ..." required />
            </label>
            <label class="field-label">
              <span>How can we help?</span>
              <textarea name="message" id="contact-message" placeholder="Tell us about your request" required></textarea>
            </label>
            <button class="btn btn-primary" type="submit">Send message</button>
          </form>
        </div>
      </div>
    </section>
  </main>

  <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp" />
  
  </body>
</html>





