package vn.fastfood.dto;

import java.math.BigDecimal;

public class CheckoutResponse {
    private boolean success;
    private String message;
    private Long orderId;
    private BigDecimal tongTienMon;
    private BigDecimal tienGiamGia;
    private BigDecimal thanhTien;

    public CheckoutResponse() {
    }

    public CheckoutResponse(boolean success, String message, Long orderId,
                            BigDecimal tongTienMon, BigDecimal tienGiamGia, BigDecimal thanhTien) {
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

    public BigDecimal getTongTienMon() {
        return tongTienMon;
    }

    public BigDecimal getTienGiamGia() {
        return tienGiamGia;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }
}