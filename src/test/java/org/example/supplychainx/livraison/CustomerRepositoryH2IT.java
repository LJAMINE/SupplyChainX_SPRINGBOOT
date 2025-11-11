package org.example.supplychainx.livraison;

import org.example.supplychainx.livraison.entity.Customer;
import org.example.supplychainx.livraison.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryH2IT {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void save_findById_and_searchByName_shouldWorkWithH2() {
        Customer c = new Customer();
        c.setName("ACME Corp");
        c.setAddress("rue 1");
        c.setCity("Casablanca");
        c.setPhone("0612345678");
        c.setCreatedAt(LocalDate.now());

        Customer saved = customerRepository.save(c);
        assertThat(saved.getId()).isNotNull();

        Customer found = customerRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("ACME Corp");

         var page = customerRepository.findByNameContainingIgnoreCase("acme", org.springframework.data.domain.PageRequest.of(0, 10));
        List<Customer> results = page.getContent();
        assertThat(results).isNotEmpty();
    }
}