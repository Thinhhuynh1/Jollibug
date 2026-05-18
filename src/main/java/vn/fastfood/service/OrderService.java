package vn.fastfood.service;

import vn.fastfood.dao.OrderDAO;
import vn.fastfood.dto.ReorderResponse;
import vn.fastfood.model.CartItem;
import vn.fastfood.dto.OrderStatusHistoryResponse;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;
import vn.fastfood.model.OrderStatusHistory;
import vn.fastfood.model.ReorderCartItemCandidate;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_SHIPPING = "SHIPPING";
    private static final String STATUS_DELIVERED = "DELIVERED";
    private static final String STATUS_RECEIVED = "RECEIVED";
    private static final String STATUS_CANCEL_REQUESTED = "CANCEL_REQUESTED";
    private static final String STATUS_CANCELLED = "CANCELLED";

    private final OrderDAO orderDAO = new OrderDAO();

    public List<Order> getOrdersByCustomerId(long customerId) {
        return orderDAO.getOrdersByCustomerId(customerId);
    }
    
    public Order getOrderById(long orderId, long customerId) {
        return orderDAO.getOrderById(orderId, customerId);
    }

    public List<OrderItem> getOrderItemsByOrderId(long orderId) {
        return orderDAO.getOrderItemsByOrderId(orderId);
    }

    public boolean requestCancelOrder(long orderId, long customerId) {
        return requestCancelOrder(orderId, customerId, null);
    }

    public boolean requestCancelOrder(long orderId, long customerId, String cancelReason) {
        Order dh = orderDAO.getOrderById(orderId, customerId);
        if (dh == null) {
            System.out.println("Không tìm thấy đơn hàng hoặc đơn không thuộc khách hàng này.");
            return false;
        }

        String status = normalizeStatus(dh.getTrangThaiDon());
        String reason = normalizeReason(cancelReason);

        if (STATUS_PENDING.equals(status)) {
            return updateCustomerStatus(orderId, customerId, status, STATUS_CANCELLED, reason);
        }

        if (STATUS_CONFIRMED.equals(status)) {
            return updateCustomerStatus(orderId, customerId, status, STATUS_CANCEL_REQUESTED, reason);
        }
        
        System.out.println("Đơn hàng ở trạng thái " + status + " nên không thể hủy.");
        return false;
    }
    
    public boolean confirmReceived(long orderId, long customerId) {
        Order dh = orderDAO.getOrderById(orderId, customerId);

        if (dh == null) {
            System.out.println("Không tìm thấy đơn hàng hoặc đơn không thuộc khách hàng này.");
            return false;
        }

        String status = normalizeStatus(dh.getTrangThaiDon());

        if (!STATUS_DELIVERED.equals(status)) {
            System.out.println("Chỉ có thể xác nhận đã nhận hàng khi đơn đã giao.");
            return false;
        }

        return updateCustomerStatus(orderId, customerId, status, STATUS_RECEIVED, null);
    }
    
    public boolean canReviewOrder(long orderId, long customerId) {
        Order dh = orderDAO.getOrderById(orderId, customerId);

        if(dh == null)
            return false;

        String status = normalizeStatus(dh.getTrangThaiDon());
        return STATUS_RECEIVED.equals(status);
    }

    public boolean reorder(long orderId, long customerId) {
        Order order = orderDAO.getOrderById(orderId, customerId);

        if (order == null) {
            System.out.println("[REORDER] Order not found or not owned by customerId=" + customerId);
            return false;
        }

        return orderDAO.addOrderItemsToCart(orderId, customerId) > 0;
    }

    public ReorderResponse prepareReorderCheckout(long orderId, long customerId, HttpSession session) {
        if (session == null) {
            return new ReorderResponse(false, "Phiên làm việc không hợp lệ. Vui lòng đăng nhập lại.", null, null);
        }

        Order order = orderDAO.getOrderById(orderId, customerId);

        if (order == null) {
            return new ReorderResponse(false, "Không tìm thấy đơn hàng hoặc đơn không thuộc khách hàng hiện tại.", null, null);
        }

        String status = normalizeStatus(order.getTrangThaiDon());

        if (!STATUS_CANCELLED.equals(status) && !STATUS_RECEIVED.equals(status)) {
            return new ReorderResponse(false, "Chỉ có thể đặt lại đơn hàng đã hủy hoặc đã nhận hàng.", null, null);
        }

        List<ReorderCartItemCandidate> candidates = orderDAO.getReorderCartItemCandidates(orderId);
        List<CartItem> cartItems = new ArrayList<>();
        List<String> skippedItems = new ArrayList<>();

        for (ReorderCartItemCandidate candidate : candidates) {
            String itemName = getReorderItemName(candidate);

            if (!candidate.isProductExists()) {
                skippedItems.add(itemName + " không còn tồn tại.");
                continue;
            }

            if (!candidate.isAvailable()) {
                skippedItems.add(itemName + " đã ngừng bán.");
                continue;
            }

            if (candidate.getAvailableQuantity() <= 0) {
                skippedItems.add(itemName + " đã hết hàng.");
                continue;
            }

            if (candidate.getCurrentPrice() == null || candidate.getCurrentPrice().compareTo(BigDecimal.ZERO) <= 0) {
                skippedItems.add(itemName + " chưa có giá bán hợp lệ.");
                continue;
            }

            int quantity = candidate.getRequestedQuantity();

            if (candidate.getAvailableQuantity() < quantity) {
                quantity = (int) Math.min(candidate.getAvailableQuantity(), Integer.MAX_VALUE);
                skippedItems.add(itemName + " chỉ còn " + quantity + " phần, đã điều chỉnh số lượng.");
            }

            if (quantity <= 0) {
                skippedItems.add(itemName + " không còn số lượng hợp lệ để đặt lại.");
                continue;
            }

            CartItem cartItem = new CartItem();
            cartItem.setMaMon(candidate.getMaMon());
            cartItem.setTenMon(itemName);
            cartItem.setSoLuong(quantity);
            cartItem.setDonGia(candidate.getCurrentPrice());
            cartItem.setThanhTien(candidate.getCurrentPrice().multiply(BigDecimal.valueOf(quantity)));
            cartItem.setImageUrl(candidate.getImageUrl());

            cartItems.add(cartItem);
        }

        if (cartItems.isEmpty()) {
            cartItems = buildCartFromOrderSnapshot(orderId);

            if (!cartItems.isEmpty()) {
                skippedItems.add("Không kiểm tra được trạng thái hiện tại của một số món, hệ thống tạm dùng thông tin trong đơn cũ.");
            }
        }

        if (cartItems.isEmpty()) {
            return new ReorderResponse(
                    false,
                    "Không còn món nào hợp lệ để đặt lại.",
                    cartItems,
                    skippedItems
            );
        }

        session.setAttribute("cart", cartItems);

        String message = skippedItems.isEmpty()
                ? "Đã tạo lại giỏ hàng từ đơn cũ. Bạn có thể kiểm tra và thanh toán ở bước tiếp theo."
                : "Đã tạo lại giỏ hàng với các món còn hợp lệ. Một số món đã được bỏ qua hoặc điều chỉnh.";

        return new ReorderResponse(true, message, cartItems, skippedItems);
    }

    private List<CartItem> buildCartFromOrderSnapshot(long orderId) {
        List<OrderItem> orderItems = orderDAO.getOrderItemsByOrderId(orderId);
        List<CartItem> cartItems = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            if (orderItem.getSoLuong() <= 0 || orderItem.getDonGia() == null) {
                continue;
            }

            CartItem cartItem = new CartItem();
            cartItem.setMaMon(orderItem.getMaMon());
            cartItem.setTenMon(orderItem.getTenMon());
            cartItem.setSoLuong(orderItem.getSoLuong());
            cartItem.setDonGia(orderItem.getDonGia());
            cartItem.setThanhTien(orderItem.getDonGia().multiply(BigDecimal.valueOf(orderItem.getSoLuong())));
            cartItem.setImageUrl(orderItem.getImageUrl());

            cartItems.add(cartItem);
        }

        return cartItems;
    }

    private String getReorderItemName(ReorderCartItemCandidate candidate) {
        if (candidate.getCurrentTenMon() != null && !candidate.getCurrentTenMon().trim().isEmpty()) {
            return candidate.getCurrentTenMon().trim();
        }

        if (candidate.getOrderTenMon() != null && !candidate.getOrderTenMon().trim().isEmpty()) {
            return candidate.getOrderTenMon().trim();
        }

        return "Món #" + candidate.getMaMon();
    }

    private String normalizeStatus(String status) {
        if (status == null)
            return "";
        return status.trim().toUpperCase();
    }

    private String normalizeReason(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            return "Khách hàng không cung cấp lý do cụ thể";
        }

        return reason.trim();
    }

    public List<Order> getOrdersForStaff(String status, String keyword, String fromDate, String toDate) {
        return orderDAO.getOrdersForStaff(status, keyword, fromDate, toDate);
    }

    public Order getOrderByIdForStaff(long orderId) {
        return orderDAO.getOrderByIdForStaff(orderId);
    }

    public List<OrderStatusHistoryResponse> getOrderStatusHistory(long orderId) {
        List<OrderStatusHistory> history = orderDAO.getOrderStatusHistory(orderId);
        List<OrderStatusHistoryResponse> responses = new ArrayList<>();

        for (OrderStatusHistory item : history) {
            responses.add(new OrderStatusHistoryResponse(
                    item.getTrangThaiCu(),
                    item.getTrangThaiMoi(),
                    item.getNguoiThucHienLoai(),
                    item.getMaNguoiThucHien(),
                    item.getLyDo(),
                    item.getThoiGian(),
                    displayStatus(item.getTrangThaiMoi())
            ));
        }

        return responses;
    }

    public List<OrderStatusHistoryResponse> getOrderStatusHistoryForCustomer(long orderId, long customerId) {
        Order order = orderDAO.getOrderById(orderId, customerId);

        if (order == null) {
            return null;
        }

        return getOrderStatusHistory(orderId);
    }

    public boolean updateOrderStatusByStaff(long orderId, long staffId, String nextStatus, String cancelReason) {
        Order order = orderDAO.getOrderByIdForStaff(orderId);

        if (order == null) {
            System.out.println("[STAFF UPDATE] Không tìm thấy đơn hàng.");
            return false;
        }

        String currentStatus = normalizeStatus(order.getTrangThaiDon());
        String normalizedNextStatus = normalizeStatus(nextStatus);

        System.out.println("[STAFF UPDATE] orderId=" + orderId);
        System.out.println("[STAFF UPDATE] staffId=" + staffId);
        System.out.println("[STAFF UPDATE] currentStatus=" + currentStatus);
        System.out.println("[STAFF UPDATE] nextStatus=" + normalizedNextStatus);
        System.out.println("[STAFF UPDATE] cancelReason=" + cancelReason);

        if (!isValidStaffTransition(currentStatus, normalizedNextStatus)) {
            System.out.println("[STAFF UPDATE] BLOCKED: " + currentStatus + " -> " + normalizedNextStatus);
            return false;
        }

        boolean result;

        logOrderStatusChange(orderId, currentStatus, normalizedNextStatus, "staff");

        if (STATUS_CANCELLED.equals(normalizedNextStatus)) {
            result = orderDAO.updateOrderStatusStaffAndCancelReasonIfCurrent(orderId, staffId, currentStatus, normalizedNextStatus, cancelReason);
        } else {
            result = orderDAO.updateOrderStatusAndStaffIfCurrent(orderId, staffId, currentStatus, normalizedNextStatus);
        }

        System.out.println("[STAFF UPDATE] result=" + result);

        if (result) {
            String historyReason = STATUS_CANCELLED.equals(normalizedNextStatus) ? cancelReason : null;
            recordOrderStatusHistory(
                    orderId,
                    currentStatus,
                    normalizedNextStatus,
                    "STAFF",
                    staffId,
                    historyReason
            );
        }

        return result;
    }

    private boolean updateCustomerStatus(
            long orderId,
            long customerId,
            String oldStatus,
            String newStatus,
            String reason
    ) {
        logOrderStatusChange(orderId, oldStatus, newStatus, "customer");

        boolean updated = orderDAO.updateCustomerOrderStatusIfCurrent(orderId, customerId, oldStatus, newStatus);

        if (updated) {
            recordOrderStatusHistory(orderId, oldStatus, newStatus, "CUSTOMER", customerId, reason);
        }

        return updated;
    }

    private void recordOrderStatusHistory(
            long orderId,
            String oldStatus,
            String newStatus,
            String actorType,
            Long actorId,
            String reason
    ) {
        boolean recorded = orderDAO.insertOrderStatusHistory(
                orderId,
                normalizeStatus(oldStatus).isEmpty() ? null : normalizeStatus(oldStatus),
                normalizeStatus(newStatus),
                normalizeActorType(actorType),
                actorId,
                reason
        );

        if (!recorded) {
            System.out.println("[ORDER HISTORY] Could not record status history for orderId=" + orderId);
        }
    }

    private void logOrderStatusChange(long orderId, String oldStatus, String newStatus, String actor) {
        System.out.println("[ORDER STATUS] orderId=" + orderId
                + ", oldStatus=" + normalizeStatus(oldStatus)
                + ", newStatus=" + normalizeStatus(newStatus)
                + ", actor=" + actor);
    }

    private String normalizeActorType(String actorType) {
        String normalizedActorType = actorType == null ? "" : actorType.trim().toUpperCase();

        if ("CUSTOMER".equals(normalizedActorType)
                || "STAFF".equals(normalizedActorType)
                || "MANAGER".equals(normalizedActorType)
                || "ADMIN".equals(normalizedActorType)
                || "SYSTEM".equals(normalizedActorType)) {
            return normalizedActorType;
        }

        return "SYSTEM";
    }

    private String displayStatus(String status) {
        String normalizedStatus = normalizeStatus(status);

        switch (normalizedStatus) {
            case "PENDING":
                return "\u0110\u00e3 \u0111\u1eb7t h\u00e0ng";
            case "CONFIRMED":
                return "\u0110\u00e3 x\u00e1c nh\u1eadn";
            case "SHIPPING":
                return "\u0110ang giao";
            case "DELIVERED":
                return "\u0110\u00e3 giao";
            case "RECEIVED":
                return "\u0110\u00e3 nh\u1eadn h\u00e0ng";
            case "CANCEL_REQUESTED":
                return "Y\u00eau c\u1ea7u h\u1ee7y";
            case "CANCELLED":
                return "\u0110\u00e3 h\u1ee7y";
            default:
                return status;
        }
    }

    private boolean isValidStaffTransition(String current, String next) {
        if (STATUS_PENDING.equals(current) && STATUS_CONFIRMED.equals(next)) return true;
        if (STATUS_PENDING.equals(current) && STATUS_CANCELLED.equals(next)) return true;

        if (STATUS_CONFIRMED.equals(current) && STATUS_SHIPPING.equals(next)) return true;
        if (STATUS_CONFIRMED.equals(current) && STATUS_CANCELLED.equals(next)) return true;

        if (STATUS_SHIPPING.equals(current) && STATUS_DELIVERED.equals(next)) return true;

        if (STATUS_CANCEL_REQUESTED.equals(current) && STATUS_CANCELLED.equals(next)) return true;
        if (STATUS_CANCEL_REQUESTED.equals(current) && STATUS_CONFIRMED.equals(next)) return true;

        return false;
    }
}
