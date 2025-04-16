package com.vaccine.controller;

import com.vaccine.model.Child;
import com.vaccine.model.User;
import com.vaccine.model.Vaccine;
import com.vaccine.model.VaccineSchedule;
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

import java.util.List;
import java.util.Optional;

@Controller
public class VaccineController {

    @Autowired
    private VaccineService vaccineService;

    @Autowired
    private ChildService childService;

    @Autowired
    private UserService userService;

    @GetMapping("/vaccines")
    public String showVaccinesList(Model model) {
        List<Vaccine> vaccines = vaccineService.findAll();
        model.addAttribute("vaccines", vaccines);
        return "vaccines";
    }

    @GetMapping("/vaccines/{id}")
    public String showVaccineDetails(@PathVariable Long id, Model model) {
        Optional<Vaccine> optionalVaccine = vaccineService.findById(id);
        
        if (optionalVaccine.isPresent()) {
            model.addAttribute("vaccine", optionalVaccine.get());
            return "vaccine-details";
        } else {
            return "redirect:/vaccines";
        }
    }

    @GetMapping("/children/{childId}/vaccines")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    public String showChildVaccines(@PathVariable Long childId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Child> optionalChild = childService.findById(childId);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalChild.isPresent() && optionalUser.isPresent()) {
            Child child = optionalChild.get();
            User user = optionalUser.get();
            
            // Check if user is parent or has admin/staff role
            if (child.getParent().getId().equals(user.getId()) || 
                    userDetails.getAuthorities().stream().anyMatch(a -> 
                        a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STAFF"))) {
                
                List<VaccineSchedule> vaccineSchedules = vaccineService.findVaccineSchedulesByChildId(childId);
                model.addAttribute("child", child);
                model.addAttribute("vaccineSchedules", vaccineSchedules);
                return "vaccination-history";
            }
        }
        
        return "redirect:/children";
    }

    @GetMapping("/children/{childId}/vaccines/upcoming")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    public String showUpcomingVaccines(@PathVariable Long childId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Child> optionalChild = childService.findById(childId);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalChild.isPresent() && optionalUser.isPresent()) {
            Child child = optionalChild.get();
            User user = optionalUser.get();
            
            // Check if user is parent or has admin/staff role
            if (child.getParent().getId().equals(user.getId()) || 
                    userDetails.getAuthorities().stream().anyMatch(a -> 
                        a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STAFF"))) {
                
                List<VaccineSchedule> upcomingVaccines = vaccineService.findUpcomingVaccineSchedules(childId);
                model.addAttribute("child", child);
                model.addAttribute("upcomingVaccines", upcomingVaccines);
                return "upcoming-vaccines";
            }
        }
        
        return "redirect:/children";
    }

    // API endpoints for vaccine information
    @GetMapping("/api/vaccines")
    @ResponseBody
    public List<Vaccine> getAllVaccines() {
        return vaccineService.findAll();
    }

    @GetMapping("/api/vaccines/{id}")
    @ResponseBody
    public ResponseEntity<?> getVaccine(@PathVariable Long id) {
        Optional<Vaccine> vaccine = vaccineService.findById(id);
        return vaccine.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/children/{childId}/vaccines")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getChildVaccines(@PathVariable Long childId, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Child> optionalChild = childService.findById(childId);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalChild.isPresent() && optionalUser.isPresent()) {
            Child child = optionalChild.get();
            User user = optionalUser.get();
            
            // Check if user is parent or has admin/staff role
            if (child.getParent().getId().equals(user.getId()) || 
                    userDetails.getAuthorities().stream().anyMatch(a -> 
                        a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STAFF"))) {
                
                List<VaccineSchedule> vaccineSchedules = vaccineService.findVaccineSchedulesByChildId(childId);
                return ResponseEntity.ok(vaccineSchedules);
            }
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/api/children/{childId}/vaccines/upcoming")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getUpcomingVaccines(@PathVariable Long childId, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Child> optionalChild = childService.findById(childId);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalChild.isPresent() && optionalUser.isPresent()) {
            Child child = optionalChild.get();
            User user = optionalUser.get();
            
            // Check if user is parent or has admin/staff role
            if (child.getParent().getId().equals(user.getId()) || 
                    userDetails.getAuthorities().stream().anyMatch(a -> 
                        a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STAFF"))) {
                
                List<VaccineSchedule> upcomingVaccines = vaccineService.findUpcomingVaccineSchedules(childId);
                return ResponseEntity.ok(upcomingVaccines);
            }
        }
        
        return ResponseEntity.notFound().build();
    }
}
