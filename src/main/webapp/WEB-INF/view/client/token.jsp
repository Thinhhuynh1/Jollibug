<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jollibug | Mã truy cập</title>
  <meta name="description" content="Hiển thị mã truy cập tài khoản" />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/global.css" />
  <link rel="stylesheet" href="/css/components.css" />
</head>
<body data-page="token">
  <jsp:include page="layout/header.jsp" />

  <main class="page-shell">
    <section class="section">
      <div class="container" style="width: min(700px, calc(100% - 3rem));">
        <article class="auth-panel reveal-up">
          <div class="auth-panel__content">
            <div class="page-intro">
              <h1 class="page-intro__heading">Xin chào ${user.hoTen}</h1>
              <p class="page-intro__text">Mã truy cập của bạn đã được tạo thành công</p>
            </div>

            <div style="background-color: #f5f5f5; padding: 20px; border-radius: 8px; margin-bottom: 20px;">
              <h3 style="margin-top: 0; color: #333;">Mã truy cập</h3>
              <div style="position: relative; background-color: white; padding: 15px; border-radius: 4px; border: 1px solid #ddd; word-break: break-all;">
                <code id="tokenValue">${token}</code>
              </div>
              <button onclick="copyToken()" style="margin-top: 10px; padding: 10px 20px; background-color: #fa4549; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: 600;">
                Sao chép mã
              </button>
            </div>

            <div style="background-color: #f0f8ff; padding: 15px; border-radius: 4px; border-left: 4px solid #0066cc; margin-bottom: 20px;">
              <h4 style="margin-top: 0; color: #0066cc;">Thông tin tài khoản</h4>
              <ul style="margin: 0; padding-left: 20px;">
                <li><strong>Họ tên:</strong> ${user.hoTen}</li>
                <li><strong>Email:</strong> ${user.email}</li>
                <li><strong>Số điện thoại:</strong> ${user.sdt}</li>
                <li><strong>Vai trò:</strong> ${user.vaiTro.tenVT}</li>
              </ul>
            </div>

            <div style="background-color: #fff8e1; padding: 15px; border-radius: 4px; border-left: 4px solid #f59e0b; margin-bottom: 20px;">
              <h4 style="margin-top: 0; color: #b45309;">Lưu ý</h4>
              <ul style="margin: 0; padding-left: 20px; font-size: 14px;">
                <li>Không chia sẻ mã này cho người khác</li>
                <li>Mã sẽ hết hạn sau 24 giờ</li>
                <li>Khi gọi API hãy gửi mã trong header `Authorization`</li>
                <li>Định dạng sử dụng là `Bearer &lt;mã-truy-cập&gt;`</li>
              </ul>
            </div>

            <div style="display: flex; gap: 10px; justify-content: center;">
              <a href="/" style="padding: 12px 30px; background-color: #fa4549; color: white; text-decoration: none; border-radius: 4px; font-weight: 600; display: inline-block;">
                Về trang chủ
              </a>
              <a href="/login" style="padding: 12px 30px; background-color: #ddd; color: #333; text-decoration: none; border-radius: 4px; font-weight: 600; display: inline-block;">
                Quay lại đăng nhập
              </a>
            </div>
          </div>
        </article>
      </div>
    </section>
  </main>

  <jsp:include page="layout/footer.jsp" />

  <script>
    function copyToken() {
      const tokenValue = document.getElementById("tokenValue").innerText;
      navigator.clipboard.writeText(tokenValue).catch((error) => {
        console.error("Không thể sao chép mã", error);
      });
    }
  </script>
</body>
</html>
