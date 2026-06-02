package vn.fastfood.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.fastfood.entity.ChiTietDH;
import vn.fastfood.entity.DiaChi;
import vn.fastfood.entity.DonHang;
import vn.fastfood.entity.MonAn;
import vn.fastfood.entity.User;
import vn.fastfood.model.CheckoutCartItem;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;
import vn.fastfood.repository.ChiTietDHRepository;
import vn.fastfood.repository.DonHangRepository;
import vn.fastfood.repository.MonAnRepository;
import vn.fastfood.repository.UserRepository;

@Service
public class JpaOrderService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDHRepository chiTietDHRepository;
    private final UserRepository userRepository;
    private final MonAnRepository monAnRepository;

    public JpaOrderService(DonHangRepository donHangRepository,
            ChiTietDHRepository chiTietDHRepository,
            UserRepository userRepository,
            MonAnRepository monAnRepository) {
        this.donHangRepository = donHangRepository;
        this.chiTietDHRepository = chiTietDHRepository;
        this.userRepository = userRepository;
        this.monAnRepository = monAnRepository;
    }

    @Transactional
    public long createOrder(long customerId, DiaChi diaChi, BigDecimal totalAmount,
            String ghiChu, List<CheckoutCartItem> items) {
        User user = userRepository.findByMaTK(customerId);
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng.");
        }

        DonHang donHang = new DonHang();
        donHang.setUser(user);
        double total = totalAmount.doubleValue();
        donHang.setTongTienMon(total);
        donHang.setTienGiamGia(0D);
        donHang.setTongTien(total);
        donHang.setTrangThai("PENDING");
        donHang.setDiaChi(diaChi);
        donHang.setGhiChu(ghiChu);
        donHang = donHangRepository.save(donHang);

        long orderId = donHang.getMaDH();
        for (CheckoutCartItem item : items) {
            ChiTietDH chiTiet = new ChiTietDH();
            chiTiet.setMaDH(orderId);
            chiTiet.setMaMon(item.getMaMon());
            chiTiet.setTenMon(item.getTenMon());
            chiTiet.setSoLuong(item.getSoLuong());
            chiTiet.setDonGia((long) item.getDonGia());
            chiTiet.setThanhTien((long) item.getThanhTien());
            chiTietDHRepository.save(chiTiet);
        }

        return orderId;
    }

    public List<Order> getOrdersByCustomerId(long customerId) {
        return donHangRepository.findByUser_MaTKOrderByNgayDatDesc(customerId).stream()
                .map(this::toOrder)
                .collect(Collectors.toList());
    }

    public Order getOrderById(long orderId, long customerId) {
        return donHangRepository.findById(orderId)
                .filter(order -> order.getUser() != null && order.getUser().getMaTK() == customerId)
                .map(this::toOrder)
                .orElse(null);
    }

    public Order getOrderByIdForStaff(long orderId) {
        return donHangRepository.findById(orderId)
                .map(this::toOrder)
                .orElse(null);
    }

    public List<OrderItem> getOrderItemsByOrderId(long orderId) {
        List<OrderItem> items = new ArrayList<>();
        for (ChiTietDH chiTiet : chiTietDHRepository.findByMaDH(orderId)) {
            OrderItem item = new OrderItem();
            item.setMaDH(orderId);
            item.setMaMon(chiTiet.getMaMon());
            item.setSoLuong(chiTiet.getSoLuong() != null ? chiTiet.getSoLuong() : 0);
            item.setDonGia(BigDecimal.valueOf(chiTiet.getDonGia() != null ? chiTiet.getDonGia() : 0L));

            MonAn monAn = chiTiet.getMonAn();
            if (monAn == null) {
                monAn = monAnRepository.findProduct(chiTiet.getMaMon());
            }
            if (monAn != null) {
                item.setTenMon(monAn.getTenMon());
            }

            item.setThanhTien(item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong())));
            items.add(item);
        }
        return items;
    }

    public List<Order> getOrdersForStaff(String status, String keyword, String fromDate, String toDate) {
        LocalDate from = parseDate(fromDate);
        LocalDate to = parseDate(toDate);
        String normalizedStatus = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);

        return donHangRepository.findAll().stream()
                .sorted(Comparator.comparing(DonHang::getNgayDat, Comparator.nullsLast(Comparator.reverseOrder())))
                .filter(order -> normalizedStatus.isEmpty()
                        || normalizedStatus.equalsIgnoreCase(order.getTrangThai()))
                .filter(order -> from == null || (order.getNgayDat() != null
                        && !order.getNgayDat().toLocalDate().isBefore(from)))
                .filter(order -> to == null || (order.getNgayDat() != null
                        && !order.getNgayDat().toLocalDate().isAfter(to)))
                .filter(order -> normalizedKeyword.isEmpty() || matchesKeyword(order, normalizedKeyword))
                .map(this::toOrder)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean updateStatus(long orderId, String newStatus) {
        return donHangRepository.findById(orderId)
                .map(order -> {
                    order.setTrangThai(newStatus);
                    donHangRepository.save(order);
                    return true;
                })
                .orElse(false);
    }

    public String getCurrentStatus(long orderId) {
        return donHangRepository.findById(orderId)
                .map(DonHang::getTrangThai)
                .orElse(null);
    }

    private boolean matchesKeyword(DonHang order, String keyword) {
        if (String.valueOf(order.getMaDH()).contains(keyword)) {
            return true;
        }
        if (order.getUser() != null && String.valueOf(order.getUser().getMaTK()).contains(keyword)) {
            return true;
        }
        return order.getGhiChu() != null && order.getGhiChu().toLowerCase(Locale.ROOT).contains(keyword);
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value.trim());
    }

    private Order toOrder(DonHang donHang) {
        Order order = new Order();
        order.setMaDH(donHang.getMaDH());
        if (donHang.getUser() != null) {
            order.setMaTKKH(donHang.getUser().getMaTK());
            order.setTenKhachHang(donHang.getUser().getHoTen());
            order.setSdtKhachHang(donHang.getUser().getSdt());
            order.setEmailKhachHang(donHang.getUser().getEmail());
        }
        if (donHang.getNgayDat() != null) {
            order.setNgayDat(Timestamp.valueOf(donHang.getNgayDat()));
        }
        order.setTrangThaiDon(donHang.getTrangThai());
        DiaChi diaChi = donHang.getDiaChi();
        if (diaChi != null) {
            order.setMaDC(diaChi.getMaDC());
            order.setTenNguoiNhan(diaChi.getTenNguoiNhan());
            order.setSdtNguoiNhan(diaChi.getSdtNguoiNhan());
            order.setDiaChiGiaoHang(formatAddress(diaChi));
        }
        order.setGhiChu(donHang.getGhiChu());

        BigDecimal total = BigDecimal.valueOf(donHang.getTongTien() != null ? donHang.getTongTien() : 0D);
        order.setThanhTien(total);
        order.setTongTienMon(BigDecimal.valueOf(donHang.getTongTienMon() != null ? donHang.getTongTienMon() : 0D));
        order.setTienGiamGia(BigDecimal.valueOf(donHang.getTienGiamGia() != null ? donHang.getTienGiamGia() : 0D));
        order.setMaPT("COD");
        order.setTenPT("Thanh toán khi nhận hàng");
        order.setTrangThaiTT("PENDING");
        return order;
    }

    private String formatAddress(DiaChi diaChi) {
        List<String> parts = new ArrayList<>();
        if (diaChi.getDiaChiCuThe() != null && !diaChi.getDiaChiCuThe().isBlank()) {
            parts.add(diaChi.getDiaChiCuThe().trim());
        }
        if (diaChi.getPhuongXa() != null && !diaChi.getPhuongXa().isBlank()) {
            parts.add(diaChi.getPhuongXa().trim());
        }
        if (diaChi.getQuanHuyen() != null && !diaChi.getQuanHuyen().isBlank()) {
            parts.add(diaChi.getQuanHuyen().trim());
        }
        if (diaChi.getTinhThanh() != null && !diaChi.getTinhThanh().isBlank()) {
            parts.add(diaChi.getTinhThanh().trim());
        }
        return String.join(", ", parts);
    }

    public static boolean isSupportedPaymentMethod(String maPT) {
        if (maPT == null || maPT.isBlank()) {
            return false;
        }
        String normalized = maPT.trim().toUpperCase(Locale.ROOT);
        return normalized.equals("COD")
                || normalized.equals("CREDIT_CARD")
                || normalized.equals("BANK")
                || normalized.equals("EWALLET");
    }

    public static String buildCheckoutNote(String userNote, String maPT, BigDecimal discountAmount) {
        StringBuilder note = new StringBuilder();
        note.append("PTTT: ").append(maPT);
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            note.append("; Giam gia: ").append(discountAmount.toPlainString());
        }
        if (userNote != null && !userNote.isBlank()) {
            note.append("; ").append(userNote.trim());
        }
        return note.toString();
    }
}
