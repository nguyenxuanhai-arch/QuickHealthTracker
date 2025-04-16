package com.vaccine.controller;

import com.vaccine.dto.ChildDTO;
import com.vaccine.model.Child;
import com.vaccine.model.User;
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
public class ChildController {

    @Autowired
    private ChildService childService;

    @Autowired
    private UserService userService;

    @Autowired
    private VaccineService vaccineService;

    @GetMapping("/children")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    public String showChildrenList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Child> children = childService.findByParent(user);
            model.addAttribute("children", children);
            return "children";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/children/add")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public String showAddChildForm(Model model) {
        model.addAttribute("child", new Child());
        model.addAttribute("genders", Child.Gender.values());
        return "child-add";
    }

    @PostMapping("/children/add")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public String addChild(@ModelAttribute Child child, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            childService.createChild(child, user);
            return "redirect:/children?added";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/children/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    public String showChildProfile(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Child> optionalChild = childService.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalChild.isPresent() && optionalUser.isPresent()) {
            Child child = optionalChild.get();
            User user = optionalUser.get();
            
            // Check if user is parent or has admin/staff role
            if (child.getParent().getId().equals(user.getId()) || 
                    userDetails.getAuthorities().stream().anyMatch(a -> 
                        a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STAFF"))) {
                
                model.addAttribute("child", child);
                
                // Get vaccine schedules for the child
                List<VaccineSchedule> vaccineSchedules = vaccineService.findVaccineSchedulesByChildId(id);
                model.addAttribute("vaccineSchedules", vaccineSchedules);
                
                return "child-profile";
            }
        }
        
        return "redirect:/children";
    }

    @GetMapping("/children/{id}/edit")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public String showEditChildForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Child> optionalChild = childService.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalChild.isPresent() && optionalUser.isPresent()) {
            Child child = optionalChild.get();
            User user = optionalUser.get();
            
            // Check if user is parent or has admin role
            if (child.getParent().getId().equals(user.getId()) || 
                    userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                
                model.addAttribute("child", child);
                model.addAttribute("genders", Child.Gender.values());
                return "child-edit";
            }
        }
        
        return "redirect:/children";
    }

    @PostMapping("/children/{id}/update")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public String updateChild(@PathVariable Long id, @ModelAttribute Child child, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Child> optionalChild = childService.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalChild.isPresent() && optionalUser.isPresent()) {
            Child existingChild = optionalChild.get();
            User user = optionalUser.get();
            
            // Check if user is parent or has admin role
            if (existingChild.getParent().getId().equals(user.getId()) || 
                    userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                
                existingChild.setFullName(child.getFullName());
                existingChild.setDateOfBirth(child.getDateOfBirth());
                existingChild.setGender(child.getGender());
                existingChild.setBloodType(child.getBloodType());
                existingChild.setAllergies(child.getAllergies());
                existingChild.setMedicalConditions(child.getMedicalConditions());
                
                childService.updateChild(existingChild);
                return "redirect:/children/" + id + "?updated";
            }
        }
        
        return "redirect:/children";
    }

    // API endpoints for child management
    @GetMapping("/api/children")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getChildren(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Child> children = childService.findByParent(user);
            return ResponseEntity.ok(children);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/children")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> createChild(@RequestBody ChildDTO childDTO, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            Child child = new Child();
            child.setFullName(childDTO.getFullName());
            child.setDateOfBirth(childDTO.getDateOfBirth());
            child.setGender(childDTO.getGender());
            child.setBloodType(childDTO.getBloodType());
            child.setAllergies(childDTO.getAllergies());
            child.setMedicalConditions(childDTO.getMedicalConditions());
            
            Child createdChild = childService.createChild(child, user);
            return ResponseEntity.ok(createdChild);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/children/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getChild(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Child> optionalChild = childService.findById(id);
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalChild.isPresent() && optionalUser.isPresent()) {
            Child child = optionalChild.get();
            User user = optionalUser.get();
            
            // Check if user is parent or has admin/staff role
            if (child.getParent().getId().equals(user.getId()) || 
                    userDetails.getAuthorities().stream().anyMatch(a -> 
                        a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STAFF"))) {
                
                return ResponseEntity.ok(child);
            }
        }
        
        return ResponseEntity.notFound().build();
    }
}
