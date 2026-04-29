<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Gửi khiếu nại</title>
  <meta name="description" content="Gửi khiếu nại đơn hàng cho Jollibug." />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />

  <link rel="stylesheet" href="css/global.css" />
  <link rel="stylesheet" href="css/components.css" />
  <link rel="stylesheet" href="css/client/profile.css" />
</head>
<body data-page="complaint">
  <jsp:include page="layout/header.jsp" />

  <main class="complaint-page">
    <section class="section">
      <div class="container container--account-wide">
        <div class="page-intro" style="margin-bottom: 1.5rem;">
          <h1 class="section-title">Gửi khiếu nại đơn hàng</h1>
        </div>

        <div class="profile-content">
            <div class="complaint-alert">Đã gửi khiếu nại thành công. Bộ phận hỗ trợ sẽ liên hệ lại sớm.</div>

            <div class="profile-grid">
              <label class="profile-field">
                <span>Mã đơn hàng</span>
                <input type="text" name="orderCode" placeholder="VD: JB20260427" required />
              </label>

              <label class="profile-field complaint-field">
                <span>Ảnh minh họa</span>
                <input type="file" name="evidenceImage" accept="image/*" />
              </label>
            </div>

            <label class="profile-field complaint-field">
              <span>Mô tả khiếu nại</span>
              <textarea id="complaintDescription" name="complaintDescription" rows="6" placeholder="Mô tả chi tiết vấn đề bạn gặp phải..." required></textarea>
            </label>

            <div class="profile-actions">
              <button class="profile-submit" type="submit">Gửi khiếu nại</button>
            </div>
        </div>
      </div>
    </section>
  </main>

  <jsp:include page="layout/footer.jsp" />

  <script>
    (function () {
      var textarea = document.getElementById('complaintDescription');
      if (!textarea) {
        return;
      }

      var resizeTextarea = function () {
        textarea.style.height = 'auto';
        textarea.style.height = textarea.scrollHeight + 'px';
      };

      textarea.addEventListener('input', resizeTextarea);
      window.addEventListener('load', resizeTextarea);
      resizeTextarea();
    })();
  </script>
</body>
</html>