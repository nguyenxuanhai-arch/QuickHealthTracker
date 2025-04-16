package com.vaccine.controller;

import com.vaccine.config.JwtTokenProvider;
import com.vaccine.dto.JwtResponse;
import com.vaccine.dto.LoginRequest;
import com.vaccine.model.Role;
import com.vaccine.model.User;
import com.vaccine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        try {
            userService.registerNewUser(user, Role.ERole.ROLE_CUSTOMER);
            return "redirect:/login?registered";
        } catch (Exception e) {
            return "redirect:/register?error=" + e.getMessage();
        }
    }

    // API endpoint for JWT authentication
    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> registerUserApi(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.registerNewUser(user, Role.ERole.ROLE_CUSTOMER);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
