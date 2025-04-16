package com.vaccine.controller;

import com.vaccine.dto.AppointmentDTO;
import com.vaccine.model.*;
import com.vaccine.service.AppointmentService;
import com.vaccine.service.ChildService;
import com.vaccine.service.UserService;
import com.vaccine.service.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Optional;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ChildService childService;

    @Autowired
    private UserService userService;

    @Autowired
    private VaccineService vaccineService;

    @GetMapping("/appointments")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    public String showAppointments(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // For customers, show their appointments
            if (user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_CUSTOMER)) {
                List<Appointment> appointments = appointmentService.findByParentId(user.getId());
                model.addAttribute("appointments", appointments);
            } 
            // For staff and admin, show all appointments
            else if (user.getRoles().stream().anyMatch(role -> 
                    role.getName() == Role.ERole.ROLE_STAFF || role.getName() == Role.ERole.ROLE_ADMIN)) {
                List<Appointment> appointments = appointmentService.findAll();
                model.addAttribute("appointments", appointments);
            }
            
            return "appointment";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/appointments/schedule")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public String showScheduleAppointment(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Child> children = childService.findByParent(user);
            List<com.vaccine.model.VaccineService> vaccineServices = vaccineService.findAll();
            
            model.addAttribute("children", children);
            model.addAttribute("vaccineServices", vaccineServices);
            model.addAttribute("appointmentDTO", new AppointmentDTO());
            
            return "appointment-schedule";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/appointments/schedule")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public String scheduleAppointment(@ModelAttribute AppointmentDTO appointmentDTO, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            Optional<Child> optionalChild = childService.findById(appointmentDTO.getChildId());
            Optional<com.vaccine.model.VaccineService> optionalVaccineService = 
                    vaccineService.findById(appointmentDTO.getVaccineServiceId());
            
            if (optionalChild.isPresent() && optionalVaccineService.isPresent()) {
                Child child = optionalChild.get();
                com.vaccine.model.VaccineService service = optionalVaccineService.get();
                
                // Create appointment
                Appointment appointment = new Appointment();
                appointment.setChild(child);
                appointment.setVaccineService(service);
                
                // Set appointment date and time
                LocalDate date = appointmentDTO.getAppointmentDate();
                LocalTime time = appointmentDTO.getAppointmentTime();
                LocalDateTime dateTime = LocalDateTime.of(date, time);
                appointment.setAppointmentDateTime(dateTime);
                
                appointment.setStatus(Appointment.Status.SCHEDULED);
                appointment.setNotes(appointmentDTO.getNotes());
                
                appointmentService.createAppointment(appointment);
                
                // Create payment record
                Payment payment = new Payment();
                payment.setAmount(service.getPrice());
                payment.setStatus(Payment.PaymentStatus.PENDING);
                payment.setPaymentMethod(Payment.PaymentMethod.valueOf(appointmentDTO.getPaymentMethod()));
                
                appointmentService.processPayment(appointment.getId(), payment);
                
                return "redirect:/appointments?scheduled";
            }
        }
        
        return "redirect:/appointments/schedule?error";
    }

    @GetMapping("/appointments/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    public String viewAppointment(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Appointment> optionalAppointment = appointmentService.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalAppointment.isPresent() && optionalUser.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            User user = optionalUser.get();
            
            // Check if user is the parent, staff, or admin
            if (appointment.getChild().getParent().getId().equals(user.getId()) || 
                    user.getRoles().stream().anyMatch(role -> 
                        role.getName() == Role.ERole.ROLE_STAFF || role.getName() == Role.ERole.ROLE_ADMIN)) {
                
                model.addAttribute("appointment", appointment);
                
                // Get payment info
                Optional<Payment> payment = appointmentService.findPaymentByAppointmentId(id);
                payment.ifPresent(p -> model.addAttribute("payment", p));
                
                return "appointment-details";
            }
        }
        
        return "redirect:/appointments";
    }

    @PostMapping("/appointments/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public String cancelAppointment(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Appointment> optionalAppointment = appointmentService.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalAppointment.isPresent() && optionalUser.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            User user = optionalUser.get();
            
            // Check if user is the parent or admin
            if (appointment.getChild().getParent().getId().equals(user.getId()) || 
                    user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_ADMIN)) {
                
                appointmentService.cancelAppointment(id);
                return "redirect:/appointments?cancelled";
            }
        }
        
        return "redirect:/appointments";
    }

    @PostMapping("/appointments/{id}/complete")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public String completeAppointment(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Appointment> optionalAppointment = appointmentService.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalAppointment.isPresent() && optionalUser.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            User user = optionalUser.get();
            
            // Update appointment status
            appointment.setStatus(Appointment.Status.COMPLETED);
            appointment.setStaff(user);
            appointmentService.updateAppointment(appointment);
            
            // Update payment status if exists
            Optional<Payment> payment = appointmentService.findPaymentByAppointmentId(id);
            if (payment.isPresent()) {
                Payment p = payment.get();
                p.setStatus(Payment.PaymentStatus.COMPLETED);
                appointmentService.processPayment(id, p);
            }
            
            return "redirect:/appointments?completed";
        }
        
        return "redirect:/appointments";
    }

    // REST API endpoints
    @GetMapping("/api/appointments")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getAppointments(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // For customers, show their appointments
            if (user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_CUSTOMER)) {
                List<Appointment> appointments = appointmentService.findByParentId(user.getId());
                return ResponseEntity.ok(appointments);
            } 
            // For staff and admin, show all appointments
            else if (user.getRoles().stream().anyMatch(role -> 
                    role.getName() == Role.ERole.ROLE_STAFF || role.getName() == Role.ERole.ROLE_ADMIN)) {
                List<Appointment> appointments = appointmentService.findAll();
                return ResponseEntity.ok(appointments);
            }
        }
        
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/api/appointments")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentDTO appointmentDTO, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            Optional<Child> optionalChild = childService.findById(appointmentDTO.getChildId());
            Optional<com.vaccine.model.VaccineService> optionalVaccineService = 
                    vaccineService.findById(appointmentDTO.getVaccineServiceId());
            
            if (optionalChild.isPresent() && optionalVaccineService.isPresent()) {
                Child child = optionalChild.get();
                com.vaccine.model.VaccineService service = optionalVaccineService.get();
                
                // Create appointment
                Appointment appointment = new Appointment();
                appointment.setChild(child);
                appointment.setVaccineService(service);
                
                // Set appointment date and time
                LocalDate date = appointmentDTO.getAppointmentDate();
                LocalTime time = appointmentDTO.getAppointmentTime();
                LocalDateTime dateTime = LocalDateTime.of(date, time);
                appointment.setAppointmentDateTime(dateTime);
                
                appointment.setStatus(Appointment.Status.SCHEDULED);
                appointment.setNotes(appointmentDTO.getNotes());
                
                Appointment createdAppointment = appointmentService.createAppointment(appointment);
                
                // Create payment record
                Payment payment = new Payment();
                payment.setAmount(service.getPrice());
                payment.setStatus(Payment.PaymentStatus.PENDING);
                payment.setPaymentMethod(Payment.PaymentMethod.valueOf(appointmentDTO.getPaymentMethod()));
                
                appointmentService.processPayment(createdAppointment.getId(), payment);
                
                return ResponseEntity.ok(createdAppointment);
            }
        }
        
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/api/appointments/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getAppointment(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Appointment> optionalAppointment = appointmentService.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalAppointment.isPresent() && optionalUser.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            User user = optionalUser.get();
            
            // Check if user is the parent, staff, or admin
            if (appointment.getChild().getParent().getId().equals(user.getId()) || 
                    user.getRoles().stream().anyMatch(role -> 
                        role.getName() == Role.ERole.ROLE_STAFF || role.getName() == Role.ERole.ROLE_ADMIN)) {
                
                return ResponseEntity.ok(appointment);
            }
        }
        
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/api/appointments/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> cancelAppointmentApi(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Appointment> optionalAppointment = appointmentService.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalAppointment.isPresent() && optionalUser.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            User user = optionalUser.get();
            
            // Check if user is the parent or admin
            if (appointment.getChild().getParent().getId().equals(user.getId()) || 
                    user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_ADMIN)) {
                
                appointmentService.cancelAppointment(id);
                return ResponseEntity.ok().build();
            }
        }
        
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/api/appointments/{id}/complete")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> completeAppointmentApi(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Appointment> optionalAppointment = appointmentService.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalAppointment.isPresent() && optionalUser.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            User user = optionalUser.get();
            
            // Update appointment status
            appointment.setStatus(Appointment.Status.COMPLETED);
            appointment.setStaff(user);
            appointmentService.updateAppointment(appointment);
            
            // Update payment status if exists
            Optional<Payment> payment = appointmentService.findPaymentByAppointmentId(id);
            if (payment.isPresent()) {
                Payment p = payment.get();
                p.setStatus(Payment.PaymentStatus.COMPLETED);
                appointmentService.processPayment(id, p);
            }
            
            return ResponseEntity.ok().build();
        }
        
        return ResponseEntity.badRequest().build();
    }
}
