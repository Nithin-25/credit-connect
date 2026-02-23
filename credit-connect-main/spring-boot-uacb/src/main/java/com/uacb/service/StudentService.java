package com.uacb.service;

import com.uacb.entity.*;
import com.uacb.repository.StudentProfileRepository;
import com.uacb.repository.CreditRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {
    
    private final StudentProfileRepository studentProfileRepository;
    private final CreditRecordRepository creditRecordRepository;
    private final UserService userService;
    
    @Transactional
    public StudentProfile createStudentProfile(User user, StudentProfile profile) {
        profile.setUser(user);
        return studentProfileRepository.save(profile);
    }
    
    public Optional<StudentProfile> findByUserId(Long userId) {
        return studentProfileRepository.findByUserId(userId);
    }
    
    public Optional<StudentProfile> findById(Long id) {
        return studentProfileRepository.findById(id);
    }
    
    public Optional<StudentProfile> findByEnrollmentNumber(String enrollmentNumber) {
        return studentProfileRepository.findByEnrollmentNumber(enrollmentNumber);
    }
    
    public List<StudentProfile> findAll() {
        return studentProfileRepository.findAll();
    }
    
    public long countAllStudents() {
        return studentProfileRepository.countAllStudents();
    }
    
    @Transactional
    public StudentProfile updateProfile(StudentProfile profile) {
        return studentProfileRepository.save(profile);
    }
    
    public List<CreditRecord> getStudentCredits(Long studentId) {
        return creditRecordRepository.findByStudentId(studentId);
    }
    
    public List<CreditRecord> getVerifiedCredits(Long studentId) {
        return creditRecordRepository.findVerifiedCreditsByStudent(studentId);
    }
    
    public int calculateTotalCredits(Long studentId) {
        List<CreditRecord> verifiedCredits = getVerifiedCredits(studentId);
        return verifiedCredits.stream()
            .mapToInt(cr -> cr.getCourse().getCreditValue())
            .sum();
    }
    
    public double calculateDegreeProgress(Long studentId) {
        Optional<StudentProfile> profileOpt = findById(studentId);
        if (profileOpt.isEmpty() || profileOpt.get().getDegreeProgram() == null) {
            return 0.0;
        }
        
        StudentProfile profile = profileOpt.get();
        int totalRequired = profile.getDegreeProgram().getTotalCreditsRequired();
        int earned = calculateTotalCredits(studentId);
        
        return Math.min(100.0, (earned * 100.0) / totalRequired);
    }
}
