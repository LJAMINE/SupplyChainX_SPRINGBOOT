package org.example.supplychainx.livraison.repository;

import org.example.supplychainx.livraison.entity.ClientOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
    Page<ClientOrder> findAll(Pageable pageable);
}