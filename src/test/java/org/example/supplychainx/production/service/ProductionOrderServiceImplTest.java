package org.example.supplychainx.production.service;

import org.example.supplychainx.approvisionnement.entity.RawMaterial;
import org.example.supplychainx.production.dto.AvailabilityResponseDto;
import org.example.supplychainx.production.dto.AvailabilityLineDto;
import org.example.supplychainx.production.dto.EstimateResponseDto;
import org.example.supplychainx.production.entity.BillOfMaterial;
import org.example.supplychainx.production.entity.Product;
import org.example.supplychainx.production.mapper.ProductionOrderMapper;
import org.example.supplychainx.production.repository.BillOfMaterialRepository;
import org.example.supplychainx.production.repository.ProductRepository;
import org.example.supplychainx.production.repository.ProductionOrderRepository;
import org.example.supplychainx.production.service.impl.ProductionOrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductionOrderServiceImplTest {

    @Mock
    private ProductionOrderRepository repository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BillOfMaterialRepository bomRepository;

    @Mock
    private org.example.supplychainx.approvisionnement.repository.RawMaterialRepository rawMaterialRepository;

    @Mock
    private ProductionOrderMapper mapper;

    @InjectMocks
    private ProductionOrderServiceImpl service;

    @Test
    void estimate_computes_hours_correctly() {
        Product p = new Product();
        p.setId(1L);
        p.setProductionTime(4);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        EstimateResponseDto res = service.estimate(1, 3);

        assertThat(res.getEstimatedHours()).isEqualTo(12);
    }

    @Test
    void checkAvailability_reports_missing_when_stock_insufficient() {
        Product p = new Product();
        p.setId(2L);
        when(productRepository.findById(2L)).thenReturn(Optional.of(p));

        BillOfMaterial b = BillOfMaterial.builder()
                .id(5L)
                .rawMaterial(org.example.supplychainx.approvisionnement.entity.RawMaterial.builder().id(10L).build())
                .quantity(2)
                .build();

        when(bomRepository.findByProductId(2L)).thenReturn(List.of(b));

        RawMaterial rm = new RawMaterial();
        rm.setId(10L);
        rm.setName("Iron");
        rm.setStock(3); // available less than required (required = quantity * requested)
        when(rawMaterialRepository.findById(10L)).thenReturn(Optional.of(rm));

        AvailabilityResponseDto avail = service.checkAvailability(2L, 2);

        assertThat(avail.isAvailable()).isFalse();
        assertThat(avail.getMissing()).isNotEmpty();
        AvailabilityLineDto line = avail.getMissing().get(0);
        assertThat(line.getMissingQty()).isEqualTo(1); // required 4, available 3 -> missing 1
    }
}
