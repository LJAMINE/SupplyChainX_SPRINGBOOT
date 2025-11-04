package org.example.supplychainx.livraison.repository;

import org.example.supplychainx.livraison.entity.ClientOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientOrderItemRepository extends JpaRepository<ClientOrderItem, Long> {
}