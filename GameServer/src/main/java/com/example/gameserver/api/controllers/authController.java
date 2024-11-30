package com.example.gameserver.api.controllers;

import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gameserver.api.dto.User.UserCreateDTO;
import com.example.gameserver.api.dto.User.UserLoginDTO;
import com.example.gameserver.entity.User;
import com.example.gameserver.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "Operations to manage authentication")
public class authController {
    private final AuthService authService;

    @Autowired
    public authController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserCreateDTO request) {
        try 
        {   
            Future<User> user = authService.registerUser(request);
            User newUser = user.get();

        
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Login a user")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO request) {
        try {
            Future<User> user = authService.loginUser(request);
            User newUser = user.get();
            return new ResponseEntity<>(newUser, HttpStatus.OK);

          
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    
}