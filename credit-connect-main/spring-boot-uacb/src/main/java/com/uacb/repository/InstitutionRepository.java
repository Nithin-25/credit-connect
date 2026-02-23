package com.uacb.repository;

import com.uacb.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    
    Optional<Institution> findByUserId(Long userId);
    
    Optional<Institution> findByInstitutionCode(String institutionCode);
    
    List<Institution> findByIsVerifiedTrue();
    
    List<Institution> findByIsVerifiedFalse();
    
    @Query("SELECT COUNT(i) FROM Institution i")
    long countAllInstitutions();
    
    @Query("SELECT COUNT(i) FROM Institution i WHERE i.isVerified = true")
    long countVerifiedInstitutions();
    
    List<Institution> findByInstitutionType(Institution.InstitutionType type);
}
