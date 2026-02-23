package com.uacb.service;

import com.uacb.entity.*;
import com.uacb.repository.InstitutionRepository;
import com.uacb.repository.CourseRepository;
import com.uacb.repository.CreditRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstitutionService {
    
    private final InstitutionRepository institutionRepository;
    private final CourseRepository courseRepository;
    private final CreditRecordRepository creditRecordRepository;
    private final AuditLogService auditLogService;
    
    @Transactional
    public Institution createInstitution(User user, Institution institution) {
        institution.setUser(user);
        Institution saved = institutionRepository.save(institution);
        
        auditLogService.log(user, AuditLogService.Action.CREATE,
            "Institution", saved.getId(), "Institution created: " + saved.getInstitutionName());
        
        return saved;
    }
    
    public Optional<Institution> findByUserId(Long userId) {
        return institutionRepository.findByUserId(userId);
    }
    
    public Optional<Institution> findById(Long id) {
        return institutionRepository.findById(id);
    }
    
    public List<Institution> findAll() {
        return institutionRepository.findAll();
    }
    
    public List<Institution> findVerified() {
        return institutionRepository.findByIsVerifiedTrue();
    }
    
    public long countAllInstitutions() {
        return institutionRepository.countAllInstitutions();
    }
    
    @Transactional
    public Course addCourse(Institution institution, Course course) {
        course.setInstitution(institution);
        Course saved = courseRepository.save(course);
        
        auditLogService.log(institution.getUser(), AuditLogService.Action.CREATE,
            "Course", saved.getId(), "Course added: " + saved.getCourseName());
        
        return saved;
    }
    
    public List<Course> getInstitutionCourses(Long institutionId) {
        return courseRepository.findByInstitutionId(institutionId);
    }
    
    @Transactional
    public CreditRecord issueCredit(Institution institution, StudentProfile student, 
                                     Course course, CreditRecord creditRecord) {
        creditRecord.setIssuingInstitution(institution);
        creditRecord.setStudent(student);
        creditRecord.setCourse(course);
        creditRecord.setStatus(CreditRecord.Status.PENDING);
        
        CreditRecord saved = creditRecordRepository.save(creditRecord);
        
        auditLogService.log(institution.getUser(), AuditLogService.Action.CREDIT_ISSUED,
            "CreditRecord", saved.getId(), 
            "Credit issued to student: " + student.getEnrollmentNumber() + 
            " for course: " + course.getCourseName());
        
        return saved;
    }
    
    public List<CreditRecord> getIssuedCredits(Long institutionId) {
        return creditRecordRepository.findByIssuingInstitutionId(institutionId);
    }
    
    public Long getTotalCreditsIssued(Long institutionId) {
        Long total = creditRecordRepository.sumVerifiedCreditsByInstitution(institutionId);
        return total != null ? total : 0L;
    }
}
