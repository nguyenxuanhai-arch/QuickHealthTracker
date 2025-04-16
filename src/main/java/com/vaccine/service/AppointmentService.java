package com.vaccine.service;

import com.vaccine.model.Appointment;
import com.vaccine.model.Child;
import com.vaccine.model.Payment;
import com.vaccine.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {

    Appointment createAppointment(Appointment appointment);
    
    Appointment updateAppointment(Appointment appointment);
    
    Optional<Appointment> findById(Long id);
    
    List<Appointment> findByChild(Child child);
    
    List<Appointment> findByParentId(Long parentId);
    
    List<Appointment> findByStaff(User staff);
    
    List<Appointment> findByStatus(Appointment.Status status);
    
    List<Appointment> findByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    List<Appointment> findAll();
    
    void cancelAppointment(Long id);
    
    // Process payment for an appointment
    Payment processPayment(Long appointmentId, Payment payment);
    
    // Find payment by appointment
    Optional<Payment> findPaymentByAppointmentId(Long appointmentId);
}
