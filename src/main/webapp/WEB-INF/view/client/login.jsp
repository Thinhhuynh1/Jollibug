<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Sign In</title>
  <meta name="description" content="Sign in to your Jollibug account to access saved addresses, order history, and faster reordering." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/global.css" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/components.css" />
    <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/admin.css" />
</head>
<body data-page="login">

  <!-- SHARED HEADER -->
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">
        

          <!-- SECTION -->


          <!-- Form panel -->
          <article class="auth-panel reveal-up">
            <div class="auth-panel__content">
              <div class="page-intro" style="margin-bottom:0;">
                <span class="eyebrow">Sign In</span>
                <h2 class="section-title">Access your Jollibug account</h2>
              </div>
              <div class="social-grid">
                <button class="btn btn-outline social-btn" type="button">Continue with Google</button>
                <button class="btn btn-outline social-btn" type="button">Continue with Facebook</button>
              </div>
              <div class="divider">or continue with email</div>
              <!--
                [data-demo-form] -> main.js bindDemoForms() attaches a submit listener.
                data-success-message -> toast message on success.
                Future Spring MVC: action="/login" method="post" + remove data-demo-form.
              -->
              <form class="floating-grid" data-demo-form
                    data-success-message="Signed in demo successfully."
                    id="login-form" novalidate>
                <div class="floating-field">
                  <input id="login-email" name="email" type="email" placeholder=" " required />
                  <label for="login-email">Email address</label>
                </div>
                <div class="floating-field">
                  <input id="login-password" name="password" type="password" placeholder=" " required minlength="6" />
                  <label for="login-password">Password</label>
                </div>
                <button class="btn btn-primary btn-block" type="submit">Sign in</button>
              </form>
              <div class="card-actions">
                <a href="/register">Create new account</a>
              </div>
            </div>
          </article>


      </div>
    </section>
  </main>

  <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp" />

<script src="js/client/nav.js" defer></script>
  <script src="js/client/store.js"></script>
<script src="js/client/main.js" defer></script>
  </body>
</html>





