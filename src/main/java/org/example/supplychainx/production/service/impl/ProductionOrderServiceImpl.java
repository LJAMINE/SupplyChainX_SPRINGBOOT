package org.example.supplychainx.production.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.supplychainx.approvisionnement.entity.RawMaterial;
import org.example.supplychainx.approvisionnement.repository.RawMaterialRepository;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.example.supplychainx.production.dto.*;
import org.example.supplychainx.production.entity.BillOfMaterial;
import org.example.supplychainx.production.entity.ProductionOrder;
import org.example.supplychainx.production.entity.ProductionOrderStatus;
import org.example.supplychainx.production.mapper.ProductionOrderMapper;
import org.example.supplychainx.production.repository.BillOfMaterialRepository;
import org.example.supplychainx.production.repository.ProductRepository;
import org.example.supplychainx.production.repository.ProductionOrderRepository;
import org.example.supplychainx.production.service.interf.ProductService;
import org.example.supplychainx.production.service.interf.ProductionOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
@Transactional
public class ProductionOrderServiceImpl  implements ProductionOrderService {
    private final ProductionOrderRepository repository;
    private final ProductRepository productRepository;
    private final BillOfMaterialRepository bomRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductionOrderMapper mapper;
//    private final ProductionOrderService productionOrderService;


    @Override
    public ProductionOrderResponseDto create(ProductionOrderRequestDto dto) {
        // validate product
        var product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + dto.getProductId()));

        ProductionOrder order = mapper.toEntity(dto);
        order.setProduct(product);
        order.setStatus(ProductionOrderStatus.EN_ATTENTE);
        order.setCreatedAt(LocalDateTime.now());

        ProductionOrder saved=repository.save(order);
        return mapper.toDto(saved);
    }

    @Override
    public ProductionOrderResponseDto update(Long id, ProductionOrderRequestDto dto) {
        ProductionOrder order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder not found: " + id));
        if (!ProductionOrderStatus.EN_ATTENTE.equals(order.getStatus())) {
            throw new IllegalStateException("Only EN_ATTENTE orders can be modified");
        }

        var product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + dto.getProductId()));
        order.setProduct(product);
        order.setQuantity(dto.getQuantity());
        ProductionOrder saved = repository.save(order);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        ProductionOrder order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder not found: " + id));

        if (!ProductionOrderStatus.EN_ATTENTE.equals(order.getStatus())) {
            throw new IllegalStateException("Only EN_ATTENTE orders can be deleted");
        }
        repository.deleteById(id);
    }

    @Override
    public ProductionOrderResponseDto get(Long id) {
        ProductionOrder order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder not found: " + id));
        return mapper.toDto(order);
    }


    @Override
    public Page<ProductionOrderResponseDto> list(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public ProductionOrderResponseDto changeStatus(Long id, ProductionOrderStatusChangeDto dto) {
        ProductionOrder order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder not found: " + id));

        ProductionOrderStatus newStatus;
        try {
            newStatus = ProductionOrderStatus.valueOf(dto.getStatus());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid status: " + dto.getStatus());
        }

        // mn  EN_ATTENTE -> EN_PRODUCTION  also check availability and consume materials

        if (order.getStatus() == ProductionOrderStatus.EN_ATTENTE && newStatus == ProductionOrderStatus.EN_PRODUCTION) {
            var availability = checkAvailability(order.getProduct().getId(), order.getQuantity());
            if (!availability.isAvailable()) {
                throw new IllegalStateException("Materials not available: " + availability.getMissing());
            }
            // consume materials

            List<BillOfMaterial> boms = bomRepository.findByProductId(order.getProduct().getId());
            for (BillOfMaterial b : boms) {
                long needed = (long) b.getQuantity() * order.getQuantity();
                RawMaterial rm = rawMaterialRepository.findById(b.getRawMaterial().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Raw material not found: " + b.getRawMaterial().getId()));
                int newStock = rm.getStock() - (int) needed;
                rm.setStock(newStock);
                rawMaterialRepository.save(rm);
            }
            order.setStartDate(LocalDate.now());
            order.setStatus(ProductionOrderStatus.EN_PRODUCTION);
        }
        // EN_PRODUCTION -> TERMINE : finalize and increase product stock
        else if (order.getStatus() == ProductionOrderStatus.EN_PRODUCTION && newStatus == ProductionOrderStatus.TERMINE) {
            order.setEndDate(LocalDate.now());
            order.setStatus(ProductionOrderStatus.TERMINE);
            // add produced quantity to product stock
            var product = order.getProduct();
            int current = product.getStock() == null ? 0 : product.getStock();
            product.setStock(current + order.getQuantity());
            productRepository.save(product);
        }
        // EN_ATTENTE -> ANNULLE (cancel)
        else if (order.getStatus() == ProductionOrderStatus.EN_ATTENTE && newStatus == ProductionOrderStatus.ANNULLE) {
            order.setStatus(ProductionOrderStatus.ANNULLE);
        } else {
            // allow setting BLOQUE from any state, or other transitions as needed
            if (newStatus == ProductionOrderStatus.BLOQUE) {
                order.setStatus(ProductionOrderStatus.BLOQUE);
            } else {
                throw new IllegalStateException("Unsupported status transition: " + order.getStatus() + " -> " + newStatus);
            }
        }

        ProductionOrder saved = repository.save(order);
        return mapper.toDto(saved);
    }

    @Override
    public AvailabilityResponseDto checkAvailability(Long productId, Integer quantity) {

        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        List<BillOfMaterial> boms = bomRepository.findByProductId(productId);
        List<AvailabilityLineDto> missing = new ArrayList<>();
        boolean allOk = true;
        for (BillOfMaterial b : boms) {
            int required = b.getQuantity() * quantity;
            RawMaterial rm = rawMaterialRepository.findById(b.getRawMaterial().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Raw material not found: " + b.getRawMaterial().getId()));
            int available = rm.getStock() == null ? 0 : rm.getStock();
            if (available < required) {
                allOk = false;
                missing.add(AvailabilityLineDto.builder()
                        .rawMaterialId(rm.getId())
                        .rawMaterialName(rm.getName())
                        .requiredQty(required)
                        .availableQty(available)
                        .missingQty(required - available)
                        .build());
            }
        }
        return AvailabilityResponseDto.builder().available(allOk).missing(missing).build();
    }

    @Override
    public EstimateResponseDto estimate(Integer productId, Integer quantity) {
        var product = productRepository.findById(Long.valueOf(productId))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        int perUnit = product.getProductionTime() == null ? 0 : product.getProductionTime();
        int estimated = perUnit * (quantity == null ? 0 : quantity);
        return EstimateResponseDto.builder().estimatedHours(estimated).build();
    }
}
