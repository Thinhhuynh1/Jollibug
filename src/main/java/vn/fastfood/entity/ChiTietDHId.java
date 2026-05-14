package vn.fastfood.entity;

import java.io.Serializable;
import java.util.Objects;

public class ChiTietDHId implements Serializable {

    private Long maDH;
    private Long maMon;

    public ChiTietDHId() {
    }

    public ChiTietDHId(Long maDH, Long maMon) {
        this.maDH = maDH;
        this.maMon = maMon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietDHId)) return false;
        ChiTietDHId that = (ChiTietDHId) o;
        return Objects.equals(maDH, that.maDH)
                && Objects.equals(maMon, that.maMon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDH, maMon);
    }
}
