package org.example.supplychainx.approvisionnement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSupplier;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "contact", length = 400)
    private String contact;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "lead_time_days")
    private Integer leadTime;

    @OneToMany(mappedBy = "supplier")
    private List<SupplyOrder> orders;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}