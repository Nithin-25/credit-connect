package com.uacb.repository;

import com.uacb.entity.Course;
import com.uacb.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByInstitution(Institution institution);
    
    List<Course> findByInstitutionId(Long institutionId);
    
    Optional<Course> findByCourseCodeAndInstitutionId(String courseCode, Long institutionId);
    
    @Query("SELECT c FROM Course c WHERE c.degreeProgram.id = :programId")
    List<Course> findByDegreeProgramId(@Param("programId") Long programId);
    
    List<Course> findByIsActiveTrue();
    
    @Query("SELECT SUM(c.creditValue) FROM Course c WHERE c.institution.id = :institutionId")
    Integer sumCreditsByInstitution(@Param("institutionId") Long institutionId);
}
