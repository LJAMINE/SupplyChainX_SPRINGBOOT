package org.example.supplychainx.approvisionnement.service;

import org.example.supplychainx.approvisionnement.dto.SupplierRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplierResponseDto;
import org.example.supplychainx.approvisionnement.entity.Supplier;
import org.example.supplychainx.approvisionnement.mapper.SupplierMapper;
import org.example.supplychainx.approvisionnement.repository.SupplierRepository;
import org.example.supplychainx.approvisionnement.service.impl.SupplierServiceImpl;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper mapper;

    @InjectMocks
    private SupplierServiceImpl service;

    @Captor
    ArgumentCaptor<Supplier> supplierCaptor;

    private SupplierRequestDto requestDto;
    private SupplierResponseDto responseDto;
    private Supplier entity;

    @BeforeEach
    void setup() {
        requestDto = new SupplierRequestDto();
        requestDto.setName("ACME");
        requestDto.setContact("casa");
        requestDto.setLeadTime(3);
        requestDto.setRating(3.5);

        responseDto = new SupplierResponseDto();
        responseDto.setId(10L);
        responseDto.setName("ACME");

        entity = new Supplier();
        entity.setId(10L);
        entity.setName("ACME");
    }

    @Test
    void list_without_search_uses_findAll_and_maps_results() {
        PageRequest pageable = PageRequest.of(0, 10);
        Supplier s = new Supplier();
        s.setId(1L);
        s.setName("Test");
        Page<Supplier> page = new PageImpl<>(Collections.singletonList(s), pageable, 1);
        when(supplierRepository.findAll(pageable)).thenReturn(page);
        when(mapper.toDto(s)).thenReturn(new SupplierResponseDto());

        Page<SupplierResponseDto> result = service.list(null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(supplierRepository).findAll(pageable);
        verify(mapper).toDto(s);
    }

    @Test
    void list_with_search_uses_findByNameContainingIgnoreCase_and_maps_results() {
        PageRequest pageable = PageRequest.of(0, 10);
        Supplier s = new Supplier();
        s.setId(2L);
        s.setName("Acme");
        Page<Supplier> page = new PageImpl<>(Collections.singletonList(s), pageable, 1);
        when(supplierRepository.findByNameContainingIgnoreCase("ac", pageable)).thenReturn(page);
        when(mapper.toDto(s)).thenReturn(new SupplierResponseDto());

        Page<SupplierResponseDto> result = service.list("ac", pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(supplierRepository).findByNameContainingIgnoreCase("ac", pageable);
        verify(mapper).toDto(s);
    }

    @Test
    void get_existingId_returnDto() {
        when(supplierRepository.findById(5L)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(responseDto);

        SupplierResponseDto dto = service.get(5L);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        verify(supplierRepository).findById(5L);
        verify(mapper).toDto(entity);
    }

    @Test
    void get_missingId_throwsResourceNotFound() {
        when(supplierRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(123L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void create_converts_setsCreatedAt_saves_and_returnsDto() {
        Supplier mapped = new Supplier();
        when(mapper.toEntity(requestDto)).thenReturn(mapped);

        Supplier saved = new Supplier();
        saved.setId(42L);
        when(supplierRepository.save(mapped)).thenReturn(saved);

        SupplierResponseDto dto = new SupplierResponseDto();
        dto.setId(42L);
        when(mapper.toDto(saved)).thenReturn(dto);

        SupplierResponseDto result = service.create(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(42L);
        verify(mapper).toEntity(requestDto);
        verify(supplierRepository).save(mapped);
        verify(mapper).toDto(saved);

        verify(supplierRepository).save(mapped);
    }

    @Test
    void update_existing_updates_fields_setsUpdatedAt_and_returnsDto() {
        Supplier existing = new Supplier();
        existing.setId(7L);
        when(supplierRepository.findById(7L)).thenReturn(Optional.of(existing));
         doAnswer(inv -> {
            SupplierRequestDto dto = inv.getArgument(0);
            Supplier sup = inv.getArgument(1);
            sup.setName(dto.getName());
            return null;
        }).when(mapper).updateEntityFromDto(any(SupplierRequestDto.class), any(Supplier.class));

        Supplier saved = new Supplier();
        saved.setId(7L);
        saved.setName("Updated");
        when(supplierRepository.save(existing)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(new SupplierResponseDto());

        SupplierResponseDto result = service.update(7L, requestDto);

        assertThat(result).isNotNull();
        verify(supplierRepository).findById(7L);
        verify(mapper).updateEntityFromDto(requestDto, existing);
        verify(supplierRepository).save(existing);
        verify(mapper).toDto(saved);
        assertThat(existing.getUpdatedAt()).isNotNull();
    }

    @Test
    void update_missing_throwsResourceNotFound() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Supplier not found");
    }

    @Test
    void delete_missing_throwsResourceNotFound() {
        when(supplierRepository.existsById(6L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(6L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("supplier not found");
    }

    @Test
    void delete_existing_deletes() {
        when(supplierRepository.existsById(6L)).thenReturn(true);
        doNothing().when(supplierRepository).deleteById(6L);

        service.delete(6L);

        verify(supplierRepository).existsById(6L);
        verify(supplierRepository).deleteById(6L);
    }
}