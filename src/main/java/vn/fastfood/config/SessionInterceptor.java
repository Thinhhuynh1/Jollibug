package vn.fastfood.config;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SessionInterceptor implements HandlerInterceptor {

    // override preHandle | func này sẽ ktra xem có req được k | check xem có quyền
    // không
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // Chống cache trang để khi logout người dùng không thể dùng nút Back của trình
        // duyệt để xem trang yêu cầu đăng nhập
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // lấy session | false là vì nếu k có session thì sẽ trả về null thay vì tạo
        // session mới
        HttpSession session = request.getSession(false);
        String requestURI = request.getRequestURI();

        // Những request k cần đăng nhập
        if (requestURI.equals("/")
                || requestURI.startsWith("/login")
                || requestURI.startsWith("/register")
                || requestURI.startsWith("/forgot-password")
                || requestURI.startsWith("/logout")
                || requestURI.startsWith("/menu")
                || requestURI.startsWith("/product")
                || requestURI.startsWith("/about")
                || requestURI.startsWith("/contact")
                || requestURI.startsWith("/resources")) {
            return true;
        }

        // Tất cả request khác PHẢI có session hợp lệ
        if (session == null || session.getAttribute("user") == null)

        {
            response.sendRedirect("/login");
            return false;
        }

        String userRole = (String) session.getAttribute("userRole");

        // Check Admin - chỉ role 1
        if (requestURI.startsWith("/admin")) {
            if (!"1".equals(userRole)) {
                response.sendRedirect("/");
                return false;
            }
        }

        // Check Manager - chỉ role 4
        if (requestURI.startsWith("/manager")) {
            if (!("4".equals(userRole))) {
                response.sendRedirect("/");
                return false;
            }
        }

        // Check Staff - role 3
        if (requestURI.startsWith("/staff")) {
            if (!"3".equals(userRole)) {
                response.sendRedirect("/");
                return false;
            }
        }

        // Tất cả request khác được phép đi tiếp | Role 2 - Client
        // Role 3 - Staff | Role 4 - Manager
        return true;
    }
}
