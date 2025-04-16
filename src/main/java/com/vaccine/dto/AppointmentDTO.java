package com.vaccine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
