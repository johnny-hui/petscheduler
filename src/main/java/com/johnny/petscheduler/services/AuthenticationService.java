package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.UserDAO;
import com.johnny.petscheduler.DTO.authentication.JWTLoginResponseDTO;
import com.johnny.petscheduler.DTO.authentication.JWTNewAccessDTO;
import com.johnny.petscheduler.DTO.authentication.JWTRefreshDTO;
import com.johnny.petscheduler.DTO.authentication.LoginUserDTO;
import com.johnny.petscheduler.DTO.registration.RegisterPetBusinessDTO;
import com.johnny.petscheduler.DTO.registration.RegisterPetOwnerDTO;
import com.johnny.petscheduler.DTO.registration.RegisterPetSitterDTO;
import com.johnny.petscheduler.DTO.user.UserResponseDTO;
import com.johnny.petscheduler.entities.users.PetBusiness;
import com.johnny.petscheduler.entities.users.PetOwner;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.EmailAlreadyExistsException;
import com.johnny.petscheduler.exceptions.EstablishmentYearInvalidException;
import com.johnny.petscheduler.security.jwt.TokenType;
import com.johnny.petscheduler.security.user.UserRole;
import com.johnny.petscheduler.security.utils.JWTUtil;
import com.johnny.petscheduler.utils.UserRegistrationUtil;
import com.johnny.petscheduler.utils.ValidationUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
public class AuthenticationService
{
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDAO userDAO;
    private final JWTUtil jwtUtil;

    @Autowired
   public AuthenticationService(AuthenticationManager authenticationManager,
                                PasswordEncoder passwordEncoder,
                                UserDAO userDAO,
                                JWTUtil jwtUtil)
    {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
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

        return new UserResponseDTO(request.getFirstName(), request.getLastName(), request.getEmail());
    }

    /**
     * Logins the user by performing email/password authentication and
     * returning a pair of JWT tokens.
     *
     * @param request
     *      A LoginUserDTO request object containing email/password field
     *
     * @return <code>JWTLoginResponseDTO</code>
     *      A DTO containing access and refresh JWT tokens
     */
    public JWTLoginResponseDTO login(final LoginUserDTO request)
    {
        // Authenticate the user and their credentials (call -> DaoAuthenticationProvider)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // If no exceptions, then generate JWT tokens and response from the set authentication object
        User user = (User) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(user.getEmail(), TokenType.ACCESS);
        String refreshToken = jwtUtil.generateToken(user.getEmail(), TokenType.REFRESH);

        return new JWTLoginResponseDTO(accessToken, refreshToken);
    }

    /**
     * Grants a new JWT access token.
     *
     * @param request
     *      A DTO request containing a JWT refresh token
     *
     * @return <code>JWTNewAccessDTO</code>
     *      A DTO response containing the JWT access token
     */
    public JWTNewAccessDTO grantNewAccessToken(final JWTRefreshDTO request)
    {
        String jwt = request.refresh().trim();
        if(jwtUtil.validateToken(jwt) && jwtUtil.isTokenRefresh(jwt)) {
            User user = userDAO.getUserByEmail(jwtUtil.getEmailFromToken(jwt));
            return new JWTNewAccessDTO(jwtUtil.generateToken(user.getEmail(), TokenType.ACCESS));
        }
        return null;
    }

    // Logout
}
