<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Token</title>
  <meta name="description" content="Your authentication token has been generated successfully." />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/global.css" />
  <link rel="stylesheet" href="/webapp/resources/shared/css/components.css" />
  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
</head>
<body data-page="token">

  <!-- SHARED HEADER -->
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container" style="width: min(700px, calc(100% - 1.5rem * 2));">
        <article class="auth-panel reveal-up">
          <div class="auth-panel__content">
            <div class="page-intro">
              <h1 class="page-intro__heading">Welcome, ${user.hoTen}!</h1>
              <p class="page-intro__text">Your authentication token has been generated successfully.</p>
            </div>

            <div style="background-color: #f5f5f5; padding: 20px; border-radius: 8px; margin-bottom: 20px;">
              <h3 style="margin-top: 0; color: #333;">Your Authentication Token:</h3>
              <div style="position: relative; background-color: white; padding: 15px; border-radius: 4px; border: 1px solid #ddd; word-break: break-all;">
                <code id="tokenValue">${token}</code>
              </div>
              <button onclick="copyToken()" style="margin-top: 10px; padding: 10px 20px; background-color: #fa4549; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: 600;">
                Copy Token
              </button>
            </div>

            <div style="background-color: #f0f8ff; padding: 15px; border-radius: 4px; border-left: 4px solid #0066cc; margin-bottom: 20px;">
              <h4 style="margin-top: 0; color: #0066cc;">User Information:</h4>
              <ul style="margin: 0; padding-left: 20px;">
                <li><strong>Name:</strong> ${user.hoTen}</li>
                <li><strong>Email:</strong> ${user.email}</li>
                <li><strong>Phone:</strong> ${user.sdt}</li>
                <li><strong>Role:</strong> ${user.vaiTro.tenVT}</li>
              </ul>
            </div>

            <div style="background-color: #fff3cd; padding: 15px; border-radius: 4px; border-left: 4px solid #ffc107; margin-bottom: 20px;">
              <h4 style="margin-top: 0; color: #856404;">⚠️ Security Notice:</h4>
              <ul style="margin: 0; padding-left: 20px; font-size: 14px;">
                <li>Keep this token secure and do not share it with anyone</li>
                <li>This token will expire in 24 hours</li>
                <li>Include this token in the Authorization header of API requests</li>
                <li>Format: <code>Bearer &lt;your-token&gt;</code></li>
              </ul>
            </div>

            <div style="display: flex; gap: 10px; justify-content: center;">
              <a href="/" style="padding: 12px 30px; background-color: #fa4549; color: white; text-decoration: none; border-radius: 4px; font-weight: 600; display: inline-block;">
                Go to Homepage
              </a>
              <a href="/login" style="padding: 12px 30px; background-color: #ddd; color: #333; text-decoration: none; border-radius: 4px; font-weight: 600; display: inline-block;">
                Back to Login
              </a>
            </div>
          </div>
        </article>
      </div>
    </section>
  </main>

  <!-- SHARED FOOTER -->
  <jsp:include page="layout/footer.jsp"/>

  <script>
    function copyToken() {
      const tokenValue = document.getElementById('tokenValue').innerText;
      navigator.clipboard.writeText(tokenValue).then(() => {
        alert('Token copied to clipboard!');
      }).catch(err => {
        console.error('Failed to copy token:', err);
      });
    }
  </script>
</body>
</html>
