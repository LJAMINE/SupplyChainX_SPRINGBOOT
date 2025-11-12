package org.example.supplychainx.approvisionnement.service;

import org.example.supplychainx.approvisionnement.dto.RawMaterialRequestDto;
import org.example.supplychainx.approvisionnement.dto.RawMaterialResponseDto;
import org.example.supplychainx.approvisionnement.entity.RawMaterial;
import org.example.supplychainx.approvisionnement.mapper.RawMaterialMapper;
import org.example.supplychainx.approvisionnement.repository.RawMaterialRepository;
import org.example.supplychainx.approvisionnement.service.impl.RawMaterialServiceImpl;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RawMaterialServiceImplTest {

    @Mock
    private RawMaterialRepository repository;

    @Mock
    private RawMaterialMapper mapper;

    @InjectMocks
    private RawMaterialServiceImpl service;

    @Captor
    ArgumentCaptor<RawMaterial> rawMaterialCaptor;

    private RawMaterialRequestDto requestDto;
    private RawMaterialResponseDto responseDto;
    private RawMaterial entity;

    @BeforeEach
    void setup() {
        requestDto = new RawMaterialRequestDto();
        requestDto.setName("steel");
        requestDto.setUnit("kg");

        responseDto = new RawMaterialResponseDto();
        responseDto.setId(7L);
        responseDto.setName("steel");

        entity = new RawMaterial();
        entity.setId(7L);
        entity.setName("steel");
    }

    @Test
    void list_without_search_calls_findAll_and_maps() {
        PageRequest pageable = PageRequest.of(0, 10);
        RawMaterial r = new RawMaterial();
        r.setId(1L);
        r.setName("iron");
        Page<RawMaterial> page = new PageImpl<>(Collections.singletonList(r), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toDto(r)).thenReturn(new RawMaterialResponseDto());

        Page<RawMaterialResponseDto> result = service.list(null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(repository).findAll(pageable);
        verify(mapper).toDto(r);
    }

    @Test
    void list_with_search_calls_findByNameContainingIgnoreCase_and_maps() {
        PageRequest pageable = PageRequest.of(0, 10);
        RawMaterial r = new RawMaterial();
        r.setId(2L);
        r.setName("steel");
        Page<RawMaterial> page = new PageImpl<>(Collections.singletonList(r), pageable, 1);
        when(repository.findByNameContainingIgnoreCase("st", pageable)).thenReturn(page);
        when(mapper.toDto(r)).thenReturn(new RawMaterialResponseDto());

        Page<RawMaterialResponseDto> result = service.list("st", pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(repository).findByNameContainingIgnoreCase("st", pageable);
        verify(mapper).toDto(r);
    }

    @Test
    void get_existing_returnsDto() {
        when(repository.findById(7L)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(responseDto);

        RawMaterialResponseDto dto = service.get(7L);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(7L);
        verify(repository).findById(7L);
        verify(mapper).toDto(entity);
    }

    @Test
    void get_missing_throwsResourceNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Raw material not found");
    }

    @Test
    void create_when_name_exists_throwsIllegalArgument() {
        when(repository.existsByName("steel")).thenReturn(true);

        RawMaterialRequestDto dto = new RawMaterialRequestDto();
        dto.setName("steel");

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(repository).existsByName("steel");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void create_success_setsCreatedAt_saves_and_returnsDto() {
        when(repository.existsByName("steel")).thenReturn(false);

        RawMaterial mapped = new RawMaterial();
        when(mapper.toEntity(requestDto)).thenReturn(mapped);

        RawMaterial saved = new RawMaterial();
        saved.setId(42L);
        saved.setName("steel");
        when(repository.save(any())).thenReturn(saved);

        when(mapper.toDto(saved)).thenReturn(responseDto);

        RawMaterialResponseDto res = service.create(requestDto);

        assertThat(res).isNotNull();
        assertThat(res.getId()).isEqualTo(7L).isNotNull(); // note: responseDto.id set to 7 in setup

        // verify that createdAt was set on the entity passed to save
        verify(repository).save(rawMaterialCaptor.capture());
        RawMaterial passed = rawMaterialCaptor.getValue();
        assertThat(passed.getCreatedAt()).isNotNull();

        verify(mapper).toEntity(requestDto);
        verify(mapper).toDto(saved);
    }

    @Test
    void update_existing_updates_and_returnsDto() {
        RawMaterial existing = new RawMaterial();
        existing.setId(8L);
        when(repository.findById(8L)).thenReturn(Optional.of(existing));

        // mapper.updateEntityFromDto is void; simulate behavior by leaving entity modified
        doAnswer(inv -> {
            RawMaterialRequestDto dto = inv.getArgument(0);
            RawMaterial ent = inv.getArgument(1);
            ent.setName(dto.getName());
            return null;
        }).when(mapper).updateEntityFromDto(any(RawMaterialRequestDto.class), any(RawMaterial.class));

        RawMaterial saved = new RawMaterial();
        saved.setId(8L);
        saved.setName("updated");
        when(repository.save(existing)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(responseDto);

        RawMaterialRequestDto dto = new RawMaterialRequestDto();
        dto.setName("updated");

        RawMaterialResponseDto result = service.update(8L, dto);

        assertThat(result).isNotNull();
        verify(repository).findById(8L);
        verify(mapper).updateEntityFromDto(dto, existing);
        verify(repository).save(existing);
        verify(mapper).toDto(saved);
    }

    @Test
    void update_missing_throwsResourceNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        RawMaterialRequestDto dto = new RawMaterialRequestDto();
        dto.setName("x");
        assertThatThrownBy(() -> service.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Raw material not found");
    }

    @Test
    void delete_missing_throwsResourceNotFound() {
        when(repository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(5L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Raw material not found");

        verify(repository).existsById(5L);
    }

    @Test
    void delete_existing_callsRepositoryDelete() {
        when(repository.existsById(5L)).thenReturn(true);
        doNothing().when(repository).deleteById(5L);

        service.delete(5L);

        verify(repository).existsById(5L);
        verify(repository).deleteById(5L);
    }
}