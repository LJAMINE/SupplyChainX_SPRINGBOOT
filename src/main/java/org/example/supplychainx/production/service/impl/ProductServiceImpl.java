package org.example.supplychainx.production.service.impl;

import lombok.AllArgsConstructor;
import org.example.supplychainx.approvisionnement.entity.RawMaterial;
import org.example.supplychainx.approvisionnement.repository.RawMaterialRepository;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.example.supplychainx.production.dto.BillOfMaterialDto;
import org.example.supplychainx.production.dto.ProductRequestDto;
import org.example.supplychainx.production.dto.ProductResponseDto;
import org.example.supplychainx.production.entity.BillOfMaterial;
import org.example.supplychainx.production.entity.Product;
import org.example.supplychainx.production.mapper.ProductMapper;
import org.example.supplychainx.production.repository.BillOfMaterialRepository;
import org.example.supplychainx.production.repository.ProductRepository;
import org.example.supplychainx.production.service.interf.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BillOfMaterialRepository bomRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductMapper mapper;
//    private final  ProductionOrderRepository productionOrderRepository;


    @Override
    public ProductResponseDto create(ProductRequestDto dto) {
        Product product = mapper.toEntity(dto);
        product.setCreatedAt(LocalDate.now());

//        build BOM

        List<BillOfMaterial> boms = new ArrayList<>();

        if (dto.getBillOfMaterials() != null) {
            for (BillOfMaterialDto b : dto.getBillOfMaterials()) {
                RawMaterial rm = rawMaterialRepository.findById(b.getRawMaterialId()).orElseThrow(() -> new ResourceNotFoundException("material not found "));
                BillOfMaterial bom = BillOfMaterial.builder()
                        .product(product)
                        .rawMaterial(rm)
                        .quantity(b.getQuantityRequired())
                        .build();

                boms.add(bom);
            }
        }

        product.setBillOfMaterials(boms);
        Product saved = productRepository.save(product);
        return mapper.toDto(saved);
    }

    @Override
    public ProductResponseDto update(Long id, ProductRequestDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        // update scalar fields
        product.setName(dto.getName());
        product.setProductionTime(dto.getProductionTime());
        product.setCost(dto.getCost());
        product.setStock(dto.getStock());
        product.setStockMin(dto.getStockMin());

        // replace BOM: clear and recreate (cascade + orphanRemoval ensures DB sync)
        product.getBillOfMaterials().clear();
        List<BillOfMaterial> boms = new ArrayList<>();
        if (dto.getBillOfMaterials() != null) {
            for (BillOfMaterialDto b : dto.getBillOfMaterials()) {
                RawMaterial rm = rawMaterialRepository.findById(b.getRawMaterialId())
                        .orElseThrow(() -> new ResourceNotFoundException("Raw material not found: " + b.getRawMaterialId()));
                BillOfMaterial bom = BillOfMaterial.builder()
                        .product(product)
                        .rawMaterial(rm)
                        .quantity(b.getQuantityRequired())
                        .build();
                boms.add(bom);
            }
        }
        product.setBillOfMaterials(boms);
        Product saved = productRepository.save(product);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {

    }

//    @Override
//    public void delete(Long id) {
//        // don't delete if any production order references this product
//        boolean hasOrders = productionOrderRepository.existsByProductId(id);
//        if (hasOrders) {
//            throw new IllegalStateException("Cannot delete product with existing production orders");
//        }
//        if (!productRepository.existsById(id)) {
//            throw new ResourceNotFoundException("Product not found: " + id);
//        }
//        productRepository.deleteById(id);
//    }

    @Override
    public ProductResponseDto get(Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found: "));
        return mapper.toDto(p);
    }

    @Override
    public Page<ProductResponseDto> list(Pageable pageable) {
        return productRepository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<ProductResponseDto> searchByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable).map(mapper::toDto);
    }
}
