package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.DTO.user.profile.UserProfileDTO;
import com.johnny.petscheduler.DTO.user.update.UpdatePetBusinessDTO;
import com.johnny.petscheduler.DTO.user.update.UpdatePetOwnerDTO;
import com.johnny.petscheduler.DTO.user.update.UpdatePetSitterDTO;
import com.johnny.petscheduler.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    /**
     * Returns user information of the current user.
     *
     * @param authentication
     *      The authenticated user.
     *
     * @return UserResponseDTO
     *      Specific information the returned user
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile(final Authentication authentication)
    {
        return ResponseEntity.ok(userService.getCurrentUserProfile(authentication));
    }

    /**
     * Returns information on a User's profile. A user can be either of type
     * owner, sitter or business.
     *
     * @param id
     *      A Long for the id of the user
     *
     * @return UserProfileDTO
     *      A user profile DTO of type of PetOwner, PetSitter, or PetBusiness
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable final long id)
    {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @PutMapping("/pet-owner")
    public ResponseEntity<Void> updatePetOwner(@RequestBody @Valid final UpdatePetOwnerDTO request,
                                               final Authentication authentication)
    {
        return this.userService.updatePetOwner(request, authentication);
    }

    @PutMapping("/pet-sitter")
    public ResponseEntity<Void> updatePetSitter(@RequestBody @Valid final UpdatePetSitterDTO request,
                                                final Authentication authentication)
    {
        return this.userService.updatePetSitter(request, authentication);
    }

    @PutMapping("/pet-business")
    public ResponseEntity<Void> updatePetBusiness(@RequestBody @Valid final UpdatePetBusinessDTO request,
                                                  final Authentication authentication)
    {
        return this.userService.updatePetBusiness(request, authentication);
    }

    // Delete a specific user
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(final Authentication authentication)
    {
        return this.userService.deleteUser(authentication);
    }
}
