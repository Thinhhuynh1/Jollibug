package vn.fastfood.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "VAITRO")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaVT")
    private long maVt;

    @Column(name = "TenVT")
    private String tenVt;

    @OneToMany(mappedBy = "vaiTro")
    private List<User> users;

    public long getMaVt() {
        return maVt;
    }

    public void setMaVt(long maVt) {
        this.maVt = maVt;
    }

    public String getTenVt() {
        return tenVt;
    }

    public void setTenVt(String tenVt) {
        this.tenVt = tenVt;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
