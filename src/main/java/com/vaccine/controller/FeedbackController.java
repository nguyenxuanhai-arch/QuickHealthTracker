package com.vaccine.controller;

import com.vaccine.model.Appointment;
import com.vaccine.model.Feedback;
import com.vaccine.model.Role;
import com.vaccine.model.User;
import com.vaccine.repository.FeedbackRepository;
import com.vaccine.service.AppointmentService;
import com.vaccine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @GetMapping("/feedback")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    public String showFeedback(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // For customers, show only their feedback
            if (user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_CUSTOMER)) {
                List<Feedback> feedbackList = feedbackRepository.findByUserId(user.getId());
                model.addAttribute("feedbackList", feedbackList);
            } 
            // For staff and admin, show all public feedback
            else if (user.getRoles().stream().anyMatch(role -> 
                    role.getName() == Role.ERole.ROLE_STAFF || role.getName() == Role.ERole.ROLE_ADMIN)) {
                List<Feedback> feedbackList = feedbackRepository.findByIsPublic(true);
                model.addAttribute("feedbackList", feedbackList);
                model.addAttribute("isStaffOrAdmin", true);
            }
            
            return "feedback";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/feedback/add")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String showAddFeedback(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // Get all completed appointments for this user
            List<Appointment> completedAppointments = appointmentService.findByParentId(user.getId());
            completedAppointments.removeIf(a -> a.getStatus() != Appointment.Status.COMPLETED);
            
            model.addAttribute("appointments", completedAppointments);
            model.addAttribute("feedback", new Feedback());
            model.addAttribute("feedbackTypes", Feedback.FeedbackType.values());
            
            return "feedback-add";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/feedback/add")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String addFeedback(@ModelAttribute Feedback feedback, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            feedback.setUser(user);
            
            // Set appointment if provided
            if (feedback.getAppointment() != null && feedback.getAppointment().getId() != null) {
                Optional<Appointment> appointment = appointmentService.findById(feedback.getAppointment().getId());
                appointment.ifPresent(feedback::setAppointment);
            }
            
            feedbackRepository.save(feedback);
            return "redirect:/feedback?added";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/feedback/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    public String viewFeedback(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Feedback> optionalFeedback = feedbackRepository.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalFeedback.isPresent() && optionalUser.isPresent()) {
            Feedback feedback = optionalFeedback.get();
            User user = optionalUser.get();
            
            // Check if user is the feedback owner, or if feedback is public and user is staff/admin
            if (feedback.getUser().getId().equals(user.getId()) || 
                    (feedback.getIsPublic() && user.getRoles().stream().anyMatch(role -> 
                        role.getName() == Role.ERole.ROLE_STAFF || role.getName() == Role.ERole.ROLE_ADMIN))) {
                
                model.addAttribute("feedback", feedback);
                return "feedback-view";
            }
        }
        
        return "redirect:/feedback";
    }

    // REST API endpoints
    @GetMapping("/api/feedback")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getFeedback(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // For customers, show only their feedback
            if (user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_CUSTOMER)) {
                List<Feedback> feedbackList = feedbackRepository.findByUserId(user.getId());
                return ResponseEntity.ok(feedbackList);
            } 
            // For staff and admin, show all public feedback
            else if (user.getRoles().stream().anyMatch(role -> 
                    role.getName() == Role.ERole.ROLE_STAFF || role.getName() == Role.ERole.ROLE_ADMIN)) {
                List<Feedback> feedbackList = feedbackRepository.findByIsPublic(true);
                return ResponseEntity.ok(feedbackList);
            }
        }
        
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/api/feedback")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public ResponseEntity<?> createFeedback(@RequestBody Feedback feedback, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            feedback.setUser(user);
            
            // Set appointment if provided
            if (feedback.getAppointment() != null && feedback.getAppointment().getId() != null) {
                Optional<Appointment> appointment = appointmentService.findById(feedback.getAppointment().getId());
                appointment.ifPresent(feedback::setAppointment);
            }
            
            Feedback savedFeedback = feedbackRepository.save(feedback);
            return ResponseEntity.ok(savedFeedback);
        }
        
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/api/feedback/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getFeedbackById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Feedback> optionalFeedback = feedbackRepository.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalFeedback.isPresent() && optionalUser.isPresent()) {
            Feedback feedback = optionalFeedback.get();
            User user = optionalUser.get();
            
            // Check if user is the feedback owner, or if feedback is public and user is staff/admin
            if (feedback.getUser().getId().equals(user.getId()) || 
                    (feedback.getIsPublic() && user.getRoles().stream().anyMatch(role -> 
                        role.getName() == Role.ERole.ROLE_STAFF || role.getName() == Role.ERole.ROLE_ADMIN))) {
                
                return ResponseEntity.ok(feedback);
            }
        }
        
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/api/feedback/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public ResponseEntity<?> updateFeedback(@PathVariable Long id, @RequestBody Feedback feedback, 
                                           @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Feedback> optionalFeedback = feedbackRepository.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalFeedback.isPresent() && optionalUser.isPresent()) {
            Feedback existingFeedback = optionalFeedback.get();
            User user = optionalUser.get();
            
            // Check if user is the feedback owner
            if (existingFeedback.getUser().getId().equals(user.getId())) {
                existingFeedback.setRating(feedback.getRating());
                existingFeedback.setComments(feedback.getComments());
                existingFeedback.setIsPublic(feedback.getIsPublic());
                existingFeedback.setType(feedback.getType());
                
                Feedback updatedFeedback = feedbackRepository.save(existingFeedback);
                return ResponseEntity.ok(updatedFeedback);
            }
        }
        
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/api/feedback/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Feedback> optionalFeedback = feedbackRepository.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalFeedback.isPresent() && optionalUser.isPresent()) {
            Feedback feedback = optionalFeedback.get();
            User user = optionalUser.get();
            
            // Check if user is the feedback owner or admin
            if (feedback.getUser().getId().equals(user.getId()) || 
                    user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_ADMIN)) {
                
                feedbackRepository.delete(feedback);
                return ResponseEntity.ok().build();
            }
        }
        
        return ResponseEntity.badRequest().build();
    }
}
