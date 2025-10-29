package org.example.supplychainx.approvisionnement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "raw_materials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RawMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMaterial;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "stock_min")
    private Integer stockMin;

    @Column(name = "unit", length = 50)
    private String unit;

    // ManyToMany -> Suppliers via SupplierMaterial (join entity)
    @OneToMany(mappedBy = "rawMaterial", cascade = CascadeType.ALL)
    private List<SupplierMaterial> supplierMaterials;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}