package com.uacb.repository;

import com.uacb.entity.CreditRecord;
import com.uacb.entity.StudentProfile;
import com.uacb.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRecordRepository extends JpaRepository<CreditRecord, Long> {
    
    List<CreditRecord> findByStudent(StudentProfile student);
    
    List<CreditRecord> findByStudentId(Long studentId);
    
    List<CreditRecord> findByIssuingInstitution(Institution institution);
    
    List<CreditRecord> findByIssuingInstitutionId(Long institutionId);
    
    List<CreditRecord> findByStatus(CreditRecord.Status status);
    
    @Query("SELECT cr FROM CreditRecord cr WHERE cr.status = 'PENDING'")
    List<CreditRecord> findAllPendingCredits();
    
    @Query("SELECT COUNT(cr) FROM CreditRecord cr WHERE cr.status = 'VERIFIED'")
    long countVerifiedCredits();
    
    @Query("SELECT COUNT(cr) FROM CreditRecord cr WHERE cr.status = 'PENDING'")
    long countPendingCredits();
    
    @Query("SELECT SUM(cr.course.creditValue) FROM CreditRecord cr WHERE cr.status = 'VERIFIED'")
    Long sumAllVerifiedCredits();
    
    @Query("SELECT SUM(cr.course.creditValue) FROM CreditRecord cr WHERE cr.issuingInstitution.id = :institutionId AND cr.status = 'VERIFIED'")
    Long sumVerifiedCreditsByInstitution(@Param("institutionId") Long institutionId);
    
    @Query("SELECT cr FROM CreditRecord cr WHERE cr.student.id = :studentId AND cr.status = 'VERIFIED'")
    List<CreditRecord> findVerifiedCreditsByStudent(@Param("studentId") Long studentId);
}
