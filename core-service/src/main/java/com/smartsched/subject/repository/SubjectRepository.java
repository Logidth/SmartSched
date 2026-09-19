package com.smartsched.subject.repository;

import com.smartsched.common.enums.Status;
import com.smartsched.subject.entity.Subject;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findBySubjectCodeIgnoreCaseAndRegulationId(
            String subjectCode,
            Long regulationId
    );
    @Query("""
SELECT s
FROM Subject s
JOIN FETCH s.regulation
""")
    List<Subject> findAllWithRegulation();
    @Query("""
SELECT s
FROM Subject s
JOIN FETCH s.regulation
WHERE s.id=:id
""")
    Optional<Subject> findByIdWithRegulation(Long id);

    @Query("""
SELECT s
FROM Subject s
JOIN FETCH s.regulation
WHERE s.regulation.id = :regulationId
""")
    List<Subject> findByRegulationIdWithRelations(Long regulationId);


    List<Subject> findByStatus(Status status);
    long count();


}