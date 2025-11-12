package org.example.supplychainx.approvisionnement.service;

import org.example.supplychainx.approvisionnement.dto.SupplyOrderItemRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplyOrderRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplyOrderResponseDto;
import org.example.supplychainx.approvisionnement.dto.SupplyOrderStatusChangeDto;
import org.example.supplychainx.approvisionnement.entity.RawMaterial;
import org.example.supplychainx.approvisionnement.entity.SupplyOrder;
import org.example.supplychainx.approvisionnement.entity.SupplyOrderItem;
import org.example.supplychainx.approvisionnement.entity.Supplier;
import org.example.supplychainx.approvisionnement.mapper.SupplyOrderMapper;
import org.example.supplychainx.approvisionnement.repository.RawMaterialRepository;
import org.example.supplychainx.approvisionnement.repository.SupplierRepository;
import org.example.supplychainx.approvisionnement.repository.SupplyOrderItemRepository;
import org.example.supplychainx.approvisionnement.repository.SupplyOrderRepository;
import org.example.supplychainx.approvisionnement.service.impl.SupplyOrderServiceImpl;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplyOrderServiceImplTest {

    @Mock
    SupplyOrderRepository repository;

    @Mock
    SupplyOrderItemRepository supplyOrderItemRepository;

    @Mock
    RawMaterialRepository rawMaterialRepository;

    @Mock
    SupplierRepository supplierRepository;

    @Mock
    SupplyOrderMapper mapper;

    @InjectMocks
    SupplyOrderServiceImpl service;

    @Captor
    ArgumentCaptor<RawMaterial> rawMaterialCaptor;

    private SupplyOrderRequestDto sampleRequestDto() {
        SupplyOrderItemRequestDto item1 = new SupplyOrderItemRequestDto();
        item1.setRawMaterialId(10L);
        item1.setQuantity(5);
        item1.setUnitPrice(BigDecimal.valueOf(12.50));

        SupplyOrderItemRequestDto item2 = new SupplyOrderItemRequestDto();
        item2.setRawMaterialId(11L);
        item2.setQuantity(5);
        item2.setUnitPrice(BigDecimal.valueOf(6.75));

        SupplyOrderRequestDto dto = new SupplyOrderRequestDto();
        dto.setSupplierId(15L);
        dto.setExpectedDate(LocalDate.now().plusDays(10));
        dto.setItems(Arrays.asList(item1, item2));
        return dto;
    }

    private SupplyOrder buildOrderEntityWithItems() {
        SupplyOrder order = new SupplyOrder();
        order.setId(6L);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(BigDecimal.valueOf(96.25));

        RawMaterial rm1 = new RawMaterial();
        rm1.setId(10L);
        rm1.setName("plastic2");
        rm1.setStock(0);

        RawMaterial rm2 = new RawMaterial();
        rm2.setId(11L);
        rm2.setName("zinc");
        rm2.setStock(2);

        SupplyOrderItem it1 = SupplyOrderItem.builder()
                .id(11L)
                .rawMaterial(rm1)
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(12.50))
                .lineTotal(BigDecimal.valueOf(62.50))
                .supplyOrder(order)
                .build();

        SupplyOrderItem it2 = SupplyOrderItem.builder()
                .id(12L)
                .rawMaterial(rm2)
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(6.75))
                .lineTotal(BigDecimal.valueOf(33.75))
                .supplyOrder(order)
                .build();

        order.setItems(Arrays.asList(it1, it2));
        return order;
    }

    @Test
    void create_should_compute_total_and_save_and_return_dto() {
        SupplyOrderRequestDto dto = sampleRequestDto();

        // raw materials and supplier available
        RawMaterial rm1 = new RawMaterial(); rm1.setId(10L);
        RawMaterial rm2 = new RawMaterial(); rm2.setId(11L);
        Supplier supplier = new Supplier(); supplier.setId(15L);

        when(supplierRepository.findById(15L)).thenReturn(Optional.of(supplier));
        when(rawMaterialRepository.findById(10L)).thenReturn(Optional.of(rm1));
        when(rawMaterialRepository.findById(11L)).thenReturn(Optional.of(rm2));

        SupplyOrder entityFromMapper = new SupplyOrder();
        when(mapper.toEntity(dto)).thenReturn(entityFromMapper);

        SupplyOrder saved = buildOrderEntityWithItems();
        when(repository.save(any())).thenReturn(saved);

        SupplyOrderResponseDto expectedDto = new SupplyOrderResponseDto();
        expectedDto.setId(6L);
        when(mapper.toDto(saved)).thenReturn(expectedDto);

        SupplyOrderResponseDto result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(6L);
        verify(mapper).toEntity(dto);
        verify(repository).save(entityFromMapper);
        verify(mapper).toDto(saved);
    }

    @Test
    void create_when_supplier_missing_throws() {
        SupplyOrderRequestDto dto = sampleRequestDto();
        when(supplierRepository.findById(15L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("supplier not found");
    }



    @Test
    void update_when_order_missing_throws() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(99L, sampleRequestDto())).isInstanceOf(ResourceNotFoundException.class);
    }



    @Test
    void list_should_map_page_elements() {
        SupplyOrder one = buildOrderEntityWithItems();
        Page<SupplyOrder> page = new PageImpl<>(Collections.singletonList(one), PageRequest.of(0, 10), 1);
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(page);
        when(mapper.toDto(any())).thenReturn(new SupplyOrderResponseDto());

        var dtoPage = service.list(null, PageRequest.of(0, 10));
        assertThat(dtoPage.getTotalElements()).isEqualTo(1);
        verify(repository).findAll(PageRequest.of(0, 10));
    }

    @Test
    void delete_should_throw_when_missing_else_delete() {
        when(repository.existsById(6L)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(6L)).isInstanceOf(ResourceNotFoundException.class);

        when(repository.existsById(6L)).thenReturn(true);
        doNothing().when(repository).deleteById(6L);
        service.delete(6L);
        verify(repository).deleteById(6L);
    }



    @Test
    void changeStatus_invalid_status_should_throw() {
        SupplyOrder order = buildOrderEntityWithItems();
        when(repository.findById(6L)).thenReturn(Optional.of(order));

        SupplyOrderStatusChangeDto dto = new SupplyOrderStatusChangeDto();
        dto.setStatus("UNKNOWN_STATUS");

        assertThatThrownBy(() -> service.changeStatus(6L, dto)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void changeStatus_RECUE_when_already_RECUE_does_not_double_increase() {
        SupplyOrder order = buildOrderEntityWithItems();
        order.setStatus(org.example.supplychainx.approvisionnement.entity.SupplyOrderStatus.RECUE);
        when(repository.findById(6L)).thenReturn(Optional.of(order));

        SupplyOrderStatusChangeDto dto = new SupplyOrderStatusChangeDto();
        dto.setStatus("RECUE");

        service.changeStatus(6L, dto);

        verify(rawMaterialRepository, never()).save(any());
    }
}