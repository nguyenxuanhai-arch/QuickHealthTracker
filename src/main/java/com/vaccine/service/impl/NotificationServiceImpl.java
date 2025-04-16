package com.vaccine.service.impl;

import com.vaccine.model.Appointment;
import com.vaccine.model.Child;
import com.vaccine.model.User;
import com.vaccine.model.VaccineSchedule;
import com.vaccine.repository.AppointmentRepository;
import com.vaccine.repository.ChildRepository;
import com.vaccine.repository.VaccineScheduleRepository;
import com.vaccine.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private VaccineScheduleRepository vaccineScheduleRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ChildRepository childRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmailNotification(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        emailSender.send(message);
    }

    @Override
    @Transactional
    public void sendVaccineReminder(Long childId, Long vaccineScheduleId) {
        VaccineSchedule schedule = vaccineScheduleRepository.findById(vaccineScheduleId)
                .orElseThrow(() -> new RuntimeException("Vaccine schedule not found!"));
        
        Child child = schedule.getChild();
        User parent = child.getParent();
        
        String subject = "Reminder: Upcoming Vaccination for " + child.getFullName();
        String content = "Dear " + parent.getFullName() + ",\n\n" +
                "This is a reminder that " + child.getFullName() + " is due for the " + 
                schedule.getVaccine().getName() + " vaccination on " + 
                schedule.getScheduledDate() + ".\n\n" +
                "Please make an appointment if you haven't already done so.\n\n" +
                "Thank you,\nVaccination Management Team";
        
        sendEmailNotification(parent.getEmail(), subject, content);
    }

    @Override
    @Transactional
    public void sendAppointmentConfirmation(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        
        Child child = appointment.getChild();
        User parent = child.getParent();
        
        String subject = "Appointment Confirmation for " + child.getFullName();
        String content = "Dear " + parent.getFullName() + ",\n\n" +
                "Your appointment for " + child.getFullName() + " has been confirmed for " + 
                appointment.getAppointmentDateTime() + ".\n\n" +
                "Service: " + appointment.getVaccineService().getName() + "\n" +
                "Please arrive 15 minutes before your scheduled time.\n\n" +
                "Thank you,\nVaccination Management Team";
        
        sendEmailNotification(parent.getEmail(), subject, content);
    }

    @Override
    @Transactional
    public void sendAppointmentReminder(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        
        Child child = appointment.getChild();
        User parent = child.getParent();
        
        String subject = "Reminder: Upcoming Appointment for " + child.getFullName();
        String content = "Dear " + parent.getFullName() + ",\n\n" +
                "This is a reminder that " + child.getFullName() + " has an appointment tomorrow at " + 
                appointment.getAppointmentDateTime().toLocalTime() + ".\n\n" +
                "Service: " + appointment.getVaccineService().getName() + "\n" +
                "Please arrive 15 minutes before your scheduled time.\n\n" +
                "Thank you,\nVaccination Management Team";
        
        sendEmailNotification(parent.getEmail(), subject, content);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findChildrenWithUpcomingVaccines(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        
        List<VaccineSchedule> schedules = vaccineScheduleRepository.findByScheduledDateBetween(today, futureDate)
                .stream()
                .filter(vs -> vs.getStatus() == VaccineSchedule.Status.SCHEDULED)
                .collect(Collectors.toList());
        
        return schedules.stream()
                .map(vs -> vs.getId())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findUpcomingAppointments(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);
        
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateTimeBetween(now, futureDate)
                .stream()
                .filter(a -> a.getStatus() == Appointment.Status.SCHEDULED)
                .collect(Collectors.toList());
        
        return appointments.stream()
                .map(a -> a.getId())
                .collect(Collectors.toList());
    }

    @Override
    @Scheduled(cron = "0 0 8 * * *") // Run every day at 8:00 AM
    @Transactional
    public void scheduledVaccineReminders() {
        // Find children with vaccines due in the next 7 days
        List<Long> scheduleIds = findChildrenWithUpcomingVaccines(7);
        
        for (Long scheduleId : scheduleIds) {
            VaccineSchedule schedule = vaccineScheduleRepository.findById(scheduleId).orElse(null);
            if (schedule != null) {
                sendVaccineReminder(schedule.getChild().getId(), scheduleId);
            }
        }
    }

    @Override
    @Scheduled(cron = "0 0 9 * * *") // Run every day at 9:00 AM
    @Transactional
    public void scheduledAppointmentReminders() {
        // Find appointments scheduled for tomorrow
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        LocalDateTime tomorrowEnd = tomorrow.toLocalDate().atTime(23, 59, 59);
        
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateTimeBetween(
                tomorrow.toLocalDate().atStartOfDay(), tomorrowEnd);
        
        for (Appointment appointment : appointments) {
            if (appointment.getStatus() == Appointment.Status.SCHEDULED) {
                sendAppointmentReminder(appointment.getId());
            }
        }
    }
}
