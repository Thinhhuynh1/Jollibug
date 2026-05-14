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
        Long currentRevenue = donHangRepository.sumRevenue(from, to);
        stats.put("totalRevenue", currentRevenue != null ? currentRevenue : 0L);
        stats.put("avgOrderValue", donHangRepository.avgOrderValue(from, to));

        // So sánh với kỳ trước
        LocalDateTime[] prevRange = calculatePreviousRange(period);
        Long prevRevenue = donHangRepository.sumRevenue(prevRange[0], prevRange[1]);
        stats.put("prevTotalRevenue", prevRevenue != null ? prevRevenue : 0L);
        stats.put("revenueGrowth", calculateGrowth(currentRevenue, prevRevenue));

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

        // Doanh thu nhanh
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDateTime.now();
        stats.put("revenueToday", donHangRepository.sumRevenue(todayStart, todayEnd));
        stats.put("revenueMonth", donHangRepository.sumRevenue(LocalDate.now().withDayOfMonth(1).atStartOfDay(), todayEnd));

        return stats;
    }

    // ==================== THỐNG KÊ ĐƠN HÀNG ====================
    public Map<String, Object> getOrderStats(String period) {
        LocalDateTime from = calculateFrom(period);
        LocalDateTime to = LocalDateTime.now();

        Map<String, Object> stats = new HashMap<>();
        Long currentOrders = donHangRepository.countByDateRange(from, to);
        stats.put("totalOrders", currentOrders != null ? currentOrders : 0L);

        // So sánh với kỳ trước
        LocalDateTime[] prevRange = calculatePreviousRange(period);
        Long prevOrders = donHangRepository.countByDateRange(prevRange[0], prevRange[1]);
        stats.put("prevTotalOrders", prevOrders != null ? prevOrders : 0L);
        stats.put("ordersGrowth", calculateGrowth(currentOrders, prevOrders));

        // Đơn theo trạng thái
        List<Object[]> rawByStatus = donHangRepository.countGroupByStatus(from, to);
        Map<String, Long> ordersByStatus = new LinkedHashMap<>();
        for (Object[] row : rawByStatus) {
            ordersByStatus.put((String) row[0], ((Number) row[1]).longValue());
        }
        stats.put("ordersByStatus", ordersByStatus);

        // Đơn theo ngày
        List<Object[]> rawByDay = donHangRepository.countByDay(from, to);
        List<Map<String, Object>> ordersByDay = new ArrayList<>();
        for (Object[] row : rawByDay) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("date", row[0].toString());
            entry.put("count", ((Number) row[1]).longValue());
            ordersByDay.add(entry);
        }
        stats.put("ordersByDay", ordersByDay);

        stats.put("pending", donHangRepository.countByTrangThai("PENDING"));
        stats.put("delivered", donHangRepository.countByTrangThai("DELIVERED"));
        stats.put("avgOrderValue", donHangRepository.avgOrderValue(from, to));

        // Recent orders
        List<vn.fastfood.entity.DonHang> recentOrderEntities = donHangRepository.findRecentOrders(PageRequest.of(0, 10));
        List<Map<String, Object>> recentOrders = new ArrayList<>();
        for (vn.fastfood.entity.DonHang order : recentOrderEntities) {
            Map<String, Object> item = new HashMap<>();
            item.put("maDH", order.getMaDH());
            item.put("tongTien", order.getTongTien());
            item.put("trangThai", order.getTrangThai());
            item.put("ngayDat", order.getNgayDat() != null ? order.getNgayDat().toString() : null);
            item.put("hoTen", order.getUser() != null ? order.getUser().getHoTen() : "Khách vãng lai");
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
        Long currentNewCustomers = userRepository.countNewCustomers(from, to);
        stats.put("newCustomers", currentNewCustomers != null ? currentNewCustomers : 0L);

        // So sánh với kỳ trước
        LocalDateTime[] prevRange = calculatePreviousRange(period);
        Long prevNewCustomers = userRepository.countNewCustomers(prevRange[0], prevRange[1]);
        stats.put("prevNewCustomers", prevNewCustomers != null ? prevNewCustomers : 0L);
        stats.put("customersGrowth", calculateGrowth(currentNewCustomers, prevNewCustomers));

        stats.put("totalCustomers", userRepository.count("CLIENT", null));
        stats.put("totalActiveCustomers", userRepository.countActiveCustomers());

        // Top customers
        List<Object[]> rawTopCustomers = donHangRepository.findTopCustomers(PageRequest.of(0, 10));
        List<Map<String, Object>> topCustomers = new ArrayList<>();
        for (Object[] row : rawTopCustomers) {
            Map<String, Object> entry = new HashMap<>();
            vn.fastfood.entity.User user = (vn.fastfood.entity.User) row[0];
            entry.put("hoTen", user.getHoTen());
            entry.put("totalSpent", ((Number) row[1]).longValue());
            entry.put("orderCount", ((Number) row[2]).longValue());
            topCustomers.add(entry);
        }
        stats.put("topCustomers", topCustomers);

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

    private LocalDateTime[] calculatePreviousRange(String period) {
        LocalDateTime from = calculateFrom(period);
        LocalDateTime to = from;
        LocalDateTime prevFrom = switch (period) {
            case "today" -> from.minusDays(1);
            case "week" -> from.minusWeeks(1);
            case "month" -> from.minusMonths(1);
            case "year" -> from.minusYears(1);
            default -> from.minusMonths(1);
        };
        return new LocalDateTime[]{prevFrom, to};
    }

    private double calculateGrowth(Long current, Long previous) {
        if (previous == null || previous == 0) {
            return current != null && current > 0 ? 100.0 : 0.0;
        }
        if (current == null) current = 0L;
        return ((double) (current - previous) / previous) * 100;
    }
}
