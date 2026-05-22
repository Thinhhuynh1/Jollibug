package vn.fastfood.dto;

public class CheckoutResponse {
    private boolean success;
    private String message;
    private Long orderId;
    private Double tongTienMon;
    private Double tienGiamGia;
    private Double thanhTien;

    public CheckoutResponse() {
    }

    public CheckoutResponse(boolean success, String message, Long orderId,
            Double tongTienMon, Double tienGiamGia, Double thanhTien) {
        this.success = success;
        this.message = message;
        this.orderId = orderId;
        this.tongTienMon = tongTienMon;
        this.tienGiamGia = tienGiamGia;
        this.thanhTien = thanhTien;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Double getTongTienMon() {
        return tongTienMon;
    }

    public Double getTienGiamGia() {
        return tienGiamGia;
    }

    public Double getThanhTien() {
        return thanhTien;
    }
}
