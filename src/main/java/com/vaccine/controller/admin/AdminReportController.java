package com.vaccine.controller.admin;

import com.vaccine.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("")
    public String showDashboard(Model model) {
        // Get summary statistics
        Map<String, Object> summaryStats = reportService.getSummaryStatistics();
        model.addAttribute("summaryStats", summaryStats);
        
        // Get current year for reporting
        int currentYear = LocalDate.now().getYear();
        model.addAttribute("currentYear", currentYear);
        
        return "admin/dashboard";
    }

    @GetMapping("/appointments")
    public String showAppointmentsReport(@RequestParam(required = false) Integer year, Model model) {
        // Default to current year if not provided
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        
        // Get monthly appointments data
        Map<String, Long> monthlyAppointments = reportService.getMonthlyAppointmentsCount(targetYear);
        model.addAttribute("monthlyAppointments", monthlyAppointments);
        model.addAttribute("year", targetYear);
        
        // Add previous and next year for navigation
        model.addAttribute("previousYear", targetYear - 1);
        model.addAttribute("nextYear", targetYear + 1);
        model.addAttribute("currentYear", LocalDate.now().getYear());
        
        return "admin/reports-appointments";
    }

    @GetMapping("/revenue")
    public String showRevenueReport(@RequestParam(required = false) Integer year, Model model) {
        // Default to current year if not provided
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        
        // Get monthly revenue data
        Map<String, Double> monthlyRevenue = reportService.getMonthlyRevenue(targetYear);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("year", targetYear);
        
        // Calculate total revenue for the year
        double totalRevenue = monthlyRevenue.values().stream().mapToDouble(Double::doubleValue).sum();
        model.addAttribute("totalRevenue", totalRevenue);
        
        // Add previous and next year for navigation
        model.addAttribute("previousYear", targetYear - 1);
        model.addAttribute("nextYear", targetYear + 1);
        model.addAttribute("currentYear", LocalDate.now().getYear());
        
        return "admin/reports-revenue";
    }

    @GetMapping("/vaccinations")
    public String showVaccinationsReport(Model model) {
        // Get vaccinations by type
        Map<String, Long> vaccinationsByType = reportService.getVaccinationsByType();
        model.addAttribute("vaccinationsByType", vaccinationsByType);
        
        // Calculate total vaccinations
        long totalVaccinations = vaccinationsByType.values().stream().mapToLong(Long::longValue).sum();
        model.addAttribute("totalVaccinations", totalVaccinations);
        
        // Calculate percentages for chart
        Map<String, Double> vaccinationPercentages = new HashMap<>();
        for (Map.Entry<String, Long> entry : vaccinationsByType.entrySet()) {
            double percentage = (totalVaccinations > 0) 
                ? (double) entry.getValue() / totalVaccinations * 100 
                : 0;
            vaccinationPercentages.put(entry.getKey(), percentage);
        }
        model.addAttribute("vaccinationPercentages", vaccinationPercentages);
        
        // Get vaccination coverage data
        Map<String, Double> vaccinationCoverage = reportService.getVaccinationCoverage();
        model.addAttribute("vaccinationCoverage", vaccinationCoverage);
        
        return "admin/reports-vaccinations";
    }

    @GetMapping("/feedback")
    public String showFeedbackReport(Model model) {
        // Get feedback statistics
        Map<String, Object> feedbackStats = reportService.getFeedbackStatistics();
        model.addAttribute("feedbackStats", feedbackStats);
        
        return "admin/reports-feedback";
    }

    @GetMapping("/reactions")
    public String showReactionsReport(Model model) {
        // Get reaction statistics
        Map<String, Object> reactionStats = reportService.getReactionStatistics();
        model.addAttribute("reactionStats", reactionStats);
        
        return "admin/reports-reactions";
    }

    @GetMapping("/custom")
    public String showCustomReportForm() {
        return "admin/custom-report";
    }

    @PostMapping("/custom")
    public String generateCustomReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        Map<String, Object> customReport = reportService.getCustomReportData(startDateTime, endDateTime);
        model.addAttribute("customReport", customReport);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "admin/custom-report-results";
    }

    @GetMapping("/export")
    @ResponseBody
    public String exportReport(@RequestParam String type, @RequestParam(required = false) Integer year) {
        // This is a placeholder for a real export feature
        // In a real implementation, you would generate CSV/PDF files and return them
        
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        
        return "Export of " + type + " report for year " + targetYear + " would be generated here.";
    }
}
