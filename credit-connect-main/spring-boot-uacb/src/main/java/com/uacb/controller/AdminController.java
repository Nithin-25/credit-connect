package com.uacb.controller;

import com.uacb.entity.*;
import com.uacb.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserService userService;
    private final StudentService studentService;
    private final InstitutionService institutionService;
    private final CreditVerificationService verificationService;
    private final AuditLogService auditLogService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Statistics
        long totalStudents = studentService.countAllStudents();
        long totalInstitutions = institutionService.countAllInstitutions();
        Long totalCreditsIssued = verificationService.sumAllVerifiedCredits();
        long pendingVerifications = verificationService.countPendingCredits();
        
        // Recent activities
        List<AuditLog> recentLogs = auditLogService.getRecentLogs();
        
        // Pending credits
        List<CreditRecord> pendingCredits = verificationService.getPendingCredits();
        
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalInstitutions", totalInstitutions);
        model.addAttribute("totalCreditsIssued", totalCreditsIssued);
        model.addAttribute("pendingVerifications", pendingVerifications);
        model.addAttribute("recentLogs", recentLogs.stream().limit(10).toList());
        model.addAttribute("pendingCredits", pendingCredits.stream().limit(5).toList());
        
        return "admin/dashboard";
    }
    
    @GetMapping("/students")
    public String viewStudents(Model model) {
        List<StudentProfile> students = studentService.findAll();
        model.addAttribute("students", students);
        return "admin/students";
    }
    
    @GetMapping("/institutions")
    public String viewInstitutions(Model model) {
        List<Institution> institutions = institutionService.findAll();
        model.addAttribute("institutions", institutions);
        return "admin/institutions";
    }
    
    @GetMapping("/verifications")
    public String pendingVerifications(Model model) {
        List<CreditRecord> pendingCredits = verificationService.getPendingCredits();
        model.addAttribute("pendingCredits", pendingCredits);
        return "admin/verifications";
    }
    
    @PostMapping("/verify/{creditId}")
    public String verifyCredit(@PathVariable Long creditId,
                               @RequestParam String action,
                               @RequestParam(required = false) String remarks,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            User admin = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
            
            if ("approve".equals(action)) {
                verificationService.verifyCredit(creditId, admin, remarks);
                redirectAttributes.addFlashAttribute("success", "Credit verified successfully!");
            } else if ("reject".equals(action)) {
                verificationService.rejectCredit(creditId, admin, remarks);
                redirectAttributes.addFlashAttribute("success", "Credit rejected.");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/verifications";
    }
    
    @GetMapping("/audit-logs")
    public String viewAuditLogs(@RequestParam(defaultValue = "0") int page, Model model) {
        var logs = auditLogService.getAllLogs(PageRequest.of(page, 50));
        model.addAttribute("logs", logs);
        model.addAttribute("currentPage", page);
        return "admin/audit-logs";
    }
    
    @GetMapping("/analytics")
    public String viewAnalytics(Model model) {
        // Get analytics data
        long totalStudents = studentService.countAllStudents();
        long totalInstitutions = institutionService.countAllInstitutions();
        Long totalCreditsIssued = verificationService.sumAllVerifiedCredits();
        long verifiedCredits = verificationService.countVerifiedCredits();
        long pendingCredits = verificationService.countPendingCredits();
        
        // Institution-wise credits
        List<Institution> institutions = institutionService.findAll();
        
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalInstitutions", totalInstitutions);
        model.addAttribute("totalCreditsIssued", totalCreditsIssued);
        model.addAttribute("verifiedCredits", verifiedCredits);
        model.addAttribute("pendingCredits", pendingCredits);
        model.addAttribute("institutions", institutions);
        
        return "admin/analytics";
    }
}
