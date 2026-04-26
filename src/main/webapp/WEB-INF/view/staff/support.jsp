<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Staff - Customer Support</title>
  <meta name="description" content="Jollibug Staff portal - receive support tickets, chat with customers, and view their purchase history." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
  <link rel="stylesheet" href="/css/admin.css" />
  <link rel="stylesheet" href="/css/staff.css" />
</head>

<!--
  data-admin-role="staff"    -> Spring Security path guard
  data-admin-page="support"  -> read by support.js
-->
<body data-admin-role="staff" data-admin-page="support">

  <div class="admin-shell admin-body" data-staff-support-root>

    <jsp:include page="layout/sidebar.jsp" />

    <!-- SECTION -->
    <main class="admin-main">

      <!-- Topbar -->
      <jsp:include page="layout/topbar.jsp" />

      <!-- SECTION -->
      
      

    </main>
  </div><!-- /data-staff-support-root -->


  <!-- Toast stack -->
  <div class="toast-stack" data-admin-toast-stack id="admin-toast-stack"></div>
  <script src="js/client/support.js" defer></script>
  </body>
</html>
