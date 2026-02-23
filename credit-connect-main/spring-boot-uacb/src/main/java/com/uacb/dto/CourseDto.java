package com.uacb.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourseDto {
    
    @NotBlank(message = "Course code is required")
    private String courseCode;
    
    @NotBlank(message = "Course name is required")
    private String courseName;
    
    private String description;
    
    @Min(value = 1, message = "Credit value must be at least 1")
    private Integer creditValue;
    
    @NotBlank(message = "Course type is required")
    private String courseType;
    
    private Integer semester;
}
