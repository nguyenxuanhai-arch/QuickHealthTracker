package com.vaccine.dto;

import com.vaccine.model.Child;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildDTO {
    
    private Long id;
    
    @NotBlank
    private String fullName;
    
    @NotNull
    @Past
    private LocalDate dateOfBirth;
    
    @NotNull
    private Child.Gender gender;
    
    private String bloodType;
    
    private String allergies;
    
    private String medicalConditions;
}
