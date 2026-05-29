package vn.fastfood.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ChiTietKhuyenMaiId implements Serializable {
    @Column(name = "MaKM")
    private Long maKM;

    @Column(name = "MaMon")
    private Long maMon;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChiTietKhuyenMaiId that)) {
            return false;
        }
        return Objects.equals(maKM, that.maKM) && Objects.equals(maMon, that.maMon);
    }
}
