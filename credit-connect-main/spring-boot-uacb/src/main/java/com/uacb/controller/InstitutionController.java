package com.uacb.controller;

import com.uacb.entity.*;
import com.uacb.service.*;
import com.uacb.dto.CourseDto;
import com.uacb.dto.CreditIssueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/institution")
@RequiredArgsConstructor
public class InstitutionController {
    
    private final UserService userService;
    private final InstitutionService institutionService;
    private final StudentService studentService;
    
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Institution institution = institutionService.findByUserId(user.getId())
            .orElseThrow(() -> new RuntimeException("Institution not found"));
        
        List<Course> courses = institutionService.getInstitutionCourses(institution.getId());
        List<CreditRecord> issuedCredits = institutionService.getIssuedCredits(institution.getId());
        Long totalCreditsIssued = institutionService.getTotalCreditsIssued(institution.getId());
        
        // Group credits by status
        Map<CreditRecord.Status, Long> creditsByStatus = issuedCredits.stream()
            .collect(Collectors.groupingBy(CreditRecord::getStatus, Collectors.counting()));
        
        model.addAttribute("institution", institution);
        model.addAttribute("courses", courses);
        model.addAttribute("issuedCredits", issuedCredits);
        model.addAttribute("totalCreditsIssued", totalCreditsIssued);
        model.addAttribute("creditsByStatus", creditsByStatus);
        model.addAttribute("courseCount", courses.size());
        
        return "institution/dashboard";
    }
    
    @GetMapping("/courses")
    public String manageCourses(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Institution institution = institutionService.findByUserId(user.getId())
            .orElseThrow(() -> new RuntimeException("Institution not found"));
        
        List<Course> courses = institutionService.getInstitutionCourses(institution.getId());
        
        model.addAttribute("courses", courses);
        model.addAttribute("courseDto", new CourseDto());
        
        return "institution/courses";
    }
    
    @PostMapping("/courses/add")
    public String addCourse(@AuthenticationPrincipal UserDetails userDetails,
                           @Valid @ModelAttribute("courseDto") CourseDto dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "institution/courses";
        }
        
        User user = userService.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Institution institution = institutionService.findByUserId(user.getId())
            .orElseThrow(() -> new RuntimeException("Institution not found"));
        
        Course course = new Course();
        course.setCourseCode(dto.getCourseCode());
        course.setCourseName(dto.getCourseName());
        course.setDescription(dto.getDescription());
        course.setCreditValue(dto.getCreditValue());
        course.setCourseType(Course.CourseType.valueOf(dto.getCourseType()));
        course.setSemester(dto.getSemester());
        
        institutionService.addCourse(institution, course);
        
        redirectAttributes.addFlashAttribute("success", "Course added successfully!");
        return "redirect:/institution/courses";
    }
    
    @GetMapping("/issue-credit")
    public String issueCreditPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Institution institution = institutionService.findByUserId(user.getId())
            .orElseThrow(() -> new RuntimeException("Institution not found"));
        
        List<Course> courses = institutionService.getInstitutionCourses(institution.getId());
        List<StudentProfile> students = studentService.findAll();
        
        model.addAttribute("courses", courses);
        model.addAttribute("students", students);
        model.addAttribute("creditDto", new CreditIssueDto());
        
        return "institution/issue-credit";
    }
    
    @PostMapping("/issue-credit")
    public String issueCredit(@AuthenticationPrincipal UserDetails userDetails,
                              @Valid @ModelAttribute("creditDto") CreditIssueDto dto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            return "institution/issue-credit";
        }
        
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            Institution institution = institutionService.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Institution not found"));
            
            StudentProfile student = studentService.findByEnrollmentNumber(dto.getEnrollmentNumber())
                .orElseThrow(() -> new RuntimeException("Student not found"));
            
            Course course = institutionService.getInstitutionCourses(institution.getId()).stream()
                .filter(c -> c.getId().equals(dto.getCourseId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Course not found"));
            
            CreditRecord creditRecord = new CreditRecord();
            creditRecord.setGrade(dto.getGrade());
            creditRecord.setMarksObtained(dto.getMarksObtained());
            creditRecord.setMaxMarks(dto.getMaxMarks());
            creditRecord.setCompletionDate(LocalDate.parse(dto.getCompletionDate()));
            creditRecord.setAcademicYear(dto.getAcademicYear());
            
            institutionService.issueCredit(institution, student, course, creditRecord);
            
            redirectAttributes.addFlashAttribute("success", "Credit issued successfully! Pending verification.");
            return "redirect:/institution/dashboard";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/institution/issue-credit";
        }
    }
    
    @GetMapping("/credits")
    public String viewIssuedCredits(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Institution institution = institutionService.findByUserId(user.getId())
            .orElseThrow(() -> new RuntimeException("Institution not found"));
        
        List<CreditRecord> issuedCredits = institutionService.getIssuedCredits(institution.getId());
        
        model.addAttribute("credits", issuedCredits);
        
        return "institution/credits";
    }
}
