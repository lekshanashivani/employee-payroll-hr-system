package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;


@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));  
        user.setRole(request.getRole());

        repo.save(user);  

        return "User registered successfully";  
    }

    public String login(String username, String password) {
        User user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with username: " + username)
                );

        if (!encoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect password");
        }

        return jwtUtil.generateToken(username, user.getRole());
    }
}
