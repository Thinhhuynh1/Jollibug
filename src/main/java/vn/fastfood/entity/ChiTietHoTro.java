package vn.fastfood.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Chi tiết hội thoại hỗ trợ (CHITIETHOTRO)
 * Mỗi dòng = một tin nhắn trong phòng chat (YeuCauHoTro).
 * VaiTroGui: Khach | NhanVien
 */
@Entity
@Table(name = "CHITIETHOTRO")
public class ChiTietHoTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaChat")
    private Long maChat;

    /** Yêu cầu hỗ trợ cha */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaYC", nullable = false)
    private YeuCauHoTro yeuCau;

    /** Người gửi tin nhắn này */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaTK_Gui", nullable = false)
    private User nguoiGui;

    // (Đã xóa cột vật lý VaiTroGui để tối ưu database)

    @Column(name = "NoiDung", length = 2000, nullable = false)
    private String noiDung;

    @CreationTimestamp
    @Column(name = "NgayGui", updatable = false)
    private LocalDateTime ngayGui;

    public ChiTietHoTro() {
    }

    // --- Getters & Setters ---
    public Long getMaChat() {
        return maChat;
    }

    public void setMaChat(Long maChat) {
        this.maChat = maChat;
    }

    public YeuCauHoTro getYeuCau() {
        return yeuCau;
    }

    public void setYeuCau(YeuCauHoTro yeuCau) {
        this.yeuCau = yeuCau;
    }

    public User getNguoiGui() {
        return nguoiGui;
    }

    public void setNguoiGui(User nguoiGui) {
        this.nguoiGui = nguoiGui;
    }

    /** 
     * Tính toán ảo (Transient): Nếu ID người gửi == ID khách hàng của yêu cầu
     * thì vai trò là Khách, ngược lại là Nhân Viên.
     */
    @Transient
    public String getVaiTroGui() {
        if (yeuCau != null && nguoiGui != null 
            && yeuCau.getMaTKKH() != null 
            && yeuCau.getMaTKKH().equals(nguoiGui.getMaTK())) {
            return "Khach";
        }
        return "NhanVien";
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public LocalDateTime getNgayGui() {
        return ngayGui;
    }

    public void setNgayGui(LocalDateTime ngayGui) {
        this.ngayGui = ngayGui;
    }

    /** Helper cho JSP – JSTL fmt:formatDate không hỗ trợ LocalDateTime */
    @Transient
    public String getTimeDisplay() {
        return ngayGui == null ? "" : ngayGui.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /** Tương thích với JSP cũ dùng msg.senderRole */
    @Transient
    public String getSenderRole() {
        // "Khach" → "client" , "NhanVien" → "staff"
        return "Khach".equals(getVaiTroGui()) ? "client" : "staff";
    }

    /** Tương thích với JSP cũ dùng msg.content */
    @Transient
    public String getContent() {
        return noiDung;
    }

    /** Tên người gửi – dùng trong realtime JS */
    @Transient
    public String getSender() {
        return nguoiGui != null ? nguoiGui.getHoTen() : "";
    }
}
