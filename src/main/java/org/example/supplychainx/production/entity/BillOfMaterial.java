package org.example.supplychainx.production.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bill_of_materials",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "raw_material_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillOfMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBOM;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "raw_material_id")
    private org.example.supplychainx.approvisionnement.entity.RawMaterial rawMaterial;

    @Column(name = "quantity_required")
    private Integer quantity;
}