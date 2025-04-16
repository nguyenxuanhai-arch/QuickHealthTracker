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
@Table(name = "vaccine_services")
// Lombok annotations removed
public class VaccineService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    private String description;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @NotNull
    private Double price;

    private Double discountPercentage;

    @ManyToMany
    @JoinTable(
            name = "service_vaccines",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "vaccine_id")
    )
    private Set<Vaccine> vaccines = new HashSet<>();

    @OneToMany(mappedBy = "vaccineService", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Appointment> appointments = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ServiceType {
        INDIVIDUAL, PACKAGE, CUSTOM
    }
    
    // Constructors
    public VaccineService() {
        // Default constructor
    }
    
    public VaccineService(Long id, String name, String description, ServiceType serviceType,
                         Double price, Double discountPercentage, Set<Vaccine> vaccines,
                         Set<Appointment> appointments, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.serviceType = serviceType;
        this.price = price;
        this.discountPercentage = discountPercentage;
        this.vaccines = vaccines;
        this.appointments = appointments;
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

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Set<Vaccine> getVaccines() {
        return vaccines;
    }

    public void setVaccines(Set<Vaccine> vaccines) {
        this.vaccines = vaccines;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
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
