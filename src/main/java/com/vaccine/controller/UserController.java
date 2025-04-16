package com.vaccine.controller;

import com.vaccine.model.User;
import com.vaccine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    public String showUserProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
            return "profile";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/profile/edit")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    public String showEditProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
            return "profile-edit";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/profile/update")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    public String updateProfile(@ModelAttribute User user, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setFullName(user.getFullName());
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            
            userService.updateUser(existingUser);
            return "redirect:/profile?updated";
        } else {
            return "redirect:/login";
        }
    }

    // API endpoints for user management
    @GetMapping("/api/users/me")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> user = userService.findByUsername(userDetails.getUsername());
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/api/users/me")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('STAFF') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> updateCurrentUser(@RequestBody User user, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setFullName(user.getFullName());
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            
            User updated = userService.updateUser(existingUser);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
