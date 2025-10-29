package org.example.supplychainx.livraison.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "client_order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_order_id")
    private ClientOrder clientOrder;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private org.example.supplychainx.production.entity.Product product;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price")
    private Double unitPrice;
}