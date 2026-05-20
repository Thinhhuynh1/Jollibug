package vn.fastfood.entity;

/**
 * DTO trung gian cho WebSocket STOMP.
 * JS ở trình duyệt gửi JSON theo cấu trúc này lên server.
 *
 * maYC = ID của YeuCauHoTro (phòng chat)
 * maTKGui = MaTK của người gửi
 * vaiTroGui = "Khach" hoặc "NhanVien"
 * noiDung = nội dung tin nhắn
 * tenNguoiGui = tên hiển thị (lấy từ session)
 */
public class ChatMessage {

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

    private MessageType type;

    /** ID phòng chat = MaYC của YeuCauHoTro */
    private Long maYC;

    /** MaTK của người gửi */
    private Long maTKGui;

    /** "Khach" hoặc "NhanVien" */
    private String vaiTroGui;

    /** Nội dung tin nhắn */
    private String noiDung;

    /** Tên hiển thị – dùng để vẽ DOM phía JS */
    private String tenNguoiGui;

    /** Timestamp server-side (ms epoch) */
    private long timestamp;

    // --- Getters & Setters ---
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Long getMaYC() {
        return maYC;
    }

    public void setMaYC(Long maYC) {
        this.maYC = maYC;
    }

    public Long getMaTKGui() {
        return maTKGui;
    }

    public void setMaTKGui(Long maTKGui) {
        this.maTKGui = maTKGui;
    }

    public String getVaiTroGui() {
        return vaiTroGui;
    }

    public void setVaiTroGui(String vaiTroGui) {
        this.vaiTroGui = vaiTroGui;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getTenNguoiGui() {
        return tenNguoiGui;
    }

    public void setTenNguoiGui(String tenNguoiGui) {
        this.tenNguoiGui = tenNguoiGui;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
