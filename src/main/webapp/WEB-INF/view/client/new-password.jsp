<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>Jollibug | Đặt lại mật khẩu</title>
                <meta name="description" content="Đặt lại mật khẩu tài khoản Jollibug của bạn." />
                <link rel="preconnect" href="https://fonts.googleapis.com" />
                <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
                <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap"
                    rel="stylesheet" />
                <link rel="stylesheet" href="css/global.css" />
                <link rel="stylesheet" href="css/components.css" />
                <link rel="stylesheet" href="css/client/profile.css">


            </head>

            <body data-page="login">

                <jsp:include page="layout/header.jsp" />

                <main class="page-shell">
                    <section class="section">
                        <div class="container" style="width: min(700px, calc(100% - 1.5rem * 2));">
                            <article class="auth-panel reveal-up">
                                <div class="auth-panel__content">
                                    <div class="page-intro" style="margin-bottom:0;">
                                        <h2 class="section-title">Đặt lại mật khẩu</h2>
                                    </div>
                                    <c:if test="${not empty error}">
                                        <div
                                            style="background-color: #fbfbfb; border: 1px solid #ffebed; color: #721c24; padding: 12px; border-radius: 4px; margin-bottom: 15px; margin-top: 15px;">
                                            ${error}
                                        </div>
                                    </c:if>
                                    <form class="floating-grid" method="POST">
                                        <div class="floating-field">
                                            <input id="new-password" name="newPassword" type="password" placeholder=" "
                                                required minlength="6" />
                                            <label for="new-password">Mật khẩu mới</label>
                                        </div>

                                        <div class="floating-field">
                                            <input id="confirm-password" name="confirmPassword" type="password"
                                                placeholder=" " required minlength="6" />
                                            <label for="confirm-password">Xác nhận mật khẩu mới</label>
                                        </div>
                                        <button class="btn btn-primary btn-block" type="submit">Đổi mật khẩu</button>
                                    </form>

                                </div>
                            </article>
                        </div>
                    </section>
                </main>
                <!-- SHARED FOOTER -->
                <jsp:include page="layout/footer.jsp" />
            </body>

            </html>
