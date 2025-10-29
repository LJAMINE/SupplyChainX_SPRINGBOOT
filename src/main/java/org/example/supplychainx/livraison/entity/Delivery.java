package org.example.supplychainx.livraison.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDelivery;

    @OneToOne(optional = false)
    @JoinColumn(name = "client_order_id", unique = true)
    private ClientOrder clientOrder;

    @Column(name = "vehicle", length = 200)
    private String vehicle;

    @Column(name = "driver", length = 200)
    private String driver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private DeliveryStatus status;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "cost")
    private Double cost;
}