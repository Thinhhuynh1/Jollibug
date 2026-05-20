package vn.fastfood.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "CHITIETGH")
@IdClass(ChiTietGHId.class)
@Data
public class ChiTietGH {

    @Id
    @Column(name = "MaGH")
    private Long maGH;

    @Id
    @Column(name = "MaMon")
    private Long maMon;

    @Column(name = "SLuong")
    private Integer sLuong;
}