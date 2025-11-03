package org.example.supplychainx.livraison.service.impl;

import lombok.AllArgsConstructor;
import org.example.supplychainx.approvisionnement.entity.Supplier;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.example.supplychainx.livraison.dto.CustomerRequestDto;
import org.example.supplychainx.livraison.dto.CustomerResponseDto;
import org.example.supplychainx.livraison.entity.Customer;
import org.example.supplychainx.livraison.mapper.CustomerMapper;
import org.example.supplychainx.livraison.repository.CustomerRepository;
import org.example.supplychainx.livraison.service.interf.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    @Override
    public Page<CustomerResponseDto> list(String q, Pageable pageable) {
        Page<Customer> page = (q == null || q.isBlank())
                ? customerRepository.findAll(pageable)
                : customerRepository.findByNameContainingIgnoreCase(q, pageable);
        return page.map(mapper::toDto);
    }

    @Override
    public CustomerResponseDto get(Long id) {
        return customerRepository.findById(id).map(mapper::toDto).orElseThrow(() -> new ResourceNotFoundException("not found"));
    }

    @Override
    public CustomerResponseDto create(CustomerRequestDto dto) {
        Customer customer = mapper.toEntity(dto);
        customer.setCreatedAt(LocalDate.now());
        Customer saved = customerRepository.save(customer);
        return mapper.toDto(saved);
    }

    @Override
    public CustomerResponseDto update(Long id, CustomerRequestDto dto) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        c.setName(dto.getName());
        c.setAddress(dto.getAddress());
        c.setCity(dto.getCity());
        c.setPhone(dto.getPhone());
        c.setUpdatedAt(LocalDateTime.now());
        Customer saved = customerRepository.save(c);
        return mapper.toDto(saved);
    }

    @Override
    public Page<CustomerResponseDto> searchByName(String name, Pageable pageable) {
        return customerRepository.findByNameContainingIgnoreCase(name, pageable).map(mapper::toDto);
    }

    @Override
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("customer not found ");
        }

//       just   if he has no  command

        customerRepository.deleteById(id);
    }
}
