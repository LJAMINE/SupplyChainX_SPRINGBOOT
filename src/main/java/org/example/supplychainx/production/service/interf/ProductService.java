package org.example.supplychainx.production.service.interf;

import org.example.supplychainx.production.dto.ProductRequestDto;
import org.example.supplychainx.production.dto.ProductResponseDto;
import org.example.supplychainx.production.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponseDto create (ProductRequestDto dto);
    ProductResponseDto update (Long id, ProductRequestDto dto);
    void delete(Long  id);
    ProductResponseDto get(Long id);

    Page<ProductResponseDto> list(Pageable pageable);
    Page<ProductResponseDto> searchByName(String name,Pageable pageable);
}
