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


    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;


    @Column(name = "quantity_delivered")
    private Integer quantityDelivered;

    @Column(name = "unit_price")
    private Double unitPrice;
}