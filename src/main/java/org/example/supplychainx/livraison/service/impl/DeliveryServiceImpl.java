package org.example.supplychainx.livraison.service.impl;

import lombok.AllArgsConstructor;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.example.supplychainx.livraison.dto.DeliveryRequestDto;
import org.example.supplychainx.livraison.dto.DeliveryResponseDto;
import org.example.supplychainx.livraison.entity.ClientOrder;
import org.example.supplychainx.livraison.entity.Delivery;
import org.example.supplychainx.livraison.entity.DeliveryStatus;
import org.example.supplychainx.livraison.mapper.DeliveryMapper;
import org.example.supplychainx.livraison.repository.ClientOrderRepository;
import org.example.supplychainx.livraison.repository.DeliveryRepository;
import org.example.supplychainx.livraison.service.interf.DeliveryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository repository;
    private final ClientOrderRepository clientOrderRepository;
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
        return null;
    }

    @Override
    public void delete(Long id) {
        Delivery delivery = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("not found "));
        repository.delete(delivery);
    }

    @Override
    public DeliveryResponseDto changeStatus(Long id, String status) {
        return null;
    }
}
