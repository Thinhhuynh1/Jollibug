package vn.fastfood.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTK")
    private Long maTK;

    @Column(name = "Username", length = 50)
    private String username;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "HoTen", nullable = false)
    private String hoTen;

    @Column(name = "SDT", length = 15)
    private String sdt;

    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "TrangThai")
    @Enumerated(EnumType.STRING)
    private UserStatus trangThai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaVT", referencedColumnName = "MaVT")
    private VaiTro vaiTro;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    public String getCreatedAtDisplay() {
        return createdAt == null ? "-" : createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Transient
    public String getUpdatedAtDisplay() {
        return updatedAt == null ? "-" : updatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
} 