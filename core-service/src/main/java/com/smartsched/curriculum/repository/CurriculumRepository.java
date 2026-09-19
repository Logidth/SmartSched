package com.smartsched.curriculum.repository;

import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.branch.entity.Branch;
import com.smartsched.common.enums.Status;
import com.smartsched.curriculum.entity.Curriculum;
import com.smartsched.regulation.entity.Regulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface CurriculumRepository
        extends JpaRepository<Curriculum, Long> {

    Optional<Curriculum> findByBranchIdAndRegulationIdAndAcademicYearId(
            Long branchId,
            Long regulationId,
            Long academicYearId
    );
    @Query("""
SELECT c
FROM Curriculum c
JOIN FETCH c.branch
JOIN FETCH c.regulation
JOIN FETCH c.academicYear
""")
    List<Curriculum> findAllWithRelations();


    @Query("""
SELECT c
FROM Curriculum c
JOIN FETCH c.branch
JOIN FETCH c.regulation
JOIN FETCH c.academicYear
WHERE c.branch.id=:branchId
""")
    List<Curriculum> findByBranchIdWithRelations(Long branchId);

    @Query("""
SELECT c
FROM Curriculum c
JOIN FETCH c.branch
JOIN FETCH c.regulation
JOIN FETCH c.academicYear
WHERE c.regulation.id = :regulationId
""")
    List<Curriculum> findByRegulationIdWithRelations(
            @Param("regulationId") Long regulationId);
    @Query("""
SELECT c
FROM Curriculum c
JOIN FETCH c.branch
JOIN FETCH c.regulation
JOIN FETCH c.academicYear
WHERE c.academicYear.id = :academicYearId
""")
    List<Curriculum> findByAcademicYearIdWithRelations(Long academicYearId);

    List<Curriculum> findByBranchId(Long branchId);

    List<Curriculum> findByRegulationId(Long regulationId);

    List<Curriculum> findByAcademicYearId(Long academicYearId);

    List<Curriculum> findByStatus(Status status);

    @Query("""
SELECT c
FROM Curriculum c
JOIN FETCH c.branch
JOIN FETCH c.regulation
JOIN FETCH c.academicYear
WHERE c.id=:id
""")
    Optional<Curriculum> findByIdWithRelations(Long id);

    Optional<Curriculum> findByBranchAndRegulationAndAcademicYear(
            Branch branch,
            Regulation regulation,
            AcademicYear academicYear
    );

}