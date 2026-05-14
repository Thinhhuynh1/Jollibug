package vn.fastfood.repository;

/**
 * @deprecated Đã xóa hoàn toàn – thay bằng {@link ChiTietHoTroRepository}.
 * Class này giữ lại rỗng để tránh lỗi compile nếu còn import ở nơi khác.
 * Spring JPA sẽ KHÔNG tạo bean cho interface này vì nó không extends JpaRepository.
 */
@Deprecated
public interface ChatRecordRepository {
    // Đã migrate sang ChiTietHoTroRepository
}
