package vn.fastfood.dto;

public class CheckoutResponse {
    private boolean success;
    private String message;
    private Long maDH;
    private Double tongTienMon;
    private Double tienGiamGia;
    private Double thanhTien;

    public CheckoutResponse() {
    }

    public CheckoutResponse(boolean success, String message, Long maDH,
            Double tongTienMon, Double tienGiamGia, Double thanhTien) {
        this.success = success;
        this.message = message;
        this.maDH = maDH;
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

    public Long getMaDH() {
        return maDH;
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
