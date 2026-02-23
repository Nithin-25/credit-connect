package com.uacb.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "enrollment_number", unique = true)
    private String enrollmentNumber;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    private String address;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution currentInstitution;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "degree_program_id")
    private DegreeProgram degreeProgram;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CreditRecord> creditRecords;
    
    @Column(name = "total_credits")
    private Integer totalCredits = 0;
    
    public void recalculateTotalCredits() {
        if (creditRecords != null) {
            this.totalCredits = creditRecords.stream()
                .filter(cr -> cr.getStatus() == CreditRecord.Status.VERIFIED)
                .mapToInt(cr -> cr.getCourse().getCreditValue())
                .sum();
        }
    }
}
