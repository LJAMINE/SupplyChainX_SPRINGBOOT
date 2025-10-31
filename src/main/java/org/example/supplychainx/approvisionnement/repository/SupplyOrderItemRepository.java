package org.example.supplychainx.approvisionnement.repository;

import org.example.supplychainx.approvisionnement.entity.SupplyOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplyOrderItemRepository extends JpaRepository<SupplyOrderItem, Long> {
}
