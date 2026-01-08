package com.hrpayroll.auth.config;

import com.hrpayroll.auth.entity.Role;
import com.hrpayroll.auth.entity.User;
import com.hrpayroll.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Admin User Initializer
 * 
 * Automatically creates an ADMIN user on application startup if it doesn't exist.
 * This eliminates the need for manual database insertion.
 */
@Component
public class AdminUserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL = "admin@company.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        if (!userRepository.existsByEmail(ADMIN_EMAIL)) {
            User adminUser = new User();
            adminUser.setEmail(ADMIN_EMAIL);
            adminUser.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            adminUser.setRole(Role.ADMIN);
            adminUser.setActive(true);

            userRepository.save(adminUser);
            System.out.println("========================================");
            System.out.println("ADMIN USER CREATED SUCCESSFULLY");
            System.out.println("Email: " + ADMIN_EMAIL);
            System.out.println("Password: " + ADMIN_PASSWORD);
            System.out.println("========================================");
        } else {
            System.out.println("Admin user already exists. Skipping creation.");
        }
    }
}

