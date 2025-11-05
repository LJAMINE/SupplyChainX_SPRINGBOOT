package org.example.supplychainx.livraison.repository;

import org.example.supplychainx.livraison.entity.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository   extends JpaRepository<Delivery, Long> {
Page<Delivery>findAllBy(Pageable pageable);
boolean existsByClientOrder_Id(Long  clientOrderId);
}
