package org.example.supplychainx.livraison.repository;

import org.example.supplychainx.livraison.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository  extends JpaRepository<Customer,Long> {

    Page<Customer>findByNameContainingIgnoreCase(String name , Pageable pageable);



}
