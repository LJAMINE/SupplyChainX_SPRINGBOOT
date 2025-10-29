package org.example.supplychainx.approvisionnement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "supplier_materials",
        uniqueConstraints = @UniqueConstraint(columnNames = {"supplier_id", "raw_material_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class SupplierMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // link to Supplier
    @ManyToOne(optional = false)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    // link to RawMaterial
    @ManyToOne(optional = false)
    @JoinColumn(name = "raw_material_id")
    private RawMaterial rawMaterial;

    @Column(name = "supplier_sku", length = 150)
    private String supplierSku;

    @Column(name = "price", precision = 19, scale = 4)
    private BigDecimal price;

    @Column(name = "lead_time_days")
    private Integer leadTimeDays;
}