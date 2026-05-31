package vn.fastfood.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import vn.fastfood.entity.DanhGia;

public class ReviewResponse {
    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private long maDG;
    private long maDH;
    private long maMon;
    private String tenMon;
    private int sao;
    private String noiDung;
    private LocalDateTime ngayDG;
    private String ngayDGDisplay;
    private boolean canEdit;
    private String editDeadlineDisplay;

    public static ReviewResponse from(DanhGia danhGia) {
        ReviewResponse response = new ReviewResponse();
        response.maDG = danhGia.getMaDG();
        response.maDH = danhGia.getDonHang().getMaDH();
        response.maMon = danhGia.getMonAn().getMaMon();
        response.tenMon = danhGia.getMonAn().getTenMon();
        response.sao = danhGia.getSao();
        response.noiDung = danhGia.getNoiDung();
        response.ngayDG = danhGia.getNgayDG();
        response.ngayDGDisplay = danhGia.getNgayDG() == null ? ""
                : danhGia.getNgayDG().format(DISPLAY);
        return response;
    }

    public long getMaDG() {
        return maDG;
    }

    public long getMaDH() {
        return maDH;
    }

    public long getMaMon() {
        return maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public int getSao() {
        return sao;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public LocalDateTime getNgayDG() {
        return ngayDG;
    }

    public String getNgayDGDisplay() {
        return ngayDGDisplay;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public String getEditDeadlineDisplay() {
        return editDeadlineDisplay;
    }

    public void setEditDeadlineDisplay(String editDeadlineDisplay) {
        this.editDeadlineDisplay = editDeadlineDisplay;
    }
}
