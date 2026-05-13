package vn.fastfood.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import vn.fastfood.repository.DonHangRepository;
import vn.fastfood.repository.UserRepository;

@Service
public class ThongKeService {

    private final DonHangRepository donHangRepository;
    private final UserRepository userRepository;

    public ThongKeService(DonHangRepository donHangRepository, UserRepository userRepository) {
        this.donHangRepository = donHangRepository;
        this.userRepository = userRepository;
    }

    // ==================== THỐNG KÊ DOANH THU ====================

    public Map<String, Object> getRevenueStats(String period) {
        LocalDateTime from = calculateFrom(period);
        LocalDateTime to = LocalDateTime.now();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", donHangRepository.sumRevenue(from, to));
        stats.put("avgOrderValue", donHangRepository.avgOrderValue(from, to));

        // Doanh thu theo ngày (cho biểu đồ bar/line chart)
        List<Object[]> rawByDay = donHangRepository.revenueByDay(from, to);
        List<Map<String, Object>> revenueByDay = new ArrayList<>();
        for (Object[] row : rawByDay) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("date", row[0].toString());
            entry.put("revenue", ((Number) row[1]).longValue());
            revenueByDay.add(entry);
        }
        stats.put("revenueByDay", revenueByDay);

        // Doanh thu theo tháng (cho năm hiện tại)
        int currentYear = LocalDate.now().getYear();
        List<Object[]> rawByMonth = donHangRepository.revenueByMonth(currentYear);
        List<Map<String, Object>> revenueByMonth = new ArrayList<>();
        for (Object[] row : rawByMonth) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("month", ((Number) row[0]).intValue());
            entry.put("revenue", ((Number) row[1]).longValue());
            revenueByMonth.add(entry);
        }
        stats.put("revenueByMonth", revenueByMonth);

        // Doanh thu hôm nay
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDateTime.now();
        stats.put("revenueToday", donHangRepository.sumRevenue(todayStart, todayEnd));

        // Doanh thu tuần này
        LocalDateTime weekStart = LocalDate.now().minusWeeks(1).atStartOfDay();
        stats.put("revenueWeek", donHangRepository.sumRevenue(weekStart, todayEnd));

        // Doanh thu tháng này
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        stats.put("revenueMonth", donHangRepository.sumRevenue(monthStart, todayEnd));

        return stats;
    }

    // ==================== THỐNG KÊ ĐƠN HÀNG ====================

    public Map<String, Object> getOrderStats(String period) {
        LocalDateTime from = calculateFrom(period);
        LocalDateTime to = LocalDateTime.now();

        Map<String, Object> stats = new HashMap<>();

        // Tổng đơn trong khoảng thời gian
        stats.put("totalOrders", donHangRepository.countByDateRange(from, to));

        // Đơn theo trạng thái (cho biểu đồ donut)
        List<Object[]> rawByStatus = donHangRepository.countGroupByStatus(from, to);
        Map<String, Long> ordersByStatus = new LinkedHashMap<>();
        for (Object[] row : rawByStatus) {
            ordersByStatus.put((String) row[0], ((Number) row[1]).longValue());
        }
        stats.put("ordersByStatus", ordersByStatus);

        // Đơn theo ngày (cho biểu đồ line chart)
        List<Object[]> rawByDay = donHangRepository.countByDay(from, to);
        List<Map<String, Object>> ordersByDay = new ArrayList<>();
        for (Object[] row : rawByDay) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("date", row[0].toString());
            entry.put("count", ((Number) row[1]).longValue());
            ordersByDay.add(entry);
        }
        stats.put("ordersByDay", ordersByDay);

        // Đếm nhanh theo từng trạng thái
        stats.put("pending", donHangRepository.countByTrangThai("PENDING"));
        stats.put("confirmed", donHangRepository.countByTrangThai("CONFIRMED"));
        stats.put("shipping", donHangRepository.countByTrangThai("SHIPPING"));
        stats.put("delivered", donHangRepository.countByTrangThai("DELIVERED"));
        stats.put("cancelled", donHangRepository.countByTrangThai("CANCELLED"));

        // Giá trị trung bình đơn hàng
        stats.put("avgOrderValue", donHangRepository.avgOrderValue(from, to));

        // Đơn hàng gần đây nhất (10 đơn)
        List<vn.fastfood.entity.DonHang> recentOrderEntities = donHangRepository.findRecentOrders(PageRequest.of(0, 10));
        List<Map<String, Object>> recentOrders = new ArrayList<>();
        for (vn.fastfood.entity.DonHang order : recentOrderEntities) {
            Map<String, Object> item = new HashMap<>();
            item.put("maDH", order.getMaDH());
            item.put("tongTien", order.getTongTien());
            item.put("trangThai", order.getTrangThai());
            item.put("ngayDat", order.getNgayDat() != null ? order.getNgayDat().toString() : null);
            if (order.getUser() != null) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("hoTen", order.getUser().getHoTen());
                userInfo.put("email", order.getUser().getEmail());
                userInfo.put("sdt", order.getUser().getSdt());
                item.put("user", userInfo);
            } else {
                item.put("user", Map.of("hoTen", "Không xác định", "email", "", "sdt", ""));
            }
            recentOrders.add(item);
        }
        stats.put("recentOrders", recentOrders);

        return stats;
    }

    // ==================== THỐNG KÊ KHÁCH HÀNG ====================

    public Map<String, Object> getCustomerStats(String period) {
        LocalDateTime from = calculateFrom(period);
        LocalDateTime to = LocalDateTime.now();

        Map<String, Object> stats = new HashMap<>();

        // Tổng khách hàng active
        stats.put("totalActiveCustomers", userRepository.countActiveCustomers());

        // Khách hàng mới trong khoảng thời gian
        stats.put("newCustomers", userRepository.countNewCustomers(from, to));

        // Khách hàng có đặt đơn trong khoảng thời gian
        stats.put("orderingCustomers", donHangRepository.countDistinctCustomers(from, to));

        // Top 10 khách hàng theo chi tiêu
        List<Object[]> rawTopCustomers = donHangRepository.findTopCustomers(PageRequest.of(0, 10));
        List<Map<String, Object>> topCustomers = new ArrayList<>();
        for (Object[] row : rawTopCustomers) {
            Map<String, Object> entry = new HashMap<>();
            vn.fastfood.entity.User user = (vn.fastfood.entity.User) row[0];
            entry.put("hoTen", user.getHoTen());
            entry.put("email", user.getEmail());
            entry.put("sdt", user.getSdt());
            entry.put("totalSpent", ((Number) row[1]).longValue());
            entry.put("orderCount", ((Number) row[2]).longValue());
            topCustomers.add(entry);
        }
        stats.put("topCustomers", topCustomers);

        // Tổng số khách hàng (role = CLIENT)
        stats.put("totalCustomers", userRepository.count("CLIENT", null));

        return stats;
    }

    // ==================== HELPER ====================

    private LocalDateTime calculateFrom(String period) {
        return switch (period) {
            case "today" -> LocalDate.now().atStartOfDay();
            case "week" -> LocalDate.now().minusWeeks(1).atStartOfDay();
            case "month" -> LocalDate.now().minusMonths(1).atStartOfDay();
            case "year" -> LocalDate.now().minusYears(1).atStartOfDay();
            default -> LocalDate.now().minusMonths(1).atStartOfDay();
        };
    }
}
