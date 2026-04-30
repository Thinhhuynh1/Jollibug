package vn.fastfood.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
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

    @Column(name = "Username", length = 50)
    private String username;

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