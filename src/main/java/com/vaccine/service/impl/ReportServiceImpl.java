package com.vaccine.service.impl;

import com.vaccine.model.*;
import com.vaccine.repository.*;
import com.vaccine.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private VaccineScheduleRepository vaccineScheduleRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSummaryStatistics() {
        Map<String, Object> summary = new HashMap<>();
        
        // Count users by role
        long customerCount = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName() == Role.ERole.ROLE_CUSTOMER))
                .count();
        
        // Count children
        long childrenCount = childRepository.count();
        
        // Count vaccines
        long vaccinesCount = vaccineRepository.count();
        
        // Count appointments
        long appointmentsCount = appointmentRepository.count();
        long completedAppointments = appointmentRepository.findByStatus(Appointment.Status.COMPLETED).size();
        
        // Calculate revenue
        Double totalRevenue = paymentRepository.sumCompletedPaymentsBetween(
                LocalDateTime.now().minusYears(100), LocalDateTime.now());
        
        if (totalRevenue == null) {
            totalRevenue = 0.0;
        }
        
        // Get average rating
        Double averageRating = feedbackRepository.averageRating();
        if (averageRating == null) {
            averageRating = 0.0;
        }
        
        summary.put("customerCount", customerCount);
        summary.put("childrenCount", childrenCount);
        summary.put("vaccinesCount", vaccinesCount);
        summary.put("appointmentsCount", appointmentsCount);
        summary.put("completedAppointments", completedAppointments);
        summary.put("totalRevenue", totalRevenue);
        summary.put("averageRating", averageRating);
        
        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getMonthlyAppointmentsCount(int year) {
        Map<String, Long> monthlyCount = new HashMap<>();
        Year targetYear = Year.of(year);
        
        // Initialize all months with zero
        for (Month month : Month.values()) {
            monthlyCount.put(month.toString(), 0L);
        }
        
        // Get all appointments for the year
        LocalDateTime startOfYear = targetYear.atMonth(Month.JANUARY).atDay(1).atStartOfDay();
        LocalDateTime endOfYear = targetYear.atMonth(Month.DECEMBER).atEndOfMonth().atTime(23, 59, 59);
        
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateTimeBetween(startOfYear, endOfYear);
        
        // Count appointments by month
        for (Appointment appointment : appointments) {
            String month = appointment.getAppointmentDateTime().getMonth().toString();
            monthlyCount.put(month, monthlyCount.getOrDefault(month, 0L) + 1);
        }
        
        return monthlyCount;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getMonthlyRevenue(int year) {
        Map<String, Double> monthlyRevenue = new HashMap<>();
        Year targetYear = Year.of(year);
        
        // Initialize all months with zero
        for (Month month : Month.values()) {
            monthlyRevenue.put(month.toString(), 0.0);
        }
        
        // Get all payments for the year
        LocalDateTime startOfYear = targetYear.atMonth(Month.JANUARY).atDay(1).atStartOfDay();
        LocalDateTime endOfYear = targetYear.atMonth(Month.DECEMBER).atEndOfMonth().atTime(23, 59, 59);
        
        List<Payment> payments = paymentRepository.findByCreatedAtBetween(startOfYear, endOfYear)
                .stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED)
                .collect(Collectors.toList());
        
        // Sum revenue by month
        for (Payment payment : payments) {
            String month = payment.getCreatedAt().getMonth().toString();
            monthlyRevenue.put(month, monthlyRevenue.getOrDefault(month, 0.0) + payment.getAmount());
        }
        
        return monthlyRevenue;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getVaccinationsByType() {
        Map<String, Long> vaccinationsByType = new HashMap<>();
        
        // Get all vaccines
        List<Vaccine> vaccines = vaccineRepository.findAll();
        
        // Count completed vaccinations for each type
        for (Vaccine vaccine : vaccines) {
            long count = vaccineScheduleRepository.findByVaccine(vaccine)
                    .stream()
                    .filter(vs -> vs.getStatus() == VaccineSchedule.Status.COMPLETED)
                    .count();
            
            vaccinationsByType.put(vaccine.getName(), count);
        }
        
        return vaccinationsByType;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getFeedbackStatistics() {
        Map<String, Object> feedbackStats = new HashMap<>();
        
        // Average rating overall
        Double overallRating = feedbackRepository.averageRating();
        if (overallRating == null) {
            overallRating = 0.0;
        }
        
        // Ratings by type
        Map<String, Double> ratingsByType = new HashMap<>();
        for (Feedback.FeedbackType type : Feedback.FeedbackType.values()) {
            Double avgRating = feedbackRepository.averageRatingByType(type);
            if (avgRating == null) {
                avgRating = 0.0;
            }
            ratingsByType.put(type.toString(), avgRating);
        }
        
        // Count feedbacks by rating
        Map<Integer, Long> feedbackByRating = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            long count = feedbackRepository.findAll()
                    .stream()
                    .filter(f -> f.getRating() == rating)
                    .count();
            feedbackByRating.put(rating, count);
        }
        
        feedbackStats.put("overallRating", overallRating);
        feedbackStats.put("ratingsByType", ratingsByType);
        feedbackStats.put("feedbackByRating", feedbackByRating);
        
        return feedbackStats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getReactionStatistics() {
        Map<String, Object> reactionStats = new HashMap<>();
        
        // Count reactions by severity
        Map<String, Long> reactionsBySeverity = new HashMap<>();
        for (Reaction.Severity severity : Reaction.Severity.values()) {
            long count = reactionRepository.findBySeverity(severity).size();
            reactionsBySeverity.put(severity.toString(), count);
        }
        
        // Count reactions by vaccine
        Map<String, Map<String, Long>> reactionsByVaccine = new HashMap<>();
        List<Vaccine> vaccines = vaccineRepository.findAll();
        
        for (Vaccine vaccine : vaccines) {
            Map<String, Long> severityCounts = new HashMap<>();
            
            for (Reaction.Severity severity : Reaction.Severity.values()) {
                long count = reactionRepository.countByVaccineIdAndSeverity(vaccine.getId(), severity);
                severityCounts.put(severity.toString(), count);
            }
            
            reactionsByVaccine.put(vaccine.getName(), severityCounts);
        }
        
        reactionStats.put("reactionsBySeverity", reactionsBySeverity);
        reactionStats.put("reactionsByVaccine", reactionsByVaccine);
        
        return reactionStats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getVaccinationCoverage() {
        Map<String, Double> coverage = new HashMap<>();
        
        // Define age groups (in months)
        int[] ageGroups = {12, 24, 36, 48, 60, 72};
        
        for (int ageLimit : ageGroups) {
            // Get all required vaccines for this age group
            List<Vaccine> requiredVaccines = vaccineRepository.findByIsRequired(true)
                    .stream()
                    .filter(v -> v.getRecommendedAgeMonthsStart() <= ageLimit)
                    .collect(Collectors.toList());
            
            if (requiredVaccines.isEmpty()) {
                coverage.put("0-" + ageLimit + " months", 0.0);
                continue;
            }
            
            // Get all children in this age group
            LocalDateTime ageDate = LocalDateTime.now().minusMonths(ageLimit);
            List<Child> childrenInAgeGroup = childRepository.findAll()
                    .stream()
                    .filter(c -> c.getDateOfBirth().isAfter(ageDate.toLocalDate()))
                    .collect(Collectors.toList());
            
            if (childrenInAgeGroup.isEmpty()) {
                coverage.put("0-" + ageLimit + " months", 0.0);
                continue;
            }
            
            // Calculate coverage
            int totalRequired = requiredVaccines.size() * childrenInAgeGroup.size();
            int totalCompleted = 0;
            
            for (Child child : childrenInAgeGroup) {
                for (Vaccine vaccine : requiredVaccines) {
                    // Check if child has completed this vaccine
                    boolean completed = vaccineScheduleRepository.findByChildId(child.getId())
                            .stream()
                            .anyMatch(vs -> vs.getVaccine().getId().equals(vaccine.getId()) && 
                                    vs.getStatus() == VaccineSchedule.Status.COMPLETED);
                    
                    if (completed) {
                        totalCompleted++;
                    }
                }
            }
            
            double coveragePercent = (double) totalCompleted / totalRequired * 100;
            coverage.put("0-" + ageLimit + " months", coveragePercent);
        }
        
        return coverage;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCustomReportData(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> customReport = new HashMap<>();
        
        // Appointments in period
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateTimeBetween(startDate, endDate);
        long totalAppointments = appointments.size();
        long completedAppointments = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.COMPLETED)
                .count();
        long cancelledAppointments = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.CANCELLED)
                .count();
        
        // Revenue in period
        Double revenue = paymentRepository.sumCompletedPaymentsBetween(startDate, endDate);
        if (revenue == null) {
            revenue = 0.0;
        }
        
        // Vaccinations in period
        List<VaccineSchedule> schedules = vaccineScheduleRepository.findAll()
                .stream()
                .filter(vs -> vs.getAdministeredDate() != null && 
                        !vs.getAdministeredDate().isBefore(startDate.toLocalDate()) && 
                        !vs.getAdministeredDate().isAfter(endDate.toLocalDate()))
                .collect(Collectors.toList());
        
        long totalVaccinations = schedules.size();
        
        // New children registered in period
        List<Child> newChildren = childRepository.findAll()
                .stream()
                .filter(c -> !c.getCreatedAt().isBefore(startDate) && !c.getCreatedAt().isAfter(endDate))
                .collect(Collectors.toList());
        
        long newChildrenCount = newChildren.size();
        
        customReport.put("period", startDate + " to " + endDate);
        customReport.put("totalAppointments", totalAppointments);
        customReport.put("completedAppointments", completedAppointments);
        customReport.put("cancelledAppointments", cancelledAppointments);
        customReport.put("revenue", revenue);
        customReport.put("totalVaccinations", totalVaccinations);
        customReport.put("newChildren", newChildrenCount);
        
        return customReport;
    }
}
