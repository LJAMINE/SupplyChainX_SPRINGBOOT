package org.example.supplychainx.production.service;

import org.example.supplychainx.approvisionnement.repository.RawMaterialRepository;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.example.supplychainx.production.dto.BillOfMaterialDto;
import org.example.supplychainx.production.dto.ProductRequestDto;
import org.example.supplychainx.production.entity.Product;
import org.example.supplychainx.production.mapper.ProductMapper;
import org.example.supplychainx.production.repository.BillOfMaterialRepository;
import org.example.supplychainx.production.repository.ProductRepository;
import org.example.supplychainx.production.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BillOfMaterialRepository bomRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductServiceImpl service;

    private ProductRequestDto dto;

    @BeforeEach
    void setup() {
        dto = new ProductRequestDto();
        dto.setName("Widget");
        dto.setProductionTime(2);
    }

    @Test
    void create_when_material_missing_throwsResourceNotFound() {
        BillOfMaterialDto b = new BillOfMaterialDto();
        b.setRawMaterialId(99L);
        b.setQuantityRequired(3);
        dto.setBillOfMaterials(List.of(b));

        when(mapper.toEntity(dto)).thenReturn(new Product());
        when(rawMaterialRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("material not found");

        verify(mapper).toEntity(dto);
        verify(rawMaterialRepository).findById(99L);
        verifyNoInteractions(productRepository);
    }

    @Test
    void get_missing_throwsResourceNotFound() {
        when(productRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(5L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found");
    }
}
