package org.example.supplychainx.livraison.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCustomer;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "city", length = 200)
    private String city;

    @OneToMany(mappedBy = "customer")
    private List<ClientOrder> orders;

    @Column(name = "created_at")
    private LocalDate createdAt;
}