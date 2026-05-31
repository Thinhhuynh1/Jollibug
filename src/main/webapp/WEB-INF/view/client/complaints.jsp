<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Gửi khiếu nại</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/components.css'/>" />
</head>
<body data-page="complaints">
  <jsp:include page="layout/header.jsp"/>

  <main class="page-shell">
    <section class="section">
      <div class="container">
        <div class="page-intro">
          <h1 class="section-title">Gửi khiếu nại</h1>
          <p style="color:var(--color-ink-500);">Mô tả vấn đề bạn gặp phải, đội ngũ hỗ trợ sẽ phản hồi qua trang chat.</p>
        </div>

        <div id="complaint-alert" hidden style="margin-bottom:1rem;padding:1rem;border-radius:8px;"></div>

        <div class="create-ticket-form" style="max-width:800px;margin:0 auto;background:var(--color-surface);padding:3rem;border-radius:16px;box-shadow:var(--shadow-md);border:1px solid var(--color-ink-200);">
          <form id="complaint-form">
            <div class="form-group" style="margin-bottom:1.5rem;">
              <label style="display:block;margin-bottom:0.75rem;font-weight:600;">Tiêu đề khiếu nại</label>
              <input type="text" name="tieuDe" required class="form-control" placeholder="Ví dụ: Đơn hàng bị giao trễ"
                     style="width:100%;padding:1rem;border:1px solid var(--color-ink-300);border-radius:8px;"/>
            </div>
            <div class="form-group" style="margin-bottom:2rem;">
              <label style="display:block;margin-bottom:0.75rem;font-weight:600;">Nội dung chi tiết</label>
              <textarea name="noiDung" required class="form-control" rows="6" placeholder="Mô tả chi tiết vấn đề..."
                        style="width:100%;padding:1rem;border:1px solid var(--color-ink-300);border-radius:8px;resize:vertical;"></textarea>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%;padding:1rem;">Gửi khiếu nại</button>
          </form>
          <p style="margin-top:1.5rem;text-align:center;font-size:0.9rem;">
            Hoặc <a href="/chat">mở trang chat hỗ trợ</a> để theo dõi phản hồi.
          </p>
        </div>
      </div>
    </section>
  </main>

  <jsp:include page="layout/footer.jsp"/>

  <script>
    (function () {
      var form = document.getElementById("complaint-form");
      var alertBox = document.getElementById("complaint-alert");
      if (!form) return;

      form.addEventListener("submit", function (e) {
        e.preventDefault();
        var tieuDe = form.tieuDe.value.trim();
        var noiDung = form.noiDung.value.trim();
        if (!tieuDe || !noiDung) return;

        fetch("/api/support/complaints", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ tieuDe: tieuDe, noiDung: noiDung })
        })
          .then(function (res) { return res.json(); })
          .then(function (json) {
            alertBox.hidden = false;
            if (json.success) {
              alertBox.style.background = "#ecfdf5";
              alertBox.style.color = "#065f46";
              alertBox.textContent = json.message + " Chuyển sang chat...";
              setTimeout(function () { window.location.href = "/chat"; }, 1500);
            } else {
              alertBox.style.background = "#fef2f2";
              alertBox.style.color = "#991b1b";
              alertBox.textContent = json.message || "Không gửi được khiếu nại.";
            }
          })
          .catch(function () {
            alertBox.hidden = false;
            alertBox.style.background = "#fef2f2";
            alertBox.style.color = "#991b1b";
            alertBox.textContent = "Lỗi kết nối. Vui lòng thử lại.";
          });
      });
    })();
  </script>
</body>
</html>
