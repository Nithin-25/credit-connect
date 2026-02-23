package com.uacb.repository;

import com.uacb.entity.DegreeProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DegreeProgramRepository extends JpaRepository<DegreeProgram, Long> {
    
    Optional<DegreeProgram> findByProgramCode(String programCode);
    
    List<DegreeProgram> findByDegreeType(DegreeProgram.DegreeType degreeType);
    
    List<DegreeProgram> findByIsActiveTrue();
}
