package vn.fastfood.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Table(name = "USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTK")
    private Long maTK;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "HoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "SDT", length = 15)
    private String sdt;

    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai")
    private UserStatus trangThai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaVT", referencedColumnName = "MaVT")
    private VaiTro vaiTro;

    // Tự động lấy giờ hiện tại khi tạo mới
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Tự động lấy giờ hiện tại mỗi khi có thao tác sửa dữ liệu
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}