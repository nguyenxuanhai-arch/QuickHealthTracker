package com.vaccine.model;

// Lombok imports removed
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reactions")
// Lombok annotations removed
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_schedule_id", nullable = false)
    private VaccineSchedule vaccineSchedule;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    private String symptoms;

    private String treatment;

    private LocalDateTime onsetTime;

    private LocalDateTime resolutionTime;

    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Severity {
        MILD, MODERATE, SEVERE
    }
    
    // Constructors
    public Reaction() {
        // Default constructor
    }
    
    public Reaction(Long id, VaccineSchedule vaccineSchedule, Severity severity, String symptoms, 
                   String treatment, LocalDateTime onsetTime, LocalDateTime resolutionTime, 
                   String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.vaccineSchedule = vaccineSchedule;
        this.severity = severity;
        this.symptoms = symptoms;
        this.treatment = treatment;
        this.onsetTime = onsetTime;
        this.resolutionTime = resolutionTime;
        this.notes = notes;
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

    public VaccineSchedule getVaccineSchedule() {
        return vaccineSchedule;
    }

    public void setVaccineSchedule(VaccineSchedule vaccineSchedule) {
        this.vaccineSchedule = vaccineSchedule;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public LocalDateTime getOnsetTime() {
        return onsetTime;
    }

    public void setOnsetTime(LocalDateTime onsetTime) {
        this.onsetTime = onsetTime;
    }

    public LocalDateTime getResolutionTime() {
        return resolutionTime;
    }

    public void setResolutionTime(LocalDateTime resolutionTime) {
        this.resolutionTime = resolutionTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
