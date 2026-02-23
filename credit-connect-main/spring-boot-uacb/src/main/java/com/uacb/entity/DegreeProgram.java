package com.uacb.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "degree_programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DegreeProgram {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Program name is required")
    @Column(name = "program_name", nullable = false)
    private String programName;
    
    @Column(name = "program_code", unique = true)
    private String programCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "degree_type")
    private DegreeType degreeType;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Min(value = 1, message = "Total credits must be at least 1")
    @Column(name = "total_credits_required", nullable = false)
    private Integer totalCreditsRequired;
    
    @Column(name = "duration_years")
    private Integer durationYears;
    
    @Column(name = "min_core_credits")
    private Integer minCoreCredits;
    
    @Column(name = "min_elective_credits")
    private Integer minElectiveCredits;
    
    @OneToMany(mappedBy = "degreeProgram", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses;
    
    @OneToMany(mappedBy = "degreeProgram", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentProfile> enrolledStudents;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    public enum DegreeType {
        BACHELOR, MASTER, DOCTORATE, DIPLOMA, CERTIFICATE
    }
}
