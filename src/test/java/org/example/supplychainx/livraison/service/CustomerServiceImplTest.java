package org.example.supplychainx.livraison.service;


import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.example.supplychainx.livraison.dto.CustomerRequestDto;
import org.example.supplychainx.livraison.dto.CustomerResponseDto;
import org.example.supplychainx.livraison.entity.Customer;
import org.example.supplychainx.livraison.mapper.CustomerMapper;
import org.example.supplychainx.livraison.repository.CustomerRepository;
import org.example.supplychainx.livraison.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper mapper;

    @InjectMocks
    private CustomerServiceImpl service ;

    private CustomerRequestDto requestDto;
//
    private CustomerResponseDto responseDto;
    private Customer entity;


    @BeforeEach
    void setup(){
        requestDto =new CustomerRequestDto();
        requestDto.setName("ACME");
        requestDto.setAddress("rue 2");
        requestDto.setCity("casa");
        requestDto.setPhone("0615155212");

        responseDto=new CustomerResponseDto();
        responseDto.setId(10L);
        responseDto.setName("ACME");

        entity =new Customer();

        entity.setName("ACME");
        entity.setAddress("rue 2");
        entity.setCity("casa");
        entity.setPhone("0615155212");



    }

    @Test
    void list_withoutQuery_callsFindAll_andReturnsPage() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").descending());
        Page<Customer> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(customerRepository.findAll(pageable)).thenReturn(page);
        when(mapper.toDto(any(Customer.class))).thenReturn(responseDto);

        Page<CustomerResponseDto> result = service.list(null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(10L);
        verify(customerRepository).findAll(pageable);
        verify(customerRepository, never()).findByNameContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    void list_withQuery_callsFindByNameContainingIgnoreCase() {
        String q = "ac";
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").descending());
        Page<Customer> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(customerRepository.findByNameContainingIgnoreCase(q, pageable)).thenReturn(page);
        when(mapper.toDto(any(Customer.class))).thenReturn(responseDto);

        Page<CustomerResponseDto> result = service.list(q, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(customerRepository).findByNameContainingIgnoreCase(q, pageable);
        verify(customerRepository, never()).findAll(any(Pageable.class));
    }


    @Test
    void get_existingId_returnDto(){
        when(customerRepository.findById(5L)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(responseDto);

        CustomerResponseDto dto=service.get(5L);



        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        verify(customerRepository).findById(5L);

    }

    @Test
    void get_missingId_throwsResourceNotFound() {
        when(customerRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(123L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void create_mapsSetsCreatedAt_savesAndReturnsDto() {
        Customer entityFromMapper = new Customer();
        when(mapper.toEntity(requestDto)).thenReturn(entityFromMapper);

        // emulate saved entity (with id)
        Customer saved = new Customer();
        saved.setId(42L);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            // return the same instance with id set to simulate JPA behaviour
            Customer arg = invocation.getArgument(0);
            arg.setId(42L);
            return arg;
        });
        CustomerResponseDto dtoOut = new CustomerResponseDto();
        dtoOut.setId(42L);
        when(mapper.toDto(any(Customer.class))).thenReturn(dtoOut);

        CustomerResponseDto result = service.create(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(42L);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        Customer captured = captor.getValue();
        assertThat(captured.getCreatedAt()).isNotNull();
        assertThat(captured.getCreatedAt()).isEqualTo(LocalDate.now()); // createdAt uses LocalDate.now()
        verify(mapper).toEntity(requestDto);
        verify(mapper).toDto(any(Customer.class));
    }


    @Test
    void update_existing_updatesFieldsAndSetsUpdatedAt() {
        Customer existing = new Customer();
        existing.setId(100L);
        existing.setName("Old");
        existing.setAddress("Old addr");
        existing.setCity("Old city");
        existing.setPhone("000");

        CustomerRequestDto dto = new CustomerRequestDto();
        dto.setName("NewName");
        dto.setAddress("New addr");
        dto.setCity("New city");
        dto.setPhone("+212666666666");

        when(customerRepository.findById(100L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CustomerResponseDto outDto = new CustomerResponseDto();
        outDto.setId(100L);
        when(mapper.toDto(any(Customer.class))).thenReturn(outDto);

        CustomerResponseDto result = service.update(100L, dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        Customer saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("NewName");
        assertThat(saved.getAddress()).isEqualTo("New addr");
        assertThat(saved.getCity()).isEqualTo("New city");
        assertThat(saved.getPhone()).isEqualTo("+212666666666");
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    void update_missing_throwsResourceNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    void delete_existing_callsDeleteById() {
        when(customerRepository.existsById(88L)).thenReturn(true);

        service.delete(88L);

        verify(customerRepository).existsById(88L);
        verify(customerRepository).deleteById(88L);
    }

    @Test
    void delete_missing_throwsResourceNotFound() {
        when(customerRepository.existsById(77L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(77L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("customer not found");

        verify(customerRepository).existsById(77L);
        verify(customerRepository, never()).deleteById(anyLong());
    }

}
