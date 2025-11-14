package org.example.supplychainx.approvisionnement.repo;


import org.example.supplychainx.approvisionnement.entity.Supplier;
import org.example.supplychainx.approvisionnement.repository.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
public class SupplierepositoryTest {


    @Autowired
    private SupplierRepository supplierRepository;


    @Test
    public void testExistsById() {
        Supplier supplier = new Supplier(null, "amine", "agadir", 3.3, 4, null, LocalDateTime.now(), LocalDateTime.now());
        supplier = supplierRepository.save(supplier);
        boolean exists = supplierRepository.existsById(supplier.getId());
        assertThat(exists).isTrue();

    }

}
