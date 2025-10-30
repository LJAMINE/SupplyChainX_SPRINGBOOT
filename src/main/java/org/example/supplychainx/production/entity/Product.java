package org.example.supplychainx.production.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product")
    private Long id;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "production_time_hours")
    private Integer productionTime;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "stock_min")
    private Integer stockMin;

    // BOM entries
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillOfMaterial> billOfMaterials;

    @Column(name = "created_at")
    private LocalDate createdAt;
}