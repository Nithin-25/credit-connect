package com.uacb.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Course code is required")
    @Column(name = "course_code", nullable = false)
    private String courseCode;
    
    @NotBlank(message = "Course name is required")
    @Column(name = "course_name", nullable = false)
    private String courseName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Min(value = 1, message = "Credit value must be at least 1")
    @Column(name = "credit_value", nullable = false)
    private Integer creditValue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "course_type")
    private CourseType courseType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "degree_program_id")
    private DegreeProgram degreeProgram;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CreditRecord> creditRecords;
    
    @Column(name = "semester")
    private Integer semester;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum CourseType {
        CORE, ELECTIVE, OPEN_ELECTIVE, LAB, PROJECT
    }
}
