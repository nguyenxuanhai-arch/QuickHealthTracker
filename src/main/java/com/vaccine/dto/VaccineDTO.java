package com.vaccine.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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
    
    // Constructors
    public VaccineDTO() {
        // Default constructor
    }
    
    public VaccineDTO(Long id, String name, String description, String manufacturer, 
                     Integer recommendedAgeMonthsStart, Integer recommendedAgeMonthsEnd, 
                     Boolean isRequired, String preventedDiseases, String sideEffects, Double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.recommendedAgeMonthsStart = recommendedAgeMonthsStart;
        this.recommendedAgeMonthsEnd = recommendedAgeMonthsEnd;
        this.isRequired = isRequired;
        this.preventedDiseases = preventedDiseases;
        this.sideEffects = sideEffects;
        this.price = price;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getRecommendedAgeMonthsStart() {
        return recommendedAgeMonthsStart;
    }

    public void setRecommendedAgeMonthsStart(Integer recommendedAgeMonthsStart) {
        this.recommendedAgeMonthsStart = recommendedAgeMonthsStart;
    }

    public Integer getRecommendedAgeMonthsEnd() {
        return recommendedAgeMonthsEnd;
    }

    public void setRecommendedAgeMonthsEnd(Integer recommendedAgeMonthsEnd) {
        this.recommendedAgeMonthsEnd = recommendedAgeMonthsEnd;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String getPreventedDiseases() {
        return preventedDiseases;
    }

    public void setPreventedDiseases(String preventedDiseases) {
        this.preventedDiseases = preventedDiseases;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
