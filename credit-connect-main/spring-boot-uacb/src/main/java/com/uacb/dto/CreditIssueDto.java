package com.uacb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreditIssueDto {
    
    @NotBlank(message = "Enrollment number is required")
    private String enrollmentNumber;
    
    @NotNull(message = "Course is required")
    private Long courseId;
    
    @NotBlank(message = "Grade is required")
    private String grade;
    
    private Double marksObtained;
    
    private Double maxMarks;
    
    @NotBlank(message = "Completion date is required")
    private String completionDate;
    
    private String academicYear;
}
