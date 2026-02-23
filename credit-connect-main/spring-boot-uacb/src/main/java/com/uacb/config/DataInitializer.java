package com.uacb.config;

import com.uacb.entity.*;
import com.uacb.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final InstitutionRepository institutionRepository;
    private final CourseRepository courseRepository;
    private final DegreeProgramRepository degreeProgramRepository;
    private final CreditRecordRepository creditRecordRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void run(String... args) {
        // Only initialize if no users exist
        if (userRepository.count() > 0) {
            log.info("Database already initialized. Skipping...");
            return;
        }
        
        log.info("Initializing sample data...");
        
        // Create Admin User
        User admin = new User();
        admin.setName("System Administrator");
        admin.setEmail("admin@uacb.gov.in");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.ADMIN);
        userRepository.save(admin);
        log.info("Created admin user: admin@uacb.gov.in / admin123");
        
        // Create Degree Program
        DegreeProgram btech = new DegreeProgram();
        btech.setProgramName("Bachelor of Technology in Computer Science");
        btech.setProgramCode("BTECH-CS");
        btech.setDegreeType(DegreeProgram.DegreeType.BACHELOR);
        btech.setTotalCreditsRequired(160);
        btech.setDurationYears(4);
        btech.setMinCoreCredits(100);
        btech.setMinElectiveCredits(40);
        degreeProgramRepository.save(btech);
        
        // Create Institution 1
        User instUser1 = new User();
        instUser1.setName("IIT Delhi");
        instUser1.setEmail("admin@iitd.ac.in");
        instUser1.setPassword(passwordEncoder.encode("inst123"));
        instUser1.setRole(User.Role.INSTITUTION);
        userRepository.save(instUser1);
        
        Institution iitd = new Institution();
        iitd.setUser(instUser1);
        iitd.setInstitutionName("Indian Institute of Technology Delhi");
        iitd.setInstitutionCode("IITD");
        iitd.setInstitutionType(Institution.InstitutionType.AUTONOMOUS_INSTITUTION);
        iitd.setCity("New Delhi");
        iitd.setState("Delhi");
        iitd.setIsVerified(true);
        institutionRepository.save(iitd);
        log.info("Created institution: admin@iitd.ac.in / inst123");
        
        // Create Courses for IIT Delhi
        Course dsa = createCourse("CS101", "Data Structures & Algorithms", 4, Course.CourseType.CORE, iitd, btech);
        Course dbms = createCourse("CS201", "Database Management Systems", 4, Course.CourseType.CORE, iitd, btech);
        Course os = createCourse("CS301", "Operating Systems", 3, Course.CourseType.CORE, iitd, btech);
        Course ml = createCourse("CS401", "Machine Learning", 3, Course.CourseType.ELECTIVE, iitd, btech);
        
        // Create Student 1
        User studentUser1 = new User();
        studentUser1.setName("Rahul Sharma");
        studentUser1.setEmail("rahul@student.ac.in");
        studentUser1.setPassword(passwordEncoder.encode("student123"));
        studentUser1.setRole(User.Role.STUDENT);
        userRepository.save(studentUser1);
        
        StudentProfile student1 = new StudentProfile();
        student1.setUser(studentUser1);
        student1.setEnrollmentNumber("2021CS001");
        student1.setDateOfBirth(LocalDate.of(2000, 5, 15));
        student1.setPhoneNumber("9876543210");
        student1.setCurrentInstitution(iitd);
        student1.setDegreeProgram(btech);
        studentProfileRepository.save(student1);
        log.info("Created student: rahul@student.ac.in / student123");
        
        // Create Credit Records for Student 1
        createCreditRecord(student1, dsa, iitd, "A", 85.0, CreditRecord.Status.VERIFIED, admin);
        createCreditRecord(student1, dbms, iitd, "A-", 78.0, CreditRecord.Status.VERIFIED, admin);
        createCreditRecord(student1, os, iitd, "B+", 72.0, CreditRecord.Status.PENDING, null);
        
        // Create Institution 2
        User instUser2 = new User();
        instUser2.setName("Delhi University");
        instUser2.setEmail("admin@du.ac.in");
        instUser2.setPassword(passwordEncoder.encode("inst123"));
        instUser2.setRole(User.Role.INSTITUTION);
        userRepository.save(instUser2);
        
        Institution du = new Institution();
        du.setUser(instUser2);
        du.setInstitutionName("University of Delhi");
        du.setInstitutionCode("DU");
        du.setInstitutionType(Institution.InstitutionType.UNIVERSITY);
        du.setCity("Delhi");
        du.setState("Delhi");
        du.setIsVerified(true);
        institutionRepository.save(du);
        
        // Create Student 2
        User studentUser2 = new User();
        studentUser2.setName("Priya Singh");
        studentUser2.setEmail("priya@student.ac.in");
        studentUser2.setPassword(passwordEncoder.encode("student123"));
        studentUser2.setRole(User.Role.STUDENT);
        userRepository.save(studentUser2);
        
        StudentProfile student2 = new StudentProfile();
        student2.setUser(studentUser2);
        student2.setEnrollmentNumber("2021CS002");
        student2.setDateOfBirth(LocalDate.of(2001, 3, 22));
        student2.setCurrentInstitution(iitd);
        student2.setDegreeProgram(btech);
        studentProfileRepository.save(student2);
        
        createCreditRecord(student2, dsa, iitd, "A+", 92.0, CreditRecord.Status.VERIFIED, admin);
        createCreditRecord(student2, ml, iitd, "A", 88.0, CreditRecord.Status.PENDING, null);
        
        log.info("Sample data initialization complete!");
        log.info("===========================================");
        log.info("Login Credentials:");
        log.info("Admin: admin@uacb.gov.in / admin123");
        log.info("Institution: admin@iitd.ac.in / inst123");
        log.info("Student: rahul@student.ac.in / student123");
        log.info("===========================================");
    }
    
    private Course createCourse(String code, String name, int credits, 
                                 Course.CourseType type, Institution inst, DegreeProgram program) {
        Course course = new Course();
        course.setCourseCode(code);
        course.setCourseName(name);
        course.setCreditValue(credits);
        course.setCourseType(type);
        course.setInstitution(inst);
        course.setDegreeProgram(program);
        return courseRepository.save(course);
    }
    
    private CreditRecord createCreditRecord(StudentProfile student, Course course, 
                                             Institution inst, String grade, Double marks,
                                             CreditRecord.Status status, User verifier) {
        CreditRecord cr = new CreditRecord();
        cr.setStudent(student);
        cr.setCourse(course);
        cr.setIssuingInstitution(inst);
        cr.setGrade(grade);
        cr.setMarksObtained(marks);
        cr.setMaxMarks(100.0);
        cr.setCompletionDate(LocalDate.now().minusMonths(3));
        cr.setAcademicYear("2023-24");
        cr.setStatus(status);
        if (status == CreditRecord.Status.VERIFIED && verifier != null) {
            cr.setVerifiedBy(verifier);
            cr.setVerificationDate(java.time.LocalDateTime.now());
        }
        return creditRecordRepository.save(cr);
    }
}
