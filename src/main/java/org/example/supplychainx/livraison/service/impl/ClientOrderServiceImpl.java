package org.example.supplychainx.livraison.service.impl;

import lombok.AllArgsConstructor;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.example.supplychainx.livraison.dto.AvailabilityLineDto;
import org.example.supplychainx.livraison.dto.AvailabilityResponseDto;
import org.example.supplychainx.livraison.dto.ClientOrderRequestDto;
import org.example.supplychainx.livraison.dto.ClientOrderResponseDto;
import org.example.supplychainx.livraison.entity.ClientOrder;
import org.example.supplychainx.livraison.entity.ClientOrderItem;
import org.example.supplychainx.livraison.entity.ClientOrderStatus;
import org.example.supplychainx.livraison.mapper.ClientOrderMapper;
import org.example.supplychainx.livraison.repository.ClientOrderItemRepository;
import org.example.supplychainx.livraison.repository.ClientOrderRepository;
import org.example.supplychainx.livraison.repository.CustomerRepository;
import org.example.supplychainx.livraison.service.interf.ClientOrderService;
import org.example.supplychainx.production.dto.ProductionOrderRequestDto;
import org.example.supplychainx.production.entity.Product;
import org.example.supplychainx.production.repository.ProductRepository;
import org.example.supplychainx.production.service.interf.ProductionOrderService;
 import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ClientOrderServiceImpl implements ClientOrderService {

    private final CustomerRepository customerRepository;
    private final ClientOrderItemRepository clientOrderItemRepository;
    private final ClientOrderRepository repository;
    private final ProductRepository productRepository;
    private final ClientOrderMapper mapper;
    private final ProductionOrderService productionOrderService;

    @Override
    public Page<ClientOrderResponseDto> list(Pageable pageable) {
         return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public ClientOrderResponseDto get(Long id) {
        ClientOrder order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientOrder not found: " + id));
        return mapper.toDto(order);
    }

    @Override
    public ClientOrderResponseDto create(ClientOrderRequestDto dto) {
        var customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + dto.getCustomerId()));

        ClientOrder order = mapper.toEntity(dto);
        order.setCustomer(customer);

        double total = 0.0;
        if (order.getItems() != null) {
            for (ClientOrderItem item : order.getItems()) {
                Product p = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));
                if (item.getUnitPrice() == null) item.setUnitPrice(p.getCost());
                total += item.getUnitPrice() * item.getQuantity();
            }
        }
        order.setTotalAmount(total);

        ClientOrder saved = repository.save(order);
        return mapper.toDto(saved);
    }

    @Override
    public ClientOrderResponseDto update(Long id, ClientOrderRequestDto dto) {
        ClientOrder order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientOrder not found: " + id));
        if (order.getStatus() != ClientOrderStatus.PLANIFIE) {
            throw new IllegalStateException("Only PLANIFIE orders can be modified");
        }

        order.getItems().clear();
        if (dto.getItems() != null) {
            List<ClientOrderItem> items = dto.getItems().stream().map(i ->
                    ClientOrderItem.builder()
                            .clientOrder(order)
                            .productId(i.getProductId())
                            .quantity(i.getQuantity())
                            .unitPrice(i.getUnitPrice())
                            .quantityDelivered(0)
                            .build()
            ).collect(Collectors.toList());
            order.setItems(items);
        }

        double total = 0;
        if (order.getItems() != null) {
            for (ClientOrderItem it : order.getItems()) {
                Product p = productRepository.findById(it.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + it.getProductId()));
                if (it.getUnitPrice() == null) it.setUnitPrice(p.getCost());
                total += it.getUnitPrice() * it.getQuantity();
            }
        }
        order.setTotalAmount(total);
        ClientOrder saved = repository.save(order);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        ClientOrder order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientOrder not found: " + id));
        if (order.getStatus() != ClientOrderStatus.PLANIFIE) {
            throw new IllegalStateException("Only PLANIFIE orders can be deleted");
        }
        repository.delete(order);
    }

    @Override
    public ClientOrderResponseDto changeStatus(Long id, String statusStr) {
        ClientOrder order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientOrder not found: " + id));
        ClientOrderStatus newStatus;
        try {
            newStatus = ClientOrderStatus.valueOf(statusStr);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid status: " + statusStr);
        }

        if (order.getStatus() == ClientOrderStatus.PLANIFIE && newStatus == ClientOrderStatus.EN_COURS) {
            order.setStatus(ClientOrderStatus.EN_COURS);
        } else if (order.getStatus() == ClientOrderStatus.EN_COURS && newStatus == ClientOrderStatus.LIVREE) {
            if (order.getItems() != null) {
                for (ClientOrderItem item : order.getItems()) {
                    Product p = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));
                    Integer current = p.getStock() == null ? 0 : p.getStock();
                    if (current < item.getQuantity()) {
                        throw new IllegalStateException("Insufficient finished product stock for product: " + p.getId());
                    }
                    p.setStock(current - item.getQuantity());
                    productRepository.save(p);
                    item.setQuantityDelivered(item.getQuantity());
                }
            }
            order.setStatus(ClientOrderStatus.LIVREE);
        } else if (order.getStatus() == ClientOrderStatus.PLANIFIE && newStatus == ClientOrderStatus.ANNULE) {
            order.setStatus(ClientOrderStatus.ANNULE);
        } else {
            throw new IllegalStateException("Unsupported status transition: " + order.getStatus() + " -> " + newStatus);
        }

        ClientOrder saved = repository.save(order);
        return mapper.toDto(saved);
    }

    @Override
    public AvailabilityResponseDto checkAvailability(Long id) {
        ClientOrder order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientOrder not found: " + id));

        List<AvailabilityLineDto> missing = new ArrayList<>();
        boolean allOk = true;

        if (order.getItems() != null) {
            for (ClientOrderItem it : order.getItems()) {
                Product p = productRepository.findById(it.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + it.getProductId()));

                // stock may be null in DB, default to 0
                Integer availableObj = p.getStock();
                int available = (availableObj == null) ? 0 : availableObj;

                // quantity is primitive int (no null check)
                int required = it.getQuantity();

                if (available < required) {
                    allOk = false;
                    int missingQty = required - available;

                    AvailabilityLineDto line = new AvailabilityLineDto();
                    line.setProductId(p.getId());
                    line.setProductName(p.getName());
                    line.setRequiredQty(required);
                    line.setAvailableQty(available);
                    line.setMissingQty(missingQty);
                    missing.add(line);
                }
            }
        }

        AvailabilityResponseDto resp = new AvailabilityResponseDto();
        resp.setAvailable(allOk);
        resp.setMissing(missing);
        return resp;
    }

    @Override
    public void createProductionForShortages(Long id) {
        ClientOrder order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientOrder not found: " + id));

        Map<Long, Integer> productShortages = new HashMap<>();
        if (order.getItems() != null) {
            for (ClientOrderItem it : order.getItems()) {
                Product p = productRepository.findById(it.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + it.getProductId()));

                int stock = p.getStock() == null ? 0 : p.getStock();
                int shortage = Math.max(0, it.getQuantity() - stock);
                if (shortage > 0) {
                    productShortages.put(p.getId(), productShortages.getOrDefault(p.getId(), 0) + shortage);
                }
            }
        }

        for (var entry : productShortages.entrySet()) {
            ProductionOrderRequestDto por = ProductionOrderRequestDto.builder()
                    .productId(entry.getKey())
                    .quantity(entry.getValue())
                    .build();
            productionOrderService.create(por);
        }
    }
}