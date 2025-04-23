package com.auth.config;

import com.auth.model.User;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create superadmin if it doesn't exist
        if (!userRepository.findByUsername("superadmin").isPresent()) {
            User superadmin = new User();
            superadmin.setUsername("superadmin");
            superadmin.setEmail("superadmin@tehrimap.com");
            superadmin.setPassword(passwordEncoder.encode("superadmin123")); // You should change this password
            superadmin.setRole("SUPERADMIN");
            superadmin.setMobile("9876543210"); // Default mobile number for superadmin
            superadmin.setCreatedAt(LocalDateTime.now());
            superadmin.setEnabled(true);
            
            userRepository.save(superadmin);
            System.out.println("Superadmin user created successfully!");
        }
    }
} 