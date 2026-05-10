package vn.fastfood.entity;

import java.io.Serializable;
import java.util.Objects;

public class ChiTietGHId implements Serializable {

    private Long maGH;
    private Long maMon;

    public ChiTietGHId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietGHId)) return false;
        ChiTietGHId that = (ChiTietGHId) o;
        return Objects.equals(maGH, that.maGH)
                && Objects.equals(maMon, that.maMon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maGH, maMon);
    }
}