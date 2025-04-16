package com.vaccine.model;

// Lombok imports removed
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vaccines")
// Lombok annotations removed
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    @Size(max = 50)
    private String manufacturer;

    @NotNull
    private Integer recommendedAgeMonthsStart;
    
    private Integer recommendedAgeMonthsEnd;

    private Boolean isRequired;

    private String preventedDiseases;

    private String sideEffects;

    @NotNull
    private Double price;

    @ManyToMany(mappedBy = "vaccines")
    private Set<VaccineService> vaccineServices = new HashSet<>();

    @OneToMany(mappedBy = "vaccine", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VaccineSchedule> vaccineSchedules = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Constructors
    public Vaccine() {
        // Default constructor
    }
    
    public Vaccine(Long id, String name, String description, String manufacturer, 
                  Integer recommendedAgeMonthsStart, Integer recommendedAgeMonthsEnd, 
                  Boolean isRequired, String preventedDiseases, String sideEffects, 
                  Double price, Set<VaccineService> vaccineServices, 
                  Set<VaccineSchedule> vaccineSchedules, 
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
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
        this.vaccineServices = vaccineServices;
        this.vaccineSchedules = vaccineSchedules;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Set<VaccineService> getVaccineServices() {
        return vaccineServices;
    }

    public void setVaccineServices(Set<VaccineService> vaccineServices) {
        this.vaccineServices = vaccineServices;
    }

    public Set<VaccineSchedule> getVaccineSchedules() {
        return vaccineSchedules;
    }

    public void setVaccineSchedules(Set<VaccineSchedule> vaccineSchedules) {
        this.vaccineSchedules = vaccineSchedules;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
