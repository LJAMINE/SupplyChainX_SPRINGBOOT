package org.example.supplychainx.livraison.service.impl;

import lombok.AllArgsConstructor;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.example.supplychainx.livraison.dto.DeliveryRequestDto;
import org.example.supplychainx.livraison.dto.DeliveryResponseDto;
import org.example.supplychainx.livraison.entity.ClientOrder;
import org.example.supplychainx.livraison.entity.ClientOrderStatus;
import org.example.supplychainx.livraison.entity.Delivery;
import org.example.supplychainx.livraison.entity.DeliveryStatus;
import org.example.supplychainx.livraison.mapper.DeliveryMapper;
import org.example.supplychainx.livraison.repository.ClientOrderRepository;
import org.example.supplychainx.livraison.repository.DeliveryRepository;
import org.example.supplychainx.livraison.service.interf.ClientOrderService;
import org.example.supplychainx.livraison.service.interf.DeliveryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;


@Service
@AllArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository repository;
    private final ClientOrderRepository clientOrderRepository;
    private final ClientOrderService clientOrderService;
    private final DeliveryMapper mapper;

    @Override
    public Page<DeliveryResponseDto> list(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public DeliveryResponseDto get(Long id) {
        Delivery delivery = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("not found "));
        return mapper.toDto(delivery);
    }

    @Override
    public DeliveryResponseDto create(DeliveryRequestDto dto) {

        ClientOrder order=clientOrderRepository.findById(dto.getClientOrderId()).orElseThrow(()->new ResourceNotFoundException("not found "));


//        no duplicate delivery for same order

        if (repository.existsByClientOrder_Id(order.getId())){
            throw new IllegalStateException("delivery for this order already exists");
        }


        Delivery d =mapper.toEntity(dto,order);
        d.setStatus(DeliveryStatus.PLANNED);
        Delivery saved=repository.save(d);

        return mapper.toDto(saved);
    }


    @Override
    public DeliveryResponseDto update(Long id, DeliveryRequestDto dto) {
        Delivery d = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found: " + id));

        if (dto.getVehicle() != null) d.setVehicle(dto.getVehicle());
        if (dto.getDriver() != null) d.setDriver(dto.getDriver());
        if (dto.getDeliveryDate() != null) d.setDeliveryDate(dto.getDeliveryDate());
        if (dto.getCost() != null) d.setCost(dto.getCost());

        Delivery saved = repository.save(d);
        return mapper.toDto(saved);
    }
    @Override
    public void delete(Long id) {
        Delivery delivery = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("not found "));
        repository.delete(delivery);
    }

    @Override
    public DeliveryResponseDto changeStatus(Long id, String statusStr) {
        Delivery d = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found: " + id));

        DeliveryStatus newStatus;
        try {
            newStatus = DeliveryStatus.valueOf(statusStr.toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid delivery status: " + statusStr);
        }

        // transitions: PLANNED -> IN_PROGRESS -> DELIVERED ; PLANNED -> CANCELLED

        if (d.getStatus() == DeliveryStatus.PLANNED && newStatus ==  DeliveryStatus.IN_PROGRESS) {
            d.setStatus(newStatus);
        } else if (d.getStatus() == DeliveryStatus.IN_PROGRESS && newStatus == DeliveryStatus.DELIVERED) {
            d.setStatus(newStatus);
        } else if (d.getStatus() == DeliveryStatus.PLANNED && newStatus == DeliveryStatus.CANCELLED) {
            d.setStatus(newStatus);
        } else {
            throw new IllegalStateException("Unsupported delivery status transition: " + d.getStatus() + " -> " + newStatus);
        }

        Delivery saved = repository.save(d);
        return mapper.toDto(saved);
    }
}
