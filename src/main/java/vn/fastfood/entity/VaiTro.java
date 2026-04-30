package vn.fastfood.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@EqualsAndHashCode
@Getter
@Entity
@Table(name = "VAITRO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaiTro {

    @Id
    @Column(name = "MaVT", length = 20)
    private String maVT;

    @Column(name = "TenVT", length = 50, nullable = false)
    private String tenVT;

    
}