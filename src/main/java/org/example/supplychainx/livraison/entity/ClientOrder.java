package org.example.supplychainx.livraison.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "client_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrder;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private ClientOrderStatus status;

    @OneToMany(mappedBy = "clientOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientOrderItem> items;
}