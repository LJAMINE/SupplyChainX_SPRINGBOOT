package org.example.supplychainx.livraison.service.interf;

import org.example.supplychainx.approvisionnement.dto.SupplierRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplierResponseDto;
import org.example.supplychainx.livraison.dto.CustomerRequestDto;
import org.example.supplychainx.livraison.dto.CustomerResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService  {


    Page<CustomerResponseDto> list(String q, Pageable pageable);
    CustomerResponseDto get (Long id);
    CustomerResponseDto create (CustomerRequestDto dto);
    CustomerResponseDto update (Long id ,CustomerRequestDto dto);

    Page<CustomerResponseDto> searchByName(String name, Pageable pageable);
    void delete (Long id );
 }
