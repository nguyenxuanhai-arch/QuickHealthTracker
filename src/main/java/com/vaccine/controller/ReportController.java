package com.vaccine.controller;

import com.vaccine.model.Role;
import com.vaccine.model.User;
import com.vaccine.service.ReportService;
import com.vaccine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public String showReports(Model model) {
        // Get current year for default reporting
        int currentYear = LocalDate.now().getYear();
        
        // Get summary statistics
        Map<String, Object> summaryStats = reportService.getSummaryStatistics();
        model.addAttribute("summaryStats", summaryStats);
        
        // Get monthly appointments
        Map<String, Long> monthlyAppointments = reportService.getMonthlyAppointmentsCount(currentYear);
        model.addAttribute("monthlyAppointments", monthlyAppointments);
        
        // Get monthly revenue
        Map<String, Double> monthlyRevenue = reportService.getMonthlyRevenue(currentYear);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        
        // Get vaccinations by type
        Map<String, Long> vaccinationsByType = reportService.getVaccinationsByType();
        model.addAttribute("vaccinationsByType", vaccinationsByType);
        
        // Get feedback statistics
        Map<String, Object> feedbackStats = reportService.getFeedbackStatistics();
        model.addAttribute("feedbackStats", feedbackStats);
        
        // Get reaction statistics
        Map<String, Object> reactionStats = reportService.getReactionStatistics();
        model.addAttribute("reactionStats", reactionStats);
        
        // Get vaccination coverage
        Map<String, Double> vaccinationCoverage = reportService.getVaccinationCoverage();
        model.addAttribute("vaccinationCoverage", vaccinationCoverage);
        
        model.addAttribute("currentYear", currentYear);
        return "admin/reports";
    }

    @GetMapping("/reports/custom")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCustomReportForm() {
        return "admin/custom-report";
    }

    @PostMapping("/reports/custom")
    @PreAuthorize("hasRole('ADMIN')")
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

    // REST API endpoints
    @GetMapping("/api/reports/summary")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getSummaryStats(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // Verify user is admin
            if (user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_ADMIN)) {
                Map<String, Object> summaryStats = reportService.getSummaryStatistics();
                return ResponseEntity.ok(summaryStats);
            }
        }
        
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/api/reports/monthly-appointments")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getMonthlyAppointments(@RequestParam(required = false) Integer year,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // Verify user is admin
            if (user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_ADMIN)) {
                // Default to current year if not provided
                int targetYear = (year != null) ? year : LocalDate.now().getYear();
                Map<String, Long> monthlyAppointments = reportService.getMonthlyAppointmentsCount(targetYear);
                return ResponseEntity.ok(monthlyAppointments);
            }
        }
        
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/api/reports/monthly-revenue")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getMonthlyRevenue(@RequestParam(required = false) Integer year,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // Verify user is admin
            if (user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_ADMIN)) {
                // Default to current year if not provided
                int targetYear = (year != null) ? year : LocalDate.now().getYear();
                Map<String, Double> monthlyRevenue = reportService.getMonthlyRevenue(targetYear);
                return ResponseEntity.ok(monthlyRevenue);
            }
        }
        
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/api/reports/vaccinations-by-type")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getVaccinationsByType(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // Verify user is admin
            if (user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_ADMIN)) {
                Map<String, Long> vaccinationsByType = reportService.getVaccinationsByType();
                return ResponseEntity.ok(vaccinationsByType);
            }
        }
        
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/api/reports/feedback-statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getFeedbackStatistics(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // Verify user is admin
            if (user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_ADMIN)) {
                Map<String, Object> feedbackStats = reportService.getFeedbackStatistics();
                return ResponseEntity.ok(feedbackStats);
            }
        }
        
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/api/reports/custom")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getCustomReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // Verify user is admin
            if (user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_ADMIN)) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                
                Map<String, Object> customReport = reportService.getCustomReportData(startDateTime, endDateTime);
                return ResponseEntity.ok(customReport);
            }
        }
        
        return ResponseEntity.badRequest().build();
    }
}
