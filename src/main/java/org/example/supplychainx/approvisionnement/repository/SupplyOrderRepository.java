package org.example.supplychainx.approvisionnement.repository;

import org.example.supplychainx.approvisionnement.entity.SupplyOrder;
import org.example.supplychainx.approvisionnement.entity.SupplyOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplyOrderRepository extends JpaRepository<SupplyOrder,Long> {


    Page<SupplyOrder> findByStatus(SupplyOrderStatus status, Pageable pageable);

    Page<SupplyOrder>findBySupplierId(Long supplierId,Pageable pageable);


}
