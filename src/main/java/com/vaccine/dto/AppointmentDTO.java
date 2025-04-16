package com.vaccine.dto;

// Lombok imports removed

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

// Lombok annotations removed
public class AppointmentDTO {
    
    private Long id;
    
    @NotNull
    private Long childId;
    
    @NotNull
    private Long vaccineServiceId;
    
    @NotNull
    @Future
    private LocalDate appointmentDate;
    
    @NotNull
    private LocalTime appointmentTime;
    
    private String notes;
    
    private String paymentMethod;
    
    // Constructors
    public AppointmentDTO() {
        // Default constructor
    }
    
    public AppointmentDTO(Long id, Long childId, Long vaccineServiceId, LocalDate appointmentDate,
                         LocalTime appointmentTime, String notes, String paymentMethod) {
        this.id = id;
        this.childId = childId;
        this.vaccineServiceId = vaccineServiceId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.notes = notes;
        this.paymentMethod = paymentMethod;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public Long getVaccineServiceId() {
        return vaccineServiceId;
    }

    public void setVaccineServiceId(Long vaccineServiceId) {
        this.vaccineServiceId = vaccineServiceId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
