package com.uacb.controller;

import com.uacb.entity.User;
import com.uacb.entity.StudentProfile;
import com.uacb.entity.Institution;
import com.uacb.service.UserService;
import com.uacb.service.StudentService;
import com.uacb.service.InstitutionService;
import com.uacb.dto.RegistrationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final StudentService studentService;
    private final InstitutionService institutionService;
    
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        return "login";
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registration", new RegistrationDto());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registration") RegistrationDto dto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        
        try {
            User user = new User();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPassword(dto.getPassword());
            user.setRole(User.Role.valueOf(dto.getRole()));
            
            User savedUser = userService.registerUser(user);
            
            // Create role-specific profile
            if (dto.getRole().equals("STUDENT")) {
                StudentProfile profile = new StudentProfile();
                // Generate unique enrollment number if not provided
                String enrollmentNumber = dto.getEnrollmentNumber();
                if (enrollmentNumber == null || enrollmentNumber.trim().isEmpty()) {
                    enrollmentNumber = "STU" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                }
                profile.setEnrollmentNumber(enrollmentNumber);
                studentService.createStudentProfile(savedUser, profile);
            } else if (dto.getRole().equals("INSTITUTION")) {
                Institution institution = new Institution();
                institution.setInstitutionName(dto.getInstitutionName());
                // Generate unique institution code if not provided
                String institutionCode = dto.getInstitutionCode();
                if (institutionCode == null || institutionCode.trim().isEmpty()) {
                    institutionCode = "INST" + System.currentTimeMillis();
                }
                institution.setInstitutionCode(institutionCode);
                institutionService.createInstitution(savedUser, institution);
            }
            
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}
