package org.example.supplychainx.approvisionnement.repository;

import org.example.supplychainx.approvisionnement.entity.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SupplierRepositoryIT {

    @Autowired
    private SupplierRepository repository;

    @Test
    void saveAnd_findByNameContainingIgnoreCase_pageResults() {
        Supplier s1 = new Supplier();
        s1.setName("ACME Supplies");
        s1.setContact("acme@example.com");

        Supplier s2 = new Supplier();
        s2.setName("OtherCo");
        s2.setContact("other@example.com");

        repository.save(s1);
        repository.save(s2);

        Page<Supplier> page = repository.findByNameContainingIgnoreCase("acme", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(1);
        assertThat(page.getContent().get(0).getName()).containsIgnoringCase("acme");
    }
}
