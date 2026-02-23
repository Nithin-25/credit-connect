package com.uacb.controller.api;

import com.uacb.entity.CreditRecord;
import com.uacb.entity.StudentProfile;
import com.uacb.service.StudentService;
import com.uacb.service.CreditVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditApiController {
    
    private final StudentService studentService;
    private final CreditVerificationService verificationService;
    
    @GetMapping("/student/{enrollmentNumber}")
    public ResponseEntity<?> getStudentCredits(@PathVariable String enrollmentNumber) {
        try {
            StudentProfile student = studentService.findByEnrollmentNumber(enrollmentNumber)
                .orElseThrow(() -> new RuntimeException("Student not found"));
            
            List<CreditRecord> credits = studentService.getStudentCredits(student.getId());
            int totalCredits = studentService.calculateTotalCredits(student.getId());
            double progress = studentService.calculateDegreeProgress(student.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("student", Map.of(
                "name", student.getUser().getName(),
                "enrollmentNumber", student.getEnrollmentNumber(),
                "email", student.getUser().getEmail()
            ));
            response.put("totalCredits", totalCredits);
            response.put("degreeProgress", progress);
            response.put("credits", credits.stream().map(cr -> Map.of(
                "id", cr.getId(),
                "courseName", cr.getCourse().getCourseName(),
                "courseCode", cr.getCourse().getCourseCode(),
                "creditValue", cr.getCourse().getCreditValue(),
                "grade", cr.getGrade(),
                "status", cr.getStatus().name(),
                "institution", cr.getIssuingInstitution().getInstitutionName(),
                "completionDate", cr.getCompletionDate().toString()
            )).toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/verify/{certificateNumber}")
    public ResponseEntity<?> verifyCertificate(@PathVariable String certificateNumber) {
        // This endpoint can be used for external verification of credits
        return ResponseEntity.ok(Map.of(
            "message", "Certificate verification endpoint",
            "certificateNumber", certificateNumber
        ));
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        long verified = verificationService.countVerifiedCredits();
        long pending = verificationService.countPendingCredits();
        Long total = verificationService.sumAllVerifiedCredits();
        
        return ResponseEntity.ok(Map.of(
            "verifiedCount", verified,
            "pendingCount", pending,
            "totalCreditsIssued", total != null ? total : 0
        ));
    }
}
