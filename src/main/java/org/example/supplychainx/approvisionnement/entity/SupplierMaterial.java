package org.example.supplychainx.approvisionnement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

      @Column(name = "supplier_sku", length = 200)
    private String supplierSku;

    @Column(name = "price")
    private Double price;

    @Column(name = "lead_time_days")
    private Integer leadTime;

    @Column(name = "min_order_quantity")
    private Integer minOrderQuantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}