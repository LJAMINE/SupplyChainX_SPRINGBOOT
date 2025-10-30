package org.example.supplychainx.approvisionnement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "supply_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class SupplyOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    private Long id;


    @ManyToOne(optional = false)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private SupplyOrderStatus status;

    @Column(name = "total_amount", precision = 19, scale = 4)
    private BigDecimal totalAmount;

    // Items (join table between orders and materials with quantity/unit price)
    @OneToMany(mappedBy = "supplyOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplyOrderItem> items;
}