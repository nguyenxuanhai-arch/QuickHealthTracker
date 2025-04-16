package com.vaccine.controller.admin;

import com.vaccine.model.Role;
import com.vaccine.model.User;
import com.vaccine.repository.RoleRepository;
import com.vaccine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        User user = new User();
        user.setUsername("");
        user.setEmail("");
        user.setPassword("");
        user.setFullName("");
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.ERole.values());
        return "admin/user-add";
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute User user, @RequestParam Role.ERole role) {
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set user role
        Set<Role> roles = new HashSet<>();
        roleRepository.findByName(role).ifPresent(roles::add);
        user.setRoles(roles);
        
        userService.updateUser(user);
        return "redirect:/admin/users?added";
    }

    @GetMapping("/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        Optional<User> optionalUser = userService.findById(id);
        
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
            model.addAttribute("roles", Role.ERole.values());
            return "admin/user-edit";
        } else {
            return "redirect:/admin/users?error=notfound";
        }
    }

    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, 
                            @RequestParam(required = false) Role.ERole role) {
        Optional<User> optionalUser = userService.findById(id);
        
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            
            // Update basic info
            existingUser.setFullName(user.getFullName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            
            // Update password if provided
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            
            // Update role if provided
            if (role != null) {
                Set<Role> roles = new HashSet<>();
                roleRepository.findByName(role).ifPresent(roles::add);
                existingUser.setRoles(roles);
            }
            
            userService.updateUser(existingUser);
            return "redirect:/admin/users?updated";
        } else {
            return "redirect:/admin/users?error=notfound";
        }
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return "redirect:/admin/users?deleted";
        } catch (Exception e) {
            return "redirect:/admin/users?error=cannotdelete";
        }
    }

    // REST API endpoints
    @GetMapping("/api/users")
    @ResponseBody
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/users")
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestBody User user, @RequestParam Role.ERole role) {
        try {
            User createdUser = userService.registerNewUser(user, role);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        Optional<User> optionalUser = userService.findById(id);
        
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            
            // Update basic info
            existingUser.setFullName(user.getFullName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            
            // Update password if provided
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            
            // Update roles if provided
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                existingUser.setRoles(user.getRoles());
            }
            
            User updatedUser = userService.updateUser(existingUser);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUserApi(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot delete user. User may have dependencies.");
        }
    }

    @GetMapping("/api/roles")
    @ResponseBody
    public Role.ERole[] getRoles() {
        return Role.ERole.values();
    }
}
