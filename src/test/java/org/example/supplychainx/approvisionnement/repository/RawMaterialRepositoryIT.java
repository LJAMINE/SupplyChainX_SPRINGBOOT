package org.example.supplychainx.approvisionnement.repository;

import org.example.supplychainx.approvisionnement.entity.RawMaterial;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RawMaterialRepositoryIT {

    @Autowired
    private RawMaterialRepository repository;

    @Test
    void saveAnd_findByNameContainingIgnoreCase_and_existsByName() {
        RawMaterial rm = new RawMaterial();
        rm.setName("Steel");
        rm.setUnit("kg");
        rm.setStock(100);

        RawMaterial saved = repository.save(rm);

        assertThat(saved.getId()).isNotNull();

        Page<RawMaterial> page = repository.findByNameContainingIgnoreCase("ste", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(1);
        assertThat(repository.existsByName("Steel")).isTrue();
    }
}
