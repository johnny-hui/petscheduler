package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.UserDAO;
import com.johnny.petscheduler.DTO.registration.RegisterPetBusinessDTO;
import com.johnny.petscheduler.DTO.registration.RegisterPetOwnerDTO;
import com.johnny.petscheduler.DTO.registration.RegisterPetSitterDTO;
import com.johnny.petscheduler.DTO.user.UserResponseDTO;
import com.johnny.petscheduler.entities.PetBusiness;
import com.johnny.petscheduler.entities.PetOwner;
import com.johnny.petscheduler.entities.PetSitter;
import com.johnny.petscheduler.exceptions.EmailAlreadyExistsException;
import com.johnny.petscheduler.exceptions.EstablishmentYearInvalidException;
import com.johnny.petscheduler.security.user.UserRole;
import com.johnny.petscheduler.utils.UserRegistrationUtil;
import com.johnny.petscheduler.utils.ValidationUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_EMAIL_ALREADY_EXISTS;
import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_INVALID_EST_YEAR;

/**
 * Provides services to handle HTTP requests regarding authentication such as
 * user registration, authentication, and logging out.
 */
@Service
public class AuthenticationService {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
   public AuthenticationService(PasswordEncoder passwordEncoder, UserDAO userDAO) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO registerPetOwner(final RegisterPetOwnerDTO request) {
        if(userDAO.doesUserExist(request.getEmail().trim().toLowerCase(Locale.ROOT))) {
            throw new EmailAlreadyExistsException(ERROR_MESSAGE_EMAIL_ALREADY_EXISTS);
        }
        PetOwner user = new PetOwner();
        UserRegistrationUtil.setBaseUserInfo(user, passwordEncoder, UserRole.PET_OWNER, request);
        UserRegistrationUtil.setPetOwnerInfo(user, request);
        userDAO.createUser(user);

        return new UserResponseDTO(request.getFirstName(), request.getLastName(), request.getEmail());
    }

    @Transactional
    public UserResponseDTO registerPetSitter(final RegisterPetSitterDTO request) {
        if(userDAO.doesUserExist(request.getEmail().trim().toLowerCase(Locale.ROOT))) {
            throw new EmailAlreadyExistsException(ERROR_MESSAGE_EMAIL_ALREADY_EXISTS);
        }

        PetSitter user = new PetSitter();
        UserRegistrationUtil.setBaseUserInfo(user, passwordEncoder, UserRole.PET_SITTER, request);
        UserRegistrationUtil.setPetSitterInfo(user, request);
        userDAO.createUser(user);

        return new UserResponseDTO(request.getFirstName(), request.getLastName(), request.getEmail());
    }

    @Transactional
    public UserResponseDTO registerPetBusiness(final RegisterPetBusinessDTO request) {
        if(!ValidationUtil.isYearEstablishedValid(request.getEstablishmentYear())) {
            throw new EstablishmentYearInvalidException(ERROR_MESSAGE_INVALID_EST_YEAR);
        }
        if(userDAO.doesUserExist(request.getEmail().trim().toLowerCase(Locale.ROOT))) {
            throw new EmailAlreadyExistsException(ERROR_MESSAGE_EMAIL_ALREADY_EXISTS);
        }

        PetBusiness user = new PetBusiness();
        UserRegistrationUtil.setBaseUserInfo(user, passwordEncoder, UserRole.PET_BUSINESS, request);
        UserRegistrationUtil.setPetBusinessInfo(user, request);
        userDAO.createUser(user);

        return new UserResponseDTO(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail()
        );
    }

    // Login

    // Refresh

    // Logout
}
