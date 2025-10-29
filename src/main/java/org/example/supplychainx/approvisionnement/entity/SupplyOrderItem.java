package org.example.supplychainx.approvisionnement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "supply_order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class SupplyOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "supply_order_id")
    private SupplyOrder supplyOrder;

    @ManyToOne(optional = false)
    @JoinColumn(name = "raw_material_id")
    private RawMaterial rawMaterial;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price", precision = 19, scale = 4)
    private java.math.BigDecimal unitPrice;

    @Column(name = "line_total", precision = 19, scale = 4)
    private java.math.BigDecimal lineTotal;
}