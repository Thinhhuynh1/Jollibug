package vn.fastfood.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "NGUOIDUNG")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTK")
    private Long maTK;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "HoTen", nullable = false)
    private String hoTen;

    @Column(name = "SDT", length = 15)
    private String sdt;

    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "TrangThai")
    private String trangThai;

    @ManyToOne()
    @JoinColumn(name = "MaVT")
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

    @OneToMany(mappedBy = "user")
    private List<DiaChi> diaChi;
}
