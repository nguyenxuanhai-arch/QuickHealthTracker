package com.vaccine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccineDTO {
    
    private Long id;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String description;
    
    @NotBlank
    private String manufacturer;
    
    @NotNull
    private Integer recommendedAgeMonthsStart;
    
    private Integer recommendedAgeMonthsEnd;
    
    private Boolean isRequired;
    
    private String preventedDiseases;
    
    private String sideEffects;
    
    @NotNull
    @Positive
    private Double price;
}
