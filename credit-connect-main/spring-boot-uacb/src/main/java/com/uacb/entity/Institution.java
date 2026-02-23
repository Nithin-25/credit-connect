package com.uacb.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "institutions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Institution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotBlank(message = "Institution name is required")
    @Column(name = "institution_name", nullable = false)
    private String institutionName;
    
    @Column(name = "institution_code", unique = true)
    private String institutionCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "institution_type")
    private InstitutionType institutionType;
    
    private String address;
    
    private String city;
    
    private String state;
    
    @Column(name = "pincode")
    private String pincode;
    
    @Column(name = "contact_number")
    private String contactNumber;
    
    @Column(name = "website_url")
    private String websiteUrl;
    
    @Column(name = "accreditation_status")
    private String accreditationStatus;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses;
    
    @OneToMany(mappedBy = "issuingInstitution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CreditRecord> issuedCredits;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum InstitutionType {
        UNIVERSITY, COLLEGE, AUTONOMOUS_INSTITUTION, DEEMED_UNIVERSITY
    }
}
