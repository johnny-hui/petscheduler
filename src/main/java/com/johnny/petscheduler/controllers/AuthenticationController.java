package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.DTO.authentication.JWTLoginResponseDTO;
import com.johnny.petscheduler.DTO.authentication.LoginUserDTO;
import com.johnny.petscheduler.DTO.registration.RegisterPetBusinessDTO;
import com.johnny.petscheduler.DTO.registration.RegisterPetOwnerDTO;
import com.johnny.petscheduler.DTO.registration.RegisterPetSitterDTO;
import com.johnny.petscheduler.DTO.user.UserResponseDTO;
import com.johnny.petscheduler.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves API endpoints for user registration, user login authentication, and logout.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController
{
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    // 1) Register
    @PostMapping("/register/pet-owner")
    public ResponseEntity<UserResponseDTO> registerPetOwner(@Valid @RequestBody RegisterPetOwnerDTO request) {
        UserResponseDTO response = authenticationService.registerPetOwner(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/register/pet-sitter")
    public ResponseEntity<UserResponseDTO> registerPetSitter(@Valid @RequestBody RegisterPetSitterDTO request) {
        UserResponseDTO response = authenticationService.registerPetSitter(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/register/pet-business")
    public ResponseEntity<UserResponseDTO> registerPetBusiness(@Valid @RequestBody RegisterPetBusinessDTO request) {
        UserResponseDTO response = authenticationService.registerPetBusiness(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 2) Login (returns JWT access + refresh)
    @PostMapping("/login")
    public ResponseEntity<JWTLoginResponseDTO> login(@Valid @RequestBody LoginUserDTO request) {
        return ResponseEntity.ok(new JWTLoginResponseDTO("", ""));
    }

    // 3) Refresh

    // 4) Logout
}
