package com.uacb.controller;

import com.uacb.entity.*;
import com.uacb.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
    
    private final UserService userService;
    private final StudentService studentService;
    private final AuditLogService auditLogService;
    
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login?error=user_not_found";
        }
        
        StudentProfile profile = studentService.findByUserId(user.getId()).orElse(null);
        if (profile == null) {
            // Profile doesn't exist yet - create a basic one with unique enrollment number
            profile = new StudentProfile();
            profile.setUser(user);
            profile.setEnrollmentNumber("STU" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
            profile = studentService.updateProfile(profile);
        }
        
        int totalCredits = 0;
        double progress = 0.0;
        List<CreditRecord> credits = new ArrayList<>();
        
        try {
            totalCredits = studentService.calculateTotalCredits(profile.getId());
            progress = studentService.calculateDegreeProgress(profile.getId());
            credits = studentService.getStudentCredits(profile.getId());
        } catch (Exception e) {
            // Handle case where no credits exist yet
        }
        
        // Group credits by status safely
        Map<CreditRecord.Status, List<CreditRecord>> creditsByStatus = credits.stream()
            .collect(Collectors.groupingBy(CreditRecord::getStatus));
        
        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("totalCredits", totalCredits);
        model.addAttribute("progress", progress);
        model.addAttribute("credits", credits);
        model.addAttribute("verifiedCredits", creditsByStatus.getOrDefault(CreditRecord.Status.VERIFIED, new ArrayList<>()));
        model.addAttribute("pendingCredits", creditsByStatus.getOrDefault(CreditRecord.Status.PENDING, new ArrayList<>()));
        
        return "student/dashboard";
    }
    
    @GetMapping("/wallet")
    public String creditWallet(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login?error=user_not_found";
        }
        
        StudentProfile profile = studentService.findByUserId(user.getId()).orElse(null);
        if (profile == null) {
            return "redirect:/student/dashboard";
        }
        
        List<CreditRecord> verifiedCredits = studentService.getVerifiedCredits(profile.getId());
        int totalCredits = studentService.calculateTotalCredits(profile.getId());
        
        // Group by course type
        Map<Course.CourseType, Integer> creditsByType = verifiedCredits.stream()
            .collect(Collectors.groupingBy(
                cr -> cr.getCourse().getCourseType(),
                Collectors.summingInt(cr -> cr.getCourse().getCreditValue())
            ));
        
        model.addAttribute("profile", profile);
        model.addAttribute("verifiedCredits", verifiedCredits);
        model.addAttribute("totalCredits", totalCredits);
        model.addAttribute("creditsByType", creditsByType);
        
        if (profile.getDegreeProgram() != null) {
            model.addAttribute("requiredCredits", profile.getDegreeProgram().getTotalCreditsRequired());
            model.addAttribute("progress", studentService.calculateDegreeProgress(profile.getId()));
        }
        
        return "student/wallet";
    }
    
    @GetMapping("/history")
    public String creditHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login?error=user_not_found";
        }
        
        StudentProfile profile = studentService.findByUserId(user.getId()).orElse(null);
        if (profile == null) {
            return "redirect:/student/dashboard";
        }
        
        List<CreditRecord> allCredits = studentService.getStudentCredits(profile.getId());
        List<AuditLog> auditLogs = auditLogService.getLogsByUser(user.getId());
        
        model.addAttribute("credits", allCredits);
        model.addAttribute("auditLogs", auditLogs);
        
        return "student/history";
    }
    
    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login?error=user_not_found";
        }
        
        StudentProfile profile = studentService.findByUserId(user.getId()).orElse(null);
        if (profile == null) {
            return "redirect:/student/dashboard";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        
        return "student/profile";
    }
}
