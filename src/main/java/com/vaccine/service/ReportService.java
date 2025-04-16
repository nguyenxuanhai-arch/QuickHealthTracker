package com.vaccine.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface ReportService {

    // Get summary statistics
    Map<String, Object> getSummaryStatistics();
    
    // Get monthly appointments count
    Map<String, Long> getMonthlyAppointmentsCount(int year);
    
    // Get monthly revenue
    Map<String, Double> getMonthlyRevenue(int year);
    
    // Get vaccinations by type
    Map<String, Long> getVaccinationsByType();
    
    // Get customer feedback statistics
    Map<String, Object> getFeedbackStatistics();
    
    // Get vaccine reaction statistics
    Map<String, Object> getReactionStatistics();
    
    // Get vaccination coverage (percentage of children vaccinated by age group)
    Map<String, Double> getVaccinationCoverage();
    
    // Get data for custom time range
    Map<String, Object> getCustomReportData(LocalDateTime startDate, LocalDateTime endDate);
}
