package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.DTO.user.UpdatePetBusinessDTO;
import com.johnny.petscheduler.DTO.user.UpdatePetOwnerDTO;
import com.johnny.petscheduler.DTO.user.UpdatePetSitterDTO;
import com.johnny.petscheduler.DTO.user.UserResponseDTO;
import com.johnny.petscheduler.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets a User by email address.
     *
     * @param email
     *      A String for the email (e.g. "/api/users?email=bob123@a.com")
     *
     * @return UserResponseDTO
     *      Specific information the returned user
     */
    @GetMapping
    public ResponseEntity<UserResponseDTO> getUserByEmail(@RequestParam @Email final String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // TODO Later => Restrict update to only one API endpoint method with Authentication object and JWT
    @PutMapping("/pet-owner/{id}")
    public ResponseEntity<Void> updatePetOwner(@PathVariable final Long id,
                                               @RequestBody @Valid final UpdatePetOwnerDTO request)
    {
        return this.userService.updatePetOwner(id, request);
    }

    @PutMapping("/pet-sitter/{id}")
    public ResponseEntity<Void> updatePetSitter(@PathVariable final Long id,
                                                @RequestBody @Valid final UpdatePetSitterDTO request)
    {
        return this.userService.updatePetSitter(id, request);
    }

    @PutMapping("/pet-business/{id}")
    public ResponseEntity<Void> updatePetBusiness(@PathVariable final Long id,
                                                  @RequestBody @Valid final UpdatePetBusinessDTO request)
    {
        return this.userService.updatePetBusiness(id, request);
    }

    // Delete a specific user
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable final Long id)
    {
        return this.userService.deleteUser(id);
    }
}
