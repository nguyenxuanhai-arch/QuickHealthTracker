package com.vaccine.dto;

import com.vaccine.model.Child;
// Lombok imports removed

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

// Lombok annotations removed
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
    
    // Constructors
    public ChildDTO() {
        // Default constructor
    }
    
    public ChildDTO(Long id, String fullName, LocalDate dateOfBirth, Child.Gender gender, 
                   String bloodType, String allergies, String medicalConditions) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.medicalConditions = medicalConditions;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Child.Gender getGender() {
        return gender;
    }

    public void setGender(Child.Gender gender) {
        this.gender = gender;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMedicalConditions() {
        return medicalConditions;
    }

    public void setMedicalConditions(String medicalConditions) {
        this.medicalConditions = medicalConditions;
    }
}
