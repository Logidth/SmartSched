package com.smartsched.regulation.repository;

import com.smartsched.common.enums.Status;
import com.smartsched.regulation.entity.Regulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegulationRepository
        extends JpaRepository<Regulation,Long> {

    Optional<Regulation> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);



    List<Regulation> findByStatus(Status status);
}