package com.uacb.repository;

import com.uacb.entity.StudentProfile;
import com.uacb.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    
    Optional<StudentProfile> findByUserId(Long userId);
    
    Optional<StudentProfile> findByEnrollmentNumber(String enrollmentNumber);
    
    List<StudentProfile> findByCurrentInstitution(Institution institution);
    
    @Query("SELECT sp FROM StudentProfile sp WHERE sp.degreeProgram.id = :programId")
    List<StudentProfile> findByDegreeProgramId(@Param("programId") Long programId);
    
    @Query("SELECT COUNT(sp) FROM StudentProfile sp")
    long countAllStudents();
    
    @Query("SELECT sp FROM StudentProfile sp JOIN FETCH sp.creditRecords WHERE sp.id = :id")
    Optional<StudentProfile> findByIdWithCreditRecords(@Param("id") Long id);
}
