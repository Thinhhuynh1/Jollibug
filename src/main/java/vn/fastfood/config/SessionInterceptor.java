package vn.fastfood.config;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);
        String requestURI = request.getRequestURI();

        if (requestURI.equals("/")
                || requestURI.startsWith("/login")
                || requestURI.startsWith("/register")
                || requestURI.startsWith("/forgot-password")
                || requestURI.startsWith("/verify")
                || requestURI.startsWith("/new-password")
                || requestURI.startsWith("/logout")
                || requestURI.startsWith("/api/ai")
                || requestURI.startsWith("/menu")
                || requestURI.startsWith("/product")
                || requestURI.startsWith("/contact")
                || requestURI.startsWith("/resources")
                || requestURI.startsWith("/images")
                || requestURI.startsWith("/css")
                || requestURI.startsWith("/js")) {
            return true;
        }

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("/login");
            return false;
        }

        String userRole = (String) session.getAttribute("userRole");

        if (requestURI.startsWith("/admin") && !"ADMIN".equals(userRole)) {
            response.sendRedirect("/");
            return false;
        }

        if (requestURI.startsWith("/manager") && !("ADMIN".equals(userRole) || "MANAGER".equals(userRole))) {
            response.sendRedirect("/");
            return false;
        }

        if (requestURI.startsWith("/staff")
                && !("ADMIN".equals(userRole) || "MANAGER".equals(userRole) || "STAFF".equals(userRole))) {
            response.sendRedirect("/");
            return false;
        }

        return true;
    }
}
