package com.uacb.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuing_institution_id", nullable = false)
    private Institution issuingInstitution;
    
    @Column(name = "grade")
    private String grade;
    
    @Column(name = "marks_obtained")
    private Double marksObtained;
    
    @Column(name = "max_marks")
    private Double maxMarks;
    
    @Column(name = "completion_date")
    private LocalDate completionDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;
    
    @Column(name = "verification_date")
    private LocalDateTime verificationDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;
    
    @Column(name = "verification_remarks")
    private String verificationRemarks;
    
    @Column(name = "certificate_number", unique = true)
    private String certificateNumber;
    
    @Column(name = "academic_year")
    private String academicYear;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (certificateNumber == null) {
            certificateNumber = "UACB-" + System.currentTimeMillis();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum Status {
        PENDING, VERIFIED, REJECTED
    }
}
