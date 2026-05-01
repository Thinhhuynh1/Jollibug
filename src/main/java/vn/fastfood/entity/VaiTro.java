package vn.fastfood.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "VAITRO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VaiTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaVT")
    private Long maVT;

    @Column(name = "TenVT", nullable = false)
    private String tenVT;

    @OneToMany(mappedBy = "vaiTro")
    private List<User> users;
}