package vn.fastfood.util;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.entity.User;

public class SessionUtil {
    // Kiểm tra có session và có trong danh sách user trong session k 
    public static boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute("user") != null;
    }
    
    // Lấy người dùng | ép kiểu vì getAttribute trả về kiểu Object
    public static User getCurrentUser(HttpSession session) {
        if (session == null) return null;
        return (User) session.getAttribute("user");
    }
    
    public static String getUserRole(HttpSession session) {
        if (session == null) return null;
        return (String) session.getAttribute("userRole");
    }
    
    public static boolean isAdmin(HttpSession session) {
        String role = getUserRole(session);
        return "1".equals(role);
    }

    public static boolean isClient(HttpSession session) {
        String role = getUserRole(session);
        return "2".equals(role);
    }
    
    public static boolean isStaff(HttpSession session) {
        String role = getUserRole(session);
        return "3".equals(role);
    }
    
        public static boolean isManager(HttpSession session) {
        String role = getUserRole(session);
        return "4".equals(role);
    }

    public static boolean isManagerOrStaff(HttpSession session) {
        String role = getUserRole(session);
        return "4".equals(role) || "3".equals(role);
    }
    
}
