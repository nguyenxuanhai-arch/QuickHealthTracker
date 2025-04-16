package com.vaccine.service;

import java.time.LocalDate;
import java.util.List;

public interface NotificationService {

    // Send email notification
    void sendEmailNotification(String to, String subject, String content);
    
    // Send reminder for upcoming vaccine
    void sendVaccineReminder(Long childId, Long vaccineScheduleId);
    
    // Send appointment confirmation
    void sendAppointmentConfirmation(Long appointmentId);
    
    // Send appointment reminder (1 day before)
    void sendAppointmentReminder(Long appointmentId);
    
    // Find all children with upcoming vaccines in the next specified days
    List<Long> findChildrenWithUpcomingVaccines(int days);
    
    // Find all appointments scheduled for the next specified days
    List<Long> findUpcomingAppointments(int days);
    
    // Scheduled job to send reminders
    void scheduledVaccineReminders();
    
    // Scheduled job to send appointment reminders
    void scheduledAppointmentReminders();
}
