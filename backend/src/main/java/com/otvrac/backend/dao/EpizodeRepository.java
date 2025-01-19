package com.otvrac.backend.dao;

import com.otvrac.backend.domain.Epizode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EpizodeRepository extends JpaRepository<Epizode, Integer>, JpaSpecificationExecutor<Epizode> {
    List<Epizode> findBySerijaId(Integer serijaId);
}
