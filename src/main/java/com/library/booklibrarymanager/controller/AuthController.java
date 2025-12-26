package com.library.booklibrarymanager.controller;

import com.library.booklibrarymanager.dto.*;
import com.library.booklibrarymanager.entity.User;
import com.library.booklibrarymanager.repository.UserRepository;
import com.library.booklibrarymanager.config.JwtUtil;
import com.library.booklibrarymanager.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        
        System.out.println("=== DEBUG: SIGNUP ENDPOINT HIT ===");
        System.out.println("Name: " + request.getName());
        System.out.println("Email: " + request.getEmail());
        System.out.println("Password: '" + request.getPassword() + "'");
        System.out.println("Password length: " + request.getPassword().length());
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            System.out.println("=== DEBUG: EMAIL ALREADY EXISTS ===");
            return ResponseEntity.badRequest().body("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        System.out.println("=== DEBUG: SAVING USER ===");
        System.out.println("Password being hashed: '" + request.getPassword() + "'");
        System.out.println("Generated hash: " + user.getPasswordHash());
        
        User savedUser = userRepository.save(user);
        System.out.println("=== DEBUG: USER SAVED WITH ID: " + savedUser.getId() + " ===");
        
        try {
            emailService.sendEmail(
                savedUser.getEmail(),
                "Welcome to Book Library Manager!",
                "Hello " + savedUser.getName() + ",\n\n" +
                "Welcome to Book Library Manager! Your account has been created successfully.\n" +
                "You can now login and start managing your book collection.\n\n" +
                "Happy Reading!\n" +
                "Book Library Manager Team"
            );
            System.out.println("=== DEBUG: EMAIL SENT ===");
        } catch (Exception e) {
            System.err.println("=== DEBUG: EMAIL FAILED: " + e.getMessage() + " ===");
        }
        
        return ResponseEntity.ok("User registered successfully");
    }
    
  
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("=== COMPLETE LOGIN DEBUG ===");
        System.out.println("Email received: " + request.getEmail());
        System.out.println("Password received: '" + request.getPassword() + "'");
        System.out.println("Password length: " + request.getPassword().length());
        
        // Print each character of password
        System.out.print("Password characters (decimal): ");
        for (char c : request.getPassword().toCharArray()) {
            System.out.print((int)c + " ");
        }
        System.out.println();
        
        try {
           
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        System.out.println("ERROR: User not found with email: " + request.getEmail());
                        return new RuntimeException("User not found");
                    });
            
            System.out.println("User found: " + user.getName());
            System.out.println("Stored password hash: " + user.getPasswordHash());
            
           
            BCryptPasswordEncoder testEncoder = new BCryptPasswordEncoder();
            
           
            boolean matches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
            System.out.println("BCrypt.matches result: " + matches);
            
          
            String newHash = passwordEncoder.encode(request.getPassword());
            System.out.println("New hash of received password: " + newHash);
         
            
            boolean testMatch = passwordEncoder.matches("test", user.getPasswordHash());
            System.out.println("Does 'test' (hardcoded) match? " + testMatch);
            
        
            String testHash = passwordEncoder.encode("test");
            System.out.println("New hash of 'test': " + testHash);
            
            if (!matches) {
                System.out.println("=== LOGIN FAILED: Password mismatch ===");
                return ResponseEntity.status(401).body("Invalid email or password");
            }
            
           
            String token = jwtUtil.generateToken(user.getEmail());
            System.out.println("=== LOGIN SUCCESS: Token generated ===");
            return ResponseEntity.ok(new AuthResponse(token));
            
        } catch (Exception e) {
            System.out.println("=== LOGIN EXCEPTION: " + e.getMessage() + " ===");
            e.printStackTrace();
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
}