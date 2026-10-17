package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.UserDAO;
import com.johnny.petscheduler.DTO.authentication.JWTLoginResponseDTO;
import com.johnny.petscheduler.DTO.authentication.JWTNewAccessDTO;
import com.johnny.petscheduler.DTO.authentication.LoginUserDTO;
import com.johnny.petscheduler.DTO.registration.RegisterPetBusinessDTO;
import com.johnny.petscheduler.DTO.registration.RegisterPetOwnerDTO;
import com.johnny.petscheduler.DTO.registration.RegisterPetSitterDTO;
import com.johnny.petscheduler.DTO.user.UserResponseDTO;
import com.johnny.petscheduler.entities.users.PetBusiness;
import com.johnny.petscheduler.entities.users.PetOwner;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.*;
import com.johnny.petscheduler.security.jwt.DAO.RefreshTokenDAO;
import com.johnny.petscheduler.security.jwt.RefreshToken;
import com.johnny.petscheduler.security.jwt.TokenPair;
import com.johnny.petscheduler.security.user.UserRole;
import com.johnny.petscheduler.security.utils.CookieUtil;
import com.johnny.petscheduler.security.utils.JWTUtil;
import com.johnny.petscheduler.utils.UserRegistrationUtil;
import com.johnny.petscheduler.utils.ValidationUtil;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_EMAIL_ALREADY_EXISTS;
import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_INVALID_EST_YEAR;
import static com.johnny.petscheduler.security.configs.SecurityConstants.JWT_REFRESH_NOT_FOUND_MSG;

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
    private final RefreshTokenDAO refreshTokenDAO;
    private final JWTUtil jwtUtil;

    @Autowired
   public AuthenticationService(AuthenticationManager authenticationManager,
                                PasswordEncoder passwordEncoder,
                                UserDAO userDAO,
                                RefreshTokenDAO refreshTokenDAO,
                                JWTUtil jwtUtil)
    {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userDAO = userDAO;
        this.refreshTokenDAO = refreshTokenDAO;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public UserResponseDTO registerPetOwner(final RegisterPetOwnerDTO request)
    {
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
    public UserResponseDTO registerPetSitter(final RegisterPetSitterDTO request)
    {
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
    public UserResponseDTO registerPetBusiness(final RegisterPetBusinessDTO request)
    {
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
    @Transactional
    public ResponseEntity<JWTLoginResponseDTO> login(final LoginUserDTO request)
    {
        // Authenticate the user and their credentials (call -> DaoAuthenticationProvider)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // If no exceptions, then generate JWT tokens from the loaded authentication object
        User user = (User) authentication.getPrincipal();
        TokenPair tokenPair = jwtUtil.generateTokenPair(user.getEmail());

        // Persist Refresh Token
        RefreshToken DBRefreshToken = new RefreshToken();
        DBRefreshToken.setUser(user);
        DBRefreshToken.setTokenHash(jwtUtil.tokenHasher(tokenPair.refresh()));
        refreshTokenDAO.create(DBRefreshToken);

        // Create and return an HttpOnly Cookie with JWT refresh token
        ResponseCookie cookie = CookieUtil.createRefreshTokenCookie(
                tokenPair.refresh(),
                Duration.ofMillis(jwtUtil.getJwtRefreshExpirationTimeMs())
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new JWTLoginResponseDTO(tokenPair.access()));
    }

    /**
     * Performs token rotation when a valid refresh token is presented.
     *
     * @param existingRefreshToken
     *      A String representing the JWT refresh token
     *
     * @return <code>JWTNewAccessDTO</code>
     *      A DTO response containing the JWT access token
     */
    @Transactional
    public ResponseEntity<JWTNewAccessDTO> refreshTokens(final String existingRefreshToken)
    {
        // Verify refresh token and extract claims
        Claims claims = jwtUtil.parseToken(existingRefreshToken);

        // Check if token is refresh && validate hash from refresh token in DB
        if (claims != null && jwtUtil.isTokenRefresh(claims)) {
            String email = jwtUtil.getEmailFromToken(claims);
            User user = userDAO.getUserByEmail(email);

            RefreshToken refreshTokenFromDB = refreshTokenDAO
                    .getRefreshTokenByUserID(user.getId())
                    .orElseThrow(() ->
                            new RefreshTokenNotFoundException(JWT_REFRESH_NOT_FOUND_MSG)
                    );

            String hash = jwtUtil.tokenHasher(existingRefreshToken);

            // If equal hash, create new token pair and update existing refresh token
            if (jwtUtil.areHashesEqual(refreshTokenFromDB.getTokenHash(), hash)) {
                TokenPair tokenPair = jwtUtil.generateTokenPair(user.getEmail());
                refreshTokenFromDB.setTokenHash(jwtUtil.tokenHasher(tokenPair.refresh())); // => update

                ResponseCookie cookie = CookieUtil.createRefreshTokenCookie(
                        tokenPair.refresh(),
                        Duration.ofMillis(jwtUtil.getJwtRefreshExpirationTimeMs())
                );
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(new JWTNewAccessDTO(tokenPair.access()));
            }
        }
        throw new MissingRefreshTokenException(JWT_REFRESH_NOT_FOUND_MSG);
    }

    // Logout
    @Transactional
    public ResponseEntity<Void> logout(final String existingRefreshToken)
    {
        // Validate the refreshToken
        Claims claims = jwtUtil.parseToken(existingRefreshToken);

        // Verify refresh token and extract claims
        if (claims != null && jwtUtil.isTokenRefresh(claims)) {
            // Get refreshToken from DB

            // Clear existing HttpOnly cookie containing the refreshToken
        }
        throw new MissingRefreshTokenException(JWT_REFRESH_NOT_FOUND_MSG);
    }
}
