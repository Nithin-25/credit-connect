package com.uacb.service;

import com.uacb.entity.*;
import com.uacb.repository.CreditRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditVerificationService {
    
    private final CreditRecordRepository creditRecordRepository;
    private final AuditLogService auditLogService;
    
    public List<CreditRecord> getPendingCredits() {
        return creditRecordRepository.findAllPendingCredits();
    }
    
    public Optional<CreditRecord> findById(Long id) {
        return creditRecordRepository.findById(id);
    }
    
    @Transactional
    public CreditRecord verifyCredit(Long creditId, User verifier, String remarks) {
        CreditRecord credit = creditRecordRepository.findById(creditId)
            .orElseThrow(() -> new RuntimeException("Credit record not found"));
        
        if (credit.getStatus() != CreditRecord.Status.PENDING) {
            throw new RuntimeException("Credit is not in pending status");
        }
        
        credit.setStatus(CreditRecord.Status.VERIFIED);
        credit.setVerifiedBy(verifier);
        credit.setVerificationDate(LocalDateTime.now());
        credit.setVerificationRemarks(remarks);
        
        CreditRecord saved = creditRecordRepository.save(credit);
        
        // Update student's total credits
        StudentProfile student = credit.getStudent();
        student.recalculateTotalCredits();
        
        auditLogService.log(verifier, AuditLogService.Action.CREDIT_VERIFIED,
            "CreditRecord", saved.getId(),
            "Credit verified for student: " + student.getEnrollmentNumber());
        
        return saved;
    }
    
    @Transactional
    public CreditRecord rejectCredit(Long creditId, User verifier, String remarks) {
        CreditRecord credit = creditRecordRepository.findById(creditId)
            .orElseThrow(() -> new RuntimeException("Credit record not found"));
        
        if (credit.getStatus() != CreditRecord.Status.PENDING) {
            throw new RuntimeException("Credit is not in pending status");
        }
        
        credit.setStatus(CreditRecord.Status.REJECTED);
        credit.setVerifiedBy(verifier);
        credit.setVerificationDate(LocalDateTime.now());
        credit.setVerificationRemarks(remarks);
        
        CreditRecord saved = creditRecordRepository.save(credit);
        
        auditLogService.log(verifier, AuditLogService.Action.CREDIT_REJECTED,
            "CreditRecord", saved.getId(),
            "Credit rejected for student: " + credit.getStudent().getEnrollmentNumber() +
            ". Reason: " + remarks);
        
        return saved;
    }
    
    public long countVerifiedCredits() {
        return creditRecordRepository.countVerifiedCredits();
    }
    
    public long countPendingCredits() {
        return creditRecordRepository.countPendingCredits();
    }
    
    public Long sumAllVerifiedCredits() {
        Long total = creditRecordRepository.sumAllVerifiedCredits();
        return total != null ? total : 0L;
    }
}
